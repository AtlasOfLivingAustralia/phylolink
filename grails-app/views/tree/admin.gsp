<%--
 Created by Temi Varghese on 27/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Expert Tree Admin</title>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink "/>
    <asset:stylesheet src="phylolink.css" />
</head>
<body class="fluid">
<div class="container">
    <h1>Maintain the list of expert recommended trees</h1>

    <g:if test="${flash.message}">
        <div class="alert alert-success">
         ${flash.message}
        </div>
    </g:if>



    <table id="treeTable" class="table table-hover table-bordered">
        <thead>
        <tr>
            <th>Tree name</th>
            <th>Species covered</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${trees}" var="tree" status="i">
            <g:render template="adminTableRow" model="[tree: tree]"/>
        </g:each>
        </tbody>
    </table>
    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'">
        <i class="icon icon-arrow-left"></i> Back
    </div>
</div>
</body>
</html>