package searchengine;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;

import searchengine.features.crawler.Crawler;
import searchengine.misc.db.DML;

public class Index {

	public static void main(String[] args) throws SQLException {
		main(new OutputStreamWriter(System.out), args);
	}

	public static void main(Writer writer, String[] args) throws SQLException {
		PrintWriter out = new PrintWriter(writer);
		out.println("Hello");
		DML.createTables();
		Crawler.crawl(3,true,30, new String[]{"http://www.uni-kl.de"});
		
	}
}
