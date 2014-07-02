<%@ page import="au.org.ala.phyloviz.Visualization" %>



<div class="fieldcontain ${hasErrors(bean: visualizationInstance, field: 'viz', 'error')} required">
    <label for="viz">
        <g:message code="visualization.viz.label" default="Viz"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select name="viz" from="${au.org.ala.phyloviz.Visualization$VizType?.values()}"
              keys="${au.org.ala.phyloviz.Visualization$VizType.values()*.name()}" required=""
              value="${visualizationInstance?.viz?.name()}"/>

</div>

