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
</head>

<body>
<div class="container">
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
                    <g:radioGroup name="options" values="['expertTrees', 'myTrees', 'myViz','addTree']"
                                  labels="['Expert suggested tree','My trees', 'My visualisations', 'Add a tree']" required="">
                        <label class="radio">${it.radio} ${it.label}</label>
                    </g:radioGroup>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <div name="back" class="btn" onclick="window.location = '/phylolink'"><i
                            class="icon icon-arrow-left"></i> Back</div>
                    %{--<input type="submit" value="Next" class="btn btn-primary right"/>--}%
                    <button type="submit" class="btn btn-primary" value="Next"><i
                            class="icon icon-white icon-arrow-right"></i> Next</button>
                </div>
            </div>
        </g:form>
    </div>
</div>
</body>
</html>