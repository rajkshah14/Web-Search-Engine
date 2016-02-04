package searchengine.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import searchengine.features.crawler.Crawler;

@WebServlet("/submitCrawlerTask")
public class SubmitCrawlerTask extends HttpServlet {

	private static final long serialVersionUID = -7782040592094091646L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Request receiverd");
		req.getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
		req.getSession().invalidate();
		try {
			Crawler.crawl(5, true, 200, new String[] { "http://www.uni-kl.de" });
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
