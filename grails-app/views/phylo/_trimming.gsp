<div id="pjTrimming" class="modal modal-wide fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>
                <h3 id="pjTrimmingLabel">Trimming Options</h3>
            </div>

            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="trimToInclude" class="control-label">Trim the tree to </label>
                            <label><input id="trimToInclude" type="radio" name="trimToInclude" value="true" data-bind="checked: trimToInclude" style="vertical-align: baseline"/>&nbsp;Include</label>
                            <label><input id="trimToExclude" type="radio" name="trimToInclude" value="false" data-bind="checked: trimToInclude" style="vertical-align: baseline"/>&nbsp;Exclude</label>
                    </div>

                    <div class="form-group">
                        <label for="trimOption" class="control-label">species from</label>
                            <select id="trimOption"
                                    data-bind="options:trimOptions, optionsText:'displayName', optionsCaption:'Choose...', value:trimOption"></select>
                    </div>

                    <div class="form-group" data-bind="visible:trimOption() == TRIM_LIST">
                        <label for="trimByList" class="control-label">Select a list</label>
                        <input id="trimByList" type="text" class="input-xlarge" data-bind="value:trimData"/>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button class="btn btn-primary" data-bind="click: $root.applyTrimOptions" aria-hidden="true">Apply</button>
                <button class="btn btn-default" data-bind="click: $root.clearTrimOptions" aria-hidden="true">Clear Trimming</button>
                <button class="btn btn-default" data-dismiss="modal" aria-hidden="true">Cancel</button>
            </div>
        </div>
    </div>
</div>