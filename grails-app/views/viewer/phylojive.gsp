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
<div class="container-fluid" style="padding-top: 60px">
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
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <th colspan="2">Tree metadata</th>
                </tr>
                <tr>
                    <td>Title:</td>
                    <td>${tree.getTitle()}</td>
                </tr>
                <tr>
                    <td>Reference:</td>
                    <td>${tree.getReference()}</td>
                </tr>
                <tr>
                    <td>Year:</td>
                    <td>${tree.getYear()}</td>
                </tr>
                <g:if test="${tree.getDoi()!= null}">
                    <tr>
                        <td>Doi:</td>
                        <td>${tree.getDoi()}</td>
                    </tr>
                </g:if>
                </tbody>
            </table>
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <th colspan="2">Actions</th>
                </tr>
                <tr>
                    <td>
                        Download tree:
                    </td>
                    <td>
                        <a class="btn" href="${createLink(controller: 'tree', action: 'download')}?id=${studyId}"><i class="icon icon-download"></i> Download</a>
                    </td>
                </tr>
                <tr>
                    <td>
                        Link tree with data:
                    </td>
                    <td>
                        <a class="btn btn-primary" href="${createLink(controller: 'wizard', action: 'visualize')}?id=${studyId}">
                            <i class="icon icon-arrow-right"></i> Visualise with Phylolink</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<r:script disposition="defer">
    var config ={
        type:'ala',
        proxyUrl: '/phylolink/ala/jsonp',
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