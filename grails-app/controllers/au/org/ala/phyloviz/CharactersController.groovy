package au.org.ala.phyloviz

import grails.converters.JSON

class CharactersController {

    TreeService treeService
    def charactersService
    def alaService
    def authService

    /**
     *
     * @return
     */
    def list() {

        List characterLists = charactersService.getCharacterListsByOwner()
        List result = treeService.filterCharacterListsByTree(Integer.parseInt(params.treeId), characterLists)

        if (params.callback) {
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

    def deleteResource(){
        def alaId = authService.getUserId()
        def characters = Characters.get(params.id)

        if (characters && characters.owner.getUserId().toString() == alaId){
            characters.delete(flush:true)
            def result = [success:true]
            render( text: result as JSON, contentType: 'application/json')
        } else {
            def result = [success:false]
            render( text: result as JSON, contentType: 'application/json')
        }
    }

    /**
     *
     * @param druid
     * @return
     */
    def getUrl(druid){
        return createLink(controller: 'ala', action: 'getCharJson') + '?drid=' + druid;
    }

    /**
     *
     */
    def getKeys(){
        def drid = params.drid;
        def result = charactersService.getKeys( drid );
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render  result as JSON;
        }
    }

    /**
     * converts character data stored in list tool into charJSON
     */
    def getCharJsonForKeys(){
        String drid = params.drid;
        String cookie = request.getHeader('Cookie')
        String keys = params.keys
        log.debug(drid)
        def result
        if(drid){
            result = alaService.getCharJsonForKeys(drid, cookie, keys);
        }

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }
}
