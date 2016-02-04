<%@page import="searchengine.features.AdAPI"%>
<%@page import="searchengine.model.UserAdItem"%>
<%@page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/search.css">

<title>User details</title>
</head>
<body>
<%
String username = "";
if (request.getParameter("username")!=null)
	username = request.getParameter("username");
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
			<form class="navbar-form navbar-left" role="search" action="userinfo.jsp" method="get">
	        	<div class="form-group">
	          		<input id="username" type="text" class="form-control col-md-9" placeholder="Username"  name="username" value=<%=username %>  >
	        	</div>
	        	<button type="submit" class="btn btn-default">Go</button>
      		</form>
		</div>
		<!-- /.container-fluid --> </nav>
		
		<div class="panel panel-default">
		<div class="panel-heading"><b>Advertisement Request status</b></div>

		<table class="table">
		<tr>
			<th>URL</th>
			<th>Budget</th>
			<th>Click left</th>
			<th>Cost per click</th>
			<th>ID</th>
		</tr>
		
		<%
		try {
		
		List<UserAdItem> items = new AdAPI().getUserAdOverview(username);
		for (int i = 0; i < items.size(); i++) {
			UserAdItem item = items.get(i);
			%>
				<tr id = "<%= item.getId() %>" onclick="getDetails(this)" >
					<td><%= item.getUrl()%></td>
					<td><%= item.getBugdet()%></td>
					<td><%= item.getClick_left()%></td>
					<td><%= item.getCostPerClick()%></td>
					<td><%= item.getId()%></td>
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
	<script type="text/javascript">
	function getDetails(e) {
		var win = window.open("/project08/adclicks.jsp?id=" + e.id, '_blank');
	  	win.focus();
		
	} </script>
</body>
</html>