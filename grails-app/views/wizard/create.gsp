<%--
 Created by Temi Varghese on 23/10/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Add tree</title>
    <r:require modules="knockout,jquery-ui,create"/>
    <r:require modules="bugherd"/>
</head>

<body>
<div class="container"  style="min-height: 700px">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri:'/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a></li>
            </ul>
        </div>
    </div>
    <g:if test="${flash.message}">
        <div class="message alert-info" role="status">${flash.message}</div>
    </g:if>

    <g:form action="create" class="form-horizontal" method="POST" enctype="multipart/form-data">
        <legend>Upload a tree</legend>
        <p style="font-size:14px">Enter your tree here by completing the form below. Tree data can be provided by uploading a file or by pasting the data into the box below. Supported formats are NEXML and NEWICK.<p/>

        <fieldset class="form">
            <div class="row-fluid">
                <g:render template="/tree/form" model="['tree':tree]"/>
                <div class="control-group">
                    <div class="controls">
                        <div name="back" class="btn" onclick="window.location = '${back}'"><i
                                class="icon icon-arrow-left"></i> Back</div>
                        <button type="submit" class="btn btn-primary" value="Next"><i
                                class="icon icon-white icon-arrow-right"></i> Next</button>
                    </div>
                </div>
        </fieldset>
    </g:form>
</div>
</body>
</html>