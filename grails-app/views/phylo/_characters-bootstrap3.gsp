<div id="charactermain">
    <div class="btn btn-xs btn-primary top-buffer offset1" data-bind="click: addCharacter"><i
            class="icon icon-plus-sign"></i> Add Character to Tree</div>

    <div class="container" data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="item top-buffer" title="You can drag or edit this item">
            <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">
                <span class="glyphicon glyphicon-sort" aria-hidden="true" style="cursor: move"></span>
                <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>
                <span class="glyphicon glyphicon-remove" data-bind="click: $root.removeCharacter"
                      style="cursor: pointer"></span>
            </div>

            <div data-bind="visibleAndSelect: $root.isCharacterSelected($data)">
                <input data-bind="value: name, event: { blur: $root.clearCharacter }"/>
            </div>
        </div>
    </div>

    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default">
            <div class="panel-heading" data-bind="text: name"></div>

            <div class="panel-body">
                <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>
            </div>
        </div>
    </div>
</div>

<div class="bs-callout bs-callout-info">
    <h4>Note</h4>

    <p>You can select characters using <i>Add Character</i>
        button. Tree branch color is determined by the first character on the list.
        To color the tree using a character either drag that character to the top of the list, or
        edit the first character by clicking on that character.</p>
</div>