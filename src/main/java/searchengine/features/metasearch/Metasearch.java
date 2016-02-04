package searchengine.features.metasearch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;

public class Metasearch {
    private static final int k = 10;
    private static final double b = 0.4;
    List<String> terms;
    int actuallyChosen;

    Integer iChosen;
    Double dChosen;

    List<Searchengine> engines;

    public Metasearch(List<String> terms, int chosen) {
        this.terms = terms;
        iChosen = chosen;
    }

    public Metasearch(List<String> terms, double chosen) {
        this.terms = terms;
        dChosen = chosen;
    }

    public Metasearch(List<String> terms) {
        this(terms,0.7);
    }

    private void queryAllSearchengines(){
        ArrayList<Searchengine> engines = new ArrayList<>();

        Connection con = DBConfig.getConnection();
        try {
            PreparedStatement stmt = con . prepareStatement("SELECT * FROM " + DML.Table_Searchengines + " WHERE "
                    + DML.Col_active + " = TRUE");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String url = rs.getString(DML.Col_url);
                int id = rs.getInt(DML.Col_id);
                String query = url+"?";
                query += rs.getString(DML.Col_queryKeyword) + "=";
                query+=terms.get(0);
                for (int i = 1; i < terms.size(); i++) {
                    query+=rs.getString(DML.Col_querySeperator)+terms.get(i);
                }
                query+="&"+rs.getString(DML.Col_kKeyword) + "="+k;
                query+= rs.getString(DML.Col_addConf);
                int cw = rs.getInt(DML.Col_cw);
                Searchengine tmp;
                if(cw > 0)
                    tmp = new Searchengine(id,query,cw);
                else
                    tmp = new Searchengine(id,query);
                engines.add(tmp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException ignored) {}
        }


        this.engines = engines;
    }

    public boolean getSearchengines(){
        ArrayList<Searchengine> engines = new ArrayList<>();
        Connection con = DBConfig.getConnection();
        try {
            String searchengineFunction = getSearchengineFunction();
            ResultSet rs = con.createStatement().executeQuery(searchengineFunction);
            while (rs.next()) {
                String url = rs.getString(DML.Col_url);
                int id = rs.getInt(DML.Col_id);
                String query = url+"?";
                query += rs.getString(DML.Col_queryKeyword) + "=";
                query+=terms.get(0);
                for (int i = 1; i < terms.size(); i++) {
                    query+=rs.getString(DML.Col_querySeperator)+terms.get(i);
                }
                query+="&"+rs.getString(DML.Col_kKeyword) + "="+k;
                query+= rs.getString(DML.Col_addConf);
                int cw = rs.getInt(DML.Col_cw);
                Searchengine tmp;
                if(cw > 0)
                    tmp = new Searchengine(id,query,cw);
                else
                    tmp = new Searchengine(id,query);
                engines.add(tmp);
            }

            rs = con.createStatement().executeQuery("SELECT COUNT(*) FROM " + DML.Table_Searchengines);
            if(rs.next()) calcChosen(rs.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException ignored) {
            }
        }
        if(engines.size()>= this.actuallyChosen && engines.size() > 0){
            this.engines = engines;
            return true;
        }else return false;
    }

    private void calculateScores() {
        if(!getSearchengines()) {
            queryAllSearchengines();
            calcChosen(engines.size());
        }
        for (Searchengine engine : engines) {
            engine.setC(engines.size());
            engine.start();
        }
        HashMap<String,Integer> terms = new HashMap<>();
        double avg_cw = 0.0;
        for (Searchengine engine : engines) {
            try {
                engine.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (String s : engine.getTerms()) {
                if(terms.containsKey(s)){
                    int i = terms.get(s)+1;
                    terms.put(s,i);
                }else
                    terms.put(s,1);
            }
            avg_cw += engine.getCw();
        }
        avg_cw = avg_cw/engines.size();
        for (Searchengine engine : engines) {
            engine.setAvg_cw(avg_cw);
            for (Map.Entry<String, Integer> entry : terms.entrySet()) {
                engine.setTermCf(entry.getKey(),entry.getValue());
            }
            engine.calculateScore();
        }

        Collections.sort(engines);
        while(engines.size()>actuallyChosen){
            engines.remove(engines.size()-1);
        }

    }

    private void calcChosen(int count)
    {
        if(iChosen != null)
        {
            actuallyChosen = Math.min(iChosen,count);
        }else if(dChosen != null){
            actuallyChosen = Math.min((int)Math.round(dChosen*count),count);
        }
    }

    public List<MetaSearchResultItem> getResults(){
        calculateScores();
        ArrayList<MetaSearchResultItem> results  = new ArrayList<>();
        for (Searchengine engine : engines) {
            results.addAll(engine.getResults());
        }
        Collections.sort(results,MetaSearchResultItem.normalizedComparator());
        int i = 1;
        for (MetaSearchResultItem result : results) {
            result.setRank(i);
            i++;
        }

        return results;
    }

    private String getSearchengineFunction() {

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        String sep = "";
        for(String s: terms) {
            sb.append(sep).append(s.hashCode());
            sep = ",";
        }
        sb.append(")");
        String termHashes = sb.toString();

        String ret = "SELECT * FROM " +
                "(SELECT s."+DML.Col_id+", s."+DML.Col_url+", s."+DML.Col_queryKeyword+", s."+DML.Col_querySeperator+", " +
                        "s."+DML.Col_kKeyword+", s."+DML.Col_addConf+", s."+DML.Col_active+", s."+DML.Col_cw+", SUM(score) AS score, " +
                        "COUNT("+DML.Col_hash+") AS terms "
        + "FROM "+DML.Table_Searchengines+" s, "
        +        "("
        +                "SELECT s_s."+DML.Col_id+", s_t."+DML.Col_hash+", ("+b+" + (1-"+b+")* "
        +        "("+DML.Col_df+"/("+DML.Col_df+"+50+150*((1.0*"+DML.Col_cw+")/"+DML.Function_avg_cw+"())))* "
        +                "(log(("+DML.Function_engine_count+"()+0.5)/"+DML.Function_cf+"(s_t."+DML.Col_hash+"))/log("+DML.Function_engine_count+"()+1.0)) "
        +        ") AS score "
        + "FROM "+DML.Table_Searchengines+" s_s, "+DML.Table_Searchengine_terms+" s_t "
        + "WHERE s_s."+DML.Col_id+" = s_t."+DML.Col_id + " AND s_t." + DML.Col_hash + " IN " +termHashes
        + ") AS scores " +
          "WHERE scores.score > " +b
        + " GROUP BY s."+DML.Col_id
        + " ORDER BY score) AS fin "
        + " WHERE score > "+b+"*terms"        ;
        return ret;
    }

    public static void main(String[] args) {
        List<String> query = new ArrayList<>();
        query.add("tu");
        query.add("kaiserslautern");
        Metasearch ms = new Metasearch(query,5);
        List<MetaSearchResultItem> results = ms.getResults();

    }
}
