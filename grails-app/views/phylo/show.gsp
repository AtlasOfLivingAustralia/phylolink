<%--
 Created by Temi Varghese on 20/01/15.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <r:require modules="application,leaflet,phylojive,character,map,contextmenu,records,appSpecific,jqxTree,select2,css"/>
    <r:require modules="bugherd"/>
</head>

<body>
%{--padding top since breadcrumb is hidden by the new nav bar--}%
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri: '/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a> <span
                        class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'myViz')}">My Visualisations</a></li>
            </ul>
        </div>
    </div>

    <div class="row-fluid">
        <div id="vizTitle"><g:render template="title"></g:render></div>
    </div>

    <div class="row-fluid">
        <div class="span6">
            <div id="info"></div>
            <g:render template="settings"></g:render>
            <g:render template="trimming"></g:render>
        </div>

        <div role="tabpanel" id="tabs" class="span6">

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li id="charLi" role="presentation" class=""><a id="characterTab" href="#character" aria-controls="home"
                                                                role="tab"
                                                                data-toggle="tab">Character</a></li>
                <li role="presentation" class="active"><a href="#mapTabContent" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="mapTab">Map</a></li>
                <li role="presentation"><a href="#habitat" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="habitatTab">Analysis</a></li>
                <li role="presentation" ><a href="#records" aria-controls="profile" role="tab"
                                                          data-toggle="tab"
                                                          id="recordsTab">Occurrences</a></li>
                <li role="presentation"><a href="#metadata" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="metadataTab">Metadata</a></li>
                <li role="presentation"><a href="#help" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="helpTab">Help</a></li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content" style="position: relative">
                <div role="tabpanel" class="tab-pane" id="character">
                    <g:render template="character"></g:render>
                </div>

                <div role="tabpanel" class="tab-pane active" id="mapTabContent">
                    <div id="map"></div>
                    <div id="mapControls">
                        <div class="text-right">
                            <a id="spLink" class="btn btn-link" data-bind="attr:{href:spUrl.url}" target="_blank" ><i class="fa fa-external-link"></i>&nbsp;Open in Spatial Portal</a>
                            <a id="downloadMapDataLink" class="btn btn-link" data-toggle="modal" href="#mapOccurrenceDownloadModal"><i class="fa fa-download"></i>&nbsp;Download occurrence data</a>
                        </div>

                        <g:render template="occurrenceDownloadPopup" model="[dialogId: 'mapOccurrenceDownloadModal', clickAction: '$root.downloadMapData', viewModel: '$root.downloadViewModel']"></g:render>
                    </div>
                </div>

                <div role="tabpanel" class="tab-pane" id="habitat">
                    <g:render template="plots"></g:render>
                </div>

                <div role="tabpanel" class="tab-pane" id="records">
                    <div id="recordsForm"></div>
                </div>

                <div role="tabpanel" class="tab-pane" id="metadata">
                    <g:render template="metadata"></g:render>
                </div>

                <div role="tabpanel" class="tab-pane" id="help">
                    <iframe width="100%" height="315"
                            src="https://www.youtube.com/embed/_fN3Nn159Tw" frameborder="0" allowfullscreen>
                    </iframe>
                    &nbsp;
                    <table class="table table-bordered">
                        <tbody>
                        <th>
                            How to use phylolink?
                        </th>
                        <tr>
                            <td>Speaker</td><td>Joseph Miller</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

<script id="templateOccurrence" type="text/html">
<g:render template="occurrence"></g:render>
</script>

