
/**
 * The _characters.gsp file contains the view associated with this script
 */

var Character = function (options) {
    // emitter mixin. adding functions that support events.
    new Emitter(this);
    var $ = jQuery;
    var that = this, character = this;

    options = $.extend({
        type: 'GET',
        dataType: 'json',
        headerHeight: 0,
        googleChartsLoaded: false,
        delayedChartCall:[],
        chartWidth:400,
        chartHeight:200,
        chartAreaHeight:100,
        chartDataHeaders:[
            ['Species Name', 'Value', { role: 'style' }]
        ],
        // flag to show upload character interface
        edit: true,
        //flag to check if character has been loaded
        characterloaded: false,
        primaryClass: 'label label-primary',
        defaultClass: 'label label-default',
        characterUndefinedColor: '#fff',
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
        /**
         * url to get the delete a character dataset from a visualisation
         */
        deleteCharacterSourceUrl: 'http://dev.ala.org.au:8080/phylolink/characters/deleteResource',
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
    var spinner = new Spinner(options.spinner), spinners={};
    var id = options.id;
    var inputId = id + 'autoComplete', uploadTitleId = 'uploadCharactersTitle',
        minUploadId = 'minimizeUpload';
    var pj = options.pj;
    var characterListLoaded = false;
    var input = $('#' + inputId);
    var characterList = [], charJson;

    // knockout code
    var Character = function (opt) {
        this.name = ko.observable(opt.name);
        this.id = ko.observable(opt.id);
		this.description = ko.observable([]);
		this.type = ko.observable([]);
    }

    var CharacterViewModel = function () {
        new Emitter(this);
        var self = this;
        /**
         * list all the events supported by this function
         * @type {Array}
         */
        self.events = [
            'statechange',
            'moved',
            'edited',
            'newchar', // when new characters are added to the list.
            'removed'
        ];
        self.newChar = false;
        self.characters = ko.observableArray([]);
        self.count = ko.observable(1);
        self.selectedCharacter = ko.observable();
        self.lists = ko.observableArray([]);
        self.activeCharacterList = ko.observableArray([]);
        self.list = ko.observable({});
        self.edit = ko.observable();
        self.listLoading = ko.observable(false);
		self.saveSource = false;
		
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

        self.addCharacter = function (e) {
            var name = $('#selectChar').val();
            var opt = {name: name, id: 'charChart-' + self.count()}
            self.count(self.count() + 1);
            var character = new Character(opt);
            self.selectedCharacter(character);
            self.characters.push(character);
            self.emit('newchar', self.charlist());
            return character;
        };

        // self.toggleGradient = function(){
        //     if(options.pj.useGradient){
        //         options.pj.useGradient = false
        //     } else {
        //         options.pj.useGradient = true
        //     }
        //     self.emit('statechange');
        // };

        self.setActiveCharacterList = function(list){
            self.activeCharacterList.removeAll();
            for (var i = 0; i< list.length; i++){
                self.activeCharacterList.push(list[i]);
            }
        }

        self.removeAllCharacters = function(silent){
            self.characters.removeAll();
            self.selectedCharacter(null);
            !silent && self.emit('statechange');
        }

        self.addNamedCharacter = function (char, silent) {
            var character = self.addCharacter(char);
            // to hide input tag and to bring label visible.
            self.clearCharacter(character,null)
            !silent && char && self.emit('statechange', self.charlist(),true);
        };

        self.isCharacterSelected = function (character) {
            // console.log(character);
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
            self.newChar && self.emit('newchar', self.charlist());
            self.newChar = false;
            self.emit('statechange', self.charlist());
        }

        /**
         * This function is called when a character is newly added. not when it is rearranged.
         */
        self.updateChart = function(char,list){
            var id = char.id(),
                name = char.name(),
                charJson;
            charJson = that.charJsonSubset(list);
            temp = that.getCharArray(name,charJson);

            if(temp == undefined || temp.length == 0){
                that.columnchart(id,[['',''],[0,0]], name);
                return;
            }

            // check if values are string or numeric
            if (typeof temp[0][1] === 'number') {
                character.chartQuantitativeChars(name, temp, id);
            } else {
                character.chartQualitativeChars(name, temp, id);
            }
        }

        self.loadNewCharacters = function(){
			var sel = self.list();
			if (self.saveSource && self.edit()) {
				var characterSourceId = (sel == null ? null : sel.id);
				var sync = {id: options.phyloId, characterSourceId: characterSourceId};
				$.ajax({
					url: options.syncSourceUrl,
					data: sync,
					success: function (data) {
						console.log('saved charactersSource in database id=' + options.phyloId + ', characterSourceId=' + characterSourceId + ' !');
					},
					error: function () {
						console.log('error saving charactersSource id=' + options.phyloId + ', characterSourceId=' + characterSourceId + ' !');
					}
				});
			}
            
			self.activeCharacterList.removeAll();
            if(self.list()){
                self.characters.removeAll()
                that.loadCharacterFromUrl(self.list().url);
            }
        }

        self.addNewSource = function(list){
            self.lists.push(list);
            self.list(list)
            $("#sourceChar").trigger('change');
            $("#sourceChar").focus();
        }

        /**
         * Disassociate a dataset from the system.
         * @param item
         */
        self.removeSource = function(item){

            //the remove below can change self.list() so look it up before the remove
			var removeCurrentSelection = (self.list() != null && self.list().id == item.id);

			self.saveSource = false;
            self.lists.remove( function (listToTest) { return listToTest.id == item.id; } );
			self.saveSource = true;

            if(removeCurrentSelection){
                self.list(null);
                self.activeCharacterList([]);
            }

            self.saveCharacters();

            //if the list
            $.ajax({
                url: options.deleteCharacterSourceUrl,
                dataType: 'JSON',
                method: 'POST',
                data: {
                    id: item.id,
                    drid: item.dataResourceId,
                    phyloId: options.phyloId
                },
                success: function (tmp) {
                    self.lists.remove(item);


                },
                error: function ( jqXHR,  textStatus,  errorThrown) {
                    console.log(jqXHR);
                    console.log(textStatus);
                    console.log(errorThrown);
                    console.log('error saving records!');
                }
            });
        };

        self.saveCharacters = function(){
            var data = ko.toJSON(this);
            var sync = $.extend({}, options.syncData);
            sync.json = data;
            $.ajax({
                url: options.syncUrl,
                type: options.syncType,
                data: sync,
                success: function (data) {
                    console.log('saved!');
                    upload.headers([]);
                    upload.charactersTitle('');
                    upload.selectedValue('');
                },
                error: function () {
                    console.log('error saving!');
                }
            });
        };

        /**
         * show header values
         * @param text
         */
        self.resetForm = function(){
            upload.headers([]);
            upload.charactersTitle('');
            upload.selectedValue('');
			if (!upload.message()) {
				$('#characterUploadPanel').attr('aria-expanded', false).removeClass('in');
			}
        }

        /**
         * give a list of characters to this function to check if it they are in the selected character list
         * and return an array of models of characters found.
         * @param keys
         * @returns {Array}
         */
        self.searchCharacters = function(keys){
            var selected = [], i, char = self.characters(), key;
            for (i = 0; i < char.length; i++) {
                for(key in keys){
                    if(keys[key] == char[i].name()){
                        selected.push(char[i]);
                        break;
                    }
                }
            }
            return selected;
        }
		
		self.findResourceById = function(id){
            return ko.utils.arrayFirst(self.lists(), function(item){
                return id === item.id;
            });
        }
    };

    this.getCharacterViewModel = function(){ return CharacterViewModel;}

    ko.bindingHandlers.addChart = {
        update: function (el, valueAccessor, innerFn, data, koObj) {
            if (valueAccessor()) {
                var id = data.id(), charName = data.name(), charJs,
                    node = pj.getSelection(),
                    list;
                if(!charJson){
                    return;
                }

                if(node){
                    list = pj.getChildrenName(node);
                    charJs = that.charJsonSubset(list);
                }

                var temp = that.getCharArray(charName,charJs)
                if(temp == undefined || temp.length == 0){
                    return;
                }
                // check if values are string or numeric
                if (typeof temp[0][1] === 'number') {
                    character.chartQuantitativeChars(charName, temp, id);
                } else {
                    character.chartQualitativeChars(charName, temp, id);
                }
            }
        }
    };

    var view = new CharacterViewModel();

    ko.applyBindings(view, document.getElementById('charactermain'));

    var UploadViewModel = function(){
        this.headers = ko.observableArray();
        this.selectedValue = ko.observable();
        this.charactersTitle = ko.observable();
        this.message = ko.observable();
        this.alertId = '#uploadMessage';
        this.sampleCSV = options.sampleCSV;

        this.onClick = function(model, e){
            var obj = $('#'+minUploadId)
            obj.toggle('hide', function(){
                if(!obj.is(':visible')){
                    $('#'+uploadTitleId +' i').addClass('glyphicon-chevron-down');
                    $('#'+uploadTitleId+' i').removeClass('glyphicon-chevron-up');
                } else {
                    $('#'+uploadTitleId+' i').removeClass('glyphicon-chevron-down');
                    $('#'+uploadTitleId+' i').addClass('glyphicon-chevron-up');
                }
            });
        }
        this.cancelCharUpload = function(model, e){
            $("#csvFile").val('');
            $("#char-upload-file-info").html('Upload a CSV file of characters');
            this.headers([]);
        }
    }

    var upload = new UploadViewModel();
    ko.applyBindings(upload, document.getElementById('uploadCharacters'));


    /**
     * transform data to be able to be displayed by chart. i.e. convert qualitative character to term frequency
     * to display as histogram.
     * @param temp
     * @returns {*[]}
     */
    this.chartDataTransform = function(temp){
        var data = [
        ],
            oneD=[];
        temp.forEach(function(it){
            if(it[1] != undefined){
                oneD.push(it[1]);
            }
        });
        temp = that.frequencyCount(oneD);
        temp.forEach(function (it) {
            data.push(it);
        });

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
                legend: { position: 'none' },
                chartArea: { top: 80 },
                vAxis: {
                    title: options.graph.yAxis
                },
                hAxis: {
                    title: options.graph.xAxis,
                    slantedText: true
                }
            };
            data = google.visualization.arrayToDataTable(data);
            chart.draw(data, opt);
        } else {
            // some times google chart is not ready when this function is called
            options.delayedChartCall.push([arguments.callee, this, arguments]);
        }
    }

    /**
     * draws a column chart. this is used when data are qualitative i.e. string. currently using google charts
     * @param id - element id to draw the map
     * @param data - contains data in google chart understandable format
     */
    this.columnchart = function(id, data, xAxis){
        xAxis = xAxis || options.graph.xAxis
        if(options.googleChartsLoaded){
            var chart = new google.visualization.ColumnChart(document.getElementById(id));
            var width = $('#' + options.id + ' .panel-body:first').width() || options.chartWidth;
            var height = options.chartHeight;
            var opt = {
                width: width,
                height: height,
                legend: { position: 'none' },
                chartArea: { top: 30, chartAreaHeight: options.chartAreaHeight },
                vAxis: {
                    title: options.graph.yAxis
                },
                hAxis: {
                    title: xAxis,
                    slantedText: true,
                    textPosition: 'in'
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
     * creates a key value pair of character name and it color value
     *
     */
    this.getColorForCharacter = function(name, state){
        var legends = pj.getLegendForCharacter(name),
            result;

        legends && legends.forEach(function(it){
            if( it['name'] == state){
               result = it;
           }
        });
        return result || options.characterUndefinedColor;
    }

    //get charJson
    this.setCharJson = function (char, keepSelection) {
        var remove = !keepSelection;
        options.characterloaded = false;
        charJson = char;
        if(!options.charOnRequest){
            characterList = this.getCharList(charJson);
        }

        remove && view.removeAllCharacters();
        options.characterloaded = true;
        this.emit('setcharacters', view.charlist())
    }

    /**
     * set keys as a list of characters
     * @param charList an array of character list
     */

    this.setCharList = function( list ){
        characterList = list;
        view.removeAllCharacters();
        view.setActiveCharacterList(list);
        this.setCharacterListLoaded(true);
        this.emit('setcharacterlist');
    }

    /**
     *
     */
    this.getCharListFromUrl = function(url, params){
        this.setCharacterListLoaded(false);
        var that = this
        this.setCharJson(null);
        $.ajax({
            url: url,
            data: params,
            success: function (data) {
                that.setCharList(data);
                that.setCharacterListLoaded(true);
            }
        })
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

        var useGradient = true;

        if (selected && selected.length) {
            pj.colorTreeWithCharacter(charJson, selected, useGradient);
        } else {
            pj.drawTreeWithoutCharacters()
        }
        that.emit('treecolored');
    }

    /**
     * get charjson for the list of characters specified
     * @param keys - array of string
     */
    this.getCharJsonForKey = function( keys ){
        var params = options.charOnRequestParams, that = this;
        params.keys = keys.join(',');
        this.emit('asynccharjsonstart',keys);
        $.ajax({
            url: options.charOnRequestBaseUrl,
            data: params,
            success: function(data){
                data = that.preprocessCharJson(data);
                var charjson = that.charJsonMerge(charJson, data);
                that.setCharJson( charjson, true );
                that.emit('asynccharjsonset',keys);
            }
        })
    }

    /**
     * converts string undefined values into JS undefined value. It is used to represent unknown values.
     * @param charJson
     * @returns {*}
     */
    this.preprocessCharJson = function(charJson){
        var values, i;
        if(charJson){
            for(var species in charJson){
                if(charJson[species]){
                    for(var char in charJson[species]){
                        values = charJson[species][char]
                        for(i=0;i<values.length;i++){
                            if(values[i]==='undefined'){
                                values[i] = undefined;
                            }
                        }
                    }
                }
            }
        }
        return charJson;
    }

    /**
     * merge the second charjson to the first charjson
     */
    this.charJsonMerge = function(first, second){
        if( !second ){
            return first;
        } else if(!first){
            return second;
        }

        var species, char;
        for (species in second){
            if(!first[species]){
                first[species] = second[species];
            } else {
                for(char in second[species]){
                    if(!first[species][char]){
                        first[species][char] = second[species][char];
                    } else {
                        $.merge(first[species][char], second[species][char]);
                    }
                }
            }
        }

        return first;
    }

    /**
     *
     */
    this.checkCharJson = function(selected){
        if(!selected){
            that.colorTreeWithCharacter()
            return;
        }
        var present = false, notPresent = [], key;
        for(var species in charJson){
            for(var i=0;i<selected.length; i++){
                key = selected[i];
                if( !(key in charJson[species]) ){
                    notPresent.push(key);
                }
            }
            break;
        }

        if(!charJson){
            notPresent = selected;
        }

        if(notPresent.length == 0){
            that.colorTreeWithCharacter(selected)
        } else {
            that.getCharJsonForKey(notPresent)
        }
    }

    this.chartHover = function () {
        // console.log(arguments);
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

    /**
     * refreshes all character charts.
     * @param node
     */
    this.updateCharts = function(node){
        node = node || pj.getSelection();
        var chars = view.characters();
        var i, data;
        data = pj.getChildrenName(node);
        for(i=0;i<chars.length;i++){
            view.updateChart(chars[i],data);
        }
    }

    /**
     * if character loading is async then refresh chart.
     * note: this function is called when setcharacters event is fired.
     */
    this.showChartCharOnRequest = function(){
        if(options.charOnRequest){
            this.updateCharts()
        }
    }

    this.getCharacterListLoaded = function(){
        return characterListLoaded;
    }

    this.setCharacterListLoaded = function(flag){
        return characterListLoaded = !!flag;
    }

    this.initCharacters = function(){
        var char, flag = false, chars;
        if( options.initCharacters && options.initCharacters.characters  && options.initCharacters.characters.length ){
            if(options.charOnRequest ){
                if( character.getCharacterListLoaded()){
                    chars = [];
                    while( char = options.initCharacters.characters.shift()) {
                        chars.push(char.name);
                        view.addNamedCharacter(char.name, true);
                    }

                    chars.length && character.getCharJsonForKey(chars);
                }
            } else {
                // make sure tree and character data are loaded.
                if( pj.isTreeLoaded() && that.isCharacterLoaded()){
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
        }
    }

    this.googleChartsLoaded = function(){
        var delay;
        // console.log('google chart loaded');
        options.googleChartsLoaded = true;
        // console.log(options.delayedChartCall.length)
        while( delay = options.delayedChartCall.shift()){
            delay[0].apply(delay[1],delay[2]);
        }
    }

    this.isCharacterLoaded = function(){
        return options.characterloaded;
    }

    this.save = function () {
        if (!(options.doSync && this.getCharacterListLoaded()) ) {
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
                upload.headers([]);
                upload.charactersTitle('');
                upload.selectedValue('');
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
        upload.message('');
        for (i = 0; i < headers.length; i++){
            upload.headers.push(headers[i]);
        }
    }

    this.initialiseTitleAndSelected = function(title){
        upload.charactersTitle(title);
        upload.selectedValue(upload.headers()[0]);
        console.log("selected: " + upload.headers())
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

    this.uploadCharacter = function(){
        var spinner = new Spinner(options.spinner);
        upload.message('');

        var param = {
            title:  upload.charactersTitle(),
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
            success: function(result){
				// check if the just uploaded characters files does not have any species that overlap with the species in the tree
		        $.ajax({
					url: options.checkCharacterFile.url,
					dataType:'JSON',
					data: {
						id: result.id,
						treeId: options.treeId
					},
					success: function(data){
						spinner.stop();
						if (data.isCompatibleWithTree) {
							result = $.extend({
								canDelete: true,
							}, result);
							view.addNewSource(result);				
						} else {
							//show message that file is not compatible
							$(upload.alertId).alert();
							upload.message('There are no species in your uploaded file that match the tree. Please ensure the species scientific name ' + 
								'exactly matches the species name as shown in the tree (please note that underscores in the species names in the tree ' + 
								'are replaced with spaces before being shown, so please remove underscores from your species names in your character ' + 
								'data).  Please update the file or choose another file.');
//							setTimeout($(upload.alertId).close, 10000)
							console.log('There are no species in your uploaded file that match the tree.  Please update the file or choose another file.')
						}
						view.resetForm();
					},
					error: function(){
						spinner.stop();
						$(upload.alertId).alert();
						upload.message('Checking that the uploaded file is compatible with the tree failed');
						setTimeout($(upload.alertId).close, 5000)
						console.log('Checking that the uploaded file is compatible with the tree failed!')
						view.resetForm();
					}
				})				
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
        var drid, params = options.charOnRequestParams;
        if (!options.charOnRequest){
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
        } else if (options.charOnRequest){
            drid = url.match(/[^\d=]+\d+/g);
            params.drid = drid.length && drid[0];
            this.getCharListFromUrl(options.charOnRequestListKeys, params);
        }
    }

    this.startSpinner = function(id, opt){
        var option  = opt || options.spinner,
            option = $.extend({},option), spinner;
        spinner = new  Spinner(option);
        spinner.spin();
        $('#'+id).append(spinner.el)
        return spinner;
    }

    this.stopSpinner = function(){
        spinner.stop();
    }

    options.characterloaded = false;

    /**
     * load character from url or from provided list.
     */
    if(options.initCharacters && options.initCharacters.list){
		options.initCharacters.list.id = null;
		options.initCharacters.list.title = '(Removed while active) ' + options.initCharacters.list.title;
		options.initCharacters.list.canDelete = false;
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
    if( options.charactersList.url ){
        var flag = false
        $.ajax({
            url: options.charactersList.url,
            dataType:'JSON',
            data: {
                treeId: options.treeId,
				initCharacterResourceId: options.initCharacterResourceId
            },
            success: function(data){
                var i, slistId;
                slistId = options.initCharacters.list && options.initCharacters.list.id;
                for(i = 0; i<data.length; i++){
                    if((data[i].id != slistId)){
                        view.lists.push(data[i]);
                    }
                }
                flag = true
                character.selectADataresource();
            }
        })
    }

    /**
     * select a data resource on init
     * todo: change this
     */
    this.selectADataresource = function(){
        var id = options.initCharacterResourceId,
            selected = view.list();
        if(id || !selected){
            if(options.selectResourceOnInit){
                var dr = view.findResourceById(id);
                view.list(dr);				
				view.loadNewCharacters();
				view.saveSource = true;
            }
        }
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

    this.onAsyncCharJsonStart = function(keys){
        var chars, id, name, opt = options.spinner;
        opt.position = 'relative';
        chars = view.searchCharacters(keys);

        for(var i in chars){
            id = chars[i].id();
            name = chars[i].name();
            if(!spinners[name]){
                spinners[name] = []
            }

            spinners[name].push(this.startSpinner(id, opt))
        }
    };

    this.onAsyncCharJsonFinish = function( chars ){
        var spin;
        for(var i in chars){
            while(spin = spinners[chars[i]].pop()){
                spin.stop();
            }
        }
    };

    this.getSelectedCharacters = function(){
        var list = view.characters(),
            result = [];
        list.forEach(function(item){
            result.push(ko.toJS(item));
        });
        return result
    }

    this.chartQualitativeChars = function (name, temp, id) {
        // clone array
        var data = options.chartDataHeaders.slice();
        // google chart representation
        data = data.concat(that.chartDataTransform(temp));
        for (i = 1; i < data.length; i++) {
            charObj = character.getColorForCharacter(name, data[i][0]);
            if (charObj) {
                data[i].push(pj.toHex(charObj.red, charObj.green, charObj.blue));
            }
        }
        character.columnchart(id, data, name);
    };

    this.chartQuantitativeChars = function(name, temp, id) {
        var data,
            i, j,
            range,
            charObj,
            range1,
            range2;

        for (i = 0; i < temp.length; i++) {
            //get range text '1 - 2' or '2 - 3'
            range = pj.getQuantCharacterState(temp[i][1], name);
            temp[i][1] = range;
        }

        // clone array
        data = options.chartDataHeaders.slice();

        // google chart representation
        data = data.concat(that.chartDataTransform(temp));

        // add color
        for (i = 1; i < data.length; i++) {
            charObj = character.getColorForCharacter(name, data[i][0]);
            if (charObj) {
                data[i].push(pj.toHex(charObj.red, charObj.green, charObj.blue));
            }
        }
        // bubble sort
        for (i = 1; i < data.length; i++) {
            for (j = i + 1; j < data.length; j++) {
                range1 = pj.getRange(data[i][0]);
                range2 = pj.getRange(data[j][0]);
                if (range1[0] > range2[0]) {
                    temp = data[i];
                    data[i] = data[j];
                    data[j] = temp;
                }
            }
        }
        character.columnchart(id, data, name)
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
        'setcharacters',
    /**
     * fired when the system requests charjson asynchronously.
     */
        'asynccharjsonstart',

    /**
     * fired when asynchronous charjson request finishes.
     */
        'asynccharjsonfinish'
    ]

    /**
     * adding event listeners
     */
    view.on('newchar', this.checkCharJson)
    pj.on('click', this.updateCharts);
    this.on('setcharacterlist',this.initCharacters)
    this.on('setcharacters',this.initCharacters)
    view.on('statechange',this.colorTreeWithCharacter)
    this.on('setcharacters',this.colorTreeWithCharacter)
    this.on('setcharacters', this.showChartCharOnRequest)
    this.on('asynccharjsonstart', this.onAsyncCharJsonStart);
    this.on('asynccharjsonfinish', this.onAsyncCharJsonFinish)

    pj.on('treeloaded', this.initCharacters);

    // sync handler
    this.on('sync', this.save);
    view.on('statechange', function (list, init) {
        // do not save when initializing the charts. changed event is fired there too.
        !init && that.emit('sync');
    });
    
	view.edit.subscribe(function(newValue) {
		if (newValue){
			$("#csvFile").on('change', function(event){
				var file = event.target.files[0];
				that.readFile(file, that.showHeaders);
				that.initialiseTitleAndSelected(file.name)
				$('#sciNameColumn').focus();
			});

			$("#uploadBtn").on('click', function(){
				that.uploadCharacter();
				return false;
			});

			$("#csvForm").show();
			$('#charactermain .alert').show();
			$("#csvFormUnavailable").hide();
		} else {
			$("#csvForm").hide();
			$('#charactermain .alert').hide();
			$("#csvFormUnavailable").show();
		}
    });
	view.edit(options.edit);
	
    //Expose this for use by other tabs
    this.config = options;
    this.characterViewModel = view;
};