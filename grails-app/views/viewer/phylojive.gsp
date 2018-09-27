<%--
  Created by IntelliJ IDEA.
  User: temi
  Date: 09/04/2015
--%>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="viewer.show"/></title>
    %{--<r:require modules="application,phylojive,contextmenu,bugherd,select2"/>--}%
    <r:require modules="application,leaflet,phylojive,character,map,contextmenu,records,appSpecific,jqxTree,select2,css"/>
</head>

<body>
<div class="container-fluid" style="margin-left:-20px; margin-right:-20px;">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri: '/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a></li>
            </ul>
        </div>
    </div>

    <div class="row-fluid">
        <div id="vizTitle"><g:render template="../phylo/title"></g:render></div>
    </div>

    <div class="row-fluid">
        <div class="col-sm-6 col-md-6">
            <div id="info"></div>
            <g:render template="../phylo/settings"></g:render>
            <g:render template="../phylo/trimming"></g:render>
        </div>
        <div class="col-sm-6 col-md-6">
            <g:render template="../phylo/metadata"></g:render>
        </div>
    </div>
</div>

<r:script disposition="defer">
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
</r:script>
</body>
</html>