<%--
 Created by Temi Varghese on 16/10/2014.
--%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>Start Phylolink</title>
    <meta name="breadcrumbParent" content="${g.createLink( controller: 'phylo', action: 'startPage')},Phylolink"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}" type="text/css" media="screen"/>
</head>
<body class="fluid" >
<div class="container"  >

    <h1>Select or create a visualisation</h1>
    <p style="font-size:14px; max-width: 60em">On this page, you can view published visualisations, view your previous visualisations, or
    create a new visualisation.  When creating a new visualisation, you can upload your own tree for visualisation, 
    or select a tree: from your previous tree uploads or from a recommended list.

	<g:if test="${noOfVizThatRequireMyAttention > 0}">
		<div class="row-fluid">
			<div class="alert alert-info alert-dismissible" role="alert">
				There are <a href="${createLink(controller: 'wizard', action: 'vizThatNeedMyAttention')}">${noOfVizThatRequireMyAttention} visualisation(s) that require your attention</a>
			</div>
		</div>
	</g:if>
	
    <div class="row-fluid">
        <g:form controller="wizard" action="pickMethod" method="POST">
            <div class="control-group">
                <div class="controls">
                        <div class="row-fluid">
                            <div class="col-sm-3 col-md-3 verticalLine wizard-option-group" >
                                <h4>Select a visualisation</h4>
                                <div style="padding-left:30px;">
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input <g:if test="${!loggedIn}">disabled</g:if> 
									type="radio" name="options" value="published" required=""> From the published visualisations
									<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio" <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>><input <g:if test="${numberOfVisualisations <= 0}">disabled</g:if>
                                                            type="radio" name="options" value="myViz" required=""> From my previous visualisations<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio"><input type="radio" name="options" value="demo" required=""> View a demonstration visualisation</label>
                                </div>
                            </div>
                            <div class="col-sm-3 col-md-3 wizard-option-group">
                                <h4>Create a visualisation with</h4>
                                <div style="padding-left:30px;">
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input <g:if test="${!loggedIn}">disabled</g:if>
                                                            type="radio" name="options" value="addTree" required=""> A new tree
                                    <g:if test="${!loggedIn}"> (login required)</g:if></input></label>
                                <label class="radio" <g:if test="${numberOfTrees <= 0}">disabled</g:if>><input <g:if test="${numberOfTrees <= 0}">disabled</g:if>
                                                                       type="radio" name="options" value="myTrees" required=""/> One of my previous trees<g:if test="${!loggedIn}"> (login required)</g:if></label>
                                <label class="radio" <g:if test="${!loggedIn}">disabled</g:if>><input type="radio" name="options" value="expertTrees" required="" <g:if test="${!loggedIn}">disabled</g:if>/> A recommended tree<g:if test="${!loggedIn}"> (login required)</g:if></label>

                                </div>
                            </div>
                            <g:if test="${isAdmin}">
                                <div class="col-sm-3 col-md-3 verticalLineLeft wizard-option-group" style="padding-left:50px;">
                                    <h4>Administration</h4>
                                    <label class="radio"><input type="radio" name="options" value="treeAdmin" required=""> Tree administration</label>
                                    <label class="radio"><input type="radio" name="options" value="rematchAll" required=""> Rematch all trees</label>
                                </div>
                            </g:if>
                        </div>
                </div>
            </div>
            <br/>
            <div class="control-group">
                <div class="controls">
                    <div name="back" class="btn btn-default" onclick="window.location = '${createLink(uri:'/')}'"><i
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