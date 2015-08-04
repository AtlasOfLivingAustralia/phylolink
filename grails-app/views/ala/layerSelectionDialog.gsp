<%--
 Created by Temi Varghese on 22/08/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

    <div class="tabbable">
        <ul class="nav nav-tabs">
            <li class="active">
                <a href="#modalSearchTab" data-toggle="tab">Search</a>
            </li>
            <li>
                <a href="#modalBrowseTab" data-toggle="tab">Browse</a>
            </li>
        </ul>
            <div class="tab-content">
                <div id="modalSearchTab" class="tab-pane active">
                    <input id="modalAutoComplete" class="ui-autocomplete-input" aria-autocomplete="list" aria-haspopup="true" autocomplete="off"
                           type="text"
                           placeholder="Choose a layer here"
                           onfocus="clearPlaceholder(this)"/>
                        <div id="modalSearchWell" class="well" style="max-height: 250px; height: 250px">

                        </div>
                </div>

                <div id="modalBrowseTab" class="tab-pane">

                </div>
            </div>
    </div>

<script type="text/javascript">
    var list;
    var env = "${params.type}";
    var i = ${params.i};

    switch ( env ){
        case '${grailsApplication.config.layersMeta.env}':
            list = widgets.envLayers;
            break;
        case '${grailsApplication.config.layersMeta.cl}':
            list = widgets.clLayers;
            break;
    }
    jQuery("#modalAutoComplete" ).autocomplete( {
        source: list,
        matchSubset: false,
        minChars: 3,
        scroll: true,
        max: 10,
        selectFirst: false,
        dataType: 'jsonp',
        formatMatch: function( row , i ){
            return row.name;
        },
        select: function( et , selection ){
            var item = selection.item;
            $.ajax("${createLink(controller: 'ala', action: 'layerSummaryFragment')}?layerName=" + item.name, {success:function(data ){
                $("#modalSearchWell").html( data );
            }})
            widgets.select( i, item );
        }
    })
    // the autocomplete box is appearing below the modal dialog. below logic to prevent it.
    $(".ui-autocomplete").css('z-index',1051);

    $('a[data-toggle="tab"]').on('shown', function (e) {

        $("#modalBrowseTab").html("");
        $("#modalSearch").html("");

        var tabHref = $(this).attr('href');

        if (tabHref == '#searchTab') {
            $("#modalAutoComplete").focus();
        } else if (tabHref == '#modalBrowseTab') {
            $('#modalBrowseTab').html("Loading... <div class='loading'></div>");
            $.ajax({
                url: "${createLink(controller: 'ala', action: 'browseLayersFragment')}",
                data: {
                    id:i,
                    type: env
                },
                success: function (data) {
                    $("#modalBrowseTab").html(data);
                }
            })
        }
    });
    $("#modalBrowseTab").click( function () {

    })
</script>