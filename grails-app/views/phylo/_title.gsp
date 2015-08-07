<div class='pull-left' data-bind=' click: $root.select' style='cursor:pointer;'><h1 style='color:#C44D34;display: inline-block'
                                                                  title='Click title to edit it'
                                                                  data-bind='text: title'></h1>&nbsp;<i
        data-bind='visible: edit' title='Click title to edit it' class='icon icon-pencil'></i></div>

<div data-bind='visible: $root.clicked()' style='padding-bottom: 10px'>
    New title for this visualisation: <input data-bind='value: title, event:{blur:$root.clearClick, change:$root.sync}'>
</div>

<div class='pull-right alert selection-info text-right' role='alert' id='selectionInfo'>
    <table><tr><td data-bind='text: selectedDr'></td></tr>
        <tr><td data-bind='text: selectedClade'></td></tr>
        <tr><td data-bind='visible: selectedCladeNumber() >= ${grailsApplication.config.biocache.maxBooleanClauses}'
                class='alert-error'>
            limited to the first ${grailsApplication.config.biocache.maxBooleanClauses} taxa</td></tr>
    </table>
</div>