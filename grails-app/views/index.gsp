<%--
 Created by Temi Varghese on 1/12/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Phylogenetic Toolbox</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="container">
    <div class="span12">
        <p>
            PhyloJIVE and ALA’s Phylogenetic Toolbox are a collection of tools to explore phylogenetic
            perspectives on biodiversity.
        </p>

        <p>
            The tools cater for both novice and experienced users of phylogenies, and provide ready access to trees,
            visualisations, data summaries, and metrics.
        </p>

        <p>
            At the core of these tools is the ability to easily intersect a phylogenetic tree of choice with species
            occurrence records, environmental variables, and species trait information.
        </p>

        <p>
            The result is powerful ways of combining data and taking into account the degree of relatedness
            between species to improve our knowledge and understanding of biodiversity.
        </p>
    </div>
    <div class="span12">
        <div class="row-fluid">
            <div class="btn btn-primary" onclick="window.location ='${createLink(controller: 'wizard', action: 'start')}'">
                Visualise Trees
            </div>
        </div>
        <div class="row">
            <p></p>
        </div>
        <div class="row-fluid">
            <div class="btn" onclick="window.location = 'http://spatial-dev.ala.org.au'">
                Phylogenetic Diversity
            </div>
        </div>
    </div>
    <div class="row">
        <p></p>
    </div>
    <div class="span12 modal-footer">
        <p>
            These tools are the result of a collaboration of scientists and the ALA. PhyloJIVE
            (Phylogeny Javascript Information Visualiser and Explorer) was conceived by Garry Jolley-Rogers and
            Joe Miller and developed by Temi Varghese and Garry Jolley-Rogers as part of the
            Taxonomy Research & Information Network (TRIN).
            </p>
        <p>
            The ALA has contributed integrated web services including occurrence, environmental and trait data.
            Temi Varghese has been the lead developer, with contributions from Adam Collins and Nick dos Remedios.
            This work has been undertaken with advice from Joe Miller and Dan Rosauer.
        </p>
    </div>
</div>
</body>
</html>