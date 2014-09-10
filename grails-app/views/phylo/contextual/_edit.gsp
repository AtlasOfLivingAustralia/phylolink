<%@ page import="grails.converters.JSON" %>
%{--<script id="_tmplContextualEdit" type="text/html">--}%

%{--</script>--}%
<script type="text/javascript">
    $(document).ready( function () {
        var data = ${raw(( widget as JSON ).toString() )}
                new widgets.Contextual(${i}, data).create( "widgets" );
    })
</script>