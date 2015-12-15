package project08.search;

import java.util.List;

import project08.model.SearchResultItem;

public class CLIDBSearch {

	public static void main(String[] args) {
		//First params db name
		//Second params boolean
		//       true = conjuctive
		//       false = disjunctive
		//Thrid params k (result size)
		// Later all query
		
		String dbName = args[0];
		boolean conjuctive = Boolean.parseBoolean(args[1]);
		int k = Integer.parseInt(args[2]);
		String query = "";
		for (int i = 3; i < args.length; i++)
			query += args[i] + " ";
		Search search = new Search();
		List<SearchResultItem> searchResult = search.search(dbName, query, k, conjuctive);
		System.out.println("----------------------------------------------------------------");
		System.out.println("------------------------- RESULT --------------------------------");
		for (SearchResultItem item : searchResult) {
			System.out.println("Rank \t\t" + item.getRank());
			System.out.println("URL \t\t" + item.getUrl());
			System.out.println("Score \t\t" + item.getTf_idf());
			System.out.println("");
		}
		System.out.println("----------------------------------------------------------------");
	}
}
