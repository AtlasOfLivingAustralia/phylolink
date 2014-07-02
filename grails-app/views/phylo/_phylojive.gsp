<%--
  Created by: nick
  Date: 8/08/12
  Time: 2:37 PM
  Modified by: Temi Varghese
  Date: 18/06/14
--%>
<%@ page import="groovy.json.StringEscapeUtils; org.codehaus.groovy.grails.commons.ConfigurationHolder; grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    %{--<title>Tree for .name}</title>--}%
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'PhyloJive.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/colorbox', file: 'colorbox.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.contextMenu.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}" type="text/css"/>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jit.js')}"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.colorbox-min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.contextMenu.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jsphylosvg-min.js')}"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.3.3/underscore-min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'PhyloLink.js')}"></script>
    <!--[if IE]><script language="javascript" type="text/javascript" src="${resource(dir: 'js', file: 'excanvas.js')}"></script><![endif]-->
    <script type="text/javascript">
        google.load("visualization", "1", {packages:["corechart"]});
        function Widgets(){
            this.widgetsList = [];
            this.add = function( widget ){
                this.widgetsList.push( widget );
            }
            this.load = function( data ){
                for( var wid in this.widgetsList ){
                    this.widgetsList[wid].load( data );
                }
            }
        }
        var widgets = new Widgets();
        function widget( wid, id, url ){
            var widgetid, widgeturl,wid;
            widgetid = id;
            widgeturl = url;
            wid = wid;
            return {
                load: function ( data ){
                    $.ajax({
                        url:widgeturl,
                        method:'GET',
                        data:{
                            speciesList : JSON.stringify( data ),
                            id: widgetid,
                            wid: wid
                        },
                        success: this.drawChart
                    });
                },
                drawChart: function ( result ) {
                    var data = google.visualization.arrayToDataTable( result.data );

                    var options = result.options;

                    var chart = new google.visualization.ColumnChart(document.getElementById( widgetid ));
                    chart.draw(data, options);
                }
            };
        }
        //        google.setOnLoadCallback(testChart);

    </script>
    <script type="text/javascript">
    function toggleProp( size , btn){
        switch ( size ){
            case 'half':
                $("#phylolink-widgets").width('50%');
                    $("#phylolink-widgets").show();
                    $("#phylolink-visualization").width('50%');
                    $("#phylolink-visualization").show();
                break;
            case 'min':
                $("#phylolink-widgets").hide();
                $("#phylolink-visualization").width('100%');
                $("#phylolink-visualization").show();
                break;
            case 'full':
                $("#phylolink-widgets").width('100%');
                $("#phylolink-widgets").show();

                $("#phylolink-visualization").width("0%");
                $("#phylolink-visualization").hide();

                break;

        }
        jQuery( btn.parentNode ).find('.btn').removeClass('active');
        jQuery( btn ).addClass('active');
    }
