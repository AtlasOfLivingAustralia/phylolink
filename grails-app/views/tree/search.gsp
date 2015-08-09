<%--
 Created by Temi Varghese on 10/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Search for Tree</title>
    <meta name="layout" content="main"/>
</head>

<body>
<g:if test="${results?.hits?.total?:0 > 0}">
    <div id="" class="row-fluid ">
        <div id="facetsCol" class="span4 well well-small">
            <g:set var="reqParams" value="sort,order,max,fq"/>
            <div class="visible-phone pull-right" style="margin-top: 5px;">
                <a href="#" id="toggleFacetDisplay" rel="facetsContent" role="button" class="btn btn-small btn-inverse" style="color:white;">
                    <span>show</span> options&nbsp;
                    <b class="caret"></b>
                </a>
            </div>
            <h3 style="margin-bottom:0;">Filter results</h3>

            <g:if test="${params.fq}">
                <div class="currentFilters">
                    <h4>Current filters</h4>
                    <ul>
                    <%-- convert either Object and Object[] to a list, in case there are multiple params with same name --%>
                        <g:set var="fqList" value="${[params.fq].flatten().findAll { it != null }}"/>
                        <g:each var="f" in="${fqList}">
                            <g:set var="fqBits" value="${f?.tokenize(':')}"/>
                            <g:set var="newUrl">
                                %{--<fc:formatParams params="${params}" requiredParams="${reqParams}" excludeParam="${f}"/>--}%
                            </g:set>
                            <li><g:message code="label.${fqBits[0]}" default="${fqBits[0]}"/>: <g:message code="label.${fqBits[1]}" default="${fqBits[1]}"/>
                                <a href="${newUrl?:"?"}" class="btn btn-inverse btn-mini tooltips" title="remove filter">
                                    <i class="icon-white icon-remove"></i></a>
                            </li>
                        </g:each>
                    </ul>
                </div>
            </g:if>
            <div id="facetsContent" class="hidden-phone">
                <g:set var="baseUrl">
                    <fc:formatParams params="${params}" requiredParams="${reqParams}"/>
                </g:set>
                <g:set var="fqLink" value="${"?"}"/>
            <!-- fqLink = ${fqLink} -->
                <g:each var="fn" in="${facetsList}">
                    <g:set var="f" value="${results.facets.get(fn)}"/>
                    <g:set var="max" value="${5}"/>
                    <g:if test="${fn != 'class' && f?.terms?.size() > 0}">
                        <g:set var="fName"><g:message code="label.${fn}" default="${fn?.capitalize()}"/></g:set>
                        <h4>${fName}</h4>
                        <ul class="facetValues">
                            <g:each var="t" in="${f.terms}" status="i">
                                <g:if test="${i < max}">
                                    <li><a href="${fqLink}&fq=${fn.encodeAsURL()}:${t.term.encodeAsURL()}"><g:message
                                            code="label.${t.term}" default="${t.term}"/></a> (${t.count})
                                    </li>
                                </g:if>
                            </g:each>
                        </ul>
                        <g:if test="${f?.terms?.size() > max}">
                            <a href="#${fn}Modal" role="button" class="moreFacets tooltips" data-toggle="modal" title="View full list of values"><i class="icon-hand-right"></i> choose more...</a>
                            <div id="${fn}Modal" class="modal hide fade">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    <h3>Filter by ${fName}</h3>
                                </div>
                                <div class="modal-body">
                                    <ul class="facetValues">
                                        <g:each var="t" in="${f.terms}">
                                            <li data-sortalpha="${t.term.toLowerCase().trim()}" data-sortcount="${t.count}"><a href="${fqLink}&fq=${fn.encodeAsURL()}:${t.term.encodeAsURL()}"><g:message
                                                    code="label.${t.term}" default="${t.term?:'[empty]'}"/></a> (<span class="fcount">${t.count}</span>)
                                            </li>
                                        </g:each>
                                    </ul>
                                </div>
                                <div class="modal-footer">
                                    <div class="pull-left">
                                        <button class="btn btn-small sortAlpha"><i class="icon-filter"></i> Sort by name</button>
                                        <button class="btn btn-small sortCount"><i class="icon-filter"></i> Sort by count</button>
                                    </div>
                                    <a href="#" class="btn" data-dismiss="modal">Close</a>
                                </div>
                            </div>
                        </g:if>
                    </g:if>
                </g:each>
            </div>
        </div>
        <div class="span8">

            %{--<div class="tabbable">--}%
                %{--<ul class="nav nav-tabs" data-tabs="tabs">--}%
                    %{--<li class="active"><a id="mapView-tab" href="#mapView" data-toggle="tab">Map</a></li>--}%
                    %{--<li class=""><a id="projectsView-tab" href="#projectsView" data-toggle="tab">Projects</a></li>--}%
                %{--Temporarily hiding the reports from non-admin until they are ready for public consumption. --}%
                    %{--<g:if test="${fc.userIsSiteAdmin()}">--}%
                        %{--<li class=""><a id="reportView-tab" href="#reportView" data-toggle="tab">Dashboard</a></li>--}%
                    %{--</g:if>--}%
                %{--</ul>--}%
            %{--</div>--}%

            %{--<div class="tab-content clearfix">--}%
                %{--<div class="tab-pane active" id="mapView">--}%
                    %{--<div class="map-box">--}%
                        %{--<div id="map" style="width: 100%; height: 100%;"></div>--}%
                    %{--</div>--}%
                    %{--<div id="map-info">--}%
                        %{--<span id="numberOfProjects">${results?.hits?.total?:0 > 0}</span> projects with <span id="numberOfSites">[calculating]</span>--}%
                    %{--</div>--}%
                %{--</div>--}%

                <div class="tab-pane " id="projectsView">
                    <div class="scroll-list clearfix" id="projectList">
                        <table class="table table-bordered table-hover" id="projectTable" data-sort="lastUpdated" data-order="DESC" data-offset="0" data-max="10">
                            <thead>
                            <tr>
                                <th width="85%" data-sort="nameSort" data-order="ASC" class="header">Study name</th>
                            </tr>
                            <g:each var="study" in="${results.hits.hits}">
                                <tr>
                                    <td class="td1">
                                        <a href="#" class="projectTitle" id="a_" data-id="" title="click to show/hide details">
                                            <span class="showHideCaret">&#9658;</span> <span class="projectTitleName">${study.title}</span></a>
                                        <div class="show projectInfo" id="proj_$id">
                                            %{--<div class="homeLine">--}%
                                                %{--<i class="icon-home"></i>--}%
                                                %{--<a href="">Tree</a>--}%
                                            %{--</div>--}%
                                            %{--<div class="sitesLine">--}%
                                                %{--<i class="icon-map-marker"></i>--}%
                                                %{--Sites: <a href="#" data-id="$id" class="zoom-in btnX btn-miniX"><i--}%
                                                    %{--class="icon-plus-sign"></i> show on map</a>--}%
                                                %{--<a href="#" data-id="$id" class="zoom-out btnX btn-miniX"><i--}%
                                                        %{--class="icon-minus-sign"></i> zoom out</a>--}%
                                            %{--</div>--}%
                                            <div class="orgLine">
                                                <i class="icon-user"></i>
                                            <g:each var="tree" in="${study.trees}">
                                                <a href="${createLink( controller: 'phylo', action: 'create')}/1/${tree.id}/1">${tree.name}</a>
                                            </g:each>
                                            </div>
                                            <div class="descLine">
                                                <i class="icon-info-sign"></i>${study.citation}
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </g:each>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                        <div id="paginateTable" class="hide" style="text-align:center;">
                            <span id="paginationInfo" style="display:inline-block;float:left;margin-top:4px;"></span>
                            <div class="btn-group">
                                <button class="btn btn-small prev"><i class="icon-chevron-left"></i>&nbsp;previous</button>
                                <button class="btn btn-small next">next&nbsp;<i class="icon-chevron-right"></i></button>
                            </div>
                            <span id="project-filter-warning" class="label filter-label label-warning hide pull-left">Filtered</span>
                            <div class="control-group pull-right dataTables_filter">
                                <div class="input-append">
                                    <g:textField class="filterinput input-medium" data-target="project"
                                                 title="Type a few characters to restrict the list." name="projects"
                                                 placeholder="filter"
                                                 onfocus="utils.clearPlaceholder(this)"/>
                                    <button type="button" class="btn clearFilterBtn"
                                            title="clear"><i class="icon-remove"></i></button>
                                </div>
                            </div>
                        </div>
                    </div>
                     %{--template for jQuery DOM injection--}%
                    %{--<table id="projectRowTempl" class="hide">--}%
                        %{--<tr>--}%
                            %{--<td class="td1">--}%
                                %{--<a href="#" class="projectTitle" id="a_" data-id="" title="click to show/hide details">--}%
                                    %{--<span class="showHideCaret">&#9658;</span> <span class="projectTitleName">$name</span></a>--}%
                                %{--<div class="hide projectInfo" id="proj_$id">--}%
                                    %{--<div class="homeLine">--}%
                                        %{--<i class="icon-home"></i>--}%
                                        %{--<a href="">View project page</a>--}%
                                    %{--</div>--}%
                                    %{--<div class="sitesLine">--}%
                                        %{--<i class="icon-map-marker"></i>--}%
                                        %{--Sites: <a href="#" data-id="$id" class="zoom-in btnX btn-miniX"><i--}%
                                            %{--class="icon-plus-sign"></i> show on map</a>--}%
                                        %{--<a href="#" data-id="$id" class="zoom-out btnX btn-miniX"><i--}%
                                        %{--class="icon-minus-sign"></i> zoom out</a>--}%
                                    %{--</div>--}%
                                    %{--<div class="orgLine">--}%
                                        %{--<i class="icon-user"></i>--}%
                                    %{--</div>--}%
                                    %{--<div class="descLine">--}%
                                        %{--<i class="icon-info-sign"></i>--}%
                                    %{--</div>--}%
                                %{--</div>--}%
                            %{--</td>--}%
                            %{--<td class="td2">$date</td>--}%
                        %{--</tr>--}%
                    %{--</table>--}%
                </div>
            %{--Temporarily hiding the reports from non-admin until they are ready for public consumption. --}%
                %{--<g:if test="${fc.userIsSiteAdmin()}">--}%
                    %{--<div class="tab-pane" id="reportView">--}%
                        %{--<div class="loading-message">--}%
                            %{--<r:img dir="images" file="loading.gif" alt="saving icon"/> Loading report...--}%
                        %{--</div>--}%
                    %{--</div>--}%
                %{--</g:if>--}%
            %{--</div>--}%
            %{--<p>&nbsp;</p>--}%
        </div>
    </div>
</g:if>
%{--<script>--}%
    %{--$('.projectTitle').click( function(){--}%
        %{----}%
    %{--})--}%
%{--</script>--}%
</body>
</html>