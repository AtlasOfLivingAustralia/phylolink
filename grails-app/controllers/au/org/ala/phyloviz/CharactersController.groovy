package au.org.ala.phyloviz

import grails.converters.JSON

class CharactersController {

    def userService
    def charactersService

    /**
     *
     * @return
     */
    def list(){
        def id = userService.getCurrentUserId()
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
}
