//this is got statistics, popup window, select, expand / collapsed , rotate, setRoot
var labelType, useGradients, nativeTextSupport, animate;

(function () {
    var ua = navigator.userAgent,
        iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
        typeOfCanvas = typeof HTMLCanvasElement,
        nativeCanvasSupport = (typeOfCanvas === 'object' || typeOfCanvas === 'function'),
        textSupport = nativeCanvasSupport && (typeof
            document.createElement('canvas').getContext('2d').fillText === 'function');
    //I'm setting this based on the fact that ExCanvas provides text support for IE
    //and that as of today iPhone/iPad current text support is lame
    labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native' : 'HTML';
    nativeTextSupport = labelType === 'Native';
    useGradients = nativeCanvasSupport;
    animate = !(iStuff || !nativeCanvasSupport);
}());
var Log = {
    elem:false,
    write:function (text) {
        if (!this.elem) {
            this.elem = document.getElementById('log');
        }
        this.elem.innerHTML = text;
        //     this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
    }
};
var Nav = {
    elem:false,
    load:function (opt) {
        function $E(tag, props) {
            var elem = document.createElement(tag);
            for (var p in props) {
                if (typeof props[p] == "object") {
                    $jit.util.extend(elem[p], props[p]);
                } else {
                    elem[p] = props[p];
                }
            }
            return elem;
        }

        opt.codeBase = opt.codeBase || '';
        var popupHTML = '<div id="popup-close" style="position:relative; width:100%; background-color:lightblue"><a href="#" onclick="this.parentNode.parentNode.style.display=\'none\';" onmouseover="this.style.cursor=\'pointer\';" class="ui-dialog-titlebar-close ui-corner-all" role="button"><span class="ui-icon ui-icon-closethick">close</span></a></div><div id="popup-text"></div>';
        var navHTML = '<div style="position:relative"><div id="panup" style="position: absolute; left: 13px; top: 4px; width: 18px; height: 18px; cursor: pointer;"><img id="north" src="' + opt.codeBase + '/Extras/Phylojive/north-mini.png" /></div><div id="panleft" style="position: absolute; left: 4px; top: 22px; width: 18px; height: 18px; cursor: pointer;"><img id="west" src="' + opt.codeBase + '/Extras/Phylojive/west-mini.png" /></div><div id="panright" style="position: absolute; left: 22px; top: 22px; width: 18px; height: 18px; cursor: pointer;"><img id="east" src="' + opt.codeBase + '/Extras/Phylojive/east-mini.png" /></div><div id="pandown" style="position: absolute; left: 13px; top: 40px; width: 18px; height: 18px; cursor: pointer;"><img id="south" src="' + opt.codeBase + '/Extras/Phylojive/south-mini.png" /></div><div id="zoomout" style="position: absolute; left: 13px; top: 99px; width: 18px; height: 18px; cursor: pointer;"><img id="zoomOUT" src="' + opt.codeBase + '/Extras/Phylojive/zoom-minus-mini.png" /></div><div id="zoomworld" style="position: absolute; left: 13px; top: 81px; width: 18px; height: 18px; cursor: pointer;"><img id="world" style="position: relative; width: 18px; height: 18px;" src="' + opt.codeBase + '/Extras/Phylojive/zoom-world-mini.png"></div><div id="zoomin" style="position: absolute; left: 13px; top: 63px; width: 18px; height: 18px; cursor: pointer;"><img id="zoomIN" src="'
            + opt.codeBase
            + '/Extras/Phylojive/zoom-plus-mini.png" /></div><div style="position:absolute;left:-45px;top:123px;width:130px">Status:<br/><span id="log"></span></div></div>';

        var jitcontainer, rightJitContainer, centerJitContainer,
            id = typeof (opt.injectInto) == 'string' ? opt.injectInto : opt.injectInto.id,
            infovis, parent, popup, navigation, menu, border;

        //  this function is losing its meaning by adding this. just for now.
        //     var popupContainer = document.getElementById('center-jitcontainer');
        //     var popup = $jit.id('popup');
        //     popup.style.display = 'none';
        border = opt.width * 100 / 90;
        //console.log("border", opt.width, border);
        jitcontainer = $E('div', {
            'id':'jitcontainer',
            'className':'clearfix roundedCornerZ',
            'style':{
                'position':'relative',
                'width':opt.width + 'px',
                'height':opt.height + 'px'
            }
        });

        rightJitContainer = $E('div', {
            'id':'right-jitcontainer',
            'className':'phylojivepadding',
            'style':{
                display:'block',
                height:(opt.height + 0) + 'px'
            }
        });
        centerJitContainer = $E('div', {
            'id':'center-jitcontainer',
            'className':'phylojivepadding'
        });
        infovis = jQuery('#' + id)[0];

        parent = infovis.parentNode;
        parent.replaceChild(jitcontainer, infovis);
        //       parent.appendChild ( jitcontainer );
        centerJitContainer.appendChild(infovis);
        jitcontainer.appendChild(centerJitContainer);
        jitcontainer.appendChild(rightJitContainer);

        popup = $E('div', {
            'id':'popup',
            'className':'',
            'style':{
                'color':'black',
                'display':'none',
                'border':'1px solid green',
                'background-color':'#B5D397',
                'position':'absolute',
                'left':'50px',
                'top':'90px',
                //'width': '250px',
                //'height': '170px',
                'overflow':'auto',
                'text-align':'left'
            }
        });
        jQuery(popup).html(popupHTML);
        centerJitContainer.appendChild(popup);
        jQuery(popup).resizable({
            maxHeight:450,
            maxWidth:350,
            minHeight:250,
            minWidth:170
        });
        jQuery(popup).draggable({
            handle:'#popup-close',
            containment:'#' + opt.injectInto
        });

        navigation = $E('div', {
            'id':'navigationPanel',
            'style':{
                'left':(opt.width - (opt.width * 0.5)) + 'px'
            }
        });
        jQuery(navigation).html(navHTML);
        jitcontainer.appendChild(navigation);

        menu = $E('div', {
            'id':'jitmenubutton',
            'className':'menubutton'
        });
        jQuery(menu).click(function () {
            toggleScreen(this);
        });
        jitcontainer.appendChild(menu);

//        var am = jQuery('<div id="actionMenu">foo</div>'),
//            el1,
//            el2,
//            opts = {
//                info: "More Infos",
//                map:"Show Map",
//                expand:"Expand/Collapse Node",
//                rotate:"Rotate",
//                root:"Set Root"
//            };
//        el1 = jQuery('<ul></ul>').addClass("context-menu-list");
//        jQuery.each(opts, function(key) {
//            console.log("opts", key, opts[key]);
//            el2 = jQuery('<li></li>').addClass("context-menu-item").html('<span>'+opts[key]+'</span>').appendTo(el1);
//        });
//        el1.appendTo(am);
//        am.show().appendTo("#jitcontainer");
        jQuery('#infovis-canvas').on('dblclick', function() {
            alert('Double clicked');
        });
        //config right click context menu
        jQuery.contextMenu({
            selector: '#infovis-canvas', //'.node','#infovis-canvas'
            trigger: 'none',
            autoHide: false,
            build: function($trigger, e){
                return {
                    callback: function(key, options) {
                        var m = "clicked: " + key;
                        window.console && console.log(m, options.$trigger) || alert(m);
                    },
                    items: {
                        "chart": {
                            name: "Refresh Widgets",
                            callback: function(key, options) {
                                var node = $trigger.data("node"),
                                    names = [];
                                st.clickedNode = node;
                                node.eachSubgraph(function (elem) {
                                    if (elem.data.leaf) {

                                        names.push(elem.name);

                                    }

//                                    if (elem.data.leaf) {
//                                        if (leafs) {
//                                            leafs += "<li>" + elem.name + "</li>";
//                                        } else {
//                                            leafs = "<li>" + elem.name + "</li>";
//                                        }
//                                        selectedClade.push(elem);
//                                    }
                                });
                                widgets.load( names );
                                st.plot();
                            }
                        },
                        "info": {
                            name: "External Links",
                            callback: function(key, options) {
                                var node = $trigger.data("node"),
                                        leafs,
                                        selectedClade = [],
                                        popup = $jit.id('popup'),
                                        popupText = $jit.id('popup-text');
                                var pos = st.labels.getLabel(node.id);
                                var locx = parseInt(pos.style.left.replace(/px/, ''), 10) + 100;
                                var locy = parseInt(pos.style.top.replace(/px/, ''), 10) + 40;
                                st.clickedNode = node;
                                node.eachSubgraph(function (elem) {
                                    if (elem.data.leaf) {
                                        if (leafs) {
                                            leafs += "<li>" + elem.name + "</li>";
                                        } else {
                                            leafs = "<li>" + elem.name + "</li>";
                                        }
                                        selectedClade.push(elem);
                                    }
                                });

                                popup.style.display = 'inline';
                                popup.style.top = locy + 'px';
                                popup.style.left = locx + 'px';
                                popupText.innerHTML = st.config.presentClade(selectedClade);
                                st.config.onPresentClade();
                                st.plot();
                            }
                        },
                        "map": {
                            name: "View Map",
                            callback: function(key, options) {
                                var id = $trigger.data("nodeId");
                                var node = $trigger.data("node");
                                var name = jQuery("div#"+id).text().trim();
                                //console.log("map", name, options)
//                                loadMap(node, name, opt, false);
                                updateMap( ['Macropus rufus', 'Acacia Mulga'])
                            }//,
//                            disabled: function(key, options) {
//                                //var name = options.$trigger.text().trim();
//                                var id = $trigger.data("nodeId");
//                                var name = jQuery("div#"+id).text().trim();
//                                if (name.indexOf("Taxa") == -1) {
//                                    return false;
//                                } else {
//                                    return true;
//                                }
//                            }
                        },
                        "expandCollapse": {
                            name: "Expand/Collapse",
                            callback: function(key, options) {
                                var node = $trigger.data("node");
                                st.setCollapsed(node);
                                var level = st.nodesExpCol(node);
                                if (level) {
                                    st.zoomIndex = level;
                                }
                                st.computePositions(st.graph.getNode(st.root), '');
                                st.plot();
                            },
                            disabled: function(key, options) {
                                //var name = options.$trigger.text().trim();
                                var id = $trigger.data("nodeId");
                                var name = jQuery("div#"+id).text().trim();
                                if (name.indexOf("Taxa") > -1) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        },
                        "rotate": {
                            name: "Rotate",
                            callback: function(key, options) {
                                var node = $trigger.data("node");
                                st.computePositions(st.graph.getNode(st.root), 'start');
                                if (typeof node.data.rotate === "undefined") {
                                    node.data.rotate = false;
                                }
                                node.data.rotate = !node.data.rotate;
                                st.computePositions(st.graph.getNode(st.root), 'end');
                                st.fx.animate({
                                    modes:['linear', 'node-property:alpha'],
                                    onComplete:function () {
                                    }
                                });
                            },
                            disabled: function(key, options) {
                                var id = $trigger.data("nodeId");
                                var name = jQuery("div#"+id).text().trim();
                                if (name.indexOf("Taxa") > -1) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        },
                        "setRoot": {
                            name: "Set Root",
                            disabled: function(key, options) {
                                //var name = options.$trigger.text().trim();
                                var id = $trigger.data("nodeId");
                                var name = jQuery("div#"+id).text().trim();
                                //console.log("map disabled", name, options)
                                if (name.indexOf("Taxa") == -1) {
                                    return false;
                                } else {
                                    return true;
                                }
                            },
                            callback: function(key, options) {
                                var id = $trigger.data("nodeId");
                                st.setRoot(id, 'animate');
                                st.root = id;
                            }
                        }
                    }
                }
            }
        });
    }
};
var settingsPage, updateCharacter, onSetCharacter, onClickAlign, onSetRoot,
    onLateralise, isLateralise, onAnimate, onRender, onBranchLength, onCharRender,
    onBranchMultiply, onGetCharacter, st, toggleScreen;
var maxChars = 10;

updateCharacter = function (character) {
    if (!character) {
        return;
    }
    var options = '',
        unselected = '',
        i, name, selectedfirst = st.config.selectedCharacters [ 0 ] || false ,
        first = st.config.selectedCharacters [ 0 ] || false ,
        second = st.config.selectedCharacters [ 1 ] || false ,
        third = st.config.selectedCharacters [ 2 ] || false,
        select1 = '', select2 = '', select3 = '', selects = [];


    for (i = 0; i < character.length; i += 1) {
        var opts = [];
        name = character[i].replace(/_/g, ' ');

        for (var j = 0; j < st.config.selectedCharacters.length; j += 1) {
            //console.log("selectedCharacters", i, character[i], st.config.selectedCharacters[j]);
            if (character[i] === st.config.selectedCharacters[j]) {
                //console.log("selectedCharacters", i, j, character[i], st.config.selectedCharacters[j]);
                opts[i] = 'selected="selected"';
            }
        }
        // iterate over each character select (0 indexed)

        for (var k = 0; k < maxChars; k++) {
            var html = '<option ' + ((opts[k]) ? opts[k] : '') + ' value="' + character[i] + '" >' + name + '</option>';
            if (selects[k]) {
                selects[k] += html;
            } else {
                selects[k] = html;
            }
        }

        // old code below
        var opt1 = '', opt2 = '', opt3 = '';
        if (first !== false && first === character [ i ]) {
            opt1 = 'selected="selected"'
        }
        if (second !== false && second === character [ i ]) {
            opt2 = 'selected="selected"'
        }
        if (third !== false && third === character [ i ]) {
            opt3 = 'selected="selected"'
        }

        name = character[i].replace(/_/g, ' ');
        select1 += '<option ' + opt1 + ' value="' + character [ i ] + '" >' + name + '</option>';
        select2 += '<option ' + opt2 + ' value="' + character [ i ] + '" >' + name + '</option>';
        select3 += '<option ' + opt3 + ' value="' + character [ i ] + '" >' + name + '</option>';
    }

    // set each character's select HTML content
    for (var n = 0; n < maxChars; n++) {
        selects[n] = '<option value="">-- please select --</option>' + selects[n];
        jQuery('#character_' + (n + 1)).html(selects[n]);
    }

    if (select2) {
        select2 = '<option value=""> Please select </option>' + select2;
    }

    if (select3) {
        select3 = '<option value=""> Please select </option>' + select3;
    }

    jQuery('#firstCharacter').html(select1);
    jQuery('#secondCharacter').html(select2);
    jQuery('#thirdCharacter').html(select3);
};
settingsPage = function (opts) {
    var rightContainer = $jit.id('right-jitcontainer');
    var actionHtml = '<table><tr><td>Node Actions</td></tr><tr><td>Select</td>' +
        '<td><input id="selectClade" name="options" type="radio" checked></td></tr><tr><td>Expand / Collapse</td>' +
        '<td><div id="expandDiv"><input id="expand" name="options" type="radio"></div></td></tr><tr><td>Rotate</td>' +
        '<td><div id="rotateDiv"><input id="rotate" name="options" type="radio"></div></td></tr><tr><td>Set Root</td>' +
        '<td><input id="setRoot" name="options" type="radio" onclick="onSetRoot ( this );"></td></tr>' +
        '<!--<tr><td>Get Characters</td><td><input id = "character" name="options" type = "radio" onclick = "onGetCharacter ( this );"/></td></tr>--></table><br>' +
        '<table><tr><td>Tree Actions</td></tr><tr><td>Align Names</td><td><div id="settings">' +
        '<input id="alignName" type="checkbox" onclick="onClickAlign ( this )"></div></td></tr>' +
        '<tr><td>Ladderize</td><td><input id="lateralise" checked type="checkbox" onclick="onLateralise ( this );"></td></tr>' +
        '<tr><td>Animate</td><td><input id="animate" type="checkbox" checked onclick="onAnimate( this )"></td></tr>' +
        '<tr><td>Branch Length</td><td><input id="branchLength" type="checkbox" checked onclick="onBranchLength( this )"></td></tr>' +
        '<tr><td>Length Multiplier</td><td><label><input id="branchMultiplier" name="multiply" checked type="radio" onclick="onBranchMultiply( 0.1 )">' +
        '&nbsp;x0.1</label> <label><input name="multiply" id="branchMultiplier" type="radio" onclick="onBranchMultiply( 1 )">&nbsp;x1</label>' +
        '<label><input id="branchMultiplier" name="multiply" type="radio" onclick="onBranchMultiply( 2 )">&nbsp;x2</label>' +
        '<label><input id="branchMultiplier" name="multiply" type="radio" onclick="onBranchMultiply( 5 )">&nbsp;x5</label></td></tr>' +
        '<!--<tr><td>Selected Nodes</td><td><div id="selected"></div></td></tr>--></table>';
    var optionsHtml = '<table><tr><td>Tree Options</td></tr><tr><td>Align Names</td><td><div id="settings">' +
        '<input id="alignName" type="checkbox" onclick="onClickAlign ( this )"></div></td></tr>' +
        '<tr><td>Ladderize</td><td><input id="lateralise" checked type="checkbox" onclick="onLateralise ( this );"></td></tr>' +
        '<tr><td>Animate</td><td><input id="animate" type="checkbox" checked onclick="onAnimate( this )"></td></tr>' +
        '<tr><td>Branch Length</td><td><input id="branchLength" type="checkbox" checked onclick="onBranchLength( this )"></td></tr>' +
        '<tr><td>Length Multiplier</td><td><label><input id="branchMultiplier" name="multiply" checked type="radio" onclick="onBranchMultiply( 0.1 )">' +
        '&nbsp;x0.1</label> <label><input name="multiply" id="branchMultiplier" type="radio" onclick="onBranchMultiply( 1 )">&nbsp;x1</label>' +
        '<label><input id="branchMultiplier" name="multiply" type="radio" onclick="onBranchMultiply( 2 )">&nbsp;x2</label>' +
        '<label><input id="branchMultiplier" name="multiply" type="radio" onclick="onBranchMultiply( 5 )">&nbsp;x5</label>' +
        '</td></tr>';
        //'<tr><td>Load characters via Identify Life</td><td><input type="button" value="Load" onClick="remoteLoadCharacters(\'' + opts.charServiceUrl + '\', characters, IlDataset);"/></td></tr>' +
        //'<!--<tr><td>Selected Nodes</td><td><div id="selected"></div></td></tr>--></table>';
    if (opts.charServiceUrl) {
        optionsHtml += '<tr><td>Load characters via Identify Life</td>' +
            '<td><input type="button" id="loadChars" value="Load" onClick="remoteLoadCharacters(\'' + opts.charServiceUrl + '\', characters, IlDataset);"/>' +
            '<br/><div id="loadingMsg" style="display:none;">Loading... </div></td></tr>';
    }
    optionsHtml += '</table>';
    var characterHtml1 = '<p><label id="characterSelection">';
    for (var i = 1; i <= maxChars; i++) {
        characterHtml1 += 'character ' + i + ': <select id="character_' + i + '" onChange="onSetCharacter()"></select>';
        characterHtml1 += (i < maxChars) ? '<br/>' : '';
    }
    characterHtml1 += '</label></p>';
//    var characterHtml = '<p><label id="characterSelection" style="display:none;">First Characters: ' +
//        '<select id="firstCharacter" onChange="onSetCharacter ( )"> </select><br/>Second Chracter: ' +
//        '<select id ="secondCharacter" onChange="onSetCharacter ( )"></select><br/>Third Chracter: ' +
//        '<select id ="thirdCharacter" onChange="onSetCharacter ( )"></select></label></p>';
//    var characterHtml2 = '';
    var legendHtml = '<table id ="legend"><tbody id = "legendBody"><tr><th>Legend:</th><td></td></tr></tbody></table>';
    var searchHtml = '<table><tr><td>Search:</td><td><input id="searchString" type="text" size="15"></td></tr>' +
        '<tr><td></td><td><input class="foswikiButton" type="submit" id="next" value="next">' +
        '<input type="submit" id="previous" class="foswikiButton" value="previous"></td></tr></table>';
    var inputHtml = '<table><tr><td>Tree data:<br/> (newick)</td><td><textarea id="newickTree" rows="4" size="15"></textarea></td></tr>' +
        '<tr><td></td><td><input id="renderTree" type="submit" value="Render" onclick="onRender ( this )"></td></tr>' +
        '<tr><td>&nbsp;</td><td>&nbsp;</td></tr>' +
        '<tr><td>Characters:<br/> (<a href="http://wiki.trin.org.au/HubRIS/PhyloJive/CharJSON" target="_blank">CharJSON</a>)</td><td><textarea id="characterUpload" rows="4"  size="15"></textarea></td></tr>' +
        '<tr><td></td><td><input id="renderCharacters" type="submit" value="Import" onclick="onCharRender(this)"></td></tr></table>';
    var mapHtml = '<div id="mapTitle" style="font-weight:bold;"></div><div id="mapColourBy" style="display:none">Colour by: <select id="colourBy" onchange="updateMapColours(this);"><option value="species">Species</option><option>----</option></select></div>' +
        '<div id="recordMap" style="height: 250px"></div><div id="mapLegend"></div>';
    var attrHtml = "<p>PhyloJive was developed by the <a href=\"http://www.taxonomy.org.au/\" target=\"_blank\">Taxonomy Research &amp; Information Network (TRIN)</a> " +
        "with enhancments added by the <a href=\"http://www.ala.org.au/\" target=\"_blank\">Atlas of Living Australia (ALA)</a>. " +
        "Project genisis thanks to Garry Jolley-Rogers and Joe Miller. Software development by Temi Varghese, Paul Harvey and Nick dos Remedios.</p>" +
        "<h4>Attribution</h4>" + ((opts.attribution) ? opts.attribution : "");
    // assemble HTML for both widget types...
    var inputTabLine = "", inputTabEntry = "";
    if (!opts.hideInput) {
        inputTabEntry = '<li><a href="#tabInput">Input</a></li>';
        inputTabLine = '<div id="tabInput"><h4>Input Data</h4>' + inputHtml + '</div>';
    }
    var tabsContent = '<div id="tabs"><ul><li><a href="#tabAction">Options</a></li><li><a href="#tabCharacter">Character</a></li>' +
        '<li><a href="#tabLegend">Legend</a></li><li><a href="#tabMap">Map</a></li><li><a href="#tabSearch">Search</a></li>' +
        inputTabEntry + '<li><a href="#tabAbout">About</a></li></ul>' +
        '<div id="tabCharacter"><h4>Select Character</h4>' + characterHtml1 + '</div><div id ="tabAction"><h4>Actions</h4>' + optionsHtml + '</div>' +
        '<div id="tabLegend"><h4>Legend</h4>' + legendHtml + '</div>' +
        '<div id="tabMap"><h4>Map</h4>'+ mapHtml +'</div>' +
        '<div id="tabSearch"><h4>Search</h4>' + searchHtml + '</div>' +
        inputTabLine +
        '<div id="tabAbout"><h4>About</h4>' + attrHtml + '</div></div>';
    var accordionContent = '<div id="accordion"><h3><a href="#tabAction">Options</a></h3><div>' + optionsHtml + '</div>' +
        '<h3><a href="#tabCharacter">Characters</a></h3><div>' + characterHtml1 + '</div>' +
        '<h3><a href="#tabLegend">Legend</a></h3><div>' + legendHtml + '</div>' +
        '<h3><a href="#tabMap">Map</a></h3><div>' + mapHtml + '</div>' +
        '<h3><a href="#tabSearch">Search</a></h3><div>' + searchHtml + '</div>' +
        '<h3><a href="#tabInput">Input</a></h3><div>' + inputHtml + '</div>' +
        '<h3><a href="#tabAbout">About</a></h3><div>' + attrHtml + '</div></div>';

    if (opts.toolWidget == "accordion") {
        rightContainer.innerHTML += accordionContent;
        jQuery('#accordion').accordion({autoHeight:false, fillSpace:true});
    } else {
        rightContainer.innerHTML += tabsContent;
        jQuery('#tabs').tabs();
        // fix height of tabs body
        var tabMenuH = jQuery('.ui-tabs-nav').height();
        jQuery('.ui-tabs-panel').height(opts.height - (tabMenuH + 35));  // fudge factor empirically determined
    }
};
var phylojive = (function () {
    var tree, character;
    return {
        drawTree:function (newickTree) {
            if (typeof newickTree === 'undefined') {
                alert('tree is not defined.');
                return;
            }
            var json, legendElem;
            Smits.NewickParse(newickTree);
            this.tree = Smits.getRoot().json();
            st.loadJSON(this.tree);
            st.compute();
            st.config.initCharacter = false;
            legendElem = $jit.id('legend');
            if (st.character) {
                html = st.colorCharacter() || '';
                jQuery('#legendBody').html(html);
                legendElem.style.display = 'inline';
                updateCharacter(st.characterList);
            } else {
                legendElem.style.display = 'none';
            }
            st.onClick(st.root);
            st.fitScreen();
        },
        drawCharacter:function () {
        }
    }
})();

function loadMap(node, name, opt, characterType) {
    var extraParams =  (opt.mapParams) ? opt.mapParams : '';
    //console.log("st", st);
    if (name.indexOf("Taxa") == -1) {
        // single taxon map
        jQuery("#mapTitle").html("Records for " + name);
        var imgUrl = "http://biocache.ala.org.au/ws/density/map?q="
            + name.split(/\s+/).join('+') + extraParams;
        var html = '<a class="mapImg" href="' + imgUrl + '"><img class="tipImage" src="' + imgUrl + '"/></a>';
        jQuery("#recordsMap").html(html);
        jQuery("#accordion").accordion("activate", 3 );
        jQuery("#tabs").tabs("select", 3 );
        jQuery("#mapLegend").html("");
        jQuery("a.mapImg").colorbox({photo: true});
    } else if (node && node.data && node.data.leaves > 32) {
        alert("Too many taxa to map, choose a node with up to 32 taxa.");
    } else {
        // mulitple taxa map

        if (characterType && characterType != "species") {
            var selectedClade = [], colourList = [], temp, value, index, charEl, hex = "";
            //var list = st.config.selectedCharacters;
            //var char = list[0];
            var char = characterType;
            var values = []; //node.data.character[char]; // array
            var legendMap = {}, categoryMap = {};
            var legendCharName = char.replace(/_/g," "); //
            st.clickedNode = node;
            node.eachSubgraph(function (elem) {
                if (elem.data.leaf) {
                    selectedClade.push(elem.name);
                    // determine colour for first character selected
                    var charTypeMapping = st.charTypeMapping;
                    values = elem.data.character[char];

                    if (!values) {
                        // no character data for this taxon
                        hex = "000000";
                        //legendMap.none = hex;
                        value = "undefined";
                        legendMap[hex] = value;
                        colourList.push(hex);
                        categoryMap[hex] = createObj(categoryMap[hex], legendCharName + " - " + value, elem.name);
                    } else if (charTypeMapping[char] === st.config.typeEnum.quali) {
                        // qualitative character type
                        temp = st.colorCoding[char];
                        value = values[0];
                        if (values.length > 1) {
                            value = 'multiple';
                        }
                        charEl = temp[value];
                        hex = (charEl) ? colourNameToHex(charEl.color) : "000000";
                        hex = hex.replace("#","");
                        legendMap[hex] = value;
                        colourList.push(hex);
                        categoryMap[hex] = createObj(categoryMap[hex], legendCharName + " - " + value, elem.name);
                    } else if (charTypeMapping[char] === st.config.typeEnum.quant) {
                        // quantitative character type
                        temp = st.colorCodingQuali[char];
                        value = values[0];
                        index = st.findIndex(value, st.range[char]);
                        charEl = temp[index];
                        hex = (charEl) ? charEl.color : "000000";
                        hex = hex.replace("#","");
                        legendMap[hex] = value;
                        colourList.push(hex);
                        categoryMap[hex] = createObj(categoryMap[hex], legendCharName + " - " + value, elem.name);
                    }
                }
            });
            //console.log("check", selectedClade, colourList);
            jQuery("#mapTitle").html("Records for " + name);
            var imgUrl = "http://biocache.ala.org.au/ws/density/map?q=*:*&forceRefresh=true&forcePointsDisplay=true&colourByFq=taxon_name:"
                + selectedClade.join(',taxon_name:') + '&colours=' + colourList.join(",") + extraParams;
            //var html = '</p><img class="tipImage" src="http://biocache.ala.org.au/ws/density/map?forceRefresh=true&forcePointsDisplay=true&colourByFq=taxon_name:'
            //    + selectedClade.join(',taxon_name:') + '&colours=' + colourList.join(",") + extraParams + '"/>';
            var html = '<a class="mapImg" href="' + imgUrl + '"><img class="tipImage" src="' + imgUrl + '"/></a>';
            jQuery("#recordsMap").html(html);
            // display the legend
            var legend = "<div style='font-weight:bold;'>Legend: " + legendCharName + "</div>";
            //    <div class=\"box\" style=\"background-color:BlueViolet;\"></div>"
            var legendList = colourList.sort();
            //remove duplicates
            var seen = {}, legendSet = [];
            for (var j in legendList) {
                var val = legendList[j];
                if (!seen[val]) {
                    legendSet.push(val);
                    seen[val] = true;
                }
            }
            for (var n in legendSet) {
                legend += "<div class=\"box inline\" style=\"background-color:#" + legendSet[n] + ";\"></div>&nbsp;" + legendMap[legendSet[n]] + "<br/>";
            }
            jQuery("#mapLegend").html(legend);
            jQuery("#mapColourBy").show();
            // spatial portal link
            var spatialUrl = "http://spatial.ala.org.au/?" + getSPParams(categoryMap);
            jQuery("#recordsMap").append("<br/><a href='" + spatialUrl + "' target='sp'>View in Spatial Portal</a>");

        } else {
            // colour by taxa
            var selectedClade = [], taxa = [],categoryMap = {};
            node.eachSubgraph(function (elem) {
                if (elem.data.leaf) {
                    selectedClade.push(elem.name);
                    taxa.push(elem.name.replace(/\s+/g,'+'));
                }
            });
            //console.log("pallette", colourScheme.colours, colourScheme.getColourForIndex(20));
            //var colours = colourScheme.colours.splice(0, selectedClade.length);
            var allColours = ["3366CC","DC3912","FF9900","109618","990099","0099C6","DD4477","66AA00","B82E2E","316395","994499","22AA99","AAAA11","6633CC","E67200","8B0707","651067","329262","5574A6","3B3EAC","B77322","16D620","B91383","F43595","9C5935","A9C413","2A778D","668D1C","BEA413","0C5922","743411", "000000"];
            var colours = allColours.splice(0, selectedClade.length);
            jQuery("#mapTitle").html("Records for " + name);
            var imgUrl = "http://biocache.ala.org.au/ws/density/map?q=*:*&forceRefresh=true&forcePointsDisplay=true&colourByFq=taxon_name:"
                + taxa.join(',taxon_name:') + '&colours=' + colours.join(",") + extraParams;
            //var html = '<img class="tipImage" src="http://biocache.ala.org.au/ws/density/map?forceRefresh=true&forcePointsDisplay=true&colourByFq=taxon_name:'
            //    + selectedClade.join(',taxon_name:') + '&colours=' + colours.join(",") + extraParams + '"/>';
            var html = '<a class="mapImg" href="' + imgUrl + '"><img class="tipImage" src="' + imgUrl + '"/></a>';
            jQuery("#recordsMap").html(html);
            // Add legend
            var legend = "<div style='font-weight:bold;'>Legend: Species</div>";
            for (var n in colours) {
                legend += "<div class=\"box inline\" style=\"background-color:#" + colours[n] + ";\"></div>&nbsp;" + selectedClade[n] + "<br/>";
                categoryMap[colours[n]] = createObj(categoryMap[colours[n]], selectedClade[n], selectedClade[n]);
            }
            jQuery("#mapLegend").html(legend);
            jQuery("#mapColourBy").show();
            jQuery("#colourBy").val("species"); // reset select to default
            // spatial portal link
            var spatialUrl = "http://spatial.ala.org.au/?" + getSPParams(categoryMap);
            jQuery("#recordsMap").append("<br/><a href='" + spatialUrl + "' target='sp'>View in Spatial Portal</a>");
        }

        // store node & nodeId in dom
        jQuery("#colourBy").data("node", node);
        jQuery("#colourBy").data("name", name);
        jQuery("#colourBy").data("opt", opt);
        jQuery("a.mapImg").colorbox({photo: true});
        // update colourBy dropdown with characters
        if (jQuery("#colourBy option").length <= 2) {
            for (var c in st.characterList) {
                var char = st.characterList[c];
                jQuery("#colourBy").append("<option value='" + char + "'>" + char.replace(/_/g,' ') + "</option>");
            }
        }

        jQuery("#accordion").accordion("activate", 3 );
        jQuery("#tabs").tabs("select", 3 );
    }

    function getSPParams(categoryMap) {
        var params = "", index = 1;
        //console.log("categoryMap", categoryMap);
        jQuery.each(categoryMap, function(key, val) {
            if (index > 1) params += "&";
            params += "ly." + index + "=" + val.name;
            params += "&ly." + index + ".s=0x" + key;
            params += "&ly." + index + ".q=" + val.taxa.join(",");
            index++;
        });
        //console.log("params", params);
        return params;
    }

    function createObj(obj, label, taxa) {
//        if (!categoryMap[hex]) categoryMap[hex] = {};
//        categoryMap[hex].name = legendCharName + " - " + value;
//        if (!categoryMap[hex].taxa) categoryMap[hex].taxa = [];
//        categoryMap[hex].taxa.push(elem.name);
        if (obj && typeof obj === 'object') {
            obj.taxa.push(taxa);
            if (!obj.name) obj.name = label;
        } else {
            obj = {
                name: label,
                taxa: [taxa]
            };
        }
        return obj;
    }
}

function remoteLoadCharacters(url, characters, dataset) {
    //console.log("remoteLoadCharacters", url, characters, dataset);
    jQuery("#loadingMsg").show();
    jQuery("#loadChars").attr("disabled","disabled");

    var nodes = [];
    var root = st.graph.getNode(st.root);
    var allChars = jQuery.extend(true, {}, characters) || {}; // copy not reference it
    // iterate over tree and extract names
    root.eachSubgraph(function (node) {
        if (node.name) {
            var sciName = node.name.replace(/\s+\d+/,""); // strip off year strings
            nodes.push(sciName);
            // create stub for all names with no existing props
            if (!allChars[node.name]) {
                allChars[node.name] = {};
            }
        }
    });
    var joinedNames = nodes.join(";").replace(/\s+/g, "+");
    //url += "?names=" + joinedNames + "&dataset=" + dataset;
    var data = {
        names: joinedNames,
        dataset: dataset
    }
    //jQuery.getJSON(url, function(data) {
    jQuery.post(url, data, function(data) {
        var propObj = {}, maxProps = 0, oldChars = {}, charsFound;
        jQuery.each(data, function(key, el) {
            // need to initialize it here. otherwise it remembers value from previous iteration.
            charsFound = false;
            //oldChars[key] = jQuery.extend(true, {}, allChars[key] ); //copy it
            //console.log("key:", key, el, oldChars[key], allChars[key]);
            jQuery.extend(allChars[key], el); // merge props
            if (el) charsFound = true;
            // (do once) build a list of all (merged) properties
            if (charsFound && objLength(propObj) <= 0 && allChars[key]) {
                maxProps = objLength(allChars[key]);
                jQuery.each(el, function(k, v) {
                    //propList.push(k);
                    propObj[k] = [];
                });

            }
        });
        // normalise the properties for all taxa
        jQuery.each(allChars, function(key, el) {
            //console.log("newChars check",key,el,objLength(el),maxProps);
            //console.log("char's length", el.length, maxProps);
            if (objLength(el) < maxProps) {
                //console.log("mismatch", key);
                jQuery.extend(true, el, propObj);
            }
        });
        // determine the first character for first taxa
//        var firstChar;
//        var getFirstKey = function (data) {
//            for (var prop in data)
//                return prop;
//        }
//
//        for (var key in allChars) {
//            if (allChars[key]) {
//                firstChar = getFirstKey(allChars[key]);
//                break;
//            }
//        }
//
//        // load chars into PJ
//        st.firstCharacter = firstChar || false; //firstChar;
        st.config.initCharacter = false;
        st.character = allChars || {}; // st.characterList ???
        //console.log("st 2", st);
        if (charsFound && st.character) {
            var html = st.colorCharacter() || '';
            jQuery('#legendBody').html(html);
            //legendElem.style.display = 'inline';
            jQuery('#legend').show();
            //st.findAllCharTypes(st.graph.getNode(st.root));
            updateCharacter(st.characterList);
            //console.log("triggering navigate:", window.location.hash.slice(1));
            var hashString = window.location.hash.slice(1);
            // we need the URL to change in order to get navigate to trigger -> toggle first hash value
            var fiddleHash1 = (hashString.indexOf("characters") > -1) ? "character" : "characters";
            var fiddleHash2 = (hashString.indexOf("characters") > -1) ? "characters" : "character";
            window.AppRouter.navigate(hashString.replace(fiddleHash2, fiddleHash1),{trigger: true, replace: true});
            st.fitScreen();
            alert("characters were loaded successfully");
            // add IL to attribution tab
            var attr = "<p>Additional character data provided by <a href=\"http://www.identifylife.org/\" target=\"_blank\">Identify Life</a>.</p>";
            jQuery("#tabAbout").append(attr);
        } else {
            alert("No characters were found for this tree");
            jQuery('#legend').hide();
        }
    }, "json").error(function(jqXHR, textStatus, errorThrown){
        alert("getJSON Error: " + jqXHR.responseText.substring(0, Math.min(500,jqXHR.responseText.length)));
    }).complete(function() {
        jQuery("#loadingMsg").hide();
        jQuery("#loadChars").removeAttr("disabled");
    });
}

function updateMapColours(el) {
    //alert("foo: " + jQuery(el).val());
    var character = jQuery(el).val();
    var node = jQuery(el).data("node");
    var name = jQuery(el).data("name");
    var opt = jQuery(el).data("opt");
    //console.log("loadMap args", node, name, opt, character );
    loadMap(node, name, opt, character);
}

/**
 * Convert color name to hex code. Borrowed from http://stackoverflow.com/a/1573141/249327
 *
 * @param colour
 * @return {*}
 */
function colourNameToHex(colour) {
    var colours = {"aliceblue":"#f0f8ff","antiquewhite":"#faebd7","aqua":"#00ffff","aquamarine":"#7fffd4","azure":"#f0ffff",
        "beige":"#f5f5dc","bisque":"#ffe4c4","black":"#000000","blanchedalmond":"#ffebcd","blue":"#0000ff","blueviolet":"#8a2be2","brown":"#a52a2a","burlywood":"#deb887",
        "cadetblue":"#5f9ea0","chartreuse":"#7fff00","chocolate":"#d2691e","coral":"#ff7f50","cornflowerblue":"#6495ed","cornsilk":"#fff8dc","crimson":"#dc143c","cyan":"#00ffff",
        "darkblue":"#00008b","darkcyan":"#008b8b","darkgoldenrod":"#b8860b","darkgray":"#a9a9a9","darkgreen":"#006400","darkkhaki":"#bdb76b","darkmagenta":"#8b008b","darkolivegreen":"#556b2f",
        "darkorange":"#ff8c00","darkorchid":"#9932cc","darkred":"#8b0000","darksalmon":"#e9967a","darkseagreen":"#8fbc8f","darkslateblue":"#483d8b","darkslategray":"#2f4f4f","darkturquoise":"#00ced1",
        "darkviolet":"#9400d3","deeppink":"#ff1493","deepskyblue":"#00bfff","dimgray":"#696969","dodgerblue":"#1e90ff",
        "firebrick":"#b22222","floralwhite":"#fffaf0","forestgreen":"#228b22","fuchsia":"#ff00ff",
        "gainsboro":"#dcdcdc","ghostwhite":"#f8f8ff","gold":"#ffd700","goldenrod":"#daa520","gray":"#808080","green":"#008000","greenyellow":"#adff2f",
        "honeydew":"#f0fff0","hotpink":"#ff69b4",
        "indianred ":"#cd5c5c","indigo ":"#4b0082","ivory":"#fffff0","khaki":"#f0e68c",
        "lavender":"#e6e6fa","lavenderblush":"#fff0f5","lawngreen":"#7cfc00","lemonchiffon":"#fffacd","lightblue":"#add8e6","lightcoral":"#f08080","lightcyan":"#e0ffff","lightgoldenrodyellow":"#fafad2",
        "lightgrey":"#d3d3d3","lightgreen":"#90ee90","lightpink":"#ffb6c1","lightsalmon":"#ffa07a","lightseagreen":"#20b2aa","lightskyblue":"#87cefa","lightslategray":"#778899","lightsteelblue":"#b0c4de",
        "lightyellow":"#ffffe0","lime":"#00ff00","limegreen":"#32cd32","linen":"#faf0e6",
        "magenta":"#ff00ff","maroon":"#800000","mediumaquamarine":"#66cdaa","mediumblue":"#0000cd","mediumorchid":"#ba55d3","mediumpurple":"#9370d8","mediumseagreen":"#3cb371","mediumslateblue":"#7b68ee",
        "mediumspringgreen":"#00fa9a","mediumturquoise":"#48d1cc","mediumvioletred":"#c71585","midnightblue":"#191970","mintcream":"#f5fffa","mistyrose":"#ffe4e1","moccasin":"#ffe4b5",
        "navajowhite":"#ffdead","navy":"#000080",
        "oldlace":"#fdf5e6","olive":"#808000","olivedrab":"#6b8e23","orange":"#ffa500","orangered":"#ff4500","orchid":"#da70d6",
        "palegoldenrod":"#eee8aa","palegreen":"#98fb98","paleturquoise":"#afeeee","palevioletred":"#d87093","papayawhip":"#ffefd5","peachpuff":"#ffdab9","peru":"#cd853f","pink":"#ffc0cb","plum":"#dda0dd","powderblue":"#b0e0e6","purple":"#800080",
        "red":"#ff0000","rosybrown":"#bc8f8f","royalblue":"#4169e1",
        "saddlebrown":"#8b4513","salmon":"#fa8072","sandybrown":"#f4a460","seagreen":"#2e8b57","seashell":"#fff5ee","sienna":"#a0522d","silver":"#c0c0c0","skyblue":"#87ceeb","slateblue":"#6a5acd","slategray":"#708090","snow":"#fffafa","springgreen":"#00ff7f","steelblue":"#4682b4",
        "tan":"#d2b48c","teal":"#008080","thistle":"#d8bfd8","tomato":"#ff6347","turquoise":"#40e0d0",
        "violet":"#ee82ee",
        "wheat":"#f5deb3","white":"#ffffff","whitesmoke":"#f5f5f5",
        "yellow":"#ffff00","yellowgreen":"#9acd32"};

    if (typeof colours[colour.toLowerCase()] != 'undefined')
        return colours[colour.toLowerCase()];

    return colour;
}

/**
 * Get a colour value (hex) by its index value in an array (32 elements)
 */
var colourScheme = {
    colours: ["3366CC","DC3912","FF9900","109618","990099","0099C6","DD4477","66AA00","B82E2E","316395","994499","22AA99","AAAA11","6633CC","E67200","8B0707","651067","329262","5574A6","3B3EAC","B77322","16D620","B91383","F43595","9C5935","A9C413","2A778D","668D1C","BEA413","0C5922","743411", "000000"],
    //colours: this.pallette.concat(this.pallette),
    getColourForIndex: function(index) {
        var hexCode = "";

        if (this.isInteger(index) && index < this.colours.length) {
            hexCode = this.colours[index];
        } else {
            hexCode = this.colours[0];
        }

        return hexCode;
    },
    isInteger: function (value) {
        if ((parseFloat(value) == parseInt(value)) && !isNaN(value)) {
            return true;
        } else {
            return false;
        }
    }
}

function smitsNode2JSON(node) {
    var childJSON = [];
    var leaves = 0;
    for (var i = 0; i < node.children.length; i++) {
        var j = smitsNode2JSON(node.children[i])
        childJSON.push(j);
        leaves += j.data.leaf;
        leaves += j.data.leaves;
    }
    var that = node;
    var sampleid = '';
    if (childJSON.length !== 0) {
        return {
            "id":node.id,
            "name":node.name,
            "data":{
                'leaves':leaves,
                'leaf':0,
                'len':node.len,
                '$type':'circle',
                '$dim':5,
                '$color':'#fff'
            },
            "children":childJSON
        };
    } else {
        node.name = node.name.replace(/_/g, ' ');
        var sampleArray = node.name.split(' ');
        if (sampleArray.length > 1) {
            sampleid = sampleArray[1];
        }
        var name = sampleArray[0];
        var nodeJSON = {
            "id":node.id,
            "name":node.name,
            "data":{
                'leaves':0,
                'leaf':1,
                'len':node.len,
                '$height':20,
                '$type':'none',
                'sampleid':sampleid,
                'name':name
            },
            "children":childJSON
        };
        return nodeJSON;
    }
}
function phylogenyExplorer_init(initial) {
    var config = {
        //id of viz container element
        injectInto:'infovis',
        width:800,
        height:600,
        offsetX:0,
        align:'left',
        alignName:false,
        lateralise:true,
        branchLength:true,
        branchMultiplier:1,
        duration:1000,
        fps:10,
        //set animation transition type
        transition:$jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance:20,
        levelsToShow:Number.MAX_VALUE,
        constrained:false,
        firstCharacter:'Raceme_length_median',
        //enable panning
        Navigation:{
            enable:true,
            panning:'avoid nodes',
            zooming:50
        },
//        Tips:{
//            enable:true,
//            onShow:function (div, node) {
//                var url = '',
//                    key, i, char, html = '',
//                    name = '',
//                    index;
//                if (node.data.leaf) {
//                    name = "<h3>" + node.name + "</h3>";
//                    //url = '<img class="tipImage" src="http://biocache.ala.org.au/ws/density/map?q='
//                    //    + node.name.split(/\s+/).join('+') + '"/>';
//                }
//                //         div.innerHTML = node.name;
//                // display all characters
//                var result = [];
//                for (index in st.config.selectedCharacters) {
//                    //           if ( node.data.character.hasOwnProperty ( key ) ) {
//                    key = st.config.selectedCharacters[index];
//                    char = node.data.character[key];
//                    html = '<strong>' + key + '</strong>: ';
//                    if (typeof char === 'undefined' || char.length === 0 || typeof char[0] === 'undefined') {
//                        html += '&mdash;';
//                    } else if (typeof char[0] !== 'number') {
//                        html += char.join(',');
//                    } else {
//                        html += char[0].toFixed(4);
//                    }
//                    result.push(html);
//                    //           }
//                }
//                result.push('[click for actions &amp; more info]');
//                div.innerHTML = name + result.join('<br/>') + url;
//            }
//        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node:{
            height:40,
            width:20,
            type:'circle',
            dim:5,
            color:'#aaa',
            overridable:true,
            align:'left'
        },
        Canvas:{
            background:{
                color:'#EEF2F7'
            }
        },
        Edge:{
            type:'line',
            color:'#000',
            overridable:true,
            lineWidth:2
        },
        Events:{
            enable:true,
            type:'Native',
            //Change cursor style when hovering a node  
            onMouseEnter:function (node, event, e) {
                st.canvas.getElement().style.cursor = 'crosshair';
                //         popup.style.display = 'inline';
                //         popup.style.top = event.pos.y+ 20 +'px';
                //         popup.style.left = event.pos.x+20 +'px';
                //         var html = '',i ; 
                //         if ( node.data.leaf ) {
                //           html = node.name ;
                //         } else {
                //           for ( i = 0 ; i < node.data.colorCharacter.length ; i += 1 ) {
                //             html += node.data.colorCharacter [ i ] + "<br/>";
                //           }
                //         }
                //         popupText.innerHTML = html;
                // call tips from here
                st.tips.config.onShow(st.tips.tip, node);
                st.tips.setTooltipPosition($jit.util.event.getPos(e));
            },
            onMouseLeave:function () {
                st.canvas.getElement().style.cursor = '';
                //         popup.style.display = 'none';
                st.tips.hide(true);
            },
            onRightClick:function (node, eventInfo, e) {
                //console.log("onRightClick", node, eventInfo, e);
                //jQuery('.node').contextMenu({x:e.clientX, y: e.clientY});
                //jQuery.contextMenu({x:e.clientX, y: e.clientY});
            },
            onClick:function (node, eventInfo, e) {
                var leafs;
                //console.log("debug",node, eventInfo, e);
                if (false && node) {
                    selectedClade = [];

                    var expand = $jit.id('expand');
                    var pos = st.labels.getLabel(node.id);
                    var setRoot = $jit.id('setRoot');
                    var rotate = $jit.id('rotate');
                    var select = $jit.id('selectClade');
                    var loc = parseInt(pos.style.left.replace(/px/, ''), 10) + 100;
                    var locy = parseInt(pos.style.top.replace(/px/, ''), 10) + 40;

                    // re-root the tree.
                    if (setRoot.checked) {
                        var id = node.id;
                        st.setRoot(id, 'animate');
                        st.root = id;
                    }

                    // rotate node
                    if (rotate.checked) {
                        st.computePositions(st.graph.getNode(st.root), 'start');
                        if (typeof node.data.rotate === "undefined") {
                            node.data.rotate = false;
                        }
                        node.data.rotate = !node.data.rotate;
                        st.computePositions(st.graph.getNode(st.root), 'end');
                        st.fx.animate({
                            modes:['linear', 'node-property:alpha'],
                            onComplete:function () {
                            }
                        });
                    }

                    // action for 
                    if (expand.checked) {
                        st.setCollapsed(node);
                        var level = st.nodesExpCol(node);
                        if (level) {
                            st.zoomIndex = level;
                        }
                        st.computePositions(st.graph.getNode(st.root), '');
                        st.plot();
                    }

                    // select clade and display it on the popup window
                    if (select.checked) {
                        st.clickedNode = node;
                        node.eachSubgraph(function (elem) {
                            if (elem.data.leaf) {
                                if (leafs) {
                                    leafs += "<li>" + elem.name + "</li>";
                                } else {
                                    leafs = "<li>" + elem.name + "</li>";
                                }
                                selectedClade.push(elem);
                            }
                        });

                        popup.style.display = 'inline';
                        popup.style.top = locy + 'px';
                        popup.style.left = loc + 'px';
                        popupText.innerHTML = st.config.presentClade(selectedClade);
                        st.config.onPresentClade();
                        st.plot();
                    }
                } else if (node) {
                    // Trigger the contextMenu to popup
                    //console.log("tips", st.tips);
                    if (st.tips.config.enable) st.tips.hide(false); // hide the tip so it doesn't cover the context menu
                    jQuery("#infovis-canvas").data("nodeId", node.id);
                    jQuery("#infovis-canvas").data("node", node);
                    jQuery("#infovis-canvas").data("info", html);
                    jQuery("#infovis-canvas").contextMenu({x:e.pageX, y: e.pageY});
                }
            }
        },
        presentClade:function (clade) {
            var tmpl = st.config.tmpl,
                nodeList = [],
                node, html, split;
            for (var i = 0; ((i < clade.length) & (i < 30)); i++) {
                node = {}
                node.name = clade [ i ].name;
                nodeList.push(node);
            }
            if (tmpl) {
                tmpl = _.template(tmpl);
                html = tmpl({nodeList:nodeList});
            } else {

            }
            return html;

        }, //presentClade
        onPresentClade:function () {
            $('a.thumbImage1').colorbox({iframe:true, width:'80%', height:'80%'});
        }, // onPresentClade
        tmpl:'<ul><% _.each(nodeList , function( value ) { %> <li> <%= value.name %> </li> <% }); %> </ul>',
        Tips:{
            enable:true,
            onShow:function (div, node) {
                var url = '', key, i , char,
                    html = '', name = '', maptitle = '', index;
                if (!!node.name) {
                    //url = '<img class="tipImage" src="http://biocache.ala.org.au/ws/density/map?q=' + node.name.replace(' ', '+') + '"/>';
                    //maptitle = '<br/>ALA <strong>reported</strong> occurrences';
                    name = "<i>" + node.name + "</i>";
                }
                else {
                    name = " unnamed clade ";
                }
                //name = name + "<strong> click</strong> for ";
                if (node.data.leaf) { // end taxon
                //    name = name + "for linked data";
                } else { //clade
                    //clade
                    name = "Part of " + name;
                    if (node.length < 30) {
                        name = name + "clade members";
                    }
                    else {
                        name = name + "30 clade members";
                    }
                }
                name = "<h3>" + name + "</h3>";
                // display all characters
                var result = [];
                for (index in st.config.selectedCharacters) {
                    key = st.config.selectedCharacters [ index ];
                    char = node.data.character [ key ];
                    html = '<strong>' + key + '</strong>: ';
                    if (typeof char === 'undefined' || char.length === 0 || typeof char[0] === 'undefined') {
                        html += '&mdash;';
                    } else if (typeof char[0] !== 'number') {
                        html += char.join(',<br/>....');
                    } else {
                        html += char[0].toFixed(4);
                    }
                    result.push(html);
                }
                div.innerHTML = name + result.join('<br/>') + maptitle + url;
            }
        },
        onBeforeCompute:function (node) {
            Log.write("loading " + node.name);
        },

        onAfterCompute:function (msg) {
            if (msg) {
                Log.write(msg);
            } else {
                Log.write("done");
            }
        },
        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel:function (label, node) {
            var char, list = st.config.selectedCharacters /*st.characterList*/
                ,
                charTypeMapping = st.charTypeMapping,
                i, values, div, colorCoding = st.colorCoding,
                firstColor, index, temp, shape;
            label.id = node.id;
            label.innerHTML = node.name;
//            label.ondblclick = function() {
//                alert(node.id);
//            };
            label.onclick = function(e) {
//                var setRoot = $jit.id('setRoot');
//                if (!setRoot.checked) {
//                    st.controller.Events.onClick(node, null, e);
//                }
                st.controller.Events.onClick(node, null, e);
            };
            //set label styles
            var style = label.style;
            style.width = 'auto';
            style.height = 17 + 'px';
            style.cursor = 'pointer';
            style.color = '#333';
            style.fontSize = '0.8em';
            style.textAlign = 'left';
            style.paddingTop = '3px';
            style.display = 'inline';

            style.color = node.data.$color;
            //         if (node.data.color) 
            var boxes = '';
            var first = st.config.firstCharacter;
            var shapes = ['box', 'star', 'triangle'],
                index = 0;

            boxes = '';

            for (i = 0; i < list.length; i += 1) {
                //           for ( char in node.data.character ) 
                char = list[i];
                values = node.data.character[char];
                if (values && values.length > 0 && typeof values[0] !== 'undefined') {
                    if (charTypeMapping[char] === st.config.typeEnum.quali) {
                        temp = colorCoding[char];
                        value = values[0];
                        if (values.length > 1) {
                            value = 'multiple';
                        }
                        shape = '<div class="' + temp[value].shape
                            + '" style="float:left;background-color:'
                            + temp[value].color + ';" title="' + char + ' : '
                            + values.join(' , ') + '"></div>';
                        if (i === 0) {
                            firstColor = temp[value].color;
                        }
                    } else if (charTypeMapping[char] === st.config.typeEnum.quant) {
                        temp = st.colorCodingQuali[char];
                        value = values[0];
                        index = st.findIndex(value, st.range[char]);
                        shape = '<div class="' + temp[index].shape
                            + '" style="float:left;background-color:'
                            + temp[index].color + ';" title="' + char + ' : '
                            + temp[index].name + '"></div>';

                        if (i === 0) {
                            //                   firstColor = temp [ index ].color;
                            firstColor = st.config.quantColor[st.config.quantColor.length - 1];
                        }
                    }
                } else {
                    shape = '<div class="empty" style="float:left;background-color:;" title="empty"></div>';
                }
                if (first !== char) {
                    boxes += shape;
                } else {
                    boxes = shape + boxes;
                }
            }
            //console.log("firstColor", firstColor);
            //       if (node.data.leaf) {
            //           label.innerHTML = boxes + '&nbsp;&nbsp;<div style="display:inline;color:' + firstColor  + '">' + node.name + '</div>';
            // //         }
            // make names for nodes.      
            if (label) {
                if (!node.data.leaf) {
                    label.innerHTML = boxes
                        + '&nbsp;&nbsp;<div style="display:inline;margin-top:-4px;color:'
                        + firstColor + '">' + node.data.leaves + ' Taxa</div>';
                } else {
                    label.innerHTML = boxes
                        + '&nbsp;&nbsp;<div style="display:inline;margin-top:-4px;color:'
                        + firstColor + '">' + node.name + '</div>';
                }
            }

        },

        //This method is called right before plotting
        //a node. It's useful for changing an individual node
        //style properties before plotting it.
        //The data properties prefixed with a dollar
        //sign will override the global node style properties.
        onBeforePlotNode:function (node) {
            //add some color to the nodes in the path between the
            //root node and the selected node.
            //       if (!node.data.leaf) {
            var result = true,
                char;
            if (!node.data.leaf) {
                for (var key in st.config.selectedCharacters) {
                    if (node.data.characterConsistency.hasOwnProperty(key)) {
                        char = st.config.selectedCharacters[key];
                        result = result && node.data.characterConsistency[char];
                    }
                }
            }
            if (!result && node.data.$type !== 'triangle') {
                node.data.$type = 'square';
            }
            if (node.data.$type === 'circle') {
                if (node.data.rotate) {
                    node.data.$color = 'purple';
                } else {
                    node.data.$color = 'red';
                }
            } else if (node.data.$type === 'square') {
                node.data.$dim = 10;
                node.data.$color = "red";
            }
            /*      }else {
             }*/
            if (node.data.$type === 'triangle') {
                node.data.$dim = 15;
                node.data.$color = '#EE9AA2';
            } else if (node.data.$type !== 'square') {
                delete node.data.$dim;
            }
            // color for root node.
            if (st.root === node.id) {
                node.data.$color = 'lightblue';
            }
        },
        //This method is called right before plotting
        //an edge. It's useful for changing an individual edge
        //style properties before plotting it.
        //Edge data proprties prefixed with a dollar sign will
        //override the Edge global style properties.
        onBeforePlotLine:function (adj) {
        },
        onClick:function (node, eventInfo, e) {
            if (node) {
                var elem = document.getElementById('selected');
                if (node.leaf) {
                    elem.innerHTML = node.name;
                } else {
                    elem.innerHTML = '';
                    node.subGraph(function (n) {
                        if (n.data.leaf) {
                            elem.innerHTML += n.name + "<br/>";
                        }
                    });
                }
            }
        },
        onPlaceLabel:function (dom, node) {
            var alignName = $jit.id('alignName')
            if (node.selected) {
                dom.style.display = 'none';
            }
            //             remove labels of non-leaf nodes
            if (!node.data.leaf) {
                dom.style.display = 'none';
                //         dom.innerHTML = node.data.leaves + ' Taxa';
            }
            //    // show label for the last visible node in the clade
            dom.style.display = node.data.display || 'block';
            if (alignName.checked) {
                jQuery('#' + dom.id + ' .quant').addClass('quantAlign');
            } else {
                jQuery('#' + dom.id + ' .quant').removeClass('quantAlign');
            }
        }
    }; //end config (?)
    var height = config.height,
        width = config.width || 800;
    $jit.util.extend(config, initial); // merge initial into config
    Nav.load(config);
    var html, rightContainer = $jit.id('right-jitcontainer'),
        popup = $jit.id('popup'),
        popupText = $jit.id('popup-text');
    settingsPage(config);

    //init data
    var dataObject, json = '', d;

    if (config.tree) {
        dataObject = new Smits.PhyloCanvas.NewickParse(config.tree);
    } else if ( config.nexml ){
        d = XMLObjectifier.textToXML(config.nexml);
        d = XMLObjectifier.xmlToJSON(d);
        dataObject = new Smits.PhyloCanvas.NexmlParse(d, {
            nexml: config.nexml,
            tree: config.treeIndex,
            fileSource: true
        });
        console.log( dataObject );
    }
    if (typeof(dataObject) === 'object') {
        json = smitsNode2JSON(dataObject.getRoot());
    }

    var selectedClade;
    var zoomIN = $jit.id('zoomIN'),
        zoomOUT = $jit.id('zoomOUT'),
        world = $jit.id('world');
    //end
    //init Spacetree
    //Create a new ST instance
    st = new $jit.Phylo(config);
    isLateralise = function () {
        return st.config.lateralise;
    };
    //load json data
    st.loadJSON(json);
    //compute node positions and layout
    st.compute();

    //color line depending on character info
    st.character = config.character || {};

    // recursive algorithm to do propogate the line color
    //append to legend table
    var legendElem = $jit.id('legend');
    if (st.character) {
        html = st.colorCharacter() || '';
        jQuery('#legendBody').html(html);
        legendElem.style.display = 'inline';
        updateCharacter(st.characterList);
    } else {
        legendElem.style.display = 'none';
    }
    var north = $jit.id('north'),
        east = $jit.id('east'),
        west = $jit.id('west'),
        south = $jit.id('south');

    function clickHandler() {
        var pos = {};
        switch (this.id) {
            case 'north':
                pos = {
                    x:0,
                    y:10
                };
                break;
            case 'west':
                pos = {
                    x:-10,
                    y:0
                };
                break;
            case 'east':
                pos = {
                    x:10,
                    y:0
                };
                break;
            case 'south':
                pos = {
                    x:0,
                    y:-10
                };
                break;
        }
        var canvas = st.canvas;
        canvas.translate(pos.x, pos.y);
    }

    north.onmousedown = south.onmousedown = east.onmousedown = west.onmousedown = clickHandler;

    function zoomHandler() {
        var scroll;
        switch (this.id) {
            case 'zoomIN':
                scroll = +1;
                break;
            case 'zoomOUT':
                scroll = -1;
                break;
        }
        st.zoom(scroll);
    }

    zoomIN.onclick = zoomOUT.onclick = zoomHandler;
    world.onclick = function () {
        st.fitScreen();
    };
    var result = [];
    var pos, prevSearch;
    var searchBtn = $jit.id('searchString');

    function nextStep(pos, step, length) {
        // logic so that search starts from the first instance 
        if (typeof pos === 'undefined') {
            return step > 0 ? 0 : length - 1;
        }
        var i = (pos + step) % length;
        return i < 0 ? length + i : i;
    }

    var search = function (step) {
        var searchString = searchBtn.value;
        // if search has been done, clear the selected label
        var len;
        var root = st.graph.getNode(st.root);
        if (result.length > 0) {
            len = result.length;
            pos = nextStep(pos, step, len);
            var prevElem = st.labels.getLabel(result[nextStep(pos, -1 * step, len)].id);
            prevElem.style.backgroundColor = '';
        }
        if (searchString && prevSearch !== searchString) {
            result = [];
            prevSearch = searchString;
            root.eachSubgraph(function (node) {
                var name = node.name,
                    pattern = new RegExp(searchString, 'i');
                if (name.match(pattern)) {
                    result.push(node);
                }
            });
            pos = nextStep(undefined, step, len);
        } else if (searchString === '') {
            result = [];
        }
        if (result.length > 0) {
            var shownNode = result[pos];
            if (!shownNode.exist) {
                root.collapsed = true;
                st.nodesExpCol(root);
                st.computePositions(root, '');
                st.plot();
            }
            // transalate to top
            var canvas = st.canvas,
                oy = canvas.translateOffsetY,
                xTranslate = 0,
                yTranslate = -oy;
            st.canvas.translate(xTranslate, yTranslate);

            var element = st.labels.getLabel(result[pos].id);
            element.style.backgroundColor = 'yellow';
            jQuery(element).click();
        }
    };

    // add event handlers to listen to enter key on search field
    function keyHandler(e) {
        var ENTER = 13;
        var shift = e.shiftKey;
        if (shift && e.keyCode === ENTER) {
            search(-1);
            if (e.preventDefault) {
                e.preventDefault();
            }
            return false;
        } else if (e.keyCode === ENTER) {
            search(1);
            if (e.preventDefault) {
                e.preventDefault();
            }
            return false;
        }
    }

    if (searchBtn.addEventListener) {
        searchBtn.addEventListener('keydown', keyHandler, false);
    } else if (searchBtn.attachEvent) {
        searchBtn.attachEvent('onkeydown', keyHandler);
    }


    var next = $jit.id('next'),
        previous = $jit.id('previous');
    next.onclick = function () {
        search(1);
    };
    previous.onclick = function () {
        search(-1);
    };

    //     var popup = $jit.id('popup');
    //     var popupText = $jit.id('popup-text');
    /* function onclickAlign 
     This function will align the names in a vertical line.
     */
    onClickAlign = function (alignName) {
        if (alignName.checked) {
            st.config.alignName = true;
            jQuery('.quant').addClass('quantAlign');
        } else {
            st.config.alignName = false;
            jQuery('.quant').removeClass('quantAlign');
        }
        st.plot();
    };
    onSetRoot = function (setRoot) {
        if (setRoot.value === 'checked') {
            var id = st.clickedNode.id;
            st.setRoot(id, 'animate');
        }
    };
    onLateralise = function (lat) {
        if (lat.checked) {
            st.config.lateralise = true;
        } else {
            st.config.lateralise = false;
        }
        st.computePositions(st.graph.getNode(st.root), '');
        st.plot();
    };
    var animateDuration;
    onAnimate = function (animate) {
        if (!animateDuration) {
            animateDuration = st.config.duration;
        }
        if (animate.checked) {
            st.config.duration = animateDuration;
        } else {
            st.config.duration = 0;
        }
    };
    onRender = function (render) {
        var newickTree = $jit.id('newickTree').value;
        if (newickTree) {
            var dataObject = new Smits.PhyloCanvas.NewickParse(newickTree),
                rootObject, json = '';

            if (typeof(dataObject) === 'object') {
                rootObject = dataObject.getRoot(),
                    json = smitsNode2JSON(rootObject);
                st.loadJSON(json);
            }
            st.compute();
            st.config.initCharacter = false;
            var legendElem = $jit.id('legend');
            if (st.character) {
                html = st.colorCharacter() || '';
                jQuery('#legendBody').html(html);
                legendElem.style.display = 'inline';
                updateCharacter(st.characterList);
            } else {
                legendElem.style.display = 'none';
            }
            st.onClick(st.root);
            st.fitScreen();
        }
    };
    onCharRender = function() {
        var charCvs = $jit.id('characterUpload').value;
        //console.log("charCvs", charCvs);
        st.config.initCharacter = false;
        st.character = jQuery.parseJSON(charCvs) || {}; // st.characterList ???
        //console.log("st 2", st);
        if (st.character) {
            var html = st.colorCharacter() || '';
            jQuery('#legendBody').html(html);
            //legendElem.style.display = 'inline';
            jQuery('#legend').show();
            //st.findAllCharTypes(st.graph.getNode(st.root));
            updateCharacter(st.characterList);
            //console.log("triggering navigate:", window.location.hash.slice(1));
            var hashString = window.location.hash.slice(1);
            // we need the URL to change in order to get navigate to trigger -> toggle first hash value
            var fiddleHash1 = (hashString.indexOf("characters") > -1) ? "character" : "characters";
            var fiddleHash2 = (hashString.indexOf("characters") > -1) ? "characters" : "character";
            window.AppRouter.navigate(hashString.replace(fiddleHash2, fiddleHash1),{trigger: true, replace: true});
            st.fitScreen();
            alert("characters were loaded successfully");
        }
    };
    onGetCharacter = function (char) {

    };
    onBranchLength = function (checkbox) {
        st.config.branchLength = checkbox.checked;
        st.computePositions(st.graph.getNode(st.root), '');
        st.plot();
    };
    onBranchMultiply = function (value) {
        st.config.branchMultiplier = value;
        st.computePositions(st.graph.getNode(st.root), '');
        st.plot();
    };
    toggleScreen = function (elem) {
        var style = jQuery('#right-jitcontainer')[0].style;
        style.display = (style.display === 'none' ? '' : 'none');
        if (style.display) {
            jQuery(elem).removeClass('on');
        } else {
            jQuery(elem).addClass('on');
        }
    };
    onSetCharacter = function () {
        populateCharacters();
        //     TODO: disable phylo and loading gif
        //     TODO: parsimony characters and redraw
        redraw();
        //     TODO: enable phylo.
    };

    function populateCharacters() {
        var chars = [], value = [];
        for (var i = 1; i <= maxChars; i++) {
            //chars[i - 1] = jQuery('#character_' + i).val();
            var selected = jQuery('#character_' + i).val();

            if (selected) {
                value.push(selected);
                chars[i - 1] = selected;
            } else {
                chars[i - 1] = '';
            }
        }

        st.config.firstCharacter = chars[0] || '';
        st.config.selectedCharacters = value;

        var charListIsEmpty = true;

        for (var ch in chars) {
            if (!chars[ch]) {
                chars[ch] = '';
            } else {
                charListIsEmpty = false;
            }
        }
        //console.log("app nav", chars, charListIsEmpty);
        if (!charListIsEmpty) window.AppRouter.navigate('character/' + chars.join("/"));

//        var first, second, third, value = [];
//        first = jQuery('#firstCharacter').val();
//        second = jQuery('#secondCharacter').val();
//        third = jQuery('#thirdCharacter').val();
//        first && value.push(first);
//        second && value.push(second);
//        third && value.push(third);
//        st.config.firstCharacter = first || '';
//        st.config.selectedCharacters = value;
//        first = typeof ( first ) === 'undefined' ? '':first;
//        second = typeof ( second ) === 'undefined' ? '':second;
//        third = typeof ( third ) === 'undefined' ? '':third;
//        app.navigate ( 'character/'+first+'/'+second+'/'+third );
    }

    function redraw() {
        var legendElem = $jit.id('legend'),
            i, node, label;
        //     st.loadJSON (json);
        //     st.compute ();
        if (st.character) {
            html = st.colorCharacter() || '';
            jQuery('#legendBody').html(html);
            legendElem.style.display = 'inline';
        } else {
            legendElem.style.display = 'none';
        }

        for (i in st.graph.nodes) {
            if (st.graph.nodes.hasOwnProperty(i)) {
                node = st.graph.nodes[i];
                //         if( node.data.leaf ) {
                label = jQuery('#' + node.id)[0];
                label && st.config.onCreateLabel(label, node);
                //         }
            }
        }

        //optional: make a translation of the tree
        //emulate a click on the root node.
        var currentZoom = st.zoomIndex;
        st.onClick(st.root);
        //alert("pre-fitScreen, zoom = " + st.zoomIndex);
        //console.log("st", st);
        st.fitScreen();
        //st.zoomIndex = currentZoom;
        //if (currentZoom) st.zoom(currentZoom);
        //alert("post-fitScreen, zoom = " + st.zoomIndex);
    }

    var AppRouter = Backbone.Router.extend({

        routes:{
            "":"start",
            "characters/*char":"characterSelection",
            "character/*char":"characterSelection",
            "test/*vars":"test"
        },

        start:function () {

            //optional: make a translation of the tree
            //emulate a click on the root node.
            st.onClick(st.root);
            st.fitScreen();
        },

        test:function (vars) {
            //console.log("test target", vars);
            st.onClick(st.root);
            st.fitScreen();
        },

        characterSelection:function (char) {
            //console.log("characterSelection", char);
            var chars = char.split('/');
            //var index = [ 'firstCharacter' , 'secondCharacter' , 'thirdCharacter' ];
            for (var i = 0; i < chars.length; i++) {
                var ch = unescape(chars [ i ]);
                //console.log("characterSelection", i, ch);
                //var select = jQuery( '#'+index[i] )[0];
                var select = jQuery('#character_' + (i + 1))[0];

                for (var j = 0; j < select.options.length; j++) {
                    if (select.options[j].value === ch) {
                        select.selectedIndex = j;
                        break;
                    }
                }
            }
            onSetCharacter();
        }

    });

    window.AppRouter = new AppRouter();
    Backbone.history.start();
}
;
