<script id="_tmplContextualCreate" type="text/html">
<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">    <div class="span4">
    <g:message code="phylo.widget.contextual.label" default="Summarize data for" />:
</div>
<div class="span4">
        <input type="hidden" id="widgets[ID].type" name="widgets[ID].type" value="contextual" readonly="true" />
        <input type="hidden" id="widgets[ID].config" name="widgets[ID].config" value="" readonly="true" />
        <input type="text" id="widgets[ID].displayname" name="widgets[ID].displayname" readonly="true" value=""/>
</div>
<div class="span4">
        <input id="widgetautocompleteID" type="text" placeholder="Choose a layer here"
               onfocus="utils.clearPlaceholder(this)"/></div>
</div>
</script>