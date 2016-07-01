<%--
 Created by Temi Varghese on 27/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Expert Tree Admin</title>
    <r:require modules="phylojive, jquery-ui"/>
</head>

<body>

<div class="container" style="min-height: 700px">
    <g:if test="${flash.message}">
        <div class="row-fluid">
            <div class="alert top-buffer">
                <button type="button" class="close" data-dismiss="alert">&times;</button>${flash.message}
            </div>
        </div>
    </g:if>

    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri: '/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a></li>
            </ul>
        </div>
    </div>

    <legend>Maintain the list of expert recommended trees</legend>
    <table id="treeTable" class="table table-hover table-bordered">
        <thead>
        <tr>
            <th>Tree name</th>
            <th>Species covered</th>
            <th>Action</th>
        </tr>
        </thead>
        <g:each in="${trees}" var="tree" status="i">
            <tbody id="row${tree.id}">

                <g:render template="adminTableRow" model="[tree: tree]"/>

        </tbody>
        </g:each>
    </table>

    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>

</body>
</html>