<%--
 Created by Jasen Schremmer on 19/10/2019.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Visualisations that require your attention</title>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink"/>
    <asset:stylesheet src="phylolink.css" />
</head>

<body class="fluid" >
<div class="container"  style="min-height: 700px">
    <h1>Visualisations that require your attention</h1>
    <g:if test="${viz.size() != 0}">
		<p style="font-size:14px; max-width: 60em">List of all visualisations that require your attention</p>
        <table class="table table-hover table-bordered">
            <thead>
            <tr>
                <th>Visualisation</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${viz}" var="v" status="i">
                <tr>
                    <td>
                        <div>
                            <div class="btn btn-link" >
                                <a href="${createLink(controller: 'phylo', action: 'show')}/${v.getId()}">${v.getTitle()}</a>
                            </div>
                        </div>
                    </td>
                    <td width="150px" style="vertical-align: middle;">
						${v.getWorkflowStatus()}
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
		<p style="font-size:14px; max-width: 60em">Currently there are no visualisations that require your attention</p>
    </g:else>
    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>
</body>
</html>