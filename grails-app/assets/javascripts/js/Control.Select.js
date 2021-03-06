/**
 * creates a select with a label. check options on configuration.
 * author: temi
 */

L.Control.Select = L.Control.extend({

    options: {
        pj: null,
        position: 'bottomleft',
        title:'Colour by selected variable',
        text:'Colour by: ',
        initialValue : [],
        url: undefined,
        dataType :'jsonp',
        style:{
            height:'30px',
            padding: '5px'
        },
        sizeSliderOptions: {
            position: 'topright',
            title:'click me',
            text:'slider: ',
            style: { padding: '2px'},
            sliderLength:'75px',
            onChange: null,
            min: 0.1,
            max: 1.0,
            step: 0.1,
            value: 0.8,
            tooltip: 'hide'
        },
        onOutlineClick: null,
        onSizeChange: null,
        /**
         * callback when select is clicked.
         */
        onClick:null,
        defaultValue: null,
        currentValue: null
    },
    /**
     * checkbox dom element
     */
    _outlineCheckbox:null,

    /**
     * select dom element
     */
    _select:null,
    _html:'<table>\
                <tr>\
                    <td><label style="display: inline-block" data-bind="text:text"></label>\
                        <div style="display: inline-block">\
                            <select data-bind="foreach: facetsGrouped, value:selectedValue">\
                                <optgroup data-bind="attr: {label: name}, foreach: children">\
                                    <option data-bind="option: $data, text:displayName, value: $data"></option>\
                                </optgroup>\
                            </select>\
                        </div>\
                    </td>\
                    <td style="padding-right:15px; padding-left:15px; cursor: pointer;">\
                        <label style="display: inline-block; cursor: pointer;" data-bind="click: downloadRecords">\
                            <i class="glyphicon glyphicon-download"></i>&nbsp;Download&nbsp;|&nbsp;<i class="glyphicon glyphicon-globe"></i>&nbsp;Spatial portal\
                        </label>\
                    </td>\                               \
                    <td>\
                        <label style="display: inline-block">Size:</label> \
                        <div id="sizeSlider" class="sizeSlider" style="display:inline-block;"></div>\
                    </td>\
                </tr>\
            </table>',

    ViewModel: function(options){
        this.facetsGrouped = ko.observableArray();
        this.text = ko.observable();
        this.selectedValue = ko.observable();
        this.selectedOutlineValue = ko.observable(false);

        /**
         * Disassociate a dataset from the system.
         * @param item
         */
        this.downloadRecords = function(item){

            $('#downloadAndLinks').modal('show');

            var downloadDataLink = "https://biocache.ala.org.au/download?searchParams=" + encodeURI("q=" + pj.getQid(true));
            var viewInSpatialPortal = "https://spatial.ala.org.au/?q=" + encodeURI(pj.getQid(true));
            var viewRecords = "https://biocache.ala.org.au/occurrence/search?q=" + encodeURI(pj.getQid(true));
            $('#downloadDataLink').attr('href', downloadDataLink);
            $('#viewInSpatialPortal').attr('href', viewInSpatialPortal);
            $('#viewRecords').attr('href', viewRecords);
        };

        /**
         * Disassociate a dataset from the system.
         * @param item
         */
        this.changeOutline = function(value){
            this.options.onOutlineClick(value);
        };
    },

    Model: function(data){
        this.displayName = ko.observable(data.displayName);
        this.name = ko.observable(data.name);
        this.group = ko.observable(data.group);
        this.type = ko.observable(data.type || 'facets');
    },

    viewModel: undefined,

    initialize: function ( options) {
        L.setOptions(this, options);
        new Emitter(this);
    },

    onAdd: function (map) {
        var container = L.DomUtil.create('div', 'leaflet-control-select leaflet-control-layers leaflet-bar');
        this._createColorBySelect(container);
        this._createSizeSlider(this.options.sizeSliderOptions.text, this.options.sizeSliderOptions.title, '', container);
        return container;
    },
    
    onClick: function(value){
        this.options.onClick && this.options.onClick.apply(this,arguments);
    },

    onRemove: function (map) {
    },

    _createSizeSlider: function (html, title, className, container) {
        var that = this;

        var sizeSliderCont = container.getElementsByClassName("sizeSlider")[0];
        var cont =  L.DomUtil.create('div','', sizeSliderCont);
        for(var i in this.options.sizeSliderOptions.style){
            cont.style[i] = this.options.sizeSliderOptions.style[i];
        }

        var div = L.DomUtil.create('div', '', cont);
        div.style['width'] = this.options.sizeSliderOptions.sliderLength;

        $(div).slider(this.options.sizeSliderOptions).on('slideStop', function(ev){
            var value;
            if(that.integer){
                value = parseInt(ev.value);
            } else {
                value = parseFloat(ev.value).toFixed(1);
            }
            that.onSizeChange(value);
        });

        //set integer flag
        var opt = this.options.sizeSliderOptions;
        if(this.isInt(opt.min)&& this.isInt(opt.max) && this.isInt(opt.step)){
            this.integer = true;
        } else {
            this.integer = false;
        }
        this._slider = $(div);

        L.DomEvent.on(div, 'mousedown dblclick', L.DomEvent.stopPropagation)
    },

    onOutlineClick : function(){
        // alert('value change: ' + value);
        this.options.onOutlineClick();
    },

    onSizeChange: function(value){
        // alert('value change: ' + value);
        this.options.onSizeChange(value);
    },

    isInt: function(a){
        if((parseFloat(a) === parseInt(a)) && !isNaN(a)){
            return true;
        } else {
            return false;
        }
    },

    _createColorBySelect: function (container) {

        var that = this,
            colorbyInput = this;
        this.el = container;
        for(var i in this.options.style){
            container.style[i] = this.options.style[i];
        }
        container.innerHTML = this._html;

        this.viewModel = new this.ViewModel();
        if (this.options.url){
            this.updateUrl();
        } else if(this.options.initialValue){
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
            that.emit('change', that.getColorBySelectValue());
        });
    },

    /**
     * Updates the colour by options with the facets from the data resources
     * @param data
     */
    updateData : function(data){
        var defval = null,
            currentVal = null;

        this.viewModel.facetsGrouped.removeAll();

        var currentGroup = null
        for (var i in data){

            var modelValue = new this.Model(data[i]);

            if (currentGroup == null || data[i].group != currentGroup().name()){
                if(currentGroup != null){
                    this.viewModel.facetsGrouped.push(currentGroup)
                }

                //create new group
                currentGroup = ko.observable({
                    name: ko.observable(data[i].group),
                    children: ko.observableArray()
                })
                currentGroup().children().push(modelValue)
            } else  {
                currentGroup().children().push(modelValue)
            }

            if (data[i].name === this.options.currentValue){
                currentVal = modelValue
            } else if (data[i].name === this.options.defaultValue) {
                defval = modelValue;
            }
        }

        if(currentGroup != null) {
            this.viewModel.facetsGrouped.push(currentGroup)
        }

        defval = currentVal || defval;

        if (defval != this.viewModel.selectedValue()) {
            this.viewModel.selectedValue(defval);
            this.emit('change');
        }
    },

    /**
     * Updates the data source URL.
     * @param url
     */
    updateUrl : function(url){
        var that = this;
        url = url || this.options.url;
        this.viewModel.facetsGrouped.removeAll();
        $.ajax({
            url: url,
            dataType: this.options.dataType,
            success: function(data){
                that.updateData(data);
            }
        });
    },

    downloadRecords :function(){
        alert('Download !!!!');
    },

    getColorBySelectValue : function(){
       return $(this.el).find('select').val();
    },

    setColorBySelectValue : function(val){
        $(this.el).find('select').val(val);
        this.emit('change');
    },

    getOutlineValue : function(val){
        return $(this.el).find('#outlineChbox').is(":checked");
    },

    getSelection : function(){
      return this.viewModel.selectedValue();
    },

    getSelectState : function(){
        return ko.toJS(this.getSelection());
    }
});

L.control.select = function (options) {
    return new L.Control.Select(options);
};