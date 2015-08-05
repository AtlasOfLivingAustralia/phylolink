<script id="_tmplContextual" type="text/html">
<div class="row">
    <div class="span6">
        <div data-bind="attr:{id: widgetId, name: widgetId}" class="panel">
            <div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} panel-heading">
                <g:message code="phylo.widget.contextual.label" default="Contextual Histogram Widget"/>
            </div>

            <div class="panel-content">
                <div class="row">
                    <div class="span10">
                        <div class="container-fluid">
                            <div class="row-fluid">
                                <div class="span6">
                                    <input type="hidden" data-bind="attr:{id: typeId, name:typeId, value:type}"
                                           value=""
                                           readonly="true"/>
                                    <input type="hidden"
                                           data-bind="attr:{id: configNameId, name:configNameId, value:config}" value=""
                                           readonly="true"/>

                                    <div class="row">
                                        <label class="span4">Title</label><input class="span8" type="text"
                                                                                 data-bind="attr:{id: titleId, name: titleId, value:title}"
                                                                                 value=""/>
                                    </div>

                                    <div class="row">
                                        <label class="span4"><g:message code="phylo.widget.contextual.layer"
                                                                        default="Chosen layer"/></label><input
                                            class="span8"
                                            type="text"
                                            data-bind="attr:{id: displayNameId, name: displayNameId, value:displayname}"
                                            readonly="true"
                                            value="" placeholder="Choose a layer using the button below"
                                            onfocus="clearPlaceholder(this)"/>

                                    </div>

                                    <div class="row">
                                        <button class="btn btn-primary offset6"><i
                                                class="icon-search icon-white"></i> Find a layer</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</script>