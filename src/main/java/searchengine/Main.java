package searchengine;

import java.sql.SQLException;

import searchengine.misc.db.DML;

public class Main {

	private static String[] urls = {"http://www.uni-kl.de",
		"http://www.ndtv.com",
		"http://www.thelocal.de/",
		"https://en.wikipedia.org/wiki/English_Wikipedia",
		"http://www.engadget.com/",
		"http://www.pcworld.com/"};
	
	public static void main (String[] args) throws ClassNotFoundException, SQLException {
//		DML.dropAllFunction();
//		DML.dropAllTable();
		DML.initializeDatabase();
//		Crawler.crawl(3,true,100, urls);
//		DML.createIndexes();
		
		
	}
}
