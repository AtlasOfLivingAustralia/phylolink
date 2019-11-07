package au.org.ala.phyloviz

import grails.converters.JSON
import groovy.json.JsonBuilder
import org.apache.commons.lang3.StringUtils

import static org.springframework.http.HttpStatus.*
/**
 * Created by Temi Varghese on 19/06/2014.
 */
class PhyloController extends BaseController {
    def webServiceService;
    def utilsService
    def treeService
    def phyloService
    def authService
	def userService
	def doiService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phylo.list(params), model: [phyloInstanceCount: Phylo.count()]
    }

    def show(Phylo phyloInstance) {
		if (! phyloService.isAuthorisedToView(phyloInstance)) {
//            notAuthorised "Only the visualisation owner or an administrator can view an unapproved visualisation"			
            if (phyloService.isLoggedIn() || !phyloInstance) {
				notAuthorised "Only a logged on user can view a visualisation that is not a demonstration"
			} else {
				respond phyloInstance, model: [ showLoginToViewMsg: true ] 
			}
		} else {
			def tree = Tree.findById(phyloInstance.getStudyid());
			def userId = authService.getUserId();
			
			log.debug("In show, user id : ${userId}")
			log.debug("In show, owner id: ${phyloInstance.getOwner()?.userId}");
			log.debug("In show, source: ${phyloInstance.getSource()}");
			
			Boolean isDemonstration = phyloService.isDemonstrationViz(phyloInstance)
			Boolean isOwner = phyloService.isOwner(phyloInstance)
			Boolean edit = phyloService.isAuthorised(phyloInstance)
			Boolean userCanApprove = phyloService.isAuthorisedToApprove(phyloInstance)
			Boolean userCanReject = phyloService.isAuthorisedToReject(phyloInstance)
			Boolean userCanReinstate = phyloService.isAuthorisedToReinstate(phyloInstance)
			Boolean userIsWorkflowAdmin = phyloService.isAuthorisedWorkflowAdmin()
			Boolean showWorkflowStatusEntries = isOwner || userIsWorkflowAdmin
			Boolean isPublished = phyloService.isPublished(phyloInstance)

			log.debug("In show, edit: ${edit}, userCanApprove: ${userCanApprove}, userCanReject: ${userCanReject}, userCanReinstate: ${userCanReinstate}");
			
			phyloService.prepopulateEmptyFields(phyloInstance)
					
			respond phyloInstance, model: [ tree: tree, userId: userId, edit: edit, studyId: phyloInstance.getStudyid(), 
				phyloInstance: phyloInstance, isOwner: isOwner, isDemonstration: isDemonstration, userCanApprove: userCanApprove,
				userCanReject: userCanReject, userCanReinstate: userCanReinstate, showWorkflowStatusEntries: showWorkflowStatusEntries,
				userName: userService.getCurrentUserDisplayName(), licences: doiService.getLicences(), userIsWorkflowAdmin: userIsWorkflowAdmin,
				isPublished: isPublished, showLoginToViewMsg: false
				]
		}
    }

    def deleteViz() {
        if (!params.id) {
            badRequest "id is a required parameter"
        } else {
            Phylo phylo = Phylo.findById(params.id)
            if (!phylo) {
                notFound "No visualisation was found for id ${params.id}"
            } else if (!params.isAdmin && phylo.owner.id != treeService.getCurrentOwner().id) {
                notAuthorised "Only the visualisation owner or an administrator can delete this profile"
            } else {
                phyloService.deleteVisualisation(params.id as int)
                redirect controller: "wizard", action: "myViz"
            }
        }
    }

    def create() {
        respond new Phylo(params)
    }

    def save(Phylo phyloInstance) {
        log.debug('save ')
        log.debug( String.valueOf(params) )
        log.debug( String.valueOf(phyloInstance) )
        if (phyloInstance == null) {
            notFound()
            return
        }

        if (phyloInstance.hasErrors()) {
            respond phyloInstance.errors, view: 'create'
            return
        }

        phyloInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'phyloInstance.label', default: 'Phylo'), phyloInstance.id])
                redirect phyloInstance
            }
            '*' { respond phyloInstance, [status: CREATED] }
        }
    }

    def edit(Phylo phyloInstance) {
        respond phyloInstance
    }

    def update(Phylo phyloInstance) {
        if (phyloInstance == null) {
            notFound()
            return
        }

        if (phyloInstance.hasErrors()) {
            respond phyloInstance.errors, view: 'edit'
            return
        }

        phyloInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Phylo.label', default: 'Phylo'), phyloInstance.id])
                redirect phyloInstance
            }
            '*' { respond phyloInstance, [status: OK] }
        }
    }

    def delete(Phylo phyloInstance) {

        if (phyloInstance == null) {
            notFound()
            return
        }

        phyloInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Phylo.label', default: 'Phylo'), phyloInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    /**
     * INTERSECTION BETWEEN A LAYER AND SPECIES OCCURRENCE
     * @param phyloInstance
     * @return
     */
    def getHabitat() {

        def download = ( params.download?:false ) as Boolean
        def data = params
        data.region = '';
        def dr = ''

        def widgetFactory = new au.org.ala.phyloviz.WidgetFactory();
        def widgetObject = widgetFactory.createWidget(data, grailsApplication, webServiceService, utilsService, applicationContext, dr)

        data = widgetObject.process(data, null);
        data.statisticSummary = utilsService.statisticSummary(data.data, true)

        def filteredData = data.findAll { it.key }

        if (download) {
            response.setHeader('Content-disposition', 'attachment; filename=data.csv')
            render(contentType: 'text/plain', text: utilsService.convertJSONtoCSV(filteredData?.data))
        } else if (params.callback) {
            render(contentType: 'text/javascript', text: "${params.callback}(${filteredData as JSON})")
        } else {
            render(contentType: 'application/json', text: "${filteredData as JSON}")
        }
    }

    def toGoogleColumnChart( summary, layer ){
        def result = []
        summary = summary.sort{ it.key }
        if( layer.contains('el') ) {
            log.debug( 'parsing to double')
            summary.each() { k, v ->
                log.debug( String.valueOf(k) )
                result.push([ Double.parseDouble( k ), v]);
            }
        } else {
            summary.each() { k, v ->
                result.push([k, v]);
            }
        }
        if( result.size() != 0 ){
            result.add(0,   ['Character','Occurrences'])
        } else {
            result.push( ['Character','Occurrences'] );
            result.push( ['',0] );
        }
        return result;
    }

    def getRegions(){
        def regions =[], json;
        def regionsUrl = grailsApplication.config.regionsUrl;
        regionsUrl.each{ type, url->
            regions.addAll( this.getRegionsByType( type ) );
        }
        render( contentType: 'application/json', text: new JsonBuilder( regions ).toString() );

    }

    def getRegionsByType( String typeS ) {
        def regions = [], json;
        def regionsUrl = grailsApplication.config.regionsUrl[typeS];
        if( regionsUrl ) {
            json = JSON.parse(webServiceService.get( regionsUrl ) );
            for (name in json.names) {
                regions.push(["value": name, "type": typeS, "code":"${typeS}:${name}"]);
            }
        }
        return  regions;
    }

    def getPD( ){
        def treeId, studyId, tree, speciesList
        Boolean noTreeText = params.noTreeText?Boolean.parseBoolean(params.noTreeText):false;
        treeId = params.treeId?.toString()?:''
        studyId = params.studyId?.toString()?:''
        tree = params.newick?:'';
        speciesList = params.speciesList?:'[]'
        try {
            def result = treeService.getPDCalc(treeId, studyId, tree, speciesList)
            result = noTreeText ? treeService.removeProp(result, grailsApplication.config.treeMeta.treeText) : result
            render(contentType: 'application/json', text: result as JSON)
        } catch (Exception e){
            log.error( e.getMessage(), e)
        }
    }

    def getTree(){
        def studyId = params.studyId?.toString()
        def treeId = params.treeId?.toString()
        def noTreeText = params.noTreeText?:false;
        noTreeText = noTreeText.toBoolean()
        def meta = [:]
        meta = treeService.getTreeMeta(treeId, studyId, meta)
        meta = noTreeText? treeService.removeProp( meta , grailsApplication.config.treeMeta.treeText ): meta ;
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${meta as JSON})")
        } else {
            render(contentType: 'application/json', text: meta as JSON)
        }
    }

    def params(){
        def url = 'http://biocache.ala.org.au/ws/mapping/params'
        def ret = webServiceService.postData(url, [fq:params.fq])
        render ( text: ret.toString() )
    }

    /**
     * webservice that returns trees recommended by experts
     * @return a json
     * {
     *  'expertTrees':[{},{}]
     * }
     */
    def getExpertTrees(){
        Boolean noTreeText = params.noTreeText?Boolean.parseBoolean(params.noTreeText):false;
        def result = treeService.getExpertTrees(noTreeText);
        render ( contentType: 'application/json', text: result as JSON)
    }

    /**
     * returns all studies and associated metadata
     * @param
     */
    def getStudies(){
        def meta = [:]
        def result = this.getStudiesMeta( meta );
        render ('contentType':'application/json', text: result as JSON )
    }

    def download(){
        def download =  JSON.parse( params.json )
        response.setHeader('Content-disposition','attachment; filename=data.csv')
        render ( contentType: 'text/plain', text: utilsService.convertJSONtoCSV( download ) )
    }

    /**
     * save the habitats json string into database
     * @param phyloInstance
     * @return
     */
    def saveHabitat(Phylo phyloInstance){
        if(!phyloService.isAuthorised(phyloInstance)){
            notAuthorised "Only owner can edit this visualisation";
        }

        String habInit = params.json
        log.debug("In saveHabitat about to setHabitat to ${habInit}");
        phyloInstance.setHabitat(JSON.parse(habInit).toString());
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
    }

    /**
     * save the characters/traits json string into database
     * @param phyloInstance
     * @return
     */
    def saveCharacters(Phylo phyloInstance){
        if(!phyloService.isAuthorised(phyloInstance)){
            notAuthorised "Only owner can edit this visualisation";
        }

        String charInit = params.json
        log.debug("In saveCharacters about to setCharacters to ${charInit}");
        phyloInstance.setCharacters(JSON.parse(charInit).toString());
        phyloInstance.setCharacterSource(null);
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
    }

    /**
     * save the reference to the character traits data set into the database
     * @param phyloInstance
     * @return
     */
    def saveCharacterSource(Phylo phyloInstance){
        if(!phyloService.isAuthorised(phyloInstance)){
            notAuthorised "Only owner can edit this visualisation";
        }

        String id = params.characterSourceId
        log.debug("In saveCharacterSource about to setSource to '${id}'");
		
		Characters characters = (StringUtils.isNotBlank(id) ? Characters.findById(id) : null);
		if (StringUtils.isNotBlank(id) && characters == null) {
			throw new IllegalArgumentException("In saveCharacterSource unknown characterSourceId '" + id + "'");
		}
        if (characters != null) {
			phyloInstance.setCharacters(null);
		}
        phyloInstance.setCharacterSource(characters);
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
    }

    /**
     * save the reference to the occurrence distribution into the database
     * @param phyloInstance
     * @return
     */
    def saveSource(Phylo phyloInstance){
        if(!phyloService.isAuthorised(phyloInstance)){
            notAuthorised "Only owner can edit this visualisation";
        }

        String id = params.sourceId
        log.debug("In saveSource about to setSource to '${id}'");
        phyloInstance.setSource(id);
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
    }

    /**
     * save the visualisation metadata to the database
     * @param phyloInstance
     * @return
     */
    def saveExpertVisualisation(Phylo phyloInstance){
		String responseMessage = "";
		String processDescription = "save metadata";
		try {
			phyloService.saveMetadata(phyloInstance, params)
		
			responseMessage = "Successfully saved metadata."
		
		} catch (Exception e) {
			log.error("Exception while trying to " + processDescription + " for Phylo with Id=" + phyloInstance?.getId(), e);
			return renderJSONError(responseMessage + " Failed to " + processDescription + ".");
		}
		
		Map response = [
			message: responseMessage
		]
		
        success(response);
    }
	
    /**
     * change the workflow status of the visualisation
     * @param phyloInstance
     * @return
     */
    def changeWorkflowStatus(Phylo phyloInstance){
		String visualisationURL = grailsApplication.config.serverURL + 
				createLink(controller: 'phylo', action: 'show', params: [id: phyloInstance.id])

		Map response = phyloService.changeWorkflowStatus(phyloInstance, params, visualisationURL)
		
		if (response.containsKey('userCanApprove')) {
			success(response);
		} else {
			renderJSONError(response.get('responseMessage'))
		}
    }
	
    /**
     * start page for phylolink
     */
    def startPage(){
        render(view: '/index',model:[ demoId: phyloService.getDemoId() ]);
    }

    def downloadMapData() {
        if (!params.qid) {
            badRequest "qid is a required parameter"
        } else {
            def data = webServiceService.get("${params.biocacheServiceUrl}?q=qid:${params.qid}")

            response.setHeader('Content-disposition', 'attachment; filename=data.csv')
            render(contentType: 'text/plain', text: data)
        }
    }

    /**
     * save pj settings to the database. currently only saving clicked node id.
     */
    def savePjSettings(Phylo phyloInstance){
        if(!phyloService.isAuthorised(phyloInstance)){
            notAuthorised "Only owner can edit this visualisation";
        }

        String pjSetting = params.json
        log.debug(pjSetting);
        phyloInstance.setPjSettings(JSON.parse(pjSetting).toString());
        phyloInstance.save(flush: true);
        if(!phyloInstance.hasErrors()){
            render(contentType: 'application/json',text: ["message":"success"] as JSON);
        } else {
            render(contentType: 'application/json',text: ["message":"failed"] as JSON, status: INTERNAL_SERVER_ERROR.value());
        }
    }
}