<%--
 Created by Temi Varghese
 --%>
<script type="text/javascript">
    var widgets= {
        widgetsList : [],
        widgetUrl: '${createLink(controller: "phylo", action:"getWidgetData" )}/${phyloInstance.id}',
        downloadUrl: '${createLink(controller: "phylo", action:"download" )}/${phyloInstance.id}',
        layerHtmlUrl:"${createLink(controller: 'ala', action: 'layerSelectionDialog')}",
        counter: 0,
        data:{
            regions:<phy:reg ab="11">null</phy:reg>
        },
        envLayerUrl:"${createLink(controller: 'ala', action: 'getEnvLayers')}",
        clLayerUrl:"${createLink(controller: 'ala', action: 'getClLayers')}",
        envLayers:[],
        clLayers:[],
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
                    success: this.display,
                    error: this.failure
                });
            }
        },
        display:function( data ){
            this.widget.display( data );
            widgets.loadingOff( this.widget )
        },
        failure:function( data ){
            widgets.loadingOff( this.widget );
            $('#' + this.widget.showContentId ).html( "<span style='color:red'> Failed to execute query.</span>" );
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
        },
        loadLayers:function( url, array ){
            var that = this;
            $.ajax(url,{
                type:'GET',
                success:function( data ){
                    for(var i in data ){
                        array.push( data[i] );
                    }
                }
            })
//            .done( function(){
//                callback && callback.apply( utils, args)
//            })
        },
        initLayers:function(){
          this.loadLayers(this.envLayerUrl, this.envLayers );
          this.loadLayers(this.clLayerUrl, this.clLayers );
        },
        select:function(index, data){
            var widget = this.widgetsList[index];
            if( !widget ){
                return;
            }
            $(document.getElementById( widget.configNameId ) ).attr( 'value', data.value );
            $( document.getElementById(  widget.displayNameId  ) ).attr( 'value', data.label );
        },
        showLayerDialog:function(e, data){
            e.preventDefault()
            var widget = e.data;
            var wtype = $(document.getElementById(widget.typeId)).val();
            var val;
            switch ( wtype ){
                case 'environmental':
                    val='${grailsApplication.config.layersMeta.env}';
                    break;
                case 'contextual':
                    val='${grailsApplication.config.layersMeta.cl}';
                    break;
            }
            var opts = {
                url: widgets.layerHtmlUrl+"?i="+widget.wid +"&type="+val,
                widget: widget,
                title: 'Select a layer',
                height:600
            }
            utils.modalDialog( opts )
        }
    };
    google.load("visualization", "1", {packages:["corechart"]});
    widgets.initLayers()
</script>
<g:render template="environmental/script"/>
<g:render template="contextual/script"/>
<g:render template="pd/script"/>