<script id="_tmplPdCreate" type="text/html">
    <div id="widget[ID]">
        <div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">
            <div class="span2">
                <g:message code="phylo.widget.pd.label" default="Phylogenetic Diversity Widget" />:
            </div>
            <div class="span4">
                    <input type="hidden" id="widgets[ID].type" name="widgets[ID].type" value="pd" readonly="true" />
                    <input type="hidden" id="widgets[ID].config" name="widgets[ID].config" value="pd" readonly="true" />
                    <input type="hidden" id="widgets[ID].data" name="widgets[ID].data" value="" readonly="true" />
                    <input type="hidden" id="widgets[ID].displayname" name="widgets[ID].displayname" readonly="true" value="Phylogenetic Diversity"/>
                    <label class="span2">Title:</label><input class="offset2 span2" type="text" id="widgets[ID].title" name="widgets[ID].title" value="Phylogenetic Diversity (ID)"/>
            </div>
        </div>
        <div id="pdRegionsID" data-bind="foreach: regions">
            <div class="row">
                <div  class="offset2 span4">
                    <label class="span2">Choose region:</label>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="offset4 span4">
                <button class="btn" onclick="utils.addTemplate( 'pdRegionsID', '_tmpRegions' );return false">Add a region</button>
            </div>
        </div>
    </div>
</script>