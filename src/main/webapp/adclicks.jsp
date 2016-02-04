
<%@page import="searchengine.features.*"%>
<%@page import="searchengine.model.AdClickItem"%>
<%@page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/search.css">

<title>AD clicks</title>
</head>
<body>
<%

String id = "";
if (request.getParameter("id")!=null)
	id = request.getParameter("id");
%>

	<div class="container">

		<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Search Engine Team 8</a>
			</div>

			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1"></div>
			<!-- /.navbar-collapse -->
			
		</div>
		<!-- /.container-fluid --> </nav>
		
		<div class="panel panel-default">
		<div class="panel-heading"><b>Advertisement Request status</b></div>

		<table class="table">
		<tr>
			<th>#</th>
			<th>Time</th>
			<th>IP</th>
		</tr>
		
		<%
		try {
		
		List<AdClickItem> items = new AdAPI().getAdClicks(id);
		for (int i = 0; i < items.size(); i++) {
			AdClickItem item = items.get(i);
			%>
				<tr>
					<td><%= i+1 %></td>
					<td><%= item.getIp()%></td>
					<td><%= item.getTimestamp()%></td>
				</tr>
			<%
		}
		} catch (Exception e ) {}
		
		
		%>
			
		</table>
			
			
		</div>
		</div>
	</div>

	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>

</body>
</html>