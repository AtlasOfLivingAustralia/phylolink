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
    <r:require modules="bootstrapApp"/>
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
                    <div class="text-center padding-top-1">
                        <button type="button" style="height: 40px; width: 80%" class="btn btn-primary btn-lg"
                                onclick="window.location = '${createLink(controller: 'wizard', action: 'start')}'">Start Phylolink</button>
                    </div>
                    <br/>
                    <div class="text-center">
                        <button type="button" style="height: 40px; width: 80%" class="btn btn-primary btn-lg" onclick="window.location ='http://www.ala.org.au/explore-phylogenetic-diversity/'">Explore Phylogenetic Diversity</button>
                    </div>
                    <br/>

                    <div class="well margin-top-3">
                        <h5>Collaborators and acknowledgement:</h5>

                        <p class="small">
                            Phylolink is the result of <a href="#" id="collaborationTip" data-toggle="popover" data-original-title="Phylolink Team">collaboration</a> between the Atlas of Living Australia and scientists at CSIRO, the Australian National University and the National Science Foundation (USA).
                            Phylolink builds upon <a href="http://trin.github.io/phyloJIVE/" target="_blank">PhyloJIVE</a>, which was developed by <a href="www.csiro.au" target="_blank">CSIRO</a>,
                            the Taxonomy Research and Information Network (<a href="http://www.taxonomy.org.au/" target="_blank">TRIN</a>), and the Centre for Australian National Biodiversity Research
                            (<a href="http://www.cpbr.gov.au/cpbr/" target="_blank">CANBR</a>).
                        </p>
                        <p class="small"><strong>Publications:</strong></p>
                            <ul>
                                <li><a href="http://bioinformatics.oxfordjournals.org/content/early/2014/01/31/bioinformatics.btu024" target="_blank">PhyloJIVE: Integrating biodiversity data with the Tree of Life</a></li>
                                <li><a href="http://www.researchgate.net/publication/262693905_Correcting_the_disconnect_between_phylogenetics_and_biodiversity_informatics" target="_blank">Correcting the disconnect between phylogenetics and biodiversity informatics</a></li>
                            </ul>

                    </div>
                </div>
            </div>

            <div class="span6 padding-left-1">
                <image src="${resource(dir: 'images', file: 'phylolink_promo.jpg')}"
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


<r:script disposition="defer">
    $('#collaborationTip').popover({
        html: true,
        content: '<ul>' +
        '<li>Developers - Temi Varghese, Adam Collins, Mark Chambers, Nick dos Remedios, Dave Martin</li>' +
        '<li>Advisors - Joe Miller, Craig Moritz, Dan Rosauer, Garry Jolley-Rogers</li>' +
        '<li>Coordinator - Rebecca Pirzl</li>' +
        '</ul>'
    });

    $(document).click(function (e) {
        if (e.target.id != "collaborationTip") {
            $('#collaborationTip').popover('hide');
        }
    });
</r:script>

</body>
</html>