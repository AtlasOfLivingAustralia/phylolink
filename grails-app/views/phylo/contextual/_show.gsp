<%--
 Created by Temi Varghese on 20/06/2014
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<div id="env${i}" class="phylolink-widget phylolink-env" >
    <h6>Contextual Widget</h6>
    <div id="env${i}-content"></div>
    <script type="text/javascript">
        new widgets.Contextual( ${i} );
    </script>
</div>