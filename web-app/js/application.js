if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}
function searchForTrees( ){
    var name = $("#nodename").attr('value');
    var data = {"property":"ot:originalLabel","value":'',"verbose":true};
    data.value = name;
    loadStudyList( data , searchTreeUrl );
}