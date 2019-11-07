<html>
<body>
    <p>An unexpected error occurred after successfully generating the DOI ${doi}.</p>
    <p>The DOI was generated, but the server failed to save to the local DB. No default landing page will exist for this DOI!</p>
    <g:renderErrors bean="${error}" />
    <p>This is an automated email. Please do not reply.</p>
</body>
</html>