package searchengine.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;

public class SearchengineConfig {
    public int id;
    public String url;
    public String queryKeyword;
    public String kKeyword;
    public String termDelimiter;
    public String additionalConfig;
    public boolean activated;

    public SearchengineConfig() {
        id = 0;
        additionalConfig = "";
    }

    public void save(){
        if(id > 0)
            update();
        else{
            Connection con = DBConfig.getConnection();
            try{
                PreparedStatement stmt = con.prepareStatement("INSERT INTO " + DML.Table_Searchengines
                        + " SELECT GREATEST(1,max("+DML.Col_id+")+1),? AS url,? AS queryKeyword," +
                        "? AS termDelimiter,? AS kKeyword,? AS additionalConfig," +
                        "? AS activated FROM "+DML.Table_Searchengines+" RETURNING id");

                stmt.setString(1,url);
                stmt.setString(2,queryKeyword);
                stmt.setString(3,termDelimiter);
                stmt.setString(4,kKeyword);
                stmt.setString(5,additionalConfig);
                stmt.setBoolean(6,activated);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    id = rs.getInt(1);
                }
                con.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public void update() {
        Connection con = DBConfig.getConnection();
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE " + DML.Table_Searchengines
                    + " SET " + DML.Col_url + " = ?, " + DML.Col_queryKeyword + " = ?," +
                    "" + DML.Col_querySeperator + "=?," + DML.Col_kKeyword + " =?," + DML.Col_addConf + "=?," +
                    "" + DML.Col_active + " =? WHERE " + DML.Col_id + "=?");

            stmt.setString(1, url);
            stmt.setString(2, queryKeyword);
            stmt.setString(3, termDelimiter);
            stmt.setString(4, kKeyword);
            stmt.setString(5, additionalConfig);
            stmt.setBoolean(6, activated);
            stmt.setInt(7, id);
            stmt.execute();
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException ignored) {}
        }
    }

    public void delete() {
        try {
            Connection con = DBConfig.getConnection();
            PreparedStatement stmt = con.prepareStatement("DELETE FROM " + DML.Table_Searchengines
                    + " WHERE " + DML.Col_id + "=?");

            stmt.setInt(1, id);
            stmt.execute();
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
