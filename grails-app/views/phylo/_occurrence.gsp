<div >
    <div class="bs-callout" id="uploadRecords" style="position: relative">
        <h4 style="cursor:pointer" id="uploadRecordsTitle" data-bind="click: onToggleForm">
            <i class="icon icon-chevron-down"></i> <a>Upload my occurrence records</a>
        </h4>
        <div id="minimizeUploadRecords" style="display:none">
            <form id="csvFormRecords" class="form-horizontal" enctype="multipart/form-data" >
                <i>You need modern browser such as Google Chrome 40 or Safari 8</i>
                <div class="control-group">
                    <label class="control-label">Choose a CSV file*:</label>
                    <div class="controls">
                        <input id="csvFileRecords" type="file" name="file" value="Upload" accept=".csv" required
                               data-bind="event:{change: onNewFile}, attr:{disabled: formDisabled}"/>
                        <label><a id="csvSampleRecords" target="_blank" data-bind="attr:{href: sampleFile}">Download sample CSV file from here.</a></label>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Title*:</label>
                    <div class="controls">
                        <input name="title" type="text" id="titleRecords" data-bind="value: title, attr:{disabled: formDisabled}" placeholder="My occurrence records" required
                               onfocus="clearPlaceholder(this)">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" >Column with species name or OTU number*:</label>
                    <div class="controls">
                        <select name="scientificName" data-bind="options:headers,value:selectedValue,optionsCaption:'Choose..', attr:{disabled: formDisabled}" required></select>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <button id="uploadBtnRecords" class="btn btn-primary" data-bind="click: uploadFile, attr:{disabled: formDisabled}"><i class="icon icon-white icon-upload"></i> Upload my file</button>
                        <button id="resetBtnRecords" class="btn" data-bind="click: resetForm, attr:{disabled: formDisabled}">Clear form</button>
                    </div>
                </div>
            </form>
            <div id="occurrenceUpload" class="well" data-bind="fadeVisible: progress()!=undefined">
                <label>Uploading Data:</label>
                <div class="progress progress-striped active">
                    <div class="bar" style="width: 0%;" data-bind="style:{width: progress() + '%'}"></div>
                </div>
                <div class="alert" data-bind="html: message, css:{ 'alert-error': error(), 'alert-success': !error()}, visible: !!message()">

                </div>
            </div>
            <div id="sandboxUpload" class="well" data-bind="fadeVisible: indexingProgress() != undefined">
                <label>Indexing Data:</label>
                <div class="progress progress-striped active">
                    <div class="bar" style="width: 0%;" data-bind="style:{width: indexingProgress() + '%'}"></div>
                </div>
                <div id="uploadMessage" class="alert" data-bind="html: indexingMessage, attr:{class: 'alert ' + indexingClass()}">

                </div>
            </div>
        </div>
    </div>
</div>
<div id="recordsmain">
    <div class="bs-callout" style="position: relative" id="pickFromListRecords">
        <div class="pull-left" ><h4><a>Or, pick a records dataset from the available list:</a></h4></div>
        <div class="pull-right">
            <form id="sourceToolbarRecords" >
                <div class="control-group">
                    <!--label class="control-label" for="">List of records available:</label-->
                    <div class="controls">
                        <select id="sourceCharRecords" data-bind="options:lists,optionsText:'title',value:selectedValue,
                        optionsCaption:'Choose..', event:{change: drChanged}" required></select>
                    </div>
                </div>
            </form>
        </div>
        <div >&nbsp;</div>
    </div>
</div>