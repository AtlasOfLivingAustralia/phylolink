<%--
 Created by Jasen Schremmer on 10/10/2019.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="au.org.ala.phyloviz.Phylo" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Published Visualisations</title>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink"/>
    <asset:stylesheet src="phylolink.css" />
	<asset:javascript src="js/application.js" />
</head>

<body class="fluid" >
<div class="container"  style="min-height: 700px">
    <h1>Published Visualisations</h1>
    
	<g:if test="${(viz.totalCount > pageSize)}">
		<div class="row">
			<nav class="col-sm-12 col-centered text-center">
				<div class="pagination pagination-lg">
					<hf:paginate total="${viz.totalCount}" controller="wizard" action="published"
								 omitLast="false" omitFirst="false" prev="&laquo;" next="&raquo;"
								 max="${pageSize}" offset="${offset}"/>
				</div>
			</nav>
		</div>
	</g:if>
	<div class="panel panel-default">
		<div class="panel-body">

			<div class="word-limit">
				<div class="well">
					<form name="filterForm" class="form-horizontal">
						<div class="form-group" style="margin-bottom: -8px;">
							<label for="filter" class="col-sm-2 control-label">Search filter</label>

							<div class="col-sm-10">
								<div class="input-group">
									<input name="filter" type="text" class="form-control" value="${params.filter}"
										   placeholder="Refine your display results">
									<span class="input-group-btn">
										<a class="btn btn-default" type="button" onclick="document.filterForm.submit(); return false;">Filter</a>
									</span>
								</div>

								<p class="help-block">Use the search filter to refine the display results below.<br/>
								Each word in the search must exist in at least one of: title, genus, species, created by or doi</p>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="row">
				<table class="table table-hover table-bordered">
					<thead>
					<tr>
						<th>Published on</th>
						<th>Genus</th>
						<th>Visualisation</th>
					</tr>
					</thead>
					<tbody>
					<g:each in="${viz.list}" var="v" status="i">
						<tr>
							<td style="padding-top: 14px;">
								<script>document.write(dateToString('${v.doiCreationDate}'));</script>
							</td>
							<td style="padding-top: 14px;">
								<g:each in="${Phylo.getArrayForNewLineAndSemiColonDelimitedValue(v.genus)}" var="g" status="gi">
									<g:if test="${gi > 0}"><br/></g:if>${g}
								</g:each>
							</td>
							<td width="70%">
								<div class="btn btn-link" >
									<a href="${createLink(controller: 'phylo', action: 'show')}/${v.getId()}">${v.getTitle()}</a>
								</div>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="small pull-right">Showing ${Math.min(viz.totalCount, offset + 1)} to ${Math.min(viz.totalCount, offset + pageSize)} of ${viz.totalCount}</div>

	<div class="clear" style="clear: both;"></div>

	<g:if test="${(viz.totalCount > pageSize)}">
		<div class="row">
			<nav class="col-sm-12 col-centered text-center">
				<div class="pagination pagination-lg">
					<hf:paginate total="${viz.totalCount}" controller="wizard" action="published"
								omitLast="false" omitFirst="false" prev="&laquo;" next="&raquo;"
								max="${pageSize}" offset="${offset}"/>
				</div>
			</nav>
		</div>
	</g:if>
	
    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>
</body>
</html>