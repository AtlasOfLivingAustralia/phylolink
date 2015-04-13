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
    %{--<g:hasErrors bean="${tree}">--}%
        %{--<ul class="errors alert-error unstyled" role="alert">--}%
            %{--<g:eachError bean="${tree}" var="error">--}%
                %{--<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message--}%
                        %{--error="${error}"/></li>--}%
            %{--</g:eachError>--}%
        %{--</ul>--}%
    %{--</g:hasErrors>--}%
    <g:form action="save" class="form-horizontal" method="POST" enctype="multipart/form-data">
        <legend>Upload a tree</legend>
        <p style="font-size:14px">Enter your tree here by filling the form below. Pressing <span class="label label-info">next</span> button will save the tree. Use <span class="label">back</span> button to go to previous page</p>
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