package searchengine.misc;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import searchengine.features.indexer.Stemmer;
import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;
import searchengine.model.SearchResultItem;

public class Search {

	private String query;
	
	public void setKeywords(String query) {
		this.query = query;
	}
	
	@SuppressWarnings("unchecked")
	public List<SearchResultItem> getSearchResult() throws IOException{
		List<SearchResultItem> resultSet = new ArrayList<SearchResultItem>();
		JSONObject resultArray = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			Connection connection = DBConfig.getConnection();
			String sql = "select documents.url, features.tf  from features inner join documents on features.doc_id = documents.doc_id where term ='" 
				+ new Stemmer().stemString(query).trim() + "' order by features.tf desc limit 20";
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int i =0;
			while (rs.next()) {
				SearchResultItem item = new SearchResultItem();
				item.setUrl(rs.getString(DML.Col_url));
				item.setTf_idf(rs.getDouble(DML.Col_tf));
				item.setRank(++i);
				resultSet.add(item);
				JSONObject obj=new JSONObject();
				obj.put("rank", i);
				obj.put("url", item.getUrl());
				obj.put("score", item.getTf_idf());
				array.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		resultArray.put("resultList", array);
		resultArray.put("query", new JSONObject().put("k", 5));
		resultArray.put("cw", 10000);
        StringWriter out = new StringWriter();
        resultArray.writeJSONString(out);
        System.out.println(out.toString());
		return resultSet;
	}
}
