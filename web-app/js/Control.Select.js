/**
 * creates a select with a label. check options on configuration.
 * author: temi
 */
L.Control.Select = L.Control.extend({
    options: {
        position: 'bottomleft',
        title:'click me',
        text:'Color by: ',
        /**
         * format: [{"name":"REGNO_s","displayName":"REGNO"}]
         */
        initialValue:[],
        url:undefined,
        dataType:'jsonp',
        style:{
            height:'30px',
            padding: '5px'
        },
        /**
         * callback when select is clicked.
         */
        onClick:null,
        defaultValue: null,
        currentValue: null
    },
    /**
     * select dom element
     */
    _select:null,
    _html:' <table style="height: 30px;"><tbody><tr><td><label style="display: inline-block" data-bind="text:text"></label>\
            <div style="display: inline-block">\
        <select data-bind="options:facets,optionsText:\'displayName\',optionsCaption:\'Choose...\',value:selectedValue"></select>' +
        '</div></td></tr></tbody></table>',
    ViewModel:function(){
        this.facets = ko.observableArray();
        this.text = ko.observable();
        this.selectedValue = ko.observable();
    },
    Model: function(data){
        this.displayName = ko.observable(data.displayName);
        this.name = ko.observable(data.name);
        this.type = ko.observable(data.type || 'facets');
    },
    viewModel: undefined,
    initialize: function ( options) {
        L.setOptions(this, options);
        new Emitter(this);
    },

    onAdd: function (map) {
        var checkName = 'leaflet-control-select leaflet-control-layers',
            container = L.DomUtil.create('div', checkName + ' leaflet-bar'),
            options = this.options;
        this._create(container);
        return container;
    },
    
    onClick: function(value){
        this.options.onClick && this.options.onClick.apply(this,arguments);
    },

    onRemove: function (map) {

    },

    _create: function (container) {
        var that = this,
            colorbyInput = this;
        this.el = container;
        for(var i in this.options.style){
            container.style[i] = this.options.style[i];
        }
        container.innerHTML = this._html;

        this.viewModel = new this.ViewModel();
        if(this.options.url){
            this.updateUrl();
        }else if(this.options.initialValue){
            this.updateData(this.options.initialValue)
        }

        this.viewModel.text(this.options.text);
        this.viewModel.selectedValue.subscribe(function(colorBy){
            if(colorBy){
                colorbyInput.options.currentValue = colorBy.name();
            }
        },'change');
        
        ko.applyBindings(this.viewModel,container);

        $(container).find('select').on('change',function(){
            that.emit('change', that.getValue());
        });
    },

    updateData:function(data){
        var defval = null,
            currentVal = null;
        this.viewModel.facets.removeAll();
        for(var i in data){
            var modelValue = new this.Model(data[i]);
            this.viewModel.facets.push(modelValue);
            if(data[i].name === this.options.currentValue){
                currentVal = modelValue
            }else if (data[i].name === this.options.defaultValue) {
                defval = modelValue;
            }
        }

        defval = currentVal || defval;

        if (defval != this.viewModel.selectedValue()) {
            this.viewModel.selectedValue(defval);
            this.emit('change');
        }
    },

    updateUrl:function(url){
        var that = this;
        url = url || this.options.url;
        this.viewModel.facets.removeAll();
        $.ajax({
            url:url,
            dataType:this.options.dataType,
            success:function(data){
                that.updateData(data);
            }
        });
    },

    getValue : function(){
       return $(this.el).find('select').val();
    },

    setValue:function(val){
        $(this.el).find('select').val(val);
        this.emit('change');
    },

    getSelection: function(){
      return this.viewModel.selectedValue();
    },

    getSelectState: function(){
        return ko.toJS(this.getSelection());
    }
});

L.control.select = function (options) {
    return new L.Control.Select(options);
};