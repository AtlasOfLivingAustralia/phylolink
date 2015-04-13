package au.org.ala.phyloviz

import grails.converters.JSON

class RestrictedmethodsController {

    def index() {}
    def treeService

    /**
     * save visualization title to database
     * @param phyloInstance
     * @return
     */
    def saveTitle(Phylo phyloInstance){
        def result = treeService.saveTitle(phyloInstance, params.title);
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})");
        } else {
            render(contentType: 'application/json', text: result as JSON);
        }
    }
}
