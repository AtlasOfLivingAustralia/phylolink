/**
 * Created by Temi Varghese on 30/07/15.
 */
var phylolink = {}, pj;
phylolink.pj = null;

(function initObjects() {
    injectHTMl();
    var config = {
        spUrl: {
            url: undefined
        }
    }

    phylolink.pj = new PJ({
        width: 400,
        height: 700,
        codeBase: "../..",
        dataType: "json",
        bootstrap: 2,
        tree: "(a,b);",
        id: "info",
        format: "newick",
        heading: "vizTitle",
        settingsId:'pjSettings',
        trimmingId:'pjTrimming',
        hData:{
            id:undefined,
            title: '',
            edit: true,
            selectedDr: ko.observable(''),
            selectedClade: ko.observable(''),
            selectedCladeNumber: ko.observable(-1)
        },
        titleUrl: "",
        edit: false,
        runSaveQuery: false,
        saveQuery: {
            url: "",
            type: "POST",
            dataType: "JSON",
            data: {
                speciesList: null,
                dataLocationType: null, // "ala" or "sandbox"
                instanceUrl: null, // "http://sandbox.ala.org.au",
                drid: null // drt121
            }
        },
        pjSettings: null,
        settingsUrl: null,
        listToolBaseURL: 'https://lists.ala.org.au'
    });
    pj = phylolink.pj;

    phylolink.character = new Character({
        id: "character",
        tabId: "characterTab",
        pj: phylolink.pj,
        url: null,
        dataType: "jsonp",
        height: 700,
        headerHeight: 55,
        initCharacters: null,
        bootstrap: 2,
        sampleCSV: null,
        doSync: false,
        syncData: {
            id: 1
        },
        syncUrl: null,
        charactersList: {
            url: null,
            type: "GET",
            dataType: "JSON"
        },
        edit: false,
        upload: {
            url: null,
            type: "POST"
        },
        charOnRequest: null,
        charOnRequestBaseUrl: null,
        charOnRequestParams: null,
        charOnRequestListKeys: null,
        character: {'acacia prainii': {'a': [1]}},
        treeId: 1
    });

    phylolink.filter = new Filter({
        q: '',
        fq: {},
        pj: phylolink.pj,
        fqVariable: 'species'
    });

    phylolink.records = new Records({
        id: "recordsForm",
        template: $('#templateOccurrence').html(),
        uploadUrl: null,
        indexingStatusUrl: null,
        sampleFile: null,
        dataresrouceInfoUrl: null,
        dataresourceListUrl: null,
        pj: phylolink.pj,
        selectResourceOnInit: true,
        initResourceId: -1
    });

    phylolink.map = new Map({
        id: "map",
        tabId: "mapTab",
        pj: phylolink.pj,
        filter: phylolink.filter,
        height: 650,
        width: 400,
        layer: null,
        query: null,
        filterFieldName: "REGNO_s",
        source: null,
        character: phylolink.character,
        colorByCharacters: true,
        legend: {
            proxy: true,
            proxyUrl: null,
            baseUrl: "",
            dataType: "jsonp",
            urlParams: {
                cm: null,
                type: "application/json",
                fq: null,
                q: null,
                source: null,
                instanceUrl: null
            },
            icon: null,
            defaultValue: [{
                red: 223,
                green: 74,
                blue: 33,
                hex: "df4a21",
                name: "All records"
            }]
        },
        env: {
            "colormode": null,
            "name": "circle",
            "size": 4,
            "opacity": 0.8,
            "color": "df4a21"
        },
        colorBy: {
            url: null,
            drid: null,
            defaultValue: "taxon_name"
        },
        downloadReasonsUrl: undefined,
        spUrl: {
            baseUrl: '',
            url: ko.observable('')
        },
        records: phylolink.records
    });

    phylolink.habitat = new Habitat({
        id: "habitat",
        tabId: "habitatTab",
        pj: phylolink.pj,
        doSync: false,
        syncData: {
            id: 1
        },
        listUrl: null,
        height: 700,
        syncUrl: null,
        initialState: null,
        graph: {
            url: null,
            type: "GET",
            dataType: "JSON",
            xAxisContextual: "Habitat states",
            xAxisEnvironmental: "values",
            yAxis: "Occurrence count"
        },
        saveQuery: {
            url: null,
            type: "POST",
            dataType: "JSONP"
        },
        downloadSummaryUrl: undefined,
        biocacheOccurrenceDownload: undefined,
        downloadReasonsUrl: undefined,
        records: phylolink.records,
        tabId: 'tab'
    });
})();

