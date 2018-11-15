<div id="compare-variables" style="margin-top:15px;">
    <h4 style="display:none;" data-bind="text: chartTitle"></h4>
    <div>
        <div class="row">
            <div class="col-md-4">
                <div class="form-group">
                    <label for="var1">Y axis:</label>
                    <select id="var1" name="var1" class="form-control input-sm" data-bind="options: variable1Options, optionsValue: 'id', optionsText: 'name',
                       value: selectedVariable1">
                    </select>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group">
                    <label for="var2">X axis:</label>
                    <select id="var2" name="var2" class="form-control input-sm" data-bind="options: variable1Options,  optionsValue: 'id', optionsText: 'name', value: selectedVariable2">
                    </select>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group" style="margin-left:20px;">
                    <label for="breakdown-type">Breakdown: </label>
                    <select id="breakdown-type" name="breakdown-type" class="form-control input-sm">
                        <option value="occurrences">Occurrences</option>
                        <option value="species">Taxa</option>
                        <option value="speciesGrouped">Grouped taxa</option>
                    </select>
                </div>
            </div>
        </div>
        <div>
            <button data-bind="click: updateChart" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-refresh"> </i>
                Redraw
            </button>
        </div>
    </div>

    <div class="row" style="margin-top: 15px;">
        <div id="scatterplot-1"  class="col-md-9"> </div>
    </div>
</div>
