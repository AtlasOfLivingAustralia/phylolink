<%--
 Created by Temi Varghese on 1/12/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Search Treebase</title>
    <meta name="layout" content="main"/>
    %{--<script type="text/javascript" src="${createLinkTo(dir: 'js', file: 'knockout-2.3.0.js')}"/>--}%
    <r:require module="knockout"/>
    <r:require modules="bugherd"/>
</head>

<body>
<div class="container">
    <span class="nav-collapse collapse">
        %{--Search TreeBASE--}%
        <form class="form-search form-inline">
            %{--<label class="control-label" style="font-size: 16px">Search DB</label>--}%
            <div class="input-append">
                <input id="searchTB" type="text" class="span2 search-query" placeholder="Search TreeBASE">
                <button type="submit" class="btn" onclick="search(); return false;"><i class="icon-search"></i></button>
            </div>
        </form>
        <hr/>
    </span>
    %{--<pre data-bind="text: ko.toJSON($data, null, 2)"></pre>--}%
    <table class="table table-hover table-bordered">
        <thead>
        <tr>
            <th>Publication details</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: results">
        <tr>
            <td class="span10">
                <div>
                    <div class="btn btn-link" data-bind="click: showInfo, attr: {index: $index}">
                        <span data-bind="text: title"></span>
                        <i class="icon-info-sign" title="Show more information"></i>
                    </div>
                </div>
            </td>
            <td class="span2">
                <form action="${createLink(controller: 'wizard', action: 'importTB')}" method="POST">
                    <input type="hidden" name="reference" data-bind="value: reference"/>
                    <input type="hidden" name="url" data-bind="value: url"/>
                    <input type="hidden" name="title" data-bind="value: title"/>
                    <input type="hidden" name="year" data-bind="value: year"/>
                    <button class="btn btn-small">
                        <i class="icon-download-alt"></i> Import</button>
                </form>
            </td>
        </tr>
        <tr>
            <td colspan="2" style="display: none" class="info" data-bind="attr: {id: 'info-'+$index()}">
                <div class="control-group" style="display: block"></div>
                <i data-bind="text: reference"></i>
                <div class="">
                    <a target="_blank" href="" data-bind="attr:{href: doiUrl},text: doi"></a>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
</div>
<script>
    function SearchResult(m) {
        this.title = m.title;
        this.url = m.url;
        this.description = m.description;
        this.reference = m.reference;
        this.year = m.year;
        this.doi = m.doi;
        this.publication = m.publication;
        this.doiUrl = m.doiUrl;
    }

    function SearchViewModel() {
        var that = this;
        this.results = ko.observableArray(
                [
                ]
        )
        this.add = function (studies) {
            var i;
            that.results.removeAll();
            for (i = 0; i < studies.length; i++) {
                that.results.push(new SearchResult(studies[i]))
            }
        }
    }

    view = new SearchViewModel()
    ko.applyBindings(view);

    function showInfo(data , event) {
        console.log ( data )
        var i = event.currentTarget.getAttribute('index')

        $('#info-' + i).toggle({
            animate: 'slow'
        })
    }

    function search(){
        var q = $("#searchTB").attr('value');
        $.ajax({
            url: "${createLink( controller: 'tree', action: 'searchTreebase')}",
            data: {
                q: q
            },
            success:function(data){
                view.add(data)
            }
        })
    }

    function importTB( data, event){
        $.ajax({
            url:"${createLink(controller: 'wizard', action: 'importTB')}",
            type:'POST',
            data: data
        });
    }
</script>

</body>
</html>