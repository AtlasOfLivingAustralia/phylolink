<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>PhyloViz | Atlas of Living Australia</title>
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'index.css')}"/>
        <style type="text/css">
        table#trees td, table#trees th {
            border-left:solid 40px transparent;
        }
        table#trees td:first-child, table#trees th:first-child {
            border-left:0;
        }
        </style>
	</head>
	<body>
    <div id="content">
        <header id="page-header">
            <div class="inner">
                <nav id="breadcrumb">
                    <ol class="breadcrumb">
                        <li><a href="http://www.ala.org.au">Home</a> <span class=" icon icon-arrow-right"></span></li>
                        %{--<li><a href="#">Second breadcrumb (change me)</a></li>--}%
                        <li class="last">PhyloLink</li>
                    </ol>
                </nav>
                <h1>Phylo Link</h1>

            </div><!--inner-->
        </header>
    %{--# JSON support for older browsers (esp. IE7)--}%
    <script type="text/javascript" src="${resource(dir: 'js', file: 'json2.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-2.3.0.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-paged-e4a5770702.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-bootstrap.min.js')}"></script>


        <script type='text/javascript'>
//        var viewOrEdit = '{{= viewOrEdit }}';
        var viewOrEdit = 'VIEW';
        var findAllStudies_url = "http://115.146.93.110:7478/db/data/ext/QueryServices/graphdb/findAllStudies";
        var phylesystem_config_url = '{{=phylesystem_config_url}}';
    </script>

    <script type="text/javascript" src="${resource(dir: 'js', file: 'curation-helpers.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'curation-dashboard.js')}"></script>

    %{--<div class="row">--}%
        %{--<div class="span12">--}%
            %{--{{if 'message' in globals():}}--}%
            %{--<h4>{{=message}}</h4>--}%
            %{--{{pass}}--}%
        %{--</div>--}%
    %{--</div><!-- /.row -->--}%

    <div class="row"  Xstyle="position: relative; top: -1em;">
        <div class="span12"><!-- full width... -->

            <a name="new-study-submit" class="btn btn-info" style="margin-bottom: 1em;" href="/curator/study/create">Add new study
                <i class="icon-circle-arrow-up Xicon-plusXicon-upload icon-white"></i></a>

            <div class="navbar" style="clear: both;">
                <div class="navbar-inner">
                    <form class="navbar-search pull-left">
                        <input type="text" id="study-list-filter" class="search-query" style="width: 290px;"
                               placeholder="Filter by reference text, DOI, tag, curator&hellip;"
                               data-bind="value: viewModel.listFilters.STUDIES.match, valueUpdate: ['afterkeydown', 'input']">
                    </form>
                    <ul class="nav" style="padding-left: 1em;">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <span data-bind="text: viewModel.listFilters.STUDIES.workflow">WORKFLOW</span>
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu" data-bind="foreach: ['Any workflow state', 'Draft study', 'Submitted for synthesis', 'Under revision', 'Included in synthetic tree']">
                                <li data-bind="css: {'disabled': viewModel.listFilters.STUDIES.workflow() == $data }">
                                    <a href="#" data-bind="text: $data, click: function () { viewModel.listFilters.STUDIES.workflow($data); }">WORKFLOW</a>
                                </li>
                            </ul>
                        </li>
                        <!--<li class="divider-vertical"></li>-->
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <span data-bind="text: viewModel.listFilters.STUDIES.order">SORT</span>
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu" data-bind="foreach: ['Newest publication first', 'Oldest publication first'] "><!-- , 'Workflow state', 'Completeness'] -->
                                <li data-bind="css: {'disabled': viewModel.listFilters.STUDIES.order() == $data }">
                                    <a href="#" data-bind="text: $data, click: function () { viewModel.listFilters.STUDIES.order($data); }">SORT</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>

            <table class="table table-condensed">
                <thead>
                <tr>
                    <th>Reference (click to view study)</th>
                    <th>Tree id</th>
                    <th>Focal clade</th>
                    <th>Curator</th>
                    <!--
                <th style="text-align: right;">Publication</th>
                <th>Completeness</th>
                <th>Year</th>
                <th>Journal</th>
                <th>Actions</th>
                -->
                </tr>
                </thead>
                <tbody data-bind="foreach: { data: viewModel.filteredStudies().pagedItems(), as: 'study' }">
                <tr >
                    <td data-bind="html: getViewOrEditLinks(study)">&nbsp;</td>
                    <td data-bind="html: createViz( study.matched_trees, '${createLink(controller:"phylo", action:"create")}'  )">&nbsp;</td>
                    <td data-bind="html: getFocalCladeLink(study)">&nbsp;</td>
                    <td data-bind="html: getCuratorLink(study)">&nbsp;</td>
                    <td data-bind="text: study.completeness">&nbsp;</td>
                    <!--
                <td data-bind="text: study.is_deprecated">?</td>
                <td data-bind="text: study.pubYear">?</td>
                <td data-bind="html: getJournalLink(study)">?</td>
                <td data-bind="html: getSuggestedActions(study)">?</td>
                -->
                </tr>
                <tr>
                    <td colspan="4" style="border-top: none; padding-top: 0px;">
                        <div class="full-study-ref" style="display: none; overflow: visible;">
                            <div Xclass=""
                                 data-bind="html: study['ot:studyPublicationReference']">&nbsp;</div>
                            <div style="Xfont-size: 90%; margin-top: 3px;" data-bind="html: getPubLink(study)">&nbsp;</div>

                            <label style="display: inline-block" data-bind="visible: (study['ot:tag'])">Tags&nbsp;</label>
                            <div class="bootstrap-tagsinput"
                                 style="display: inline-block; border: none; margin-bottom: 0; padding: 0 0 4px 0;"
                                 data-bind="visible: study['ot:tag'] !== ''">
                                <!-- ko foreach: makeArray(study['ot:tag']) -->
                                <a class="tag label label-info" style="display: none;"
                                   href="#" data-bind="text: $data, visible: true"
                                   onclick="filterByTag($(this).text()); return false;">&nbsp;</a>
                                <!-- /ko -->
                                <span style="color: #999; display: none;" data-bind="visible: !(study['ot:tag'])">
                                    This study has not been tagged.
                                </span>
                            </div>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <div class="pagination pagination-centered pagination-small"
                 Xdata-bind="if: viewModel.filteredStudies()().length &gt; viewModel.filteredStudies().pagedItems().length">
                <ul>
                    <li data-bind="css: { 'disabled': !viewModel.filteredStudies().prev.enabled() }" class="disabled">
                        <a href="#" onclick="viewModel.filteredStudies().prev(); return false;">&laquo;</a>
                    </li>
                    <!-- ko foreach: getPageNumbers( viewModel.filteredStudies() ) -->
                    <!-- ko if: isVisiblePage( $data, viewModel.filteredStudies() ) -->
                    <li data-bind="css: { 'active': (viewModel.filteredStudies().current() === $data) }" class="active">
                        <a href="#" data-bind="text: $data, click: function() { viewModel.filteredStudies().goToPage($data); return false; }">0</a>
                    </li>
                    <!-- /ko -->
                    <!-- ko if: ! isVisiblePage( $data, viewModel.filteredStudies() ) -->
                    <li><a class="pagination-spacer" href="#" data-bind="click: function() { viewModel.filteredStudies().goToPage($data); return false; }">&nbsp;</a></li>
                    <!-- /ko -->
                    <!-- /ko -->
                    <li data-bind="css: { 'disabled': !viewModel.filteredStudies().next.enabled() }" class="disabled">
                        <a href="#" onclick="viewModel.filteredStudies().next(); return false;">&raquo;</a>
                    </li>
                </ul>
            </div>

        </div><!-- /div.spanX -->
    </div><!-- /.row -->

    %{--{{block right_sidebar}}--}%
    %{--{{=A(T("Administrative Interface"), _href=URL('admin','default','index'), _class='btn',--}%
    %{--_style='margin-top: 1em;')}}--}%
    %{--<h6>{{=T("Don't know what to do?")}}</h6>--}%
    %{--<ul>--}%
        %{--<li>{{=A(T("Online examples"), _href=URL('examples','default','index'))}}</li>--}%
        %{--<li><a href="http://web2py.com">web2py.com</a></li>--}%
        %{--<li><a href="http://web2py.com/book">{{=T('Documentation')}}</a></li>--}%
    %{--</ul>--}%
    %{--{{end}}--}%

    %{--<p>--}%
            %{--<button id="newTreeButton" class="btn btn-small" onclick="location.href='${createLink(controller:"phylo", action:"create")}'">Create a new tree</button>--}%
        %{--</p>--}%
    </div> <!-- content div -->
	</body>
</html>