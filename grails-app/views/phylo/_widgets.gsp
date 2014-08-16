<%--
 Created by Temi Varghese
 --%>
<script type="text/javascript">
    var widgets= {
        widgetsList : [],
        widgetUrl: '${createLink(controller: "phylo", action:"getWidgetData" )}/${phyloInstance.id}',
        downloadUrl: '${createLink(controller: "phylo", action:"download" )}/${phyloInstance.id}',
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
                this.loadingOn( widget );
                params.speciesList = data
                params.wid =  widget.getId();
                widget.addParams( params );
                $.ajax({
                    // this is the index. this is need to call the correct widget display
                    widget: widget,
                    url: this.widgetUrl,
                    type:'POST',
                    data:params,
                    success: this.display
                });
            }
        },
        display:function( data ){
            this.widget.display( data );
            widgets.loadingOff( this.widget )
        },
        beforeSave: function(){
            var widget;
            for( var wid in this.widgetsList ) {
                widget = this.widgetsList[ wid ];
                widget.beforeSave && widget.beforeSave();
            }
        },
        loadingOn:function( widget ){
            $( '#'+widget.showContentId ).html( '' );
            $( '#'+widget.showId ).addClass( 'loading' );
        },
        loadingOff:function( widget ){
            $( "#" + widget.showId ).removeClass( 'loading' );
        },
        download:function( i ){
            var widget = this.widgetsList[i];
            var data;
            if( widget ){
                data = widget.getData();
                data && $.ajax({
                    url: this.downloadUrl,
                    type:'POST',
                    data:{
                        json:JSON.stringify( data )
                    },
                    success:function( csv ){
                        var uri = 'data:application/csv;charset=UTF-8,' + encodeURIComponent( csv );
                        $("<a style='display: none' href='"+uri+"' download='data.csv'>download data</a>").appendTo('body')[0].click()
                    }
                });
            }
        }
    };
    google.load("visualization", "1", {packages:["corechart"]});
</script>
<g:render template="environmental/script"/>
<g:render template="contextual/script"/>
<g:render template="pd/script"/>