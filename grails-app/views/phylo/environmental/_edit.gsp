<%@ page import="grails.converters.JSON" %>
%{--<script id="_tmplEnvironmentalEdit" type="text/html">--}%

%{--</script>--}%
<script type="text/javascript">
    $(document).ready( function () {
          var data = ${raw(( widget as JSON ).toString() )}
          new widgets.Environmental(${i}, data).create( "widgets" );
    })
</script>