<g:render template="contextual/form"/>
%{--<g:render template="contextual/edit"/>--}%
%{--<g:render template="contextual/show"/>--}%
<script>
    widgets.Contextual = function( id , initData){
        this.wid = id || widgets.counter;
        this.displayData = null;
        this.initVars( initData )
        widgets.add( this )
    }
    widgets.Contextual.prototype = {
        createTmpl: $("#_tmplContextual").html(),
//        showTmpl: $("#_tmplContextualShow").html(),
//        editTmpl: $("#_tmplContextualEdit").html()
        clLayers:[],
        layerUrl:"${createLink(controller: 'ala', action: 'getClLayers')}",
        widgetEl: 'widgets[ID]',
        showEl: 'envID',
        showContentEl: 'envID-content',
        autocompleteEl:'widgetautocompleteID',
        displayNameEl:'.displayname',
        configNameEl:'.config',
        dataEL: '.data',
        typeEl: '.type',
        titleEl: '.title',
        domainData:{
            data:"",
            title:"Contextual Layer Histogram",
            displayname:"",
            config:"",
            type:"contextual"
        },
        // store data here so that it can be later downloaded.
        displayData: null,
        initVars:function( initData ){
            var id = this.wid;
            this.widgetId = this.widgetEl.replace(/ID/, id );
            this.displayNameId =  this.widgetId + this.displayNameEl
            this.configNameId =   this.widgetId + this.configNameEl
            this.dataId =  this.widgetId + this.dataEL
            this.typeId =  this.widgetId + this.typeEl;
            this.titleId =  this.widgetId + this.titleEl;

            this.autocompleteId = this.autocompleteEl.replace(/ID/g, id)
            this.showId = this.showEl.replace(/ID/g, id)
            this.showContentId = this.showContentEl.replace(/ID/g, id)

//            this.dataId = this.dataEl.replace(/ID/g, id)
//            this.clLayers.length == 0 ? this.loadLayers(): null;

            if( initData ){
                $.extend( this, initData)
            } else {
                $.extend( this, this.domainData)
            }

        },
        create:function(  widgetAddAreaId ){
            var html = $( this.createTmpl ).appendTo( "#"+widgetAddAreaId );
            ko.applyBindings( this, html[0]);
            utils.autocomplete( this.autocompleteId, widgets.clLayers, this.displayNameId, this.configNameId);
            this.addEventHandlers();
        },
        edit:function(){

        },
        show:function(){

        },
        display: function (  data  ){
//            var data = google.visualization.arrayToDataTable( result.data );
//            var options = result.options;
//            var chart = new google.visualization.ColumnChart( document.getElementById( this.showContentId ));
//            chart.draw(data, options);

            // aggregate returned values.
            var agg = {}, row;
            this.displayData = data;
            var result = [['Contextual value','Count']], pd;
            for( var i in data ){
                row = data[i]
                agg[ row['variable'] ] = ( agg[ row['variable'] ] + row['count'] ) || 0
            }
            for( i in agg){
                result.push([i, agg[i]])
            }

//            console.log( result )
            result = google.visualization.arrayToDataTable( result );
            var chart = new google.visualization.ColumnChart( document.getElementById( this.showContentId ));
            chart.draw(result, this.chartOptions );
        },
        addParams:function( params ){

        },
        createData: function(  ){
            var result = [];
            $( document.getElementById( this.widgetId ) ).find('select').each( function( index, it ){
//                console.log( it.getValue() );
                result.push( $(it).val() );
            });
            return result
        },
        beforeSave: function( params ){
            $( document.getElementById( this.dataId )).attr( 'value', JSON.stringify ( this.createData ( ) ) );
        },
        getId:function (){
            return this.wid;
        },
        loadLayers:function(callback, args){
            var that = this;
            $.ajax(this.layerUrl,{
                type:'GET',
                success:function( data ){
                    for(var i in data ){
                        that.clLayers.push( data[i] );
                    }
                }
            })
//            .done( function(){
//                callback && callback.apply( utils, args)
//            })
        },
        addEventHandlers:function(){
            $(document.getElementById( this.widgetId )).find('button').click(  this, widgets.showLayerDialog);
        },
        getData:function(){
            return this.displayData;
        }
    }
</script>