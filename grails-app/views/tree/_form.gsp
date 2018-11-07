<%@ page import="au.org.ala.phyloviz.Tree" %>
%{--<g:hiddenField id="owner" name="owner.id" value="${tree?.owner?.id}"/>--}%
<g:hiddenField name="formSubmitted" value="1"></g:hiddenField>
<g:hiddenField name="id" value="${tree?.id}"></g:hiddenField>
<div class="control-group">
    <label class="control-label" for="tree">Tree data <span class="mandatory">*</span></label>

    <div class="controls ${hasErrors(bean: tree, field: 'tree', 'error')}">
        <g:textArea name="tree" class="field form-control" rows="8" value="${tree?.tree}"
                    placeholder="(Acacia_semicircinalis:0.03434179368091353,(Acacia_adinophylla:0.01786382226553121,Acacia_aphanoclada:0.027802263108902575));"
                    onfocus="utils.clearPlaceholder(this)" />

        <div class="row-fluid" style="padding-top:10px; padding-bottom:10px;">
            Or, upload a file:<br/>
            <label class="btn btn-default" for="my-file-selector">
                <input id="my-file-selector" type="file" name="file" style="display:none"
                       onchange="$('#upload-file-info').html(this.files[0].name)">
                <i class="glyphicon glyphicon-upload"> </i>
                <span id="upload-file-info">Upload load file</span>
            </label>
        </div>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="treeFormat">Tree data format <span class="mandatory">*</span></label>

    <div class="controls ${hasErrors(bean: tree, field: 'treeFormat', 'error')}">
        <g:select name="treeFormat" from="${['newick','nexml']}" value="${tree?.treeFormat}" class="span2"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="reference">Publication citation</label>

    <div class="controls ${hasErrors(bean: tree, field: 'reference', 'error')}">
        <g:textArea name="reference" class="form-control" value="${tree?.reference}"
        placeholder="Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x"
        onfocus="utils.clearPlaceholder(this)"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="title">Publication title <span class="mandatory">*</span></label>

    <div class="controls ${hasErrors(bean: tree, field: 'title', 'error')}">
        <g:textField name="title" class="form-control" value="${tree?.title}" required="" placeholder="The evolution and phylogenetic placement of invasive Australian Acacia species."
        onfocus="utils.clearPlaceholder(this)"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="year">Publication year</label>

    <div class="controls ${hasErrors(bean: tree, field: 'year', 'error')}">
        <g:textField name="year" class="form-control" value="${tree?.year?:''}" placeholder="2011"
         onfocus="utils.clearPlaceholder(this)"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="doi">Publication DOI</label>

    <div class="controls ${hasErrors(bean: tree, field: 'doi', 'error')}">
        <g:textField name="doi" class="form-control" value="${tree?.doi}" placeholder="10.1111/j.1472-4642.2011.00780.x"
         onfocus="utils.clearPlaceholder(this)"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="notes">Notes</label>

    <div class="controls ${hasErrors(bean: tree, field: 'notes', 'error')}">
        <g:textArea name="notes" class="form-control" value="${tree?.notes}"
                    placeholder=""/>
    </div>
</div>

<g:if test="${isAdmin}">
    <div class="control-group">
        <label class="control-label" for="notes">Default Query to use (to help optimise visualisations of large trees)
        - comma separated list of scientific names</label>
        <div class="controls">
            <g:textArea name="defaultQuery" class="form-control" value="${tree?.defaultQuery}" />
        </div>
    </div>
</g:if>

<div class="control-group">
    <div class="controls ${hasErrors(bean: tree, field: 'hide', 'error')}">
        <label class="checkbox">
            <g:checkBox name="hide" checked="${tree?.hide ? 'true' : 'false'}"/> Do not make this tree public
        </label>
    </div>
</div>

<g:if test="${isAdmin}">
<div class="control-group">
    <div class="controls ${hasErrors(bean: tree, field: 'hide', 'error')}">
        <label class="checkbox">
            <g:checkBox name="expertTree" checked="${tree?.expertTree ? 'true' : 'false'}"/> Make this expert public
        </label>
    </div>
</div>
</g:if>

<div class="col-sm-6 col-md-6" id="pubSuggestions">
</div>
