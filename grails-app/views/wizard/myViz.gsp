<%--
 Created by Temi Varghese on 28/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${name} Visualisations</title>
    <meta name="breadcrumbParent" content="${g.createLink( controller: 'phylo', action: 'startPage')},Phylolink"/>
</head>

<body>
<div class="container"  style="min-height: 700px">
    <h1>${name} Visualisations</h1>
    <g:if test="${!isDemonstration}">
        <p style="font-size:14px; max-width: 60em">List of all visualisation you created.</p>
    </g:if>
    <g:if test="${viz.size() != 0}">
        <table class="table table-hover table-bordered">
            <thead>
            <tr>
                <th>Visualisation</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${viz}" var="v" status="i">
                <tr>
                    <td width="90%">
                        <div>
                            <div class="btn btn-link" >
                                <a href="${createLink(controller: 'phylo', action: 'show')}/${v.getId()}">${v.getTitle()}</a>
                            </div>
                        </div>
                    </td>
                    <td>
                        <a id="deleteTreeLink${v.getId()}" class="btn btn-default btn-small" data-toggle="modal" href="#${v.getId()}ConfirmationModal"><i class="fa fa-trash"></i>&nbsp;Delete</a>

                        <div id="${v.getId()}ConfirmationModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="Confirmation" aria-hidden="true">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                <h3 id="myModalLabel">Confirmation</h3>
                            </div>
                            <div class="modal-body">
                                <p>Are you sure you wish to delete this visualisation?</p><p>This operation cannot be undone.</p>
                            </div>
                            <div class="modal-footer">
                                <g:form method="DELETE" controller="phylo" action="deleteViz" params="[id: v.id]" class="inline-block">
                                    <g:actionSubmit value="Delete" controller="phylo" action="deleteViz" params="[id: v.id]" class="btn btn-danger"/>
                                </g:form>
                                <button id="closeDownloadModal" class="btn closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>
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
    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>
</body>
</html>