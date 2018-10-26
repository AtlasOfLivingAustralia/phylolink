<tr id="summary-${tree.id}">
    <td width="40%">
        <div>
            <div class="btn btn-link" onclick="showInfo('${tree.id}')">
                <g:if test="${tree.expertTree}">
                    <span class="fa fa-mortar-board" title="Expert recommended tree">&nbsp;&nbsp;</span>
                </g:if>
                <g:else>
                    <span class="fa fa-users" title="Public tree">&nbsp;&nbsp;</span>
                </g:else>
                ${tree.getTitle()}
                <i class="icon-info-sign" title="Show more information"></i>
            </div>
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
<tr id="info-${tree.id}" class="hide">
    <td colspan="3" class="info" width="100%">
        <i>${tree.getReference()}</i>
        <g:if test="${tree.doi}">
            <div class="">
                <a href="${tree.getDoi()}">${tree.getDoi()}</a>
            </div>
        </g:if>
    </td>
</tr>
<tr id="controls-${tree.id}" class="hide" width="100%">
    <td colspan="3" class="info">
        <g:if test="${tree.expertTree}">
            <g:link params="[treeId: tree.id]" class="btn btn-primary btn-small"
                          action="toggleExpertTree" update="row${tree.id}"
                          title="Remove this tree from the list of expert trees' : ''}">
                Change to Public Tree
            </g:link>
        </g:if>
        <g:else>
            <g:formRemote name="bla" url="[action: 'toggleExpertTree']" controller="tree" action="toggleExpertTree"
                          method="POST" update="row${tree.id}"
                          onComplete="jQuery('#promoteExpertTreeModal${tree.id}').modal('hide')"
                          class="form-horizontal">
                <h4>Promote this tree as an 'expert recommended tree'.</h4>

                <div class="control-group">
                    <label for="taxa${tree.id}"
                           class="control-label">Select the higher taxa covered by this tree</label>

                    <div class="controls">
                        <g:hiddenField id="lsid${tree.id}" name="expertTreeLSID"/>
                        <g:textField id="taxa${tree.id}" type="text" name="expertTreeTaxonomy"
                                     class="form-control input-xlarge" required="true"
                                     placeholder="e.g. 'Aves' for a tree of bird species"
                                     autocomplete="off" value="${expertTreeLSID}"/>
                    </div>
                </div>

                <div class="controls">
                    <g:hiddenField name="treeId" value="${tree.id}"/>
                    <g:submitButton name="toggleExpertTree" params="[treeId: tree.id]"
                                    class="btn btn-primary btn-small    "
                                    value="Change to Expert Tree"/>
                </div>
            </g:formRemote>
        </g:else>
    </td>
</tr>

<script>
    $("#taxa${tree.id}").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "http://bie.ala.org.au/ws/search/auto.json?idxType=TAXON&q=" + request.term,
                contentType: "application/javascript",
                dataType: "jsonp",
                jsonpCallback: "JSON_CALLBACK",
                success: function (data) {
                    var values = [];
                    for (var i in data.autoCompleteList) {
                        values.push({label: data.autoCompleteList[i].name, value: data.autoCompleteList[i].guid});
                    }
                    response(values);
                }
            });
        },
        focus: function (event, ui) {
            $("#taxa${tree.id}").val(ui.item.label);
            return false;
        },
        select: function (event, ui) {
            $("#taxa${tree.id}").val(ui.item.label);
            $("#lsid${tree.id}").val(ui.item.value);
            return false;
        },
        minLength: 1
    });

    function showInfo(treeId) {
        $('#showHideDetailsBtn' + treeId + " i").toggleClass('fa-angle-double-down fa-angle-double-up');


        $('#info-' + treeId).toggle({
            animate: 'slow'
        });
        $('#controls-' + treeId).toggle({
            animate: 'slow'
        });

    }
</script>