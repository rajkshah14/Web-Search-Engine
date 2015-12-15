package project08.misc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple class to create preconfigured connections
 * Created by nico on 04.November.15.
 */
public class DBConfig {
    private static final String connectionString = "jdbc:postgresql://localhost:5432/";
    private static final String user = "postgres";
    private static final String password = "admin";
    private static final String defaultDB = "Searchengine";

    public static Connection getConnection(){
        return getCon(connectionString+defaultDB, false);
    }

    public static Connection getConnection(String db){
    	return getCon(connectionString+db, false);
    }
    
    public static Connection getConnection(boolean autocommit){
        return getCon(connectionString+defaultDB, autocommit);
    }

    public static Connection getConnection(String db,boolean autocommit){
    	return getCon(connectionString+db, autocommit);
    }
    
    private static Connection getCon(String connectionString, boolean autocommit) {
    	Connection con = null;
        try {
        	Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionString,user,password);
            con.setAutoCommit(autocommit);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } 
        return con;
    }
}
