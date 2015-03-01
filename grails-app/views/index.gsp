<%--
 Created by Temi Varghese on 1/12/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Phylogenetic Toolbox</title>
    <meta name="layout" content="main"/>
    <r:require modules="jquery,bs3theme"/>
</head>

<body>

%{--<!-- Breadcrumb -->--}%
%{--<ol class="breadcrumb">--}%
    %{--<li><a class="font-xxsmall" href="#">Home</a></li>--}%
    %{--<li class="font-xxsmall active">Application: PhyloLink</li>--}%
%{--</ol>--}%
<!-- End Breadcrumb -->
<h2 class="heading-medium">Application: PhyloLink</h2>

<!-- Alert Information -->
%{--<div class="alert alert-info alert-dismissible hidden-xs hidden-sm hidden-md" role="alert">--}%
    %{--<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>--}%
    %{--<strong>This page has multiple view settings.</strong> For best viewing results, select the&emsp; <i class="fa fa-desktop" title="Full width display"></i>&emsp; to swap to full screen. To reset your view, select&emsp; <i class="fa fa-tablet" title="Default display view"></i>&emsp;.--}%
%{--</div>--}%

<div class="panel panel-default">
    %{--<div class="panel-heading">--}%
        <h4>Overview</h4>
    %{--</div>--}%
    <div class="panel-body">

        <div class="row">
            <div class="span6">
                <div class="word-limit">
                    <p>
                        PhyloLink is a collection of tools through which biodiversity can be explored from  a phylogenetic (or tree of life) perspective.
                    </p>
                    <p>
                        At the core of these tools is the ability to easily intersect a phylogenetic tree with species occurrence records, environmental data, and species character information.
                    </p>
                    <p>
                        The result is powerful ways of combining data to generate flexible and customisable visualisations, profiles and metrics for biodiversity.
                    </p>

                    %{--<div class="alert alert-danger alert-dismissible">--}%
                        %{--<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>--}%
                        %{--<strong>Context needed.</strong> Call to action needed to explain why the user will interact with the buttons below. Note: this alert is dismissable.--}%
                    %{--</div>--}%

                    <div class="button-toolbar row">
                        <button type="button" class="btn btn-primary btn-lg btn-block span6" onclick="window.location ='${createLink(controller: 'wizard', action: 'start')}'">Start PhyloLink</button>
                        %{--<button type="button" class="btn btn-default btn-lg btn-block">Phylogenetic Diversity</button>--}%
                    </div>


                    %{--<div class="alert alert-danger alert-dismissible">--}%
                        %{--<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>--}%
                        %{--<strong>Context needed.</strong> I have included a title below to give the body text meaningful context. Note: this alert is dismissable.--}%
                    %{--</div>--}%
                    <h3 class="heading-small"><a href="#" onclick="$('#whatisPhylo').toggleClass('hide');return false;"> What is Phylogeny?</a></h3>
                    <div id="whatisPhylo" class="hide">

                    <p>
                        A phylogeny (or a tree of life) is a theory about how organisms are related to one another through evolutionary time. Phylogenies are based on the assumption that more closely related species will be more similar to one another, and they are commonly built using genetic sequences or physical characters. They are often visually represented as trees: the tips of the ever branching tree representing species, and the branches representing ‘evolutionary distance’ (e.g. length of time) from the ancestors from which they evolved.
                    </p>
                    <p>
                        The tools intersect species occurrence data with environmental layers and phylogenetic trees, enabling a variety of new perspectives on biodiversity. For example, you can investigate the environmental envelopes occupied by the species of any chosen clade (a group of related organisms sharing a common ancestral node). You can also measure and compare biodiversity for any given area/s in ways that account for both the number of species occurring there, and their evolutionary distinctness from one another, using phylogenetic diversity. The  tools will also allow you to map the spatial distribution of characters (e.g. waxy leaves) across the landscape.
                    </p>
                    </div>
                    <div class="well">
                        <h4 class="heading-xsmall">Collaborators and acknowledgement: </h4>
                        <p class="font-xsmall">These tools are the result of a collaboration between scientists, the creators of PhyloJIVE and the Atlas of Living Australia. The tools have been developed by Temi Varghese, Rebecca Pirzl, Adam Collins, Nick dos Remedios and Dave Martin, with advice from Joe Miller, Craig Moritz, Dan Rosauer and Garry Jolley-Rogers.</p>
                    </div>

                </div>
            </div>
            <div class="span6">
                <image src="${resource(dir: 'images', file: 'pjscreenshot.png')}"
            </div>
        </div>

    </div>
</div>

</div>
</div>
%{--<div class="container">--}%
    %{--<div class="span12">--}%
        %{--<p>--}%
            %{--PhyloJIVE and ALA’s Phylogenetic Toolbox are a collection of tools to explore phylogenetic--}%
            %{--perspectives on biodiversity.--}%
        %{--</p>--}%

        %{--<p>--}%
            %{--The tools cater for both novice and experienced users of phylogenies, and provide ready access to trees,--}%
            %{--visualisations, data summaries, and metrics.--}%
        %{--</p>--}%

        %{--<p>--}%
            %{--At the core of these tools is the ability to easily intersect a phylogenetic tree of choice with species--}%
            %{--occurrence records, environmental variables, and species trait information.--}%
        %{--</p>--}%

        %{--<p>--}%
            %{--The result is powerful ways of combining data and taking into account the degree of relatedness--}%
            %{--between species to improve our knowledge and understanding of biodiversity.--}%
        %{--</p>--}%
    %{--</div>--}%
    %{--<div class="span12">--}%
        %{--<div class="row-fluid">--}%
            %{--<div class="btn btn-primary" onclick="window.location ='${createLink(controller: 'wizard', action: 'start')}'">--}%
                %{--Visualise Trees--}%
            %{--</div>--}%
        %{--</div>--}%
        %{--<div class="row">--}%
            %{--<p></p>--}%
        %{--</div>--}%
        %{--<div class="row-fluid">--}%
            %{--<div class="btn" onclick="window.location = 'http://spatial-dev.ala.org.au'">--}%
                %{--Phylogenetic Diversity--}%
            %{--</div>--}%
        %{--</div>--}%
    %{--</div>--}%
    %{--<div class="row">--}%
        %{--<p></p>--}%
    %{--</div>--}%
    %{--<div class="span12 modal-footer">--}%
        %{--<p>--}%
            %{--These tools are the result of a collaboration of scientists and the ALA. PhyloJIVE--}%
            %{--(Phylogeny Javascript Information Visualiser and Explorer) was conceived by Garry Jolley-Rogers and--}%
            %{--Joe Miller and developed by Temi Varghese and Garry Jolley-Rogers as part of the--}%
            %{--Taxonomy Research & Information Network (TRIN).--}%
            %{--</p>--}%
        %{--<p>--}%
            %{--The ALA has contributed integrated web services including occurrence, environmental and trait data.--}%
            %{--Temi Varghese has been the lead developer, with contributions from Adam Collins and Nick dos Remedios.--}%
            %{--This work has been undertaken with advice from Joe Miller and Dan Rosauer.--}%
        %{--</p>--}%
    %{--</div>--}%
%{--</div>--}%
</body>
</html>