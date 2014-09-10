<%@ page import="au.org.ala.phyloviz.Phylo" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}"/>
        %{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}"/>--}%
        <title><g:message code="default.show.label" args="[entityName]"/></title>
        <r:require modules="application,form"/>
        %{--render widgets after getting all requirements--}%
        <g:render template="widgets"/>
        <g:render template="phylojive" model="['instance': phyloInstance]"/>
    </head>
    <body>
        <div id="show-phylo" class="content scaffold-show" role="main">
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
                    <g:link class="edit" action="edit" resource="${phyloInstance}">
                        <g:message code="default.button.edit.label" default="Edit"/>
                    </g:link>
                    <g:actionSubmit class="delete" action="delete"
                                    value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                    onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
                </fieldset>
            </g:form>
            <div id="phylolink-content">
                <div id="phylolink-visualization">
                    <div id="content">
                        <header id="page-header">
                            <div class="inner">
                            </div><!--inner-->
                        </header>
                        <div class="row-fluid">
                            %{--<h2 id="loadingMsg" >Loading Tree...</h2>--}%
                            <div id="section" class="span12">
                                <div id="infovis"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="phylolink-widgetpanel">
                    <div id="phyolink-widgetsblock">
                        <div id="widgets-overlay"></div>
                        <div id="phylolink-widgets">
                            <div id="phylolink-widgetsscrollable">
                                <g:each in="${phyloInstance.widgets}" var="w" status="i">
                                    <g:render template="${phyloInstance.widgets[i].type}/show" model="['i': i]"/>
                                </g:each>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>