<%--
 Created by Temi Varghese on 20/01/15.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}"/>
    <title data-bind='text: title'>${phyloInstance.title}</title>

    <g:if test="${isDemonstration}">
        <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink \\ ${g.createLink( controller: 'wizard', action: 'demo')},Demonstration vizualisations"/>
    </g:if>
    <g:else>
        <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink \\ ${g.createLink( controller: 'wizard', action: 'myViz')},My Visualisations"/>
    </g:else>

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {packages: ["corechart"]});
    </script>
    <asset:stylesheet src="PhyloJive.css" />
    <asset:stylesheet src="phylolink.css" />
    <asset:stylesheet src="jquery.contextMenu.css" />
    <asset:stylesheet src="jqwidgets/styles/jqx.base.css" />
    <asset:stylesheet src="maingsp.css" />
    <asset:stylesheet src="jquery-ui.css" />
    <asset:stylesheet src="slider.css" />
    <asset:stylesheet src="leaflet.v0.7.3.css" />
    <asset:stylesheet src="leaflet.fullscreen.v0.0.2.css" />
    <asset:stylesheet src="Control.Legend.css" />
    <asset:stylesheet src="Control.Loading.css" />
</head>


<body class="fluid" >
<g:render template="settings"></g:render>
<g:render template="trimming"></g:render>

<div class="container-fluid" style="margin-left:-10px; margin-right:-20px;">
    <div class="row" style="margin-top: -20px">
        <div class="col-sm-4 col-md-4" style="padding-right:0px;padding-left:0px;">
            <div id="info"></div>
        </div>
        <div role="tabpanel" id="tabs" class="col-sm-8 col-md-8" style="padding-left:0px; margin-left:0px; margin-right:0px;">

            <div style="float:left;">
                <div id="vizTitle"><g:render template="title"></g:render></div>
            </div>

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li id="charLi" role="presentation">
                    <a id="characterTab" href="#character" aria-controls="home"
                                                                role="tab"
                                                                data-toggle="tab">Character</a>
                </li>
                <li role="presentation" class="active">
                    <a href="#mapTabContent" aria-controls="profile" role="tab" data-toggle="tab" id="mapTab">Map</a>
                </li>
                <li role="presentation">
                    <a href="#compareVariables" aria-controls="profile" role="tab" data-toggle="tab" id="compareVariablesTab">Compare variables</a>
                </li>
                <li role="presentation">
                    <a href="#habitat" aria-controls="profile" role="tab" data-toggle="tab" id="habitatTab">Analysis</a>
                </li>
                <g:if test="${edit}">
                    <li role="presentation" >
                        <a href="#records" aria-controls="profile" role="tab"
                                                data-toggle="tab"
                                                id="recordsTab">Occurrences</a>
                    </li>
                </g:if>
                %{--<li role="presentation"><a href="#metadata" aria-controls="profile" role="tab" data-toggle="tab"--}%
                                           %{--id="metadataTab">Metadata</a></li>--}%
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
                        <div class="text-right" style="display:none;">
                            <a id="spLink" class="btn btn-link" data-bind="attr:{href:spUrl.url}" target="_blank" ><i class="fa fa-external-link"></i>&nbsp;Open in Spatial Portal</a>
                            <a id="downloadMapDataLink" class="btn btn-link" data-toggle="modal" href="#mapOccurrenceDownloadModal"><i class="fa fa-download"></i>&nbsp;Download occurrence data</a>
                        </div>

                        <g:render template="occurrenceDownloadPopup" model="[dialogId: 'mapOccurrenceDownloadModal', clickAction: '$root.downloadMapData', viewModel: '$root.downloadViewModel']"></g:render>
                    </div>
                </div>

                <div role="tabpanel" class="tab-pane" id="habitat">
                    <g:render template="plots"></g:render>
                </div>

                <div role="tabpanel" class="tab-pane" id="compareVariables">
                    <g:render template="compareVariables"></g:render>
                </div>

                <div role="tabpanel" class="tab-pane" id="records">
                    <div id="recordsForm"></div>
                </div>

                <div role="tabpanel" class="tab-pane" id="help">
                    <p class="pull-right">Presented by: <strong>Joseph Miller</strong>
                    </p>
                    <h3 style="margin-top:10px;">How do I use phylolink ?</h3>
                    <iframe width="100%" height="230"
                            src="https://www.youtube.com/embed/_fN3Nn159Tw" frameborder="0" allowfullscreen>
                    </iframe>
                    &nbsp;

                    <g:render template="metadata"></g:render>
                </div>
            </div>

        </div>
    </div>
