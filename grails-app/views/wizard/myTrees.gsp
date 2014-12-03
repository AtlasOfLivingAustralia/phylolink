<%--
 Created by Temi Varghese on 28/11/2014.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${name} trees</title>
</head>

<body>
<div class="container">
    <g:if test="${flash.message}">
        <div class="message alert-info" role="status">${flash.message}</div>
    </g:if>
    <legend>${name} trees</legend>
    <g:if test="${trees.size() != 0}">
        <table class="table table-hover table-bordered">
            <thead>
            <tr>
                <th>Tree name</th>
                <th>Species covered</th>
                <th>Choose</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${trees}" var="tree" status="i">
                <tr>
                    <td class="span6">
                        <div>
                            <div class="btn btn-link" onclick="showInfo(${i})">
                                ${tree.getTitle()}
                                <i class="icon-info-sign"  title="Show more information"></i>
                            </div>
                        </div>
                    </td>
                    <td><a
                            href="http://bie.ala.org.au/species/${tree.expertTreeLSID}"
                            target="_blank">${tree.getExpertTreeTaxonomy()}</a>
                    </td>
                    <td style="justify: center">
                        <div class="btn btn-small" onclick="window.location =
                                '${createLink( action: 'visualize')}?id=${tree.getId()}'">
                            <i class="icon-ok"></i> Open</div>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" style="display: none" class="info" id="info-${i}">
                        <div class="control-group" style="display: block"></div>
                        <i>${tree.getReference()}</i>
                        <g:if test="${tree.doi}">
                            <div class="">
                                <a href="${tree.getDoi()}">${tree.getDoi()}</a>
                            </div>
                        </g:if>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
        <div>
        </div>
    </g:else>
    <div name="back" class="btn" onclick="window.location = '${createLink(controller: 'wizard',action: 'start')}'"><i
            class="icon icon-arrow-left"></i> Back</div>
    <script>
        function showInfo(i) {
            $('.info').hide({
                animate: 'slow'
            })
            $('#info-' + i).show({
                animate: 'slow'
            })
        }
    </script>
</div>
</body>
</html>