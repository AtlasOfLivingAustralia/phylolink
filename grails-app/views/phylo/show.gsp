<%--
 Created by Temi Varghese on 20/01/15.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
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

        <div role="tabpanel" id="tabs" class="span5">

            <!-- Nav tabs -->
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation"><a href="#character" aria-controls="home" role="tab"
                                           data-toggle="tab">Character</a></li>
                <li role="presentation" class="active"><a href="#map" aria-controls="profile" role="tab"
                                                          data-toggle="tab" id="mapTab">Map</a></li>
            </ul>

            <!-- Tab panes -->
            <div class="tab-content">
                <div role="tabpanel" class="tab-pane" id="character"></div>

                <div role="tabpanel" class="tab-pane active" id="map"></div>
            </div>

        </div>
    </div>
</div>

<r:script disposition="defer">
    var config = {
        treeUrl:"${createLink(controller: 'tree', action: 'getTree')}?id=${phyloInstance.studyid}&treeid=${phyloInstance.treeid}",
        format: "${tree.treeFormat}"
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
        url:config.treeUrl,
        id: 'info',
        format: config.format
    });
    var character = new Character({
        id: "character",
        pj: pj,
        url:'http://115.146.93.110:8080/phylolink/ala/getSandboxCharJson?drid=drt2811&fields=["REGNO_s","Body_length_i","Genetic_lineage_s","Current_Taxonomy_s","Candidate_species_s","RAG_1_s","PDC_s","ND2_s","Allozymes_s","locality_s","colour_pattern_i"]&key=REGNO_s',
        dataType:'jsonp',
        height:700,
        headerHeight:41,
        initCharacters:['Genetic lineage'],
        bootstrap:2
    });
    var map = new Map({
        id: 'map',
        pj: pj,
        height: 650,
        width: $('#tab-content').width(),
        layer:'http://biocache.ala.org.au/ws/ogc/wms/reflect?',
        query:'data_resource_uid:drt2811',
        filterFieldName:'species',
        legend:{
            urlParams:{
                cm:'genetic_lineage_s',
                type:'application/json',
                q:'data_resource_uid:drt2811'
            },
            icon:'<i class="icon-list"></i>'
        },
        env: {
            'colormode': 'genetic_lineage_s',
            'name': 'circle',
            'size': 4,
            'opacity': 0.8
        }
    });

    // a fix to display map tiles properly. Without it map is grey colored for majority of area.
    $("body").on("shown.bs.tab", "#mapTab", function() {
        map.invalidateSize();
    });

    google.setOnLoadCallback(character.googleChartsLoaded);
</r:script>
</body>
</html>