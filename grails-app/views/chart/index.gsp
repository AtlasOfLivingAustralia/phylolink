<%--
 Created by Temi Varghese on 23/10/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Chart test</title>

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
        google.load("visualization", "1", {packages: ["corechart"]});
    </script>
</head>
<body>
<div class="container">
    <h1>Chart Test</h1>

    <div class="row">
        <div id="scatterplot-1"  class="col-md-9"> </div>
        <div class="col-md-3">
            <div class="form-group">
                <label for="var1">Variable 1</label>
                <select id="var1" name="var1" class="form-control">
                    <option>state</option>
                    <option>cl678</option>
                </select>
            </div>
            <div class="form-group">
                <label for="var2">Variable 2</label>
                <select id="var2" name="var2" class="form-control">
                    <option value="cl620">Vegetation</option>
                    <option value="cl2125">NVIS 4.1 Major Vegetation Groups</option>
                    <option>cl1049</option>
                    <option>cl678</option>

                    <option>state</option>
                </select>
            </div>
            <div class="form-group">
                <label for="breakdown-type">Chart breakdown type</label>
                <select id="breakdown-type" name="breakdown-type" class="form-control">
                    <option value="occurrences">Occurrences counts</option>
                    <option value="species">Taxa counts</option>
                    <option value="speciesGrouped">Grouped taxa counts</option>
                </select>
            </div>
            <button onclick="redraw();" class="btn btn-default">
                Redraw
            </button>
        </div>
    </div>
    <script>

        function redraw() {

            var chartData = {
                breakdown: $('#breakdown-type').val(),
                variable1: $('#var1').val(),
                variable2: $('#var2').val()
            };

            $.ajax({
                url: "${raw(createLink(controller: "chart", action: "stackedBar"))}",
                data: chartData,
                success: function (data) {
                    var chartData = google.visualization.arrayToDataTable(data);

                    var units = "taxa"
                    if (chartData.breakdown == "occurrences"){
                        units = "occurrences";
                    }

                    var options = {
                        height: 500,
                        isStacked: true,
                        bar: {groupWidth: "60%"},
                        chartArea: {top:0, left: 200, width: 400},
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
                }
            })
        }
        redraw();
    </script>
</div>

</body>
</html>