<%--
 Created by Temi Varghese on 27/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Expert Trees</title>
</head>

<body>

<div class="container">
    <legend>Choose from a list of expert recommended trees</legend>
    <table class="table table-hover table-bordered">
        <thead>
        <tr>
            <th>Tree name</th>
            <th>Species covered</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${trees}" var="tree" status="i">
            <tr>
                <td class="span6">
                    <div>
                        <div class="btn btn-link"  onclick="showInfo(${i})">
                            ${tree.getTitle()}
                            <i class="icon-info-sign" title="Show more information"></i>
                        </div>
                    </div>
                </td>
                <td><a
                        href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                        target="_blank">${tree.getExpertTreeTaxonomy()}</a></td>
                <td style="justify: center">
                    <div class="btn btn-small" onclick="window.location = '${createLink( action: 'visualize')}?id=${tree.getId()}'"><i class="icon-ok"></i> Open</div>
                </td>
            </tr>
            <tr>
                <td colspan="3" style="display: none" class="info" id="info-${i}">
                    <div class="control-group" style="display: block"></div>
                %{--<label>Citation:</label>--}%<i>${tree.getReference()}</i>
                    <g:if test="${tree.doi}">
                        <div class="">
                            %{--<label>Doi:</label>--}%
                            <a href="${tree.getDoi()}">${tree.getDoi()}</a>
                        </div>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
    <script>
        function showInfo(i){
//            $('.info').hide({
//                animate:'slow'
//            })
            $('#info-'+i).toggle({
                animate:'slow'
            })
        }
    </script>
</div>
</body>
</html>