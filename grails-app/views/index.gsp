<%--
 Created by Temi Varghese on 1/12/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Phylogenetic Toolbox</title>
    <meta name="layout" content="main"/>
    <r:require modules="jquery,bs3theme"/>
    <r:require modules="bugherd"/>
</head>

<body>

<h2 class="heading-medium">Application: PhyloLink</h2>

<div class="panel panel-default">
    <h4>Overview</h4>

    <div class="panel-body">

        <div class="row-fluid">
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

                    <br/>

                    <div class="button-toolbar row-fluid">
                        <button type="button" style="height: 50px" class="btn btn-primary btn-lg btn-block"
                                onclick="window.location = '${createLink(controller: 'wizard', action: 'start')}'">Start PhyloLink</button>
                    </div>
                    <br/>
                    <div class="button-toolbar row-fluid">
                        <button type="button" style="height: 50px" class="btn btn-primary btn-lg btn-block" onclick="window.location ='${createLink(controller: 'ala', action: 'pd')}'">Explore Phylogenetic Diversity</button>
                    </div>
                    <br/>

                    <h3 class="heading-small"><a href="#" onclick="$('#whatisPhylo').toggleClass('hide');
                    return false;">What is Phylogeny?</a></h3>

                    <div id="whatisPhylo" class="hide">

                        <p>
                            A phylogeny (or a tree of life) is a theory about how organisms are related to one another through evolutionary time. Phylogenies are based on the assumption that more closely related species will be more similar to one another, and they are commonly built using genetic sequences or physical characters. They are often visually represented as trees: the tips of the ever branching tree representing species, and the branches representing ‘evolutionary distance’ (e.g. length of time) from the ancestors from which they evolved.
                        </p>

                        <p>
                            The tools intersect species occurrence data with environmental layers and phylogenetic trees, enabling a variety of new perspectives on biodiversity. For example, you can investigate the environmental envelopes occupied by the species of any chosen clade (a group of related organisms sharing a common ancestral node). You can also measure and compare biodiversity for any given area/s in ways that account for both the number of species occurring there, and their evolutionary distinctness from one another, using phylogenetic diversity. The  tools will also allow you to map the spatial distribution of characters (e.g. waxy leaves) across the landscape.
                        </p>
                    </div>

                    <div class="well">
                        <h4 class="heading-xsmall">Collaborators and acknowledgement:</h4>

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
</body>
</html>