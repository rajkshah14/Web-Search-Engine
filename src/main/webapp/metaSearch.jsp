<%@page import="searchengine.features.metasearch.MetaSearchResultItem"%>
<%@page import="searchengine.features.metasearch.Metasearch"%>
<%@page import="searchengine.model.SearchResultItem"%>
<%@ page import="java.util.*"%>
<%@ page import="searchengine.features.AdAPI" %>
<%@ page import="searchengine.model.AdItem" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML>

<html>
<head>
	<link rel="stylesheet" href="css/style.css">
	<link rel="stylesheet" href="css/bootstrap.min.css">
	<link rel="stylesheet" href="css/search.css">
	
</head>
<body>
<div class="container">

	<%
	String query = "";
	if (request.getParameter("query") != null) {
		query = request.getParameter("query");
	}

	%>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Search Engine Team 8</a>
			</div>

			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
			<!-- typeahead tt-query -->
				<form class="navbar-form navbar-right" role="search" method="get">

					<div class="form-group" id="myQuery">
						<input type="text"  placeholder="Query" name="query"  value = "<%=query%>" id="query" >
					</div>
					<button type="submit" class="btn btn-default">Search</button>
				</form>
			</div> <!-- /.navbar-collapse -->
		</div> <!-- /.container-fluid -->
	</nav>

	<%
		List<MetaSearchResultItem> resultItems;
		if(!query.equalsIgnoreCase("")){


		String[] split = query.split(" ");
		Metasearch search = new Metasearch(Arrays.asList(split));


		resultItems = search.getResults();
	%>
	<%
		if(resultItems.size() > 0){
		AdAPI api = new AdAPI();
		List<AdItem> ads = api.getAds(query);
		for (int i =0; i <ads.size(); i++) {
			AdItem item = ads.get(i);
	%>
	<div class="bs-callout bs-callout-info col-sm-12">
		<a href="<%=item.getClickURL()%>" >
			<% if (item.getImage().length()>0)  { %>
			<div class="col-sm-2"><img src="<%= item.getImage() %>" style="height: 100px; width: 100px"></div>
			<div class="col-sm-9">
					<%} else { %>
				<div class="col-sm-11">
					<%} %>
					<h4><b><%=item.getDescription()%></b></h4>
					<h5> <%=item.getUrl()%></h5>
				</div>
				<div class="col-sm-1">
					<h5 style="color: green">Ads</h5>
				</div>
		</a>
	</div>
	<%
			}
		};
	%>
	<%
	for (int i = 0; i < resultItems.size(); i++) {
		MetaSearchResultItem item = resultItems.get(i);

	%>
	<div class="bs-callout bs-callout-info col-sm-12">
		<a href="<%=item.getUrl()%>">
		<div class="col-sm-1">
			<h3>#<%=item.getRank()%></h3>
		</div>
		<div class="col-sm-11">
			<h5> <%=item.getUrl()%></h5>
			<h5><%=item.getScore()%></h5>
			<h6><%=item.getEngineURL()%></h6>
		</div>
		</a>
	</div>
	<%
		}
		}else {
			resultItems = new ArrayList<MetaSearchResultItem>();
		}
		if (resultItems.size() < 1) {
	%>
	<div style="top: 30%; left: 40%; position: absolute;">
		<h3 align="center" style="color:#5E6263;">No Result found</h3>		
	</div>
	<%
		}
	%>


	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/typeahead.bundle.min.js"></script>
	<script type="text/javascript" src="js/search.js"></script>
</div>
</body>
</html>
