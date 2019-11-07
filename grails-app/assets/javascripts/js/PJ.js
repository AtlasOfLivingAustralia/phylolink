var isLateralise = function () {
    return true;
}
var st;
/**
 * Convert color name to hex code. Borrowed from http://stackoverflow.com/a/1573141/249327
 *
 * @param colour
 * @return {*}
 */
function colourNameToHex(colour) {
    var colours = {
        "aliceblue": "#f0f8ff",
        "antiquewhite": "#faebd7",
        "aqua": "#00ffff",
        "aquamarine": "#7fffd4",
        "azure": "#f0ffff",
        "beige": "#f5f5dc",
        "bisque": "#ffe4c4",
        "black": "#000000",
        "blanchedalmond": "#ffebcd",
        "blue": "#0000ff",
        "blueviolet": "#8a2be2",
        "brown": "#a52a2a",
        "burlywood": "#deb887",
        "cadetblue": "#5f9ea0",
        "chartreuse": "#7fff00",
        "chocolate": "#d2691e",
        "coral": "#ff7f50",
        "cornflowerblue": "#6495ed",
        "cornsilk": "#fff8dc",
        "crimson": "#dc143c",
        "cyan": "#00ffff",
        "darkblue": "#00008b",
        "darkcyan": "#008b8b",
        "darkgoldenrod": "#b8860b",
        "darkgray": "#a9a9a9",
        "darkgreen": "#006400",
        "darkkhaki": "#bdb76b",
        "darkmagenta": "#8b008b",
        "darkolivegreen": "#556b2f",
        "darkorange": "#ff8c00",
        "darkorchid": "#9932cc",
        "darkred": "#8b0000",
        "darksalmon": "#e9967a",
        "darkseagreen": "#8fbc8f",
        "darkslateblue": "#483d8b",
        "darkslategray": "#2f4f4f",
        "darkturquoise": "#00ced1",
        "darkviolet": "#9400d3",
        "deeppink": "#ff1493",
        "deepskyblue": "#00bfff",
        "dimgray": "#696969",
        "dodgerblue": "#1e90ff",
        "firebrick": "#b22222",
        "floralwhite": "#fffaf0",
        "forestgreen": "#228b22",
        "fuchsia": "#ff00ff",
        "gainsboro": "#dcdcdc",
        "ghostwhite": "#f8f8ff",
        "gold": "#ffd700",
        "goldenrod": "#daa520",
        "gray": "#808080",
        "green": "#008000",
        "greenyellow": "#adff2f",
        "honeydew": "#f0fff0",
        "hotpink": "#ff69b4",
        "indianred ": "#cd5c5c",
        "indigo ": "#4b0082",
        "ivory": "#fffff0",
        "khaki": "#f0e68c",
        "lavender": "#e6e6fa",
        "lavenderblush": "#fff0f5",
        "lawngreen": "#7cfc00",
        "lemonchiffon": "#fffacd",
        "lightblue": "#add8e6",
        "lightcoral": "#f08080",
        "lightcyan": "#e0ffff",
        "lightgoldenrodyellow": "#fafad2",
        "lightgrey": "#d3d3d3",
        "lightgreen": "#90ee90",
        "lightpink": "#ffb6c1",
        "lightsalmon": "#ffa07a",
        "lightseagreen": "#20b2aa",
        "lightskyblue": "#87cefa",
        "lightslategray": "#778899",
        "lightsteelblue": "#b0c4de",
        "lightyellow": "#ffffe0",
        "lime": "#00ff00",
        "limegreen": "#32cd32",
        "linen": "#faf0e6",
        "magenta": "#ff00ff",
        "maroon": "#800000",
        "mediumaquamarine": "#66cdaa",
        "mediumblue": "#0000cd",
        "mediumorchid": "#ba55d3",
        "mediumpurple": "#9370d8",
        "mediumseagreen": "#3cb371",
        "mediumslateblue": "#7b68ee",
        "mediumspringgreen": "#00fa9a",
        "mediumturquoise": "#48d1cc",
        "mediumvioletred": "#c71585",
        "midnightblue": "#191970",
        "mintcream": "#f5fffa",
        "mistyrose": "#ffe4e1",
        "moccasin": "#ffe4b5",
        "navajowhite": "#ffdead",
        "navy": "#000080",
        "oldlace": "#fdf5e6",
        "olive": "#808000",
        "olivedrab": "#6b8e23",
        "orange": "#ffa500",
        "orangered": "#ff4500",
        "orchid": "#da70d6",
        "palegoldenrod": "#eee8aa",
        "palegreen": "#98fb98",
        "paleturquoise": "#afeeee",
        "palevioletred": "#d87093",
        "papayawhip": "#ffefd5",
        "peachpuff": "#ffdab9",
        "peru": "#cd853f",
        "pink": "#ffc0cb",
        "plum": "#dda0dd",
        "powderblue": "#b0e0e6",
        "purple": "#800080",
        "red": "#ff0000",
        "rosybrown": "#bc8f8f",
        "royalblue": "#4169e1",
        "saddlebrown": "#8b4513",
        "salmon": "#fa8072",
        "sandybrown": "#f4a460",
        "seagreen": "#2e8b57",
        "seashell": "#fff5ee",
        "sienna": "#a0522d",
        "silver": "#c0c0c0",
        "skyblue": "#87ceeb",
        "slateblue": "#6a5acd",
        "slategray": "#708090",
        "snow": "#fffafa",
        "springgreen": "#00ff7f",
        "steelblue": "#4682b4",
        "tan": "#d2b48c",
        "teal": "#008080",
        "thistle": "#d8bfd8",
        "tomato": "#ff6347",
        "turquoise": "#40e0d0",
        "violet": "#ee82ee",
        "wheat": "#f5deb3",
        "white": "#ffffff",
        "whitesmoke": "#f5f5f5",
        "yellow": "#ffff00",
        "yellowgreen": "#9acd32"
    };

    if (typeof colours[colour.toLowerCase()] != 'undefined')
        return colours[colour.toLowerCase()];

    return colour;
}

