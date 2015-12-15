package project08.web;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;

import project08.crawler.Crawler;
import project08.misc.db.DML;

public class Index {

	public static void main(String[] args) throws SQLException {
		System.out.println("sift");
		main(new OutputStreamWriter(System.out), args);
	}

	public static void main(Writer writer, String[] args) throws SQLException {
		PrintWriter out = new PrintWriter(writer);
		out.println("Hello");
		DML.createTables();
		Crawler.crawl(3,true,30, new String[]{"http://www.uni-kl.de"});
		
	}
}
