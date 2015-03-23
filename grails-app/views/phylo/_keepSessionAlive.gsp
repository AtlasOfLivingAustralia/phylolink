<g:form name="keepAlive" method="POST" action="${createLink(controller: 'ala', action: 'keepSessionAlive')}">
    <g:field name="keepAliveHidden" type="hidden" value="1"/>
</g:form>
<script>
    setInterval(function(){
       var form = $('#keepAlive');
        $.ajax({
            url:"${createLink(controller: 'ala', action: 'keepSessionAlive')}",
            data:form.serialize(),
            type:'POST',
            dataType:'JSON',
            success:function(){

            }
        })
    },300000);
</script>