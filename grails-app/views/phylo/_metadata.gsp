<div>
    <table class="table table-bordered">
        <tbody>
        <tr>
            <th colspan="2">Tree metadata</th>
        </tr>
        <tr>
            <td>Title:</td>
            <td>${tree.getTitle()}</td>
        </tr>
        <tr>
            <td>Reference:</td>
            <td>${tree.getReference()}</td>
        </tr>
        <tr>
            <td>Year:</td>
            <td>${tree.getYear()}</td>
        </tr>
        <g:if test="${tree.getDoi()!= null}">
            <tr>
                <td>Doi:</td>
                <td>${tree.getDoi()}</td>
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
    <table class="table table-bordered">
        <tbody>
        <tr>
            <th colspan="2">Actions</th>
        </tr>
        <tr>
            <td>
                Download tree:
            </td>
            <td>
                <a class="btn" href="${createLink(controller: 'tree', action: 'download')}?id=${studyId}"><i class="icon icon-download"></i> Download</a>
            </td>
        </tr>
        <tr>
            <td>
                Link tree with data:
            </td>
            <td>
                <a class="btn btn-primary" href="${createLink(controller: 'wizard', action: 'visualize')}?id=${studyId}">
                    <i class="icon icon-arrow-right"></i> Visualise with Phylolink</a>
            </td>
        </tr>
        </tbody>
    </table>
</div>