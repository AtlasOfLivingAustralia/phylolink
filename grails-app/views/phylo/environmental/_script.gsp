%{--<g:render template="environmental/create"/>--}%
%{--<g:render template="environmental/edit"/>--}%
%{--<g:render template="environmental/show"/>--}%
<g:render template="environmental/form"/>
<script>
    widgets.Environmental = function( id, initData ){
        this.wid = id || widgets.counter;
        this.initVars( initData )
        widgets.add( this )
    }
    widgets.Environmental.prototype = {
        createTmpl: $("#_tmplEnvironmental").html(),
//        showTmpl: $("#_tmplEnvironmentalShow").html(),
//        editTmpl: $("#_tmplEnvironmentalEdit").html(),
        envLayers:[],
        layerUrl:"${createLink(controller: 'ala', action: 'getEnvLayers')}",
        showEl: 'envID',
        showContentEl: 'envID-content',
        autocompleteEl:'widgetautocompleteID',
        showContentEl: 'envID-content',
        displayNameEl:'.displayname',
        configNameEl:'.config',
        dataEL: '.data',
        typeEl: '.type',
        titleEl: '.title',
        widgetEl:'widgets[ID]',
        domainData:{
            data:"",
            title:"Environmental Layer Histogram",
            displayname:"",
            config:"",
            type:"environmental"
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
            this.envLayers.length == 0 ? this.loadLayers(): null;
            if( initData ){
                $.extend( this, initData)
            } else {
                $.extend( this, this.domainData)
            }
        },
        create:function(  widgetAddAreaId ){
            var html = $( this.createTmpl ).appendTo( "#"+widgetAddAreaId );
            ko.applyBindings( this, html[0])
//            utils.autocomplete( this.autocompleteId, widgets.envLayers, this.displayNameId, this.configNameId);
            this.addEventHandlers();
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
            return this.wid;
        },
        loadLayers:function(callback, args){
            var that = this;
            $.ajax(this.layerUrl,{
                type:'GET',
                success:function( data ){
                    for(var i in data ){
                        that.envLayers.push( data[i] );
                    }
                }
            })
//            .done( function(){
//                callback && callback.apply( utils, args)
//            })
        },
        addEventHandlers:function(){
            $(document.getElementById( this.widgetId )).find('button').click(  this, widgets.showLayerDialog);
        }
    }
</script>