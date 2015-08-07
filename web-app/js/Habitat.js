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
        'sync'
    ]
    var config = $.extend({
        bootstrap: 2,
        list: [],
        listUrl: 'http://localhost:8080/phylolink/ala/getAllLayers',
        graph: {
            url: 'http://localhost:8080/phylolink/phylo/getHabitat',
            type: 'GET',
            dataType: 'JSONP',
//            title:'Habitat',
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
        }]
    }, c);
    var pj = config.pj, hab = this, id = config.id;

    /**
     * stores query id for a node.
     */
    var qid;

    if (config.listUrl) {
        $.ajax({
            url: config.listUrl,
            dataType: config.dataType || 'JSON',
            success: function (data) {
                while (config.list.pop()) {
                    // empty list
                }
                var i = 0;
                for (i = 0; i < data.length; i++) {
                    data[i].value = data[i].id;
                    config.list.push(data[i]);
                }
            }
        })
    }

    var Habitat = function (c) {
        this.name = ko.observable(c.name);
        this.displayName = ko.observable(c.displayName);
        this.id = ko.observable(c.id);
        this.id_metadata = ko.observable(c.id_metadata);
        this.mdDescription = ko.observable(c.description);
        this.mdNotes = ko.observable(c.notes);
        this.mdMin = ko.observable(c.environmentalvaluemin);
        this.mdMax = ko.observable(c.environmentalvaluemax);
        this.mdUnits = ko.observable(c.environmentalvalueunits);
        this.mdUrl = ko.observable(c.url);
        this.xAxis = ko.observable(c.xAxis);
        this.yAxis = ko.observable(c.yAxis);
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

        self.downloadViewModel = new utils.OccurrenceDownloadViewModel();

        /**
         *
         * @param init
         */
        self.initialize = function (init) {
            var habitats = init.habitats, temp, habitat, count = init.count;
            if( !habitats || (count === undefined)){
                return;
            }

            for (var i in habitats) {
                temp = habitats[i];

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
            }
            self.count(count);
            self.selectedHabitat(null);
        };

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
                    console.log('cont')
                    break;
                case 'Environmental':
                    xaxis = data.environmentalvalueunits || config.graph.xAxisEnvironmental;
                    console.log('env');
                    break;
            }
            console.log(xaxis);
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
            var data = {
                speciesList: JSON.stringify(list),
                config: habitat.name(),
                biocacheServiceUrl: pj.records.getDataresource().biocacheServiceUrl
            };
            if(config.doSaveQuery){
                self.saveQuery(data).then(function(qid){
                    var data = {q:"qid:"+qid, biocacheServiceUrl:records.getDataresource().biocacheServiceUrl};
                    self.updateChartDirect(habitat, data);
                });
            } else {
                self.updateChartDirect(habitat, data);
            }
            updateCladeInfo(list)
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
                        ]);
                        return;
                    }

                    hab.columnchart(id, data, view.getOptions(habitat));
                },
                error: function () {
                    habitat.stopLoading();
                }
            });
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
                    title: habitat.xAxis()
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
            var data, list;
            var qid = pj.getQid(true);
            if( qid ){
                data = {
                    q : qid,
                    config: habitat.name(),
                    biocacheServiceUrl: records.getDataresource().biocacheServiceUrl
                };
                self.updateChartDirect(habitat, data);
            } else {
                list = pj.getChildrenName(pj.getSelection());
                data = {
                    speciesList: JSON.stringify(list),
                    config: habitat.name()
                };
                self.updateChartDirect(habitat, data);
                updateCladeInfo(list)
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
            var qid = pj.getQid(true);

            $.ajax({
                url: config.downloadSummaryUrl,
                type: 'GET',
                data: {
                    q: qid,
                    config: habitat.name()
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

            var url = records.getDataresource().biocacheServiceUrl + '/occurrences/index/download'
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
            console.log('init visible and select handler');
            var input = $(element).find("input");
            var tree = $(element).parent().find('div[id="jqxTree"]');
            input.autocomplete({
                source: config.list,
                minLength: 1,
                select: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                    var self = koObj.$root;
                    self.changeHabitat(ui.item, data)
                    self.selectedHabitat(null);
                },
                _renderItem: function (ul, item) {
                    return $("<li>")
                        .attr("data-value", item.value)
                        .append(item.label)
                        .appendTo(ul);
                }
            });
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

            var source = []
            var i
            for (i = 0; i < config.list.length; i++) {
                var class1 = config.list[i].classification1
                var class2 = config.list[i].classification2
                var label = config.list[i].displayname
                config.list[i].label = label

                if (class1 === undefined || class1 === '') class1 = 'Other'
                if (class2 === undefined || class2 === '') class2 = 'Other'

                var sclass1 = undefined
                var sclass2 = undefined
                var j
                for (j = 0; j < source.length; j++) {

                    if (source[j].label === class1) {
                        sclass1 = source[j]

                        var k
                        for (k = 0; k < source[j].items.length; k++) {
                            if (source[j].items[k].label === class2) {
                                sclass2 = source[j].items[k]
                            }
                        }
                    }
                }
                var item = {label: label, value: config.list[i]}
                if (sclass1 === undefined) {
                    source.push({label: class1, items: [{label: class2, items: [item]}]})
                } else if (sclass2 === undefined) {
                    sclass1.items.push({label: class2, items: [item]})
                } else {
                    sclass2.items.push(item)
                }
            }

            source = source.sort(function (a, b) {
                return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
            })
            for (i = 0; i < source.length; i++) {
                source[i].items = source[i].items.sort(function (a, b) {
                    return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
                })
                var j
                for (j = 0; j < source[i].items.length; j++) {
                    source[i].items[j].items = source[i].items[j].items.sort(function (a, b) {
                        return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
                    })
                }
            }
            tree.jqxTree({
                source: [{label: 'Layers', items: source, expanded: true}],
                height: '300px',
                allowDrag: false,
                toggleMode: "click"
            });
            tree.on('select', function (event) {
                var args = event.args;
                if (args.element !== undefined) {
                    var item = tree.jqxTree('getItem', args.element);
                    if (item.value !== undefined && item.value != null && item.value.label !== undefined) {
                        input.treeSelect(item.value)
                    }
                }
            });
        },
        update: function (element, valueAccessor, innerFn, data, koObj) {
            console.log('update function');
            ko.bindingHandlers.visible.update(element, valueAccessor);
            if (valueAccessor()) {
                // focus on input tag once clicked to edit
                $(element).find("input[id='layerCombobox']").focus().select();
                $(element).find("input[id='layerCombobox']").autocomplete("search", "")
                
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
//                google.visualization.events.addListener(chart, 'onmouseover', that.chartHover);
            }
        }
    };
    var view = new HabitatViewModel();
    ko.applyBindings(view, document.getElementById(config.id));

    view.on('changed', function (habitat) {
        view.refreshHabitat(habitat);
    });

    updateCladeInfo = function(list) {
        if (list.length == 0) {
            pj.selectedClade('All taxa selected')
            pj.selectedCladeNumber(-1)
        } else {
            pj.selectedClade('' + list.length + ' taxa selected')
            pj.selectedCladeNumber(list.length)
        }
    }

    this.refresh = function (node, list, saveQuery) {
        var habitats = view.habitats();
        var i, data;

        if(saveQuery){
            saveQuery.then(function(qid){
                var data = {q:pj.getQid(true),
                    biocacheServiceUrl:records.getDataresource().biocacheServiceUrl}
                for (i = 0; i < habitats.length; i++) {
                    data.config = habitats[i].name();
                    view.updateChartDirect(habitats[i], data);
                }

            },function(qid){
                console.log('failed')
            });
        } else {
            for (i = 0; i < habitats.length; i++) {
                data = {
                    speciesList: JSON.stringify(list),
                    config: habitats[i].name(),
                    biocacheServiceUrl:records.getDataresource().biocacheServiceUrl
                };

                view.updateChartDirect(habitats[i], data);
            }
        }
        updateCladeInfo(list)
    };

    this.save = function () {
        if (!config.doSync) {
            return;
        }

        var data = ko.toJSON(view);
        console.log(view.count());
        var sync = $.extend({}, config.syncData);
        sync.json = data;
        $.ajax({
            url: config.syncUrl,
            type: config.syncType,
            data: sync,
            success: function (data) {
                console.log('saved!');
            },
            error: function () {
                console.log('error saving!');
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
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.columnchart = function (id, data, opt) {
        if (config.googleChartsLoaded) {
            var chart = new google.visualization.ColumnChart(document.getElementById(id));
            data = google.visualization.arrayToDataTable(data);
            chart.draw(data, opt);
        } else {
            // some times google chart is not ready when this function is called
            config.delayedChartCall.push([arguments.callee, this, arguments]);
        }

    }

    this.googleChartsLoaded = function () {
        var delay;
        console.log('google chart loaded');
        config.googleChartsLoaded = true;
        console.log(config.delayedChartCall.length)
        while (delay = config.delayedChartCall.shift()) {
            delay[0].apply(delay[1], delay[2]);
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
            console.log('clicked button')
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


    /**
     * initialize the selected charts.
     */
    config.initialState && view.initialize(config.initialState);

    //set style
    $('#' + id).css('max-height', config.height - config.headerHeight);
    $('#' + id).css('overflow-y', 'auto');
};