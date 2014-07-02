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
    var counter = ${phyloInstance.widgets? phyloInstance.widgets.size() : 0 };
    function addWidgetForm(){
        var widgetid = "widgets[" + counter +  "]",
            id = "widgets[" + counter +  "].config",
            nameid = widgetid+".displayname", source, widgetType;
        var tpl =  '<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} ">\
            <label for="widgets">\
            <g:message code="phylo.widgets.label" default="layer" />\
            </label>\
            <input type="text" id="' + id +  '" name="' + id +  '" value=""/><input type="text" id="'+nameid+'" name="'+ nameid +'" value=""/><br/><input id="contextualautocomplete'+counter+'" type="text"/>\
            </div>';
        $("#widgets").append( tpl );
        widgetType = $("#widgetType").attr('value');
        switch ( widgetType ){
            case 'Contextual':
                source = contextual;
                break;
            case 'Environmental':
                source = environmental;
                break;

        }


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


<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'treeid', 'error')} required">
	<label for="treeid">
		<g:message code="phylo.treeid.label" default="Treeid" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="treeid" type="string" value="${phyloInstance.treeid}" required=""/>
    <g:field name="studyid" type="string" value="${phyloInstance.studyid}" hidden="true" required=""/>
    <g:field name="index" type="string" value="${phyloInstance.index}" hidden="true" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'nodeid', 'error')} ">
	<label for="nodeid">
		<g:message code="phylo.nodeid.label" default="Nodeid" />
		
	</label>
	<g:field name="nodeid" type="number" value="${phyloInstance.nodeid}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'displayName', 'error')} ">
	<label for="displayName">
		<g:message code="phylo.displayName.label" default="Display Name" />
		
	</label>
	<g:textField name="displayName" value="${phyloInstance?.displayName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'viz', 'error')} required">
	<label for="viz">
		<g:message code="phylo.viz.label" default="Viz" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:select id="viz" name="viz.id" from="${au.org.ala.phyloviz.Visualization.list()}" optionKey="id" required="" value="${phyloInstance?.viz?.id}" class="many-to-one"/>--}%
    <g:select name="viz.viz" from="${au.org.ala.phyloviz.Visualization$VizType?.values()}"
              optionKey="id"
              keys="${au.org.ala.phyloviz.Visualization$VizType.values()*.name()}" required=""
              value="${phyloInstance?.viz?.viz}" class="many-to-one"/>
</div>

<div id="widgets">
    <g:each in="${phyloInstance.widgets}" var="w" status="i">
        %{--<g:if test="${phyloInstance?.env[0]}">--}%
            <div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} ">
                <label for="widgets">
                    <g:message code="phylo.env.label" default="layer" />
                </label>
                %{--<g:textField name="env[0].config" value="${phyloInstance?.env[0].config}"/>--}%
                <g:field name="widgets[${i}].config" value="${phyloInstance?.widgets?.getAt(i)?.config}" hidden="true" type="string" required=""/>
                <g:field name="widgets[${i}].displayname" value="${phyloInstance?.widgets?.getAt(i)?.displayname}" required="" type="string"/>
            </div>
        %{--</g:if>--}%
    </g:each>
</div>
<g:select name="widgetType" from="${au.org.ala.phyloviz.WidgetType.list()}"
          value=""/>
<button id="addWidget" onclick="addWidgetForm();return false;">Add a Widget</button>