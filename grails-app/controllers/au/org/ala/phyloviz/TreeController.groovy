package au.org.ala.phyloviz

import au.org.ala.phylolink.TrimOption
import au.org.ala.web.AlaSecured
import grails.converters.JSON
import org.apache.http.client.HttpResponseException
/**
 * Created by Temi Varghese
 */

class TreeController extends BaseController {

    def utilsService
    def opentreeService
    def treeService
    def authService
    def alaService
    def userService
    def webServiceService

    Boolean stackTrace = true

    def index() {}

    def create() {
        userService.registerCurrentUser()
        def userId = authService.getUserId()
        def user = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')
        if( user ){
            params.user = user
            [ tree: new Tree( params ), isAdmin:userService.userIsSiteAdmin() ]
        } else {
            def msg = "Failed to detect current user details. Are you logged in?"
            flash.message = msg
            redirect( controller: 'wizard', action: 'start');
        }
    }

    def edit() {
        userService.registerCurrentUser()
        def userId = authService.getUserId()
        def user = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')
        if( user ){
			def treeId = (params.id ? params.id : params.studyId)
			String hql = "select count(p) " + 
					"from Phylo as p " +
                    "where p.status = '" + Phylo.WorkflowStatus.Approved.getKey() + "' " +
					"and p.studyid = :treeId"
			Map hqlParams = [ treeId: treeId ]					
			def rowCount = Phylo.executeQuery(hql, hqlParams).get(0)
			log.debug("In edit hql='${hql}', treeId=${treeId}, rowCount=${rowCount}")
			if (rowCount == 0) {
				params.user = user
				render( view: 'create', model:  [ tree: Tree.findById(treeId), isAdmin:userService.userIsSiteAdmin() ])
			} else {
				def msg = "You can't edit this tree as it is associated with a Published visualisation."
				flash.message = msg
				treeAdmin();
			}
        } else {
            def msg = "Failed to detect current user details. Are you logged in?"
            flash.message = msg
            redirect( controller: 'wizard', action: 'start');
        }
    }

    def save(){
        def tree
        if (params.id){
            tree = Tree.findById(params.id)
            bindData(tree, params)
            if (tree.save(flush: true)) {
                log.debug('tree saved to database.' + tree.getId())
            }
            redirect( action:'mapOtus', id: tree.id)
        } else {
            tree = treeService.createTreeInstance( params )
            log.debug( String.valueOf(tree?.getErrors()) )
            if( !tree || tree?.hasErrors() ){
                render ( view: 'create', model: [ tree: tree ] )
                return
            }
            flash.message = message( code:'default.created.message', args: [ message(code: 'tree.label', default: 'Tree')])
            redirect( action:'mapOtus', id: tree.id)
        }
    }

    /**
     * Web service to map a tree nodes to ALA taxonomy. It does automatic mapping on the following coniditions.
     * This function can be accessed by admin or by owner of a tree.
     * 1. Checks if tree nodes are mapped to ALA taxonomy, then cancel auto mapping and return only tree nodes.
     * 2. If ALA taxonomy mapping not done, then do auto mapping and return all tree nodes.
     * @param id {@link Tree#id}
     * @return
     */
    def mapOtus(){
        try{
            if(params.id){
                List otus
                Integer id = Integer.parseInt(params.id)
                Tree tree = Tree.findById( id )
                Owner owner = utilsService.getOwner()

                if( tree && ((tree.ownerId == owner?.id) || userService.userIsSiteAdmin())) {
                    otus = treeService.getMappedOtus(tree)
                    withFormat {
                        html {
                            render( view: 'mapOtus', model: [ tree: tree, otus: otus.sort { it['^ot:originalLabel']}, id: id ])
                        }
                        json {
                            render(contentType: 'application/json', text: otus as JSON)
                        }
                    }
                } else {
                    flash.message = "Tree not found or you do not have permission"
                    redirect(controller: 'wizard', action: 'start')
                }
            } else {
                log.error("Parameter id must be provided to mapOTUs")
                badRequest "Parameter id must be provided"
            }
        } catch (Exception e){
            log.error("Unable to load mapOTU page: " + e.getMessage(), e)
            e.printStackTrace()
            badRequest "Could not process request"
        }
    }

    def show(){
        render ( text: "temi ${params.id}" )
    }

    def searchDoi(){
        def terms = params.q;
        log.debug( String.valueOf(terms) );
        withFormat {
            json {
                render ( contentType: 'application/json', text: utilsService.searchDoi( terms ) as JSON )
            }
        }
    }

