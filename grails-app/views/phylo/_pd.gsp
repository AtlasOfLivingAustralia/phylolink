<%--
 Created by Temi Varghese on 7/07/2014
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

</head>

<body>
<div id="env${i}" class="phylolink-widget phylolink-env" >
    <h6>${title}</h6>
    %{--<div class="row">--}%
    <div id="env${i}-content" style="font-size: xx-large;text-align: center;padding-top:65px" ></div>
    %{--</div>--}%
    <script type="text/javascript">
        widgets.add( widgetPD( '${i}','env${i}-content', "${createLink( controller: 'Phylo', action:'getWidgetData') }/${phyloInstance.id}" ) );
    </script>
</div>

</body>
</html>