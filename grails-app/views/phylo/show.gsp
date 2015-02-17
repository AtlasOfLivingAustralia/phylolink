<%--
 Created by Temi Varghese on 20/01/15.
--%>

<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'phylo.label', default: 'Phylo')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
    <r:require modules="application,leaflet,phylojive,character,map"/>
</head>

<body>
<div class="container">
    <div class="row-fluid">
        <div class="span6">
            <div id="info"></div>
        </div>
        <div role="tabpanel" id="tabs" class="span6">

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation"><a href="#character" aria-controls="home" role="tab" data-toggle="tab">Character</a></li>
                <li role="presentation"><a href="#map" aria-controls="profile" role="tab" data-toggle="tab" id="mapTab">Map</a></li>
                <li role="presentation" class="active"><a href="#habitat" aria-controls="profile" role="tab" data-toggle="tab" id="habitatTab">Analysis</a></li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content">
                <div role="tabpanel" class="tab-pane" id="character"></div>
                <div role="tabpanel" class="tab-pane" id="map"></div>
                <div role="tabpanel" class="tab-pane active" id="habitat"></div>
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
        proxyUrl: '/phylolink/ala/jsonp',
        layer: function(){
            switch (config.type){
                case 'sandbox': return config.sandboxLayer;
                case 'ala': return config.biocacheLayer;
            }
        },
        treeUrl:"${createLink(controller: 'tree', action: 'getTree')}?id=${phyloInstance.studyid}&treeid=${phyloInstance.treeid}",
        format: "${tree.treeFormat}",
        charUrl: '/phylolink/ala/getSandboxCharJson?drid=data_resource_uid:drt2783&fields=["phenotype_s"]&key=lineage_ID_s',
        initCharacters: <g:message message="${JSON.parse(phyloInstance.getCharacters()?:'[]') as grails.converters.JSON}"/>,
        filterParams: {
            q: '',
            fq:{

            }
        },
        colorByUrl: '/phylolink/ala/facets'
    }
    google.load("visualization", "1", {packages: ["corechart"]});
//    $(document).ready(function(){
        $( "#tabs" ).tab('show');
        var pj = new PJ({
            width: 500,
            height: 700,
            codeBase: '../..',
            dataType:'json',
            bootstrap: 2,
            url: config.treeUrl,
            id: 'info',
            format: config.format
        });

        var filter = new Filter($.extend(config.filterParams, {
            pj: pj,
            fqVariable:'species'
        }));

        var character = new Character({
            id: "character",
            pj: pj,
            url: config.charUrl,
            dataType:'jsonp',
            height:700,
            headerHeight:41,
            initCharacters:config.initCharacters,
            bootstrap:2,
            doSync: true,
            syncData: {
                id: ${phyloInstance.getId()}
            },
            syncUrl: "${createLink(controller: 'phylo', action: 'saveCharacters')}"
        });
var map = new Map({
    id: 'map',
    pj: pj,
    filter: filter,
    height: 650,
    width: $('#tab-content').width(),
    layer:config.layer(),
    query:config.query,
    filterFieldName:'REGNO_s',
    source: config.type,
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
            pj: pj,
            doSync: ${(phyloInstance.getOwner()?.getUserId() == userId)? true:false},
            syncData: {
                id: ${phyloInstance.getId()}
            },
            height: 700,
            syncUrl: "${createLink(controller: 'phylo', action: 'saveHabitat')}",
            initialState: <g:message message="${JSON.parse(phyloInstance.getHabitat()?:'[]') as grails.converters.JSON}"/>,
        })

        // a fix to display map tiles properly. Without it map is grey colored for majority of area.
        $("body").on("shown.bs.tab", "#mapTab", function() {
            map.invalidateSize();
        });

        google.setOnLoadCallback(character.googleChartsLoaded);
        google.setOnLoadCallback(habitat.googleChartsLoaded);
</r:script>
</body>
</html>