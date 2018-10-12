/**
 *
 * This code is used by the Analysis tab in the UI. The HTML is in the _plots.gsp template.
 *
 */


var Habitat = function (c) {
    var $ = jQuery;
    new Emitter(this);
    /**
     * Events
     *
     *
     *
     */
    var events = [
    /**
     * save state to database
     */
        'sync',
    /**
     * when layer metadata is successfully loaded
     */
        'layermetadataadded'
    ]
    var config = $.extend({
        bootstrap: 2,
        list: [],
        listUrl: 'http://localhost:8080/phylolink/ala/getAllLayers',
        graph: {
            url: 'http://localhost:8080/phylolink/phylo/getHabitat',
            type: 'POST',
            dataType: 'JSON',
            xAxisContextual: 'Habitat states',
            xAxisEnvironmental: 'values',
            yAxis: 'Occurrence count'
        },
        dataType: 'JSONP',
        googleChartsLoaded: false,
        delayedChartCall: [],
        chartWidth: '100%',
        chartHeight: 200,
        chartAreaHeight: 100,
        headerHeight:60,
        tabId: 'tab',
        /**
         * sync flag
         *
         */
        doSync: true,
        /**
         * address to sync to
         */
        syncUrl: 'http://localhost:8080/phylolink/phylo/saveHabitat',
        syncType: 'POST',
        syncData: {
            id: 4
        },
        /**
         * save query flags
         */
        doSaveQuery: true,
        saveQuery:{
            url: 'http://localhost:8080/phylolink/ala/saveQuery',
            type: 'POST',
            dataType: 'JSONP'
        },
        /**
         * popover when user first interacts
         */
        popOver: [{
            id: '#'+ c.tabId,
            options:{
                placement: 'top',
                trigger: 'manual',
                html: 'true',
                content : '<button id="habitatPopOverClose" class="btn btn-primary">Okay, got it!</button> '
            }
        },{
            id: '#habitatMain .btn',
            options:{
                placement: 'bottom',
                trigger: 'manual',
                html: 'true',
                content : 'Select an environmental layer like precipitation to graph the profile of a species'
            }
        }],
        records: undefined
    }, c);

    var pj = config.pj,
        hab = this,
        id = config.id,
        updateMetadata = [];

    /**
     * stores query id for a node.
     */
    var qid;

    if (config.listUrl) {
        $.ajax({
            url: config.listUrl,
            dataType: 'JSON',
            success: function (data) {
                while (config.list.pop()) {
                    // empty list
                }
                for (var i = 0; i < data.length; i++) {
                    data[i].value = data[i].id;
                    config.list.push(data[i]);
                }
                hab.emit('layermetadataadded')
            },
            error: function (xhr, ajaxOptions, thrownError) {
                console.error(xhr.status);
                console.error("Error retrieving lists of layers: " + xhr.responseText);
            }
        })
    }

    var Habitat = function (c) {
        this.name = ko.observable(c.name);
        this.displayName = ko.observable(c.displayName);
        this.id = ko.observable(c.id);
        this.id_metadata = ko.observable(c.id_metadata);
        this.mdDescription = ko.observable(c.description||'');
        this.mdNotes = ko.observable(c.notes||'');
        this.mdMin = ko.observable(c.environmentalvaluemin||'');
        this.mdMax = ko.observable(c.environmentalvaluemax||'');
        this.mdUnits = ko.observable(c.environmentalvalueunits||'');
        this.mdUrl = ko.observable(c.url||'');
        this.xAxis = ko.observable(c.xAxis||'');
        this.yAxis = ko.observable(c.yAxis||'');
        this.sampleSize = ko.observable();
        this.leastFrequent = ko.observable();
        this.leastFrequentCount = ko.observable();
        this.mostFrequent = ko.observable();
        this.mostFrequentCount = ko.observable();
        this.min = ko.observable();
        this.max = ko.observable();
        this.mean = ko.observable();
        this.median = ko.observable();
        this.standardDeviation = ko.observable();
        this.numeric = ko.observable();
        this.loading = ko.observable(false);

        var frequency;
        var spinner = new Spinner({
            top: '50%',
            left: '50%',
            className: 'loader'
        });
        this.setFrequency = function (freq) {
            frequency = freq;
        };
        this.getFrequency = function () {
            return frequency;
        };
        this.startLoading = function () {
            if (!spinner || !spinner.el) {
                spinner = new Spinner({
                    top: '50%',
                    left: '50%',
                    className: 'loader'
                });
            }
            this.loading(true);
            spinner.spin();
            $('#' + this.id()).parent().append(spinner.el);
        };
        this.stopLoading = function () {
            spinner.stop();
            this.loading(false);
        };
    };

    var HabitatViewModel = function () {
        new Emitter(this);
        var self = this;
        var events = [
        /**
         * when a model is reordered in the list
         */
            'moved',
        /**
         * when a model is deleted
         */
            'removed',
        /**
         * when selected value is changed for another.
         * params:
         *  habitat- current model
         *  initialization flag - boolean
         */
            'changed'
        ];
        self.habitats = ko.observableArray();
        self.selectedHabitat = ko.observable();
        self.count = ko.observable(1);

        self.downloadViewModel = new utils.OccurrenceDownloadViewModel(config.downloadReasonsUrl);

        /**
         *
         * @param init
         */
        self.initialize = function (init) {
            var habitats = init.habitats,
                temp, habitat,
                metadata,
                count = init.count || 0;

            if( !habitats || (count === undefined)) {
                return;
            }

            for (var i in habitats) {
                temp = habitats[i];
                temp.id = temp.id || this.generateHabitatId(i);
                temp.id_metadata = temp.id_metadata || temp.id + 'metadata';


                //var rename catch
                if (temp.description === undefined) temp.description = temp.mdDescription
                if (temp.notes === undefined) temp.notes = temp.mdNotes
                if (temp.url === undefined) temp.url = temp.mdUrl
                if (temp.environmentalvaluemax === undefined) temp.environmentalvaluemax = temp.mdMax
                if (temp.environmentalvaluemin === undefined) temp.environmentalvaluemin = temp.mdMin
                if (temp.environmentalvalueunits === undefined) temp.environmentalvalueunits = temp.mdUnits


                habitat = new Habitat(temp);
                self.habitats.push(habitat);
                self.emit('changed', habitat, true);

                // add habitat to this list so that after layer metadata loads, metadata for this habitat is populated.
                if(!temp.description && !temp.mdDescription){
                    updateMetadata.push(habitat);
                }

            }
            self.count(count);
            self.selectedHabitat(null);
        };

        /**
         * auto generate habitat id
         * @returns {string}
         */
        self.generateHabitatId = function(num){
            var count = num || this.count()
            return 'habitat-' + count;
        }
        /**
         * add a habitat to list
         */
        self.addHabitat = function () {
            var habitat = new Habitat({name: '', displayName: '', id: 'habitat-' + self.count(),
                id_metadata: 'habitat-metadata-' + self.count(), description: '', url: '',
                environmentalvaluemin: '', environmentalvaluemax: '', environmentalvalueunits: '', notes: ''});
            self.count(self.count() + 1);
            self.selectedHabitat(habitat);
            self.habitats.push(habitat);
        };

        /**
         * is the passed character same as the selected character?
         * @param character
         * @returns {boolean}
         */
        self.isHabitatSelected = function (character) {
            return character === self.selectedHabitat();
        };

        /**
         * remove a habitat from list
         * @param data
         * @param event
         */
        self.removeHabitat = function (data, event) {
            self.habitats.remove(data);
            self.emit('removed');
        };

        self.clearHabitat = function (data, event) {
            if (data === self.selectedHabitat()) {
                self.selectedHabitat(null);
            }

            if (data.name() == "") {
                self.removeHabitat(data);
            }
        };

        /**
         * change the selected item
         */
        self.changeHabitat = function (data) {
            var xaxis = '';
            switch (data.type) {
                case 'Contextual':
                    xaxis = config.graph.xAxisContextual;
                    // console.log('cont')
                    break;
                case 'Environmental':
                    xaxis = data.environmentalvalueunits || config.graph.xAxisEnvironmental;
                    // console.log('env');
                    break;
            }
            // console.log(xaxis);
            self.selectedHabitat().displayName(data.label);
            self.selectedHabitat().name(data.value);
            self.selectedHabitat().xAxis(xaxis);
            self.selectedHabitat().yAxis(config.graph.yAxis);
            self.selectedHabitat().mdDescription(data.description === undefined ? '' : data.description);
            self.selectedHabitat().mdNotes(data.notes === undefined ? '' : data.notes );
            self.selectedHabitat().mdMin(data.environmentalvaluemin === undefined ? '' : data.environmentalvaluemin);
            self.selectedHabitat().mdMax(data.environmentalvaluemax === undefined ? '' : data.environmentalvaluemax );
            self.selectedHabitat().mdUnits(data.environmentalvalueunits === undefined ? '' : data.environmentalvalueunits);
            self.selectedHabitat().mdUrl(data.url === undefined ? '' : data.url);
            self.emit('changed', self.selectedHabitat());
        };

        self.updateChart = function(habitat, list){
            if (config.records.getDataresource() !== undefined) {
                var data = {
                    speciesList: JSON.stringify(list),
                    config: habitat.name(),
                    biocacheServiceUrl: config.records.getDataresource().biocacheServiceUrl
                };
                if (config.doSaveQuery) {

                    self.saveQuery(data).then(function (qid) {
                        var data = {
                            q: "qid:" + qid,
                            biocacheServiceUrl: config.records.getDataresource().biocacheServiceUrl
                        };
                        self.updateChartDirect(habitat, data);
                    });

                } else {
                    self.updateChartDirect(habitat, data);
                }
                utils.updateCladeInfo(list)
            }
        };

        self.saveQuery = function(params){
            return $.ajax({
                url: config.saveQuery.url,
                data: params,
                dataType: config.saveQuery.dataType,
                type: config.saveQuery.type,
                success: function(qid){
                    //todo: how to pass this value?
                }
            })
        };

        self.updateChartDirect = function (habitat, params) {
            var id = habitat.id();
            habitat.startLoading();

            $.ajax({
                url: config.graph.url,
                type: config.graph.type,
                dataType: config.graph.dataType,
                data: params,
                success: function (temp) {
                    habitat.stopLoading();

                    var data = temp.data;

                    var len = Math.min(5, temp.statisticSummary.leastFrequent.items.length) + 1;
                    var leastFrequent = temp.statisticSummary.leastFrequent.items.slice(0, len - 1).join(", ");
                    if (temp.statisticSummary.leastFrequent.items.length > 5) {
                        leastFrequent += ", ..."
                    }
                    len = Math.min(5, temp.statisticSummary.mostFrequent.items.length) + 1;
                    var mostFrequent = temp.statisticSummary.mostFrequent.items.slice(0, len - 1).join(", ");
                    if (temp.statisticSummary.mostFrequent.items.length > 5) {
                        mostFrequent += ", ..."
                    }

                    habitat.sampleSize(temp.statisticSummary.sampleSize);
                    habitat.leastFrequent(leastFrequent);
                    habitat.leastFrequentCount(temp.statisticSummary.leastFrequent.count);
                    habitat.mostFrequent(mostFrequent);
                    habitat.mostFrequentCount(temp.statisticSummary.mostFrequent.count);
                    habitat.numeric(temp.statisticSummary.numeric);
                    habitat.min(temp.statisticSummary.min);
                    habitat.max(temp.statisticSummary.max);
                    habitat.mean(temp.statisticSummary.mean);
                    habitat.median(temp.statisticSummary.median);
                    habitat.standardDeviation(temp.statisticSummary.standardDeviation);

                    habitat.setFrequency(data);
                    if (data == undefined || data.length == 0) {
                        hab.columnchart(id, [
                            ['', ''],
                            [0, 0]
                        ], view.getOptions(habitat));
                        return;
                    }

                    hab.columnchart(id, data, view.getOptions(habitat), habitat);
                },
                error: function () {
                    // if failed to load resource stop loading gif
                    habitat.stopLoading();
                }
            })
        };

        /**
         * google chart options. sets x & y axis title.
         * @param habitat
         * @returns {{width: *, height: *, legend: {position: string}, vAxis: {title: *}, hAxis: {title: *}}}
         */
        self.getOptions = function (habitat) {
            var c = {
                width: $('#'+config.id+' .panel-body:first').width() ||config.chartWidth,
                height: config.chartHeight,
                chartArea: { top: 10 },
                legend: { position: 'none' },
                vAxis: {
                    title: habitat.yAxis()
                },
                hAxis: {
                    title: habitat.displayName() + '(' + habitat.mdUnits() + ')'
                }
            };
            if (typeof habitat.getFrequency()[1][0] !== 'number') {
                c.chartArea.height = config.chartAreaHeight
            } else {
                c.chartArea.height = config.chartHeight - 40
            }
            return c;
        };

        /**
         *
         * @param habitat
         */
        self.refreshHabitat = function (habitat) {
            if (config.records.getDataresource() !== undefined) {
                var data, list;
                var qid = pj.getQid(true);
                if (qid) {
                    data = {
                        q: qid,
                        config: habitat.name(),
                        biocacheServiceUrl: config.records.getDataresource().biocacheServiceUrl
                    };
                    self.updateChartDirect(habitat, data);
                } else {
                    list = pj.getChildrenName(pj.getSelection());
                    data = {
                        speciesList: JSON.stringify(list),
                        config: habitat.name()
                    };
                    self.updateChartDirect(habitat, data);
                    utils.updateCladeInfo(list)
                }
            }
        };

        /**
         * handler when list is reordered.
         */
        self.onMove = function () {
            self.emit('moved');
        };

        /**
         * Downloads the faceted source data for the selected plot in CSV format
         * @param habitat The model of the selected plot
         */
        self.downloadSummaryCsv = function (habitat) {
            var qid = pj.getQid(true),
                biocacheServiceUrl = config.records.getDataresource().biocacheServiceUrl;

            $.ajax({
                url: config.downloadSummaryUrl,
                type: 'GET',
                data: {
                    q: qid,
                    config: habitat.name(),
                    biocacheServiceUrl: biocacheServiceUrl
                },
                success: function (csv) {
                    var uri = 'data:application/csv;charset=UTF-8,' + encodeURIComponent(csv);
                    $("<a style='display: none' href='" + uri + "' download='data.csv'>download data</a>").appendTo('body')[0].click()
                }
            });
        };

        /**
         * Downloads the source occurrence records for all plots
         */
        self.downloadOccurrenceData = function () {
            var qid = pj.getQid(true);

            var fields = [];
            for (var i = 0; i < view.habitats().length; i++) {
                fields.push(view.habitats()[i].name());
            }

            var email = self.downloadViewModel.email();
            if (email === undefined) {
                email = '';
            }

            var url = config.records.getDataresource().biocacheServiceUrl + '/occurrences/index/download'
                + "?q=" + qid
                + "&reasonTypeId=" + self.downloadViewModel.reason().id()
                + "&email=" + email
                + "&extra=" + fields.join(",");

            $("<a style='display: none' href='" + url + "' download='data.zip'>download data</a>").appendTo('body')[0].click();
            $(".closeDownloadModal").filter(":visible").click();
        };

        self.togglePanel = function(data, event, panelId, toggleId) {
            $('#' + panelId).toggleClass('show hide');
            $('#' + toggleId).toggleClass('fa-angle-double-down fa-angle-double-up');
        }

        /**
         * code added by adam to toggle layer information
         * @param i
         */
        self.showInfo = function (koObj, e) {
            var i = e.target;
            var t = $("div[name=\'" + i.id + "\'")[0];
            if (t.style.display === "none") t.style.display = "block"; else t.style.display = "none";
        }
    };

    ko.bindingHandlers.select = {
        init: function (element, valueAccessor, innerFn, data, koObj) {
            // console.log('init visible and select handler');
            var input = $(element).find("input");
            var tree = $(element).parent().find('div[id="jqxTree"]');

            // input.autocomplete(config.list);

            input.focus();
            $("#"+config.id).scroll(function(e){
                if( input.is(':visible')){
                    $('.ui-autocomplete').position({
                        my: "left top",
                        at: "left bottom",
                        collision: "none",
                        of: input,
                        within: '#'+config.id
                    });
                }
            });


            input.treeSelect = function (item) {
                $(this).val(item.label);
                var self = koObj.$root;
                self.changeHabitat(item, data)
                self.selectedHabitat(null);
            }

            utils.setupTree(config, tree, input)
        },
        update: function (element, valueAccessor, innerFn, data, koObj) {
            // console.log('update function');
            ko.bindingHandlers.visible.update(element, valueAccessor);
            if (valueAccessor()) {
                // focus on input tag once clicked to edit
                $(element).find("input[id='layerCombobox']").focus().select();
                // $(element).find("input[id='layerCombobox']").autocomplete("search", "")
                // $(element).find("input[id='layerCombobox']").autocomplete(config.list);
                
            }
        }
    };

    ko.bindingHandlers.addHabitatChart = {
        update: function (el, valueAccessor, innerFn, data, koObj) {
            if (valueAccessor()) {
                var id = data.id();
                var temp = data.getFrequency();
                if (temp == undefined || temp.length == 0) {
                    return;
                }
                hab.columnchart(id, temp, view.getOptions(data));
            }
        }
    };

    var view = new HabitatViewModel();
    ko.applyBindings(view, document.getElementById(config.id));

    view.on('changed', function (habitat) {
        view.refreshHabitat(habitat);
    });
    
    this.redraw = function() {
        var i;
        var habitats = view.habitats();
        for (i = 0; i < habitats.length; i++) {
            if (habitats[i].chartopt !== undefined) {
                hab.columnchart(habitats[i].chartid, habitats[i].chartdata, habitats[i].chartopt)
            }
        }
    }

    this.refresh = function (node, list, saveQuery) {
        if (config.records.getDataresource() !== undefined) {
            var habitats = view.habitats();
            var i, data;

            if (saveQuery) {
                saveQuery.then(function (qid) {
                    var data = {
                        q: pj.getQid(true),
                        biocacheServiceUrl: config.records.getDataresource().biocacheServiceUrl
                    }
                    for (i = 0; i < habitats.length; i++) {
                        data.config = habitats[i].name();
                        view.updateChartDirect(habitats[i], data);
                    }

                }, function (qid) {
                    // console.log('failed')
                });
            } else {
                for (i = 0; i < habitats.length; i++) {
                    data = {
                        speciesList: JSON.stringify(list),
                        config: habitats[i].name(),
                        biocacheServiceUrl: config.records.getDataresource().biocacheServiceUrl
                    };

                    view.updateChartDirect(habitats[i], data);
                }
            }
            utils.updateCladeInfo(list)
        }
    };

    this.save = function () {
        if (!config.doSync) {
            return;
        }

        var data = hab.currentState();
        $.ajax({
            url: config.syncUrl,
            type: config.syncType,
            data: data,
            success: function (data) {
                // console.log('saved!');
            },
            error: function () {
                // console.log('error saving!');
            }
        });
    }

    pj.on('click', this.refresh);
    this.on('sync', this.save);
    view.on('changed', function (habitat, init) {
        // do not save when initializing the charts. changed event is fired there too.
        !init && hab.emit('sync');
    });
    view.on('removed', function () {
        hab.emit('sync');
    });
    view.on('moved', function () {
        hab.emit('sync');
    });
    /**
     * draws a histogram. currently using google charts
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.histogram = function (id, data, opt) {
        if (config.googleChartsLoaded) {
            var chart = new google.visualization.Histogram(document.getElementById(id));
            data = google.visualization.arrayToDataTable(data);
            chart.draw(data, opt);
        } else {
            // some times google chart is not ready when this function is called
            config.delayedChartCall.push([arguments.callee, this, arguments]);
        }
    }

    /**
     * draws a column chart. this is used when data are qualitative i.e. string. currently using google charts
     * retain inputs for redrawing
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.columnchart = function (id, data, opt, habitat) {
        if (config.googleChartsLoaded) {
            // sometimes google chart get loaded after finishing data request. this ensures loading is stopped
            // when chart is rendered
            habitat && habitat.stopLoading();
            var chart = new google.visualization.ColumnChart(document.getElementById(id));
            var tdata = google.visualization.arrayToDataTable(data);
            chart.draw(tdata, opt)
            if (habitat !== undefined && habitat != null) {;
                habitat.chartid = id;
                habitat.chartdata = data;
                habitat.chartopt = opt;
            }
        } else {
            // some times google chart is not ready when this function is called
            habitat.startLoading();
            config.delayedChartCall.push([arguments.callee, this, arguments]);
        }

    }

    this.googleChartsLoaded = function () {
        var delay;
        // console.log('google chart loaded');
        config.googleChartsLoaded = true;
        // console.log(config.delayedChartCall.length)
        while (delay = config.delayedChartCall.shift()) {
            delay[0].apply(delay[1], delay[2]);
        }
    }

    /**
     * returns data that is used to save the current state of this tab
     */
    this.currentState = function(){
        var data = ko.toJSON(view);
        var sync = $.extend({}, config.syncData);
        sync.json = data;
        return sync;
    };

    /**
     * getter function accessing view model
     */
    this.setHabitats = function(data){
        data && view.initialize(data);
    };
    /**
     * get layer metadata for the requested id
     * @param layerid
     * @returns {*}
     */
    this.getLayerMetadata = function(layerid){
        if(config.list.length){
            for(var i in config.list ){
                if( config.list[i].value === layerid){
                    return config.list[i];
                }
            }
        }
    }

    this.updateMetadataForInitializedHabitats = function(){
        if(updateMetadata.length){
            var habitat,
                metadata;
            for(var i in updateMetadata){
                habitat = updateMetadata[i]
                view.selectedHabitat(habitat);
                metadata = hab.getLayerMetadata(habitat.name());
                metadata && view.changeHabitat(metadata);
                view.selectedHabitat(null);
            }
        }
    }

    function initPopover(){
        var pops = config.popOver, i,id;
        if($.cookie('habitatc') == "ok")    {
            return
        }
        for(i=0;i<pops.length;i++){
            id = pops[i].id
            if(typeof id == "function"){
                pops[i].id = id = id();
            }
            $(id).popover(pops[i].options);
            $(id).popover('show');
        }
        $('body').on('click','.popover #habitatPopOverClose', function(){
            // console.log('clicked button')
            that.popOver(false);
            $.cookie('habitatc',"ok")
        })
    }

    this.popOver = function(toggle){
        var pops = config.popOver, i,id;
        for(i=0;i<pops.length;i++){
            id = pops[i].id
            if(typeof id == "function"){
                id = id();
            }
            if(toggle){
                $(pops[i].id).popover('show');
            } else {
                $(pops[i].id).popover('hide');
            }
        }
    }
//    $('#'+options.id).on('show',initPopover);
//    $("body").on("shown.bs.tab", "#"+config.tabId, function() {
//        initPopover();
//    });

    this.on('layermetadataadded', this.updateMetadataForInitializedHabitats)


    /**
     * initialize the selected charts.
     */
    config.initialState && view.initialize(config.initialState);

    //set style
    $('#' + id).css('max-height', config.height - config.headerHeight);
    $('#' + id).css('overflow-y', 'auto');
};