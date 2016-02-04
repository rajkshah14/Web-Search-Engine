package searchengine.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import searchengine.model.SearchengineConfig;

@WebServlet("/searchengine")
public class SearchengineConfigServlet extends HttpServlet {

	private static final long serialVersionUID = -7782040592094091646L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().invalidate();
		SearchengineConfig conf = new SearchengineConfig();
		String id = req.getParameter("id");
		if(id != null ){
			int tmp = Integer.parseInt(id);
			if(tmp > 0) {
				conf.id = tmp;
			}
		}
		String del = req.getParameter("delete");
		if(conf.id > 0 & del != null && del.equalsIgnoreCase("true")){
			conf.delete();
		}else {
			conf.url = req.getParameter("url");
			conf.queryKeyword = req.getParameter("queryKeyword");
			conf.termDelimiter = req.getParameter("termDelimiter");
			conf.kKeyword = req.getParameter("kKeyword");
			conf.additionalConfig = req.getParameter("additionalConfig");
			String activated = req.getParameter("activated");
			if (activated != null && activated.equalsIgnoreCase("activated")) {
				conf.activated = true;
			}
			conf.save();
		}

		resp.sendRedirect("/project08/searchengineConfig.jsp");
	}

}
