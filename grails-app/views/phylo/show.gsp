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
    <r:require modules="application,leaflet,phylojive,character,map,contextmenu"/>
    <r:require modules="bugherd"/>
</head>

<body>
%{--padding top since breadcrumb is hidden by the new nav bar--}%
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri:'/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard',action: 'myViz')}">My Visualisations</a> </li>
            </ul>
        </div>
    </div>
    <div id="vizTitle"></div>

    <div class="row-fluid">
        <div class="span6">
            <div id="info"></div>
        </div>

        <div role="tabpanel" id="tabs" class="span6">

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li id="charLi" role="presentation" class="active"><a id="characterTab" href="#character" aria-controls="home" role="tab"
                                                          data-toggle="tab">Character</a></li>
                <li role="presentation"><a href="#map" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="mapTab">Map</a></li>
                <li role="presentation" ><a   href="#habitat" aria-controls="profile" role="tab" data-toggle="tab"
                                           id="habitatTab">Analysis</a></li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content" style="position: relative">
                <div role="tabpanel" class="tab-pane active" id="character"></div>

                <div role="tabpanel" class="tab-pane" id="map"></div>

                <div role="tabpanel" class="tab-pane" id="habitat"></div>
            </div>

        </div>
    </div>
</div>

<r:script disposition="defer">
    var config ={
        type:'ala',
        sandboxLayer: 'http://sandbox.ala.org.au/biocache-service/webportal/wms/reflect',
        biocacheLayer: 'http://biocache.ala.org.au/ws/ogc/wms/reflect',
        sandboxLegend: 'http://sandbox.ala.org.au/ala-hub/occurrence/legend',
        biocacheLegend: 'http://biocache.ala.org.au/occurrence/legend',
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
        initCharacters: <g:message
        message="${JSON.parse(phyloInstance.getCharacters() ?: '[]') as grails.converters.JSON}"/>,
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
        pjId: 'info'
    }

    google.load("visualization", "1", {packages: ["corechart"]});
//    $(document).ready(function(){

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
            hData:{
                id:config.id,
                title: config.title,
                edit: config.edit
            },
            titleUrl: config.titleUrl,
            edit: config.edit,
            saveQuery:{
                url: '${createLink(controller: 'ala', action: 'saveQuery')}',
                type: 'POST',
                dataType: 'JSON'
            }
        });

        var filter = new Filter($.extend(config.filterParams, {
            pj: pj,
            fqVariable:'species'
        }));

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
                url: "${createLink(controller: 'ala', action: 'saveAsList')}",
                type: 'POST'
            }
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
    legend:{
        proxy: true,
        proxyUrl: config.proxyUrl,
        baseUrl: config.legendUrl(),
        dataType:'jsonp',
        urlParams:{
            cm:undefined,
            type:'application/json'
        },
        icon:'<i class="icon icon-list"></i> <label style="display: inline-block">Legend</label>'
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
                dataType:'jsonp',
                drid: config.drid
            }
        });
        var habitat = new Habitat({
            id:'habitat',
            tabId:'habitatTab',
            pj: pj,
            doSync: ${edit},
            syncData: {
                id: ${phyloInstance.getId()}
    },
    listUrl: '${createLink(controller: 'ala', action: 'getAllLayers')}',
    height: 700,
    syncUrl: "${createLink(controller: 'phylo', action: 'saveHabitat')}",
            initialState: <g:message
        message="${JSON.parse(phyloInstance.getHabitat() ?: '{}') as grails.converters.JSON}"/>,
            graph: {
            url: '${createLink(controller: 'phylo', action: 'getHabitat')}',
            type: 'GET',
            dataType: 'JSON',
//            title:'Habitat',
            xAxisContextual: 'Habitat states',
            xAxisEnvironmental: 'values',
            yAxis: 'Occurrence count'
        },
        saveQuery:{
            url: '${createLink(controller: 'ala',action: 'saveQuery')}',
            type: 'POST',
            dataType: 'JSONP'
        }
        })
        $( "#tabs" ).tab('show');
        // a fix to display map tiles properly. Without it map is grey colored for majority of area.
        $("body").on("shown.bs.tab", "#mapTab", function() {
            map.invalidateSize();
        });

        google.setOnLoadCallback(character.googleChartsLoaded);
        google.setOnLoadCallback(habitat.googleChartsLoaded);
</r:script>
<g:render template="keepSessionAlive"/>
</body>
</html>