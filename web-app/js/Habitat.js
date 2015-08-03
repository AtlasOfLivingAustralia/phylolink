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
        }],
        template: '<style>' +
            '.ui-autocomplete {\
            max-height: 200px;\
            overflow-y: auto;   /* prevent horizontal scrollbar */\
            overflow-x: hidden; /* add padding to account for vertical scrollbar */\
            z-index:1000 !important;\
            }\
            .ellipselabel {\
              white-space: nowrap;\
              max-width: 220px;\
              overflow: hidden;              /* "overflow" value must be different from "visible" */\
              text-overflow: ellipsis;\
            }' +
            '</style>' +
            '<div id="habitatMain">' +
            '<div class="btn btn-primary" data-bind="click:addHabitat"><i class="icon-white icon-plus-sign"></i> Plot profile</div>' +
            '<div data-bind="sortable:{data:habitats, afterMove: $root.onMove}">' +
            '<div class="item top-buffer">' +
            '<div class="label label-default" data-bind="visible: !$root.isHabitatSelected($data)">\
                <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>\
                <div class="ellipselabel" style="color: #ffffff;display:inline-block;cursor: pointer\
                " href="#" data-bind="text: displayName, click: $root.selectedHabitat"></div>\
                <i class="icon-white icon-remove" data-bind="click: $root.removeHabitat" \
                       style="cursor: pointer"></i>\
            </div>' +
            '<div data-bind="select: $root.isHabitatSelected($data)">' +
            '<input data-bind="value: displayName, event:{blur: $root.clearHabitat}"/>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="text-right">\
                <a id="downloadPlotDataLink" class="btn btn-link" data-toggle="modal" href="#plotOccurrenceDownloadModal" data-bind="visible: habitats().length > 0"><i class="fa fa-download"></i>&nbsp;Download source data</a>\
            </div>'+
            '<div data-bind="sortable: {data:habitats, afterMove: $root.onMove}">\
                <div class="top-buffer panel panel-default" style="position: relative">\
                    <div class="panel-heading">\
                        <div data-bind="text: displayName" class="pull-left"></div>\
                        <div class="pull-right">\
                            <div data-bind="text: name" style="display:none"></div>\
                            <i class="icon-info-sign" title="Show more information" onclick="showInfo(this)" data-bind="attr: { id: id_metadata }"></i>\
                        </div><div>&nbsp;</div>\
                    </div>\
                    <div style="display:none"  data-bind="attr: { name: id_metadata }">\
                        <table class="table table-bordered" ><tbody><tr><th colspan="2">Layer metadata</th></tr>\
                        <tr><td>Name:</td><td><div data-bind="text: displayName" ></div></td></tr>\
                        <tr ><td>Description:</td><td><div data-bind="text: mdDescription" ></div></td></tr>\
                        <tr data-bind="visible: mdNotes().length > 0"><td>Notes:</td><td><div data-bind="text: mdNotes" ></div></td></tr>\
                        <tr data-bind="visible: mdMin().length > 0"><td>Min:</td><td><div data-bind="text: mdMin" ></div></td></tr>\
                        <tr data-bind="visible: mdMax().length > 0" ><td>Max:</td><td><div data-bind="text: mdMax" ></div></td></tr>\
                        <tr data-bind="visible: mdUnits().length > 0"><td>Units:</td><td><div data-bind="text: mdUnits" ></div></td></tr>\
                        <tr><td>More info:</td><td><a target="_blank" data-bind="attr: { href: mdUrl, title: mdUrl }">more information</a></td></tr>\
                        </tbody></table>\
                    </div>\
                    <div class="panel-body" >\
                        <div data-bind="attr:{id: id}, addHabitatChart: !$root.isHabitatSelected($data)"></div>\
                        <div class="text-right">\
                            <button type="button" class="btn btn-link small" data-bind="click: $root.downloadSummaryCsv, visible: !loading()"><i class="fa fa-download"></i>&nbsp;Download summary data</button>\
                        </div>\
                    </div>\
                </div>\
            </div>' +
            '</div>'+
            '<div class="alert top-buffer">\
                <button type="button" class="close" data-dismiss="alert">&times;</button>\
                <h4>Note</h4><p>You can click on <i>Plot profile</i> button to find out the environmental \
                characteristics like precipitation, temperature etc. of a clade. You can pick the environmental parameter \
                from the drop down list, or filter the list by typing into the input box.</p>\
            </div>'+
            '<script> function showInfo(i){ var t = $("div[name=\'" + i.id + "\'")[0]; ' +
                'if (t.style.display === "none") t.style.display = "block"; else t.style.display = "none"; } </script>'
    }, c);
    var pj = config.pj, hab = this, id = config.id;
    $('#' + config.id + 'Inner').html(config.template);
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
        this.loading = ko.observable(false);

        this.email = ko.observable();
        this.reason = ko.observable();

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
            self.selectedHabitat().mdDescription(data.description);
            self.selectedHabitat().mdNotes(data.notes);
            self.selectedHabitat().mdMin(data.environmentalvaluemin);
            self.selectedHabitat().mdMax(data.environmentalvaluemax);
            self.selectedHabitat().mdUnits(data.environmentalvalueunits);
            self.selectedHabitat().mdUrl(data.url);
            self.emit('changed', self.selectedHabitat());
        };

        self.updateChart = function(habitat, list){
            var data = {
                speciesList: JSON.stringify(list),
                config: habitat.name()
            };
            if(config.doSaveQuery){
                self.saveQuery(data).then(function(qid){
                    var data = {q:"qid:"+qid};
                    self.updateChartDirect(habitat, data);
                });
            } else {
                self.updateChartDirect(habitat, data);
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
                    config: habitat.name()
                };
                self.updateChartDirect(habitat, data);
            } else {
                list = pj.getChildrenName(pj.getSelection());
                data = {
                    speciesList: JSON.stringify(list),
                    config: habitat.name()
                };
                self.updateChartDirect(habitat, data);
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

            var url = config.biocacheOccurrenceDownload
                + "?q=qid:" + qid
                + "&reasonTypeId=" + self.downloadViewModel.reason().id()
                + "&email=" + email
                + "&extra=" + fields.join(",");

            $("<a style='display: none' href='" + url + "' download='data.zip'>download data</a>").appendTo('body')[0].click();
            $(".closeDownloadModal").filter(":visible").click();
        };
    };

    ko.bindingHandlers.select = {
        init: function (element, valueAccessor, innerFn, data, koObj) {
            console.log('init visible and select handler');
            var input = $(element).find("input");
            input.autocomplete({
                source: config.list,
                minLength: 0,
                minChars: 0,
                select: function (event, ui) {
                    event.preventDefault();
                    $(this).val(ui.item.label);
                    var self = koObj.$root;
                    self.changeHabitat(ui.item, data)
                    input.blur();
                },
                _renderItem: function (ul, item) {
                    return $("<li>")
                        .attr("data-value", item.value)
                        .append(item.label)
                        .appendTo(ul);
                }
            }).on('focus', function (event, ui) {
                var self = this;
                ui && $(self).val(ui.item.label);
                $(self).autocomplete("search", "");
                koObj.$root.newChar = true;
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
        },
        update: function (element, valueAccessor, innerFn, data, koObj) {
            console.log('update function');
            ko.bindingHandlers.visible.update(element, valueAccessor);
            if (valueAccessor()) {
                // focus on input tag once clicked to edit
                $(element).find("input").focus().select();
            } else {
                $(element).find("input").blur();
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

    this.refresh = function (node, list, saveQuery) {
        var habitats = view.habitats();
        var i, data;

        if(saveQuery){
            saveQuery.then(function(qid){
                var data = {q:pj.getQid(true)}
                console.log(data)
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
                    config: habitats[i].name()
                };

                view.updateChartDirect(habitats[i], data);
            }
        }

    };

    this.save = function () {
        if (!config.doSync) {
            return;
        }

        var data = ko.toJSON(view);
        console.log(data);
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