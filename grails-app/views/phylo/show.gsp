<%@ page import="au.org.ala.phyloviz.Phylo" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
%{--<a href="#show-phylo" class="skip" tabindex="-1"><g:message code="default.link.skip.label"--}%
                                                            %{--default="Skip to content&hellip;"/></a>--}%

%{--<div class="nav" role="navigation">--}%
    %{--<ul>--}%
        %{--<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>--}%
        %{--<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>--}%
        %{--<li><g:link class="create" action="create"><g:message code="default.new.label"--}%
                                                              %{--args="[entityName]"/></g:link></li>--}%
    %{--</ul>--}%
%{--</div>--}%

<div id="show-phylo" class="content scaffold-show" role="main">
    %{--<h1>${phyloInstance.displayName}</h1>--}%

    <div id="show-widgetpanel-group" style="float:right">
        Adjust widget panel size:
        <div class="btn-group">
            <button class="btn" onclick="toggleProp('min', this)"><i class="icon-minus"></i></button>
            <button class="btn active" onclick="toggleProp('half', this)"><i class="icon-resize-small"></i></button>
            <button class="btn" onclick="toggleProp('full', this)"><i class="icon-resize-full"></i></button>
        </div>
    </div>
    <g:form url="[resource: phyloInstance, action: 'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${phyloInstance}"><g:message code="default.button.edit.label"
                                                                                      default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
    <div id="phylolink-content">
        <div id="phylolink-visualization">
            <g:render template="phylojive" model="['instance': phyloInstance]"/>
        </div>

        <div id="phylolink-widgetpanel">
            <div id="phyolink-widgetsblock">
                <div id="widgets-overlay"></div>

                <div id="phylolink-widgets">
                    <div id="phylolink-widgetsscrollable">
                    <g:each in="${phyloInstance.widgets}" var="w" status="i">
                    %{--<g:render template="environment" model="['i':i]"/>--}%
                        <g:if test="${phyloInstance.widgets[i].config == 'pd'}">
                            <g:render template="pd" model="['i': i, 'title': 'Phylogenetic Diversity']"/>
                        </g:if>
                        <g:else>
                            <g:render template="environmental" model="['i': i]"/>
                        </g:else>
                    </g:each>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>