<script id="_tmplPd" type="text/html">
    <div class="row">
    <div class="span6">
    <div data-bind="attr:{id: widgetId}" class="panel">
        <div class="panel-heading">
            <g:message code="phylo.widget.pd.label" default="Phylogenetic Diversity Widget" />
        </div>
        <div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row panel-content">
            <div class="span4">
                <input type="hidden" value="pd" readonly="true" data-bind="attr:{id: typeId,name: typeId, value: type}"/>
                <input type="hidden" value="pd" readonly="true" data-bind="attr:{id: configNameId,name: configNameId, value: config}"/>
                <input type="hidden"  value="" readonly="true" data-bind="attr:{id: dataId,name: dataId, value: data}"/>
                <input type="hidden"  readonly="true" value="Phylogenetic Diversity" data-bind="attr:{id: displayNameId,name: displayNameId, value:displayname}"/>
                <label class="span2">Title:</label><input class="offset2 span2" type="text" value="Phylogenetic Diversity (ID)" data-bind="attr:{id: titleId,name: titleId, value: title}"/>
            </div>
        </div>
        <div data-bind="attr:{id: pdRegionsId, name: pdRegionsId}">
            <div data-bind="foreach: regions">
                <div class="row">
                    <div  class="span4">
                        <label class="span2">Choose region:</label>
                        <select class="span2" data-bind="options:$parent.regionSelect, optionsText:'value', optionsValue:'code', value: $data.code"></select>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="offset2 span4">
                <button class="btn" data-bind="click: function( data, events){ data.addRegionSelect(); }">Add a region</button>
            </div>
        </div>
    </div>
    </div>
    </div>
</script>