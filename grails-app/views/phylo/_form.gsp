<%@ page import="au.org.ala.phyloviz.Phylo" %>
<script>
        var contextual = [], environmental = [];
        $.ajax({
            type:'jsonp',
            dataType: 'jsonp',
            url:"http://spatial.ala.org.au/ws/layers",
            success:function( data ){
                data && data.forEach(function ( val , index ){
                    switch ( val.type){
                        case "Contextual":
                            contextual.push( {
                                data : val,
                                value: "cl" + val.id,
                                label: val.displayname
                            } );
//                            contextual.push( val.displayname );
                            break;
                        case "Environmental":
                            environmental.push( {
                                data : val,
                                value: "el" + val.id,
                                label: val.displayname
                            } );
                            break;
                    }
                });
            }
        })
</script>
<script>
    var regions = [];
    $.ajax({
        type:'GET',
        dataType: 'json',
        url:"${createLink( controller: 'phylo',action: 'getRegions')}",
        success:function( data ){
            regions = data;
//            $.ready( function () {
                $("#regionName").autocomplete({
                    source: data,
                    matchSubset: false,
                    minChars: 3,
                    scroll: false,
                    max: 10,
                    selectFirst: false,
                    dataType: 'json',
                    formatMatch: function (row, i) {
//                    console.log( row );
                        return row.value;
                    },
                    select: function (event, item) {
                        $("#regionType").attr('value', item.item.type);
                    }
                });
//            });
        }
    });
</script>
<script>
    var counter = ${phyloInstance.widgets? phyloInstance.widgets.size() : 0 };
    function addWidgetForm(){
        var widgetid = "widgets[" + counter +  "]",
            id = "widgets[" + counter +  "].config",
            nameid = widgetid+".displayname", source, widgetType, typeid = widgetid+".type";
        var tpl ;
        widgetType = $("#widgetType").attr('value');
        switch ( widgetType ){
            case 'Contextual':
                tpl = '<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">\
            <div class="span4">\
            <g:message code="phylo.widgets.label" default="Summarize data for" />:\
            </div><div class="span4">\
            <input type="hidden" id="' + id +  '" name="' + id +  '" value="contextual" readonly="true" />\
            <input type="hidden" id="' + id +  '" name="' + id +  '" value="pd" readonly="true" />' +
                ' <input type="text" id="'+nameid+'" name="'+ nameid +'" readonly="true" value=""/></div><div class="span4"> ' +
                '<input id="contextualautocomplete'+counter+'" type="text" placeholder="Choose a layer here"/></div>\
            </div>'
                source = contextual;
                break;
            case 'Environmental':
                    tpl = '<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">\
            <div class="span4">\
            <g:message code="phylo.widgets.label" default="Summarize data for" />:\
            </div><div class="span4">\
            <input type="hidden" id="' + typeid +  '" name="' + typeid +  '" value="environmental" readonly="true" />\
            <input type="hidden" id="' + id +  '" name="' + id +  '" value="pd" readonly="true" />' +
                            ' <input type="text" id="'+nameid+'" name="'+ nameid +'" readonly="true" value=""/></div><div class="span4"> ' +
                            '<input id="contextualautocomplete'+counter+'" type="text" placeholder="Choose a layer here"/></div>\
            </div>'
                source = environmental;
                break;
            case 'PD':
                tpl = '<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">\
            <div class="span4">\
            Metric:\
            </div><div class="span4">\
            <input type="hidden" id="' + typeid +  '" name="' + typeid +  '" value="pd" readonly="true" />\
            <input type="hidden" id="' + id +  '" name="' + id +  '" value="pd" readonly="true" />' +
                        ' <input type="text" id="'+nameid+'" name="'+ nameid +'" readonly="true" value="PD"/></div>\
            </div>';

        }
        $("#widgets").append( tpl );

        jQuery("#contextualautocomplete"+counter).autocomplete( {
            source: source,
            matchSubset: false,
            minChars: 3,
            scroll: false,
            max: 10,
            selectFirst: false,
            dataType: 'jsonp',
            formatMatch: function( row , i){
                console.log( row );
                return row.label;
            },
            select: function( et , selection ){
                $(document.getElementById(id)).attr( 'value', selection.item.value );
                $(document.getElementById(nameid)).attr( 'value', selection.item.label );
            }
        });
        counter ++;
    }
