%{--<g:render template="pd/create"/>--}%
<g:render template="pd/form"/>
%{--<g:render template="pd/edit"/>--}%
%{--<g:render template="pd/show"/>--}%
<script type="text/javascript">
    widgets.PD = function( id, initData ){
        this.id = id || widgets.counter;
        this.initVars( initData )
        widgets.add( this )
    }
    widgets.PD.prototype = {
        createTmpl: $("#_tmplPd").html(),
//        createTmpl: $("#_tmplPdCreate").html(),
//        showTmpl: $("#_tmplContextualShow").html(),
//        editTmpl: $("#_tmplContextualEdit").html(),
        showEl: 'envID',
        widgetEl: 'widgets[ID]',
        showContentEl: 'envID-content',
        displayNameEl:'.displayname',
        configNameEl:'.config',
        dataEL: '.data',
        pdRegionsEl: 'pdRegionsID',
        typeEl: '.type',
        titleEl: '.title',
        domainData:{
            data:"",
            title:"Phylogenetic Diversity",
            displayname:"Phylogenetic Diversity",
            region:"",
            config:"pd",
            type:"pd"
        },
        // store data here so that it can be later downloaded.
        displayData: null,
        // lists the regions selected
        regions: null,
        initVars:function( initData ){
            var id = this.id
            var that = this;

            this.widgetId =  this.widgetEl.replace(/ID/g, id)
            this.displayNameId =  this.widgetId + this.displayNameEl
            this.configNameId =   this.widgetId + this.configNameEl
            this.dataId =  this.widgetId + this.dataEL
            this.typeId =  this.widgetId + this.typeEl;
            this.titleId =  this.widgetId + this.titleEl;


            this.showId =   this.showEl.replace(/ID/g, id)
            this.showContentId =  this.showContentEl.replace(/ID/g, id)
            this.pdRegionsId =  this.pdRegionsEl.replace(/ID/g, id)

            this.regions = ko.observableArray([])
            this.regionSelect = ko.observableArray ( widgets.data.regions );
            this.regionSelected = ko.observable();

            // adds all values from initData into PD object
            if( initData ){
                $.extend(this, initData );
                var temp = JSON.parse( this.data )
                temp.forEach( function(it ) {
                    // extract the stored values and add it so that the select buttons will appear
                    that.addRegionSelect(  it.code )
                })
            } else {
                $.extend( this, this.domainData )
            }
//            this.displayNameId = ko.observable( this.displayNameEl.replace(/ID/g, id) )
//            this.configNameId = ko.observable(  this.configNameEl.replace(/ID/g, id) )
//            this.showId = ko.observable(  this.showEl.replace(/ID/g, id) )
//            this.showContentId = ko.observable( this.showContentEl.replace(/ID/g, id) )
//            this.widgetId = ko.observable( this.widgetEl.replace(/ID/g, id))
//            this.dataId = ko.observable( this.dataEL.replace(/ID/g, id))
//            this.pdRegionsId = ko.observable( this.pdRegionsEl.replace(/ID/g, id))
//            this.typeId = ko.observable( this.typeEl.replace( /ID/g, id));
//            this.titleId = ko.observable( this.titleEl.replace( /ID/g, id));
//            this.regions = [];
//            this.regionViewModelInstance = new this.RegionsViewModel( this );
        },
        chartOptions: {
          title:'Phylogenetic Diversity',
          hAxis:{
              title:'Geographic Regions',
              titleTextStyle:{
                  color:"blue"
              }
          }
        },
        create:function( widgetAddAreaId ){
            var html = $(this.createTmpl).appendTo( "#"+widgetAddAreaId )
            //$( "#"+widgetAddAreaId ).append( this.createTmpl.replace(/ID/g, this.id ) );
            // add the regions drop down on init
//            utils.addTemplate(this.pdRegionsId, '_tmpRegions' )
            ko.applyBindings( this, html[0] )
        },

        edit:function(){
//            var region
//            var data = $( document.getElementById( this.dataId ) ).attr( 'value' );
//            data = JSON.parse( data )
//            for( var i in data ){
//                region = data[i];
//                utils.addTemplate( 'pdRegions'+this.getId(), '_tmpRegions' )
//            }
//            this.addRegions( data )
        },
        show:function(){

        },
        display: function (  data ){
            this.displayData = data;
            var result = [['region','pd']], pd;
            for( var i in data ){
                pd = data[i]
                result.push( [pd['region'], pd['pd'] ] );
            }
            result.push( ['maximum PD', pd['maxPd']] );

            console.log( result )
            result = google.visualization.arrayToDataTable( result );
            var chart = new google.visualization.ColumnChart( document.getElementById( this.showContentId ));
            chart.draw(result, this.chartOptions );
//                $("#"+this.showContentId ).html( "<p>PD: "+data[0].pd + "</p><p>Max PD: " + data[0].maxPd +"</p");
        },
        addParams:function( params ){

        },
        createData: function(  ){
            var result = [];
//            $( document.getElementById( this.widgetId ) ).find('select').each( function( index, it ){
////                console.log( it.val() );
//                result.push( $(it).val() );
//            });
//            this.regions().forEach( function( it ){
//                result.push( it.selected )
//            })
            return ko.toJS(  this.regions )
//            return result
        },
        beforeSave: function( params ){
            $( document.getElementById( this.dataId ) ).attr( 'value', JSON.stringify ( this.createData ( ) ) );
        },
        getId:function (){
            return this.id;
        },
        download:function(){
            utils.download( this.data )
        },
        getData: function(){
            return this.displayData;
        },
        RegionModel:function( data ){
            this.region = data.region;
            this.code = data.code;
        },
        RegionsViewModel: function( pd ){
            pd.regions = ko.observableArray( [] )
        },
        addRegionSelect:function( region ){
            console.log( region )
            var obj = {}
            if( region ){
              region = ko.observable( region );
            } else {
                region = ko.observable( );
            }
            // cannot let koArray to have an array of observables. I had to use code property for the logic to function correctly
            obj.code = region;
            this.regions.push( obj );
        }
    }
</script>