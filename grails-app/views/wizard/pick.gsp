<%--
 Created by Temi Varghese on 16/10/2014.
--%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main">
    <title>Add Phylogenetic Tree</title>
    <r:require modules="bootstrap"/>
</head>

<body>
<div class="container">
    <legend>Add or lookup a tree</legend>

    <div class="row-fluid">
        <g:form controller="wizard" action="pickMethod" method="POST">
            <div class="control-group">
                <div class="controls">
                    <g:radioGroup name="options" values="['addTree', 'searchTB','expertTrees','myTrees']"
                                  labels="['Add a tree', 'Import from TreeBase','Expert suggested tree','My trees']" required="">
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