<g:render template="pd/form"/>
<script type="text/javascript">
    widgets.PD = function( id, initData ){
        this.id = id || widgets.counter;
        this.initVars( initData )
        widgets.add( this )
    }
    widgets.PD.prototype = {
        createTmpl: $("#_tmplPd").html(),
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
            // add the regions drop down on init
            ko.applyBindings( this, html[0] )
        },

        edit:function(){

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
        },
        addParams:function( params ){

        },
        createData: function(  ){
            var result = [];
            return ko.toJS(  this.regions )
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