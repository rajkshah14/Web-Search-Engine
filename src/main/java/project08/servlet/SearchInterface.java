package project08.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import project08.misc.db.DBConfig;
import project08.model.SearchResultItem;
import project08.query.QueryParser;
import project08.search.Search;

@WebServlet("/search")
public class SearchInterface extends HttpServlet {
	
	private static final long serialVersionUID = 5306911266935762345L;
	static HashMap<String, Long> logger = new HashMap<String, Long>();
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		response.setContentType("application/json");
		try {
			long timeStamp = System.currentTimeMillis();
			String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
			if (ipAddress == null)
				ipAddress = request.getHeader("Remote_Addr");
			if (ipAddress == null) {
			    ipAddress = request.getRemoteAddr();
			}
			
			// check ip last request time stamp
			if (logger.containsKey(ipAddress)) {
				long lastRequestTimestamp = logger.get(ipAddress);
				if (timeStamp - lastRequestTimestamp < 1000) {
					response.getWriter().print("{error:1}");
					return;
				} else {
					logger.remove(ipAddress);
				}
			}
			
			// remove all ip who requested 1 second ago
			for (Iterator<Entry<String, Long>> it = logger.entrySet().iterator(); it.hasNext();) {
				Entry<String, Long> entry = it.next();
				if (timeStamp - entry.getValue() > 1000) {
					it.remove();
				}
			}
			
			// if request size in last 1 sec in more then 10 then skip this request
			if (logger.size()>10){
				response.getWriter().print("{error:1}");
				return;
			}
			
			//make entry in logger
			logger.put(ipAddress, timeStamp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String query = request.getParameter("query");
		int k = Integer.parseInt(request.getParameter("k"));
		Search search = new Search();
		List<SearchResultItem> resultList = search.search(query, k, false);
		JSONObject responseJson = new JSONObject();
		
		//Result Set
		JSONArray resultItemJson = new JSONArray();
		for (SearchResultItem item : resultList) {
			JSONObject object = new JSONObject();
			object.put("rank", item.getRank());
			object.put("url", item.getUrl());
			object.put("score", item.getTf_idf());
			resultItemJson.add(object);			
		}
		responseJson.put("resultList", resultItemJson);
		
		//Query
		JSONObject queryJson = new JSONObject();
		queryJson.put("k", k);
		queryJson.put("query", query);
		responseJson.put("query", queryJson);
		
		//Stat
		responseJson.put("stat", calculateStat(query));
		
		//CW
		responseJson.put("cw", calculateCW(query));
		
		StringWriter out = new StringWriter();
		responseJson.writeJSONString(out);
		response.getWriter().print(out.toString());
	}
	
	private int calculateCW (String query) {
		Connection con = DBConfig.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT cast(document_count() as int) AS docCount");
			ResultSet rs = ps.executeQuery();

			while (rs.next())
				return rs.getInt("docCount");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {con.close();} catch (SQLException e) {}
		}
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray calculateStat(String query) {
		Connection con = DBConfig.getConnection();
		JSONArray jsonArray = new JSONArray();
		QueryParser qp = new QueryParser(query);
		try {
			List<String> terms = new ArrayList<>();
			terms.addAll(qp.getTerms());
			terms.addAll(qp.getQuoted());
			for (int i = 0; i < terms.size(); i++) {
				int wordId =terms.get(i).hashCode();
				PreparedStatement ps = con.prepareStatement("SELECT cast(document_term_count("+wordId+") as int) AS docCount");
				ResultSet rs = ps.executeQuery();
				int df = 0;
				if(rs.next())
					df = rs.getInt("docCount");
				
				JSONObject object = new JSONObject();
				object.put("df", df);
				object.put("term", terms.get(i));
				jsonArray.add(object);
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {con.close();} catch (SQLException e) {}
		}
		return jsonArray;
	}

}
