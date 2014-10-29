<%--
 Created by Temi Varghese on 16/10/2014.
--%>

<%@ page import="au.org.ala.phyloviz.PhylogeneticTree" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Add Phylogenetic Tree</title>
    <r:require modules="bootstrap"/>
</head>
<body>
    <div class="btn btn-primary" onclick="window.location = '${createLink(controller: 'phylogeneticTree', action: 'create')}'">
        <i class="icon icon-search"></i> Add tree
    </div>
<div class="btn">
    <i class="icon icon-search"></i> List all trees
</div>
</body>
</html>