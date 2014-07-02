<%@ page import="au.org.ala.phyloviz.Visualization" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main.gsp.old">
    <g:set var="entityName" value="${message(code: 'visualization.label', default: 'Visualization')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-visualization" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                                    default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-visualization" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list visualization">

        <g:if test="${visualizationInstance?.viz}">
            <li class="fieldcontain">
                <span id="viz-label" class="property-label"><g:message code="visualization.viz.label"
                                                                       default="Viz"/></span>

                <span class="property-value" aria-labelledby="viz-label"><g:fieldValue bean="${visualizationInstance}"
                                                                                       field="viz"/></span>

            </li>
        </g:if>

    </ol>
    <g:form url="[resource: visualizationInstance, action: 'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${visualizationInstance}"><g:message
                    code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
