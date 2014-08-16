<%--
 Created by Temi Varghese on 20/06/2014
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<div id="env${i}" class="phylolink-widget phylolink-env">
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <a class="brand" href="#" ><h6>${phyloInstance.widgets.getAt(i).title}</h6></a>

                <div class="nav-collapse collapse pull-right">
                    <ul class="nav">
                        <li>
                            <a href="#" title="Download this data" onclick="widgets.download(${i})">
                                <i class="icon-download"></i>
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div id="env${i}-content"></div>
    <script type="text/javascript">
        new widgets.PD(${i});
    </script>
</div>