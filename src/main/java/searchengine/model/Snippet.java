package searchengine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import searchengine.features.indexer.Stemmer;

public class Snippet {

	private Stemmer stemmer;
	private int subSnippetSize = 32;  //size of subsnippet
	private List<String> termsInDocument, contentList;
	private String content, stemContent;
	
	public Snippet() {
		stemmer = new Stemmer();
	}
	
	public String getSnippet(String content, String query) {
		this.content = content;
		this.contentList = Arrays.asList(content.split(" "));
		this.stemContent = stemmer.stemString(content);
		
		getTermsInDocument(query);
		
		String result = "";

		if (termsInDocument.size()<1) { //no terms found in document
			String[] breakdown = content.split(" ");
			for (int i = 0; i < 32; i++) {
				result += breakdown[i] + " ";
			}
			return result;
		}
		
		// as minimum 8 words and snippet size is 32, maximum 4 subsnippet can be generated
		int count = Math.min(termsInDocument.size(), 4);
		// try to get maximum snippet 
		subSnippetSize = 32/count ;
		for (int i = 0; i < count; i++) {
			result += getSubSnippet(termsInDocument.get(i)) + " ";
		}
		return result;
	}

	private String getSubSnippet(String keyword) {
		String result = "";
		int pos = stemContent.indexOf(keyword);
		
		if (pos < 4) {
			for (int i = 0; i < subSnippetSize; i++) 
				result += contentList.get(i) + " ";
			return result;
		}
		else {
			for (int i = subSnippetSize/2; i < subSnippetSize + subSnippetSize/2; i++) 
				result += contentList.get(i) + " ";
			return result;
		} 
	}

	private void getTermsInDocument(String query) {
		termsInDocument = new ArrayList<String>();
		query = stemmer.stemString(query);
		String[] keywords = query.split(" ");
		
		for (int i = 0; i < keywords.length; i++)
			if (stemContent.contains(keywords[i]))
				termsInDocument.add(keywords[i]);
	}
}
