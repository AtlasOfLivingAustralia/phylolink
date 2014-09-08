<%--
 Created by Temi Varghese on 20/06/2014
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<div id="env${i}" class="phylolink-widget phylolink-env panel">
    <div class="panel-heading" title="${phyloInstance.widgets.getAt(i).title}">
            ${phyloInstance.widgets.getAt(i).title}
        <div class="pull-right">
            <button class="btn-mini btn btn-inverse" onclick="widgets.download(${i})" title="Download this data">
                <i class="icon-download-alt icon-white"></i>
            </button>
        </div>
    </div>
    <div id="env${i}-content" class="panel-content"></div>
    <script type="text/javascript">
        $(document).ready(function(){
            new widgets.PD(${i});
        })
    </script>
</div>