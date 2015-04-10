/**
 * author: temi varghese
 */
var Character = function (options) {
    // emitter mixin. adding functions that support events.
    new Emitter(this);
    var $ = jQuery;
    var that = this;

    options = $.extend({
        type: 'GET',
        dataType: 'json',
        headerHeight: 0,
        googleChartsLoaded: false,
        delayedChartCall:[],
        chartWidth:400,
        chartHeight:200,
        // flag to show upload character interface
        edit: true,
        //flag to check if character has been loaded
        characterloaded: false,
        primaryClass: 'label label-primary',
        defaultClass: 'label label-default',
        /**
         * sync flag
         *
         */
        doSync: true,
        /**
         * address to sync to
         */
        syncUrl: 'http://localhost:8080/phylolink/phylo/saveHabitat',
        syncType: 'POST',
        syncData: {
            id: 4
        },
        /**
         * upload character params
         */
        upload: {
            url: 'http://dev.ala.org.au:8080/phylolink/ala/saveAsList',
            type: 'POST'
        },
        /**
         * url to get the list of character datasets
         */
        charactersList : {
            url: 'http://dev.ala.org.au:8080/phylolink/characters/list',
            type: 'GET',
            dataType: 'JSON'
        },
        spinner: {
            top: '50%',
            left: '50%',
            className: 'loader'
        },
        graph: {
            xAxis: 'character state',
            yAxis: 'number of species'
        },
        /**
         * popover when user first interacts
         */
        popOver: [{
            id:'#sourceChar',
            options:{
                placement: 'top',
                trigger: 'manual',
                html: true,
//                title : 'Choose character set',
                content: 'Select from a list of available character data. It could be a default list of characters , ' +
                    'or character data uploaded by you.'
            }
        },{
            id:'#main .btn',
            options:{
                placement: 'bottom',
                trigger: 'manual',
                html: false,
                content: 'Choose a character by clicking this button. This will colour tree and bring up a graphs.'
            }
        },{
            id: '#charLi',
            options:{
                placement: 'top',
                trigger: 'manual',
                html: true,
                content : '<div id="charPopOverClose" class="btn btn-primary">Okay, got it!</div> '
            }
        },{
            id:'#uploadCharacters h4',
            options:{
                placement:'top',
                trigger:'manual',
                html:true,
                content:'Upload your own character set.'
            }
        }]
    }, options);
    var spinner = new Spinner(options.spinner);
    var id = options.id;
    var inputId = id + 'autoComplete';
    var pj = options.pj;
    var template3 = '\
    <div id="charactermain">\
        <div class="btn btn-xs btn-primary top-buffer offset1" data-bind="click: addCharacter">Add Character to Tree</div>\
        <div class="container" data-bind="sortable: {data:characters, afterMove: $root.onMove}">\
            <div class="item top-buffer" title="You can drag or edit this item">\
                <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">\
                    <span class="glyphicon glyphicon-sort" aria-hidden="true" style="cursor: move"></span>\
                    <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>\
                    <span class="glyphicon glyphicon-remove" data-bind="click: $root.removeCharacter" \
                        style="cursor: pointer"></span>\
                </div>\
                <div data-bind="visibleAndSelect: $root.isCharacterSelected($data)">\
                    <input data-bind="value: name, event: { blur: $root.clearCharacter }" />\
                </div>\
            </div>\
        </div>\
        <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">\
            <div class="top-buffer panel panel-default">\
                <div class="panel-heading" data-bind="text: name"></div>\
                <div class="panel-body" >\
                    <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>\
                </div>\
            </div>\
        </div>\
    </div>\
    <div class="bs-callout bs-callout-info">\
        <h4>Note</h4><p>You can select characters using <i>Add Character</i>\
        button. Tree branch color is determined by the first character on the list.\
        To color the tree using a character either drag that character to the top of the list, or \
        edit the first character by clicking on that character.</p>\
    </div>';

    var template2 = '\
    <div >\
        <div class="bs-callout" id="uploadCharacters" style="position: relative">\
        <h4 style="cursor:pointer" id="uploadCharactersTitle">Upload your character data</h4>\
        <div id="minimizeUpload">\
        <form id="csvForm" class="form-horizontal" enctype="multipart/form-data">\
        <i>You need modern browser such as Google Chrome 40 or Safari 8</i>\
        <div class="control-group">\
        <label class="control-label">Choose a CSV file*:</label>\
        <div class="controls">\
        <input id="csvFile" type="file" name="file" value="Upload" accept=".csv" required/>\
        <label><a data-bind="attr{href:sampleCSV}" target="_blank">Download sample CSV file from here.</a></label    >\
        </div>\
        </div>\
    <div class="control-group">\
        <label class="control-label" for="inputPassword">Title*:</label>\
        <div class="controls">\
            <input type="text" id="title" data-bind="value: title" placeholder="My acacia characters" required>\
            </div>\
        </div>\
    <div class="control-group">\
        <label class="control-label" for="inputPassword">Column with scientific name*:</label>\
        <div class="controls">\
            <select data-bind="options:headers,optionsText:\'displayname\',value:selectedValue,optionsCaption:\'Choose..\'" required></select>\
            </div>\
        </div>\
          <div class="control-group">\
          <div class="controls">\
        <button id="uploadBtn" class="btn btn-small btn-primary">Upload</button>\
        </div>\
        </div>\
        </form>\
        <div class="alert" id="uploadMessage" data-bind="visible:message">\
        \
        <div data-bind="text:message"></div></div></div>\
        </div>\
    </div>\
    <div id="charactermain">\
        <div class="bs-callout" style="position: relative" id="pickFromList">\
        <h4>Or, pick a character dataset from the available list:</h4>\
        <form id="sourceToolbar" class="form-horizontal">\
        <div class="control-group">\
        <label class="control-label" for="">List of characters available:</label>\
        <div class="controls">\
            <select id="sourceChar" data-bind="options:lists,optionsText:\'title\',value:list,optionsCaption:\'Choose..\', event:{change:loadNewCharacters}" required></select>\
            </div>\
        </div>\
        </form>\
    </div>\
    <div class="btn btn-xs btn-primary top-buffer offset4" data-bind="click: addCharacter, visible:list(), attr:{disabled:listLoading()}">Add Character to Tree</div>\
    <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">\
            <div class="item top-buffer" title="You can drag or edit this item">\
                <div data-bind="visible: !$root.isCharacterSelected($data), attr:{class: $root.characterClass($data)}">\
                    <i class="icon-white icon-resize-vertical" aria-hidden="true" style="cursor: move"></i>\
                    <a style="color: #ffffff" href="#" data-bind="text: name, click: $root.selectedCharacter"></a>\
                    <i class="icon-white icon-remove" data-bind="click: $root.removeCharacter" \
                        style="cursor: pointer"></i>\
                </div>\
                <div data-bind="visibleAndSelect: $root.isCharacterSelected($data)">\
                    <input data-bind="value: name, event: { blur: $root.clearCharacter }" />\
                </div>\
            </div>\
        </div>\
        <div data-bind="sortable: {data:characters, afterMove: $root.onMove}">\
            <div class="top-buffer panel panel-default">\
                <div class="panel-heading" data-bind="text: name"></div>\
                <div class="panel-body" >\
                    <div data-bind="attr:{id: id}, addChart: !!$data.name()" style="width: 100%; height: 200px;"></div>\
                </div>\
            </div>\
        </div>\
    </div>\
    <div class="alert top-buffer">\
        <button type="button" class="close" data-dismiss="alert">&times;</button>\
        <h4>Note</h4><p>First, select a character dataset from the given list, or upload your character data. Then click on <i>Add Character to Tree</i>\
        button. Tree branch color is determined by the first character on the list.\
        To color the tree using a character either drag that character to the top of the list, or \
        edit the first character by clicking on that character.</p>\
    </div>';

    var template = options.bootstrap == 2? template2:template3;
    //check bootstrap version
    switch (options.bootstrap){
        case 2:
            options.primaryClass = 'label label-info';
            options.defaultClass = 'label';
            break;
        case 3:
            //  use default value
            break;
    }

    //adding template to html page
    $('#' + id).html(template);
    var input = $('#' + inputId);
    var characterList = [], charJson;

    // knockout code
    var Character = function (opt) {
        this.name = ko.observable(opt.name);
        this.id = ko.observable(opt.id);
    }

    var CharacterViewModel = function () {
        new Emitter(this);
        var self = this;
        /**
         * list all the events supported by this function
         * @type {Array}
         */
        self.events = [
        /**
         * params
         * @selected - a list of
         */
            'statechange',
            'moved',
            'edited',
            'newchar',
            'removed'
        ]
        /**
         * serial number of the next character
         * @type {number}
         */
//        var count = 1;
        self.newChar = false;
        self.characters = ko.observableArray([]);
        self.count = ko.observable(1);
        self.selectedCharacter = ko.observable();
        self.lists = ko.observableArray([]);
        self.list = ko.observable();
        self.edit = ko.observable(options.edit);
        self.listLoading = ko.observable(false);

        self.clearCharacter = function (data, event) {
            if (data === self.selectedCharacter()) {
                self.selectedCharacter(null);
            }

            if (data.name() == "") {
                self.characters.remove(data);
                self.emit('removed')
                self.emit('statechange', self.charlist());
            }
        };

        self.removeCharacter = function (data, event) {
            self.characters.remove(data);
            self.emit('removed');
            self.emit('statechange', self.charlist());
        };

        self.addCharacter = function (name, e) {
            if(e && !!$(e.target).attr('disabled')){
                return;
            }
            if(typeof name != 'string'){
                name = "";
            }

            var opt = {name: name, id: 'charChart-' + self.count()}
            self.count(self.count()+1);
            var character = new Character(opt);
            self.selectedCharacter(character);
            self.characters.push(character);
            self.newChar = true;
            return character;
        };

        self.removeAllCharacters = function(){
            self.characters.removeAll();
            self.selectedCharacter(null);
            self.emit('statechange');
        }

        self.addNamedCharacter = function (char) {
            var character = self.addCharacter(char);
            // to hide input tag and to bring label visible.
            self.clearCharacter(character,null)
            char && self.emit('statechange', self.charlist(),true);
        };

        self.isCharacterSelected = function (character) {
            return character === self.selectedCharacter();
        };

        self.onMove = function () {
            self.emit('moved');
            self.emit('statechange', self.charlist());
        };

        self.charlist = function () {
            var selected = [], i, char = self.characters();
            for (i = 0; i < char.length; i++) {
                selected.push(char[i].name())
            }
            return selected;
        }

        self.isPrimary = function (data) {
            var first = self.characters()[0];
            return data === first
        }

        self.characterClass = function (data) {
            if (self.isPrimary(data)) {
                return options.primaryClass;
            } else {
                return options.defaultClass;
            }
        }

        self.isNewChar = function () {
            return self.newChar;
        }

        self.changeName = function (name, char) {
            char.name(name);
            self.newChar && self.emit('newchar', char.id(), char.name());
            self.newChar = false;
            self.emit('statechange', self.charlist());
        }

        self.updateChart = function(char,list){
            var id = char.id(),
                name = char.name(),charJson,i;
            charJson = that.charJsonSubset(list);
            temp = that.getCharArray(name,charJson);
            if(temp == undefined || temp.length == 0){
//                for(i in charJson){
//                    temp.push([undefined,undefined])
//                }
//                data = that.chartDataTransform([0,0]);
                that.columnchart(id,[['',''],[0,0]]);
                return;
            }
            // check if values are string or numeric
            if (typeof temp[0][1] === 'number') {
                data = that.chartDataTransform(temp);
                that.histogram(id,data);
            } else {
                data = that.chartDataTransform(temp);
                that.columnchart(id, data)
            }
        }

        self.loadNewCharacters= function(){
            if(self.list()){
                self.characters.removeAll()
                that.loadCharacterFromUrl(self.list().url);
            }
        }

        self.addNewSource = function(list){
            self.lists.push(list);
            self.list(list)
            $("#sourceChar").trigger('change');
        }
    };

    ko.bindingHandlers.visibleAndSelect = {
        init: function (element, valueAccessor, innerFn, data, koObj) {
            console.log('init visible and select handler');
            var input  = $(element).find("input");
            input.autocomplete({
                source: characterList,
                minLength: 0,
                minChars: 0,
                select: function (event, ui) {
                    var self = koObj.$root;
                    self.changeName(ui.item.value,data)
                    input.blur();
                }
            }).on('focus', function (event) {
                var self = this;
                $(self).autocomplete("search", "");
                koObj.$root.newChar = true;
            });
            input.focus();
        },
        update: function (element, valueAccessor, innerFn, data, koObj) {
            console.log('update function');
            ko.bindingHandlers.visible.update(element, valueAccessor);
            if (valueAccessor()) {
                // focus on input tag once clicked to edit
                $(element).find("input").focus();
            } else {
                $(element).find("input").blur();
            }
        }
    };

    ko.bindingHandlers.addChart = {
        update: function (el, valueAccessor, innerFn, data, koObj) {
            if (valueAccessor()) {
                var id = data.id(), charName = data.name(), charJson,
                    node = pj.getSelection(),
                    list;
                if(node){
                    list = pj.getChildrenName(node);
                    charJson = that.charJsonSubset(list);
                }

                var temp = that.getCharArray(charName,charJson)
                if(temp == undefined || temp.length == 0){
                    return;
                }
                // check if values are string or numeric
                if (typeof temp[0][1] === 'number') {
                    data = that.chartDataTransform(temp);
                    that.histogram(id,data);
                } else {
                    data = that.chartDataTransform(temp);
                    that.columnchart(id, data);
                }
//                google.visualization.events.addListener(chart, 'onmouseover', that.chartHover);
            }
        }
    };

    var view = new CharacterViewModel();
    ko.applyBindings(view, document.getElementById('charactermain'));

    var UploadViewModel = function(){
        this.headers = ko.observableArray();
        this.selectedValue = ko.observable();
        this.title = ko.observable();
        this.message = ko.observable();
        this.alertId = '#uploadMessage';
        this.sampleCSV = options.sampleCSV;
    }

    var upload = new UploadViewModel();
    ko.applyBindings(upload, document.getElementById('uploadCharacters'));
    $('#minimizeUpload').hide()
    $('#uploadCharactersTitle').click(function(){
        $('#minimizeUpload').toggle('hide');
    })
    /**
     * transform data to be able to be displayed by chart. i.e. convert qualitative character to term frequency
     * to display as histogram.
     * @param temp
     * @returns {*[]}
     */
    this.chartDataTransform = function(temp){
        var data = [
            ['Species Name', 'Value']
        ],oneD=[];
        var type = typeof temp[0][1] === 'number'?'histogram':'columnchart';
        switch (type){
            case 'histogram':
                temp.forEach(function (it) {
                    data.push(it);
                });
                break;
            case 'columnchart':
                temp.forEach(function(it){
                    oneD.push(it[1]);
                });
                temp = that.frequencyCount(oneD);
                temp.forEach(function (it) {
                    data.push(it);
                });
                break;
        }
        return data;
    };

    /**
     * draws a histogram. currently using google charts
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.histogram = function(id, data){
        if(options.googleChartsLoaded){
            var chart = new google.visualization.Histogram(document.getElementById(id));
            var width = $('#'+options.id+' .panel-body:first').width() || options.chartWidth;
            var height = options.chartHeight;
            var opt = {
                width: width,
                height: height,
                title: 'characters',
                legend: { position: 'none' },
                vAxis: {
                    title: options.graph.yAxis
                },
                hAxis: {
                    title: options.graph.xAxis
                }
            };
            data = google.visualization.arrayToDataTable(data);
            chart.draw(data, opt);
        } else {
            // some times google chart is not ready when this function is called
            options.delayedChartCall.push([arguments.callee,this,arguments]);
        }
    }

    /**
     * draws a column chart. this is used when data are qualitative i.e. string. currently using google charts
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.columnchart = function(id, data){
        if(options.googleChartsLoaded){
            var chart = new google.visualization.ColumnChart(document.getElementById(id));
            var width = $('#'+options.id+' .panel-body:first').width() || options.chartWidth;
            var height = options.chartHeight;
            var opt = {
                width: width,
                height: height,
                title: 'characters',
                legend: { position: 'none' },
                vAxis: {
                    title: options.graph.yAxis
                },
                hAxis: {
                    title: options.graph.xAxis
                }
            };
            data = google.visualization.arrayToDataTable(data);
            chart.draw(data, opt);
        } else {
            // some times google chart is not ready when this function is called
            options.delayedChartCall.push([arguments.callee,this,arguments]);
        }

    }

    //get charJson
    this.setCharJson = function (char) {
        options.characterloaded = false;
        charJson = char;
        characterList = this.getCharList(charJson);
        view.removeAllCharacters();
        options.characterloaded = true;
        this.emit('setcharacters')
    }

    this.getCharList = function (char) {
        var result = [], value, key, state
        if (char) {
            for (key in char) {
                value = char[key];
                for (state in value) {
                    result.push(state);
                }
                break;
            }
        }
        return result;
    }

    this.colorTreeWithCharacter = function (selected) {
        if(!pj.isTreeLoaded()){
            return;
        }
        if (selected && selected.length) {
            pj.colorTreeWithCharacter(charJson, selected);
        } else {
            // since the color is not disappearing when an empty array of selected character is passed
//            charJson && pj.colorTreeWithCharacter(charJson, ['12#!!@']);
            pj.drawTreeWithoutCharacters()
        }
        that.emit('treecolored');
    }

    this.chartHover = function () {
        console.log(arguments);
    }

    /**
     * provide a character name and it will give a two dimensional array of species name and value
     * @param charName
     */
    this.getCharArray = function (charName,cjson) {
        var result = [], char;
        cjson = cjson || charJson;
        for (var species in cjson) {
            var char = cjson[species];
            char[charName] && char[charName].forEach(function (it) {
                result.push([species, it]);
            })
        }
        return result;
    }

    /**
     * subset a charjson data structure to the list provided.
     * @param species - list of species to make subset.
     * @param chars (charjson - optional) if not provided charjson initially provided is used.
     * @returns {{}} charjson
     */
    this.charJsonSubset = function(species,chars){
        var comp = function(a,b){
            return a === b
        }

        var cFn = options.cFn || comp,
            result = {};
        chars = chars || charJson;
//        for(i=0;i<species.length;i++){
//            for(j in chars){
//                if(cFn(species[i],j)){
//                    result[j]=chars[j];
//                }
//            }
//        }

        for(i=0;i<species.length;i++){
            if(chars[species[i]]){
                result[species[i]]=chars[species[i]];
            }
        }
        return result;
    }

    /**
     * The function accepts an array of terms and returns a frequency count of the all the terms.
     * the returned array is a two dimensional array.
     * @param terms
     * @returns {Array} [['term1',1]['term2',2]]
     */
    this.frequencyCount = function(terms){
        var freq ={},result=[],i;
        for(i=0;i<terms.length;i++){
            if(freq[terms[i]]){
                freq[terms[i]]++;
            }else{
                freq[terms[i]]=1;
            }
        }

        for(i in freq){
            result.push([i,freq[i]]);
        }

        return result;
    }

    this.updateCharts = function(node){
        var chars = view.characters();
        var i, data;
        data = pj.getChildrenName(node);
        for(i=0;i<chars.length;i++){
            view.updateChart(chars[i],data);
        }
    }

    this.initCharacters = function(){
        var char, flag = false;
        // make sure tree and character data are loaded.
        if(options.initCharacters.characters && pj.isTreeLoaded() && that.isCharacterLoaded()){
            while( char = options.initCharacters.characters.shift()) {
                view.addNamedCharacter(char.name);
                flag = true;
            }
            // click selection again otherwise, this will not reflect the character color on map.
            if( flag ){
                if(pj.getSelection()){
                    pj.clickNode(pj.getSelection().id);
                }
            }
        }
    }

    this.googleChartsLoaded = function(){
        var delay;
        console.log('google chart loaded');
        options.googleChartsLoaded = true;
        console.log(options.delayedChartCall.length)
        while( delay = options.delayedChartCall.shift()){
            delay[0].apply(delay[1],delay[2]);
        }
    }

    this.isCharacterLoaded = function(){
        return options.characterloaded;
    }

    this.save = function () {
        if (!options.doSync) {
            return;
        }

        var data = ko.toJSON(view);
        var sync = $.extend({}, options.syncData);
        sync.json = data;
        $.ajax({
            url: options.syncUrl,
            type: options.syncType,
            data: sync,
            success: function (data) {
                console.log('saved!');
            },
            error: function () {
                //TODO: popup error?
                console.log('error saving!');
            }
        });
    }

    this.readFile = function(file, callback){
        if(!file){
            return;
        }
        var f = new FileReader(),that = this;
        f.onload = function(){
            callback.apply(that, [f.result]);
        }
        f.readAsText(file);
    }

    /**
     * show header values
     * @param text
     */
    this.showHeaders = function(text){
        var headers, i;
        headers = this.getHeaders( text );
        upload.headers.removeAll();
        for(i=0;i<headers.length;i++){
            upload.headers.push(headers[i]);
        }
    }

    /**
     * pass a csv file content and get the headers.
     * @param text
     * @returns {Array}
     */
    this.getHeaders = function(text){
        var lines = text.split(/[\r\n]/g), firstLine = lines[0];
        var headers = [], columns = firstLine.split(',');
        if(columns){
            for(i=0;i<columns.length;i++){
                headers.push({
                    id: i,
                    displayname : columns[i]
                })
            }
        }
        return headers;
    }

    this.uploadCharacter= function(){
        var spinner = new Spinner(options.spinner);
        upload.message('');
//        $('#csvFrom').validate()
//        if($('#csvFrom').valid()){
//            $(upload.alertId).alert();
//            upload.message('Form not filled')
//            return false;
//        }

        var param = {
            title: upload.title(),
            column: upload.selectedValue()
        };
        var data = new FormData(document.getElementById('csvForm'));
        data.append("formParms",JSON.stringify(param));

        spinner.spin();
        $('#uploadCharacters').append(spinner.el);
        $.ajax({
            url: options.upload.url,
            type: options.upload.type,
            data: data,
            processData: false,
            contentType: false,
            success: function(data){
                spinner.stop();
                view.addNewSource(data)
            },
            error: function(){
                spinner.stop();
                $(upload.alertId).alert();
                upload.message('Upload failed');
                setTimeout($(upload.alertId).close, 5000)
                console.log('failed!')
            }
        })
    }

    this.loadCharacterFromUrl = function(url, select){
        view.listLoading(true)
        var spinner = new Spinner(options.spinner);
        spinner.spin();
        $('#sourceToolbar').append(spinner.el)
        $('#charactermain .btn-xs').append(spinner.el)
        $.ajax({
            url: url,
            success: function (data) {
                view.listLoading(false);
                spinner.stop();
                that.setCharJson(data);
                select && view.list(select);
            },
            error: function(){
                view.listLoading(false);
                spinner.stop();
            }
        })
    }

    this.startSpinner = function(){
        if(!spinner.el){
            spinner = new  Spinner(options.spinner);
        }

        spinner.spin();
        $('#'+id).append(spinner.el)
    }

    this.stopSpinner = function(){
        spinner.stop();
    }

    options.characterloaded = false;
    /**
     * load character from url or from provided list.
     */
    if(options.initCharacters.list){
        view.addNewSource(options.initCharacters.list);
    }else if (options.url) {
        $.ajax({
            url: options.url,
            type: options.type,
            dataType: options.dataType,
            success: function (data) {
                that.setCharJson(data);
            }
        })
    } else if (options.character) {
        this.setCharJson(options.character)
    }

    // load characters list
    if( options.edit && options.charactersList.url ){
        var flag = false
        $.ajax({
            url: options.charactersList.url,
            dataType:'JSON',
            success: function(data){
                var i, slistId;
                slistId = options.initCharacters.list && options.initCharacters.list.id;
                for(i = 0; i<data.length; i++){
                    if((data[i].id != slistId)){
                        view.lists.push(data[i]);
                    }
                }
                flag = true
            }
        })
    }

    function initPopover(){
        var pops = options.popOver, i,id;
        if($.cookie('_chari') == "ok")    {
            return
        }
        for(i=0;i<pops.length;i++){
            id = pops[i].id
            if(typeof id == "function"){
                pops[i].id = id = id();
            }
            $(id).popover(pops[i].options);
            $(id).popover('show');
        }
        $('body').on('click','.popover #charPopOverClose', function(){
            console.log('clicked button')
            that.popOver(false);
            $.cookie('_chari',"ok")
        })
    }

    this.popOver = function(toggle){
        var pops = options.popOver, i,id;
        for(i=0;i<pops.length;i++){
            id = pops[i].id
            if(typeof id == "function"){
                id = id();
            }
            if(toggle){
                $(pops[i].id).popover('show');
            } else {
                $(pops[i].id).popover('hide');
            }
        }
    }
//    $('#'+options.id).on('show',initPopover);
    $("body").on("show.bs.tab", "#"+options.tabId, function() {
        initPopover();
    });

    if($('#'+options.id).hasClass('active')){
        initPopover()
    }

    //set style
    $('#' + id).css('max-height', options.height - options.headerHeight);
    $('#' + id).css('overflow-y', 'auto');

    /**
     * this array will list all the recognised events of this object
     * @type {Array}
     */
    this.events = [
    /**
     * params
     * @selected - a list of
     */
        'statechange',
    /**
     * when charjson is set. fired by setCharJson function. handle this event when user provides a set of characters
     * to be initialized at start.
     */
        'setcharacters'
    ]

    /**
     * adding event listeners
     */
    view.on('statechange', this.colorTreeWithCharacter)
//    view.on('newchar', this.addChart)
    pj.on('click', this.updateCharts);
    this.on('setcharacters',this.initCharacters)
//    this.on('setcharacters',this.colorTreeWithCharacter)

    pj.on('treeloaded', this.initCharacters);
//    pj.on('treeloaded',this.colorTreeWithCharacter)

    // sync handler
    this.on('sync', this.save);
    view.on('statechange', function (list, init) {
        // do not save when initializing the charts. changed event is fired there too.
        !init && that.emit('sync');
    });

    if(options.edit){
        $("#csvFile").on('change', function(event){
            var file = event.target.files[0];
            that.readFile(file, that.showHeaders);
        });

        $("#uploadBtn").on('click', function(){
            that.uploadCharacter();
            return false;
        });
    } else {
        $("#uploadCharacters").hide();
        $("#pickFromList").hide();
        $('#charactermain .alert').hide();
    }
};