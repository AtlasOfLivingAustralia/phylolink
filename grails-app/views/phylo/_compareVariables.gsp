<div id="compare-variables" style="margin-top:15px;">
    <h4 style="display:none;" data-bind="text: chartTitle"></h4>
    <div>
        <div class="form-inline">
            <div class="form-group">
                <label for="var1">Y: </label>
                %{--<select id="var1" name="var1" class="form-control" data-bind="options: variable1Options, optionsText: 'name',--}%
                       %{--value: 'id'">--}%
                <select id="var1" name="var1" class="form-control input-sm">
                    <option value="state">State/Territory</option>
                    <option value="cl678">Land Use</option>
                    <option value="cl620">Vegetation</option>
                    <option value="cl2125">NVIS 4.1 Major Vegetation Groups</option>
                </select>
            </div>
            <div class="form-group" style="margin-left:10px;">
                <label for="var2">X: </label>
                %{--<select id="var2" name="var2" class="form-control" data-bind="options: variable1Options, optionsText: 'name',--}%
                       %{--value: 'id'">--}%
                <select id="var2" name="var2" class="form-control input-sm">
                    <option value="cl620">Vegetation</option>
                    <option value="cl678">Land Use</option>
                    <option value="cl2125">NVIS 4.1 Major Vegetation Groups</option>
                    <option value="cl1049">IBRA 7 Subregions</option>
                </select>
            </div>
            <div class="form-group" style="margin-left:20px;">
                <label for="breakdown-type">Breakdown: </label>
                <select id="breakdown-type" name="breakdown-type" class="form-control input-sm">
                    <option value="occurrences">Occurrences counts</option>
                    <option value="species">Taxa counts</option>
                    <option value="speciesGrouped">Grouped taxa counts</option>
                </select>
            </div>
            <button data-bind="click: updateChart" class="btn btn-default"  style="margin-left:10px;">
                Redraw
            </button>
        </div>
    </div>

    <div class="row" style="margin-top: 15px;">
        <div id="scatterplot-1"  class="col-md-9"> </div>
    </div>
</div>
