<div class="panel panel-default" style="margin-top:10px;">
    <div class="panel-heading">
        Phylogenetic tree metadata
    </div>
    <div class="panel-body">
        <table class="table table-sm">
            <tbody>
            <tr>
                <td class="col-md-2">Title:</td>
                <td>${tree.getTitle()}</td>
            </tr>
            <g:if test="${tree.getReference()!= null}">
            <tr>
                <td>Publication citation:</td>
                <td>${tree.getReference()}</td>
            </tr>
            </g:if>
            <g:if test="${tree.getYear()!= null}">
            <tr>
                <td>Publication Year:</td>
                <td>${tree.getYear()}</td>
            </tr>
            </g:if>
            <g:if test="${tree.getDoi()!= null}">
                <tr>
                    <td>Publication DOI:</td>
                    <td>
                       <g:link url="${tree.getDoi()}">${tree.getDoi()}</g:link>
                    </td>
                </tr>
            </g:if>
            <g:if test="${tree.getNotes()!= null}">
                <tr>
                    <td>Notes:</td>
                    <td>${tree.getNotes()}</td>
                </tr>
            </g:if>
            </tbody>
        </table>

        <div>
            <a class="btn btn-default" href="${createLink(controller: 'tree', action: 'download')}?id=${studyId}">
                <i class="glyphicon glyphicon-download"></i> Download Tree
            </a>
        </div>
    </div>
</div>
