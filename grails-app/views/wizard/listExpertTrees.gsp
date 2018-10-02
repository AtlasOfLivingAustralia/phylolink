<%--
 Created by Temi Varghese on 27/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Expert Trees</title>
    <meta name="breadcrumbParent" content="${g.createLink( controller: 'phylo', action: 'startPage')},Phylolink"/>
</head>

<body>
<g:set var="userLoggedIn"><fc:userIsLoggedIn/></g:set>
<div class="container"  style="min-height: 700px">
    <h1>Select an expert recommended tree</h1>
    <table class="table table-hover table-bordered">
        <thead>
        <tr>
            <th>Tree name</th>
            <th>Species covered</th>
            <th>Action</th>
            <g:if test="${isAdmin}">
                <th>Admin functions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
        <g:each in="${trees}" var="tree" status="i">
            <tr>
                <td class="col-sm-5 col-md-5">
                    <div>
                        <div class="btn btn-link" style="text-align: left; " onclick="showInfo(${i})">
                            ${tree.getTitle()}
                            <i class="icon-info-sign" title="Show more information"></i>
                        </div>
                    </div>
                </td>
                <td>
                    <g:if test="${tree.expertTreeLSID}">
                        <a
                                href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                                target="_blank">${tree.getExpertTreeTaxonomy()}</a>
                    </g:if>
                    <g:else>
                        ${tree.getExpertTreeTaxonomy()}
                    </g:else>
                </td>
                <td>
                    <div class="btn-group-vertical" role="group">
                        <g:if test="${userLoggedIn}">
                            <a class="btn btn-small btn-primary"
                                 onclick="window.location = '${createLink( action: 'visualize')}?id=${tree.getId()}'">
                                <i class="icon icon-ok icon-white"></i> Open</a>
                        </g:if>
                        <a href="${createLink(controller: 'viewer',action: 'show')}?studyId=${tree.getId()}"
                                class="btn btn-default btn-small"><i class="icon icon-camera"></i> Preview</a>
                    </div>

                </td>
                <g:if test="${isAdmin}">
                <td>
                        <div class="btn btn-small" onclick="window.location =
                                '${createLink( controller: 'tree', action: 'rematchExpertTree')}?treeId=${tree.getId()}'">
                            <i class="icon-repeat"></i> Rematch
                        </div>
                        <g:render template="/tree/mapOtu"  model="${[id:tree.getId()]}"></g:render>
                </td>
                </g:if>
            </tr>
            <tr>
                <td colspan="3" style="display: none" class="info" id="info-${i}">
                    <div class="control-group" style="display: block"></div>
                %{--<label>Citation:</label>--}%<i>${tree.getReference()}</i>
                    <g:if test="${tree.doi}">
                        <div class="">
                            %{--<label>Doi:</label>--}%
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
    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
    <script>
        function showInfo(i){
//            $('.info').hide({
//                animate:'slow'
//            })
            $('#info-'+i).toggle({
                animate:'slow'
            })
        }
    </script>
</div>
</body>
</html>