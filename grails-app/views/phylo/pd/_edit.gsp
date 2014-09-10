<%@ page import="grails.converters.JSON" %>
%{--<div id="widget[${i}]">--}%
    %{--<div class="fieldcontain ${hasErrors(bean: phyloInstance, field: 'widgets', 'error')} row">--}%
        %{--<div class="span2">--}%
            %{--<g:message code="phylo.widget.pd.label" default="Phylogenetic Diversity Widget" />:--}%
        %{--</div>--}%
        %{--<div class="span4">--}%
            %{--<input type="hidden" id="widgets[${i}].type" name="widgets[${i}].type" value="${widget.type}" readonly="true" />--}%
            %{--<input type="hidden" id="widgets[${i}].config" name="widgets[${i}].config" value="${widget.config}" readonly="true" />--}%
            %{--<input type="hidden" id="widgets[${i}].data" name="widgets[${i}].data" value="${widget.data}" readonly="true" />--}%
            %{--<input type="hidden" id="widgets[${i}].displayname" name="widgets[${i}].displayname" readonly="true" value="${widget.displayname}"/>--}%
            %{--<label class="span2">Title:</label><input class="offset2 span2" type="text" id="widgets[${i}].title" name="widgets[${i}].title" value="${widget.title}"/>--}%
        %{--</div>--}%
    %{--</div>--}%
    %{--<div id="pdRegions${i}">--}%
       %{--<g:if test="${widget.data}">--}%
        %{--<g:def var="data" value="${JSON.parse( widget.data )}"/>--}%
        %{--<g:each in="${data}" var="region">--}%
            %{--<script>--}%
                %{--$(document).ready( function (){--}%
                    %{--utils.addTemplate("pdRegions${i}","_tmpRegions").find('select').attr('value',"${region}")--}%
                %{--});--}%
            %{--</script>--}%
        %{--</g:each>--}%
        %{--</g:if>--}%
    %{--</div>--}%
    %{--<div class="row">--}%
        %{--<div class="offset4 span4">--}%
            %{--<button class="btn" onclick="utils.addTemplate( 'pdRegions${i}', '_tmpRegions' );return false">Add a region</button>--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%
%{--${grailsApplication.config.grails.views.default.codec="none"}--}%
<script type="text/javascript">
    $(document).ready( function (){
        data = ${raw(( widget as JSON ).toString() )}
//        data =  data.replace(/&quot;/g,"\"")
//        console.log(data)
//        data = JSON.parse( data )
//        console.log(data)
        new widgets.PD( ${i}, data).create("widgets");
    })
</script>
%{--${raw( widget as JSON ) }--}%
%{--${"<br/>"}--}%
%{--<g:fieldValue field="data" bean="${widget}"/>--}%
%{--${grailsApplication.config.grails.views.default.codec="html"}--}%