package project08;

import java.sql.SQLException;

import project08.algorithms.pagerank.PageRank;
import project08.crawler.Crawler;
import project08.misc.db.DML;


public class Main {

	private static String[] urls = {"http://www.uni-kl.de",
		"http://www.ndtv.com",
		"http://www.thelocal.de/",
		"https://en.wikipedia.org/wiki/English_Wikipedia",
		"http://www.engadget.com/",
		"http://www.pcworld.com/"};
	
	public static void main (String[] args) throws ClassNotFoundException, SQLException {
		DML.dropAllFunction();
		DML.dropAllTable();
		DML.initializeDatabase();
		Crawler.crawl(3,false,2500, urls);
		System.out.println("Creating indexes");
		DML.createIndexes();
	}
}
