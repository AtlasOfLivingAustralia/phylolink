<div class='pull-left' data-bind='click: $root.select' style='cursor:pointer;'>
    <h3 style='color:#C44D34;margin-bottom: 10px;
    padding-bottom: 0px;
    margin-top: 5px;display: inline-block' data-bind="text: title, attr:{title: (edit()?'Click title to edit it':'')}"></h3>
    &nbsp;<i data-bind='visible: edit' title='Click title to edit it' class='icon icon-pencil'></i>
</div>





<div data-bind='visible: $root.clicked()' >
    New title for this visualisation: <input data-bind='value: title, event:{blur:$root.clearClick, change:$root.sync}'>
</div>


