package au.org.ala.phyloviz

import grails.converters.JSON
import org.springframework.web.multipart.MultipartHttpServletRequest

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

        List characterLists = charactersService.getCharacterListsByOwner(params.initCharacterResourceId)		
        List result = treeService.filterCharacterListsByTree(Integer.parseInt(params.treeId), characterLists)

        if (params.callback) {
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

	def isResourceCompatibleWithTree() {
        def characters = Characters.get(params.id)
        List characterLists = new ArrayList(1)
		characterLists.add(characters)		
        List list = treeService.filterCharacterListsByTree(Integer.parseInt(params.treeId), charactersService.getCharUrl(characterLists))

        def result = [
            isCompatibleWithTree: (list.size() > 0)
        ]

        if (params.callback) {
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }		
	}
	
    def deleteResource(){
        def alaId = authService.getUserId()
        def characters = Characters.get(params.id)

        log.debug("In deleteResource id=${params.id}, characters=${characters}")
		
        if (characters && characters.owner.getUserId().toString() == alaId && ! Boolean.FALSE.equals(characters.canDelete)){
			log.debug("In deleteResource about to delete")
            Object eUresult = Phylo.executeUpdate("update Phylo p set p.characterSource = null where p.characterSource=:characters",
                      [characters: characters])
			log.debug("In deleteResource executeUpdate returned=${eUresult}")
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

    /**
     * save uploaded images
     */
    def saveImages(){
	
        def id
        def imageFiles = isMultipartRequest() ? request.getFile('imagesFiles[]') : null;
		def result = new ArrayList();
		for (Object file : imageFiles) {
			def imageResult = [ : ]
			imageResult['fileName'] = file.originalFilename
			imageResult['id'] = charactersService.saveImage(file.originalFilename, file.bytes)
			imageResult['url'] = createLink()  // TODO Jasen
			result.add(imageResult)
		}	

//TODO Jasen - ran out of time to finish this		
//            result['error'] = 'error executing function';
//            response.sendError(400)
//        }

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

    private boolean isMultipartRequest() {
        request instanceof MultipartHttpServletRequest
	}
}
