package project08.search;

import java.util.List;

import project08.model.SearchResultItem;

public class CLISearch {
	public static void main(String[] args) {
		// first params boolean
		//       true = conjuctive
		//       false = disjunctive
		// second params k (result size)
		// Later all query

		boolean conjuctive = Boolean.parseBoolean(args[0]);
		int k = Integer.parseInt(args[1]);
		String query = "";
		for (int i = 2; i < args.length; i++)
			query += args[i] + " ";
		Search search = new Search();
		List<SearchResultItem> searchResult = search.search(query, k, conjuctive);
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
