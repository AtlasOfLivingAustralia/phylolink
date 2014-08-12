<%--
 Created by Temi Varghese on 20/06/2014
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

</head>

<body>
<div id="env${i}" class="phylolink-widget phylolink-env" >
    <h6>Environmental Widget</h6>
    <div id="env${i}-content"></div>
    <script type="text/javascript">
        new widgets.Template( ${i} );
    </script>
</div>

</body>
</html>