function inject() {
    var args = arguments, type, injections = [];
    var func;
    for (var i = 0; i < args.length; i++) {
        type = typeof args[i];
        switch (type) {
            case "string":
                injections.push(phylolink[args[i]]);
                break;
            case "function":
                func = args[i];
                break;
        }
    }
    return function () {
        func.apply(this, injections);
    }
};

function injectHTMl() {
    var html = '<div class="container-fluid">    <div class="row-fluid">        <div class="span12">            <ul class="breadcrumb">                <li><a href="/phylolink/">Home</a> <span class="divider">/</span></li>                <li><a href="/phylolink/wizard/start">Start PhyloLink</a> <span                        class="divider">/</span></li>                <li><a href="/phylolink/wizard/myViz">My Visualisations</a></li>            </ul>        </div>    </div>    <div class="row-fluid">        <div id="vizTitle"><div class=\'pull-left\' data-bind=\' click: $root.select\' style=\'cursor:pointer;\'><h1 style=\'color:#C44D34;display: inline-block\'                                                                  title=\'Click title to edit it\'                                                                  data-bind=\'text: title\'></h1>&nbsp;<i        data-bind=\'visible: edit\' title=\'Click title to edit it\' class=\'icon icon-pencil\'></i></div><div data-bind=\'visible: $root.clicked()\' style=\'padding-bottom: 10px\'>    New title for this visualisation: <input data-bind=\'value: title, event:{blur:$root.clearClick, change:$root.sync}\'></div><div class=\'pull-right alert selection-info text-right\' role=\'alert\' id=\'selectionInfo\'>    <table><tr><td data-bind=\'text: selectedDr\'></td></tr>        <tr><td data-bind=\'text: selectedClade\'></td></tr>        <tr><td data-bind=\'visible: selectedCladeNumber() >= 1024\'                class=\'alert-error\'>            limited to the first 1024 taxa</td></tr>    </table></div></div>    </div>    <div class="row-fluid">        <div class="span6">            <div id="info"></div>            <div id="pjSettings" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="pjSettingLabel" aria-hidden="true">  <div class="modal-header">    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>    <h3 id="pjSettingLabel">Phylojive Settings</h3>  </div>  <div class="modal-body">    <label class="checkbox">      <input type="checkbox" data-bind="checked: alignName, click: $root.alignPJ"> Align names in vertical line    </label>  </div>  <div class="modal-footer">    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>  </div></div>            <div id="pjTrimming" class="modal modal-wide hide fade" tabindex="-1" role="dialog" aria-labelledby="pjTrimmingLabel"     aria-hidden="true">    <div class="modal-header">        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>        <h3 id="pjTrimmingLabel">Trimming Options</h3>    </div>    <div class="modal-body">        <div class="form-horizontal">            <div class="control-group">                <label for="trimToInclude" class="control-label">Trim the tree to </label>                <div class="controls">                    <label><input id="trimToInclude" type="radio" name="trimToInclude" value="true" data-bind="checked: trimToInclude" style="vertical-align: baseline"/>&nbsp;Include</label>                    <label><input id="trimToExclude" type="radio" name="trimToInclude" value="false" data-bind="checked: trimToInclude" style="vertical-align: baseline"/>&nbsp;Exclude</label>                </div>            </div>            <div class="control-group">                <label for="trimOption" class="control-label">species from</label>                <div class="controls">                    <select id="trimOption"                            data-bind="options:trimOptions, optionsText:\'displayName\', optionsCaption:\'Choose...\', value:trimOption"></select>                </div>            </div>            <div class="control-group" data-bind="visible:trimOption() == TRIM_LIST">                <label for="trimByList" class="control-label">Select a list</label>                <div class="controls">                    <input id="trimByList" type="text" class="input-xlarge" data-bind="value:trimData"/>                </div>            </div>        </div>    </div>    <div class="modal-footer">        <button class="btn btn-primary" data-bind="click: $root.applyTrimOptions" aria-hidden="true">Apply</button>        <button class="btn btn-default" data-bind="click: $root.clearTrimOptions" aria-hidden="true">Clear Trimming</button>        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>    </div></div>        </div>        <div role="tabpanel" id="tabs" class="span6">            <!-- Nav tabs -->            <ul class="nav nav-tabs" role="tablist">                <li id="charLi" role="presentation" class=""><a id="characterTab" href="#character" aria-controls="home"                                                                role="tab"                                                                data-toggle="tab">Character</a></li>                <li role="presentation" class="active"><a href="#mapTabContent" aria-controls="profile" role="tab" data-toggle="tab"                                           id="mapTab">Map</a></li>                <li role="presentation"><a href="#habitat" aria-controls="profile" role="tab" data-toggle="tab"                                           id="habitatTab">Analysis</a></li>                <li role="presentation" ><a href="#records" aria-controls="profile" role="tab"                                                          data-toggle="tab"                                                          id="recordsTab">Occurrences</a></li>                <li role="presentation"><a href="#metadata" aria-controls="profile" role="tab" data-toggle="tab"                                           id="metadataTab">Metadata</a></li>                <li role="presentation"><a href="#help" aria-controls="profile" role="tab" data-toggle="tab"                                           id="helpTab">Help</a></li>            </ul>            <!-- Tab panes -->            <div class="tab-content" style="position: relative">                <div role="tabpanel" class="tab-pane" id="character">                    <div >    <div class="bs-callout" id="uploadCharacters" style="position: relative">        <h4 style="cursor:pointer" id="uploadCharactersTitle" data-bind="click: onClick"><i class="icon icon-chevron-down"></i> <a>Upload your character data</a></h4>        <div id="minimizeUpload" style="display:none">            <div id="csvFormUnavailable" class="alert-error"><i>Login to enable character upload.</i></div>            <form id="csvForm" class="form-horizontal" enctype="multipart/form-data">                <i>You need modern browser such as Google Chrome 40 or Safari 8</i>                <div class="control-group">                    <label class="control-label">Choose a CSV file*:</label>                    <div class="controls">                        <input id="csvFile" type="file" name="file" value="Upload" accept=".csv" required/>                        <label><a data-bind="attr{href:sampleCSV}" target="_blank">Download sample CSV file from here.</a></label    >                    </div>                </div>                <div class="control-group">                    <label class="control-label" for="inputPassword">Title*:</label>                    <div class="controls">                        <input type="text" id="title" data-bind="value: title" placeholder="My acacia characters" required>                    </div>                </div>                <div class="control-group">                    <label class="control-label" for="inputPassword">Column with scientific name*:</label>                    <div class="controls">                        <select data-bind="options:headers,optionsText:\'displayname\',value:selectedValue,optionsCaption:\'Choose..\'" required></select>                    </div>                </div>                <div class="control-group">                    <div class="controls">                        <button id="uploadBtn" class="btn btn-small btn-primary">Upload</button>                    </div>                </div>            </form>            <div class="alert" id="uploadMessage" data-bind="visible:message">                            <div data-bind="text:message"></div></div></div>    </div></div><div id="charactermain">    <div class="bs-callout" style="position: relative" id="pickFromList">        <h4><a>Or, pick a character dataset from the available list:</a></h4>        <form id="sourceToolbar" class="form-horizontal">            <div class="control-group">                <label class="control-label" for="">List of characters available:</label>                <div class="controls">                    <select id="sourceChar" data-bind="options:lists,optionsText:\'title\',value:list,optionsCaption:\'Choose..\', event:{change:loadNewCharacters}" required></select>                </div>            </div>            <div class="control-group">                <div class="controls">                    <a data-bind="attr:{href: list() ? list().listurl : null}, visible: list()" target="_blank"><i class="icon icon-globe"></i> View in List Tool</a>                </div>            </div>        </form>    </div>    <div class="btn btn-xs btn-primary top-buffer offset4" data-bind="click: addCharacter, visible:list(), attr:{disabled:listLoading()}"><i class="icon-white icon-plus-sign"></i> Add Character to Tree</div>    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">        <div class="item top-buffer" title="You can drag or edit this item">            <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">                <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>                <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>                <i class="icon-white icon-remove" data-bind="click: $root.removeCharacter"                    style="cursor: pointer"></i>            </div>            <div data-bind="visibleAndSelect: $root.isCharacterSelected($data)">                <input data-bind="value: name, event: { blur: $root.clearCharacter }" />            </div>        </div>    </div>    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">        <div class="top-buffer panel panel-default">            <div class="panel-heading" data-bind="text: name"></div>            <div class="panel-body" >                <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>            </div>        </div>    </div></div><div class="alert top-buffer">    <button type="button" class="close" data-dismiss="alert">&times;</button>    <h4>Note</h4><p>First, select a character dataset from the given list, or upload your character data. Then click on <i>Add Character to Tree</i>button. Tree branch color is determined by the first character on the list.To color the tree using a character either drag that character to the top of the list, or edit the first character by clicking on that character.</p></div>                </div>                <div role="tabpanel" class="tab-pane active" id="mapTabContent">                    <div id="map"></div>                    <div id="mapControls">                        <div class="text-right">                            <a id="spLink" class="btn btn-link" data-bind="attr:{href:spUrl.url}" target="_blank" ><i class="fa fa-external-link"></i>&nbsp;Open in Spatial Portal</a>                            <a id="downloadMapDataLink" class="btn btn-link" data-toggle="modal" href="#mapOccurrenceDownloadModal"><i class="fa fa-download"></i>&nbsp;Download occurrence data</a>                        </div>                        <div id="mapOccurrenceDownloadModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="Occurrence Downloads" aria-hidden="true">    <div class="modal-header">        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>        <h3 id="myModalLabel">Download occurrence records</h3>    </div>    <div class="modal-body">        <p>By downloading this content you are agreeing to use it in accordance with the Atlas of Living Australia <a href="http://www.ala.org.au/about-the-atlas/terms-of-use/#TOUusingcontent" target="_blank">Terms of Use</a> and any Data Provider Terms associated with the data download.</p>        <p>Please provide the following details before downloading (* required):</p>        <div class="form-horizontal">            <div class="control-group">                <label for="email" class="control-label">Email</label>                <div class="controls">                    <input type="text" id="email" data-bind="value: $root.downloadViewModel.email">                </div>            </div>            <div class="control-group">                <label for="reason" class="control-label">Download reason *</label>                <div class="controls">                    <select id="reason" data-bind="options: $root.downloadViewModel.downloadReasons, optionsText: \'displayName\', optionsCaption: \'Choose...\', value: $root.downloadViewModel.reason"></select>                </div>            </div>        </div>    </div>    <div class="modal-footer">        <button class="btn btn-primary occurrenceDownloadButton" data-bind="click: $root.downloadMapData, disable: !$root.downloadViewModel.reason()">Download</button>        <button id="closeDownloadModal" class="btn closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>    </div></div>                    </div>                </div>                <div role="tabpanel" class="tab-pane" id="habitat">                    <style>.ui-autocomplete {    max-height: 200px;    overflow-y: auto; /* prevent horizontal scrollbar */    overflow-x: hidden; /* add padding to account for vertical scrollbar */    z-index: 1000 !important;}.ellipselabel {    white-space: nowrap;    max-width: 220px;    overflow: hidden; /* "overflow" value must be different from "visible" */    text-overflow: ellipsis;}</style><div id="habitatMain">    <div class="bs-callout">        <p>Display histograms of information like precipitation, temperature, State etc. of occurrences in the selected clade.</p>        <div class="btn btn-primary" data-bind="click:addHabitat"><i class="icon-white icon-plus-sign"></i>Add histogram</div>        <div id="downloadPlotDataLink" class="btn btn-default" data-toggle="modal" href="#plotOccurrenceDownloadModal"             data-bind="visible: habitats().length > 0"><i class="fa fa-download"></i>&nbsp;Download raw data</div>    </div>    <div data-bind="sortable:{data:habitats, afterMove: $root.onMove, options: { cancel: \'.no-sort\'}}">        <div class="item top-buffer">            <div class="label label-default" data-bind="visible: !$root.isHabitatSelected($data)">                <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>                <div class="ellipselabel" style="color: #ffffff;display:inline-block;cursor: pointer" href="#"                     data-bind="text: displayName, click: $root.selectedHabitat"></div>                <i class="icon-white icon-remove" data-bind="click: $root.removeHabitat"                   style="cursor: pointer"></i>            </div>            <div class="no-sort panel panel-default" data-bind="select: $root.isHabitatSelected($data), visible: $root.isHabitatSelected($data)">                <button type="button" class="close" data-bind="click: $root.clearHabitat">&times;</button>                <div><input class=\'pull-left\' data-bind="value: displayName" id="layerCombobox"/><div>&nbsp;Search for a layer by name or browse the tree.</div></div>                <div>&nbsp;</div>                <div id=\'jqxTree\'></div>            </div>        </div>    </div>    <div data-bind="sortable: {data:habitats, afterMove: $root.onMove}">        <div class="top-buffer panel panel-default" style="position: relative">            <div class="panel-heading">                <div data-bind="text: displayName" class="pull-left"></div>                <div class="pull-right">                    <div data-bind="text: name" style="display:none"></div>                    <i class="icon-download-alt" title="Download plot\'s raw data" data-bind="click: $root.downloadSummaryCsv, visible: !loading()"></i>                    <i class="icon-info-sign" title="Show more information"                       data-bind="attr: { id: id_metadata }, click: $root.showInfo"></i>                </div>                <div>&nbsp;</div>            </div>            <div style="display:none" data-bind="attr: { name: id_metadata }">                <table class="table table-bordered"><tbody><tr><th colspan="2">Layer metadata</th></tr>                <tr><td>Name:</td><td><div data-bind="text: displayName"></div></td></tr>                <tr><td>Description:</td><td><div data-bind="text: mdDescription"></div></td></tr>                <tr data-bind="visible: mdNotes().length > 0"><td>Notes:</td><td><div data-bind="text: mdNotes"></div>                </td></tr>                <tr data-bind="visible: mdMin().length > 0"><td>Min:</td><td><div data-bind="text: mdMin"></div></td>                </tr>                <tr data-bind="visible: mdMax().length > 0"><td>Max:</td><td><div data-bind="text: mdMax"></div></td>                </tr>                <tr data-bind="visible: mdUnits().length > 0"><td>Units:</td><td><div data-bind="text: mdUnits"></div>                </td></tr>                <tr><td>More info:</td><td><a target="_blank"                                              data-bind="attr: { href: mdUrl, title: mdUrl }">more information</a></td>                </tr>                </tbody></table>            </div>            <div class="panel-body">                <div data-bind="attr:{id: id}, addHabitatChart: !$root.isHabitatSelected($data)"                     style="width: 100%; height: 200px;"></div>            </div>            <a data-bind="click: function(data, event) { $root.togglePanel(data, event, $index() + \'StatisticSummary\', $index() + \'DataSummaryText\') }" href="#" type="button" class="btn btn-link small" data-toggle="collapse">    <span data-bind="attr: {id: $index() + \'DataSummaryText\'}"  class="fa fa-angle-double-down">&nbsp;View data summary</span></a><div data-bind="attr: {id: $index() + \'StatisticSummary\'}" class="hide">    <table class="statistic-summary">        <tbody>        <tr>            <th width="15%">Sample Size</th>            <td><span data-bind="text: sampleSize"></span></td>            <th data-bind="visible: numeric">Min.</th>            <td data-bind="visible: numeric"><span data-bind="text: min"></span></td>            <th data-bind="visible: numeric">Max.</th>            <td data-bind="visible: numeric"><span data-bind="text: max"></span></td>            <th data-bind="visible: numeric">Mean</th>            <td data-bind="visible: numeric"><span data-bind="text: mean"></span></td>            <th data-bind="visible: numeric">Median</th>            <td data-bind="visible: numeric"><span data-bind="text: median"></span></td>            <th data-bind="visible: numeric">Std. Dev.</th>            <td data-bind="visible: numeric"><span data-bind="text: standardDeviation"></span></td>        </tr>        <tr data-bind="visible: leastFrequent">            <th width="15%">Least frequent <a class="fa fa-question-circle" title="Only the first 5 items will be shown here if multiple items share the same frequency"></a></th>            <td width="85%" colspan="11"><span class="strong">Frequency:</span> <span data-bind="text: leastFrequentCount"></span>; <span class="strong">Item(s):</span> <span data-bind="text: leastFrequent"></span></td>        </tr>        <tr data-bind="visible: mostFrequent">            <th width="15%">Most frequent <a class="fa fa-question-circle" title="Only the first 5 items will be shown here if multiple items share the same frequency"></a></th>            <td width="85%" colspan="11"><span class="strong">Frequency: </span><span data-bind="text: mostFrequentCount"></span>; <span class="strong">Item(s):</span> <span data-bind="text: mostFrequent"></span></td>        </tr>        </tbody>    </table></div>        </div>    </div></div><div id="plotOccurrenceDownloadModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="Occurrence Downloads" aria-hidden="true">    <div class="modal-header">        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>        <h3 id="myModalLabel">Download occurrence records</h3>    </div>    <div class="modal-body">        <p>By downloading this content you are agreeing to use it in accordance with the Atlas of Living Australia <a href="http://www.ala.org.au/about-the-atlas/terms-of-use/#TOUusingcontent" target="_blank">Terms of Use</a> and any Data Provider Terms associated with the data download.</p>        <p>Please provide the following details before downloading (* required):</p>        <div class="form-horizontal">            <div class="control-group">                <label for="email" class="control-label">Email</label>                <div class="controls">                    <input type="text" id="email" data-bind="value: $root.downloadViewModel.email">                </div>            </div>            <div class="control-group">                <label for="reason" class="control-label">Download reason *</label>                <div class="controls">                    <select id="reason" data-bind="options: $root.downloadViewModel.downloadReasons, optionsText: \'displayName\', optionsCaption: \'Choose...\', value: $root.downloadViewModel.reason"></select>                </div>            </div>        </div>    </div>    <div class="modal-footer">        <button class="btn btn-primary occurrenceDownloadButton" data-bind="click: $root.downloadOccurrenceData, disable: !$root.downloadViewModel.reason()">Download</button>        <button id="closeDownloadModal" class="btn closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>    </div></div>                </div>                <div role="tabpanel" class="tab-pane" id="records">                    <div id="recordsForm"></div>                </div>                <div role="tabpanel" class="tab-pane" id="metadata">                    <div>    <table class="table table-bordered">        <tbody>        <tr>            <th colspan="2">Tree metadata</th>        </tr>        <tr>            <td>Title:</td>            <td>Acacia &ndash; Miller et al 2012</td>        </tr>                <tr>            <td>Reference:</td>            <td>Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and Gonz&aacute;lez-Orozco, C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. Diversity and Distributions, 17: 848&ndash;860. doi: 10.1111/j.1472-4642.2011.00780.x</td>        </tr>                        <tr>            <td>Year:</td>            <td>2011</td>        </tr>                            <tr>                <td>Doi:</td>                <td>http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full</td>            </tr>                        </tbody>    </table>    <table class="table table-bordered">        <tbody>        <tr>            <th colspan="2">Actions</th>        </tr>        <tr>            <td>                Download tree:            </td>            <td>                <a class="btn" href="/phylolink/tree/download?id=2"><i class="icon icon-download"></i> Download</a>            </td>        </tr>        <tr style="display:none">            <td>                Link tree with data:            </td>            <td>                <a class="btn btn-primary" href="/phylolink/wizard/visualize?id=2">                    <i class="icon icon-arrow-right"></i> Visualise with Phylolink</a>            </td>        </tr>        </tbody>    </table></div>                </div>                <div role="tabpanel" class="tab-pane" id="help">                    <iframe width="100%" height="315"                            src="https://www.youtube.com/embed/_fN3Nn159Tw" frameborder="0" allowfullscreen>                    </iframe>                    &nbsp;                    <table class="table table-bordered">                        <tbody>                        <th>                            How to use phylolink?                        </th>                        <tr>                            <td>Speaker</td><td>Joseph Miller</td>                        </tr>                        </tbody>                    </table>                </div>            </div>        </div>    </div></div><script id="templateOccurrence" type="text/html"><div >    <div class="bs-callout" id="uploadRecords" style="position: relative">        <h4 style="cursor:pointer" id="uploadRecordsTitle" data-bind="click: onToggleForm">            <i class="icon icon-chevron-down"></i> <a>Upload my occurrence records</a>        </h4>        <div id="minimizeUploadRecords" style="display:none">            <div id="csvFormRecordsUnavailable" class="alert-error"><i>Login to enable character upload.</i></div>            <form id="csvFormRecords" class="form-horizontal" enctype="multipart/form-data" >                <i>You need modern browser such as Google Chrome 40 or Safari 8</i>                <div class="control-group">                    <label class="control-label">Choose a CSV file*:</label>                    <div class="controls">                        <input id="csvFileRecords" type="file" name="file" value="Upload" accept=".csv" required                               data-bind="event:{change: onNewFile}, attr:{disabled: formDisabled}"/>                        <label><a id="csvSampleRecords" target="_blank" data-bind="attr:{href: sampleFile}">Download sample CSV file from here.</a></label>                    </div>                </div>                <div class="control-group">                    <label class="control-label">Title*:</label>                    <div class="controls">                        <input name="title" type="text" id="titleRecords" data-bind="value: title, attr:{disabled: formDisabled}" placeholder="My occurrence records" required                               onfocus="utils.clearPlaceholder(this)">                    </div>                </div>                <div class="control-group">                    <label class="control-label" >Column with species name or OTU number*:</label>                    <div class="controls">                        <select name="scientificName" data-bind="options:headers,value:selectedValue,optionsCaption:\'Choose..\', attr:{disabled: formDisabled}" required></select>                    </div>                </div>                <div class="control-group">                    <div class="controls">                        <button id="uploadBtnRecords" class="btn btn-primary" data-bind="click: uploadFile, attr:{disabled: formDisabled}"><i class="icon icon-white icon-upload"></i> Upload my file</button>                        <button id="resetBtnRecords" class="btn" data-bind="click: resetForm, attr:{disabled: formDisabled}">Clear form</button>                    </div>                </div>            </form>            <div id="occurrenceUpload" class="well" data-bind="fadeVisible: progress()!=undefined">                <label>Uploading Data:</label>                <div class="progress progress-striped active">                    <div class="bar" style="width: 0%;" data-bind="style:{width: progress() + \'%\'}"></div>                </div>                <div class="alert" data-bind="html: message, css:{ \'alert-error\': error(), \'alert-success\': !error()}, visible: !!message()">                </div>            </div>            <div id="sandboxUpload" class="well" data-bind="fadeVisible: indexingProgress() != undefined">                <label>Indexing Data:</label>                <div class="progress progress-striped active">                    <div class="bar" style="width: 0%;" data-bind="style:{width: indexingProgress() + \'%\'}"></div>                </div>                <div id="uploadMessage" class="alert" data-bind="html: indexingMessage, attr:{class: \'alert \' + indexingClass()}">                </div>            </div>        </div>    </div></div><div id="recordsmain">    <div class="bs-callout" style="position: relative" id="pickFromListRecords">        <div class="pull-left" ><h4><a>Or, pick a records dataset from the available list:</a></h4></div>        <div class="pull-right">            <form id="sourceToolbarRecords" >                <div class="control-group">                    <!--label class="control-label" for="">List of records available:</label-->                    <div class="controls">                        <select id="sourceCharRecords" data-bind="options:lists,optionsText:\'title\',value:selectedValue,                        optionsCaption:\'Choose..\', event:{change: drChanged}" required></select>                    </div>                </div>            </form>        </div>        <div >&nbsp;</div>    </div></div></script>';
    $(document.body).html(html)
}