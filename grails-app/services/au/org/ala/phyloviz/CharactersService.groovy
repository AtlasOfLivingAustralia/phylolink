package au.org.ala.phyloviz

import grails.transaction.Transactional

@Transactional
class CharactersService {
    def alaService
    def g
    def getCharUrl( ArrayList<Characters> lists ) {
        def result=[]
        lists.each{ list->
            result.push([
                'url': getUrl(list.drid),
                'title':list.title,
                'id': list.id
            ]);
        }
        return result
    }
    /**
     *
     * @param druid
     * @return
     */
    def getUrl(druid){
        if(!g) {
            g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
        }
        return g.createLink(controller: 'ala', action: 'getCharJson') + '?drid=' + druid;
    }

}
