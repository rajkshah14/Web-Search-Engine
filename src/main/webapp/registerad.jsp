<%@page import="searchengine.features.AdAPI"%>
<%@page import="java.util.Base64"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/search.css">

<title>Register Ad</title>
</head>
<body>

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
		
		<%
		try {
		String username = request.getParameter("username");
		String ngrams = request.getParameter("ngrams");
		String url = request.getParameter("URL");
		String description = request.getParameter("description");
		double budget = Double.parseDouble(request.getParameter("budget"));
		double cost_per_click = Double.parseDouble(request.getParameter("cost_per_click"));
		if (request.getParameter("image")!=null)
			new AdAPI().register(username, ngrams, url, description, budget, cost_per_click, request.getParameter("image"));
		else
			new AdAPI().register(username, ngrams, url, description, budget, cost_per_click);
		} catch (Exception e ) {}
		%>

		<form action="registerad.jsp" method="get">
			<input type="text" class="form-control" placeholder="User name" name="username" />
			<input type="text" class="form-control" placeholder="n grams" name="ngrams" />
			<input type="text" class="form-control" placeholder="URL" name="url"/>
			<input type="text" class="form-control" placeholder="Short description" name="description" />
			<input type="text" class="form-control" placeholder="Budget" name="budget" />
			<input type="text" class="form-control" placeholder="Cost per click" name="cost_per_click" />
			<input type="text" class="form-control" placeholder="Image url" name="image" />
			<input type="submit" value="Submit" />		
		</form>

	</div>

	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>

</body>
</html>