<%@page import="searchengine.misc.db.ReadTables"%>
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
		<div class="panel-heading">Configure new searchengine</div>

		<div class="form-group">
			<form id="config" name="config" action="http://localhost:8080/project08/searchengine" method="post">
				<input id="id" type="hidden" name="id" value="0">
				<input id = "url" type="text" class="form-control" placeholder="Url" name="url">
				<input id = "queryKeyword" type="text" class="form-control" placeholder="Query Keyword" name="queryKeyword">
				<input id="termDelimiter" type="text" class="form-control" placeholder="Query Delimiter (eg. %20,+,...)" name="termDelimiter">
				<input id="kKeyword" type="text" class="form-control" placeholder="k Keyword" name="kKeyword">
				<input id="additionalConfig" type="text" class="form-control" placeholder="Additional Configuration" name="additionalConfig">
				<input id="activated" type="checkbox" name="activated" value="activated" checked>Activated<br>
				<input id="delete" type="hidden" name="delete" value="" >

				<button type="Submit" class="btn btn-default" name="sub_config" >Submit</button>
			</form>
		</div>
		

		</div>
		<div class="panel panel-default col-md-9">

			<div class="panel-heading">Searchengines</div>

			<table class="table">

				<tr>
					<th>ID</th>
					<th>URL</th>
					<th>Query Keyword</th>
					<th>Query Delimiter</th>
					<th>k Keyword</th>
					<th>Additional Configuration</th>
					<th>Activated</th>
					<th></th>
				</tr>
				<%
					List<SearchengineConfig> items = ReadTables.getSearchengineConfig();
					for (int i = 0; i < items.size(); i++) {
						SearchengineConfig item = items.get(i);
				%>

				<tr>
					<td><%=item.id%></td>
					<td><%=item.url%></td>
					<td><%=item.queryKeyword%></td>
					<td><%=item.termDelimiter%></td>
					<td><%=item.kKeyword%></td>
					<td><%=item.additionalConfig%></td>
					<td><a href="#" onclick="change('<%=item.id%>','<%=item.url%>','<%=item.queryKeyword%>','<%=item.termDelimiter%>','<%=item.kKeyword%>','<%=item.additionalConfig%>','<%=item.activated%>');return false;"><%=item.activated%></a></td>
					<td><a href="#" onclick="rem('<%=item.id%>');return false;">Delete</a></td>
				</tr>
				<%
					}
				%>

			</table>
		</div>
	</div>
	<script>
		function change(id,url,qK,qD,kK,aC,a) {

			document.getElementById('id').value = id;
			document.getElementById('url').value = url;
			document.getElementById('queryKeyword').value = qK;
			document.getElementById('termDelimiter').value = qD;
			document.getElementById('kKeyword').value = kK;
			document.getElementById('additionalConfig').value = aC;
			if(a === 'true')
				document.getElementById('activated').checked = false;
			else
				document.getElementById('activated').checked = true;

			document.getElementById('config').submit();
		}

		function rem(id) {
			document.getElementById('id').value = id;
			document.getElementById('delete').value = "true";
			document.getElementById('config').submit();
		}
	</script>

</body>
</html>
