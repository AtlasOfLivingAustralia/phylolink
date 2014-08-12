/**
 * Created by Temi Varghese on 8/08/2014.
 */
var utils={
    autocomplete:function( elementId, list, displayId, configId ){
        jQuery("#"+elementId ).autocomplete( {
            source: list,
            matchSubset: false,
            minChars: 3,
            scroll: false,
            max: 10,
            selectFirst: false,
            dataType: 'jsonp',
            formatMatch: function( row , i ){
                return row.label;
            },
            select: function( et , selection ){
                console.log( displayId );
                console.log( elementId );
                $(document.getElementById( configId ) ).attr( 'value', selection.item.value );
                $( document.getElementById(  displayId  ) ).attr( 'value', selection.item.label );
            }
        });
    }
}
