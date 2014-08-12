<script id="_tmplTemplateCreate" type="text/html">
<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">
    <div class="span4">
        <g:message code="phylo.widget.template.label" default="Summarize data for" />:
    </div>
    <div class="span4">
            <input type="hidden" id="widgets[ID].type" name="widgets[ID].type" value="TYPENAME" readonly="true" />
            <input type="hidden" id="widgets[ID].config" name="widgets[ID].config" value="CONFIG" readonly="true" />
            <input type="text" id="widgets[ID].displayname" name="widgets[ID].displayname" readonly="true" value="DISPLAYNAME"/>
    </div>
</div>
</script>