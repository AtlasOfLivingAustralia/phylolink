<%--
  Created by IntelliJ IDEA.
  User: temi
  Date: 09/04/2015
--%>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="viewer.show"/></title>
    <r:require modules="application,phylojive,contextmenu,bugherd"/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri: '/')}">Home</a> <span class="divider">/</span></li>
                <li><a href="${createLink(controller: 'wizard', action: 'start')}">Start PhyloLink</a></li>
            </ul>
        </div>
    </div>

    <div id="vizTitle"></div>

    <div class="row-fluid">
        <div class="span6">
            <div id="info"></div>
        </div>
        <div class="span6">
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
        title:"${tree.getTitle()}",
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
            edit: config.edit
        });
</r:script>
</body>
</html>