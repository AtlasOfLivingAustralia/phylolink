<%@ page import="au.org.ala.phyloviz.Phylo" %>
<link rel="stylesheet" href="${resource(dir: 'jqwidgets/styles', file: 'jqx.base.css')}" type="text/css"/>
<script>
    var regions = [];
    $.ajax({
        type:'GET',
        dataType: 'json',
        url:"${createLink( controller: 'phylo',action: 'getRegions')}",
        success:function( data ){
            regions = data;
                $("#regionName").autocomplete({
                    source: data,
                    matchSubset: false,
                    minChars: 3,
                    scroll: false,
                    max: 10,
                    selectFirst: false,
                    dataType: 'json',
                    formatMatch: function (row, i) {
                        return row.value;
                    },
                    select: function (event, item) {
                        $("#regionType").attr('value', item.item.type);
                    }
                });
        }
    });
</script>
<script id="_tmpModal" type="text/html">
    <div id="modalDialog" class="modal hide fade" style="overflow: hidden">
        <div class="modal-header"><button class="close" data-dismiss="modal" aria-hidden="true" >&times;</button><h3 id="modalHeader" data-bind="text: title"></h3></div>
        <div class="modal-body">Loading..<div class="loading"/> </div>
        <div class="modal-footer">
            <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">Save</a>
        </div>
    </div>
</script>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'displayName', 'error')} row">
    <div class="span4">
        <g:message code="phylo.displayName.label" default="Name" />:
</div>
    <div class="span8">
    <g:textField name="displayName" value="${phyloInstance?.displayName}"/>
</div>
</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'treeid', 'error')} required row">

<div class="span4">
		<g:message code="phylo.treeid.label" default="Tree ID" />
		<span class="required-indicator">*</span>:
</div>

<div class="span8">
	<g:field name="treeid" type="text" value="${phyloInstance.treeid}" required="" readonly="true"/>
    <g:field name="studyid" type="hidden" value="${phyloInstance.studyid}"  required=""/>
    <g:field name="index" type="hidden" value="${phyloInstance.index}"  required=""/>
    <g:field name="nodeid" type="hidden" value="${phyloInstance.nodeid}"/>
    </div>
</div>


<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'viz', 'error')} required row">
    <div class="span4">
		<g:message code="phylo.viz.label" default="Visualisation Tool" />
		<span class="required-indicator">*</span>:
    </div>
    <div class="span8">
    <g:select name="viz.viz" from="${au.org.ala.phyloviz.Visualization$VizType?.list()}"
              optionKey="id"
              keys="${au.org.ala.phyloviz.Visualization$VizType.list()}" required=""
              value="${phyloInstance?.viz?.viz}" class="many-to-one"/>
        </div>
</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'regionName', 'error')} row">
    <div class="span4">
        <g:message code="phylo.regions.label" default="Select a region" />:</div>
    <div class="span8">
    <g:field name="regionType" value="${phyloInstance?.regionType}" type="hidden" />
    <g:field name="regionName" value="${phyloInstance?.regionName}" type="text"/>
    </div>
</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'regionName', 'error')} row">
    <div class="span4">
        <g:message code="phylo.dataResouce.label" default="Data resource id" />:</div>
    <div class="span8">
        <g:field name="dataResource" value="${phyloInstance?.dataResource}" type="text"/>
    </div>
</div>

<div id="widgets">
    <g:each in="${phyloInstance.widgets}" var="w" status="i">
        <g:render template="${phyloInstance?.widgets?.getAt(i)?.type}/edit" model="[i:i, widget:w]"/>
    </g:each>
</div>
<div id="widgetSelectionRow" class="row">
            <div class="span8">
<g:select name="widgetType" from="${au.org.ala.phyloviz.WidgetType.list()}"
          value="" style="margin-bottom: 0px" optionValue="id"/>
<button class="btn" id="addWidget" onclick="addWidgetForm();return false;" >Add widget</button>
</div>
</div>
<script src="${resource(dir: 'jqwidgets', file: 'jqxcore.js')}"></script>
<script src="${resource(dir: 'jqwidgets', file: 'jqxbuttons.js')}"></script>
<script src="${resource(dir: 'jqwidgets', file: 'jqxscrollbar.js')}"></script>
<script src="${resource(dir: 'jqwidgets', file: 'jqxpanel.js')}"></script>
<script src="${resource(dir: 'jqwidgets', file: 'jqxtree.js')}"></script>
<script src="${resource(dir: 'jqwidgets', file: 'jqxexpander.js')}"></script>