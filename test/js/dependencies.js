/**
 * Created by Temi Varghese on 30/07/15.
 */
var phylolink = {};
phylolink.pj = null;

(function initObjects() {
    injectHTMl();
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
        hData: {
            id: "testId",
            title: "blaah",
            edit: false
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
        }
    });

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
        character: {'acacia prainii': {'a': [1]}}
    });

    phylolink.filter = new Filter({
        q: '',
        fq: {},
        pj: phylolink.pj,
        fqVariable: 'species'
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
        }
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
        }
    });

    phylolink.records = new Records({
        id: "recordsForm",
        template: '<div >     <div class="bs-callout" id="uploadRecords" style="position: relative">        <h4 style="cursor:pointer" id="uploadRecordsTitle" data-bind="click: onToggleForm">        <i class="icon icon-chevron-down"></i> <a>Upload my occurrence records</a>    </h4>    <div id="minimizeUploadRecords" style="display:none">        <form id="csvFormRecords" class="form-horizontal" enctype="multipart/form-data" >        <i>You need modern browser such as Google Chrome 40 or Safari 8</i>    <div class="control-group">        <label class="control-label">Choose a CSV file*:</label>    <div class="controls">        <input id="csvFileRecords" type="file" name="file" value="Upload" accept=".csv" required    data-bind="event:{change: onNewFile}, attr:{disabled: formDisabled}"/>        <label><a id="csvSampleRecords" target="_blank" data-bind="attr:{href: sampleFile}">Download sample CSV file from here.</a></label>    </div>    </div>    <div class="control-group">        <label class="control-label">Title*:</label>    <div class="controls">        <input name="title" type="text" id="titleRecords" data-bind="value: title, attr:{disabled: formDisabled}" placeholder="My occurrence records" required>    </div>    </div>    <div class="control-group">        <label class="control-label" >Column with species name or OTU number*:</label>    <div class="controls">        <select name="scientificName" data-bind="options:headers,value:selectedValue,optionsCaption:"Choose..", attr:{disabled: formDisabled}" required></select>    </div>    </div>    <div class="control-group">        <div class="controls">        <button id="uploadBtnRecords" class="btn btn-primary" data-bind="click: uploadFile, attr:{disabled: formDisabled}"><i class="icon icon-white icon-upload"></i> Upload my file</button>    <button id="resetBtnRecords" class="btn" data-bind="click: resetForm, attr:{disabled: formDisabled}">Clear form</button>    </div>    </div>    </form>    <div id="occurrenceUpload" class="well" data-bind="fadeVisible: progress()!=null">        <label>Uploading Data:</label>    <div class="progress progress-striped active">        <div class="bar" style="width: 0%;" data-bind="style:{width: progress() + "%"}"></div>        </div>        <div class="alert" data-bind="html: message, css:{ "alert-error": error(), "alert-success": !error()}, visible: !!message()">        </div>        </div>        <div id="sandboxUpload" class="well" data-bind="fadeVisible: indexingProgress() != null">        <label>Indexing Data:</label>    <div class="progress progress-striped active">        <div class="bar" style="width: 0%;" data-bind="style:{width: indexingProgress() + "%"}"></div>        </div>        <div id="uploadMessage" class="alert" data-bind="html: indexingMessage, attr:{class: "alert " + indexingClass()}">        </div>        </div>        </div>        </div>        </div>        <div id="recordsmain">        <div class="bs-callout" style="position: relative" id="pickFromListRecords">        <h4><a>Or, pick a records dataset from the available list:</a></h4>    <form id="sourceToolbarRecords" class="form-horizontal">        <div class="control-group">        <label class="control-label" for="">List of records available:</label>    <div class="controls">        <select id="sourceCharRecords" data-bind="options:lists,optionsText:"title",value:selectedValue,    optionsCaption:"Choose..", event:{change: drChanged}" required></select>    </div>    </div>    </form>    </div>    </div>',
        uploadUrl: null,
        indexingStatusUrl: null,
        sampleFile: null,
        dataresrouceInfoUrl: null,
        dataresourceListUrl: null,
        map: phylolink.map,
        pj: phylolink.pj,
        selectResourceOnInit: true,
        initResourceId: -1
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
    var html = '<div class="container-fluid">        <div class="row-fluid">        <div class="span12">        <ul class="breadcrumb">        <li><a href="${createLink(uri: " / ")}">Home</a> <span class="divider">/</span></li>    <li><a href="${createLink(controller: "    wizard    ", action: "    start    ")}">Start PhyloLink</a> <spanclass="divider">/</span></li>    <li><a href="${createLink(controller: "    wizard    ", action: "    myViz    ")}">My Visualisations</a></li>    </ul>    </div>    </div>    <div id="vizTitle"></div>        <div class="row-fluid">        <div class="span6">        <div id="info"></div>        </div>        <div role="tabpanel" id="tabs" class="span6">            <!-- Nav tabs -->        <ul class="nav nav-tabs" role="tablist">        <li id="charLi" role="presentation" class=""><a id="characterTab" href="#character" aria-controls="home"    role="tab"    data-toggle="tab">Character</a></li>    <li role="presentation"><a href="#map" aria-controls="profile" role="tab" data-toggle="tab"    id="mapTab">Map</a></li>    <li role="presentation"><a href="#habitat" aria-controls="profile" role="tab" data-toggle="tab"    id="habitatTab">Analysis</a></li>    <li role="presentation" class="active"><a href="#records" aria-controls="profile" role="tab"    data-toggle="tab"    id="recordsTab">Occurrences</a></li>    <li role="presentation"><a href="#help" aria-controls="profile" role="tab" data-toggle="tab"    id="helpTab">Help</a></li>    </ul>        <!-- Tab panes -->    <div class="tab-content" style="position: relative">        <div role="tabpanel" class="tab-pane" id="character"></div>        <div role="tabpanel" class="tab-pane" id="map"></div>        <div role="tabpanel" class="tab-pane" id="habitat"></div>        <div role="tabpanel" class="tab-pane active" id="records">        <div id="recordsForm"></div>        </div>        <div role="tabpanel" class="tab-pane" id="help">          &nbsp;<table class="table table-bordered">        <tbody>        <th>        How to use phylolink?</th>    <tr>    <td>Speaker</td><td>Joseph Miller</td>    </tr>    </tbody>    </table>    </div>    </div>    </div>    </div>    </div>';
    $(document.body).html(html)
}