package au.org.ala.phyloviz
import grails.converters.JSON
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import static org.springframework.http.HttpStatus.*

/**
 * Created by Temi Varghese on 19/06/2014.
 */
class PhyloController {
    def webService;
    def metricsService;
    def opentreeService
    def utilsService
    def userService
    def treeService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phylo.list(params), model: [phyloInstanceCount: Phylo.count()]
    }

    def show(Phylo phyloInstance) {
        def tree = Tree.findById(phyloInstance.getStudyid());
        def user = userService.getUser();
        log.debug("current user: "+user);
        def userId = userService.getCurrentUserId();
        if(userId != ""){
            userId = userId instanceof String?Long.parseLong(userId):userId;
        }

        Boolean edit = false
        log.debug("user id : ${userId instanceof String}")
        log.debug("owner id: ${phyloInstance.getOwner().userId}");
        if( phyloInstance.getOwner().userId == userId ){
            edit = true
            log.debug('editable');
        }

        respond phyloInstance, model: [ tree: tree, userId: userId, edit: edit]
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
     * TODO: Delete this function? not used any more.
     * @param phyloInstance
     * @return
     */
    def getWidgetData(Phylo phyloInstance){
        def species = JSON.parse( params.speciesList );
        def summary = [:], result
        log.debug( 'wid' )
        log.debug( params.wid );
        def widget = phyloInstance.widgets?.getAt( Integer.parseInt(params.wid) );
        def layer = widget.config
        def name = widget.displayname
        def region = phyloInstance.regionName;
        def regionType = phyloInstance.regionType? phyloInstance.regionType : 'state' ;
        def download = ( params.download?:false ) as Boolean
        region = region? "${regionType}:\"${region.replaceAll(' ', '+')}\"" : '';
        def data = widget
        data.region = region;
        def dr = phyloInstance.dataResource
        log.debug( widget)
        def widgetObject = WidgetFactory.createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr)
        data = widgetObject.process( params , phyloInstance )
        log.debug( data )
        if( download ){
            response.setHeader('Content-disposition','attachment; filename=data.csv')
            render ( contentType: 'text/plain', text: utilsService.convertJSONtoCSV(data) )
        } else {
            render(contentType: 'application/json', text: data as JSON)
        }
    }

    /**
     * INTERSECTION BETWEEN A LAYER AND SPECIES OCCURRENCE
     * @param phyloInstance
     * @return
     */
    def getHabitat(){
//        def species = JSON.parse( params.speciesList?:'[]' );
        def summary = [:], result
        def region = '';
        def download = ( params.download?:false ) as Boolean
        def data = params
        data.region = region;
        def dr = ''
        def widgetObject = new WidgetFactory();
        widgetObject = widgetObject.createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr);