</div>

<script id="templateOccurrence" type="text/html">
    <g:render template="occurrence"></g:render>
</script>

<asset:javascript src="thirdparty/jsphylosvg-min.js" />
<asset:javascript src="thirdparty/jit.js" />
<asset:javascript src="thirdparty/md5.js" />
<asset:javascript src="js/PJ.js" />
<asset:javascript src="js/Filter.js" />
<asset:javascript src="js/Habitat.js" />
<asset:javascript src="js/Character.js" />
<asset:javascript src="js/CompareVariables.js" />
<asset:javascript src="thirdparty/jquery.contextMenu.js" />
<asset:javascript src="js/application.js" />
<asset:javascript src="js/Records.js" />
<asset:javascript src="jqwidgets/jqxcore.js" />
<asset:javascript src="thirdparty/jquery-ui.min.js" />
<asset:javascript src="thirdparty/bootstrap-slider.js" />
<asset:javascript src="thirdparty/jquery.cookie.js" />
<asset:javascript src="thirdparty/leaflet.v0.7.3.js" />
<asset:javascript src="thirdparty/Leaflet.fullscreen.v0.0.2.min.js" />
<asset:javascript src="js/Control.Checkbox.js" />
<asset:javascript src="js/Control.Legend.js" />
<asset:javascript src="js/Control.Loading.js" />
<asset:javascript src="js/Control.Select.js" />
<asset:javascript src="js/Control.Slider.js" />
<asset:javascript src="js/Map.js" />
<asset:javascript src="jqwidgets/jqxbuttons.js" />
<asset:javascript src="jqwidgets/jqxscrollbar.js" />
<asset:javascript src="jqwidgets/jqxpanel.js" />
<asset:javascript src="jqwidgets/jqxtree.js" />
<asset:javascript src="jqwidgets/jqxexpander.js" />
<asset:javascript src="thirdparty/bugherd.js" />
<asset:javascript src="thirdparty/knockout-3.0.0.js" />
<asset:javascript src="thirdparty/knockout-custom-bindings.js" />
<asset:javascript src="thirdparty/emitter.js" />
<asset:javascript src="thirdparty/spin.min.v2.0.1.js" />
<asset:javascript src="thirdparty/knockout-sortable.min.js" />
<asset:javascript src="js/utils.js" />
<asset:javascript src="thirdparty/select2/select2-3.5.8.min.js" />

