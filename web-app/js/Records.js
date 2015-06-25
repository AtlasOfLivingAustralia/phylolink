/**
 * Created by Temi Varghese on 15/06/15.
 */

var Records = function (c) {
    var config = $.extend({
            templateUrl: undefined,
            template: undefined,
            uploadUrl: undefined,
            formId: 'csvFormRecords',
            uploadCalloutId: 'uploadRecords',
            uploadCalloutHeaderId: 'uploadRecordsTitle',
            toggleId: 'minimizeUploadRecords',
            uploadMessageId: 'uploadMessage',
            messageDelay: 5000,
            sampleFile: undefined,
            dataresrouceInfoUrl:undefined,
            drListId:'recordsmain',
            dataresourceListUrl:undefined,
            pj: undefined,
            map: undefined,
            selectResourceOnInit: true,
            initResourceId: -1
        }, c),
        records = new Emitter(this),
        pj = config.pj,
        map = config.map;

    var Events = [
    /**
     * fired when data resource list is loaded successfully.
     */
        'resourcelistloaded'
    ]

    // map instance uses this for colorby and other operations. passing this instance as soon as possible.
    map.setRecords(this);

    var DataresourceModel = function (opt) {
        this.id = ko.observable(opt.id || null);
        this.title = ko.observable(opt.title||'');
        this.scName = ko.observable(opt.scName||'');
        this.drid = ko.observable(opt.drid||undefined);
        this.instanceUrl = ko.observable(opt.instanceUrl||'');
        this.layerUrl = ko.observable(opt.layerUrl||'');
        this.type = ko.observable(opt.type||'');
    };

    var FormModel = function (opt) {
        this.title = ko.observable(opt.title || '');
        this.headers = ko.observableArray(opt.headers || []);
        this.selectedValue = ko.observable(opt.selectedValue || '');
        this.progress = ko.observable(opt.progress || undefined);
        this.error = ko.observableArray(opt.error || '');
        this.message = ko.observable(opt.message || '');
        this.indexingProgress = ko.observable(opt.indexingProgress || undefined);
        this.indexingMessage = ko.observable(opt.indexingMessage || '');
        this.indexingClass = ko.observable(opt.indexingClass || '');
        this.sampleFile = ko.observable(opt.sampleFile||'')
        this.formDisabled = ko.computed(function(){
            if((this.indexingProgress() == undefined) && (this.progress() == undefined)){
                return false;
            } else if(this.progress() == undefined){
                if(this.indexingProgress() == 100 ){
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }, this);
    }

    var FormViewModel = function (m) {
        $.extend(this, m);
        this.onToggleForm = function () {
            $('#' + config.toggleId).toggle('hide');
        }

        this.uploadFile = function () {
            var node = document.getElementById(config.formId),
                form = new FormData(node),
                xhr,
                model = this;
            model.message('');
            model.progress(0);
            xhr = $.ajax({
                url: config.uploadUrl,
                method: 'POST',
                data: form,
                processData: false,
                contentType: false,
                success: function (result) {
                    model.progress(100);
                    model.message('File uploaded successfully');
                    model.error(false);
                    config.uid = result.uid;
                    setTimeout(function () {
                        model.progress(undefined)
                    }, config.messageDelay)
                    records.checkIndexingStatus();
                },
                error: function (xhr) {
                    var data = xhr.responseJSON;
                    model.message(data.error + ' ' + data.message);
                    model.error(true);
                    setTimeout(function () {
                        model.progress(undefined)
                    }, config.messageDelay)
                },
                xhr: function () {
                    var xhr = $.ajaxSettings.xhr();
                    xhr.upload.onprogress = function (e) {
                        if (e.lengthComputable) {
                            var percentage = e.loaded / e.total * 100 * .8;
                            model.progress(percentage);
                        }
                    };
                    return xhr;
                }
            })
        }

        this.onNewFile = function (model, evt) {
            var file = evt.target.files[0],
                fr = new FileReader(),
                that = this;
            fr.onload = function () {
                var headers = records.getHeaders(fr.result);
                that.addHeaders(headers);
            }

            fr.readAsText(file);
        }

        this.addHeaders = function (headers) {
            this.headers.removeAll();
            for (var i = 0; i < headers.length; i++) {
                this.headers.push(headers[i]);
            }
        }


        this.resetForm = function () {
            this.title('');
            this.headers([]);
            this.selectedValue('');
            this.progress(undefined);
            this.error('');
            this.message('');
            this.indexingProgress(undefined);
            this.indexingMessage('');
            this.indexingClass('');
            $('#' + config.formId + ' input[type="file"]').val(null);
        }

    }

    var DataresourceViewModel = function () {
        var self = this;
        this.lists = ko.observableArray([]);
        this.selectedValue = ko.observable();

        /**
         * add a new data resource
         * @param src
         */
        this.addDataresource = function (src, select) {
            var dr = new DataresourceModel(src)
            this.lists.push(dr);
            if(select){
                this.selectedValue(dr);
                records.updateMap();
            }
        }

        this.addDataresources = function (drs) {
            for(var i = 0; i < drs.length; i++){
                this.addDataresource(drs[i]);
            }
        }

        this.drChanged = function(){
            records.updateMap();
        }

        this.findResourceById = function(id){
            return ko.utils.arrayFirst(this.lists(), function(item){
                return id === item.id();
            });
        }
    };

    var uploadData = new FormModel({
        title: '',
        headers: [],
        selectedValue: '',
        message: '',
        progress: undefined,
        error: false,
        sampleFile: config.sampleFile
    });

    var viewModel = new FormViewModel(uploadData);
    var dataresourceViewModel = new DataresourceViewModel();

    this.showForm = function () {
        var that = this;
        if (!config.template) {
            $.ajax({
                url: config.templateUrl,
                dataType: 'html',
                success: function (tmp) {
                    config.template = tmp;
                    that.showForm();
                }
            });
        } else {
            $('#' + config.id).html(config.template);
            that.init();
        }
    }

    this.init = function () {
        var formObj = document.getElementById(config.uploadCalloutId),
            listObj = document.getElementById(config.drListId);
        ko.applyBindings(viewModel, formObj);
        ko.applyBindings(dataresourceViewModel, listObj);
        this.on('resourcelistloaded',function(flag){
            pj.setSaveQueryFlag(flag);
        });
        records.getAllDataresources( );
    }

    /**
     * pass a csv file content and get the headers.
     * @param text
     * @returns {Array}
     */
    this.getHeaders = function (text) {
        var lines = text.split(/[\r\n]/g), firstLine = lines[0];
        var headers = [], columns = firstLine.split(',');
        if (columns) {
            for (i = 0; i < columns.length; i++) {
                headers.push(
                    columns[i]
                )
            }
        }
        return headers;
    }

    /**
     * check the status of uploaded file and display the result.
     */
    this.checkIndexingStatus = function () {
        $.ajax({
            url: config.indexingStatusUrl,
            data: { uid: config.uid},
            success: function (data) {
                viewModel.indexingMessage(data.description);
                viewModel.indexingProgress(data.percentage);
                switch (data.status) {
                    case 'LOADING':
                    case 'SAMPLING':
                    case 'PROCESSING':
                    case 'INDEXING':
                        viewModel.indexingClass('alert-info');
                        break;
                    case 'COMPLETE':
                        viewModel.indexingClass('alert-success');
                        setTimeout(function(){
                            viewModel.indexingProgress(undefined);
                            viewModel.resetForm();
                        },config.messageDelay);
                        records.findDataresourceInfo(config.uid, records.addDataresource);
                        return;
                    case 'FAILED':
                    default :
                        viewModel.indexingClass('alert-error');
                        setTimeout(function(){
                            viewModel.indexingProgress(undefined);
                        },config.messageDelay);
                        return;
                }
                setTimeout(records.checkIndexingStatus, 1000);
            },
            error: function () {
                viewModel.indexingMessage('Failed to check status.');
                viewModel.indexingClass('alert-error');
                viewModel.resetForm();
            }
        });
    }

    /**
     * adds a data resource to the list of data resources. exposes this functionality to outside world.
     */
    this.addDataresource = function(dr, select){
        select= select || false;
        dataresourceViewModel.addDataresource(dr, select);
    }

    /**
     * get the config parameters for recently uploaded
     */
    this.findDataresourceInfo = function(druid, callback){
        var that = this;
        $.ajax({
            url: config.dataresrouceInfoUrl,
            data:{
                druid: druid
            },
            success: function(data){
                if(callback){
                    callback.apply(that, [data, true]);
                }
            }
        })
    }

    /**
     * get list of data resources from provided url.
     */
    this.getAllDataresources = function(){
        var that = this;
        $.ajax({
            url: config.dataresourceListUrl,
            success:function(data){
                records.emit('resourcelistloaded');
                dataresourceViewModel.addDataresources(data);
                records.selectADataresource();
            }
        })
    }

    /**
     * select a data resource on init
     * todo: change this
     */
    this.selectADataresource = function(){
        var flag = config.selectResourceOnInit,
            id = config.initResourceId,
            dr,
            selected = dataresourceViewModel.selectedValue();
        if(!(selected && selected.type())){
            if(flag){
                dr = dataresourceViewModel.findResourceById(id);
                dataresourceViewModel.selectedValue(dr);
                records.updateMap();
            }
        }
    }

    /**
     * get the properties of selected data resource
     */
    this.getDataresource = function(){
        var dr = dataresourceViewModel.selectedValue();
        return ko.toJS(dr);
    }

    /**
     * called when a new data resource is selected
     */
    this.updateMap = function(){
        var sel = dataresourceViewModel.selectedValue();
        var layer = sel.layerUrl(),
            instanceUrl = sel.instanceUrl();
        pj.clearQid();
        pj.setSaveQueryParams(sel.type(), instanceUrl, sel.drid());
        map.setDataSource(sel.type());
        // saving query to server is only done when this flag is set. otherwise, query parameters are sent on
        // all get request.
        if(!pj.getSaveQueryFlag()){
            pj.setSaveQueryFlag(true)
        }
        map.setLayerUrl(layer);
        pj.clickSelectedNode();
    }

    /**
     * checks if data resource has been loaded.
     * @returns {boolean}
     */
    this.isDRLoaded = function(){
        var drProp = records && records.getDataresource();
        if(!drProp){
            return false;
        }
        return true
    }

    this.showForm();
}