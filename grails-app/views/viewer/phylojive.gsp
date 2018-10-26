<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="viewer.show"/></title>
    <asset:stylesheet src="PhyloJive.css" />
    <asset:stylesheet src="phylolink.css" />
</head>

<body>
<div class="container">

    <div class="row">
        <div id="vizTitle"><g:render template="/phylo/title"></g:render></div>
    </div>

    <div class="row">
        <div class="col-sm-6 col-md-6">
            <div id="info"></div>
            <g:render template="/phylo/settings"></g:render>
            <g:render template="/phylo/trimming"></g:render>
        </div>
        <div class="col-sm-6 col-md-6">
            <g:render template="/phylo/metadata"></g:render>
        </div>
    </div>
</div>

<asset:javascript src="thirdparty/jsphylosvg-min.js" />
<asset:javascript src="thirdparty/jit.js" />
<asset:javascript src="thirdparty/md5.js" />
<asset:javascript src="js/PJ.js" />
<asset:javascript src="js/Filter.js" />
<asset:javascript src="js/Habitat.js" />
<asset:javascript src="js/Character.js" />
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

<script disposition="defer">
    var config ={
        type:'ala',
        treeUrl:"${createLink(controller: 'tree', action: 'getTree')}?id=${studyId}&treeid=${treeId}",
        format: undefined,
        edit:false,
        title:"${raw(tree.getTitle())}",
        titleUrl: undefined,
        pjId: 'info'
    }
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
                title: config.title,
                edit: config.edit
            },
            titleUrl: config.titleUrl,
            edit: config.edit,
            listToolBaseURL: "${grailsApplication.config.listToolBaseURL}",
            heading:'vizTitle',
            settingsId:'pjSettings',
            trimmingId:'pjTrimming'
        });
</script>
</body>
</html>