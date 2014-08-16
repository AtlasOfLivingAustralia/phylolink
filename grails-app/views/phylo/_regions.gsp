<%@ page import="au.org.ala.phyloviz.PhyloController" %>
%{--
params
    id
    name
    type
--}%
<g:select class="offset2 span2" id="${id}" name="${name}" from="${new PhyloController().getRegionsByType(type)}" optionKey="code" optionValue="value" key=""/>