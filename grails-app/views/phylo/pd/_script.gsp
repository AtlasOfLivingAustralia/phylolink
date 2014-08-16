<g:render template="pd/create"/>
%{--<g:render template="pd/edit"/>--}%
%{--<g:render template="pd/show"/>--}%
<script type="text/javascript">
    widgets.PD = function( id ){
        this.id = id || widgets.counter;
        this.initVars(  )
        widgets.add( this )
    }
    widgets.PD.prototype = {
        createTmpl: $("#_tmplPdCreate").html(),
//        showTmpl: $("#_tmplContextualShow").html(),
//        editTmpl: $("#_tmplContextualEdit").html(),
        showEl: 'envID',
        showContentEl: 'envID-content',
        displayNameEl:'widgets[ID].displayname',
        configNameEl:'widgets[ID].config',
        widgetEl: 'widget[ID]',
        dataEL: 'widgets[ID].data',
        pdRegionsEl: 'pdRegionsID',
        // store data here so that it can be later downloaded.
        displayData: null,
        initVars:function(  ){
            var id = this.id
            this.displayNameId = this.displayNameEl.replace(/ID/g, id)
            this.configNameId = this.configNameEl.replace(/ID/g, id)
            this.showId = this.showEl.replace(/ID/g, id)
            this.showContentId = this.showContentEl.replace(/ID/g, id)
            this.widgetId = this.widgetEl.replace(/ID/g, id)
            this.dataId = this.dataEL.replace(/ID/g, id)
            this.pdRegionsId = this.pdRegionsEl.replace(/ID/g, id)

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
            $( "#"+widgetAddAreaId ).append( this.createTmpl.replace(/ID/g, this.id ) );
            // add the regions drop down on init
            utils.addTemplate(this.pdRegionsId, '_tmpRegions' )
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
//                $("#"+this.showContentId ).html( "<p>PD: "+data[0].pd + "</p><p>Max PD: " + data[0].maxPd +"</p");
        },
        addParams:function( params ){

        },
        createData: function(  ){
            var result = [];
            $( document.getElementById( this.widgetId ) ).find('select').each( function( index, it ){
//                console.log( it.val() );
                result.push( $(it).val() );
            });
            return result
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
        }
    }
</script>