<%--
 Created by Temi Varghese on 21/08/2014.
--%>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="col-sm-6 col-md-6">
        <div class="tabbable">
            <ul class="nav nav-tabs">
                <li class="active">
                    <a href="#searchTab" data-toggle="tab">Search</a>
                </li>
                <li>
                    <a href="#browseTab" data-toggle="tab">Browse</a>
                </li>
            </ul>

            <div class="tab-content">
                <div class="tab-pane active" id="searchTab">
                    <input id="modalAutoComplete"
                            class="span8"
                            data-bind="attr:{id: autocompleteId}"
                            type="text"
                            placeholder="Choose a layer here"
                            onfocus="utils.clearPlaceholder(this)"/>
                    <div class="col-sm-6 col-md-6">
                        <div class="well">

                        </div>
                    </div>
                </div>

                <div class="tab-pane" id="browseTab">

                </div>
            </div>
        </div>
        </div>
    </div>
</div>
<script>
        jQuery("#modalAutoComplete" ).autocomplete( {
            source: widget.envLayers,
            matchSubset: false,
            minChars: 3,
            scroll: true,
            max: 10,
            selectFirst: false,
            dataType: 'jsonp',
            formatMatch: function( row , i ){
                return row.label;
            },
            select: function( et , selection ){
                var item = selection.item;
                $.ajax("${createLink(controller: 'ala', action: 'layerSummaryFragment')}?layerName=" + item.name)
                $(document.getElementById( configId ) ).attr( 'value', selection.item.value );
                $( document.getElementById(  displayId  ) ).attr( 'value', selection.item.label );
            }
        })
        // the autocomplete box is appearing below the modal dialog. below logic to prevent it.
        $(".ui-autocomplete").css('z-index',1051);
</script>