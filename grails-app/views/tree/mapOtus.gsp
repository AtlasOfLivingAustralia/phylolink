<%--
 Created by Temi Varghese on 3/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" defaultCodec="html" %>
<html>
<head>
    <title>${tree.title} - Name reconciliation</title>
    <meta name="layout" content="main"/>
    <meta name="breadcrumbs" content="${g.createLink( controller: 'phylo', action: 'startPage')}, Phylolink \\ ${createLink(controller: 'wizard', action: 'start')}, Start PhyloLink \\ ${g.createLink( controller: 'tree', action: 'treeAdmin')},Tree admin"/>

</head>
<body>
<div class="container">

    <h1>${tree.title} - Reconcile names with ALA taxonomy </h1>
    <p>
        On this page, you can reconcile the tip names of your tree with ALA taxonomy.
        The system-matched name is listed in the <span class="strong">Matched name</span> column.
        <br/>
        To edit, click on a cell and save changes, click <span class="strong">Save</span> button.
        <br/>
        <strong>Note:</strong> The names and ALA matched name and ALA matched GUID are the values
        used for querying occurrence data. Changes to the  ALA matched name will not
        change what is displayed in the tree view.
    </p>
    <div id="myGrid">
        <table class="table">
            <thead>
                <th>Original name from tree</th>
                <th>ALA matched name</th>
                <th>ALA matched GUID</th>
                <th></th>
            </thead>
            <tbody>
            <g:each in="${otus}" var="otu">
            <tr id="${otu.id}">
                <td class="hide">
                    <input type="text" class="nodeID" value="${otu.id}"/>
                </td>
                <td class="hide">
                    <input type="text" class="nodeOtuID" value="${otu.otuId}"/>
                </td>
                <td>
                    ${otu['^ot:originalLabel']}
                </td>
                <td>
                    <input type="text" class="nodeLabel form-control" value="${otu['^ot:altLabel']}"/>
                </td>
                <td>
                    <input type="text" class="nodeGuid form-control"  value="${otu['@ala']}"/>
                </td>
                <td>
                    <button class="saveOtuChange btn btn-default" disabled>Save</button>
                </td>
                <td>
                    <span class="status"></span>
                </td>
            </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
    <g:javascript>

        $( ".nodeLabel, .nodeGuid" ).on("click change paste keyup", function(event) {
            var $row = $(event.target).parent().parent();
            $row.find('.saveOtuChange').addClass('btn-primary');
            $row.find('.saveOtuChange').removeClass('btn-default');
            $row.find('.saveOtuChange').removeAttr("disabled");
        });

        $( ".saveOtuChange" ).click(function(event) {
            var $row = $(event.target).parent().parent();
            var data = {
                id: ${params.id},
                otus: JSON.stringify([{
                    nodeID : $row.find('.nodeID').val(),
                    'otuId' : $row.find('.nodeOtuID').val(),
                    '@ala' : $row.find('.nodeGuid').val(),
                    '^ot:altLabel' : $row.find('.nodeLabel').val()
                }])
            };
            $.ajax({
                method: "POST",
                url: '${raw(createLink(controller: "tree", action:"saveOtus" ))}',
                data: data
            }).done(function() {
                $row.find('.saveOtuChange').addClass('btn-default');
                $row.find('.saveOtuChange').removeClass('btn-primary');
                $row.find('.saveOtuChange').attr("disabled", true);
            }).fail(function() {
                alert( "There was a problem saving" );
            });
        });


    </g:javascript>
</body>


</html>