/**
 * Created by Temi Varghese on 8/08/2014.
 */
var utils={
    autocomplete:function( elementId, list, displayId, configId ){
        jQuery("#"+elementId ).autocomplete( {
            source: list,
            matchSubset: false,
            minChars: 3,
            scroll: true,
            max: 10,
            selectFirst: false,
            dataType: 'jsonp',
            formatMatch: function( row , i ){
                return row.label;
            },
            select: function( et , selection ){
                $(document.getElementById( configId ) ).attr( 'value', selection.item.value );
                $( document.getElementById(  displayId  ) ).attr( 'value', selection.item.label );
            }
        });
        // the autocomplete box is appearing below the modal dialog. below logic to prevent it.
        $(".ui-autocomplete").css('z-index',1051);
    },
    addTemplate: function( dest, tmpId){
//        $('#'+dest).append( $('#'+tmpId).html() )
        return $($('#'+tmpId).html()).appendTo('#'+dest)
    },
    download:function( data ){
        $.ajax({
            url:'http://localhost:8080/phylolink/download',
            method:'POST',
            data:{
                json: JSON.stringify( data )
            }
        })
    },
    modalDialog:function( opts ){
        var options= {
            id: opts.id || 'modalDialog',
            remote: opts.url,
            height: opts.height || 500,
            width: opts.width || 560
        }
        var mSel = "#_tmpModal";
        var html = $(mSel).html();
        var modal = $(html).appendTo( "body" );
        ko.applyBindings(opts, modal[0]);

        var sel = '#'+ options.id;
        modal.find('.modal-body').css( 'max-height', options.height +'px');
        $( sel ).on("hidden", function(){
           $( sel ).remove();
        });
        modal.modal( options )
    },

    getDownloadReasons: function(url) {
        var downloadReasons = ko.observableArray();
        $.ajax({
            url: url,
            dataType: "JSONP",
            success: function (data) {
                for (var i in data) {
                    downloadReasons.push(new utils.SelectItem(data[i].id, data[i].name));
                }
            }
        });

        return downloadReasons;
    },

    OccurrenceDownloadViewModel: function(url) {
        this.downloadReasons = utils.getDownloadReasons(url);
        this.email = ko.observable();
        this.reason = ko.observable();
    },

    SelectItem: function(id, name) {
        this.id = ko.observable(id);
        this.displayName = ko.observable(name);
    },
    
    clearPlaceholder: function(it) {
        $(it).attr('placeholder','')
    },

    updateCladeInfo: function(list) {
        if (list.length == 0) {
            pj.hData.selectedClade('All taxa selected')
            pj.hData.selectedCladeNumber(-1)
        } else {
            pj.hData.selectedClade('' + list.length + ' taxa selected')
            pj.hData.selectedCladeNumber(list.length)
        }
    },
    setupTree: function(config, tree, input) {
        if (config === undefined || config.list === undefined || config.list.length == 0) {
            setTimeout(function() {utils.setupTree(config, tree, input)}, 200)
            return
        }

        var source = []
        var i
        for (i = 0; i < config.list.length; i++) {
            var class1 = config.list[i].classification1
            var class2 = config.list[i].classification2
            var label = config.list[i].displayname
            config.list[i].label = label

            if (class1 === undefined || class1 === '') class1 = 'Other'
            if (class2 === undefined || class2 === '') class2 = 'Other'

            var sclass1 = undefined
            var sclass2 = undefined
            var j
            for (j = 0; j < source.length; j++) {

                if (source[j].label === class1) {
                    sclass1 = source[j]

                    var k
                    for (k = 0; k < source[j].items.length; k++) {
                        if (source[j].items[k].label === class2) {
                            sclass2 = source[j].items[k]
                        }
                    }
                }
            }
            var item = {label: label, value: config.list[i]}
            if (sclass1 === undefined) {
                source.push({label: class1, items: [{label: class2, items: [item]}]})
            } else if (sclass2 === undefined) {
                sclass1.items.push({label: class2, items: [item]})
            } else {
                sclass2.items.push(item)
            }
        }

        source = source.sort(function (a, b) {
            return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
        })
        for (i = 0; i < source.length; i++) {
            source[i].items = source[i].items.sort(function (a, b) {
                return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
            })
            var j
            for (j = 0; j < source[i].items.length; j++) {
                source[i].items[j].items = source[i].items[j].items.sort(function (a, b) {
                    return a.label < b.label ? -1 : a.label > b.label ? 1 : 0
                })
            }
        }
        console.log($('#tabs').width() - 50 + 'px')
        tree.jqxTree({
            source: [{label: 'Layers', items: source, expanded: true}],
            height: '300px',
            width: $('#tabs').width() - 50 + 'px',
            allowDrag: false,
            toggleMode: "click"
        });
        tree.on('select', function (event) {
            var args = event.args;
            if (args.element !== undefined) {
                var item = tree.jqxTree('getItem', args.element);
                if (item.value !== undefined && item.value != null && item.value.label !== undefined) {
                    input.treeSelect(item.value)
                }
            }
        });
    }
};