    def treeInfo( ) {
        def nexml = params.tree
        def treeFormat = params.treeFormat?:'nexml'
        try{
            def nexson = opentreeService.convertToNexson( nexml, treeFormat )
            def info = treeService.treeInfo( nexson )
            def study = treeService.studyInfo( nexson );
            study.each { key, value ->
                info[key] = value
            }
            withFormat {
                json {
                    render( contentType: 'application/json', text: info as JSON )
                }
                html {
                    render( template: 'treeInfo', model:[info: info]);
                }
            }
        } catch ( java.lang.reflect.UndeclaredThrowableException r ){
            def rexp = r.getCause()
            if( rexp instanceof  HttpResponseException){
                log.debug( rexp.getClass().getName() )
                def resp = rexp.getResponse();
                response.status = resp.getStatus();
                def error = [ 'error': "failed response: ${ resp.getStatusLine() }" , 'errorCode': resp.getStatus() ]
                if( stackTrace ){
                    error.stackTrace = rexp.getStackTrace()
                }
                render( contentType: 'application/json', text: error as JSON );
            } else {
                this.basicExceptionHandler( rexp )
            }
        } catch ( Exception e ){
            this.basicExceptionHandler( e )
        }

    }

    private def basicExceptionHandler( Exception e ){
        response.status = 500;
        def resp = e.getMessage()
        def error = [ 'error': "failed response: ${  e.getMessage() }" , 'errorCode': 500 , 'errorClass': e.getClass().getName()]

        if( stackTrace ){
            error.stackTrace = e.getStackTrace()
        }

        render( contentType: 'application/json', text: error as JSON );
    }

    def testCitation(){
        def citation = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and Gonzlez-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species.' +
                ' Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        log.debug( 'in testcitation')


        def nex = new au.org.ala.phyloviz.Nexson( (new File( 'artifacts/ot_29.json.1.2.1.json'  ) ).text )
        render ( contentType: 'text/html', text:  nex.getTitle())
    }

    def guessFormat(){
        def tree = params.tree
        def guess = treeService.guessFormat( tree )
        render ( contentType: 'text/plain', text: guess )
    }

    def getTreeMeta(){
        def tree = params.tree
        def meta = treeService.getTreeMeta( tree );
        render( contentType: 'application/json', text: meta as JSON)
    }

    private def loadSampleTree(){
        def result = [:], filename = 'ot_29.nexml.txt'
        result['version'] = '1.2.1'
        def file = new File( 'artifacts/' + filename )
        result['text'] = file.text
        result['year'] = '2011'
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['citation'] = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. ' +
                'Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        result['title'] = 'The evolution and phylogenetic placement of invasive Australian Acacia species'
        result['format'] = 'nexml'
        return result
    }

    def autocomplete(){
        def q = params.q;
        q= q.replaceAll(' ', '+')
        def res = utilsService.autocomplete( q );
        render( contentType: 'application/json', text: ['autoCompleteList':res] as JSON );
    }

    def taxonInfo () {
        def q = params.q
        def taxon = alaService.getTaxonInfo( q )
//        withFormat {
//            html {
                render( template: 'taxonInfo', model: [taxon: taxon])
//            }
//            json {
//                render( contentType: 'application/json', text: taxon as JSON )
//            }
//        }
    }

    /**
     * save reconciled otus back into nexson
     */
    def saveOtus(){
        def otus = params.otus
        def id = params.id
        def study = Tree.get( params.id )

        otus = JSON.parse( otus )
        treeService.saveOtus( otus, study )
        redirect( controller: 'tree', action: 'mapOtus', params: [ id: id ] )
    }

    /**
     * save OTUs and then redirect to wizard controller.
     * @return
     */
    def visualize(){
        def otus = params.otus;
        def id = params.id;
        def study = Tree.get( params.id )

        otus = JSON.parse( otus )
        treeService.saveOtus( otus, study )
        redirect( controller: 'wizard', action: 'visualize', params: [ id: id ] )
    }

