<div class="panel-group" id="uploadRecords" style="margin-top:20px;">
    <div class="panel panel-default">
        <div class="panel-heading" style="cursor:pointer">
            Upload your occurrence records
        </div>
        <div id="occurrenceUploadPanel" class="panel-body panel-collapse">

            <p>
                Upload your own occurrence data. Once you upload the dataset, you'll be able
                to select this for viewing with the phylogenetic tree and map. For best results please ensure the species name exactly matches the species name as shown 
				in the tree (please note that underscores in the species names in the tree are replaced with spaces before being shown, so please remove underscores from
				your species names in your occurrence data).
            </p>
            <div id="csvFormRecordsUnavailable" class="alert-error hide">
                <i>Login to enable occurrence upload or,
            you are unauthorized to upload for this visualisation.</i></div>

            <!-- upload file form -->
            <form id="csvFormRecords" class="form-horizontal" enctype="multipart/form-data">

                <label class="btn btn-default btn-file">
                    <i class="glyphicon glyphicon-upload"> </i>

                    <span id="upload-file-info">Upload a CSV file of occurrences</span>
                    <input id="csvFileRecords" type="file" name="file" value="Upload" accept=".csv" style="display: none;"
                           data-bind="event:{change: onNewFile}, attr:{disabled: formDisabled}"
                           onchange="$('#upload-file-info').html(this.files[0].name)"
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
                    <button id="resetBtnRecords" class="btn btn-default" data-bind="click: resetForm, attr:{disabled: formDisabled}">Cancel</button>
                </div>
            </form>

            <div id="sandboxProgress" style="margin-top: 10px;" class="well" data-bind="visible: progress() != undefined || indexingProgress() != undefined">
                <div id="occurrenceUpload" data-bind="visible: progress() != undefined">
                    <label data-bind="html:message, visible: !!message()"></label>
                    <label data-bind="if:!message()">Uploading data ...</label>
                    <div class="progress">
                        <div class="progress-bar"
                             role="progressbar"
                             data-bind="style:{width: progress() + '%'}"
                             aria-valuemin="0"
                             aria-valuemax="100">
                        </div>
                    </div>
                </div>

                <div id="sandboxUpload" data-bind="visible: indexingProgress() != undefined">
                    <label data-bind="html: indexingMessage">Indexing Data</label>
                    <div class="progress">
                        <div class="progress-bar"
                             role="progressbar"
                             data-bind="style:{width: indexingProgress() + '%'}"
                             aria-valuemin="10"
                             aria-valuemax="100">
                        </div>
                    </div>
                </div>
            </div>

            <div class="pull-right small">
                <a id="csvSampleRecords" target="_blank" data-bind="attr:{href: sampleFile}">Download sample occurrence CSV file from here.</a>
            </div>
        </div>
    </div>
</div>

<div id="recordsmain" class="panel-group"  style="margin-top:20px;">
    <div class="panel panel-default">
        <div class="panel-heading" id="pickFromListRecordsHeader">
            Select records dataset from the available list
        </div>
        <div class="panel-body">
            <p>
                Select the source of occurrence data from the options below.
                <br/>
                This will affect the records displayed on the map, and the records used
                to populate histograms in the Analysis section.
            </p>

            <div id="pickFromListRecords" class="panel-collapse">
                <form id="sourceToolbarRecords">
                    <div class="form-group row">
                        <label for="sourceCharRecords" class="col-sm-2 col-form-label">Occurrence&nbsp;dataset:</label>
                        <div class="col-sm-7">
                            <select id="sourceCharRecords"
                                    class="form-control"
                                    data-bind="options:lists,optionsText:'displayTitle',value:selectedValue,
                        optionsCaption:'Please choose..', event:{change: drChanged}, disable: ! edit()" required></select>
                        </div>
                        <div class="col-sm-3">
                            <g:if test="${edit}">
                            <button data-bind="disable: ! edit()" type="button" class="btn btn-default" data-toggle="modal" data-target="#occurrenceDatasets">
                                <i class="glyphicon glyphicon-cog"></i> Manage datasets
                            </button>
                            </g:if>
                        </div>
                    </div>
                </form>
            </div>

            <div id="occurrenceDatasets" class="modal fade" role="dialog">
                <div class="modal-dialog">
                    <!-- Modal content-->
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                            <h4 class="modal-title">Manage occurrence datasets</h4>
                        </div>
                        <div class="modal-body">
                            <div class="table-wrapper-scroll-y">
                                <table class="table">
                                    <tbody data-bind="foreach: lists">
                                        <tr data-bind="visible: id() != -1">
                                            <td data-bind="text: displayTitle"></td>
                                            <td>
                                                <a class="btn btn-default" target="_blank" data-bind="attr: {href: biocacheQueryUrl}">
                                                    View records
                                                </a>
                                            </td>
                                            <td>
                                                <button class="btn btn-danger" data-bind="disable: ! canDelete(), click:$parent.removeDataset;">Remove</button>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style type="text/css">
.table-wrapper-scroll-y {
    display: block;
    max-height: 300px;
    overflow-y: auto;
    -ms-overflow-style: -ms-autohiding-scrollbar;
}

</style>