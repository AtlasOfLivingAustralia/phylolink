package au.org.ala.phyloviz

import grails.converters.JSON;

class SandboxController {
    def authService;
    def sandboxService

    def listDatasets(){
        def alaId = authService.getUserId();
        def result = sandboxService.findListByAlaId(alaId);

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render( text: result as JSON, contentType: 'application/json');
        }
    }

    def checkStatus(){
        def uid = params.uid;
        def msg = sandboxService.checkStatus(uid);
        response.setContentType('application/json');
        render(msg)
    }

    /**
     * pass a dataresource id and get its details
     */
    def dataresourceInfo(){
        def owner = authService.getUserId();
        def druid = params.druid;
        def source = grailsApplication.config.sandboxUrl;
        def result = sandboxService.getDataresourceInfo(druid, owner, source);
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render( text: result as JSON, contentType: 'application/json');
        }
    }

    /**
     * get all dataresources uploaded by this user
     */
    def allDataresourceInfo(){
        def userId = authService.getUserId();
        def result = sandboxService.getAllDataresourceInfo(userId);
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render( text: result as JSON, contentType: 'application/json');
        }
    }
}
