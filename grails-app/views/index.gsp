<%--
 Created by Temi Varghese on 1/12/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Phylogenetic Toolbox</title>
    <meta name="layout" content="main"/>
    <r:require modules="jquery"/>
    <r:require modules="bugherd"/>
</head>

<body>

<h2 class="heading-medium">Phylolink</h2>

<div class="panel panel-default" style="min-height: 700px">
    <h4>Overview</h4>

    <div class="panel-body">

        <div class="row-fluid">
            <div class="span6">
                <div class="word-limit">
                    <p>
                        Phylolink is a collection of tools through which biodiversity can be explored from  a <a href="http://www.ala.org.au/what-is-phylogeny/">phylogenetic</a>    (or tree of life) perspective.
                    </p>

                    <p>
                        At the core of these tools is the ability to easily intersect a phylogenetic tree with species occurrence records, environmental data, and species character information.
                    </p>

                    <p>
                        The result is powerful ways of combining data to generate flexible and customisable visualisations, profiles and metrics for biodiversity.
                    </p>

                    <br/>

                    <div class="button-toolbar row-fluid">
                        <button type="button" style="height: 40px" class="btn btn-primary btn-lg btn-block"
                                onclick="window.location = '${createLink(controller: 'wizard', action: 'start')}'">Start Phylolink</button>
                    </div>
                    <br/>
                    <div class="button-toolbar row-fluid">
                        <button type="button" style="height: 40px" class="btn btn-primary btn-lg btn-block" onclick="window.location ='http://www.ala.org.au/explore-phylogenetic-diversity/'">Explore Phylogenetic Diversity</button>
                    </div>
                    <br/>

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