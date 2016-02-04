package searchengine.features.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import searchengine.misc.Log;
import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;
import searchengine.model.ImageSearchResultItem;
import searchengine.model.SearchResultItem;

public class ImageSearch extends Search {

	public ImageSearch() {
        sm = ScoringModel.BM25_Pagerank;
    }

    public ImageSearch(ScoringModel scoringModel) {
        sm = scoringModel;
    }
    
    public List<SearchResultItem> search(String query, int resultSize, boolean disjunctive) {
        return search(query,resultSize,disjunctive, "", true);
    }
    
    public List<SearchResultItem> search (String db, String query, int resultSize, boolean disjunctive ) {
        return search(db,query,resultSize,disjunctive, "", true);
    }
    
    public List<ImageSearchResultItem> search_(String query, int resultSize, boolean disjunctive, String language) {
    	Connection con = DBConfig.getConnection();
    	List<SearchResultItem> webPages = search (con,query,resultSize * 2,disjunctive, language, true);
    	List<ImageSearchResultItem> resultList = new ArrayList<ImageSearchResultItem>();
    	
    	for (SearchResultItem page : webPages) {
    		try {
    			if (con.isClosed())
    				con = DBConfig.getConnection();
    			PreparedStatement ps = con.prepareStatement("Select * from " + DML.Table_Images + " WHERE doc_id = " + page.getUrl().hashCode());
    			ResultSet rs = ps.executeQuery();
    			while (rs.next()) {
    				ImageSearchResultItem item = new ImageSearchResultItem();
    				item.copy(page);
    				item.setImage(rs.getString(DML.Col_image));//nico
    				item.setPosition(rs.getInt(DML.Col_position));
    				item.setPageIndex(rs.getInt(DML.Col_pageindex));
    				item.setExtension(rs.getString(DML.Col_type));
    				item.setAlt_text(rs.getString(DML.Col_altText));
    				item.calculateScore(query);
    				resultList.add(item);
    			}
    		} catch (Exception e) {
    			Log.logException(e);
    		} finally {
    			try { con.close();} catch (SQLException e) {e.printStackTrace();}
    		}
    		
    		
    		Collections.sort(resultList,new Comparator<ImageSearchResultItem>() {
				@Override
				public int compare(ImageSearchResultItem o1, ImageSearchResultItem o2) {
					if (o1.getImageBasedScore() > o2.getImageBasedScore())
						return -1;
					else if (o1.getImageBasedScore() < o2.getImageBasedScore())
						return 1;
					else 
						return 0;
				}
			});
    		
    		while (resultList.size() > resultSize) {
    			resultList.remove(resultList.size()-1);
    		}
    	}
    	return resultList;
    }
    
    public static void main(String[] args) {
    	ImageSearch im = new ImageSearch();
    	List<ImageSearchResultItem> list = im.search_("de", 5, true,"en");
    	for (ImageSearchResultItem item : list) {
    		System.out.println(item.getExtension() + " : " + item.getImageBasedScore() + item.getAlt_text() + " : " + item.getUrl());
    	}
    }
}