<r:script disposition="defer">
    var config ={
        type:'ala',
        sandboxLayer: 'http://sandbox1.ala.org.au/ws/webportal/wms/reflect',
        biocacheLayer: 'http://biocache.ala.org.au/ws/webportal/wms/reflect',
        sandboxLegend: 'http://sandbox1.ala.org.au/ala-hub/occurrence/legend',
        biocacheLegend: 'http://biocache.ala.org.au/occurrence/legend',
        downloadReasonsUrl: 'http://logger.ala.org.au/service/logger/reasons',
        legendUrl: function(){
            switch (config.type){
                case 'sandbox': return config.sandboxLegend;
                case 'ala': return config.biocacheLegend;
            }
        },
        proxyUrl: '${createLink(controller: 'ala', action: 'jsonp')}',
        layer: function(){
            switch (config.type){
                case 'sandbox': return config.sandboxLayer;
                case 'ala': return config.biocacheLayer;
            }
        },
        treeUrl:"${createLink(controller: 'tree', action: 'getTree')}?id=${phyloInstance.studyid}&treeid=${phyloInstance.treeid}",
        format: "${tree.treeFormat}",
        initCharacters: <g:message message="${JSON.parse(phyloInstance.getCharacters() ?: '[]') as grails.converters.JSON}"/>,
        filterParams: {
            q: '',
            fq:{

            }
        },
        colorByUrl: '${createLink(controller: 'ala', action: 'facets')}',
        edit:${edit},
        id: ${phyloInstance.getId()},
        title:'<g:message message="${phyloInstance.getTitle().replace('\'', '\\\'')}"/>',
        titleUrl: '${createLink(controller: 'restrictedmethods', action: 'saveTitle')}',
        pjId: 'info',
        charOnRequest: true,
        charOnRequestListKeys:"${createLink(controller: 'characters', action: 'getKeys')}",
        charOnRequestBaseUrl:"${createLink(controller: 'characters', action: 'getCharJsonForKeys')}",
        charOnRequestParams:{
            drid:undefined,
            keys:undefined
        },
        spUrl: {
            baseUrl: '${grailsApplication.config.spatialPortalRoot}',
            url: ko.observable('${grailsApplication.config.spatialPortalRoot}')
        },
        chartWidth: $('#tabs').width() - 30
    }

    google.load("visualization", "1", {packages: ["corechart"]});

    var pj = new PJ({
        width: $('#'+config.pjId).width()-10,
        height: 700,
        codeBase: '../..',
        dataType:'json',
        bootstrap: 2,
        url: config.treeUrl,
        id: config.pjId ,
        format: config.format,
        heading:'vizTitle',
        settingsId:'pjSettings',
        trimmingId:'pjTrimming',
        hData:{
            id:config.id,
            title: config.title,
            edit: config.edit,
            selectedDr: ko.observable(''),
            selectedClade: ko.observable(''),
            selectedCladeNumber: ko.observable(-1)
        },
        titleUrl: config.titleUrl,
        edit: config.edit,
        runSaveQuery: false,
        saveQuery:{
            url: '${createLink(controller: 'ala', action: 'saveQuery')}',
            type: 'POST',
            dataType: 'JSON',
            data: {
                speciesList: undefined,
                dataLocationType: undefined, // 'ala' or 'sandbox'
                biocacheServiceUrl: undefined, // 'http://sandbox.ala.org.au',
                drid: undefined // drt121
            }
        },
        listToolBaseURL: "${grailsApplication.config.listToolBaseURL}"
    });

    var filter = new Filter($.extend(config.filterParams, {
        pj: pj,
        fqVariable:'species'
    }));

    var records = new Records({
        id: 'recordsForm',
        template: $('#templateOccurrence').html(),
        uploadUrl: '${createLink(controller: 'ala', action: 'uploadData')}?phyloId=${phyloInstance.id}',
        indexingStatusUrl: "${createLink(controller: 'sandbox', action: 'checkStatus')}",
        sampleFile: "${createLink(controller: 'artifacts', action: 'occurrenceRecords.csv')}",
        dataresrouceInfoUrl: "${createLink(controller: 'sandbox', action: 'dataresourceInfo')}?phyloId=${phyloInstance.id}",
        dataresourceListUrl: '${createLink(controller: 'ala', action: 'getRecordsList')}?phyloId=${phyloInstance.id}',
        pj: pj,
        selectResourceOnInit: true,
        initResourceId: <g:message message="${phyloInstance.getSource()?:-1}"/>,
        edit: ${edit},
        syncUrl: "${createLink(controller: 'phylo', action: 'saveSource')}",
        phyloId: config.id
    });

    var character = new Character({
        id: "character",
        tabId: 'characterTab',
        pj: pj,
        url: config.charUrl,
        dataType:'jsonp',
        height:700,
        headerHeight:55,
        initCharacters:config.initCharacters,
        bootstrap:2,
        sampleCSV:'${resource(dir: 'artifacts', file: 'traits.csv')}',
        doSync: ${edit},
        syncData: {
            id: ${phyloInstance.getId()}
    },
    syncUrl: "${createLink(controller: 'phylo', action: 'saveCharacters')}",
        charactersList : {
            url: '${createLink(controller: 'characters', action: 'list')}',
            type: 'GET',
            dataType: 'JSON'
        },
        edit: ${edit},
        upload: {
            url: "${createLink(controller: 'ala', action: 'saveAsList')}?phyloId=${phyloInstance.id}",
            type: 'POST'
        },
        charOnRequest: config.charOnRequest,
        charOnRequestBaseUrl: config.charOnRequestBaseUrl,
        charOnRequestParams: config.charOnRequestParams,
        charOnRequestListKeys: config.charOnRequestListKeys,
        treeId: "${phyloInstance.studyid}",
        // dynamic chart size. the default chart width is too small.
        chartWidth: config.chartWidth

    });

    var map = new Map({
        id: 'map',
        tabId:'mapTab',
        pj: pj,
        filter: filter,
        height: 650,
        width: $('#tab-content').width(),
        layer:config.layer(),
        query:config.query,
        filterFieldName:'REGNO_s',
        source: config.type,
        character: character,
        colorByCharacters: true,
        legend:{
            proxy: true,
            proxyUrl: config.proxyUrl,
            baseUrl: "${createLink(controller: 'ala', action: 'getLegends')}",
            dataType:'jsonp',
            urlParams:{
                cm:undefined,
                type:'application/json',
                fq: undefined,
                q: undefined,
                source: undefined
            },
            icon:'<i class="icon icon-list"></i> <label style="display: inline-block">Legend</label>',
            defaultValue: [{
                red: 223,
                green: 74,
                blue: 33,
                hex: 'df4a21',
                name: 'All records'
            }]
        },
        env: {
            'colormode': undefined,
            'name': 'circle',
            'size': 4,
            'opacity': 0.8,
            'color': 'df4a21'
        },
        colorBy: {
            url: config.colorByUrl,
            drid: config.drid,
            defaultValue: 'taxon_name'
        },
        downloadReasonsUrl: config.downloadReasonsUrl,
        spUrl: config.spUrl,
        records: records,
        mapbox: {
            id: "${grailsApplication.config.map.mapbox.id}",
            token: "${grailsApplication.config.map.mapbox.token}"
        }
    });

    var habitat = new Habitat({
        id:'habitat',
        tabId:'habitatTab',
        pj: pj,
        doSync: ${edit},
        syncData: {
            id: ${phyloInstance.getId()},
        },
        listUrl: '${createLink(controller: 'ala', action: 'getAllLayers')}',
        height: 700,
        syncUrl: "${createLink(controller: 'phylo', action: 'saveHabitat')}",
        initialState: <g:message message="${JSON.parse(phyloInstance.getHabitat() ?: '{}') as grails.converters.JSON}"/>,
        graph: {
            url: '${createLink(controller: 'phylo', action: 'getHabitat')}',
            type: 'GET',
            dataType: 'JSON',
            xAxisContextual: 'Habitat states',
            xAxisEnvironmental: 'values',
            yAxis: 'Occurrence count'
        },
        saveQuery:{
            url: '${createLink(controller: 'ala', action: 'saveQuery')}',
            type: 'POST',
            dataType: 'JSONP'
        },
        downloadSummaryUrl: '${createLink(controller: "phylo", action:"getHabitat" )}/?download=true',
        biocacheOccurrenceDownload: 'http://biocache.ala.org.au/ws/occurrences/index/download',
        downloadReasonsUrl: config.downloadReasonsUrl,
        records: records,
        // dynamic chart size. the default chart width is too small.
        chartWidth: config.chartWidth,
        tabId: 'tab'
    });


    $( "#tabs" ).tab('show');
    // a fix to display map tiles properly. Without it map is grey colored for majority of area.
    $("body").on("shown.bs.tab", "#mapTab", function() {
        map.invalidateSize();
    });
    $("body").on("shown.bs.tab", "#habitatTab", function() {
        // abort previous save query calls since this is a new query
        habitat.redraw()
    });

    google.setOnLoadCallback(character.googleChartsLoaded);
    google.setOnLoadCallback(habitat.googleChartsLoaded);
</r:script>
<g:render template="keepSessionAlive"/>
</body>
</html>