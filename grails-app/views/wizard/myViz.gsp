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
<div class="container">
    <g:if test="${flash.message}">
        <div class="message alert-info" role="status">${flash.message}</div>
    </g:if>
    <legend>${name} Visualisations</legend>
    <p style="font-size:14px; max-width: 60em">List of all visualisation you created. You can click
        on visualisation name to view it. Or, use
        <span class="label label-info">back</span> button to go to previous page</p>
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