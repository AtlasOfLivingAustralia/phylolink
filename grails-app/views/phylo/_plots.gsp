<style>
.ui-autocomplete {
    max-height: 200px;
    overflow-y: auto; /* prevent horizontal scrollbar */
    overflow-x: hidden; /* add padding to account for vertical scrollbar */
    z-index: 1000 !important;
}

.ellipselabel {
    white-space: nowrap;
    max-width: 220px;
    overflow: hidden; /* "overflow" value must be different from "visible" */
    text-overflow: ellipsis;
}
</style>

<div id="habitatMain">
    <div class="btn btn-primary" data-bind="click:addHabitat"><i class="icon-white icon-plus-sign"></i> Plot profile
    </div>
    <div id="downloadPlotDataLink" class="btn btn-default" data-toggle="modal" href="#plotOccurrenceDownloadModal"
       data-bind="visible: habitats().length > 0"><i class="fa fa-download"></i>&nbsp;Download raw data</div>

    <div data-bind="sortable:{data:habitats, afterMove: $root.onMove}">
        <div class="item top-buffer">
            <div class="label label-default" data-bind="visible: !$root.isHabitatSelected($data)">
                <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>

                <div class="ellipselabel" style="color: #ffffff;display:inline-block;cursor: pointer" href="#"
                     data-bind="text: displayName, click: $root.selectedHabitat"></div>
                <i class="icon-white icon-remove" data-bind="click: $root.removeHabitat"
                   style="cursor: pointer"></i>
            </div>

            <div data-bind="select: $root.isHabitatSelected($data)">
                <input data-bind="value: displayName, event:{blur: $root.clearHabitat}"/>
            </div>
        </div>
    </div>

    <div data-bind="sortable: {data:habitats, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default" style="position: relative">
            <div class="panel-heading">
                <div data-bind="text: displayName" class="pull-left"></div>

                <div class="pull-right">
                    <div data-bind="text: name" style="display:none"></div>
                    <i class="icon-download-alt" title="Download plot's raw data" data-bind="click: $root.downloadSummaryCsv, visible: !loading()"></i>
                    <i class="icon-info-sign" title="Show more information" onclick="showInfo(this)"
                       data-bind="attr: { id: id_metadata }, click: $root.showInfo"></i>
                </div>

                <div>&nbsp;</div>
            </div>

            <div style="display:none" data-bind="attr: { name: id_metadata }">
                <table class="table table-bordered"><tbody><tr><th colspan="2">Layer metadata</th></tr>
                <tr><td>Name:</td><td><div data-bind="text: displayName"></div></td></tr>
                <tr><td>Description:</td><td><div data-bind="text: mdDescription"></div></td></tr>
                <tr data-bind="visible: mdNotes().length > 0"><td>Notes:</td><td><div data-bind="text: mdNotes"></div>
                </td></tr>
                <tr data-bind="visible: mdMin().length > 0"><td>Min:</td><td><div data-bind="text: mdMin"></div></td>
                </tr>
                <tr data-bind="visible: mdMax().length > 0"><td>Max:</td><td><div data-bind="text: mdMax"></div></td>
                </tr>
                <tr data-bind="visible: mdUnits().length > 0"><td>Units:</td><td><div data-bind="text: mdUnits"></div>
                </td></tr>
                <tr><td>More info:</td><td><a target="_blank"
                                              data-bind="attr: { href: mdUrl, title: mdUrl }">more information</a></td>
                </tr>
                </tbody></table>
            </div>

            <div class="panel-body">
                <div data-bind="attr:{id: id}, addHabitatChart: !$root.isHabitatSelected($data)"
                     style="width: 100%; height: 200px;"></div>
            </div>

            <g:render template="plotDataSummary"></g:render>
        </div>
    </div>
</div>

<div class="alert top-buffer">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <h4>Note</h4>

    <p>You can click on <i>Plot profile</i> button to find out the environmental
    characteristics like precipitation, temperature etc. of a clade. You can pick the environmental parameter
    from the drop down list, or filter the list by typing into the input box.</p>
</div>

<g:render template="occurrenceDownloadPopup"
          model="[dialogId: 'plotOccurrenceDownloadModal', clickAction: '$root.downloadOccurrenceData', viewModel: '$root.downloadViewModel']"></g:render>
