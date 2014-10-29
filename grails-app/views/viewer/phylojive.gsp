<%--
  Created by IntelliJ IDEA.
  User: nick
  Date: 8/08/12
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="groovy.json.StringEscapeUtils; org.codehaus.groovy.grails.commons.ConfigurationHolder; grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'PhyloJive.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css/colorbox', file: 'colorbox.css')}" type="text/css" media="screen" />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.contextMenu.css')}" type="text/css" media="screen" />

    <script type="text/javascript" src="${resource(dir: 'js', file: 'jit.js')}"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.colorbox-min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.contextMenu.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jsphylosvg-min.js')}"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.3.3/underscore-min.js"></script>
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'phylolink.js')}"></script>
    <!--[if IE]><script language="javascript" type="text/javascript" src="${resource(dir: 'js', file: 'excanvas.js')}"></script><![endif]-->
    <script type="text/javascript">
        var tree = '', nexml = '', characters =[];
        $(document).ready(function() {
            var url = ""; //used
            $('#loadingMsg') .hide()  // hide it initially
            .ajaxStart(function() {
                $(this).show();
            })
            .ajaxStop(function() {
                $(this).hide();
            });
            // check if tree has to be downloaded from an url
            if( tree || nexml ){
                init_phylojive(tree, characters, url, nexml);
            } else {
                var treeurl = "${createLink(controller: 'phylo', action: 'getTree')}?studyId=${studyId}&treeId=${treeId}";

                $.ajax({
                    url: treeurl
                }).done(function( data ){
                    tree = data.tree
                    // smits parser does not like quotes in label. It goes into infinite loop.
                    tree = tree.replace(/\'/g, '');
                    // sometimes opentree interface returns something like "[pre-ingroup-marker]"
                    tree = tree.replace(/\[[^\]]+]/g,'');
                    init_phylojive(tree, characters, url);
                });
            }
        });

        function init_phylojive(tree, characters, url, nexml) {

            phylogenyExplorer_init({
                width: width || 900,
                height: height || 600,
                tree: tree,
                nexml: nexml,
                branchMultiplier: 0.1,
                charServiceUrl: url,
                character: characters,
                attribution:'',
                toolWidget: "tabs", // tabs || accordion
                codeBase: "${resource(dir: 'images')}",
                hideInput: true,
                mapParams: "&fq=data_provider_uid:dp36",
                presentClade: function(clade) {
                    var tmpl = st.config.tmpl, nodeList = [], node, html, split;
                    for (var i = 0;
                         ((i < clade.length) & (i < 30)); i++) {
                        node = {}
                        node.name = clade[i].name;
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
                    if (tmpl) {
                        tmpl = _.template(tmpl);
                        html = tmpl({
                            nodeList: nodeList
                        });
                    } else {

                    }
                    return html;

                }
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
    <div>
        <h2 id="loadingMsg" >Loading Tree...</h2>
        <div id="section">
            <div id="infovis"></div>
        </div>
    </div>
</div>
</body>
</html>