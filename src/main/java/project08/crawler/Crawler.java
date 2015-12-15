package project08.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import project08.algorithms.pagerank.PageRank;
import project08.misc.db.DBConfig;
import project08.misc.db.DML;
import project08.misc.log.Log;

/**
 * Main Crawler class, from here the workers will be configured and started.
 * Created by Nico on 03.November.15.
 */
public class Crawler {
    
	Connection conn;
	
	public static void main (String[] args) throws SQLException {
		// first param 		URL
		// second param		depth
		// third param		count
		// fourth param		leavedomain
		// http://www.ndtv.com 3 100 true
		String url = args[0];
		int depth = Integer.parseInt(args[1]);
		int count = Integer.parseInt(args[2]);
		boolean leaveDomain = Boolean.parseBoolean(args[3]);
		
		DML.initializeDatabase();
		Crawler.crawl(depth,leaveDomain,count, new String[]{url});
		new PageRank();
		DML.createIndexes();
	}

    public static void crawl(int depth, boolean leaveDomain, int maxDocuments, String[] urls) throws SQLException {

        Connection conn = DBConfig.getConnection();

        if(conn != null) {

            Statement stmt = conn.createStatement();
            boolean resume = false;
            try {
                stmt.execute("CREATE SEQUENCE crawlerNumDocuments START 1");
            } catch (SQLException e) {
                resume = true;
            }
            conn.commit();

            insertIntoRequestQueue(conn, depth, leaveDomain, maxDocuments,urls);

            if(!resume) {
                for(int i = 0; i<urls.length;i++) {
                    try {
                        URL url = null;
                        url = new URL(urls[i]);

                        Website start = new Website(url, 0); //The startwebsite
                        start.saveToDB(conn);//put the startwebsite into the queue
                        conn.commit();
                    } catch (SQLException e) {
                    	Log.logException(e);
                    } catch (MalformedURLException e) {
                        Log.logException(e);
                    }
                }
            }

            List<CrawlerWorker> workerList = new ArrayList<CrawlerWorker>();
            for(int i = 0; i<Runtime.getRuntime().availableProcessors(); i++){
                CrawlerWorker worker = new CrawlerWorker(depth, leaveDomain, maxDocuments); //Start one worker
                workerList.add(worker);
                worker.start();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (CrawlerWorker crawlerWorker : workerList) {
                try {
                    crawlerWorker.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.logInfo("Calculating IDF");
            long timer = Log.startTimer();
            calculateIDF(conn);
            Log.logTimer("IDF calculated in",timer);

            Log.logInfo("Calculating TF*IDF/BM25");
            timer = Log.startTimer();
            calculateTFIDF(conn);
            Log.logTimer("TF*IDF/BM25 calculated in",timer);

            Log.logInfo("Calculating PageRank");
            timer = Log.startTimer();
            new PageRank();
            Log.logTimer("Pagerank calculated in",timer);

            Log.logInfo("Calculating BM25/PageRank Score");
            timer = Log.startTimer();
            calculateBM25PagerankFast(conn);
            Log.logTimer("BM25/PageRank Score calculated in",timer);

            closeCrawler(conn);
            Log.logInfo("Finished crawling");
        }else{
            Log.logError("Could not connect to Database");
        }
    }

    private static void insertIntoRequestQueue(Connection conn, int depth, boolean leaveDomain, int maxDocuments, String[] urls) throws SQLException {
		String sql = "insert into " + DML.Table_Crawl_Request + " (" 
			+ DML.Col_docId + ", " 
			+ DML.Col_depth + ", " 
			+ DML.Col_leave_domain + ", "
			+ DML.Col_max_doc + ", "
			+ DML.Col_url + ")"
			+ " values (?,?,?,?,?)";	
		for (int i = 0; i < urls.length; i++) {
	    	PreparedStatement ps = conn.prepareStatement(sql);
	    	ps.setInt(1, urls[i].hashCode());
			ps.setInt(2, depth);
			ps.setBoolean(3, leaveDomain);
			ps.setInt(4, maxDocuments);
			ps.setString(5, urls[i]);
			ps.execute();
	    	ps.close();
		}
    	conn.commit();
	}

	/**
     * Finishes the crawler and resets all working tables
     * @param con Connection used for DB communication
     * @throws SQLException
     */
    public static void closeCrawler(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.execute("DROP SEQUENCE crawlerNumDocuments ");
        stmt.execute("TRUNCATE crawler_queue RESTART IDENTITY ");
        con.commit();
        con.close();
    }

    private static void calculateIDF(Connection con) throws SQLException {
        con.setAutoCommit(true);
        Statement stmt = con.createStatement();
        stmt.execute("SELECT "+ DML.Function_Calc_IDF+"()");
        con.setAutoCommit(false);

    }

    private static void calculateTFIDF(Connection con) throws SQLException {
        con.setAutoCommit(true);
        Statement stmt = con.createStatement();
        stmt.execute("SELECT "+ DML.Function_Calc_TFIDF+"()");
        con.setAutoCommit(false);
    }

    private static void calculateBM25Pagerank(Connection con) throws SQLException {
        con.setAutoCommit(true);
        Statement stmt = con.createStatement();
        stmt.execute("SELECT "+ DML.Function_calculate_bm25_pagerank+"()");
        con.setAutoCommit(false);
    }

    private static void calculateBM25PagerankFast(Connection con) throws SQLException {
        con.setAutoCommit(true);
        Statement stmt = con.createStatement();
        stmt.execute("SELECT " + DML.Function_bm25_Weight + "(), " + DML.Function_Pagerank_Weight + "()");
        ResultSet rs = stmt.getResultSet();
        if(rs.next())
        {
            double bm25 = rs.getDouble(1);
            double pageRank = rs.getDouble(2);

            PreparedStatement ps = con.prepareStatement("UPDATE " + DML.Table_Features + " SET " + DML.Col_bm25_pagerank +" = "
                    + "? *" + DML.Col_bm25  + " + ? *" +  DML.Col_pageRank);
            ps.setDouble(1,bm25);
            ps.setDouble(2,pageRank);

            ps.execute();
            ps.close();
        }
        stmt.close();

        con.setAutoCommit(false);
    }
}
