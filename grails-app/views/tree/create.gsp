<%--
 Created by Temi Varghese on 23/10/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Add tree</title>
    <r:require modules="knockout,jquery-ui"/>
    <r:require modules="bugherd"/>
</head>

<body>
<div class="container">
    <g:form action="save" class="form-horizontal">

        <legend>Upload a tree</legend>

        <fieldset class="form">
            <div class="row-fluid">
                <g:render template="form"/>
                <div class="control-group">
                    <div class="controls">
                        <g:submitButton name="create" value="Create" class="btn btn-primary right"/>
                    </div>
                </div>
            </div>
        </fieldset>

    </g:form>
</div>
</body>
</html>