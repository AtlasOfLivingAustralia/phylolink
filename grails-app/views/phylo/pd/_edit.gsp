<%@ page import="grails.converters.JSON" %>
<script type="text/javascript">
    $(document).ready( function (){
        data = ${raw(( widget as JSON ).toString() )}
        new widgets.PD( ${i}, data).create("widgets");
    })
</script>