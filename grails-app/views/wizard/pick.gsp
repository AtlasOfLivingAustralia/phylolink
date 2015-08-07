<%--
 Created by Temi Varghese on 16/10/2014.
--%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>Add Phylogenetic Tree</title>
    <r:require modules="bootstrap"/>
    <r:require modules="bugherd"/>
    <style>
    .verticalLine {
        border-right: thin solid;
    }
    </style>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}" type="text/css" media="screen"/>
</head>
<body>
<div class="container"  style="min-height: 700px">
    <div class="row-fluid">
        <div class="span12">
            <ul class="breadcrumb">
                <li><a href="${createLink(uri:'/')}">Home</a></li>
            </ul>
        </div>
    </div>
    <legend>Pick a tree or view a visualisation</legend>
    <p style="font-size:14px; max-width: 60em">On this page, you can pick a tree for visualisation, view your previous visualisations, or
    upload your own tree for visualisation. You can pick a tree from expert recommended list or from your previous
    uploads. Click <span class="label label-info">next</span> button to proceed or to go to previous page click <span class="label">back</span> button.</p>

    <div class="row-fluid">
        <g:form controller="wizard" action="pickMethod" method="POST">
            <div class="control-group">
                <div class="controls">
                    %{--<g:radioGroup name="options" values="['addTree', 'searchTB','expertTrees','myTrees']"--}%
                                  %{--labels="['Add a tree', 'Import from TreeBase','Expert suggested tree','My trees']" required="">--}%
                        %{--<label class="radio">${it.radio} ${it.label}</label>--}%
                    %{--</g:radioGroup>--}%
                    %{--<g:radioGroup name="options" required="">--}%
                        <div class="row-fluid">
                            <div class="span3 verticalLine">
                                
                                <label class="radio"><input type="radio" name="options" value="expertTrees" required=""> Expert suggested tree</input></label>
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input <g:if test="${!loggedIn}">disabled</g:if>
                                                            type="radio" name="options" value="addTree" required=""> Add a tree
                                    <g:if test="${!loggedIn}"> (login required)</g:if></input></label>
                                <label class="radio" <g:if test="${numberOfTrees <= 0}">disabled</g:if>><input <g:if test="${numberOfTrees <= 0}">disabled</g:if>
                                                            type="radio" name="options" value="myTrees" required=""> My trees</input></label>
                            </div>
                            <div class="span5">
                                <label class="radio" <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>><input <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>
                                                            type="radio" name="options" value="myViz" required=""> My visualisations</label>
                                <label class="radio"><input type="radio" name="options" value="demo" required=""> Example demonstrations</label>
                            </div>
                        </div>
                    %{--</g:radioGroup>--}%
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <div name="back" class="btn" onclick="window.location = '${createLink(uri:'/')}'"><i
                            class="icon icon-arrow-left"></i> Back</div>
                    <button type="submit" class="btn btn-primary" value="Next"><i
                            class="icon icon-white icon-arrow-right"></i> Next</button>
                </div>
            </div>
        </g:form>
    </div>
</div>
</body>
</html>