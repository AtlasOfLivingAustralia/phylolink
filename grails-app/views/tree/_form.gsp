<%@ page import="au.org.ala.phyloviz.Tree" %>
%{--<g:hiddenField id="owner" name="owner.id" value="${tree?.owner?.id}"/>--}%
<div class="control-group">
    <label class="control-label" for="tree">Tree data</label>

    <div class="controls ${hasErrors(bean: tree, field: 'tree', 'error')}">
        <g:textArea name="tree" class="field span8" rows="8" value="${tree?.tree}"
                    placeholder="(Acacia_semicircinalis:0.03434179368091353,(Acacia_adinophylla:0.01786382226553121,Acacia_aphanoclada:0.027802263108902575));"/>
                    <div class="row-fluid">Or, upload a file: <input type="file" name="file" value="Upload"/></div>

                </div>
    %{--<div id="treeInfo" class="span6">--}%
    %{--</div>--}%
</div>

<div class="control-group">
    <label class="control-label" for="treeFormat">Tree data format</label>

    <div class="controls ${hasErrors(bean: tree, field: 'treeFormat', 'error')}">
        <g:select name="treeFormat" from="${['newick','nexml']}" value="${tree?.treeFormat}" class="span2"/>
        %{--<g:select name="treeFormat" from="${['nexml']}" value="${tree?.treeFormat}" class="span2"/>--}%
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="reference">Publication citation</label>

    <div class="controls ${hasErrors(bean: tree, field: 'reference', 'error')}">
        <g:textArea name="reference" class="span8" value="${tree?.reference}" required=""
        placeholder="Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="title">Publication title</label>

    <div class="controls ${hasErrors(bean: tree, field: 'title', 'error')}">
        <g:textField name="title" class="span8" value="${tree?.title}" required="" placeholder="The evolution and phylogenetic placement of invasive Australian Acacia species."/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="year">Publication year</label>

    <div class="controls ${hasErrors(bean: tree, field: 'year', 'error')}">
        <g:textField name="year" class="span2" value="${tree?.year?:''}" placeholder="2011"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="doi">Publication DOI</label>

    <div class="controls ${hasErrors(bean: tree, field: 'doi', 'error')}">
        <g:textField name="doi" class="span8" value="${tree?.doi}" placeholder="10.1111/j.1472-4642.2011.00780.x"/>
    </div>

    %{--<div id="doiField" class="span4">--}%

    %{--</div>--}%
</div>

<div class="control-group">
    <div class="controls ${hasErrors(bean: tree, field: 'hide', 'error')}">
        <label class="checkbox">
            <g:checkBox name="hide" checked="${tree?.hide ? 'true' : 'false'}"/> Do not make this tree public
        </label>
    </div>
</div>



%{--<div class="row">--}%
%{--<div class="span2">Is this an expert tree?</div>--}%
%{--<div class="span8 control-group ${hasErrors(bean:tree, field:'expertTree', 'error')}">--}%
%{--<g:checkBox name="expertTree" class="span8" checked="${tree?.expertTree?'true':'false'}"--}%
%{--onclick="toggleExpert( this )"/>--}%
%{--</div>--}%
%{--</div>--}%
%{--<div class="row">--}%
%{--<div class="span2">Expert tree taxonomic group</div>--}%
%{--<div class="span8 control-group ${hasErrors(bean:tree, field:'expertTreeTaxonomy', 'error')}">--}%
%{--<g:textField name="expertTreeTaxonomy" class="span8" value="${tree?.expertTreeTaxonomy}"--}%
%{--disabled="${tree?.expertTree?'false':'true'}"/>--}%
%{--</div>--}%
%{--</div>--}%
%{--<g:hiddenField name="expertTreeLSID" class="span8" value="${tree?.expertTreeLSID}"/>--}%
%{--<div class="row">--}%
%{--<div class="span2">Expert Tree ID</div>--}%
%{--<div class="span8 control-group ${hasErrors(bean:tree, field:'expertTreeID', 'error')}">--}%
%{--<g:textField name="expertTreeID" class="span8" value="${tree?.expertTreeID}"--}%
%{--disabled="${tree?.expertTree?'false':'true'}"/>--}%
%{--</div>--}%
%{--</div>--}%
<script>
    $("#expertTreeTaxonomy").autocomplete({
        source: function (request, response) {
            var url = "${createLink(controller: 'tree', action: 'autocomplete')}" +
                    "?q=" + request.term;
            $.ajax(url).done(function (data) {
                var rows = [];
                if (data.autoCompleteList) {
                    var list = data.autoCompleteList;
                    for (var i = 0; i < list.length; i++) {
                        rows[i] = {
                            value: list[i].name,
                            label: list[i].name,
                            data: list[i]
                        };
                    }
                }
                if (response) {
                    response(rows);
                }
            });
        },
        select: function (event, i) {
            $('#expertTreeLSID').val(i.item.data.guid);
        }
    })
    /**
     * this function will disable / enable input textbox
     */
    function toggleExpert(checkbox) {
        if (checkbox.checked) {
            $('#expertTreeTaxonomy').attr('disabled', false)
            $('#expertTreeID').attr('disabled', false)
        } else {
            $('#expertTreeTaxonomy').attr('disabled', true)
            $('#expertTreeID').attr('disabled', true)
        }
    }
</script>

<div class="span6" id="pubSuggestions">

</div>

<script>
    function treeStats() {
        var tree = $('#tree').val();
        console.log(tree);
        $.ajax({
            url: "${createLink( controller: 'tree', action: 'getTreeMeta')}",
            type: 'POST',
            data: {
                tree: tree
            },
            success: function (data) {

            },
            error: function (data) {

            }
        });
        $.ajax({
            url: "${createLink( controller: 'tree', action: 'treeInfo')}.html",
            type: 'POST',
            data: {
                tree: tree
            },
            success: function (data) {
                $("#treeInfo").html(data)
            },
            error: function (data) {

            }
        })
    }
    function doiInfo() {
        var doi = $('#doi').val()
        console.log(doi)
        $.ajax({
            url: "${createLink( controller: 'tree', action: 'searchDoi')}.json",
            type: "GET",
            data: {
                q: doi
            },
            success: function (data) {
                $("#doiField").html(JSON.stringify(data))
            }
        })
    }

    //    function StudyViewModel(){
    //        this.citation = ko.observable();
    //        this.year = ko.observable();
    //        this.title = ko.observable();
    //        this.format = ko.observable();
    //        this.doi  = ko.observable();
    //        this.tree = ko.observable();
    //    }
    //    ko.applyBindings( new StudyViewModel() )
</script>