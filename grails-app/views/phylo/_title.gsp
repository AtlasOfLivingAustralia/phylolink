<div class='pull-left' data-bind=' click: $root.select' style='cursor:pointer;'>
    <h3 style='color:#C44D34;margin-bottom: 10px;
    padding-bottom: 0px;
    margin-top: 5px;display: inline-block' title='Click title to edit it' data-bind='text: title'></h3>
    &nbsp;<i data-bind='visible: edit' title='Click title to edit it' class='icon icon-pencil'></i>
</div>





<div data-bind='visible: $root.clicked()' >
    New title for this visualisation: <input data-bind='value: title, event:{blur:$root.clearClick, change:$root.sync}'>
</div>


