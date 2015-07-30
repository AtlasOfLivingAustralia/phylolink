<div id="${dialogId ?: 'occurrenceDownloadModal'}" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="Occurrence Downloads" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="myModalLabel">Download occurrence records</h3>
    </div>
    <div class="modal-body">
        <p>By downloading this content you are agreeing to use it in accordance with the Atlas of Living Australia <a href="http://www.ala.org.au/about-the-atlas/terms-of-use/#TOUusingcontent" target="_blank">Terms of Use</a> and any Data Provider Terms associated with the data download.</p>
        <p>Please provide the following details before downloading (* required):</p>
        <div class="form-horizontal">
            <div class="control-group">
                <label for="email" class="control-label">Email</label>
                <div class="controls">
                    <input type="text" id="email" data-bind="value: ${viewModel}.email">
                </div>
            </div>
            <div class="control-group">
                <label for="reason" class="control-label">Download reason *</label>
                <div class="controls">
                    <select id="reason" data-bind="options: ${viewModel}.downloadReasons, optionsText: 'displayName', optionsCaption: 'Choose...', value: ${viewModel}.reason"></select>
                </div>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn btn-primary occurrenceDownloadButton" data-bind="click: ${clickAction}, disable: !${viewModel}.reason()">Download</button>
        <button id="closeDownloadModal" class="btn closeDownloadModal" data-dismiss="modal" aria-hidden="true">Close</button>
    </div>
</div>