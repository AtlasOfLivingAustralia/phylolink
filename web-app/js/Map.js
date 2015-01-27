/**
 * author: temi
 * @constructor
 */
function Map(options) {
    // mixin for event handling functions
    new Emitter(this);
    var $ = jQuery;
    var that = this;
    var i;
    options = $.extend({
        type: 'GET',
        dataType: 'json',
        headerHeight: 0,
        wms: 'http://biocache.ala.org.au/ws/webportal/wms/reflect',
        //flag to check if character has been loaded
        characterloaded: false,
        env: {
            'colormode': 'lineage_ID_s',
            'name': 'circle',
            'size': 4,
            'opacity': 0.8//,
//            'color': 'df4a21'
        }
    }, options);

    var env = options.env;
    var id = options.id;
    var pj = options.pj;
    $('#' + id).height(options.height);
    $('#' + id).width(options.width);
    var query = options.query;
    this.invalidateSize = function () {
        map.invalidateSize(false);
    };
    this.getEnv = function () {
        var str = []
        for (var i in env) {
            if (env[i]) {
                str.push(i + ':' + env[i]);
            }
        }
        return str.join(';');
    };
    this.updateEnv = function () {
        layer.setParams({
            'ENV': this.getEnv(),
            'opacity': env.opacity,
            'outline': outlineCtrl.getValue(),
            'STYLE': 'opacity:'+env.opacity
        });
    };

    //add map here
    var RecordLayerControl = L.Control.extend({
        options: {
            position: 'topright',
            collapsed: false
        },
        onAdd: function (map) {
            // create the control container with a particular class name
            //var $controlToAdd = $('.colourbyTemplate').clone();
            var container = L.DomUtil.create('div', 'leaflet-control-layers');
            var $container = $(container);
            $container.attr("id", "recordLayerControl");
            $('#mapLayerControls').prependTo($container);
            // Fix for Firefox select bug
            var stop = L.DomEvent.stopPropagation;
            L.DomEvent
                .on(container, 'click', stop)
                .on(container, 'mousedown', stop);
            return container;
        }
    });

    var map = L.map(id, {
        fullscreenControl: true,
        fullscreenControlOptions: {
            position: 'topleft'
        }
    }).setView([-27, 133], 3);

    L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
            '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
            'Imagery © <a href="http://mapbox.com">Mapbox</a>',
        id: 'examples.map-i875mjb7'
    }).addTo(map);

    var layer;

    // spinner
    var loadingControl = L.Control.loading({
        spinjs: true
    });

    var outlineCtrl = new L.Control.Checkbox({
        position:'bottomleft',
        text: 'Outline: ',
        onClick: function(){
            that.updateEnv();
        }
    })

    var opactiySlider = new L.Control.Slider({
        position:'bottomleft',
        text: 'Opacity:&nbsp;',
        onChange: function (val) {
            env.opacity = val;
            that.updateEnv();
        },
        sliderOpt: {
            min: 0.1,
            max: 1.0,
            step: 0.1,
            value: env.opacity,
            tooltip: 'hide'
        }
    });

    var sizeSlider = new L.Control.Slider({
        text: 'Size:&nbsp;',
        position:'bottomleft',
        onChange: function (val) {
            env.size = Number.parseInt(val);
            that.updateEnv();
        },
        sliderOpt: {
            min: 1,
            max: 9,
            step: 1,
            value: env.size,
            tooltip: 'hide'
        }
    });

    var colorBy = new L.Control.Select({
        position: 'topright',
        onChange:function(val){
            env.colormode = val;
            legendCtrl.options.urlParams.cm = val;
            legendCtrl.update({})
            that.updateEnv();
        },
        url:options.facetUrl
    });

    var legendCtrl = new L.Control.Legend(options.legend);
    // initializing
    env.colormode = legendCtrl.options.urlParams.cm = colorBy.getValue();

    legendCtrl.update({});

    layer = L.tileLayer.wms(options.layer, {
//        layers: 'Ala occurrence',
        format: 'image/png',
        transparent: true,
        attribution: "PhyloJive",
        bgcolor: "0x000000"
    });


    // add all control and layer to map.
    layer.addTo(map);
    map.addControl(loadingControl);
    map.addControl(new RecordLayerControl());
    map.addControl(outlineCtrl);
    map.addControl(opactiySlider);
    map.addControl(sizeSlider);
    colorBy.addTo(map);
    map.addControl(legendCtrl);

    pj.on('click', function (node) {
        var children = pj.getChildrensName(node);
        var params;
        for (i in children) {
            children[i] = options.filterFieldName + ':"' + children[i] + '"';
        }
        params = children.join('+OR+').replace(/ /g, '+');
        query = options.query;
        layer && map.removeLayer(layer);
        layer = L.tileLayer.wms(options.layer + '&fq=' + params, {
//            layers: 'Ala occurrence',
            format: 'image/png',
            transparent: true,
            attribution: "PhyloJive",
            bgcolor: "0x000000"
        });
        that.updateEnv();
        map.addLayer(layer);
//        legendCtrl.options.urlParams.q = options.query;
        legendCtrl.options.urlParams.fq = params;
        legendCtrl.update({});
    });

    this.updateEnv();
}