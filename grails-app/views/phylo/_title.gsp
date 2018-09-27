<div class='pull-left' data-bind=' click: $root.select' style='cursor:pointer;'><h2 style='color:#C44D34;display: inline-block'
                                                                  title='Click title to edit it'
                                                                  data-bind='text: title'></h2>&nbsp;<i
        data-bind='visible: edit' title='Click title to edit it' class='icon icon-pencil'></i></div>

<div data-bind='visible: $root.clicked()' style='padding-bottom: 10px'>
    New title for this visualisation: <input data-bind='value: title, event:{blur:$root.clearClick, change:$root.sync}'>
</div>


