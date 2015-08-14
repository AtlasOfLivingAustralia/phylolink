<div >
    <div class="bs-callout" id="uploadCharacters" style="position: relative">
        <h4 style="cursor:pointer" id="uploadCharactersTitle" data-bind="click: onClick"><i class="icon icon-chevron-down"></i> <a>Upload your character data</a></h4>
        <div id="minimizeUpload" style="display:none">
            <div id="csvFormUnavailable" class="alert-error"><i>Login to enable character upload.</i></div>
            <form id="csvForm" class="form-horizontal" enctype="multipart/form-data">
                <i>You need modern browser such as Google Chrome 40 or Safari 8</i>
                <div class="control-group">
                    <label class="control-label">Choose a CSV file*:</label>
                    <div class="controls">
                        <input id="csvFile" type="file" name="file" value="Upload" accept=".csv" required/>
                        <label><a data-bind="attr{href:sampleCSV}" target="_blank">Download sample CSV file from here.</a></label    >
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="inputPassword">Title*:</label>
                    <div class="controls">
                        <input type="text" id="title" data-bind="value: title" placeholder="My acacia characters" required>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="inputPassword">Column with scientific name*:</label>
                    <div class="controls">
                        <select data-bind="options:headers,optionsText:'displayname',value:selectedValue,optionsCaption:'Choose..'" required></select>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="uploadBtn" class="btn btn-small btn-primary">Upload</button>
                    </div>
                </div>
            </form>
            <div class="alert" id="uploadMessage" data-bind="visible:message">
            
                <div data-bind="text:message"></div></div></div>
    </div>
</div>
<div id="charactermain">
    <div class="bs-callout" style="position: relative" id="pickFromList">
        <h4><a>Or, pick a character dataset from the available list:</a></h4>
        <form id="sourceToolbar" class="form-horizontal">
            <div class="control-group">
                <label class="control-label" for="">List of characters available:</label>
                <div class="controls">
                    <select id="sourceChar" data-bind="options:lists,optionsText:'title',value:list,optionsCaption:'Choose..', event:{change:loadNewCharacters}" required></select>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <a data-bind="attr:{href: list() ? list().listurl : null}, visible: list()" target="_blank"><i class="icon icon-globe"></i> View in List Tool</a>
                </div>
            </div>
        </form>
    </div>
    <div class="btn btn-xs btn-primary top-buffer offset4" data-bind="click: addCharacter, visible:list(), attr:{disabled:listLoading()}"><i class="icon-white icon-plus-sign"></i> Add Character to Tree</div>
    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="item top-buffer" title="You can drag or edit this item">
            <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">
                <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>
                <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>
                <i class="icon-white icon-remove" data-bind="click: $root.removeCharacter" 
                   style="cursor: pointer"></i>
            </div>
            <div data-bind="visibleAndSelect: $root.isCharacterSelected($data)">
                <input data-bind="value: name, event: { blur: $root.clearCharacter }" />
            </div>
        </div>
    </div>
    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default">
            <div class="panel-heading" data-bind="text: name"></div>
            <div class="panel-body" >
                <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>
            </div>
        </div>
    </div>
</div>
<div class="alert top-buffer">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <h4>Note</h4><p>First, select a character dataset from the given list, or upload your character data. Then click on <i>Add Character to Tree</i>
button. Tree branch color is determined by the first character on the list.
To color the tree using a character either drag that character to the top of the list, or 
edit the first character by clicking on that character.</p>
</div>