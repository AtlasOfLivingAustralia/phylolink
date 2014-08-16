package au.org.ala.phyloviz
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import static org.springframework.http.HttpStatus.*

/**
 * Created by Temi Varghese on 19/06/2014.
 */
@Transactional(readOnly = true)
class PhyloController {
    def webService;
    def metricsService;
    def opentreeService
    def utilsService
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phylo.list(params), model: [phyloInstanceCount: Phylo.count()]
    }

    def show(Phylo phyloInstance) {
        respond phyloInstance
    }

    def create() {
        respond new Phylo(params)
    }
//    def create( $studyId, $treeId, $index) {
//        respond new Phylo(params)
//    }
    @Transactional
    def save(Phylo phyloInstance) {
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

    @Transactional
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

    @Transactional
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

    def getWidgetData(Phylo phyloInstance){
        def species = JSON.parse( params.speciesList );
//        params.speciesList = species;
        def summary = [:], result
        println( 'wid' )
        println( params.wid );
        def widget = phyloInstance.widgets?.getAt( Integer.parseInt(params.wid) );
        def layer = widget.config
        def name = widget.displayname
        def region = phyloInstance.regionName;
        def regionType = phyloInstance.regionType? phyloInstance.regionType : 'state' ;
        def download = ( params.download?:false ) as Boolean
        region = region? "${regionType}:\"${region.replaceAll(' ', '+')}\"" : '';
        def data = widget
        data.region = region;
        println( widget)
        def widgetObject = WidgetFactory.createWidget( data, grailsApplication, webService, utilsService, applicationContext)
        data = widgetObject.process( params , phyloInstance )
        println( data )
        if( download ){
            response.setHeader('Content-disposition','attachment; filename=data.csv')
            render ( contentType: 'text/plain', text: utilsService.convertJSONtoCSV(data) )
        } else {
            render(contentType: 'application/json', text: data as JSON)
        }
    }


    def toGoogleColumnChart( summary, layer ){
        def result = []
        summary = summary.sort{ it.key }
        if( layer.contains('el') ) {
            println( 'parsing to double')
            summary.each() { k, v ->
                println( k )
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
            println( e.message )
            println( e.stackTrace )
            println('breaking');
        }
    }
//    def getPDCalc( String treeId, String studyId, String tree, String speciesList){
//        return  this.getPDCalc( treeId, studyId, tree, speciesList, true)
//    }
    def getPDCalc( String treeId, String studyId, String tree, String speciesList){
        def startTime, deltaTime
        def treeUrl, type, i,pd, sList;
        def studyMeta = [:], result =[], trees = [], input =[]
//        maxPd = maxPd?:true;
        type = tree?"tree":treeId?"gettree":"besttrees"
//        startTime = System.currentTimeMillis()
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
                println( "time elapse: ${deltaTime}")
                input = input[grailsApplication.config.expertTreesMeta.et]
                break;
        }
//        deltaTime = System.currentTimeMillis() - startTime
//        println( "time elapse: ${deltaTime}")
        sList = new JsonSlurper().parseText( speciesList )

        for( i = 0; i < input.size(); i++){
            studyMeta = [:]
            println( input[i] );
            input[i][grailsApplication.config.treeMeta.treeText] = metricsService.treeProcessing( input[i][grailsApplication.config.treeMeta.treeText] )
//            startTime = System.currentTimeMillis()
            // calculate pd
            pd = metricsService.pd( input[i][grailsApplication.config.treeMeta.treeText], sList )
//            deltaTime = System.currentTimeMillis() - startTime
//            println( "time elapse: ${deltaTime}")
//            startTime = System.currentTimeMillis()
//            if( maxPd ){
                input[i]['maxPd'] = metricsService.maxPd( input[i].tree )
//            }
//            deltaTime = System.currentTimeMillis() - startTime
//            println( "time elapse: ${deltaTime}")
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
        render( contentType: 'application/json', text: meta as JSON )
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
//        startTime = System.currentTimeMillis()
        meta = utilsService.getViewerUrl(treeId, studyId, meta)
//        deltaTime = System.currentTimeMillis() - startTime
//        println( " get viewer url time elapse: ${deltaTime}")
//        startTime = System.currentTimeMillis()
        meta = opentreeService.getStudyMetadata( studyId, meta )
//        deltaTime = System.currentTimeMillis() - startTime
//        println( " get study meta elapse: ${deltaTime}")
//        startTime = System.currentTimeMillis()
        println( meta )
        def jadetree = metricsService.getJadeTree( meta[grailsApplication.config.treeMeta.treeText] )
//        deltaTime = System.currentTimeMillis() - startTime
//        println( " create tree  object time elapse: ${deltaTime}")
//        startTime = System.currentTimeMillis()
        meta = opentreeService.addTreeMeta(jadetree, meta )
//        deltaTime = System.currentTimeMillis() - startTime
//        println( " add meta of tree: ${deltaTime}")
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

}