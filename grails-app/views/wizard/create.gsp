<%--
 Created by Temi Varghese on 23/10/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Add Phylogenetic Tree</title>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink"/>

    <asset:stylesheet src="phylolink.css" />
    <asset:javascript src="thirdparty/knockout-3.0.0.js" />
    <asset:javascript src="thirdparty/knockout-custom-bindings.js" />
</head>

<body class="fluid">
<div class="container"  style="min-height: 700px">

    <h1>Upload a tree</h1>

    <g:form action="create" class="form-horizontal" method="POST" enctype="multipart/form-data">

        <p>Enter your tree here by completing the form below. Tree data can be provided by uploading a file or by pasting the data into the box below. Supported formats are NEXML and NEWICK.<p/>

        <fieldset class="form">
            <div class="row-fluid">
                <g:render template="/tree/form" model="['tree':tree]"/>
                <div class="control-group">
                    <div class="controls">
                        <div name="back" class="btn" onclick="window.location = '${back}'"><i
                                class="icon icon-arrow-left"></i> Back</div>
                        <button type="submit" class="btn btn-primary" value="Next"><i
                                class="icon icon-white icon-arrow-right"></i> Next</button>
                    </div>
                </div>
        </fieldset>
    </g:form>
</div>
</body>
</html>