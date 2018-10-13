<div>
<div class="panel-group" id="uploadRecords" style="margin-top:20px;">
    <div class="panel panel-default">
        <div class="panel-heading" style="cursor:pointer" id="uploadCharactersTitle" >
            <i class="glyphicon glyphicon-chevron-down"></i> <a>Upload your occurrence records</a>
        </div>
        <div id="characterUploadPanel" class="panel-collapse">

            <div id="csvFormRecordsUnavailable" class="alert-error hide">
                <i>Login to enable occurrence upload or,
            you are unauthorized to upload for this visualisation.</i></div>

            <!-- upload file form -->
            <form id="csvFormRecords" class="form-horizontal" enctype="multipart/form-data">

                <label class="btn btn-default btn-file">
                    <i class="glyphicon glyphicon-upload"> </i>
                    Upload a CSV file of occurrences
                    <input id="csvFileRecords" type="file" name="file" value="Upload" accept=".csv" style="display: none;"
                           data-bind="event:{change: onNewFile}, attr:{disabled: formDisabled}"
                           required
                    />
                </label>

                <div id="csvFormInfo"  style="margin-top:15px;" data-bind="visible: headers().length > 0" >

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label">Title*:</label>
                        <div class="col-sm-9">
                            <input name="title"
                                   type="text"
                                   id="titleRecords"
                                   class="form-control"
                                   data-bind="value: title, attr:{disabled: formDisabled}"
                                   placeholder="My occurrence records" required
                                   onfocus="utils.clearPlaceholder(this)">
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label" >Column with species name or OTU number*:</label>
                        <div class="col-sm-9">
                            <select name="scientificName" data-bind="options:headers,value:selectedValue,optionsCaption:'Choose..', attr:{disabled: formDisabled}" required></select>
                        </div>
                    </div>


                    <button id="uploadBtnRecords" class="btn btn-primary" data-bind="click: uploadFile, attr:{disabled: formDisabled}">
                        <i class="glyphicon glyphicon-upload"></i> Upload my file
                    </button>
                    <button id="resetBtnRecords" class="btn btn-default" data-bind="click: resetForm, attr:{disabled: formDisabled}">Clear form</button>
                </div>
            </form>

            <div id="sandboxProgress" style="margin-top: 10px;" class="well" data-bind="visible: progress() != undefined || indexingProgress() != undefined">
                <div id="occurrenceUpload" data-bind="visible: progress() != undefined">
                    <label>Uploading Data</label>
                    <div class="progress">
                        <div class="progress-bar"
                             role="progressbar"
                             data-bind="style:{width: progress() + '%'}"
                             aria-valuemin="0"
                             aria-valuemax="100">
                        </div>
                    </div>
                    <div class="alert" data-bind="html: message, css:{ 'alert-error': error(), 'alert-success': !error()}, visible: !!message()">
                    </div>
                </div>

                <div id="sandboxUpload" data-bind="visible: indexingProgress() != undefined">
                    <label>Indexing Data</label>
                    <div class="progress-bar"
                         role="progressbar"
                         data-bind="style:{width: indexingProgress() + '%'}"
                         aria-valuemin="0"
                         aria-valuemax="100">
                    </div>
                    <div id="uploadMessage" class="alert" data-bind="html: indexingMessage, attr:{class: 'alert ' + indexingClass()}">
                    </div>
                </div>
            </div>

            <div class="pull-right small">
                <a id="csvSampleRecords" target="_blank" data-bind="attr:{href: sampleFile}">Download sample CSV file from here.</a>
            </div>
        </div>
    </div>
</div>

<div id="recordsmain" class="panel-group"  style="margin-top:20px;">
    <div class="panel panel-default">
        <div class="panel-heading" id="pickFromListRecordsHeader">
            Select records dataset from the available list
        </div>
        <p>
            Select the source of occurrence data from the options below.
            <br/>
            This will affect the records displayed on the map, and the records used
            to populate histograms in the Analysis section.
        </p>

        <div id="pickFromListRecords" class="panel-collapse">
            <form id="sourceToolbarRecords" >

                <div class="form-group row">
                    <label for="sourceCharRecords" class="col-sm-3 col-form-label">Occurrence dataset:</label>
                    <div class="col-sm-9">
                        <select id="sourceCharRecords"
                                class="form-control"
                                data-bind="options:lists,optionsText:'title',value:selectedValue,
                    optionsCaption:'Choose..', event:{change: drChanged}" required></select>
                    </div>
                </div>
            </form>
        </div>
        <div >&nbsp;</div>
    </div>
</div>
</div>