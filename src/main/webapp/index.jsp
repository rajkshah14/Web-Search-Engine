<%@page import="searchengine.misc.db.ReadTables"%>
<%@page import="searchengine.Main"%>
<%@page import="searchengine.features.crawler.Crawler"%>
<%@page import="searchengine.misc.db.DML"%>
<%@page import="searchengine.Index"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="searchengine.model.*"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>

<html>
<head>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/style.css">
</head>
<body>

	<div class="container">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="#"> Project Team 08 </a>
				</div>
				<form class="navbar-form navbar-right" role="search" action="search.jsp">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Query" name="query">
					</div>
					<button type="submit" class="btn btn-default">Search</button>
				</form>
			</div>
		</nav>

		<div class="panel panel-primary col-md-3">
		<div class="panel-heading">Submit Crawl Request</div>
		<% {CrawlRequestItem item = new CrawlRequestItem(); %>
		<div class="form-group">
			<form action="http://localhost:8080/project08/submitCrawlerTask" method="post">
			<input type="text" class="form-control" placeholder="Url" value="<%=item.url%>">
			<input type="text" class="form-control" placeholder="Depth" value="<%=item.depth%>">
			<input type="text" class="form-control" placeholder="Max Document" value="<%=item.max_doc%>">
			
			<button type="crawl" class="btn btn-default" >Crawl</button>
			</form>
		</form>
		</div>
		
		<%} %>
		</div>
		<div class="panel panel-default col-md-9">

			<div class="panel-heading">Crawl Request</div>

			<table class="table">

				<tr>
					<th>Depth</th>
					<th>URL</th>
					<th>Max_Doc</th>
					<th>Time stamp</th>
					<th>isLeave</th>
					<th>isVisited</th>
				</tr>
				<%
					List<CrawlRequestItem> items = ReadTables.getCrawlRequestItem();
					for (int i = 0; i < items.size(); i++) {
						CrawlRequestItem item = items.get(i);
				%>

				<tr>
					<td><%=item.getDepth()%></td>
					<td><%=item.getUrl()%></td>
					<td><%=item.getMax_doc()%></td>
					<td><%=item.getTimeStamp()%></td>
					<td><%=item.isLeave_domain()%></td>
					<td><%=item.isVisited()%></td>
				</tr>

				<%
					}
				%>

			</table>
		</div>
	</div>

</body>
</html>
