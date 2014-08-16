<g:render template="contextual/create"/>
%{--<g:render template="contextual/edit"/>--}%
%{--<g:render template="contextual/show"/>--}%
<script>
    widgets.Contextual = function( id ){
        this.id = id || widgets.counter;
        this.initVars( )
        widgets.add( this )
    }
    widgets.Contextual.prototype = {
        createTmpl: $("#_tmplContextualCreate").html(),
//        showTmpl: $("#_tmplContextualShow").html(),
//        editTmpl: $("#_tmplContextualEdit").html()
        clLayers:[],
        layerUrl:"${createLink(controller: 'ala', action: 'getClLayers')}",
        widgetEl: 'widget[ID]',
        showEl: 'envID',
        showContentEl: 'envID-content',
        autocompleteEl:'widgetautocompleteID',
        displayNameEl:'widgets[ID].displayname',
        configNameEl:'widgets[ID].config',
        dataEL: 'widgets[ID].data',
        initVars:function( ){
            var id = this.id;
            this.displayNameId = this.displayNameEl.replace(/ID/g, id)
            this.autocompleteId = this.autocompleteEl.replace(/ID/g, id)
            this.configNameId = this.configNameEl.replace(/ID/g, id)
            this.showId = this.showEl.replace(/ID/g, id)
            this.showContentId = this.showContentEl.replace(/ID/g, id)
            this.widgetId = this.widgetEl.replace(/ID/g, id)
            this.dataId = this.dataEl.replace(/ID/g, id)
            this.clLayers.length == 0 ? this.loadLayers(): null;

        },
        create:function(  widgetAddAreaId ){
            $( "#"+widgetAddAreaId ).append( this.createTmpl.replace(/ID/g, this.id ) );
            utils.autocomplete( this.autocompleteId, this.clLayers, this.displayNameId, this.configNameId);
        },
        edit:function(){

        },
        show:function(){

        },
        display: function (  result  ){
            var data = google.visualization.arrayToDataTable( result.data );
            var options = result.options;
            var chart = new google.visualization.ColumnChart( document.getElementById( this.showContentId ));
            chart.draw(data, options);
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
            return this.id;
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
            }).done( function(){
                callback && callback.apply( utils, args)
            })
        }
    }
</script>