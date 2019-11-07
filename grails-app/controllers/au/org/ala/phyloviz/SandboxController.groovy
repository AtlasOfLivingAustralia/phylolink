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

    def deleteResource(){
        def alaId = authService.getUserId()
        def sandbox = Sandbox.get(params.id)
		
        log.debug("In deleteResource id=${params.id}, sandbox=${sandbox}")
		
        if(sandbox && sandbox.owner.getUserId().toString() == alaId && ! Boolean.FALSE.equals(sandbox.canDelete)){
			log.debug("In deleteResource about to delete")
            Object eUresult = Phylo.executeUpdate("update Phylo p set p.source = null where p.source=:source",
                      [source: String.valueOf(params.id)])
			log.debug("In deleteResource executeUpdate returned=${eUresult}")
            sandbox.delete(flush:true)
            def result = [success:true]
            render( text: result as JSON, contentType: 'application/json')
        } else {
            def result = [success:false]
            render( text: result as JSON, contentType: 'application/json')
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
        def phyloId = params.phyloId;
        def biocacheServiceUrl = grailsApplication.config.sandboxBiocacheServiceUrl;
        def biocacheHubUrl = grailsApplication.config.sandboxHubUrl;
        def result = sandboxService.getDataresourceInfo(druid, owner, biocacheServiceUrl, biocacheHubUrl, phyloId);
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
        if (userId == null) {
            result.addAll(sandboxService.getAllDataresourceInfoByPhyloId(params?.phyloId))
        }
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render( text: result as JSON, contentType: 'application/json');
        }
    }
}
