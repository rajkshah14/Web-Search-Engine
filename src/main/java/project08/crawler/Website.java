package project08.crawler;


import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Helper class which represents a URL and further information needed for the crawler.
 * It implements the queue and enqueue functions
 */
public class Website {
    private int id = 0;
   private int num = 0;
   private String domain = "";
   private URL url = null;
   private int depth = -1;

    /**
     * Creates a new Website with given URL and Depth
     * @param url The URL of the website
     * @param depth The depth where this website was crawled
     */
    public Website(URL url, int depth){
        this.url = url;
        this.depth = depth;
        this.domain = this.url.getHost();
        this.domain = domain.startsWith("www.") ? domain.substring(4) : domain;
        this.id = getUrlString().hashCode();
    }

    /**
     * Creates a dummy website for loading from the queue
     */
    public Website()
    {

    }

    /**
     * Loads a Website from the DB queue
     * @param con The connection to use for DB communication
     * @return True if a new Website could be loaded, false if not
     * @throws SQLException If there is a error with the connection
     * @throws MalformedURLException If the URL could not be created
     */
    public boolean getFromQueue(Connection con) throws SQLException, MalformedURLException {
        synchronized (Website.class){
            PreparedStatement stmt = con.prepareStatement(
                    // "SELECT id, url, depth, nextval('crawlerNumDocuments') as num " +
                    //"FROM public.crawler_queue;");
                    "UPDATE  public.crawler_queue SET visited = true WHERE doc_id = (SELECT min(q.doc_id) FROM crawler_queue q WHERE q.visited = FALSE " +
                            "GROUP BY q.depth ORDER BY q.depth ASC LIMIT 1 ) " +
                            " RETURNING doc_id, url, depth, nextval('crawlerNumDocuments') as num");
            stmt.execute();
            con.commit();

            ResultSet rs = stmt.getResultSet();

            if(rs.next()) {
                this.id = rs.getInt(1);
                this.url = new URL(rs.getString(2));
                this.depth = rs.getInt(3);
                this.num = rs.getInt(4);
                this.domain = this.url.getHost();
                this.domain = domain.startsWith("www.") ? domain.substring(4) : domain;
                return true;
            }else return false;
        }
    }

    /**
     * Saves the Website to the Db queue
     * @param con The connection used for writing
     * @throws SQLException Thrown if there was a problem with the connection or data (E.G.: duplicate entries)
     */
    public void savetoQueue(Connection con)  {

        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO public.crawler_queue(doc_id,url, depth) VALUES (?,?, ?) " +
                    "RETURNING doc_id;");

            stmt.setInt(1, id); //save hashcode as ID
            stmt.setString(2, getUrlString());
            stmt.setInt(3, getDepth());

            stmt.execute();

            ResultSet rs = stmt.getResultSet();
            if (rs.next())
                id = rs.getInt(1);

            con.commit();
        }catch(SQLException e){try{con.rollback();}catch(SQLException e2){}}

    }

    /**
     * Checks if this website has the same domain then other
     * @param other The other website to check
     * @return True if both sites are in the same domain, false if not
     */
    public boolean sameDomain(Website other)
    {
        return this.domain.equalsIgnoreCase(other.domain);
    }

    /**
     * Gets the domain name
     * @return Domain name
     */
    public String getDomain() {
        return domain;
    }

    public String getUrlString() {
        return url.toString();
    }
    public URL getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
