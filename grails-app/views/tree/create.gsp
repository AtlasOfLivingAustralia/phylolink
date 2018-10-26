<%--
 Created by Temi Varghese on 23/10/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:if test="${tree && tree.id}">
        <title>Edit tree - ${tree.title}</title>
    </g:if>
    <g:else>
        <title>Add tree</title>
    </g:else>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink "/>
</head>

<body>
<div class="container">
    <g:form action="save" class="form-horizontal">

        <g:if test="${tree && tree.id}">
            <h1>Edit phylogenetic tree - ${tree.title}</h1>
        </g:if>
        <g:else>
            <h1>Add phylogenetic tree</h1>
        </g:else>

        <fieldset class="form">
            <div class="row-fluid">
                <g:render template="form"/>
                <div class="control-group">
                    <div class="controls">
                        <g:if test="${tree && tree.id}">
                            <g:submitButton name="save" value="Save" class="btn btn-primary right"/>
                        </g:if>
                        <g:else>
                            <g:submitButton name="create" value="Create" class="btn btn-primary right"/>
                        </g:else>
                    </div>
                </div>
            </div>
        </fieldset>

    </g:form>
</div>
</body>
</html>