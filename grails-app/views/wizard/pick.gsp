<%--
 Created by Temi Varghese on 16/10/2014.
--%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>Add Phylogenetic Tree</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}" type="text/css" media="screen"/>
</head>
<body>
<div class="container"  >

    <legend>Load a tree or select a visualisation</legend>
    <p style="font-size:14px; max-width: 60em">On this page, you can load a tree for visualisation, view your previous visualisations, or
    upload your own tree for visualisation. You can select a tree from an expert recommended list or from your previous uploads.

    <div class="row-fluid">
        <g:form controller="wizard" action="pickMethod" method="POST">
            <div class="control-group">
                <div class="controls">
                        <div class="row-fluid">
                            <div class="col-sm-3 col-md-3 verticalLine wizard-option-group">
                                <h5>Load a tree</h5>
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input type="radio" name="options" value="expertTrees" required="" <g:if test="${!loggedIn}">disabled</g:if>/> Select an expert recommended tree<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio" <g:if test="${numberOfTrees <= 0}">disabled</g:if>><input <g:if test="${numberOfTrees <= 0}">disabled</g:if>
                                                                       type="radio" name="options" value="myTrees" required=""/> Select from my trees<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input <g:if test="${!loggedIn}">disabled</g:if>
                                                            type="radio" name="options" value="addTree" required=""> Add a new tree
                                    <g:if test="${!loggedIn}"> (login required)</g:if></input></label>

                            </div>
                            <div class="col-sm-3 col-md-3 wizard-option-group">
                                <h5>Select a visualisation</h5>
                                <label class="radio" <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>><input <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>
                                                            type="radio" name="options" value="myViz" required=""> Select from my previous visualisations<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio"><input type="radio" name="options" value="demo" required=""> View a demonstration visualisation</label>

                            </div>
                            <g:if test="${params.isAdmin}">
                                <div class="col-sm-3 col-md-3 verticalLineLeft wizard-option-group">
                                    <h5>Administration</h5>
                                    <label class="radio"><input type="radio" name="options" value="treeAdmin" required=""> Tree administration</label>
                                    <label class="radio"><input type="radio" name="options" value="rematchAll" required=""> Rematch all trees</label>
                                </div>
                            </g:if>
                        </div>
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