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
        initVars:function(  ){
            var id = this.id
            this.displayNameId = this.displayNameEl.replace(/ID/g, id)
            this.configNameId = this.configNameEl.replace(/ID/g, id)
            this.showId = this.showEl.replace(/ID/g, id)
            this.showContentId = this.showContentEl.replace(/ID/g, id)

        },
        create:function( widgetAddAreaId ){
            $( "#"+widgetAddAreaId ).append( this.createTmpl.replace(/ID/g, this.id ) );
        },

        edit:function(){

        },
        show:function(){

        },
        display: function (  data ){
                $("#"+this.showContentId ).html( "<p>PD: "+data[0].pd + "</p><p>Max PD: " + data[0].maxPd +"</p");
        },
        addParams:function( params ){

        },
        getId:function (){
            return this.id;
        }
    }
</script>