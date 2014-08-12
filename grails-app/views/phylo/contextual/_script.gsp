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
        showEl: 'envID',
        showContentEl: 'envID-content',
        autocompleteEl:'widgetautocompleteID',
        displayNameEl:'widgets[ID].displayname',
        configNameEl:'widgets[ID].config',
        initVars:function( ){
            var id = this.id;
            this.displayNameId = this.displayNameEl.replace(/ID/g, id)
            this.autocompleteId = this.autocompleteEl.replace(/ID/g, id)
            this.configNameId = this.configNameEl.replace(/ID/g, id)
            this.showId = this.showEl.replace(/ID/g, id)
            this.showContentId = this.showContentEl.replace(/ID/g, id)
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
        getId:function (){
            return this.id;
        },
        loadLayers:function(callback, args){
            var that = this;
            $.ajax(this.layerUrl,{
                method:'GET',
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