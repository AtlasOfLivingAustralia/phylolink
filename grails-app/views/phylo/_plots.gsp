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

    <div style="margin-top:10px;" class="btn btn-primary pull-right" data-bind="click:addHabitat"><i class="glyphicon glyphicon-white glyphicon glyphicon-plus-sign"></i> Add new histogram</div>
    <div>
        <p style="padding-top:10px;">Display histograms of information like precipitation, temperature, State etc. of occurrences in the selected clade.</p>
        %{--<div class="btn btn-primary pull-right" data-bind="click:addHabitat"><i class="glyphicon glyphicon-white glyphicon glyphicon-plus-sign"></i>Add new histogram</div>--}%
    </div>

    %{--<h4 data-bind="visible: habitats().length > 0">Selected histograms (drag to changeorder)</h4>--}%
    <div data-bind="sortable:{data:habitats, afterMove: $root.onMove, options: { cancel: '.no-sort'}}" style="margin-top:30px;">
        <div class="item top-buffer">
            <div class=" hide  label label-default" data-bind="visible: !$root.isHabitatSelected($data)">
                <i class="glyphicon glyphicon-white glyphicon glyphicon-resize-vertical" aria-hidden="true" style="cursor: move"></i>

                <div class="label label-default" style="color: #ffffff;display:inline-block;cursor: pointer" href="#"
                     data-bind="text: displayName, click: $root.selectedHabitat"></div>
                <i class="glyphicon glyphicon-remove" data-bind="click: $root.removeHabitat"
                   style="cursor: pointer"></i>
            </div>

            <div class="no-sort panel panel-default" data-bind="select: $root.isHabitatSelected($data), visible: $root.isHabitatSelected($data)">

                %{--<button type="button" data-bind="click: $root.clearHabitat">--}%
                    %{--<i class="glyphicon glyphicon-remove"> </i>--}%
                %{--</button>--}%

                <div class="hide">
                    <input class='pull-left' data-bind="value: displayName" id="layerCombobox"/>
                    <div>&nbsp;&nbsp;Search for a layer by name or browse the tree.</div>
                </div>

                <div class="panel-heading">
                    Select a layer below to add a histogram
                    <span type="button" class="pull-right" data-bind="click: $root.clearHabitat">
                        <i class="glyphicon glyphicon-remove"> </i>
                    </span>
                </div>
                <div class="panel-body">
                    <div id='jqxTree'></div>
                </div>
            </div>
        </div>
    </div>

    <div data-bind="sortable: {data:habitats, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default" style="position: relative">
            <div class="panel-heading">
                <div data-bind="text: displayName" class="pull-left"></div>

                <div class="pull-right">
                    <div data-bind="text: name" style="display:none"></div>

                    <i class="glyphicon glyphicon-download-alt" title="Download plot's raw data" data-bind="click: $root.downloadSummaryCsv, visible: !loading()"></i>
                    <i class="glyphicon glyphicon-info-sign" title="Show more information"
                       data-bind="attr: { id: id_metadata }, click: $root.showInfo"></i>
                    <i class="glyphicon glyphicon-remove" data-bind="click: $root.removeHabitat"
                       style="cursor: pointer"></i>
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

            <div class="panel-body" style="padding:0px;">
                <div data-bind="attr:{id: id}, addHabitatChart: !$root.isHabitatSelected($data)"
                     style="width: 100%; height: 200px; "></div>
            </div>

            <g:render template="plotDataSummary"></g:render>
        </div>
    </div>
</div>

<g:render template="occurrenceDownloadPopup"
          model="[dialogId: 'plotOccurrenceDownloadModal', clickAction: '$root.downloadOccurrenceData', viewModel: '$root.downloadViewModel']"></g:render>