//        var tree = "(Thalassiosira weissflogii,(((Thalassiosira sp. A,(Thalassiosira gessneri,Thalassiosira lacustris)),((Thalassiosira sp. B,(((((((Thalassiosira brunii,(Thalassiosira cedarkeyensis,Tryblioptychus cocconeiformis)),Thalassiosira temperei),Thalassiosira transitoria),Thalassiosira kanayae),Thalassiosira hyperborea),Thalassiosira perispinosa),(((Thalassiosira californica,(Thalassiosira flexuosa,Thalassiosira grunowii)),Thalassiosira elliptipora),Thalassiosira yabei),Thalassiosira praeyabei)),(Thalassiosira marujamica,Thalassiosira orientalis))),(((Spicaticribra kingstonii,Cyclotella meneghiniana),Thalassiosira pseudonana),Conticribra tricircularis)));";

        %{--var treebaseurl = "${createLink(controller: 'phylojive', action: 'getTree')}";--}%
        var treebaseurl = "http://115.146.93.110:8000/api/v1/study/${instance.studyid}/tree/${instance.treeid}.tre", tree = '';
    var treeIndex = ${instance.index};
        var characters = [];
        var setup = false;

        $(function() {
            if( tree ){
                init_phylojive(tree, characters, '', '');
            }else {
                $.ajax({
                    url: treebaseurl,
                    method:'JSONP',
                    success:function( nex ){
//                        debugger;
//                    console.log('success!')
                        console.log( nex );
                        nex = nex.replace(/\'/g, '');
                        !setup && init_phylojive(nex, characters, '', '');
                        setup = true;
                    }
                });
            }
        });

        function init_phylojive(tree, characters, url, nexml) {
            //console.log("getkey", getKey(characters), getKey(characters[getKey(characters)]));
            phylogenyExplorer_init({
                width: 900,
                height: 600,
                tree: tree,
                treeIndex: treeIndex,
                nexml: nexml,
                branchMultiplier: 0.1,
                charServiceUrl: url,
                character: characters,
                %{--attribution:'<g:if test=".attribution}">".attribution.normalize().encodeAsHTML().replace('\n', '<br/>')?:''}"</g:if>',--}%
                toolWidget: "tabs", // tabs || accordion
                codeBase: "${resource(dir: 'images')}",
                hideInput: true,
                %{--<g:if test=".identifyLifeDataset =='acacia'}">mapParams: "&fq=data_provider_uid:dp36", // AVH only</g:if>--}%
                presentClade: function(clade) {
                    var tmpl = st.config.tmpl, nodeList = [], node, html, split, names =[];
                    for (var i = 0;
                         ((i < clade.length) & (i < 30)); i++) {
                        node = {}
                        node.name = clade[i].name;
                        names.push( node.name );
                        node.plus = clade[i].name.replace(/\s+/g, '+');
                        split = node.name.split(/\s+/);
                        if (split.length > 1) {
                            node.genus = split[0];
                            node.species = split[1];
                        } else {
                            node.species = split[0];
                        }
                        node.rel = node.species + '' + i;
                        node.index = i;
                        nodeList.push(node);
                    }
                    widgets.load( names );
                    if (tmpl) {
                        tmpl = _.template(tmpl);
                        html = tmpl({
                            nodeList: nodeList
                        });
                    } else {

                    }
                    return html;

                },
                //presentClade
                %{--tmpl: '.presentClade?.code?:defaultTmpl}'--}%
            });
            st.config.initCharacter = false;
        } // end init_phylojive

        function objLength(obj) {
            var L=0;
            $.each(obj, function(i, elem) {
                L++;
            });
            return L;
        }

    </script>
</head>
<body>
<div id="content">
    <header id="page-header">
        <div class="inner">
            %{--<button id="newTreeButton" class="btn" onclick="location.href='${createLink(controller:"tree", action:"create")}'">Create a new tree</button>--}%
            <phy:isLoggedIn>
            %{--<hf:ifGranted role="ROLE_ADMIN">--}%
                %{--<div style="color:#999;margin-bottom: 20px;">--}%
                    %{--Admin actions:--}%
                    %{--<g:link controller="tree" action="list" style="color:#999;font-size:12px;" class="btn btn-mini">Tree list</g:link>&nbsp;--}%
                    %{--<g:link controller="tree" action="edit" id=".id}" style="color:#999;font-size:12px;" class="btn btn-mini">Edit tree</g:link>--}%
                    %{--<g:link controller="tree" action="create" style="color:#999;font-size:12px;" class="btn btn-mini">Create a new tree</g:link>--}%
                %{--</div>--}%
            %{--</hf:ifGranted>--}%
            </phy:isLoggedIn>
        </div><!--inner-->
    </header>
    <div class="row-fluid">
        %{--<h2 id="loadingMsg" >Loading Tree...</h2>--}%
        <div id="section" class="span12">
            <div id="infovis"></div>
        </div>
    </div>
</div>
</body>
</html>