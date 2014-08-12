<%--
 Created by Temi Varghese
 --%>
<script type="text/javascript">
    var widgets= {
        widgetsList : [],
        widgetUrl: '${createLink(controller: "phylo", action:"getWidgetData" )}/${phyloInstance.id}',
        counter: 0,
        add :  function( widget ){
            this.widgetsList.push( widget );
            this.counter ++;
        },
        load: function( data ){
            var data = JSON.stringify( data), widget, params;
            for( var wid in this.widgetsList ){
                params = {};
                widget =  this.widgetsList[wid];
                params.speciesList = data
                params.wid =  widget.getId();
                widget.addParams( params );
                $.ajax({
                    // this is the index. this is need to call the correct widget display
                    widget: widget,
                    url: this.widgetUrl,
                    method:'POST',
                    data:params,
                    success: this.display
                });
            }
        },
        display:function( data ){
            this.widget.display( data );
        }
    };
    google.load("visualization", "1", {packages:["corechart"]});
</script>
<g:render template="environmental/script"/>
<g:render template="contextual/script"/>
<g:render template="pd/script"/>