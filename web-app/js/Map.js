/**
 * author: temi
 * @constructor
 */
function Map(options) {
    // mixin for event handling functions
    new Emitter(this);
    var events = [
    /**
     * when an ajax function is fired
     */
        'updatestart',
    /**
     * when an ajax function finishes, either on success or failure.
     */
        'updateend'
    ]
    var that = map = this;
    var i,
        records,
        xhrColorBy,
        xhrLegend;
    var $ = jQuery;
    options = $.extend({
        type: 'GET',
        dataType: 'json',
        headerHeight: 0,
        wms: '${grailsApplication.config.biocacheServiceUrl}/webportal/wms/reflect',
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
         * used to add selected characters to color by options
         */
        colorByCharacters: false,
        /**
         * instance of records class. used to get selected data resource properties.
         */
        records: undefined,
        /**
         * popover when user first interacts
         */
        popOver: [
            {
                id: '#' + options.id,
                options: {
                    placement: 'top',
                    trigger: 'manual',
                    html: 'true',
                    content: '<button id="pjPopOverClose" class="btn btn-primary">Okay, got it!</button> '
                }
            },
            {
                id: 'sourceChar',
                options: {
                    placement: 'left',
                    trigger: 'manual',
                    html: 'true',
                    content: 'Action buttons'
                }
            },
            {
                id: 'main .btn',
                options: {
                    placement: 'right',
                    trigger: 'manual',
                    html: 'true',
                    title: 'Tree Leaf',
                    content: 'You can left and right click on this name. Left click interacts with tabs on right.' +
                        'Right click provides you with a list of options to choose from.'
                }
            },
            {
                id: 'charLi',
                options: {
                    placement: 'top',
                    trigger: 'manual',
                    html: 'true',
                    content: '<div id="pjPopOverClose" class="btn btn-primary">Okay, got it!</div> '
                }
            }
        ]
    }, options);

    var env = options.env;
    var id = options.id;
    var pj = options.pj;
    var filter = options.filter,
        character = options.character;

    $('#' + id).height(options.height);
    $('#' + id).width(options.width);
    var query = filter.getQuery();

    this.invalidateSize = function () {
        lmap.invalidateSize(false);
    };

    /**
     * this function simplifies access to the value of the current color by selection
     */
    this.getColorByValue = function(){
        var select = colorBy.getSelectState();
        if(select){
            return select.name;
        }
    };

    /**
     * returns colorby value when facets are  selected. otherwise, undefined.
     * Biocache services with otherwise through error.
     */
    this.getColorMode = function(){
        var select = colorBy.getSelectState();
        if(select && select.type == 'facets'){
            return select.name;
        }
    };

    this.getEnv = function () {
        var str = []
        env['colormode'] = this.getColorMode();
        for (var i in env) {
            if (env[i] && !( (i == 'color') && env['colormode'])) {
                str.push(i + ':' + env[i]);
            }
        }
        return str.join(';');
    };
    
    this.setSpUrl = function (q) {
        var ws = '&ws=' + records.getDataresource().biocacheHubUrl
        var bs = '&bs=' + records.getDataresource().biocacheServiceUrl

        options.spUrl.url(options.spUrl.baseUrl + '?' + q + ws + bs)
    }

    this.updateEnv = function (l) {
        l = l || layer
        if (colorBy.getValue() == 'None') {
            env.color = l.color;
        }
        l && l.setParams({
            'ENV': this.getEnv(),
            'opacity': env.opacity,
            'outline': outlineCtrl.getValue(),
            'STYLE': 'opacity:' + env.opacity
        });
    };

    this.updateLayersEnv = function () {
        for (var i = 0; i < layers.length; i++) {
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

    var lmap = L.map(id, {
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
    }).addTo(lmap);

    var layer, layers = [], spinner = null;

    // spinner
    var loadingControl = L.Control.loading({
        spinjs: true
    });

    var outlineCtrl = new L.Control.Checkbox({
        position: 'bottomleft',
        text: 'Outline: ',
        onClick: function () {
            that.updateLayersEnv();
        }
    })

    var opactiySlider = new L.Control.Slider({
        position: 'bottomleft',
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
        position: 'bottomleft',
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
        url: options.facetUrl,
        defaultValue: options.colorBy.defaultValue
    });

    colorBy.on('change', function (val) {
        env.colormode = val;
        that.updateMap();
        that.updateLegend();
        that.updateLayersEnv();
    });

    var legendCtrl = new L.Control.Legend(options.legend);
    // initializing
    env.colormode = legendCtrl.options.urlParams.cm = colorBy.getValue();
    lmap.addControl(loadingControl);
    lmap.addControl(new RecordLayerControl());
    lmap.addControl(outlineCtrl);
    lmap.addControl(opactiySlider);
    lmap.addControl(sizeSlider);
    colorBy.addTo(lmap);
    lmap.addControl(legendCtrl);

    pj.on('click', function (node, list, ajax) {

        if (ajax) {
            ajax.then(function(){
                that.update();
            }, function () {
                console.log('failed to save query')
            })
        } else {
            that.update();
        }
    });

    this.update = function () {
        this.abortRequests();

        // color mode reset
        colorBy.updateData([]);
        this.updateColorBy();

        // reload legend
        this.updateLegend();

        // update map
        this.updateMap();
    }

    this.updateColorBy = function (f) {
        // do not execute if records tab has not loaded data resource.
        if(!records.isDRLoaded()){
            return;
        }

        f = f || filter;
        var url = options.colorBy.url;
        var that = this;
        var qid = pj.getQid(true),
            drProp = records && records.getDataresource(),
            data = {};

        if(!drProp){
            return
        }

        switch (drProp.type){
            case 'sandbox':
                data = {
                    source: 'sandbox',
                    q: qid,
                    biocacheServiceUrl: drProp.biocacheServiceUrl
                }
                if(!qid){
                    data.q = 'q=data_resource_uid:' + drProp.drid;
                    data.fq = f.formatFq()
                }
                break;
            case 'ala':
                data = {
                    source: 'ala',
                    q: qid,
                    biocacheServiceUrl: drProp.biocacheServiceUrl
                }
                if(!qid){
                    data.q = f.formatQuery(),
                    data.fq = f.formatFq()
                }
                break;

        }

        this.emit('updatestart');
        xhrColorBy = $.ajax({
            url: url,
            data: data,
            method: 'POST',
            success: function (data) {
                data = data || [];
                that.loadColorByData(data)
                that.emit('updateend');
            },
            error: function () {
                console.log('error communicating with colorby service.');
                that.emit('updateend');
            }
        });
    };

    this.updateLegend = function (f) {
        // do not execute if records tab has not loaded data resource.
        if(!records.isDRLoaded()){
            return;
        }

        var cby = colorBy.getSelection();
        if(cby){
            switch ( cby.type()){
                case 'character':
                    this.showLegendsWithCharacter();
                    break;
                case 'facets':
                    this.showLegendWithFacets();
                    break;
            }
        } else {
            this.showLegendDefault();
        }

    };

    /**
     * this function will get legends by querying the webservice.
     */
    this.showLegendWithFacets = function(){
        var data = $.extend({}, options.legend.urlParams),
            url = options.legend.baseUrl,
            dr = records.getDataresource() || {},
            cby = colorBy.getSelection();
        data.cm = cby.name() || '';
        data.q = pj.getQid(true);
        data.source = dr.type;
        data.biocacheHubUrl = dr.biocacheHubUrl;
        // if query id is not present.
        if(!data.q){
            data.q = filter.formatQuery();
            data.fq = filter.formatFq();
        }

        // if cm is not defined, this url will produce an error.
        if (data.cm) {
            this.emit('updatestart');
            xhrLegend  = $.ajax({
                url: url,
                data: data,
                success: function (data) {
                    legendCtrl.legend(data);
                    that.emit('updateend');
                },
                error: function(){
                    that.emit('updateend');
                }
            });
        }
    }

    /**
     * show legends using character data
     */
    this.showLegendsWithCharacter = function(){
        var sel = colorBy.getSelection()
            char = sel.name();
        legendCtrl.legend(pj.getLegendForCharacter(char));
    }

    this.showLegendDefault = function(){
        legendCtrl.legend( options.legend.defaultValue);
    }

    this.updateMap = function (f) {
        // do not execute if records tab has not loaded data resource.
        if(!records.isDRLoaded()){
            return;
        }

        var qid = pj.getQid(true),
            cm = colorBy.getSelectState(),
            url,
            type,
            dr = records.getDataresource();

        this.removeLayers();
        url = dr.layerUrl;
        type = cm && cm.type || 'facets';
        switch (type){
            case 'character':
                this.mapWithCharacter(url, cm.name);
                break;
            case 'facets':
            default :
                this.mapWithFacets(url);
                break;
        }
    };

    this.mapWithFacets = function(query){
        query += '?q=' + pj.getQid(true);
        var layer = L.tileLayer.wms(query, {
            format: 'image/png',
            transparent: true,
            attribution: "PhyloLink",
            bgcolor: "0x000000"
        });
        layers.push(layer);
        options.env.color = options.legend.defaultValue[0].hex;
        this.updateEnv(layer);
        lmap.addLayer(layer);
        this.setSpUrl('&ly.1=' + 'Phylolink' + '&ly.1.s=' + parseInt('0x' + options.env.color) + '&ly.1.q=' + pj.getQid(true))
    }

    this.mapWithCharacter = function(query, char){
        var groups = pj.groupByCharacter(char, true, true),
            list,
            that = this;
        var spUrl = ''
        var c = 1
        for (i in groups) {
            list = groups[i].list;
            if (list.length) {
                this.emit('updatestart');
                ajax = pj.saveQuery(undefined, list, true);
                ajax.color = i;
                ajax.then(function (q, status, ajx) {
                    var qid = 'qid:' + q.qid;
                    var q = query + '?q=' + qid;
                    var layer = L.tileLayer.wms(q, {
                        format: 'image/png',
                        transparent: true,
                        attribution: "PhyloLink",
                        bgcolor: "0x000000"
                    });

                    layers.push(layer);
                    options.env.colormode = undefined;
                    // passing color to use in updateEnv method
                    layer.color = options.env.color = ajx.color.replace('#', '');
                    that.updateEnv(layer);
                    lmap.addLayer(layer)
                    spUrl += '&ly.' + c + '=' + list[0] + '&ly.' + c + '.s=' + parseInt('0x' + layer.color) + '&ly.' + c + '.q=' + qid
                    c = c + 1
                    that.emit('updateend');
                }, function(){
                    that.emit('updateend');
                });
            }
        }
        this.setSpUrl(spUrl)
    }

    this.removeLayers = function () {
        var i;
        if (layers.length) {
            while (i = layers.pop()) {
                lmap.removeLayer(i);
            }
        }
    }

    /**
     * converts groupbycharacter output to a format understood by legends
     * @param data
     * @returns {Array}
     */
    this.makeLegend = function (data) {
        var result = [], rgb
        for (var color in data) {
            rgb = this.hexToRgb(color);
            result.push({
                red: rgb[0],
                green: rgb[1],
                blue: rgb[2],
                name: data[color].name
            });

        }
        return result;
    }

    this.hexToRgb = function (hex) {
        hex = hex.replace('#', '');
        var rgb = hex.match(/../g);
        for (var i = 0; i < rgb.length; i++) {
            rgb[i] = parseInt(rgb[i], 16);
        }
        return rgb;
    }

    this.beginSpinner = function () {
        var spin;
        spin = new Spinner(options.spinner)
        spin.spin();
        $('#' + options.id).append(spin.el)
        spinner && spinner.stop();
        spinner = spin;
    }
    this.endSpinner = function () {
        spinner && spinner.stop();
    }

    this.setLayerUrl = function (url) {
        options.layer = url;
    }

    this.setDataSource = function (src) {
        options.source = src;
    }

    /**
     * passing the records instance
     * @param rec
     */
    this.setRecords = function(rec){
        records = options.records =  rec;
    }

    /**
     * fill the drop down options of colorby with the data provided. It has a flag to include characters from
     * character tab.
     * @param data - Array - array of color by options.
     */
    this.loadColorByData = function(data){
        var cols = [],
            chars,
            selectedValue = options.legend.defaultValue;
        if(options.colorByCharacters){
            chars = character.getSelectedCharacters();
            chars.forEach(function(item){
                cols.push(map.convertCharacterToColorby(item));
            });

            if(Array.isArray(data)){
                data = cols.concat(data);
            } else {
                data =cols;
            }
        }
        colorBy.updateData(data);
    };

    /**
     * convert character to colorby option
     */
    this.convertCharacterToColorby = function( character){
       return {
           'displayName':character.name,
           'name': character.name,
           'type': 'character'
       }
    }

    this.abortRequests = function(){
        // cancel all ajax requests.
        xhrColorBy && xhrColorBy.abort();
        xhrColorBy = null;
        xhrLegend && xhrLegend.abort();
        xhrLegend = null;
        this.removeLayers();
        // destroy spinner
        this.endSpinner();
    }

    options.character && options.character.on('treecolored', function () {
        // color mode reset
        colorBy.updateData([]);

        // color mode reload
        that.updateColorBy();
        that.updateMap();
    })
    this.on('updatestart', function () {
        that.beginSpinner();
    })
    this.on('updateend', function () {
        that.endSpinner();
    })

    /**
     * View model for controls that are outside the map itself, such as the download option
     */
    this.MapViewModel = function (downloadViewModel) {
        var self = this;

        self.downloadViewModel = downloadViewModel;

        /**
         * Downloads the occurrence data for the current map
         */
        self.downloadMapData = function () {
            var qid = pj.getQid(true);
            var url = records.getDataresource().biocacheServiceUrl + "/occurrences/index/download";
            var email = self.downloadViewModel.email();
            if (email === undefined) {
                email = '';
            }
            console.log('url')
            console.log(url)

            url = url + "?q=" + qid + "&reasonTypeId=" + self.downloadViewModel.reason().id() + "&email=" + email;

            $("<a style='display: none' href='" + url + "' download='data.zip'>download data</a>").appendTo('body')[0].click();
            $(".closeDownloadModal").filter(":visible").click();
        };
    };

    this.downloadViewModel = new utils.OccurrenceDownloadViewModel(options.downloadReasonsUrl);
    this.mapViewModel = new this.MapViewModel(this.downloadViewModel);
    this.mapViewModel.spUrl = options.spUrl

    this.initialiseBindings = function () {
        ko.applyBindings(this.mapViewModel, document.getElementById("mapControls"));
    };

    this.initialiseBindings();
}