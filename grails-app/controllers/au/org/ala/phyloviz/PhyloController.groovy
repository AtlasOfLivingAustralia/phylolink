package au.org.ala.phyloviz
import grails.converters.JSON
import groovy.json.JsonBuilder

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.springframework.http.HttpStatus.*
/**
 * Created by Temi Varghese on 19/06/2014.
 */
class PhyloController extends BaseController {
    def webService;
    def utilsService
    def userService
    def treeService
    def phyloService
    def authService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phylo.list(params), model: [phyloInstanceCount: Phylo.count()]
    }

    def show(Phylo phyloInstance) {
        def tree = Tree.findById(phyloInstance.getStudyid());
//        def user = userService.getUser();
//        log.debug("current user: "+user);
        def userId = authService.getUserId();
        if(userId != ""){
            userId = userId instanceof String?Long.parseLong(userId):userId;
        }

        Boolean edit = false
        log.debug("user id : ${userId instanceof String}")
        log.debug("owner id: ${phyloInstance.getOwner()?.userId}");
        if( phyloInstance.getOwner()?.userId == userId && userId != null){
            edit = true
            log.debug('editable');
        }

        respond phyloInstance, model: [ tree: tree, userId: userId, edit: edit, studyId: phyloInstance.getStudyid()]
    }

    def create() {
        respond new Phylo(params)
    }

    def save(Phylo phyloInstance) {
        log.debug('save ')
        log.debug( params )
        log.debug( phyloInstance )
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

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'phyloInstance.label', default: 'Phylo'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * INTERSECTION BETWEEN A LAYER AND SPECIES OCCURRENCE
     * @param phyloInstance
     * @return
     */
    def getHabitat() {
        def region = '';
        def download = ( params.download?:false ) as Boolean
        def data = params
        data.region = region;
        def dr = ''
        def widgetObject = new WidgetFactory();
        widgetObject = widgetObject.createWidget(data, grailsApplication, webService, utilsService, applicationContext, dr);

        data = widgetObject.process(data, null);
        data.statisticSummary = utilsService.statisticSummary(data.data, true)
        log.debug(data)
        if (download) {
            response.setHeader('Content-disposition', 'attachment; filename=data.csv')
            render(contentType: 'text/plain', text: utilsService.convertJSONtoCSV(data?.data))
        } else if (params.callback) {
            render(contentType: 'text/javascript', text: "${params.callback}(${data as JSON})")
        } else {
            render(contentType: 'application/json', text: data as JSON)
        }
    }

    def toGoogleColumnChart( summary, layer ){
        def result = []
        summary = summary.sort{ it.key }
        if( layer.contains('el') ) {
            log.debug( 'parsing to double')
            summary.each() { k, v ->
                log.debug( k )
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
            json = JSON.parse(webService.get( regionsUrl ) );
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
            log.debug( e.message )
            log.debug( e.stackTrace )
            log.debug('breaking');
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

    public def params(){
        def url = 'http://biocache.ala.org.au/ws/webportal/params'
        def ret = webService.postData(url, [fq:params.fq])
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
        String habInit = params.json
        log.debug(habInit);
        phyloInstance.setHabitat(JSON.parse(habInit).toString());
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
    }

    /**
     * save the habitats json string into database
     * @param phyloInstance
     * @return
     */
    def saveCharacters(Phylo phyloInstance){
        String charInit = params.json
        log.debug(charInit);
        phyloInstance.setCharacters(JSON.parse(charInit).toString());
        phyloInstance.save(flush: true);
        render(contentType: 'application/json',text:"{\"message\":\"success\"}");
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
            def data = webService.get("${params.biocacheServiceUrl}?q=qid:${params.qid}")

            response.setHeader('Content-disposition', 'attachment; filename=data.csv')
            render(contentType: 'text/plain', text: data)
        }
    }
}