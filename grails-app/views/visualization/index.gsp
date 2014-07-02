
<%@ page import="au.org.ala.phyloviz.Visualization" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main.gsp.old">
		<g:set var="entityName" value="${message(code: 'visualization.label', default: 'Visualization')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-visualization" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-visualization" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="viz" title="${message(code: 'visualization.viz.label', default: 'Viz')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${visualizationInstanceList}" status="i" var="visualizationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${visualizationInstance.id}">${fieldValue(bean: visualizationInstance, field: "viz")}</g:link></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${visualizationInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
