<!-- Phylojive Settings modal popup -->
<div id="pjSettings" class="modal modal-wide fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3 id="pjSettingLabel">Phylojive Settings</h3>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            <label class="checkbox">
              <input type="checkbox" data-bind="checked: alignName, click: $root.alignPJ"> Align names in vertical line
            </label>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button class="btn btn-default" data-dismiss="modal" aria-hidden="true">Close</button>
      </div>
    </div>
  </div>
</div>