package searchengine.misc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import searchengine.model.CrawlRequestItem;
import searchengine.model.SearchengineConfig;

public class ReadTables {

	static private Connection connection;

	public static List<CrawlRequestItem> getCrawlRequestItem() {
		List<CrawlRequestItem> items = new ArrayList<CrawlRequestItem>();
		if (connection == null)
			connection = DBConfig.getConnection();
		String sql = "Select * from crawl_request ";

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				CrawlRequestItem item = new CrawlRequestItem();
				item.setDepth(rs.getInt(DML.Col_depth));
				item.setDocId(rs.getInt(DML.Col_docId));
				item.setUrl(rs.getString(DML.Col_url));
				item.setLeave_domain(rs.getBoolean(DML.Col_leave_domain));
				item.setTimeStamp(rs.getString(DML.Col_crawl_on_timestamp));
				item.setMax_doc(rs.getInt(DML.Col_max_doc));
				items.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {connection.close(); connection = null;} catch (SQLException e) {}
		}
		return items;
	}

	public static List<SearchengineConfig> getSearchengineConfig() {
		List<SearchengineConfig> items = new ArrayList<>();
		if (connection == null)
			connection = DBConfig.getConnection();
		String sql = "Select * from "+ DML.Table_Searchengines;

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				SearchengineConfig item = new SearchengineConfig();
				item.id = rs.getInt(DML.Col_id);
				item.url = rs.getString(DML.Col_url);
				item.queryKeyword = rs.getString(DML.Col_queryKeyword);
				item.termDelimiter = rs.getString(DML.Col_querySeperator);
				item.kKeyword = rs.getString(DML.Col_kKeyword);
				item.additionalConfig = rs.getString(DML.Col_addConf);
				item.activated = rs.getBoolean(DML.Col_active);

				items.add(item);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {connection.close(); connection = null;} catch (SQLException e) {}
		}
		return items;
	}

}
