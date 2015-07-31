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

    <div class="text-right">
        <a id="downloadPlotDataLink" class="btn btn-link" data-toggle="modal" href="#plotOccurrenceDownloadModal" data-bind="visible: habitats().length > 0"><i class="fa fa-download"></i>&nbsp;Download source data</a>
    </div>

    <div data-bind="sortable: {data:habitats, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default" style="position: relative">
            <div class="panel-heading" data-bind="text: displayName"></div>
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

<g:render template="occurrenceDownloadPopup" model="[dialogId: 'plotOccurrenceDownloadModal', clickAction: '$root.downloadOccurrenceData', viewModel: '$root.downloadViewModel']"></g:render>
