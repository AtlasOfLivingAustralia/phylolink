<%--
 Created by Temi Varghese on 28/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${name} trees</title>
    <r:require modules="css"/>
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
    <legend>${name} trees</legend>
    <p >List of all trees that you have uploaded.</p>
    <g:if test="${trees.size() != 0}">
        <table class="table table-hover table-bordered">
            <thead>
            <tr>
                <th>Tree name</th>
                <th>Species covered</th>
                <th>Choose</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${trees}" var="tree" status="i">
                <tr>
                    <td width="43%">
                        <div>
                            <div class="btn btn-link" onclick="showInfo(${i})">
                                ${tree.getTitle()}
                                <i class="icon-info-sign"  title="Show more information"></i>
                            </div>
                        </div>
                    </td>
                    <td width="30%"><a
                            href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                            target="_blank">${tree.getExpertTreeTaxonomy()}</a>
                    </td>
                    <td width="37%">
                        <div class="btn btn-small btn-primary" onclick="window.location =
                                '${createLink( action: 'visualize')}?id=${tree.getId()}'">
                            <i class="icon-ok"></i> Open</div>
                        <a
                                href="${createLink(controller: 'viewer',action: 'show')}?studyId=${tree.getId()}"
                                class="btn btn-small" ><i class="icon icon-camera"></i> Preview tree</a>
                        <div class="btn btn-small" onclick="window.location =
                                '${createLink( controller: 'tree', action: 'rematchMyTree')}?treeId=${tree.getId()}'">
                            <i class="icon-repeat"></i> Rematch
                        </div>
                        <g:render template="/tree/mapOtu"  model="${[id:tree.getId()]}"></g:render>
                        <a id="deleteTreeLink${tree.getId()}" class="btn btn-default btn-small" data-toggle="modal" href="#${tree.getId()}ConfirmationModal"><i class="fa fa-trash"></i>&nbsp;Delete tree</a>

                        <div id="${tree.getId()}ConfirmationModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="Confirmation" aria-hidden="true">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                <h3 id="myModalLabel">Confirmation</h3>
                            </div>
                            <div class="modal-body">
                                <p>Are you sure you wish to delete this tree?</p><p>This operation cannot be undone.</p>
                            </div>
                            <div class="modal-footer">
                                <g:form method="DELETE" controller="Tree" action="deleteTree" params="[id: tree.getId()]" class="inline-block">
                                    <g:actionSubmit value="Delete tree" controller="Tree" action="deleteTree" params="[id: tree.getId()]" class="btn btn-warning" />
                                </g:form>
                                <button id="closeDownloadModal" class="btn closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" style="display: none" class="info" id="info-${i}">
                        <div class="control-group" style="display: block"></div>
                        <i>${tree.getReference()}</i>
                        <g:if test="${tree.doi}">
                            <div class="">
                                <a href="${tree.getDoi()}">${tree.getDoi()}</a>
                            </div>
                        </g:if>
                        <g:if test="${tree.notes}">
                            <div><i>${tree.getNotes()}</i></div>
                        </g:if>
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
    <script>
        function showInfo(i) {
            $('.info').hide({
                animate: 'slow'
            })
            $('#info-' + i).show({
                animate: 'slow'
            })
        }
    </script>
</div>

</body>
</html>