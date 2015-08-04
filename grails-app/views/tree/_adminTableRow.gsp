<tr id="summary-${tree.id}">
    <td width="40%">
        <div>
            <div class="btn btn-link" onclick="showInfo('${tree.id}')">
                ${tree.getTitle()}
                <i class="icon-info-sign" title="Show more information"></i>
            </div>
        </div>
    </td>
    <td width="30%"><a href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                       target="_blank">${tree.getExpertTreeTaxonomy()}</a></td>
    <td width="30%">
        <a id="showHideDetailsBtn${tree.id}" class="btn btn-small btn-primary" data-toggle="modal" href="#"
           onclick="showInfo('${tree.id}')">
            <i class="fa fa-angle-double-down"></i>&nbsp;&nbsp;Details</a>

        <a href="${createLink(controller: 'viewer', action: 'show')}?studyId=${tree.getId()}"
           class="btn btn-small"><i class="icon icon-camera"></i> Preview tree</a>
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
            <g:remoteLink params="[treeId: tree.id]" class="btn btn-primary btn-small"
                          action="toggleExpertTree" update="row${tree.id}"
                          title="Remove this tree from the list of expert trees' : ''}">
                Change to Public Tree
            </g:remoteLink>
        </g:if>
        <g:else>
            <g:formRemote name="bla" url="[action: 'toggleExpertTree']" controller="tree" action="toggleExpertTree"
                          method="POST" update="row${tree.id}"
                          onComplete="jQuery('#promoteExpertTreeModal${tree.id}').modal('hide')">
                <p>Promote this tree as an 'expert recommended tree'.</p>

                <div class="form-horizontal">
                    <div class="control-group">
                        <label for="expertTreeTaxonomy" class="control-label">Species covered</label>

                        <div class="controls">
                            <g:textField name="expertTreeTaxonomy" id="expertTreeTaxonomy" required="true"
                                         value="${expertTreeTaxonomy}"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label for="taxa${tree.id}" class="control-label">Select the taxa covered by this tree</label>

                        <div class="controls">
                            <g:hiddenField id="lsid${tree.id}" name="expertTreeLSID"/>
                            <g:textField id="taxa${tree.id}" type="text" name="expertTreeLSID12"
                                         class="form-control" required="true"
                                         autocomplete="off" value="${expertTreeLSID}"/>
                        </div>
                    </div>
                </div>

                <g:hiddenField name="treeId" value="${tree.id}"/>
                <g:submitButton name="toggleExpertTree" params="[treeId: tree.id]"
                                class="btn btn-primary btn-small fa fa-mortar-board"
                                value="Change to Expert Tree"/>
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