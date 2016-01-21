package project08.crawler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Old unused Crawler queue, may be needed later again
 */
public class CrawlerQueue {

    private LinkedList<Website> urls;
    private Connection conn;
    private int docCount;

    public CrawlerQueue(Connection conn, int count) {
        this.conn=conn;
        urls = new LinkedList<Website>();
        this.docCount = count;
    }

    public CrawlerQueue(Connection conn) {
        this(conn,0);
    }

    public synchronized void addUrl(Website url)
    {
        docCount++;
        url.setNum(docCount);
        try{
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO crawler_queue(url,num, depth) VALUES (?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setString(1,url.getUrlString());
            stmt.setInt(2, url.getNum());
            stmt.setInt(3, url.getDepth());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if( rs.next())
                url.setId(rs.getInt(1));

            conn.commit();

            urls.add(url);
        } catch (SQLException e) {
            docCount--;
            e.printStackTrace();
        }
        notifyAll();
    }

    public synchronized Website getUrl()
    {
        try {
            while (urls.size() == 0) {
                wait();
            }
            Website tmp = urls.getFirst();

            try{
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM crawler_queue WHERE id = ?");

                stmt.setInt(1, tmp.getId());

                stmt.executeUpdate();

                conn.commit();

                urls.removeFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return tmp;
        }catch (InterruptedException e)
        {
            return null;
        }
    }

}
