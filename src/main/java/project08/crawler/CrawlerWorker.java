package project08.crawler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.postgresql.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import project08.indexer.Indexer;
import project08.misc.db.DBConfig;
import project08.misc.db.DML;
import project08.misc.log.Log;

/**
 * The Heart of the crawler
 */
public class CrawlerWorker extends Thread {

    private int maxDocuments;
    private int maxDepth;
    private boolean leaveDomain;
    private Connection con;
    static volatile int docCount = 0;

    public CrawlerWorker( int maxDepth, boolean leaveDomain, int maxDocuments) {
        this.maxDepth = maxDepth;
        this.maxDocuments = maxDocuments;
        this.leaveDomain = leaveDomain;
        con = DBConfig.getConnection();
    }

    /**
     * Crawls the given url.
     */
    
    @Override
    public void run() {
    	Website currSite = null;
    	do {
			currSite = new Website();
			boolean gotSite = false;
			try {//Get a new Site from the queue
			    gotSite =  currSite.getFromQueue(con);
			} catch (SQLException e) { //If there is a problem, rollback
			    rollback(e,true);
			    continue;
			} catch (MalformedURLException e) {//This should not happen, as URLS are already checked when writing to DB
			    continue;
			}
			if(gotSite == false) {//No Site is in the queue
			    currSite = null; //Terminate the Crawler worker if no more work is to be done
			    continue;
			}

            InputStreamReader stream = null;
            URL currUrl = null;
            try {//Open the URL and get the filestream
                currUrl = currSite.getUrl();
                if(currUrl != null) {
                    URLConnection urlCon = currUrl.openConnection();
                    urlCon.setConnectTimeout(5000);
                    urlCon.setReadTimeout(5000);
                    urlCon.connect();
                    stream = new InputStreamReader(urlCon.getInputStream(), StandardCharsets.UTF_8);
                }else continue;
            } catch (MalformedURLException e) {
                continue;
            } catch (IOException e) {
                continue;
            }catch(IllegalArgumentException e){
                continue;
            }

            Log.logInfo("Crawling site " + currSite.getNum() + "("+docCount+ ") "+ currUrl.toString());


            ByteArrayInputStream inputStream= null;
            try {
				currUrl = currSite.getUrl();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();	
				int ptr = 0;
				while ((ptr = stream.read()) != -1) {
					baos.write((char) ptr);
				}
				stream.close();
				inputStream = new ByteArrayInputStream(baos.toByteArray());
			} catch (Exception e) {
                Log.logException(e);
				continue;
			} finally {
				if (inputStream == null )
					continue;
			}



            Tidy tidy = getTidy();

            //API needs output stream, but we only need document. So discard outputstream
            OutputStream nullStream = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            };
            //Parse HTML to XHTML
            inputStream.reset();
            Document xhtmlDoc = null;
            try{  xhtmlDoc = tidy.parseDOM(inputStream, nullStream);}
            catch(Exception e){continue;}
            if(xhtmlDoc == null){continue;}
            //Get all outgoing links with XPath
            XPath xpath = XPathFactory.newInstance().newXPath();
            if (!extractImages(currSite, currUrl, xhtmlDoc, xpath)) continue;

            //Insert document into documents table
            try {
                inputStream.reset();

                PreparedStatement stmt = con.prepareStatement("INSERT INTO "
                        + DML.Table_Documents + "(" + DML.Col_docId + ","
                        + DML.Col_url + ") VALUES (?,?) ON CONFLICT(" + DML.Col_docId + ") DO "
                        + "UPDATE SET " + DML.Col_url + " = EXCLUDED." + DML.Col_url + " ,"
                        + DML.Col_crawl_on_timestamp + " = EXCLUDED." + DML.Col_crawl_on_timestamp+" ");

                stmt.setInt(1,currSite.getId());
                stmt.setString(2,currSite.getUrlString());
                stmt.execute();
                con.commit();

                Indexer indexer = new Indexer(inputStream, currSite.getId());

                Log.logInfo("Indexer successful ? : " + indexer.processStream() + " url " + currUrl.toString());
            } catch (SQLException e) {
                rollback(e, true);
                continue;
            }

            if (!extractLinks(currSite, currUrl, xhtmlDoc, xpath)) continue;

            try {
                sleep(200);
            } catch (InterruptedException e) {
                Log.logException(e);
                continue;
            }
            docCount++;
        }  while(currSite != null && currSite.getDepth() <= maxDepth && docCount <= maxDocuments);

