<%@ page import="au.org.ala.phyloviz.Phylo" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-phylo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-phylo" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="treeid" title="${message(code: 'phylo.treeid.label', default: 'Treeid')}" />
					
						<g:sortableColumn property="nodeid" title="${message(code: 'phylo.nodeid.label', default: 'Nodeid')}" />
					
						<g:sortableColumn property="displayName" title="${message(code: 'phylo.displayName.label', default: 'Display Name')}" />
					
						<th><g:message code="phylo.viz.label" default="Viz" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${phyloInstanceList}" status="i" var="phyloInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${phyloInstance.id}">${fieldValue(bean: phyloInstance, field: "treeid")}</g:link></td>
					
						<td>${fieldValue(bean: phyloInstance, field: "nodeid")}</td>
					
						<td>${fieldValue(bean: phyloInstance, field: "displayName")}</td>
					
						<td>${phyloInstance.viz.viz.toString()}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${phyloInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