/**
 * Get a colour value (hex) by its index value in an array (32 elements)
 */
var colourScheme = {
    colours: ["3366CC", "DC3912", "FF9900", "109618", "990099", "0099C6", "DD4477", "66AA00", "B82E2E", "316395", "994499", "22AA99", "AAAA11", "6633CC", "E67200", "8B0707", "651067", "329262", "5574A6", "3B3EAC", "B77322", "16D620", "B91383", "F43595", "9C5935", "A9C413", "2A778D", "668D1C", "BEA413", "0C5922", "743411", "000000"],
    getColourForIndex: function (index) {
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

var PJ = function (params) {
    // console.log('in pj');
    var self = new Emitter(this);
    var pj = this;
    this.hData = params.hData;
    var qid,
        prevSearch,
        queryObj,
        settings,
        trimming;

    //adding support functions for event handling
    this.events = [
    /**
     * when a node is clicked
     */
        'click',
    /**
     * when a node is hovered
     */
        'hover',
    /**
     * after tree has been loaded
     */
        'treeloaded',
    /**
     * when query is saved
     *
     */
        'savequerybegin',
    /**
     * when save query ends
     */
        'savequeryend'
    ]
    var $ = jQuery;
    var spinner = new Spinner({
        top: '50%',
        left: '50%',
        className: 'loader'
    });

    // used by search
    var result = []
    var config = $.extend({
        //id of viz container element
        injectInto: 'infovis',
        width: 800,
        height: 600,
        offsetX: 0,
        align: 'left',
        //treeloaded flag to check if the tree is loaded
        treeloaded: false,
        alignName: false,
        lateralise: true,
        branchLength: true,
        branchMultiplier: 1,
        duration: 1000,
        fps: 10,
        //set animation transition type
        transition: $jit.Trans.Quart.easeInOut,
        //set distance between node and its children
        levelDistance: 20,
        levelsToShow: Number.MAX_VALUE,
        constrained: false,
        useGradient: false,
        firstCharacter: 'Raceme_length_median',
        /**
         * save query flags
         */
        doSaveQuery: true,
        runSaveQuery: false, // boolean
        saveQuery: {
            url: 'http://dev.ala.org.au:8080/phylolink/ala/saveQuery',
            type: 'POST',
            dataType: 'JSON'
        },
        /**
         * flag to see if hash is changing due to click by user or going back on browser history
         */
        setNodeToUrlFlag: false,
        linkouts: {
            ALA: {
                displayName: 'View species details',
                url: 'http://bie.ala.org.au/species/${name}'
            }
        },
        /**
         * popover when user first interacts
         */
        popOver: [{
            id: '#' + params.id,
            options: {
                placement: 'top',
                trigger: 'manual',
                html: 'true',
                content: '<button id="pjPopOverClose" class="btn btn-primary">Okay, got it!</button> '
            }
        }, {
            id: '#zoomworld',
            options: {
                placement: 'left',
                trigger: 'manual',
                html: 'true',
                content: 'Action buttons'
            }
        }, {
            id: function () {
                var nodes = $('.node:visible'), result = [], i, top;
                for (i = 0; i < nodes.length; i++) {
                    top = parseInt($(nodes[i]).css('top').replace('px', ''))
                    if ((top < ( params.height / 2 + 100)) && ( top > ( params.height / 2 - 100) )) {
                        result.push(nodes[i].id)
                    }
                }
                return '#' + result[Math.max(result.length / 2)];
            },
            options: {
                placement: 'right',
                trigger: 'manual',
                html: 'true',
                title: 'Tree Leaf',
                content: 'You can left and right click on this name. Left click interacts with tabs on right.' +
                'Right click provides you with a list of options to choose from.'
            }
        }, {
            id: 'canvas',
            options: {
                placement: 'bottom',
                trigger: 'manual',
                html: 'true',
                content: '<div id="pjPopOverClose" class="btn btn-primary">Okay, got it!</div> '
            }
        }],
        //enable panning
        Navigation: {
            enable: true,
            panning: 'avoid nodes',
            zooming: 50
        },
        //set node and edge styles
        //set overridable=true for styling individual
        //nodes or edges
        Node: {
            height: 20,
            width: 20,
            type: 'circle',
            dim: 5,
            color: '#aaa',
            overridable: true,
            align: 'left',
            selectedShape: 'ellipse',
            selectedColor: 'royalblue'
        },
        Edge: {
            type: 'line',
            color: '#000',
            overridable: true,
            lineWidth: 2
        },
        Events: {
            enable: true,
            type: 'Native',
            //Change cursor style when hovering a node
            onMouseEnter: function (node, event, e) {
                st.canvas.getElement().style.cursor = 'crosshair';
                // call tips from here
                st.tips.config.onShow(st.tips.tip, node);
                st.tips.setTooltipPosition($jit.util.event.getPos(e));
            },

            onMouseLeave: function () {
                st.canvas.getElement().style.cursor = '';
                st.tips.hide(true);
            },

            onRightClick: function (node, eventInfo, e) {
                var canvas = jQuery("#" + config.injectInto + " canvas");
                if (node) {
                    if (st.tips.config.enable) st.tips.hide(false);
                    canvas.data("nodeId", node.id);
                    canvas.data("node", node);
                    canvas.data("info", "");
                    canvas.contextMenu({x: e.pageX, y: e.pageY});
                } else if (e.target) {
                    node = $(e.target)
                    if (!node.hasClass('.node')) {
                        parent = node.parent('.node');
                        child = node.children('.node');
                        if (parent.length) {
                            node = parent.attr('id')
                        } else if (child.length) {
                            node = child.attr('id')
                        }
                    }
                    node = st.graph.getNode(node);
                    if (node) {
                        canvas.data("node", node);
                        canvas.data("nodeId", node.id);
                        canvas.contextMenu({x: e.pageX, y: e.pageY});
                    }
                }
            },

            onClick: function (node, eventInfo, e) {
                var leafs, names, canvas;
                e = e || {};
                eventInfo = eventInfo || {};
                if (node) {
                    // Trigger the contextMenu to popup
                    if (st.tips.config.enable) st.tips.hide(false); // hide the tip so it doesn't cover the context menu
                    if (e.which == 3) {
                        //right click
                        canvas = jQuery("#infovis-canvas").find('canvas');
                        canvas.data("nodeId", node.id);
                        canvas.data("node", node);
                        canvas.data("info", html);
                        canvas.contextMenu({x: e.pageX, y: e.pageY});
                    } else {
                        // abort previous save query calls since this is a new query
                        queryObj && !queryObj.statusText && queryObj.abort();
                        queryObj = null;
                        st.clickedNode = node;
                        st.plot()
                        pj.setNodeToUrl(node.id);
                        names = pj.getChildrenName(node);
                        if (config.runSaveQuery) {
                            queryObj = pj.saveQuery(node, names);
                        }



                        node && self.emit('click', node, names, queryObj);
                    }
                }
            }
        },

        presentClade: function (clade) {
            var tmpl = st.config.tmpl,
                nodeList = [],
                node, html, split;
            for (var i = 0; ((i < clade.length) & (i < 30)); i++) {
                node = {}
                node.name = clade [i].name;
                nodeList.push(node);
            }
            if (tmpl) {
                tmpl = _.template(tmpl);
                html = tmpl({nodeList: nodeList});
            } else {

            }
            return html;

        }, //presentClade

        onPresentClade: function () {
            $('a.thumbImage1').colorbox({iframe: true, width: '80%', height: '80%'});
        }, // onPresentClade

        tmpl: '<ul><% _.each(nodeList , function( value ) { %> <li> <%= value.name %> </li> <% }); %> </ul>',

        Tips: {
            enable: true,
            onShow: function (div, node) {
                var url = '', key, i, char,
                    html = '', name = '', maptitle = '', index;
                if (!!node.name) {
                    name = "<i>" + node.name + "</i>";
                }
                else {
                    name = " unnamed";
                }
                if (node.data.leaf) { // end taxon
                } else { //clade
                    if (name) {
                        name = "Inner Node:" + name;
                    } else {
                        name = "Inner Node: not named"
                    }
                }
                name = "<h3>" + name + "</h3>";
                // display all characters
                var result = [];
                for (index in st.config.selectedCharacters) {
                    key = st.config.selectedCharacters [index];
                    char = node.data.character [key];
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

        onBeforeCompute: function (node) {
            // console.log("loading " + node.name);
        },

        onAfterCompute: function (msg) {
            // if (msg) {
            //     console.log(msg);
            // } else {
            //     console.log("done");
            // }
        },

        //This method is called on DOM label creation.
        //Use this method to add event handlers and styles to
        //your node.
        onCreateLabel: function (label, node) {
            var char, list = st.config.selectedCharacters /*st.characterList*/
                ,
                charTypeMapping = st.charTypeMapping,
                i, values, div, colorCoding = st.colorCoding,
                firstColor, index, temp, shape;
            label.id = node.id;
            label.innerHTML = node.name;
            label.onclick = function (e) {
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
            var boxes = '';
            var first = st.config.firstCharacter;
            var shapes = ['box', 'star', 'triangle'],
                index = 0;

            for (i = 0; i < list.length; i += 1) {
                char = list[i];
                values = node.data.character && node.data.character[char];
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
        onBeforePlotNode: function (node) {
            //add some color to the nodes in the path between the
            //root node and the selected node.
            var result = true,
                char;
            if (!node.data.leaf) {
                for (var key in st.config.selectedCharacters) {
                    if (node.data.characterConsistency && node.data.characterConsistency.hasOwnProperty(key)) {
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
        onBeforePlotLine: function (adj) {
        },

        onClick: function (node, eventInfo, e) {
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

        onPlaceLabel: function (dom, node) {
            var alignName = settings.alignName() || false
            if (node.selected) {
                dom.style.display = 'none';
            }
            //             remove labels of non-leaf nodes
            if (!node.data.leaf) {
                dom.style.display = 'none';
            }
            // show label for the last visible node in the clade
            dom.style.display = node.data.display || 'block';
            if (alignName) {
                jQuery('#' + dom.id + ' .quant').addClass('quantAlign');
            } else {
                jQuery('#' + dom.id + ' .quant').removeClass('quantAlign');
            }
        },

        getSelectedNode: function () {
            return st.clickedNode;
        }
    }, params);
    config.injectInto = config.id;
    delete config.id;
    //end config

    // settings model and functionality
    function SettingsModel(opt) {
        this.alignName = ko.observable(opt.alignName || false);
        this.alignPJ = function (obj, e) {
            pj.alignNames(this.alignName());
            return true;
        }
    }

    settings = new SettingsModel({
        alignName: config.alignName
    });
    ko.applyBindings(settings, document.getElementById(config.settingsId));
    // end settings functionality


    // tree trimming model and functionality
    function TrimmingModel(config) {
        this.TRIM_LIST = {displayName: "A species list", value: "SPECIES_LIST"};

        this.trimOption = ko.observable();
        this.trimData = ko.observable();
        this.trimToInclude = ko.observable("true");

        this.trimOptions = ko.observableArray([
            {displayName: "Australia", value: "AUSTRALIAN_ONLY"},
            this.TRIM_LIST,
            {displayName: "The Atlas of Living Australia", value: "ALA_ONLY"}]);

        this.applyTrimOptions = function () {
            pj.clearNodeToUrl();

            getTree(config.url, setTree, {});
            $("#" + config.trimmingId).modal('hide');
        };

        this.clearTrimOptions = function () {
            pj.clearNodeToUrl();

            this.trimOption = ko.observable();
            this.trimData = ko.observable();
            this.trimToInclude = ko.observable("true");

            getTree(config.url, setTree, {});
            $("#" + config.trimmingId).modal('hide');
        };

        var url = config.listToolBaseURL + "/ws/speciesList?max=1000";

        this.init = function () {
            $.ajax({
                url: url,
                dataType: "json",
                success: function (data) {
                    var lists = [];
                    data.lists.forEach(function (list) {
                        lists.push({id: list.dataResourceUid, text: list.listName})
                    });

                    $("#trimByList").select2({
                        data: lists
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("Failed to retrieve species lists from url " + url)
                }
            });
        };
    }

    trimming = new TrimmingModel(config);
    trimming.init();
    ko.applyBindings(trimming, document.getElementById(config.trimmingId));


    var nextStep = function (pos, step, length) {
        // logic so that search starts from the first instance
        if (typeof pos === 'undefined') {
            return step > 0 ? 0 : length - 1;
        }
        var i = (pos + step) % length;
        return i < 0 ? length + i : i;
    };

    var smitsNode2JSON = function (node) {
        var childJSON = [];
        var childrenName = []
        var leaves = 0;
        for (var i = 0; i < node.children.length; i++) {
            var j = smitsNode2JSON(node.children[i])
            childJSON.push(j);
            childrenName = childrenName.concat(j.childrenName);
            leaves += j.data.leaf;
            leaves += j.data.leaves;
        }

        var that = node;
        var sampleid = '';
        if (childJSON.length !== 0) {
            return {
                "id": md5(childrenName.join('')),
                "name": node.name,
                "md5":md5(childrenName.join('')),
                "data": {
                    'leaves': leaves,
                    'leaf': 0,
                    'len': node.len,
                    '$type': 'circle',
                    '$dim': 5,
                    '$color': '#fff'
                },
                "children": childJSON,
                childrenName: childrenName
            };
        } else {
            node.name = node.name.replace(/_/g, ' ');
            var sampleArray = node.name.split(' ');
            if (sampleArray.length > 1) {
                sampleid = sampleArray[1];
            }
            var name = sampleArray[0];
            var nodeJSON = {
                "id": md5(node.name),
                "name": node.name,
                "md5":md5(node.name),
                "data": {
                    'leaves': 0,
                    'leaf': 1,
                    'len': node.len,
                    '$height': 20,
                    '$type': 'none',
                    'sampleid': sampleid,
                    'name': name
                },
                "children": childJSON,
                childrenName: [node.name]
            };
            return nodeJSON;
        }
    };

    var navigation = function (opt) {
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

        var navHTML2 = ''+
            '<div class="node-count" style="position: absolute; right: -40px; top: 5px; white-space: nowrap;">No node selected</div>' +
            '<div style="position:relative">' +
                '<div style="position: absolute; left: -170px; top: 20px; width:50px; height: 20px; cursor: pointer;">' +
                    '<div class="input-group input-group-sm">' +
                        '<input type="text" id="searchText" style="width:180px;" class="form-control input-sm" placeholder="Search tree" name="srch-term" id="srch-term" onfocus="utils.clearPlaceholder(this)"> ' +
                        '<div class="input-group-btn">' +
                            '<button class="btn btn-default btn-sm" id="searchBtn" type="submit"><i class="glyphicon glyphicon-search"></i></button>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="pj-nav-controls" style="position: absolute; left: -10px; top: 15px; width:50px; height: 20px; cursor: pointer;">' +
                '<div id="panup" style="position: absolute; left: 10px; top: 42px; cursor: pointer;">' +
                    '<div id="north" title="Pan up">' +
                '       <i class="glyphicon glyphicon-arrow-up" aria-hidden="true"></i>' +
                '   </div>' +
            '   </div>' +
            '   <div id="panleft" title="Pan left" style="position: absolute; left: 0px; top: 56px;cursor: pointer;">' +
            '       <div id="west">' +
            '           <i class="glyphicon glyphicon-arrow-left" aria-hidden="true"></i>' +
                '   </div>' +
            '   </div>' +
            '   <div id="panright" title="Pan right" style="position: absolute; left:22px; top: 56px; cursor: pointer;">' +
            '       <div id="east">' +
            '           <i class="glyphicon glyphicon-arrow-right" aria-hidden="true"></i>' +
            '       </div>' +
            '   </div>' +
            '   <div id="pandown" title="Pan down" style="position: absolute; left: 10px; top: 70px; cursor: pointer;">' +
            '       <div id="south">' +
            '           <i class="glyphicon glyphicon-arrow-down" aria-hidden="true"></i>' +
            '       </div>' +
            '   </div>' +
                '<div id="zoomout" title="Zoom out" style="position: absolute; left: 13px; top: 169px; cursor: pointer;">' +
                    '<div id="zoomOUT">' +
                '       <i class="glyphicon glyphicon-zoom-out"></i>' +
                '   </div>' +
                '</div>' +
                '<div id="zoomworld" style="position: absolute; left: 13px; top: 103px;  cursor: pointer;">' +
                    '<div id="world" style="position: relative;" >' +
                        '<i class="glyphicon glyphicon-resize-small"></i>' +
                    '</div>' +
                '</div>' +
                '<div id="zoomin" title="Zoom in" style="position: absolute; left: 13px; top: 141px; cursor: pointer;">' +
                    '<div id="zoomIN" margin-top="10px;">' +
                        '<i class="glyphicon glyphicon-zoom-in"></i>' +
                    '</div>' +
                '</div>' +
                '<div id="settingsBtn" title="Tree display options" style="margin-top:70px;">' +
                    ' <a href="#' + config.settingsId + '" role="button" data-toggle="modal">' +
                        '<i class="glyphicon glyphicon-cog"></i>' +
                    '</a>' +
                '</div>' +
                '<div id="trimmingBtn" title="Tree trimming options" style="margin-top:90px;"> ' +
                    '<a href="#' + config.trimmingId + '" role="button" data-toggle="modal">' +
                        '<i class="glyphicon glyphicon-leaf"></i>' +
                    '</a>' +
                '</div>' +
                '<div id="resetTreeSelection" title="Clear node selection" style="margin-top:300px; cursor: pointer;"> ' +
                    '<div id="resetTreeSelection">' +
                        '<i class="glyphicon glyphicon-repeat"></i>' +
                    '</div>' +
                '</div>' +
            '</div>';


        var navHTML3 = '<div style="position:relative"><div id="panup" style="position: absolute; left: 13px; top: 4px;' +
            ' width: 18px; height: 18px; cursor: pointer;"><div id="north"><span class="glyphicon glyphicon-arrow-up"' +
            ' aria-hidden="true"></span></div></div><div id="panleft" style="position: absolute; left: 4px; top: 22px;' +
            ' width: 18px; height: 18px; cursor: pointer;"><div id="west"><span class="glyphicon glyphicon-arrow-left"' +
            ' aria-hidden="true"></span></div></div><div id="panright" style="position: absolute; left: 22px; ' +
            'top: 22px; width: 18px; height: 18px; cursor: pointer;"><div id="east"><span class="glyphicon ' +
            'glyphicon-arrow-right" aria-hidden="true"></span></div></div><div id="pandown" style="position: ' +
            'absolute; left: 13px; top: 40px; width: 18px; height: 18px; cursor: pointer;"><div id="south"><span ' +
            'class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span></div></div>' +
            '<div id="zoomout" style="position: absolute; left: 13px; top: 99px; width: 18px; height: 18px; ' +
            'cursor: pointer;"><div id="zoomOUT"><span class="glyphicon glyphicon-zoom-out"></span></div></div>' +
            '<div id="zoomworld" style="position: absolute; left: 13px; top: 63px; width: 18px; height: 18px; cursor: pointer;"><div id="world" style="position: relative; width: 18px; height: ' +
            '18px;" ><span class="glyphicon glyphicon-resize-small"></span></div></div>' +
            '<div id="zoomin" style="position: absolute; left: 13px; top: 81px; width: 18px; height: 18px; cursor: ' +
            'pointer;">' +
            '<div id="zoomIN"><span class="glyphicon glyphicon-zoom-in"></span></div></div>' +
            '</div>';

        var navHTML = config.bootstrap == 2 ? navHTML2 : navHTML3;
        var jitcontainer, rightJitContainer, centerJitContainer,
            id = typeof (opt.injectInto) == 'string' ? opt.injectInto : opt.injectInto.id,
            infovis, parent, navigation, border, nodeCount;

        border = opt.width * 100 / 90;
        jitcontainer = $E('div', {
            'id': 'jitcontainer',
            'className': 'clearfix',
            'style': {
                'position': 'relative'
            }
        });

        rightJitContainer = $E('div', {
            'id': 'right-jitcontainer',
            'className': '',
            'style': {
                display: 'none'
            }
        });
        centerJitContainer = $E('div', {
            'id': 'center-jitcontainer',
            'className': ''
        });
        infovis = jQuery('#' + id)[0];
        parent = infovis.parentNode;
        navigation = $E('div', {
            'id': 'navigationPanel',
            'style': {
                'left': (opt.width - 50) + 'px',
                'z-index': 1
            }
        });

        jQuery(navigation).html(navHTML);
        jQuery(infovis).prepend(navigation);

        // setup handlers for navigation icons
        var north = $jit.id('north'),
            east = $jit.id('east'),
            west = $jit.id('west'),
            south = $jit.id('south');

        function clickHandler() {
            var pos = {};
            switch (this.id) {
                case 'north':
                    pos = {
                        x: 0,
                        y: 10
                    };
                    break;
                case 'west':
                    pos = {
                        x: -10,
                        y: 0
                    };
                    break;
                case 'east':
                    pos = {
                        x: 10,
                        y: 0
                    };
                    break;
                case 'south':
                    pos = {
                        x: 0,
                        y: -10
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

        var zoomIN = $jit.id('zoomIN'),
            zoomOUT = $jit.id('zoomOUT'),
            world = $jit.id('world'),
            resetTree = $jit.id('resetTreeSelection');

        zoomIN.onclick = zoomOUT.onclick = zoomHandler;
        world.onclick = function () {
            st.fitScreen();
        };

        resetTree.onclick  = function () {
            pj.clickNode(st.root);
        };

        var searchBox = $("#searchBtn");
        searchBox.click(function () {
            var str = $('#searchText').val();
            pj.search(str, 1);
        })
        var searchText = $('#searchText').keypress(function (e) {
            if (e.which == 13) {
                searchBox.trigger('click');
            }
        });
    };

    /**
     * gets tree data from url
     * @param url
     * @param callback
     * @param options
     */
    var getTree = function (url, callback, options) {
        var that = pj,
            initialArguments = arguments;
        var method = options.method || 'GET'

        spinner.spin();
        $('#' + config.injectInto).append(spinner.el);

        $.ajax({
            url: url,
            dataType: options.dataType,
            data: {
                trimOption: (trimming.trimOption() ? trimming.trimOption().value : "NONE"),
                trimData: (trimming.trimData() ? trimming.trimData() : null),
                trimToInclude: (trimming.trimToInclude() === undefined ? true : trimming.trimToInclude())
            },
            success: function (data) {
                spinner.stop();
                if (typeof data == 'object') {
                    options.format = data.format || options.format;
                    options.tree = data.tree;
                } else {
                    options.tree = data;
                }

                if (!options.tree || options.tree.length == 0 || options.tree === "();") {
                    alert("The resulting tree has no nodes");
                }

                callback.apply(that, [options])
            },
            error: function (jqXHR, textStatus, errorThrown) {
                spinner.stop();
                // CAS sometimes redirects to auth.ala.org.au. Since that page returns html code and jquery ajax tries
                // to convert it to JSON, an error is throw. The below code attempts to catch such error and regenerate
                // the ajax request. If it was a CAS problem, the second request must work.
                if (jqXHR.status == 200 && textStatus == 'parsererror') {
                    getTree.apply(that, initialArguments);
                } else {
                    console.log("jqXHR = " + JSON.stringify(jqXHR));
                    console.log("textStatus = " + textStatus);
                    console.log("errorThrown = " + errorThrown);
                    console.log(arguments);
                    alert('Could not load tree. Tree URL seems to be incorrect.');
                }
                ;

            }
        })
    }

    /**
     * pass a tree and render it on phylojive canvas
     * params - an object
     * tree - tree data
     * format - 'newick','nexml' - mandatory
     * url - url to the tree data
     */
    var setTree = function (obj) {
        var dataObject, json, d, id, node, prevId;
        config.treeloaded = false;
        switch (obj.format) {
            case 'newick':
                if (obj.tree) {
                    obj.tree = obj.tree.replace(/ /g, '_').replace(/'/g, "").replace(/\[pre-ingroup-marker\]/g, '')
                    dataObject = new Smits.PhyloCanvas.NewickParse(obj.tree);
                }
                break;
            case 'nexml':
                if (obj.tree) {
                    d = XMLObjectifier.textToXML(obj.tree);
                    d = XMLObjectifier.xmlToJSON(d);
                    dataObject = new Smits.PhyloCanvas.NexmlParse(d, {nexml: obj.nexml});
                }
                break;
        }

        if (dataObject) {
            // console.log('converting using smits library');
            json = smitsNode2JSON(dataObject.getRoot());
            st.loadJSON(json)
            st.compute();
            // check if pj setting is present. if yes, use node id from it.
            prevId = (config.pjSettings || {} ).nodeId
            // if node id is set on url then use it, otherwise use the settings.
            id =  pj.getNodeFromUrl() || prevId;
            st.onClick(st.root, true);

            // if zoomIndex is not set, the rendering will go crazy. make sure zoomIndex is set when a node is clicked
            st.zoomIndex = st.graph.depth.length;
            st.plot();
            if (!config.clickNodeHandlerRegistered) {
                pj.on('treeloaded', function () {
                    if (id) {
                        pj.clickNode(id);
                    } else {
                        pj.clickNode(st.root);
                    }
                });
            }
            config.clickNodeHandlerRegistered = true;
            config.treeloaded = true;
            // fire event after tree is loaded
            pj.emit('treeloaded');
            // console.log('successfully completed')
        } else if (obj.url) {
            // if no format is given, then get it from url. remember the url should provide tree and format.
            getTree(obj.url, setTree, obj)
        }
    }

    var redraw = function () {
        var i, node, label;
        if (st.character) {
            st.colorCharacter() || '';
        }
        var start, stop
        for (i in st.graph.nodes) {
            if (st.graph.nodes.hasOwnProperty(i)) {
                node = st.graph.nodes[i];
                label = jQuery('#' + node.id)[0];
                label && st.config.onCreateLabel(label, node);
            }
        }
    };

    var setTitle = function (config) {
        var HeadingModel = function (d) {
            this.title = ko.observable(d.title || 'unnamed');
            this.id = d.id;
            this.selected = ko.observable(d.selected || false)
            this.edit = ko.observable(d.edit || false)
            this.selectedDr = d.selectedDr
            this.selectedClade = d.selectedClade
            this.selectedCladeNumber = d.selectedCladeNumber

            this.clicked = function () {
                return this.selected();
            }
            this.clearClick = function () {
                this.selected(false)
            }
            this.select = function () {
                if (this.edit()) {
                    this.selected(true)
                    $('#' + config.heading).find('input').focus()
                }
            }
            this.sync = function () {
                pj.syncTitle(JSON.parse(ko.toJSON(this)));
            }
        }

        var data = config.hData;
        var titleDom = $('#' + config.heading);
        var model = new HeadingModel(data);
        ko.applyBindings(model, titleDom[0]);
        return model;
    }

    this.st = st;

    this.setTree = function (tree, format, url) {
        setTree({
            tree: tree,
            format: format,
            url: url
        });
    };

    this.highlight = function (obj) {
        var nodeName, value
        for (var nodeName in obj) {
            value = obj[nodeName];
            if (typeof value != 'object') {
                obj[nodeName] = {'highlight': [1]}
            }
        }
        st.character = obj;
        console.log(obj)
        st.firstCharacter = 'highlight';
        st.colorCharacter();
        console.log(st.characterList)
        st.plot();
    };

    this.clearHighlight = function () {
        console.log('in clear hightlight')
        var nodeName, value;
        var obj = st.character;
        for (var nodeName in obj) {
            obj[nodeName] = {}
        }

        st.character = obj;
        console.log(st.character)
        st.firstCharacter = 'hightlight'
        st.colorCharacter()
        st.plot();
    };

    this.ladderize = function () {

    };

    this.registerEvents = function () {

    };

    this.search = function (searchString, step) {

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
                ox = canvas.translateOffsetX,
                xTranslate = -ox,
                yTranslate = -oy;
            st.canvas.translate(xTranslate, yTranslate);

            var element = st.labels.getLabel(result[pos].id);
            element.style.backgroundColor = 'yellow';
            yTranslate = $(element).css('top').replace('px', '');
            yTranslate = yTranslate ? parseInt(yTranslate) : 0;
            st.canvas.translate(0, -yTranslate);
        }
    };

    this.colorTreeWithCharacter = function (charJson, selected, useGradient) {
        st.character = charJson;
        st.config.initCharacter = false;
        st.config.firstCharacter = st.firstCharacter = selected[0];
        st.config.selectedCharacters = st.selectedCharacters = selected;
        st.colorCharacter();
        redraw();
        st.plot();
    };

    /**
     * this function does not draw characters on tree. It will display tree as if no character was ever selected
     */
    this.drawTreeWithoutCharacters = function () {
        st.character = {};
        st.config.initCharacter = false;
        st.config.firstCharacter = st.firstCharacter = undefined;
        st.config.selectedCharacters = st.selectedCharacters = [];
        this.clearCharacters();
        st.colorCharacter();
        redraw();
        st.plot();
    }

    this.clearCharacters = function () {
        var root = this.getRoot(),
            node = this.getNodeById(root);
        node.eachSubgraph(function (n) {
            n.eachAdjacency(function (adj) {
                adj.setData('color', '#000');
            })
        })

    }

    this.getChildrenName = function (node) {
        var result = []
        node && node.eachSubgraph(function (n) {
            if (n.data.leaf) {
                result.push(n.name);
            }
        });
        return result;
    }

    this.getSelection = function () {
        return st.clickedNode;
    }

    this.getRoot = function () {
        return st.root;
    }

    this.getNodeById = function (id) {
        return st.graph.getNode(id);
    }

    /**
     * check if tree is loaded
     * @returns {boolean}
     */
    this.isTreeLoaded = function () {
        return config.treeloaded;
    }

    /**
     * get query id. The returned value can be formatted using the format parameter.
     * @param format (Boolean) format query id in ala format
     * @returns (string) eg: 'qid:1234887' or '1234887'
     */
    this.getQid = function (format) {
        if (qid === undefined) {
            return;
        }

        if (format) {
            return 'qid:' + qid;
        } else {
            return qid;
        }
    }

    /**
     * clear qid.
     */
    this.clearQid = function () {
        qid = undefined;
    }

    /**
     * gets node id from url hash
     */
    this.getNodeFromUrl = function () {
        var hash = window.location.hash;
        var nodeId = hash.split("#node/");
        if (nodeId.length > 1) {
            return nodeId[1];
        }
    }

    /**
     * set a node id to url
     * @param id - node id
     */
    this.setNodeToUrl = function (id) {
        if (id == undefined) {
            return;
        }
        config.setNodeToUrlFlag = true;
        var hash = '#node/' + id
        window.location.hash = hash;
    };

    this.clearNodeToUrl = function () {
        window.location.hash = "";
    };

    /**
     * simulate a click on a node
     * @param node
     */
    this.clickNode = function (id) {
        var node = st.graph.getNode(id);
        st.clickedNode = node
        st.plot()
        config.Events.onClick(node);
    }

    /**
     * sync title with server
     */
    this.syncTitle = function (data) {
        $.ajax({
            url: config.titleUrl,
            data: data,
            success: function () {
                console.log('title saved!');
            }
        })
    }

    this.saveQuery = function (node, names, characterQuery) {

        console.log("selected nodes: " + names.length);

        var params = config.saveQuery.data;
        params.characterQuery = characterQuery;
        params.treeId = config.treeId;
        if (config.doSaveQuery && typeof(config.saveQuery) != "undefined" && typeof(config.saveQuery.url) != "undefined") {
            if (!characterQuery) {
                qid = undefined;
            }
            this.emit('savequerybegin');

            if(names.length > 200 && config.saveQuery.defaultQuery){
                $('.node-count').html(names.length + ' taxa selected');
                $('.node-count-query-detail').html('Using default query: ' + config.saveQuery.defaultQuery)
                $('.node-count-query-help').html("Select node with less than 200 nodes");
                params.speciesList = JSON.stringify(config.saveQuery.defaultQuery);
            } else {

                $('.node-count').html(names.length + ' taxa selected');
                $('.node-count-query-detail').html('');
                $('.node-count-query-help').html('');
                params.speciesList = JSON.stringify(names);
            }

            var obj = $.ajax({
                url: config.saveQuery.url,
                type: config.saveQuery.type,
                dataType: config.saveQuery.dataType,
                data: params,
                success: function (q) {
                    // console.log(JSON.stringify(q))
                    if (!characterQuery) {
                        qid = q.qid;
                    }
                    pj.emit('savequeryend');
                    // console.log(qid);
                },
                error: function () {
                    console.log('failed!');
                    pj.emit('savequeryend');
                }
            });
            return obj;
        }
    }

    /**
     * used to set parameters sent when calling saveQuery function. This is used by Records.js.
     * @param type
     * @param instance
     * @param drid
     */

    this.setSaveQueryParams = function (type, biocacheServiceUrl, drid) {
        config.saveQuery.data.speciesList = undefined;
        config.saveQuery.data.dataLocationType = type || 'ala';
        config.saveQuery.data.biocacheServiceUrl = biocacheServiceUrl;
        config.saveQuery.data.drid = drid;
    }

    /**
     * change runSaveQuery flag
     * @param flag
     */
    this.setSaveQueryFlag = function (flag) {
        config.runSaveQuery = flag;
    }

    this.getSaveQueryFlag = function () {
        return config.runSaveQuery;
    }

    /**
     * get the color coding of character to make legends in map tab
     * @param char
     * @returns {*}
     */
    this.getLegendForCharacter = function (char) {
        var lg = this.getPJLegendData(char)
        return this.formatPJLegendData(lg);
    }

    this.hexToRgb = function (hex) {
        hex = hex.replace('#', '');
        var rgb = hex.match(/../g);
        for (var i = 0; i < rgb.length; i++) {
            rgb[i] = parseInt(rgb[i], 16);
        }
        return rgb;
    }

    this.formatPJLegendData = function (data) {
        if (!data) {
            return;
        }

        var result = [],
            lg,
            color,
            hex,
            rgb;

        for (var name in data) {
            color = data[name].color;
            if (color.match('#')) {
                rgb = this.hexToRgb(color);
            } else {
                rgb = this.hexToRgb(colourNameToHex(color));
            }

            result.push({
                name: name,
                red: rgb[0],
                green: rgb[1],
                blue: rgb[2],
                count: data[name].count || 0
            });
        }

        return result;
    }

    /**
     * get legend data from pj.
     * @param char
     * @returns {*}
     */
    this.getPJLegendData = function (char) {
        if (!char) {
            return;
        }

        var state = st.colorCoding[char];
        if (!state) {
            state = st.colorCodingQuali[char];
        }

        return state;
    }

    this.getRange = function (name) {
        var range = name && name.match(/\d+\.*\d*/g);
        if (range.length) {
            range[0] = Number.parseFloat(range[0]);
            range[1] = Number.parseFloat(range[1]);
        }
        return range;
    }

    this.getQuantCharacterState = function (val, char) {
        var quant = this.getLegendForCharacter(char),
            name,
            range;

        for (var i = 0; i < quant.length; i++) {
            name = quant[i].name;
            range = pj.getRange(name);
            if (val >= range[0] && val <= range[1]) {
                return name;
            }
        }

        return;
    };

    /**
     * group species by color of character. this function returns an object with hex color as key and
     * array of species list as value.
     */
    this.groupByCharacter = function (char, addEmpty, addNone) {
        var node = this.getSelection();
        var result = {},
            legend,
            state,
            i,
            name;
        node && node.eachSubgraph(function (n) {
            if (n.data.leaf) {
                state = n.data.character[char]
                if (state) {
                    for (i = 0; i < state.length; i++) {
                        name = state[i];
                        switch (typeof name) {
                            case 'number':
                                name = pj.getQuantCharacterState(name, char);
                                break;
                        }
                        if (!result[name]) {
                            result[name] = {
                                list: []
                            }
                        }

                        result[name].list.push(n.name);
                        result[name].name = name;
                    }
                }
            }
        });

        legend = this.getLegendForCharacter(char);
        for (var i = 0; i < legend.length; i++) {
            state = legend[i]
            if (result[state.name]) {
                result[this.toHex(state.red, state.green, state.blue)] = result[state.name];
                delete result[state.name];
            }
        }

        return result;
    }

    /**
     * click on selected node again
     */
    this.clickSelectedNode = function () {
        var nodeId = this.getSelection();
        nodeId && this.clickNode(nodeId.id);
    }

    /**
     * converts rgb to hex
     * returns string
     * #aabbcc
     */
    this.toHex = function (r, g, b) {
        function componentToHex(c) {
            if (typeof c != 'number') {
                c = parseInt(c);
            }
            var hex = c.toString(16);
            return hex.length == 1 ? "0" + hex : hex;
        }

        return '#' + componentToHex(r) + componentToHex(g) + componentToHex(b);
    }

    this.linkout = function (name, obj) {
        var url = config.linkouts[name].url;
        var node = obj.$trigger.data("node");
        url = url.replace("${name}", node.name.replace(' ', '+'));
        window.open(url, '_newtab');
    }
    this.linkoutDisabled = function (name, obj) {
        var node = obj.$trigger.data('node');
        if (node.data.leaf) {
            return false;
        } else {
            return true;
        }
    }
    jQuery.contextMenu({
        selector: '#' + config.injectInto + " canvas",
        trigger: 'none',
        autoHide: false,
        build: function ($trigger, e) {
            var items = {
                "info": {
                    name: "Expand/Collapse",
                    callback: function (key, options) {
                        var node = $trigger.data("node");
                        st.setCollapsed(node);
                        var level = st.nodesExpCol(node);
                        if (level) {
                            st.zoomIndex = level;
                        }
                        st.computePositions(st.graph.getNode(st.root), '');
                        st.plot();
                    },
                    disabled: function (key, options) {
                        return false;
                    }
                }
            }, link;
            for (var i in config.linkouts) {
                link = config.linkouts[i];
                var node = $trigger.data('node');
                if (node.data.leaf) {
                    items[i] = {
                        name: link.displayName,
                        callback: pj.linkout,
                        disabled: pj.linkoutDisabled
                    }
                }
            }
            return {
                callback: function (key, options) {
                    var m = "clicked: " + key;
                    window.console && console.log(m, options.$trigger) || alert(m);
                },
                items: items
            }
        }
    });

    function initPopover() {
        var pops = config.popOver, i, id;
        if ($.cookie('_pji') == "ok") {
            return
        }
        for (i = 0; i < pops.length; i++) {
            id = pops[i].id
            if (typeof id == "function") {
                pops[i].id = id = id();
            }
            $(id).popover(pops[i].options);
            $(id).popover('show');
        }
        $('body').on('click', '.popover #pjPopOverClose', function () {
            console.log('clicked button')
            pj.popOver(false);
            $.cookie('_pji', "ok")
        })
    }

    this.popOver = function (toggle) {
        var pops = config.popOver, i, id;
        for (i = 0; i < pops.length; i++) {
            id = pops[i].id
            if (typeof id == "function") {
                id = id();
            }
            if (toggle) {
                $(pops[i].id).popover('show');
            } else {
                $(pops[i].id).popover('hide');
            }
        }
    }

    /**
     * align leaf names
     * alignName - Boolean
     */
    this.alignNames = function (alignName) {
        st.config.alignName = alignName;
        if (alignName) {
            jQuery('.quant').addClass('quantAlign');
        } else {
            jQuery('.quant').removeClass('quantAlign');
        }
        st.plot();
    };

    /**
     *
     */
    this.savePjSettings = function(){
        // console.log("Save PJ settings " + config.settingsUrl);
        if(config.edit){
            var data = {nodeId:(pj.getSelection()||{}).id}
            $.ajax({
                url: config.settingsUrl,
                data: {
                    json: JSON.stringify(data)
                },
                success: function(){
                    // console.log('saved clicked node to workbook!')
                },
                error: function(error){
                    console.log(error);
                    console.error('could not save selected node to workbook')
                }
            })
        }
    }

    //this.on('treeloaded', initPopover);
    pj.on('click', pj.savePjSettings)

    $(window).on('hashchange', function () {
        if (!config.setNodeToUrlFlag) {
            pj.clickNode(pj.getNodeFromUrl())
        }
        config.setNodeToUrlFlag = false;
    })


    // add this class to make the labels disappear outside the bounds of canvas
    $('#' + config.injectInto).addClass('infovis');

    // console.log('before jit initialized')
    // console.log(config)
    st = new $jit.Phylo(config);
    navigation(config)
    setTree(config)
    //Expose this for use by other tabs
    this.titleViewModel = setTitle(config);
    // console.log('after jit initialized')
};
