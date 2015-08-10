package au.org.ala.phyloviz

import au.org.ala.web.AlaSecured
import grails.converters.JSON
import org.apache.http.client.HttpResponseException

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_BAD_REQUEST

/**
 * Created by Temi Varghese
 */

class TreeController extends BaseController {
    def utilsService
    def opentreeService
    def treeService
    def authService
    def alaService
    def nexsonService
    def userService
    def webService

    Boolean stackTrace = true
    def index() {

    }

    def create() {
        userService.registerCurrentUser()
        def user = Owner.findByUserId( authService.getUserId()?:-1 )
        if( user ){
            params.user = user
            [ tree: new Tree( params ) ]
        } else {
            def msg = "Failed to detect current user details. Are you logged in?"
            flash.message = msg
            redirect( controller: 'wizard', action: 'start');
        }
    }

    def save(){
        def tree = treeService.createTreeInstance( params )
        log.debug( tree?.getErrors() )
        if( !tree || tree?.hasErrors() ){
            render ( view: 'create', model: [ tree: tree ] )
            return
        }

        flash.message = message( code:'default.created.message', args: [ message(code: 'tree.label', default: 'Tree'),
                                                                       tree.id] )
        redirect( action:'mapOtus', id: tree.id)
    }

    def mapOtus(){
        def id = params.id;
        def tree = Tree.findById( id );
        def nex;
        def otus;

        //TODO: check for permissions
        if( tree ){
            log.debug( 'nexson retrieved: '+ tree.nexson )
            nex = new Nexson( tree.nexson )
            otus = nex.getOtus()
            otus = nexsonService.autoSuggest( otus )
            withFormat {
                html {
                    render( view: 'mapOtus', model: [ otus: otus, id: id ])
                }
                json {
                    render(contentType: 'application/json', text: otus as JSON)
                }
            }

        } else {
            flash.message = message( code: "tree.not.found")
        }
    }

    def show(){
        render ( text: "temi ${params.id}" )
    }
    def searchDoi(){
        def terms = params.q;
        log.debug( terms );
        withFormat {
            json {
                render ( contentType: 'application/json', text: utilsService.searchDoi( terms ) as JSON )
            }
        }
    }

    def treeInfo( ) {
        def nexml = params.tree
        def treeFormat = params.treeFormat?:'nexml'
        def info = ''
//        log.debug( nexml )
        def nexson = '';
        try{
            nexson = opentreeService.convertToNexson( nexml, treeFormat )
            info = treeService.treeInfo( nexson )
            def study = treeService.studyInfo( nexson );
            log.debug( study )
            study.each{ key, value ->
                info[key] = value
            }
            log.debug( info )
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

    def extractStudyData(){
        def nexml = params.tree
        def treeFormat = params.treeFormat?:'nexml'
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


        def nex = new Nexson( (new File( 'artifacts/ot_29.json.1.2.1.json'  ) ).text )
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

    public def autocomplete(){
        def q = params.q;
        q= q.replaceAll(' ', '+')
        def res = utilsService.autocomplete( q );
        render( contentType: 'application/json', text: ['autoCompleteList':res] as JSON );
    }

    public def taxonInfo () {
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
    public def saveOtus(){
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
    public def visualize(){
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
        result['tree']=tree.getTree();
        result['format']=tree.getTreeFormat();
        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }

    def deleteTree() {
        if (!params.id) {
            badRequest "id is a required parameter"
        } else {
            Tree tree = Tree.findById(params.id)
            if (!tree) {
                notFound "No tree found for id ${params.id}"
            } else if (!params.isAdmin && tree.getOwner() != treeService.getCurrentOwner()) {
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
}