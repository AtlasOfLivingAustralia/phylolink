<%--
  Created by: Temi Varghese
  Date: 18/06/14
--%>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'PhyloJive.css')}" type="text/css" media="screen"/>
<link rel="stylesheet" href="${resource(dir: 'css/colorbox', file: 'colorbox.css')}" type="text/css" media="screen"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.contextMenu.css')}" type="text/css" media="screen"/>
<link rel="stylesheet" href="${resource(dir: 'css', file: 'phylolink.css')}" type="text/css"/>
<link rel="stylesheet" href="http://leafletjs.com/dist/leaflet.css" />
<script type="text/javascript" src="${resource(dir: 'js', file: 'jit.js')}"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.colorbox-min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.contextMenu.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'jsphylosvg-min.js')}"></script>
<script type="text/javascript"
        src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.3.3/underscore-min.js"></script>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min.js"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'PhyloLink.js')}"></script>
<script src="http://leafletjs.com/dist/leaflet.js"></script>
<!--[if IE]><script language="javascript" type="text/javascript" src="${resource(dir: 'js', file: 'excanvas.js')}"></script><![endif]-->
<script type="text/javascript">
    $(document).ready(function () {
        $('#phylolink-widgetpanel').width(width / 2);
        $('#phylolink-widgetpanel').height(height);
        $("#widgets-overlay").height(height);
        $("#phylolink-widgets").height(height);
        $("#widgets-overlay").width(width / 2);
        $("#phylolink-widgets").width(width / 2);
        $("#phylolink-widgetpanel").css('left', width / 2 + 'px');
        $("#phylolink-widgetpanel").css('top', '0px');
    })
</script>
<script type="text/javascript">
    function toggleProp(size, btn) {
        switch (size) {
            case 'half':
                $("#phylolink-widgetpanel").css('display', 'block');
                $('#phylolink-widgetpanel').width(width / 2);
                $('#phylolink-widgetpanel').height(height);
                $("#widgets-overlay").height(height);
                $("#phylolink-widgets").height(height);
                $("#widgets-overlay").width(width / 2);
                $("#phylolink-widgets").width(width / 2);
                $("#phylolink-widgetpanel").css('left', width / 2 + 'px');
                $("#phylolink-widgetpanel").css('top', '0px');
                break;
            case 'min':
                $("#phylolink-widgetpanel").css('display', 'none');
                break;
            case 'full':
                $("#phylolink-widgetpanel").css('display', 'block');
                $('#phylolink-widgetpanel').width(width);
                $("#widgets-overlay").width(width);
                $("#phylolink-widgets").width(width);
                $("#phylolink-widgetpanel").css('left', '0px');
                $("#phylolink-widgetpanel").css('top', '0px');
                break;

        }
        jQuery(btn.parentNode).find('.btn').removeClass('active');
        jQuery(btn).addClass('active');
    }

    %{--var treebaseurl = "${createLink(controller: 'phylo', action: 'getTree')}?studyId=${instance.studyid}&treeId=${instance.treeid}", tree = '';--}%
    var treebaseurl = "${createLink(controller: 'tree', action: 'getTreeNexml')}?id=${instance.studyid}", tree = '';
    %{--var treeIndex = ${instance.index};--}%
    var characters = [];
    var setup = false;

    $(function () {
        if (tree) {
            init_phylojive(tree, characters, '', '');
        } else {
            $.ajax({
                url: treebaseurl,
                success: function (nex) {
                    console.log(nex);
//                        nex = nex.tree;
                    // smits parser does not like quotes in label. It goes into infinite loop.
//                        nex = nex.replace(/\'/g, '');
                    // sometimes opentree interface returns something like "[pre-ingroup-marker]"
//                        nex = nex.replace(/\[[^\]]+]/g,'');
                    !setup && init_phylojive(null, characters, '', nex);
                    setup = true;
                }
            });
        }
    });

    function init_phylojive(tree, characters, url, nexml) {
        phylogenyExplorer_init({
            width: width,
            height: height,
            tree: tree,
//                treeIndex: treeIndex,
            nexml: nexml,
            branchMultiplier: 0.1,
            charServiceUrl: url,
            character: characters,
            toolWidget: "tabs", // tabs || accordion
            codeBase: "${resource(dir: 'images')}",
            hideInput: true,
            presentClade: function (clade) {
                var tmpl = st.config.tmpl, nodeList = [], node, html, split, names = [];
                for (var i = 0;
                     ((i < clade.length) & (i < 30)); i++) {
                    node = {}
                    node.name = clade[i].name;
                    names.push(node.name);
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
                widgets.load(names);
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
        init_map()
    } // end init_phylojive

    function objLength(obj) {
        var L = 0;
        $.each(obj, function (i, elem) {
            L++;
        });
        return L;
    }
</script>
<script>
    var updateMap;
    function init_map() {
        var wms, queryId;
        updateMap = function( names ){
            var fq = []
            wms?speciesLayers.removeLayer( wms ):null;

            names.forEach(function(value, index){
                names[index] = 'species:'+names[index].replace(' ','+')
            })
            fq = names.join(' OR ')
            console.log( fq )
            %{--$.ajax({--}%
%{--//                url:"http://biocache.ala.org.au/ws/webportal/params",--}%
                %{--url:"${createLink(controller: 'phylo', action: 'params')}",--}%
                %{--type:'POST',--}%
                %{--data:{--}%
                    %{--fq:fq--}%
                %{--},--}%
                %{--success:function( data ){--}%
                    %{--console.log('data type'+ data)--}%
                    %{--queryId = data--}%
                %{--},--}%
                %{--dataType:"html"--}%
            %{--});--}%
            wms= L.tileLayer.wms("http://biocache.ala.org.au/ws/mapping/wms/reflect?q="+fq, {
                layers: 'ALA:occurrences',
                format: 'image/png',
                transparent: true,
                attribution: "Atlas of Living Australia",
                bgcolor: "0x000000",
                outline: "true",
                ENV: "color:5574a6;name:circle;size:4;opacity:1"
            });
            wms.addTo( speciesLayers )
        }

        var speciesLayers = new L.LayerGroup();

        var map = L.map('recordMap', {
            center: [-23.6, 133.6],
            zoom: 2,
            layers: [speciesLayers]
        });

        var streetView = L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                    '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                    'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            id: 'examples.map-i875mjb7'
        }).addTo(map);

        var baseLayers = {
            "Street": streetView
        };

//        var overlays = {
//            "Macropus rufus": macropus,
//            "Acacia acuminata": acacia
//        };

//        L.control.layers(baseLayers, overlays).addTo(map);

        map.on('click', onMapClick);

        function onMapClick(e) {
            $.ajax({
                url: "http://biocache.ala.org.au/ws/occurrences/info",
                jsonp: "callback",
                dataType: "jsonp",
                data: {
                    q: "Macropus+rufus OR Acacia+acuminata",
                    zoom: "6",
                    lat: e.latlng.lat,
                    lon: e.latlng.lng,
                    radius: 20,
                    format: "json"
                },
                success: function (response) {
                    var popup = L.popup()
                            .setLatLng(e.latlng)
                            .setContent("<h3>Test</h3>Occurrences at this point: " + response.count)
                            .openOn(map);
                }
            });
        }
    }
</script>