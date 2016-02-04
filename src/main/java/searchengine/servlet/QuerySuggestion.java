package searchengine.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import searchengine.features.query.LevenshteinDistance;

@WebServlet("/querysuggestion")
public class QuerySuggestion extends HttpServlet{

	private static final long serialVersionUID = 8097275150560579135L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		response.setContentType("application/json");
		String query = request.getParameter("query");
		query = URLDecoder.decode(query, "UTF-8");

		String language = request.getParameter("language");
		LevenshteinDistance ld;
		try {
			ld = new LevenshteinDistance();
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().print("{'error':'SQLException}");
			return;
		}
		String[] suggestion = ld.getSuggestion(query,language);
		JSONArray array = new JSONArray();
		for (int i = 0; i < suggestion.length ;i++ ) {
			JSONObject value = new JSONObject();
			value.put("value",suggestion[i]);
			array.add(value);
		}
		StringWriter out = new StringWriter();
		array.writeJSONString(out);
		response.getWriter().print(out.toString());
	}
}