</script>


<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'displayName', 'error')} row">
    %{--<label for="displayName">--}%
    <div class="span4">
        <g:message code="phylo.displayName.label" default="Study Name" />:
</div>
    %{--</label>--}%
    <div class="span8">
    <g:textField name="displayName" value="${phyloInstance?.displayName}"/>
</div>
</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'treeid', 'error')} required row">
	%{--<label for="treeid">--}%
<div class="span4">
		<g:message code="phylo.treeid.label" default="Treeid" />
		<span class="required-indicator">*</span>:
</div>
	%{--</label>--}%
<div class="span8">
	<g:field name="treeid" type="text" value="${phyloInstance.treeid}" required="" readonly="true"/>
    <g:field name="studyid" type="hidden" value="${phyloInstance.studyid}"  required=""/>
    <g:field name="index" type="hidden" value="${phyloInstance.index}"  required=""/>
    <g:field name="nodeid" type="hidden" value="${phyloInstance.nodeid}"/>
    </div>
</div>


<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'viz', 'error')} required row">
	%{--<label for="viz">--}%    <div class="span4">
		<g:message code="phylo.viz.label" default="Choose visualisation software" />
		<span class="required-indicator">*</span>:
    </div>
    <div class="span8">
	%{--</label>--}%
	%{--<g:select id="viz" name="viz.id" from="${au.org.ala.phyloviz.Visualization.list()}" optionKey="id" required="" value="${phyloInstance?.viz?.id}" class="many-to-one"/>--}%
    <g:select name="viz.viz" from="${au.org.ala.phyloviz.Visualization$VizType?.list()}"
              optionKey="id"
              keys="${au.org.ala.phyloviz.Visualization$VizType.list()}" required=""
              value="${phyloInstance?.viz?.viz}" class="many-to-one"/>
        </div>
</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'regionName', 'error')} row">
    %{--<label for="displayName">--}%    <div class="span4">
        <g:message code="phylo.regions.label" default="Select a region" />:</div>
    %{--</label>--}%    <div class="span8">
    <g:field name="regionType" value="${phyloInstance?.regionType}" type="hidden" />
    <g:field name="regionName" value="${phyloInstance?.regionName}" type="text"/>
    </div>
</div>

<div id="widgets">
    <g:each in="${phyloInstance.widgets}" var="w" status="i">
        %{--<g:if test="${phyloInstance?.env[0]}">--}%
            <div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">
                %{--<label for="widgets">--}%
            <div class="span4">
                    <g:message code="phylo.env.label" default="layer" />:
            </div>
                %{--</label>--}%
                %{--<g:textField name="env[0].config" value="${phyloInstance?.env[0].config}"/>--}%
        <div class="span8">
                <g:field name="widgets[${i}].config" value="${phyloInstance?.widgets?.getAt(i)?.config}" type="hidden" required=""/>
                <g:field name="widgets[${i}].displayname" value="${phyloInstance?.widgets?.getAt(i)?.displayname}" required="" readonly="" type="text"/>
            </div>
            </div>
        %{--</g:if>--}%
    </g:each>
</div>
<div id="widgetSelectionRow" class="row">
        %{--<div class="span4">--}%
    %{--Widget:--}%
            %{--</div>--}%
            <div class="offset4 span8">
<g:select name="widgetType" from="${au.org.ala.phyloviz.WidgetType.list()}"
          value="" style="margin-bottom: 0px"/>
                %{--</div><div class="span4">--}%
<button class="btn" id="addWidget" onclick="addWidgetForm();return false;" >Add widget</button>
</div>
</div>