<%@page import="project08.model.SearchResultItem"%>
<%@ page import="java.util.*"%>
<%@ page import="project08.search.*"%>
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
	final String[] lang = {"English", "German", "All"};
	String language = lang[0];
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
	Search search = new Search();
	List<SearchResultItem> resultItems = null;
	if (language!=null && language.length()>0) {
		if (language.equalsIgnoreCase(lang[0]))
			resultItems = search.search(query,20,false,"en");
		else if (language.equalsIgnoreCase(lang[1]))
			resultItems = search.search(query,20,false,"de");
		else 
			resultItems = search.search(query,20,false);
	} else 
	resultItems = search.search(query,20,true);
	Snippet snippet = new Snippet();
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
			<h6><%=snippet.getSnippet(item.getContent_snap(), query)%></h6>
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
	%>


	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/typeahead.bundle.min.js"></script>
	<script type="text/javascript" src="js/search.js"></script>
</div>
</body>
</html>
