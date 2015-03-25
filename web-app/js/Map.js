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
    var $ = jQuery;
    options = $.extend({
        type: 'GET',
        dataType: 'json',
        headerHeight: 0,
        wms: 'http://biocache.ala.org.au/ws/webportal/wms/reflect',
        //flag to check if character has been loaded
        characterloaded: false,
        spinner: {
            top: '50%',
            left: '50%',
            className: 'loader'
        },
        env: {
            'colormode': undefined,
            'name': 'circle',
            'size': 4,
            'opacity': 0.8,
            'color': 'df4a21'
        },
        showCharacterOnMap: true,
        /**
         * popover when user first interacts
         */
        popOver: [{
            id: '#'+ options.id,
            options:{
                placement: 'top',
                trigger: 'manual',
                html: 'true',
                content : '<button id="pjPopOverClose" class="btn btn-primary">Okay, got it!</button> '
            }
        },{
            id:'sourceChar',
            options:{
                placement: 'left',
                trigger: 'manual',
                html: 'true',
                content : 'Action buttons'
            }
        },{
            id:'main .btn',
            options:{
                placement: 'right',
                trigger: 'manual',
                html: 'true',
                title : 'Tree Leaf',
                content: 'You can left and right click on this name. Left click interacts with tabs on right.' +
                    'Right click provides you with a list of options to choose from.'
            }
        },{
            id: 'charLi',
            options:{
                placement: 'top',
                trigger: 'manual',
                html: 'true',
                content : '<div id="pjPopOverClose" class="btn btn-primary">Okay, got it!</div> '
            }
        }]
    }, options);

    var env = options.env;
    var id = options.id;
    var pj = options.pj;
    var filter = options.filter;

    $('#' + id).height(options.height);
    $('#' + id).width(options.width);
    var query = filter.getQuery();

    this.invalidateSize = function () {
        map.invalidateSize(false);
    };

    this.getEnv = function () {
        var str = []
        env['colormode'] = colorBy.getValue();
        if(env['colormode'] == 'None'){
            env['colormode'] = undefined;
        }
        for (var i in env) {
            if (env[i] && !( (i == 'color') && env['colormode']) ) {
                str.push(i + ':' + env[i]);
            }
        }
        return str.join(';');
    };

    this.updateEnv = function ( l ) {
        l = l || layer
        if(colorBy.getValue() == 'None'){
            env.color = l.color;
        }
        l && l.setParams({
            'ENV': this.getEnv(),
            'opacity': env.opacity,
            'outline': outlineCtrl.getValue(),
            'STYLE': 'opacity:'+env.opacity
        });
    };

    this.updateLayersEnv = function(){
        for(var i = 0;i <layers.length; i++){
            this.updateEnv(layers[i]);
        }
    }

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

    var layer, layers = [], spinner=[];

    // spinner
    var loadingControl = L.Control.loading({
        spinjs: true
    });

    var outlineCtrl = new L.Control.Checkbox({
        position:'bottomleft',
        text: 'Outline: ',
        onClick: function(){
            that.updateLayersEnv();
        }
    })

    var opactiySlider = new L.Control.Slider({
        position:'bottomleft',
        text: 'Opacity:&nbsp;',
        onChange: function (val) {
            env.opacity = val;
            that.updateLayersEnv();
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
            env.size = parseInt(val);
            that.updateLayersEnv();
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
        url:options.facetUrl
    });
    colorBy.on('change', function(val){
        env.colormode = val;
        that.updateMap();
//        legendCtrl.options.urlParams.cm = val;
        that.updateLegend();
        that.updateLayersEnv();
    });
    var legendCtrl = new L.Control.Legend(options.legend);
    // initializing
    env.colormode = legendCtrl.options.urlParams.cm = colorBy.getValue();

    legendCtrl.update({});

    map.addControl(loadingControl);
    map.addControl(new RecordLayerControl());
    map.addControl(outlineCtrl);
    map.addControl(opactiySlider);
    map.addControl(sizeSlider);
    colorBy.addTo(map);
    map.addControl(legendCtrl);

    pj.on('click', function (node,list,ajax) {
        var update = function(){
            // color mode reset
            colorBy.updateData([]);

            // color mode reload
//        colorBy.updateUrl(filter.format(colorBy.url));
            that.updateColorBy();

            // set env variable
            // set legend variables
            // reload legend
            that.updateLegend();

            // update map
            that.updateMap();
        }

        if(ajax){
            ajax.then(update,function(){
                console.log('failed to save query')
            })
        } else {
            update();
        }
    });

    this.updateColorBy = function( f ){
        f = f || filter;
        var url = options.colorBy.url;
        var that = this;
        var qid = pj.getQid(true), data;
        if(qid){
            data = {
                q:qid,
                source: options.source
            }
        } else {
            data = {
                q: f.formatQuery(),
                fq: f.formatFq(),
                source: options.source
            }
        }
        $.ajax({
            url: url,
            dataType:options.colorBy.dataType,
            data:data,
            success: function( data ){
                data = data || [];
                colorBy.updateData( data );
            },
            error: function(){
                console.log('error communicating with colorby service.');
            }
        });
    };

    this.updateLegend = function(f){
        f = f || filter;
        var that = this;
        var params =[];
        var data = options.legend.urlParams;
        data.cm = colorBy.getValue() || '';
//        data.q = f.getQuery();
        data.q = pj.getQid(true);
//        var url = f.format(options.legend.baseUrl)
        var url = options.legend.baseUrl
        if( data.cm ){
            if(options.legend.proxy){
                data.cm && params.push('cm='+data.cm);
                data.type && params.push('type='+data.type);
                data.q && params.push('q='+data.q);
                if(url.split(/\?/g).length > 1){
                    url = url + '&' + params.join('&');
                } else {
                    url = url + '?' + params.join('&');
                }
                data = {};
                url =  options.legend.proxyUrl + '?url='+encodeURIComponent(url);
            }

            $.ajax({
                url: url,
                data:data,
                dataType:options.legend.dataType,
                success:function(data){
                    legendCtrl.legend(data);
                }
            });
        }
//        else {
//            legendCtrl.legend(legendCtrl.options.defaultData);
//        }
    };

    this.updateMap = function(f){
        var qid = pj.getQid(true),
            groups, i, list, ajax,ldata=[], cm, spinner;
        var query = filter.format(options.layer)
        if(qid){
            query = options.layer + '?q=' + qid
        }
        f = f || filter;
        cm = colorBy.getValue();
        this.removeLayers();
        // only if query exist add layer. otherwise, the whole atlas data gets added.
        if(options.showCharacterOnMap && ( !cm || (cm == 'None' ))){
            spinner = new Spinner(options.spinner)
            spinner.spin();
            $('#'+options.id).append(spinner.el)
            groups = pj.groupByCharacter(true,true);
            for(i in groups){
                list = groups[i].list;
                if(list.length){
                    ajax = pj.saveQuery(undefined, list, true);
                    ajax.color = i;
                    ajax.then(function(q,status,ajx){
                        var qid = 'qid:'+q.qid;
                        var query = options.layer + '?q=' + qid;
                        var layer = L.tileLayer.wms(query, {
                            format: 'image/png',
                            transparent: true,
                            attribution: "PhyloLink",
                            bgcolor: "0x000000"
                        });
                        console.log(layer);

                        layers.push(layer);
                        options.env.colormode = undefined;
                        // passing color to use in updateEnv method
                        layer.color = options.env.color = ajx.color.replace('#','');
                        that.updateEnv( layer );
                        map.addLayer(layer)
                        spinner.stop();
                    })

                    //update legend with the colors
                    legendCtrl.legend(that.makeLegend(groups));
                }
            }
        } else if( f.getQuery() || f.getFq() ||  (options.source != 'ala')){
            console.log(query);
            layer = L.tileLayer.wms(query, {
                format: 'image/png',
                transparent: true,
                attribution: "PhyloLink",
                bgcolor: "0x000000"
            });
            layers.push(layer);
            that.updateLayersEnv();
            map.addLayer(layer);
            that.updateLegend()
        }
    };

    this.removeLayers = function(){
        var i;
        if(layers.length){
            while(i = layers.pop()){
                map.removeLayer(i);
            }
        }
    }

    /**
     * converts groupbycharacter output to a format understood by legends
     * @param data
     * @returns {Array}
     */
    this.makeLegend= function(data){
        var result = [], rgb
        for(var color in data){
            rgb = this.hexToRgb(color);
            result.push({
                red: rgb[0],
                green: rgb[1],
                blue: rgb[2],
                name: data[color].name
            });

//            for(var i =0;i < data[color].length;i++){
//                result.push({
//                    red: rgb[0],
//                    green: rgb[1],
//                    blue:rgb[2],
//                    name: data[color][i]
//                });
//            }
        }
        return result;
    }

    this.hexToRgb = function(hex){
        hex = hex.replace('#','');
        var rgb = hex.match(/../g);
        for( var i =0;i<rgb.length;i++){
            rgb[i] = parseInt( rgb[i], 16 );
        }
        return rgb;
    }

    this.beginSpinner = function(){
        var spin;
        spin = new Spinner(options.spinner)
        spin.spin();
        $('#'+options.id).append(spin.el)
        spinner.push(spin)
    }
    this.endSpinner = function(){
        var spin
        while(spin = spinner.pop()){
            spin.stop();
        }
    }

    options.character && options.character.on('treecolored',function() {
        // color mode reset
        colorBy.updateData([]);

        // color mode reload
        that.updateColorBy();

        // set env variable
        // set legend variables
        // reload legend
//        that.updateLegend();

        that.updateMap();
    })
    pj.on('savequerybegin', function(){
        that.beginSpinner();
    })
    pj.on('savequeryend', function(){
        that.endSpinner();
    })
//    this.updateEnv();
//    that.updateMap();
//    this.updateColorBy();
//    this.updateLegend();
}