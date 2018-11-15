var CompareVariables = function (options) {
    // emitter mixin. adding functions that support events.
    new Emitter(this);
    var $ = jQuery;
    var pj = options.pj;
    var chartDataUrl = options.chartDataUrl;
    var graphibleFieldsUrl = options.graphibleFieldsUrl;
    var chartWidth = options.chartWidth;
    var records = options.records;

    var defaultVariable1 = options.variable1;
    var defaultVariable2 = options.variable2;

    var CompareVariablesViewModel = function () {
        new Emitter(this);
        var self = this;
        var spinner = new Spinner({
            top: '50%',
            left: '50%',
            className: 'loader'
        });

        self.chartTitle = ko.observable("");
        self.selectedVariable1 = ko.observable("");
        self.selectedVariable2 = ko.observable("");
        self.variable1Options = ko.observableArray();
        self.variable2Options = ko.observableArray();

        self.init = function(){

            if(records.getDataresource() === undefined){
                return;
            }

            $.ajax({
                url: graphibleFieldsUrl,
                data: {
                    biocacheServiceUrl: records.getDataresource().biocacheServiceUrl,
                    q: "qid:" + pj.getQid(true)
                },
                success: function (data) {
                    self.variable1Options(data);
                    self.variable2Options(data);

                    self.selectedVariable1(defaultVariable1);
                    self.selectedVariable2(defaultVariable2);
                }
            });
        };

        self.updateChart = function(){

            if(pj.getQid(true) === undefined){
                return;
            }

            if(self.selectedVariable1()  == "" || self.selectedVariable2()  == ""){
                return;
            }

            self.startLoading();

            var chartData = {
                query: "qid:" + pj.getQid(true),
                breakdown: $('#breakdown-type').val(),
                variable1: self.selectedVariable1(),
                variable2: self.selectedVariable2(),
                biocacheServiceUrl: records.getDataresource().biocacheServiceUrl
            };

            $.ajax({
                url: chartDataUrl,
                data: chartData,
                success: function (data) {
                    var chartData = google.visualization.arrayToDataTable(data);

                    var units = "taxa"
                    if (chartData.breakdown == "occurrences"){
                        units = "occurrences";
                    }

                    var options = {
                        height: 650,
                        width: chartWidth,
                        isStacked: true,
                        bar: {groupWidth: "60%"},
                        chartArea: {top:0, left: 200, width: 300},
                        legend: {textStyle: {fontSize: 10}},
                        hAxis: {
                            textStyle: {
                                fontSize: 10 // or the number you want
                            },
                            format:'# ' + units
                        },
                        vAxis: {
                            textStyle: {
                                fontSize: 10 // or the number you want
                            },
                            format:'# ' + units
                        }
                    };
                    var chart = new google.visualization.BarChart(document.getElementById("scatterplot-1"));
                    chart.draw(chartData, options);

                    self.stopLoading();
                },
                error: function (xhr, status, error){
                    self.stopLoading();
                }
            });
        };

        self.startLoading = function () {
            if (!spinner || !spinner.el) {
                spinner = new Spinner({
                    top: '50%',
                    left: '50%',
                    className: 'loader'
                });
            }
            spinner.spin();
            $('#scatterplot-1').append(spinner.el);
        };

        self.stopLoading = function () {
            spinner.stop();
        };
    };

    var compareVariablesViewModel = new CompareVariablesViewModel();
    ko.applyBindings(compareVariablesViewModel, document.getElementById('compare-variables'));
    compareVariablesViewModel.init();

    //savequeryend
    pj.on('savequeryend', function () {
        compareVariablesViewModel.updateChart();
        compareVariablesViewModel.init();
    });

    pj.on('updateend', function () {
        compareVariablesViewModel.updateChart();
        compareVariablesViewModel.init();
    });

    pj.on('change', function () {
        compareVariablesViewModel.updateChart();
        compareVariablesViewModel.init();
    });

    records.on('sourcechanged', function () {
        compareVariablesViewModel.init();
    });


    this.googleChartsLoaded = function(){
        compareVariablesViewModel.updateChart();
    };
};