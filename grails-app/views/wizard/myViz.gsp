<%--
 Created by Temi Varghese on 28/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${name} trees</title>
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
    <legend>${name} Visualisations</legend>
    <g:if test="${!isDemonstration}">
        <p style="font-size:14px; max-width: 60em">List of all visualisation you created.</p>
    </g:if>
    <g:if test="${viz.size() != 0}">
        <table class="table table-hover table-bordered">
            <thead>
            <tr>
                <th>Visualisation</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${viz}" var="v" status="i">
                <tr>
                    <td class="span6">
                        <div>
                            <div class="btn btn-link" >
                                <a href="${createLink(controller: 'phylo', action: 'show')}/${v.getId()}">${v.getTitle()}</a>
                            </div>
                        </div>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
        <div>
        </div>
    </g:else>
    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>
</body>
</html>