//        ContextualWidget widgetObject = new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
        data = widgetObject.process( data ,  null);
        log.debug( data )
        if( download ){
            response.setHeader('Content-disposition','attachment; filename=data.csv')
            render ( contentType: 'text/plain', text: utilsService.convertJSONtoCSV(data) )
        }else if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${data as JSON})")
        }else {
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
            result.add(0, ['Character','Occurrences'])
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
        def noTreeText = params.noTreeText?:false;
        noTreeText = noTreeText.toBoolean()
        treeId = params.treeId?.toString()
        studyId = params.studyId?.toString()
        tree = params.newick;
        speciesList = params.speciesList
        try {
            def result = this.getPDCalc(treeId, studyId, tree, speciesList)
            result = noTreeText ? this.removeProp(result, grailsApplication.config.treeMeta.treeText) : result
            render(contentType: 'application/json', text: result as JSON)
        } catch (Exception e){
            log.debug( e.message )
            log.debug( e.stackTrace )
            log.debug('breaking');
        }
    }

    def getPDCalc( String treeId, String studyId, String tree, String speciesList){
        def startTime, deltaTime
        def treeUrl, type, i,pd, sList;
        def studyMeta = [:], result =[], trees = [], input =[]
        type = tree?"tree":treeId?"gettree":"besttrees"
        switch (type){
            case 'tree':
                studyMeta [grailsApplication.config.treeMeta.treeText]=tree
                studyMeta [ grailsApplication.config.studyMetaMap.name ]= message(code: 'phylo.userTreeName', default: 'User tree' )
                studyMeta = opentreeService.addTreeMeta(metricsService.getJadeTree( tree ), studyMeta )
                input.push( studyMeta )
                break;
            case 'gettree':
                studyMeta = this.getTreeMeta(treeId, studyId, null )
                input.push( studyMeta )
                break;
            case 'besttrees':
                startTime = System.currentTimeMillis()
                input = this.getExpertTreeMeta();
                deltaTime = System.currentTimeMillis() - startTime
                log.debug( "time elapse: ${deltaTime}")
                input = input[grailsApplication.config.expertTreesMeta.et]
                break;
        }
        sList = new JsonSlurper().parseText( speciesList )

        for( i = 0; i < input.size(); i++){
            studyMeta = [:]
            log.debug( input[i] );
            input[i][grailsApplication.config.treeMeta.treeText] = metricsService.treeProcessing( input[i][grailsApplication.config.treeMeta.treeText] )
            // calculate pd
            pd = metricsService.pd( input[i][grailsApplication.config.treeMeta.treeText], sList )
                input[i]['maxPd'] = metricsService.maxPd( input[i].tree )
            // merge the variables
            pd.each {k,v->
                input[i][k]=v
            }
            result.push( input[i] )
        }
        return  result;
    }

    def getTree(){
        def studyId = params.studyId?.toString()
        def treeId = params.treeId?.toString()
        def noTreeText = params.noTreeText?:false;
        noTreeText = noTreeText.toBoolean()
        def meta = [:]
        meta = this.getTreeMeta(treeId, studyId, meta)
        meta = noTreeText? this.removeProp( meta , grailsApplication.config.treeMeta.treeText ): meta ;
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${meta as JSON})")
        } else {
            render(contentType: 'application/json', text: meta as JSON)
        }
    }

    public def params(){
        def url = 'http://biocache.ala.org.au/ws/webportal/params'
        def ret = webService.postData(url, [fq:params.fq])
//        def ret = webService.postData(url,'/ws/webportal/params',[fq:params.fq])
    //        log.debug( "POST return" +ret.readLines() )
        render ( text: ret.toString() )
    }

    /**
     * attaches metadata of tree onto given metadata variable
     * @param treeId
     * @param studyId
     * @param meta
     * @return
     */
    private def getTreeMeta(String treeId, String studyId, meta){
        def startTime, deltaTime
        meta = meta?:[:]
        meta = this.getTreeText( treeId, studyId, meta )
        meta = utilsService.getViewerUrl(treeId, studyId, meta)
        meta = opentreeService.getStudyMetadata( studyId, meta )
        log.debug( meta )
        def jadetree = metricsService.getJadeTree( meta[grailsApplication.config.treeMeta.treeText] )
        meta = opentreeService.addTreeMeta(jadetree, meta )
        return  meta
    }

    private def removeProp( Collection meta, String prop){
        for ( def i = 0 ; i < meta.size(); i++){
            meta[i]?.remove( prop )
        }
        return meta;
    }

    private def removeProp( HashMap meta , String prop ){
        meta?.remove( prop )
        return meta;
    }

    /**
     * this func creates a url and fetches its newick string
     * @param treeId
     * @param studyId
     * @param meta
     * @return
     */
    private def getTreeText(String treeId, String studyId, meta){
        meta = meta?:[:]
        def tree = webService.get( opentreeService.getTreeUrlNewick(treeId, studyId) )
        meta[grailsApplication.config.treeMeta.treeText] = tree;
        return meta
    }

    /**
     * webservice that returns trees recommended by experts
     * @return a json
     * {
     *  'expertTrees':[{},{}]
     * }
     */
    def getExpertTrees(){
        def noTreeText = params.noTreeText?:false;
        def result = this.getExpertTreeMeta()
        result[grailsApplication.config.expertTreesMeta.et] = noTreeText ? this.removeProp(result[grailsApplication.config.expertTreesMeta.et] , grailsApplication.config.treeMeta.treeText ):result[grailsApplication.config.expertTreesMeta.et]
        render ( contentType: 'application/json', text: result as JSON)
    }

    def getExpert(){
        def noTreeText = params.noTreeText?:false;
        def exp = Tree.findAllWhere(['expertTree':true]);
        def result = []
//        log.debug(result);
        exp.each {tree->
//            log.debug(tree as JSON)
            result.push(tree as JSON);
        }
        result[grailsApplication.config.expertTreesMeta.et] = noTreeText ? this.removeProp(result[grailsApplication.config.expertTreesMeta.et] , grailsApplication.config.treeMeta.treeText ):result[grailsApplication.config.expertTreesMeta.et]
        render ( contentType: 'application/json', text: result as JSON)
    }

    /**
     *
     * @param meta
     * metadata variable to which the result of this function gets added to
     * @return
     */
    private def getExpertTreeMeta( meta ) {
        meta = meta?:[:]
        def trees = grailsApplication.config.expert_trees, i, studyId , treeId, input = [], studyMeta, temp
        for( i = 0;i < trees.size(); i++ ){
            if( trees[i][grailsApplication.config.treeMeta.treeText] == null ){
                studyId = trees[i].studyId?.toString()
                treeId = trees[i].treeId?.toString()
                studyMeta = this.getTreeMeta( treeId, studyId, trees[i] )
                input.push( studyMeta.clone() )
            } else {
                input.push( trees[i].clone() )
            }

        }
        meta[ grailsApplication.config.expertTreesMeta.et ] = input
        return  meta
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

    private def getStudiesMeta( meta ){

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
     * gets habitats json string
     * @param phyloInstance
     * @return
     */
    def getHabitatInit(Phylo phyloInstance){
        String meta = JSON.parse( phyloInstance.getHabitat() );
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${meta as JSON})");
        } else {
            render(contentType: 'application/json', text: meta as JSON);
        }
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
     * gets habitats json string
     * @param phyloInstance
     * @return
     */
    def getCharacters(Phylo phyloInstance){
        String meta = JSON.parse( phyloInstance.getCharacters() );
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${meta as JSON})");
        } else {
            render(contentType: 'application/json', text: meta as JSON);
        }
    }

    /**
     * save visualization title to database
     * @param phyloInstance
     * @return
     */
    def saveTitle(Phylo phyloInstance){
        def user = Owner.findByUserId(userService.getCurrentUserId())
        def result = [:]
        log.debug(user)
        log.debug(phyloInstance.getOwner()?.userId)
        if(phyloInstance.getOwner().userId == user.userId){
            phyloInstance.setTitle(params.title);
            phyloInstance.save(
                    flush: true
            );
            if (phyloInstance.hasErrors()) {
                result['error'] = 'An error occurred';
            } else {
                result['message'] = 'Successfully saved title';
            }
        } else {
            result['error'] = 'User not recognised'
        }

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})");
        } else {
            render(contentType: 'application/json', text: result as JSON);
        }
    }
}