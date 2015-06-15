package au.org.ala.phyloviz

import grails.converters.JSON

class CharactersController {

    def userService
    def charactersService
    def alaService
    def authService

    /**
     *
     * @return
     */
    def list(){
        def id = authService.getUserId()
        log.debug(id)
        def owner = Owner.findByUserId(id)
        def lists = Characters.findAllByOwner(owner);
        def slist = Characters.findAllByOwner(Owner.findByUserId(1));
        lists.addAll(slist);
        log.debug(lists)
        def result = charactersService.getCharUrl(lists);

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
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
