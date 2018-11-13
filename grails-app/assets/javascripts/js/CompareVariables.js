var CompareVariables = function (options) {
    // emitter mixin. adding functions that support events.
    new Emitter(this);
    var $ = jQuery;
    var pj = options.pj;
    var chartDataUrl = options.chartDataUrl;
    var graphibleFieldsUrl = options.graphibleFieldsUrl;
    var chartWidth = options.chartWidth;
    var records = options.records;

    var CompareVariablesViewModel = function () {
        new Emitter(this);
        var self = this;
        var spinner = new Spinner({
            top: '50%',
            left: '50%',
            className: 'loader'
        });

        self.chartTitle = ko.observable("Land use by State/Territory");
        self.selectedVariable1 = ko.observable(options.variable1);
        self.selectedVariable2 = ko.observable(options.variable2);
        self.variable1Options = ko.observableArray();
        self.variable2Options = ko.observableArray();

        self.init = function(){
            // $.ajax({
            //     url: graphibleFieldsUrl,
            //     data: {
            //         biocacheServiceUrl: records.getDataresource().biocacheServiceUrl
            //     },
            //     success: function (data) {
            //         console.log("Graphible fields: " + data);
            //         self.variable1Options(data);
            //         self.variable2Options(data);
            //     }
            // });
        };

        self.updateChart = function(){

            console.log("Updating comparison charts: " + pj.getQid(true));

            if(pj.getQid(true) === undefined){
                return;
            }

            self.startLoading();

            var chartData = {
                query: "qid:" + pj.getQid(true),
                breakdown: $('#breakdown-type').val(),
                variable1: $('#var1').val(),
                variable2: $('#var2').val(),
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
        console.log(" compareVariables - savequeryend event");
        compareVariablesViewModel.updateChart();
    });

    pj.on('updateend', function () {
        console.log(" compareVariables - updateend event");
        compareVariablesViewModel.updateChart();
    })

    pj.on('change', function () {
        console.log(" compareVariables - updateend event");
        compareVariablesViewModel.updateChart();
    })

    this.googleChartsLoaded = function(){
        compareVariablesViewModel.updateChart();
    };
};