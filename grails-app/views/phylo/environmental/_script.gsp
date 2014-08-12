<g:render template="environmental/create"/>
%{--<g:render template="environmental/edit"/>--}%
%{--<g:render template="environmental/show"/>--}%
<script>
    widgets.Environmental = function( id ){
        this.id = id || widgets.counter;
        this.initVars( )
        widgets.add( this )
    }
    widgets.Environmental.prototype = {
        createTmpl: $("#_tmplEnvironmentalCreate").html(),
//        showTmpl: $("#_tmplEnvironmentalShow").html(),
//        editTmpl: $("#_tmplEnvironmentalEdit").html(),
        envLayers:[],
        layerUrl:"${createLink(controller: 'ala', action: 'getEnvLayers')}",
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
            this.envLayers.length == 0 ? this.loadLayers(): null;

        },
        create:function(  widgetAddAreaId ){
            $( "#"+widgetAddAreaId ).append( this.createTmpl.replace(/ID/g, this.id ) );
            utils.autocomplete( this.autocompleteId, this.envLayers, this.displayNameId, this.configNameId);
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
                        that.envLayers.push( data[i] );
                    }
                }
            }).done( function(){
                callback && callback.apply( utils, args)
            })
        }
    }
</script>