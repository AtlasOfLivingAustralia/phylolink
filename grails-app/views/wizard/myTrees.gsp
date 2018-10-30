<%--
 Created by Temi Varghese on 28/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${raw(name)} trees</title>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink"/>
</head>

<body class="fluid">
<div class="container" >
    <h1>${name} trees</h1>
    <p >List of all trees that you have uploaded.</p>
    <g:if test="${trees}">
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
                            ${tree.getTitle()}
                        </div>
                    </td>
                    <td width="30%"><a
                            href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                            target="_blank">${tree.getExpertTreeTaxonomy()}</a>
                    </td>
                    <td width="37%">
                        <div class="btn btn-small btn-primary" onclick="window.location =
                                '${createLink( action: 'visualize')}?id=${tree.getId()}'">
                            <i class="glyphicon glyphicon-ok"></i> Create visualisation</div>
                        <a
                                href="${createLink(controller: 'viewer',action: 'show')}?studyId=${tree.getId()}"
                                class="btn btn-default btn-small" ><i class="glyphicon glyphicon-camera"></i> Preview tree</a>
                        <div class="btn btn-default btn-small" onclick="window.location =
                                '${createLink( controller: 'tree', action: 'rematchMyTree')}?treeId=${tree.getId()}'">
                            <i class="glyphicon glyphicon-repeat"></i> Rematch
                        </div>

                        <g:render template="/tree/mapOtu"  model="${[id:tree.getId()]}"></g:render>
                        <a id="deleteTreeLink${tree.getId()}" class="btn btn-default btn-small" data-toggle="modal" href="#${tree.getId()}ConfirmationModal"><i class="fa fa-trash"></i>&nbsp;Delete tree</a>


                        <div id="${tree.getId()}ConfirmationModal" class="modal" tabindex="-1" role="dialog" aria-labelledby="Confirmation" aria-hidden="true">

                            <div class="modal-dialog" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                    <h3 id="myModalLabel">Confirmation</h3>
                                </div>
                                    <div class="modal-body">
                                        <p>Are you sure you wish to delete this tree?</p><p>This operation cannot be undone.</p>
                                    </div>
                                    <div class="modal-footer">
                                        <g:form method="DELETE" controller="Tree" action="deleteTree" params="[id: tree.getId()]" class="inline-block">
                                            <g:actionSubmit value="Delete tree" controller="Tree" action="deleteTree" params="[id: tree.getId()]" class="btn btn-danger" />
                                            <button id="closeDownloadModal" class="btn btn-default closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>
                                        </g:form>

                                    </div>
                                </div>
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
            <p>
                You have not yet uploaded a tree.
            </p>
        </div>
    </g:else>
    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
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