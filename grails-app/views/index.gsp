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
    <r:require modules="css"/>
</head>

<body>

<div class="border-bottom margin-left-3 margin-right-3">
    <h1>Phylolink</h1>
</div>

<div class="panel panel-default padding-left-3 padding-right-3">
    <h4>Overview</h4>

    <div class="panel-body">

        <div class="row-fluid">
            <div class="span6 padding-right-1">
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

                    <p>
                        View an example demonstration <a href="${createLink(controller: 'phylo',action: 'show')}/${demoId}"> here</a>. Or, view screencast on how to view phylolink <a href="#myModal" role="button"  data-toggle="modal">here</a>.
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

            <div class="span6 padding-left-1">
                <image src="${resource(dir: 'images', file: 'phylolink_promo.jpg')}"
            </div>
        </div>

    </div>
</div>

</div>
</div>
<div id="myModal" class="modal hide fade">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h3>How to use phylolink?</h3>
    </div>
    <div class="modal-body">
        <iframe width="100%" height="315" src="https://www.youtube.com/embed/_fN3Nn159Tw" frameborder="0" allowfullscreen="">
        </iframe>
    </div>
</div>
</body>
</html>