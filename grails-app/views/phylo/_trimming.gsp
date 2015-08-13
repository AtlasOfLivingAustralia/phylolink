<div id="pjTrimming" class="modal modal-wide hide fade" tabindex="-1" role="dialog" aria-labelledby="pjTrimmingLabel"
     aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>

        <h3 id="pjTrimmingLabel">Trimming Options</h3>
    </div>

    <div class="modal-body">
        <div class="form-horizontal">
            <div class="control-group">
                <label for="trimOption" class="control-label">Trim the tree to show</label>
                <div class="controls">
                    <select id="trimOption"
                            data-bind="options:trimOptions, optionsText:'displayName', optionsCaption:'Choose...', value:trimOption"></select>
                </div>
            </div>

            <div class="control-group" data-bind="visible:trimOption() == TRIM_LIST">
                <label for="trimByList" class="control-label">Select a list</label>
                <div class="controls">
                    <input id="trimByList" type="text" class="input-xlarge" data-bind="value:trimData"/>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" data-bind="click: $root.applyTrimOptions" aria-hidden="true">Apply</button>
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
    </div>
</div>