         try {
             con.close();
         } catch (SQLException e) {
             Log.logException(e);
         }

        Log.logInfo("Worker finished");
    }

    private boolean extractImages(Website currSite,URL currUrl, Document xhtmlDoc, XPath xpath){
        String expression = "//img";
        try {
            NodeList nodes = (NodeList) xpath.evaluate(expression, xhtmlDoc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                try {
                    Node n = nodes.item(i);
                    String src = n.getAttributes().getNamedItem("src").getNodeValue();
                    String alt = "";
                    if (n.getAttributes().getNamedItem("alt") != null) {
                        alt = n.getAttributes().getNamedItem("alt").getNodeValue();
                    }

                    URL imgUrl = new URL(currUrl, src);
                    URLConnection urlConnection = imgUrl.openConnection();


                   PreparedStatement stmt =  con.prepareStatement("INSERT INTO " + DML.Table_Images + " VALUES(?,?,?,?,?,?,?)");
                    stmt.setInt(1,currSite.getId()); //DocID
                    stmt.setInt(2,i); //Index of Image on Page
                    stmt.setInt(3,-1); //Placeholder position in text
                    stmt.setString(4,imgUrl.toString()); //URL
                    stmt.setString(5,alt); //AltText
                    InputStream is = urlConnection.getInputStream();

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    byte[] bytes = buffer.toByteArray();
                    String imageType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
                    if(imageType != null && imageType.equalsIgnoreCase("application/xml"))
                        imageType = "image/svg+xml";
                    stmt.setString(6,imageType);
                    stmt.setString(7,Base64.encodeBytes(bytes));

                    stmt.execute();
                    con.commit();
                } catch (MalformedURLException e) {
                    continue;
                } catch (IOException e) {
                    continue;
                } catch (SQLException e) {
                    rollback(e);
                    continue;
                }catch (NullPointerException e){
                    continue;
                }
            }
        } catch (XPathExpressionException e) {
            return false;
        }
        return true;
    }

    private boolean extractLinks(Website currSite, URL currUrl, Document xhtmlDoc, XPath xpath) {

        String expression = "//a[@href]";

        try {
            NodeList nodes = (NodeList) xpath.evaluate(expression, xhtmlDoc, XPathConstants.NODESET);
            for(int i = 0; i<nodes.getLength();i++)
            {
                //For every link on the webpage
                Node n = nodes.item(i);
                String link = n.getAttributes().getNamedItem("href").getNodeValue(); //Get the URL
                URL url = null;
                try {//And create a cleaned URL for it
                    url = cleanURL(currUrl,link);

                if(filterURL(url)) { //Check if the URL should be ignored
                    //Create a new Website object with one more depth than the current site
                    Website childSite = new Website(url, currSite.getDepth() + 1);
                    if(childSite.getDepth() < maxDepth) { //Check if max depth is already reached
                        if(leaveDomain || childSite.sameDomain(currSite)) { //Check Domain
                            childSite.savetoQueue(con); //Save website to queue
                            try {//insert link
                                PreparedStatement stmt = con.prepareStatement("INSERT INTO "
                                        + DML.Table_Links +"(" + DML.Col_from_doc_id +"," + DML.Col_to_doc_id + ") "
                                        + "VALUES (?,?) ON CONFLICT DO NOTHING ");
                                stmt.setInt(1,currSite.getId());
                                stmt.setInt(2, childSite.getId());
                                stmt.execute();
                                con.commit();

                            } catch (SQLException e) {
                                rollback(e);
                            }
                        }
                    }
                }
                } catch (MalformedURLException e) {//Ignore wrong URLS
                }

            }
        } catch (XPathExpressionException e) {
            Log.logException(e);
            return false;
        }catch(ArrayIndexOutOfBoundsException e){
            return false;
        } //Happens rarely because of a Bug in XPath/Jtidy
        catch(NullPointerException e){
            return false;
        } //Happens rarely because of a Bug in XPath/Jtidy
        catch (Exception e){Log.logException(e);
            return false;
        }
        return true;
    }

    private Tidy getTidy() {
    	Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setShowWarnings(false);
        tidy.setShowErrors(0);
        tidy.setXHTML(true);
        tidy.setQuiet(true);
        return tidy;
	}

	/**
     * Receives a URL string and creates a cleaned URL object. The URL will be normalized according to RFC 2396, section 5.2.
     * Furthermore Fragments will be removed.
     * @param context The current url, for handling of relative paths
     * @param link The URL text to clean
     * @return URL A URL object created from the cleaned link
     * @throws MalformedURLException If a invalid URL text was given
     */
    private URL cleanURL(URL context, String link) throws MalformedURLException {
        link = link.toLowerCase();
        String fragment = null;
        URI uri = null;
        URL url = new URL(context, link); //Create the basic URL object

        try {
            uri = url.toURI(); //Translate it to a URI
            uri = uri.normalize(); //Normalize URI
            fragment = uri.getRawFragment(); //get fragment
        } catch (URISyntaxException e) {}

        if(uri != null)
            link = uri.toString();//Translate URI back to string for cleaning
        //Remove Fragment
        if( fragment != null && fragment.length()>0) //if URL has a fragment, remove it
            link = link.substring(0,link.indexOf("#"+fragment));

        //Remove last slash
        link = link.substring(0, link.length() - (link.endsWith("/") ? 1 : 0));
        //Create URL
        URL ret = new URL(link);
        return ret;
    }

    /**
     * Filters out files and links with unsupported protocols
     * @param url The URL to check
     * @return boolean True if the URL may be crawled, false if not
     */
    private boolean filterURL(URL url)
    {
        boolean ret = true;
        String protocol = url.getProtocol();
        //Ignore FTP, SSH, MAILTO, ... links
        if(!(protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https") ) )
            ret = false;


        // Ignore Files
        //Get "home/index.html" from "www.rhrk.uni-kl.de/home/index.html"
        String file = url.getPath();
        //Split "home/folder1/index.html" into "home/folder1" and "index.html"
        String[] tokens = file.split("/(?=[^/]+$)");
        if(tokens.length >= 1)
        {
            String fileName = tokens[tokens.length-1];
            //Split off the part behind the last "." E.g.: "index.test.html" => "index.test" and "html"
            String[] fileParts = fileName.split("\\.(?=[^\\.]+$)");
            if(fileParts.length >= 2) {
                String fileExt = fileParts[tokens.length-1];

                if(!(fileExt.equalsIgnoreCase("html") || fileExt.equalsIgnoreCase("htm")|| fileExt.equalsIgnoreCase("shtml")
                        || fileExt.equalsIgnoreCase("php")))
                    ret = false;
            }

        }
        return ret;
    }

    /**
     * Rolls back the transaction if an error occurred
     * @param e The SQLException for printing
     */
    private void rollback(SQLException e){
        rollback(e,false);
    }
    private void rollback(SQLException e, boolean reset)
    {
        try{
            con.rollback();
            if(reset) {
                resetSequence();
            }
        } catch (SQLException e1) {
            Log.logException(e);
        }
    }

    private void resetSequence(){
        try{
			String sql = "ALTER SEQUENCE crawlerNumDocuments INCREMENT BY -1";
			con.createStatement().execute(sql);
			con.commit();
        } catch (SQLException e1) {
            Log.logException(e1);
        }
    }
}
