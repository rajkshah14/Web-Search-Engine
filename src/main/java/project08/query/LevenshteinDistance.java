package project08.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import project08.indexer.Stemmer;
import project08.misc.Parameters;
import project08.misc.db.DBConfig;
import project08.misc.db.DML;

public class LevenshteinDistance {

	private List<String> terms;
	static private Connection conn;
	private Stemmer stemmer;

	public LevenshteinDistance() throws SQLException {
		if (conn==null)			
			conn = DBConfig.getConnection();
		else if (conn.isClosed())
			conn = DBConfig.getConnection();
		stemmer = new Stemmer();
	}
	
	public String[] getSuggestion(String query, String language) {
		terms = new QueryParser(query).getTerms();
		int limit = Parameters.CLOSE_TERM_LIMIT;
		HashMap<String, List<Suggestion>> optionsMap = getAllOptionsHashMap(language);
		List<String> suggestions = new ArrayList<String>();
		List<Suggestion> currentTermOptions = optionsMap.get(terms.get(terms.size()-1));
		for (int i = 0; i < Math.min(limit,currentTermOptions.size()); i++) {
			suggestions.add("");
		}

		for (int i = 0; i < terms.size()-1; i++) { //For all but the current term
			List<Suggestion> options = optionsMap.get(terms.get(0));
			for (int j = 0; j < suggestions.size(); j++) {

				if (options != null && options.size() > 0) { //If there are suggestions, suggest it
					suggestions.set(j,suggestions.get(j) + options.get(0) + " ");
				} else {//If not, don't suggest and keep current term
					suggestions.set(j,suggestions.get(j) + terms.get(i)+ " ");
				}
			}
		}

		//For the current term
		for (int j = 0; j < suggestions.size(); j++) {
			suggestions.set(j,suggestions.get(j) + currentTermOptions.get(j));
		}

		return suggestions.toArray(new String[suggestions.size()]);
	}
	
	private HashMap<String,List<Suggestion>> getAllOptionsHashMap(String language) {
		HashMap<String, List<Suggestion>> terms_options = new HashMap<String, List<Suggestion>>();
		for (String key : terms) {
			List<Suggestion> options = getCloseTerms(key, language);
			terms_options.put(key, options);
		}
		return terms_options;
	}
	
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	private List<Suggestion> getCloseTerms(String key, String language) {
		key = stemmer.stemString(key).trim();
		List<Suggestion> options = new ArrayList<Suggestion>();
		try {
			String sql = getQuery(key, language);
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Suggestion sugg = new Suggestion(key,rs.getString(DML.Col_term),rs.getDouble("distance"));
				options.add(sugg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Collections.sort(options);
		return options;
	}
	
	private String getQuery(String key, String language) {
		String sql = "SELECT DISTINCT " + DML.Col_term + ", levenshtein (" + DML.Col_term + ", '" + key + "') AS distance "
				+ " FROM " + DML.Table_Features
				+ " WHERE levenshtein (" + DML.Col_term + ", '" + key + "') <=" + Parameters.LEVENSHTEIN_DISTANCE 
				+ " ORDER BY distance "  
				+ " LIMIT " + Parameters.CLOSE_TERM_LIMIT;
		
		if (language.equalsIgnoreCase("english"))
			sql = "SELECT DISTINCT " + DML.Col_term + ", levenshtein (" + DML.Col_term + ", '" + key + "') AS distance "
					+ " FROM " + DML.Table_Features
					+ " WHERE levenshtein (" + DML.Col_term + ", '" + key + "') <=" + Parameters.LEVENSHTEIN_DISTANCE
					+ " AND " + DML.Col_language + " ='en' " 
					+ " ORDER BY distance "  
					+ " LIMIT " + Parameters.CLOSE_TERM_LIMIT;
		else if (language.equalsIgnoreCase("german"))
			sql = "SELECT DISTINCT " + DML.Col_term + ", levenshtein (" + DML.Col_term + ", '" + key + "') AS distance "
					+ " FROM " + DML.Table_Features
					+ " WHERE levenshtein (" + DML.Col_term + ", '" + key + "') <=" + Parameters.LEVENSHTEIN_DISTANCE
					+ " AND " + DML.Col_language + " ='de' " 
					+ " ORDER BY distance "  
					+ " LIMIT " + Parameters.CLOSE_TERM_LIMIT;
		
		return sql;
	}
	
	private int getDistance(String term1, String term2) {
		term1 = term1.toLowerCase();
		term2 = term2.toLowerCase();
        int [] costs = new int [term2.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= term1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= term2.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), term1.charAt(i - 1) == term2.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[term2.length()];
	}
	
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) throws SQLException{
		LevenshteinDistance dis = new LevenshteinDistance();
		dis.getCloseTerms("kaiser","ot");
	}
	
}
