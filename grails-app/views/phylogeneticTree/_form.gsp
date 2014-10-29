<%@ page import="au.org.ala.phyloviz.PhylogeneticTree" %>
<div class="well well-small">
    <div class="panel">
        <div class="row">
            <div class="span2">Tree</div>
            <div class="span5">
                <g:textArea name="tree" class="field span5" rows="8" value="${phylogeneticTreeInstance?.tree}"
                            required="" onchange="treeStats()"/>
            </div>
            <div id="treeInfo" class="span6">
            </div>
        </div>
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span7">
                    <div class="row">
                        <div class="span2">Tree format</div>
                        <div class="span5">
                            %{--<g:textField name="format" class="span5" onchange="doiInfo()"/>--}%
                            <g:select name="format" from="${['nexml','newick','nexus']}" value="nexml"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="span2">Title</div>
                        <div class="span5">
                            <g:textField name="title" class="span5" onchange="doiInfo()"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="span2">Citation</div>
                        <div class="span5">
                            <g:textArea name="reference" class="span5" onchange="doiInfo()"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="span2">Study Year</div>
                        <div class="span5">
                            <g:textField name="year" class="span5" onchange="doiInfo()"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="span2">DOI</div>
                        <div class="span5">
                            <g:textField name="doi" class="span5" onchange="doiInfo()"/>
                        </div>
                        <div id="doiField" class="span4">

                        </div>
                    </div>
                    <div class="row">
                        <div class="span2">Hide this tree?</div>
                        <div class="span5">
                            <g:checkBox name="public" class="span5" onchange="doiInfo()" checked="true"/>
                        </div>
                    </div>
                    %{--<div class="row">--}%
                        %{--<div class="span2">Tags</div>--}%
                        %{--<div class="span5">--}%
                            %{--<g:textField name="tag" class="span5" onchange="doiInfo()"/>--}%
                        %{--</div>--}%
                    %{--</div>--}%
                </div>
                <div class="span6" id="pubSuggestions">

                </div>
            </div>
        </div>
    </div>
</div>
<script>
    function treeStats(){
        var tree = $('#tree').val();
        console.log( tree );
        $.ajax({
            url: "${createLink( controller: 'phylogeneticTree', action: 'treeInfo')}.html",
            type:'POST',
            data:{
              tree: tree
            },
            success: function( data ){
                $("#treeInfo").html( data )
            },
            error: function( data ){

            }
        })
    }
    function doiInfo(){
        var doi = $('#doi').val()
        console.log( doi )
        $.ajax({
            url: "${createLink( controller: 'phylogeneticTree', action: 'searchDoi')}.json",
            type:"GET",
            data:{
                q: doi
            },
            success: function( data ){
                $("#doiField").html( JSON.stringify( data ))
            }
        })
    }
</script>