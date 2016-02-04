<%@page import="searchengine.features.search.Search"%>
<%@page import="searchengine.features.search.ImageSearch"%>
<%@page import="searchengine.model.AdItem"%>
<%@page import="searchengine.features.AdAPI"%>
<%@page import="searchengine.model.ImageSearchResultItem"%>
<%@page import="searchengine.model.SearchResultItem"%>
<%@ page import="java.util.*"%>
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
	final String[] lang = {"English", "German", "All"}, search_types = {"Text","Image"};
	String language = lang[0], search_type = search_types[0];
	try {
		Locale locale = request.getLocale();
		
		if (locale.getLanguage().equals("en"))
			language = lang[0];
		else if (locale.getLanguage().equals("de"))
			language = lang[1];
		else 
			language = lang[2];
		
		if (request.getParameter("language")!=null) {
			if (request.getParameter("language").equals(lang[0]))
				language = lang[0];
			else if (request.getParameter("language").equals(lang[1]))
				language = lang[1];
			else 
				language = lang[2];	
		}
		
		if(request.getParameter("search_type").equals(search_types[1])) {
			search_type = search_types[1];
		}
	} catch(Exception e) {}
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
					
					<select name="search_type" id="search_type" class="form-control">
						<% for (int count = 0; count < search_types.length; count++) { %>
							<%if(search_type.equals(search_types[count])) { %>           
	            				<option selected="selected" value="<%=search_types[count]%>"><%=search_types[count]%></option>  
	        				<%} else { %> 
        					<option value="<%=search_types[count]%>"><%=search_types[count]%></option>  
						<% }} %>
					</select>
					
					<select name="language" id="language" class="form-control">
						<% for (int count = 0; count < lang.length; count++) { %>
							<%if(language.equals(lang[count])) { %>           
	            				<option selected="selected" value="<%=lang[count]%>"><%=lang[count]%></option>  
	        				<%} else { %> 
        					<option value="<%=lang[count]%>"><%=lang[count]%></option>  
						<% }} %>
					</select>
					
					<div class="form-group" id="myQuery">
						<input type="text"  class="typeahead" placeholder="Query" name="query" autocomplete="off" value = "<%=query%>" id="query"  spellcheck="false">
					</div>
					<button type="submit" class="btn btn-default">Search</button>
				</form>
			</div> <!-- /.navbar-collapse -->
		</div> <!-- /.container-fluid -->
	</nav>
	
	<%
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
	};
	%>

	<%
	
	if (!search_type.equals(search_types[1])) {
		Search search = new Search();
		List<SearchResultItem> resultItems = null;
		if (language!=null && language.length()>0) {
			if (language.equalsIgnoreCase(lang[0]))
				resultItems = search.search(query,20,false,"en");
			else if (language.equalsIgnoreCase(lang[1]))
				resultItems = search.search(query,20,false,"de");
			else 
				resultItems = search.search(query,20,false);
		} else { 
			resultItems = search.search(query,20,true);
		}
		for (int i = 0; i < resultItems.size(); i++) {
			SearchResultItem item = resultItems.get(i);
			%>
			<div class="bs-callout bs-callout-info col-sm-12">
				<a href="<%=item.getUrl()%>">
				<div class="col-sm-1">
					<h3>#<%=item.getRank()%></h3>
				</div>
				<div class="col-sm-11">
					<h4><b><%=item.getTitle() %></b></h4>
					<h5> <%=item.getUrl()%></h5>
					<h5><%=item.getTf_idf()%></h5>
					<h6><%=item.getContent_snap()%></h6>
				</div>
				</a>
			</div>
			<%
		}
		if (resultItems.size() < 1) {
			%>
			<div style="top: 30%; left: 40%; position: absolute;">
				<h3 align="center" style="color:#5E6263;">No Result found</h3>		
			</div>
			<%
		}
	} else {
	%>
	
	<%
	
		ImageSearch search = new ImageSearch();
		List<ImageSearchResultItem> resultItems = null;
		
		if (language!=null && language.length()>0) {
			if (language.equalsIgnoreCase(lang[0]))
				resultItems = search.search_(query,20,false,"en");
			else if (language.equalsIgnoreCase(lang[1]))
				resultItems = search.search_(query,20,false,"de");
			else 
				resultItems = search.search_(query,20,false,"en");
		} else { 
			resultItems = search.search_(query,20,true,"en");
		}
		
		for (int i = 0; i < resultItems.size(); i++)
		{
			%>
			<img src="<%= "data:"+resultItems.get(i).getExtension()+";base64,"+ resultItems.get(i).getImage() %>"/>
			<%
		}
	}
	%>

	
	

	
</div>

	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/typeahead.bundle.min.js"></script>
	<script type="text/javascript" src="js/search.js"></script>
</body>
</html>
