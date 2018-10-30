<tr id="summary-${tree.id}">
    <td width="40%">
        <div>
                <g:if test="${tree.expertTree}">
                    <span class="fa fa-mortar-board" title="Expert recommended tree">&nbsp;&nbsp;</span>
                </g:if>
                <g:else>
                    <span class="fa fa-users" title="Public tree">&nbsp;&nbsp;</span>
                </g:else>
                ${tree.getTitle()}
                <i class="icon-info-sign" title="Show more information"></i>
        </div>
    </td>
    <td width="30%"><a href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                       target="_blank">${tree.getExpertTreeTaxonomy()}</a></td>
    <td width="30%">

        <a href="${createLink(controller: 'tree', action: 'edit')}?studyId=${tree.getId()}"
           class="btn btn-default btn-small"><i class="icon icon-camera"></i> Edit metadata</a>

        <a href="${createLink(controller: 'viewer', action: 'show')}?studyId=${tree.getId()}"
           class="btn btn-default btn-small"><i class="icon icon-camera"></i> Preview tree</a>

        <div class="btn btn-default btn-small" onclick="window.location =
                '${createLink( controller: 'tree', action: 'rematchExpertTree')}?treeId=${tree.getId()}&redirect=treeAdmin'">
            <i class="icon-repeat"></i> Rematch
        </div>

        <div class="btn btn-default btn-small" onclick="window.location =
            '${createLink( controller: 'tree', action: 'mapOtus')}?id=${tree.getId()}&redirect=treeAdmin'">
            <i class="icon-repeat"></i> Rematch manually
        </div>

    </td>
</tr>