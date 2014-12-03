<%--
 Created by Temi Varghese on 3/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" defaultCodec="html" %>
<html>
<head>
    <title>Name reconciliation</title>
    <meta name="layout" content="main"/>
    <r:require modules="slickgrid, appSpecific"/>
    <jqui:resources theme="darkness"/>
</head>

<body>
<div class="container">
    <div id="myGrid" style="width: 500px;height: 400px">

    </div>

    <div id="alaProfile" class="span6">

    </div>
</div>

<div class="container">
    <div class="control-group">
        <div class="controls">
            <g:form controller="tree" action="saveOtus" method="POST">
                <input type="hidden" name="otus" id="otus">
                <input type="hidden" name="id" value="${id}">
                <g:submitButton name="Save" class="btn" onclick="saveOtus()"></g:submitButton>
                <g:actionSubmit value="Visualise" action="visualize" onclick="saveOtus()" class="btn btn-primary"/>
            </g:form>
        </div>
    </div>
</div>

<script>
    var profileId = 'alaProfile'
    var cache = []
    var renderProfile = function (guid, data) {
        if (guid == null) {
            $('#' + profileId).html('');
            return
        }
        if (cache[guid]) {
            $('#' + profileId).html(cache[guid]);
        } else {
            $.ajax({
                url: '${createLink( controller: 'PhylogeneticTree', action:'taxonInfo')}/',
                data: {
                    q: guid
                },
                success: function (data) {
                    cache[guid] = data
                    renderProfile(guid)
                },
                failure: function () {
                    $('#' + profileId).html("<div class='error'>Error occurred</div>");
                }
            })
        }
    }
    var otus = <g:message message="${otus as grails.converters.JSON}"/>;

    var columns = [
        {
            name: 'Original name',
            id: '^ot:originalLabel',
            field: '^ot:originalLabel',
            width: 250
        },
        {
            name: 'Matched name',
            id: '^ot:altLabel',
            field: '^ot:altLabel',
            editor: BVP.SlickGrid.Autocomplete(1, ''),
            width: 250
        }
    ]

    var options = {
        editable: true,
        enableAddRow: true,
        enableCellNavigation: true,
        asyncEditorLoading: false,
        autoEdit: true,
        syncColumnCellResize: true,
        enableColumnReorder: false,
        editCommandHandler: function (item, grid, autocomplete) {
            console.log(arguments)
            item['@ala'] = autocomplete.editor.selectedItem.data.guid;
            renderProfile(item['@ala'])
            autocomplete.execute()
        }
    };
    var dataView = new Slick.Data.DataView();
    var grid = new Slick.Grid("#myGrid", dataView, columns, options)
    grid.onSelectedRowsChanged.subscribe(function (e, args) {
//        console.log(args)
//        debugger;
        renderProfile(args.item['@ala'])
    })
    dataView.onRowCountChanged.subscribe(function (e, args) {
        grid.updateRowCount();
        grid.render();
    })

    dataView.onRowsChanged.subscribe(function (e, args) {
        grid.invalidateRows(args.rows);
        grid.render();
    })

    dataView.setItems(otus)

    function saveOtus() {
        $('#otus').val(JSON.stringify(otus))
    }
</script>
</body>
</html>