<g:javascript disposition="defer">
    var config = {
        type: 'ala',
        sandboxLayer: 'http://sandbox1.ala.org.au/ws/mapping/wms/reflect',
        biocacheLayer: 'https://biocache.ala.org.au/ws/mapping/wms/reflect',
        sandboxLegend: 'http://sandbox1.ala.org.au/ala-hub/occurrence/legend',
        biocacheLegend: 'https://biocache.ala.org.au/occurrence/legend',
        downloadReasonsUrl: 'http://logger.ala.org.au/service/logger/reasons',
        legendUrl: function(){
            switch (config.type){
                case 'sandbox': return config.sandboxLegend;
                case 'ala': return config.biocacheLegend;
            }
        },
        proxyUrl: '${raw(createLink(controller: 'ala', action: 'jsonp'))}',
        layer: function(){
            switch (config.type){
                case 'sandbox': return config.sandboxLayer;
                case 'ala': return config.biocacheLayer;
            }
        },
        treeId: ${tree.id},
        treeUrl:"${raw(createLink(controller: 'tree', action: 'getTree'))}?id=${phyloInstance.studyid}&treeid=${phyloInstance.treeid}",
        format: "${tree.treeFormat}",
        initCharacters: <g:message message="${JSON.parse(phyloInstance.getCharacters() ?: '[]') as grails.converters.JSON}"/>,
        filterParams: {
            q: '',
            fq:{}
        },
        colorByUrl: '${raw(createLink(controller: 'ala', action: 'facets'))}',
        edit:${edit},
        id: ${phyloInstance.getId()},
        title:'<g:message message="${phyloInstance.getTitle().replace('\'', '\\\'')}"/>',
        titleUrl: '${raw(createLink(controller: 'restrictedmethods', action: 'saveTitle'))}',
        pjId: 'info',
        charOnRequest: true,
        charOnRequestListKeys:"${raw(createLink(controller: 'characters', action: 'getKeys'))}",
        charOnRequestBaseUrl:"${raw(createLink(controller: 'characters', action: 'getCharJsonForKeys'))}",
        charOnRequestParams:{
            drid:undefined,
            keys:undefined
        },
        spUrl: {
            baseUrl: '${raw(grailsApplication.config.spatialPortalRoot)}',
            url: ko.observable('${raw(grailsApplication.config.spatialPortalRoot)}')
        },
        runSaveQuery: false,
        chartWidth: $('#character').width() - 30
    }

    var pj = new PJ({
        width: $('#' + config.pjId).width() - 10,
        height: 590,
        codeBase: '../..',
        dataType:'json',
        bootstrap: 2,
        treeId: config.treeId,
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
        settingsUrl: "${raw(createLink(controller: 'phylo', action: 'savePjSettings'))}/${phyloInstance.getId()}",
        edit: config.edit,
        runSaveQuery: false,
        saveQuery:{
            defaultQuery: <phy:formatDefaultQueryAsJSON tree="${tree}"/>,
            url: '${raw(createLink(controller: 'ala', action: 'saveQuery'))}',
            type: 'POST',
            dataType: 'JSON',
            data: {
                speciesList: undefined,
                dataLocationType: undefined, // 'ala' or 'sandbox'
                biocacheServiceUrl: undefined, // 'http://sandbox.ala.org.au',
                drid: undefined // drt121
            }
        },
        listToolBaseURL: "${raw(grailsApplication.config.listToolBaseURL)}",
        pjSettings: <g:message message="${JSON.parse(phyloInstance.getPjSettings() ?: '{}') as grails.converters.JSON}"/>
    });

    var filter = new Filter($.extend(config.filterParams, {
        pj: pj,
        fqVariable:'species'
    }));

    var records = new Records({
        id: 'recordsForm',
        template: $('#templateOccurrence').html(),
        deleteResourceUrl:"${raw(createLink(controller: 'sandbox', action: 'deleteResource'))}",
        uploadUrl: '${raw(createLink(controller: 'ala', action: 'uploadData'))}?phyloId=${phyloInstance.id}',
        indexingStatusUrl: "${raw(createLink(controller: 'sandbox', action: 'checkStatus'))}",
        sampleFile: "${raw(resource(dir: 'artifacts', file: 'occurrenceRecords.csv'))}",
        dataresourceInfoUrl: "${raw(createLink(controller: 'sandbox', action: 'dataresourceInfo'))}?phyloId=${phyloInstance.id}",
        dataresourceListUrl: '${raw(createLink(controller: 'ala', action: 'getRecordsList'))}?phyloId=${phyloInstance.id}',
        pj: pj,
        selectResourceOnInit: true,
        initResourceId: <g:message message="${phyloInstance.getSource()?:-1}"/>,
        edit: ${edit},
        syncUrl: "${raw(createLink(controller: 'phylo', action: 'saveSource'))}",
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
        bootstrap: 2,
        sampleCSV:'${raw(resource(dir: 'artifacts', file: 'traits.csv'))}',
        doSync: ${edit},
        syncData: {
            id: ${phyloInstance.getId()}
        },
        syncUrl: "${raw(createLink(controller: 'phylo', action: 'saveCharacters'))}",
        charactersList : {
            url: '${raw(createLink(controller: 'characters', action: 'list'))}',
            type: 'GET',
            dataType: 'JSON'
        },
        edit: ${edit},
        upload: {
            url: "${raw(createLink(controller: 'ala', action: 'saveAsList'))}?phyloId=${phyloInstance.id}",
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
    google.setOnLoadCallback(character.googleChartsLoaded);


    var compareVariables = new CompareVariables({
        id: "compare-variables",
        tabId: 'compareVariablesTab',
        pj: pj,
        variable1: "state",
        variable2: "cl678",
        chartDataUrl: "${raw(createLink(controller: "chart", action: "stackedBar"))}",
        graphibleFieldsUrl: "${raw(createLink(controller: "chart", action: "graphibleFields"))}",
        chartWidth: config.chartWidth,
        records: records
    });

    google.setOnLoadCallback(compareVariables.googleChartsLoaded);

    var habitat = new Habitat({
        id:'habitat',
        tabId:'habitatTab',
        pj: pj,
        doSync: ${edit},
        syncData: {
            id: ${phyloInstance.getId()},
        },
        listUrl: '${raw(createLink(controller: 'ala', action: 'getAllLayers'))}',
        height: 700,
        syncUrl: "${raw(createLink(controller: 'phylo', action: 'saveHabitat'))}",
        initialState: <g:message message="${JSON.parse(phyloInstance.getHabitat() ?: '{}') as grails.converters.JSON}"/>,
        graph: {
            url: '${raw(createLink(controller: 'phylo', action: 'getHabitat'))}',
            type: 'GET',
            dataType: 'JSON',
            xAxisContextual: 'Habitat states',
            xAxisEnvironmental: 'values',
            yAxis: 'Occurrence count'
        },
        saveQuery:{
            url: '${raw(createLink(controller: 'ala', action: 'saveQuery'))}',
            type: 'POST',
            dataType: 'JSONP'
        },
        downloadSummaryUrl: '${raw(createLink(controller: "phylo", action:"getHabitat" ))}/?download=true',
        biocacheOccurrenceDownload: 'http://biocache.ala.org.au/ws/occurrences/index/download',
        downloadReasonsUrl: config.downloadReasonsUrl,
        records: records,
        // dynamic chart size. the default chart width is too small.
        chartWidth: config.chartWidth,
        tabId: 'tab'
    });
    google.setOnLoadCallback(habitat.googleChartsLoaded);

    var map = new Map({
        id: 'map',
        tabId:'mapTab',
        pj: pj,
        filter: filter,
        height: $('.infovis').height() - 43,
        width: $('#tab-content').width(),
        layer: config.layer(),
        query: config.query,
        filterFieldName:'REGNO_s',
        source: config.type,
        character: character,
        colorByCharacters: true,
        legend:{
            proxy: true,
            proxyUrl: config.proxyUrl,
            baseUrl: "${raw(createLink(controller: 'ala', action: 'getLegends'))}",
            dataType:'jsonp',
            urlParams:{
                cm:undefined,
                type:'application/json',
                fq: undefined,
                q: undefined,
                source: undefined
            },
            icon:'<i class="glyphicon glyphicon-list"></i> <label style="display: inline-block">Legend - click to show</label>',
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
            'size': 7,
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

    $( "#tabs" ).tab('show');
    // a fix to display map tiles properly. Without it map is grey colored for majority of area.
    $("body").on("shown.bs.tab", "#mapTab", function() {
        map.invalidateSize();
    });
    $("body").on("shown.bs.tab", "#habitatTab", function() {
        // abort previous save query calls since this is a new query
        habitat.redraw()
    });
</g:javascript>
<g:render template="keepSessionAlive"/>

<style type="text/css">
#freshwidget-button { display:none; top:700px !important;}
#FreshWidget { display:none; }
#freshwidget-button { display:none; }
</style>
</body>
</html>