    /**
     *
     * @return
     */
    def search(){
        def q = params.q;
        def result;
        result = treeService.search(q);
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

    /**
     * get tree
     */
    def getTreeNexml(Tree tree){
        render(text: tree?.getTree(), contentType: 'text/plain')
    }

    /**
     * get tree. return json object
     */
    def getTree(Tree tree){
        def result = [:]

        if (params.trimOption && TrimOption.valueOf(params.trimOption) != TrimOption.NONE) {
            tree = treeService.trimTree(tree.id, TrimOption.valueOf(params.trimOption), params.trimToInclude?.toBoolean(), params.trimData)
        }

        result['tree']=tree.getTree();
        result['format']=tree.getTreeFormat();
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

    def trimTree() {
        TrimOption trimOption = TrimOption.valueOf(params.trimOption)

        if (!params.treeId || !trimOption) {
            badRequest "treeId and trimOption are required parameters. trimOption must be one of ${TrimOption.values()}"
        } else if (trimOption == TrimOption.SPECIES_LIST && !params.trimData) {
            badRequest "trimData is a required parameter when trimOption = SPECIES_LIST"
        } else if (!Tree.findById(params.treeId)) {
            notFound "No matching tree was found for id ${params.treeId}"
        } else {
            Tree tree = treeService.trimTree(params.treeId, TrimOption.valueOf(params.trimOption), params.trimToInclude?.toBoolean(), params.trimData)

            def result = [:]
            result['tree']=tree.getTree();
            result['format']=tree.getTreeFormat();
            if(params.callback){
                render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
            } else {
                render(contentType: 'application/json', text: result as JSON)
            }
        }
    }

    def deleteTree() {
        if (!params.id) {
            badRequest "id is a required parameter"
        } else {
            Tree tree = Tree.findById(params.id)
            if (!tree) {
                notFound "No tree found for id ${params.id}"
            } else if (!userService.userIsSiteAdmin() && tree.getOwner().getUserId() != treeService.getCurrentOwner().getUserId()) {
                notAuthorised "Only the tree owner or an administrator can delete this tree"
            } else {
                treeService.deleteTree(params.id as int)
                redirect controller: "wizard", action: "myTrees"
            }
        }
    }

    /**
     *
     * @return
     */
    public def map(){
        render ( view: 'mapOtus',  model: [ otus: [], id: 1 ])
    }

    def searchTreebase() {
        def query = params.q;
        def json = treeService.searchTreebase( query )
        if (params.callback) {
            render (contentType: "application/javascript", text: "${params.callback}("+json+")")
        } else {
            render (contentType: "application/json", text: json as JSON)
        }
    }

    def download(Tree tree){
        response.setHeader("Content-disposition", "attachment;filename=tree.txt")
        if( tree && treeService.canAccess()){
            render( contentType: 'text/plain', text:tree.getTree());
        } else {
            render(contentType: 'text/plain', fileName:'tree.txt', text: 'An error occurred or you are not authorized to access this tree.');
        }
    }

    @AlaSecured(value = ["ROLE_ADMIN", "ROLE_PHYLOLINK_ADMIN"], redirectUri = "/403", anyRole = true)
    def treeAdmin() {
        List publicTrees = treeService.getPublicTrees()

        render view: "admin", model: [trees: publicTrees]
    }

    @AlaSecured(value = ["ROLE_ADMIN", "ROLE_PHYLOLINK_ADMIN"], redirectUri = "/403", anyRole = true)
    def toggleExpertTree() {
        if (!params.treeId) {
            badRequest "treeId is a required parameter"
        } else {
            Tree tree = treeService.toggleExpertTree(params.treeId, params.expertTreeTaxonomy, params.expertTreeLSID, params.expertTreeId)

            render template: "adminTableRow", model: [tree: tree]
        }
    }

    /**
     * Rematch a tree to ALA taxonomy. This web service is called for a user create tree.
     * @param treeId - {@link Tree#id}
     * @return
     */
    @AlaSecured(value = ["ROLE_USER"], redirectUri = "/403", anyRole = true)
    def rematchMyTree(){
        if(params.treeId){
            try {
                Integer id = Integer.parseInt(params.treeId)
                Owner owner = utilsService.getOwner()
                List<Tree> trees = Tree.findAllByIdAndOwner(id, owner)
                if(trees.size()){
                    treeService.rematchTrees(trees, true)
                    flash.message = 'Successfully re-matched tree'
                    redirect( controller: 'wizard', action: 'myTrees')
                } else {
                    notFound("Could not find tree for the given id or you do not have permission")
                }
            } catch (Exception e){
                log.error(e.message, e)
                flash.message = 'Internal Server error - Could not rematch tree. ' + e.message
                redirect( controller: 'wizard', action: 'myTrees')
            }
        } else {
            badRequest "treeId is a required parameter"
        }
    }

    /**
     * Rematch an expert tree to ALA taxonomy. This web service can only be called by a user with appropriate role.
     * @param treeId - {@link Tree#id}
     * @param redirect - Decides the page to redirect to after completing the request.
     * @return
     */
    @AlaSecured(value = ["ROLE_ADMIN", "ROLE_PHYLOLINK_ADMIN"], redirectUri = "/403", anyRole = true)
    def rematchExpertTree(){

        if (params.treeId){
            String controller = 'wizard', action = 'expertTrees'
            switch (params.redirect){
                case 'treeAdmin':
                    controller = 'tree'
                    action = 'treeAdmin'
                    break
            }

            try {
                Integer id = Integer.parseInt(params.treeId)
                List<Tree> trees = Tree.findAllById(id)

                if(trees.size()){
                    treeService.rematchTrees(trees, true)
                    flash.message = 'Successfully re-matched tree'
                    log.info('Successfully re-matched tree ' + params.treeId)
                    redirect( controller: controller, action: action)
                } else {
                    notFound("Could not find tree for the given id")
                }
            } catch (Exception e) {
                log.error(e.message, e)
                flash.message = 'Internal Server error - Could not rematch tree. ' + e.message
                redirect( controller: controller, action: action)
            }
        } else {
            badRequest "treeId is a required parameter"
        }
    }
}