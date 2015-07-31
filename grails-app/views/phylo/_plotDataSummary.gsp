<a data-bind="click: function(data, event) { $root.togglePanel(data, event, $index() + 'StatisticSummary', $index() + 'DataSummaryText') }" href="#" type="button" class="btn btn-link small" data-toggle="collapse">
    <span data-bind="attr: {id: $index() + 'DataSummaryText'}"  class="fa fa-angle-double-down">&nbsp;View data summary</span>
</a>
<div data-bind="attr: {id: $index() + 'StatisticSummary'}" class="hide">
    <table class="statistic-summary">
        <tbody>
        <tr>
            <th width="15%">Sample Size</th>
            <td><span data-bind="text: sampleSize"></span></td>
            <th data-bind="visible: numeric">Min.</th>
            <td data-bind="visible: numeric"><span data-bind="text: min"></span></td>
            <th data-bind="visible: numeric">Max.</th>
            <td data-bind="visible: numeric"><span data-bind="text: max"></span></td>
            <th data-bind="visible: numeric">Mean</th>
            <td data-bind="visible: numeric"><span data-bind="text: mean"></span></td>
            <th data-bind="visible: numeric">Median</th>
            <td data-bind="visible: numeric"><span data-bind="text: median"></span></td>
            <th data-bind="visible: numeric">Std. Dev.</th>
            <td data-bind="visible: numeric"><span data-bind="text: standardDeviation"></span></td>
        </tr>

        <tr data-bind="visible: leastFrequent">
            <th width="15%">Least frequent <a class="fa fa-question-circle" title="Only the first 5 items will be shown here if multiple items share the same frequency"></a></th>
            <td width="85%" colspan="11"><span class="strong">Frequency:</span> <span data-bind="text: leastFrequentCount"></span>; <span class="strong">Item(s):</span> <span data-bind="text: leastFrequent"></span></td>
        </tr>
        <tr data-bind="visible: mostFrequent">
            <th width="15%">Most frequent <a class="fa fa-question-circle" title="Only the first 5 items will be shown here if multiple items share the same frequency"></a></th>
            <td width="85%" colspan="11"><span class="strong">Frequency: </span><span data-bind="text: mostFrequentCount"></span>; <span class="strong">Item(s):</span> <span data-bind="text: mostFrequent"></span></td>
        </tr>
        </tbody>
    </table>

    <div class="text-right">
        <button type="button" class="btn btn-link" data-bind="click: $root.downloadSummaryCsv, visible: !loading()"><i class="fa fa-download"></i>&nbsp;Download plot data</button>
    </div>
</div>