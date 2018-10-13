<div>
    <div class="panel-group" id="uploadCharacters" style="margin-top:20px;">

        <div class="panel panel-default">
            <div class="panel-heading" id="uploadCharactersTitle" >
                <a data-toggle="collapse" href="#characterUploadPanel">
                    <i class="glyphicon glyphicon-chevron-right"></i>
                    Upload your character data
                </a>
             </div>
            <div id="characterUploadPanel" class="panel-collapse collapse">
                <div class="panel-body" style="border:none;">
                    <div id="minimizeUploadXX">
                        <div id="csvFormUnavailable" class="alert-error"><i>Is this your visualisation? If yes, login to upload your characters.</i></div>

                        <p>
                            Upload your own character data. Data should be in CSV format with the first column
                            being a scientific name. You can supply any number of additional columns with each column being a trait/character.
                            <br/>
                            You can download an example CSV file
                            <a data-bind="attr{href:sampleCSV}" target="_blank">here</a>.
                            Once you've upload, you'll be able to select your character and add character to tree.
                        </p>

                        <!-- upload file form -->
                        <form id="csvForm" class="form-horizontal" enctype="multipart/form-data">
                            <label class="btn btn-default btn-file">
                                <i class="glyphicon glyphicon-upload"> </i> Upload a CSV file of characters
                                <input id="csvFile" type="file" name="file" value="Upload" accept=".csv" style="display: none;" required/>
                            </label>
                        </form>


                        <div data-bind="visible: headers().length > 0" style="margin-top:15px;">
                            <form>
                                <div class="form-group row">
                                    <label for="characters-title" class="col-sm-3 col-form-label">Title*:</label>
                                    <div class="col-sm-9">
                                        <input type="text" class="form-control" id="characters-title" data-bind="value: charactersTitle" placeholder="My acacia characters" required>
                                    </div>
                                </div>
                                <div class="form-group row">
                                    <label for="sciNameColumn" class="col-sm-3 col-form-label">Column with scientific name*:</label>
                                    <div class="col-sm-9">
                                        <select id="sciNameColumn" class="form-control"  data-bind="options:headers,optionsText:'displayname',value:selectedValue" required></select>
                                    </div>
                                </div>

                                <button id="uploadBtn" class="btn btn-small btn-primary">
                                    <i class="glyphicon glyphicon-floppy-save"> </i>
                                    Save character list
                                </button>
                                <button id="clearBtn" class="btn btn-small btn-default" data-bind="click: cancelCharUpload">
                                    Cancel
                                </button>

                            </form>

                            <div class="alert alert-danger" id="uploadMessage" data-bind="visible:message">
                                <div data-bind="text:message"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="charactermain">
    <div class="panel panel-default">
        <div class="panel-heading" id="pickFromList">
            <a data-toggle="collapse" href="#addCharacterPanel">
                <i class="glyphicon glyphicon-chevron-right"></i>
                Add Character to Tree
            </a>
        </div>
        <div id="addCharacterPanel" class="panel-body panel-collapse" style="border:none;">

            <p>First, select a character dataset from the given list, or upload your character data. Then click on <i>Add Character to Tree</i>
            button. Tree branch color is determined by the first character on the list.
            To color the tree using a character either drag that character to the top of the list, or
            edit the first character by clicking on that character.</p>


            <form id="sourceToolbar" >

                <div class="form-group row">
                    <label  for="sourceChar" class="col-sm-3 col-form-label">Character datasets:</label>
                    <div class="col-sm-9">
                        <select id="sourceChar"
                                class="form-control"
                                data-bind="options:lists,optionsText:'title',value:list,optionsCaption:'Choose..', event:{change:loadNewCharacters}" required>
                        </select>
                    </div>
                </div>

                <div class="form-group row">
                    <label class="col-sm-3 col-form-label" for="selectChar">Select a character:</label>
                    <div class="col-sm-9">
                        <select id="selectChar" class="form-control" data-bind="options: activeCharacterList">
                        </select>
                    </div>
                </div>

                <div class="btn btn-primary top-buffer offset4"
                     data-bind="click: addCharacter, enable:list(), attr:{disabled:listLoading()}">
                    <i class="glyphicon glyphicon-white glyphicon-plus-sign"></i>
                    Add Character to Tree
                </div>

                <div class="control-group hide">
                    <div class="controls">
                        <a data-bind="attr:{href: list() ? list().listurl : null}, visible: list()" target="_blank"><i class="icon icon-globe"></i> View in List Tool</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <h3 class="hide">Selected characters</h3>
    <div class="hide" data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="item top-buffer" title="You can drag or edit this item">
            <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">
                <i class="glyphicon  glyphicon glyphicon-resize-vertical" aria-hidden="true" style="cursor: move"></i>
                <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>
                <i class="glyphicon glyphicon-white glyphicon glyphicon-remove" data-bind="click: $root.removeCharacter"
                   style="cursor: pointer"></i>
            </div>
        </div>
    </div>

    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">
        <div class="top-buffer panel panel-default">
            <div class="panel-heading">
                <div class="pull-right">
                    <i class="glyphicon  glyphicon glyphicon-remove" title="Remove" data-bind="click: $root.removeCharacter"
                       style="cursor: pointer"> </i>
                </div>
                <div data-bind="text: name"></div>
            </div>
            <div class="panel-body" >
                <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>
            </div>
        </div>
    </div>
</div>
