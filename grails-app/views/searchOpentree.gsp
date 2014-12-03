<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>PhyloLink | Atlas of Living Australia</title>
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'index.css')}"/>
        <style type="text/css">
        table#trees td, table#trees th {
            border-left:solid 40px transparent;
        }
        table#trees td:first-child, table#trees th:first-child {
            border-left:0;
        }
        </style>
        <r:require modules="application"/>
	</head>
	<body>
    <div id="content">
    %{--# JSON support for older browsers (esp. IE7)--}%
    <script type="text/javascript" src="${resource(dir: 'js', file: 'json2.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-2.3.0.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-paged-e4a5770702.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'knockout-bootstrap.min.js')}"></script>

    <script type='text/javascript'>
        var viewOrEdit = 'VIEW';
        var findAllStudies_url = "${createLink(controller: "OTStudy", action: 'listStudies')}";
        var searchTreeUrl = findAllStudies_url;
        var phylesystem_config_url = '{{=phylesystem_config_url}}';
    </script>

    <script type="text/javascript" src="${resource(dir: 'js', file: 'curation-helpers.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'curation-dashboard.js')}"></script>
    <span style="width:100%">  <input id="nodename" type="text" value="" placeholder="Search for trees with name"/> <a style="margin-bottom: 10px" class="btn" onclick="searchForTrees()">Search</a></span>
    <div class="row"  Xstyle="position: relative; top: -1em;">
        <div class="span12"><!-- full width... -->
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
                    <th>Focal clade</th>
                    <th>Curator</th>
                    <th>Trees</th>
                </tr>
                </thead>
                <tbody data-bind="foreach: { data: viewModel.filteredStudies().pagedItems(), as: 'study' }">
                <tr >
                    <td data-bind="html: getViewOrEditLinks(study)">&nbsp;</td>
                    <td data-bind="html: getFocalCladeLink(study)">&nbsp;</td>
                    <td data-bind="html: getCuratorLink(study)">&nbsp;</td>
                    <td data-bind="html: createViz( study.trees, '${createLink(controller:"phylo", action:"create")}'  )">&nbsp;</td>
                </tr>
                <tr>
                    <td colspan="4" style="border-top: none; padding-top: 0px;">
                        <div class="full-study-ref" style="display: none; overflow: visible;">
                            <div Xclass=""
                                 data-bind="html: study['studyName']">&nbsp;</div>
                            <div style="Xfont-size: 90%; margin-top: 3px;" data-bind="html: getPubLink(study)">&nbsp;</div>

                            <label style="display: inline-block" data-bind="visible: (study['tag'])">Tags&nbsp;</label>
                            <div class="bootstrap-tagsinput"
                                 style="display: inline-block; border: none; margin-bottom: 0; padding: 0 0 4px 0;"
                                 data-bind="visible: study['tag'] !== ''">
                                <!-- ko foreach: makeArray(study['tag']) -->
                                <a class="tag label label-info" style="display: none;"
                                   href="#" data-bind="text: $data, visible: true"
                                   onclick="filterByTag($(this).text()); return false;">&nbsp;</a>
                                <!-- /ko -->
                                <span style="color: #999; display: none;" data-bind="visible: !(study['tag'])">
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
                    <li data-bind="css: { 'active': (viewModel.filteredStudies().current() === $data) }" class="active">
                        <a href="#" data-bind="text: $data, click: function() { viewModel.filteredStudies().goToPage($data); return false; }">0</a>
                    </li>
                    <li><a class="pagination-spacer" href="#" data-bind="click: function() { viewModel.filteredStudies().goToPage($data); return false; }">&nbsp;</a></li>
                    <li data-bind="css: { 'disabled': !viewModel.filteredStudies().next.enabled() }" class="disabled">
                        <a href="#" onclick="viewModel.filteredStudies().next(); return false;">&raquo;</a>
                    </li>
                </ul>
            </div>

        </div><!-- /div.spanX -->
    </div><!-- /.row -->
    </div> <!-- content div -->
	</body>
</html>