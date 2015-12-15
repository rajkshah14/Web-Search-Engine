package project08.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import project08.misc.db.DBConfig;
import project08.misc.db.DML;
import project08.model.SearchResultItem;
import project08.query.QueryParser;

/**
 * Created by Nico on 17.November.15.
 */
public class Search {
    public enum ScoringModel {
        TF_IDF(DML.View_Features_tfidf),
        BM25(DML.View_Features_bm25),
        BM25_Pagerank(DML.View_Features_bm25_pagerank);

        private final String table;

        ScoringModel(String table){
            this.table = table;
        }

    }

    private ScoringModel sm;

    public Search()
    {
        sm = ScoringModel.BM25_Pagerank;
    }

    public Search(ScoringModel scoringModel)
    {
        sm = scoringModel;
    }

    public List<SearchResultItem> search(String query, int resultSize, boolean disjunctive) {
        return search(query,resultSize,disjunctive, "");
    }
	
	public List<SearchResultItem> search(String query, int resultSize, boolean disjunctive, String language) {
    	Connection con = DBConfig.getConnection();
    	return search(con,query,resultSize,disjunctive, language);
    }
	
	public List<SearchResultItem> search (String db, String query, int resultSize, boolean disjunctive, String language ) {
		Connection con = DBConfig.getConnection(db);
    	return search(con,query,resultSize,disjunctive, language);
	}

    public List<SearchResultItem> search (String db, String query, int resultSize, boolean disjunctive ) {
        return search(db,query,resultSize,disjunctive, "");
    }

    private List<SearchResultItem> search(Connection con, String query, int resultSize, boolean disjunctive, String language ) {
        QueryParser qp = new QueryParser(query);
        List<String> queryTerms = qp.getTerms();
        String where = qp.getWherePedicate();

        if(language != null && language.length()>0 && !language.equals("ot"))
            where += " AND documents.language = '"+language+"'";

        int termCount = queryTerms.size();
        List<SearchResultItem> resultList = new ArrayList<SearchResultItem>();
        if(termCount>0) {
            try {
                ResultSet rs;
                if(disjunctive)
                    rs = searchDisjuntive(queryTerms,resultSize, con, where);
                else
                    rs = searchConjunctive(queryTerms, resultSize, con, where);

                if(rs != null) {
                    int i = 0;
                    while (rs.next() && i < resultSize) {
                        SearchResultItem item = new SearchResultItem();
                        item.setUrl(rs.getString(DML.Col_url));
                        item.setTf_idf(rs.getDouble("score"));
                        item.setRank(++i);
                        item.setTitle(rs.getString("title")); //todo if title is null then Extract domain name & set it as title, This can be done while adding title in db
                        item.setContent_snap(rs.getString("content_snap"));
                        resultList.add(item);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }finally            {
                try {con.close();} catch (SQLException e) {}
            }
        }
        return resultList;
    }

    private ResultSet searchDisjuntive(List<String> terms, int resultSize, Connection con, String where){
        int termCount = terms.size();
        String termList = "?";
        for (int i = 1; i < termCount; i++) {
            termList += ",?";
        }
            try {
                PreparedStatement ps = con.prepareStatement(
                    "select documents.doc_id, sum(score) as score, MIN(url) as url, documents.title as title, documents.content_snap as content_snap " +
                        "from "+sm.table+" AS features, documents " +
                        "where features.doc_id = documents.doc_id AND word_id in ("+termList+") " + where +
                        "group by documents.doc_id " +
                        "having count(word_id) = ?" +
                        "order by score desc " +
                        "LIMIT ?");

                for (int i = 0; i < termCount; i++) {
                    ps.setInt(i + 1, terms.get(i).hashCode());
                }
                ps.setInt(termCount+1,termCount);
                ps.setInt(termCount+2,resultSize);
                ResultSet rs = ps.executeQuery();
                return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return null;
    }

    private ResultSet searchConjunctive(List<String> terms, int resultSize, Connection con, String where){
        int termCount = terms.size();
        String termList = "?";
        for (int i = 1; i < termCount; i++) {
            termList += ",?";
        }
        try {
            PreparedStatement ps = con.prepareStatement(
                "select documents.doc_id, sum(score) as score, MIN(url) as url, documents.title as title, documents.content_snap as content_snap  " +
                    "from "+sm.table+" AS features, documents " +
                    "where features.doc_id = documents.doc_id AND word_id in ("+termList+") " + where +
                    "group by documents.doc_id " +
                    "order by score desc " +
                    "LIMIT ?");

            for (int i = 0; i < termCount; i++) {
                ps.setInt(i + 1, terms.get(i).hashCode());
            }
            ps.setInt(termCount+1,resultSize);
            ResultSet rs = ps.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main (String[] args) throws ClassNotFoundException, SQLException {

        Search search = new Search();
        String query = "de tu startpag die";
        System.out.println("----------- Searching for \"" + query +"\"---------------------");
        System.out.println("---------Englisch----------------------");
        for (SearchResultItem searchResultItem : search.search(query, 5, true, "en")) {
            System.out.println(searchResultItem.toString());
        }
        System.out.println("---------German----------------------");
        for (SearchResultItem searchResultItem : search.search(query, 5, true,"de")) {
            System.out.println(searchResultItem.toString());
        }

        System.out.println("---------No Language----------------------");
        for (SearchResultItem searchResultItem : search.search(query, 5, true,null)) {
            System.out.println(searchResultItem.toString());
        }

    }
}
