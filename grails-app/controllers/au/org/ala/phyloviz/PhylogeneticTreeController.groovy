package au.org.ala.phyloviz
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.http.client.HttpResponseException

/**
 * Created by Temi Varghese
 */

@Transactional(readOnly = true)
class PhylogeneticTreeController {
    def utilsService
    def opentreeService
    def treeService
    def stackTrace = true
    def index() {}
    def create() {
        respond new PhylogeneticTree( params )
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
            nexson = opentreeService.convertNexmltoNexson( nexml, treeFormat )
//            log.debug( nexson.getClass().getName() )
//            log.debug( nexson.data.nexml.otus )
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
//            render( contentType: 'application/json', text: nexson );
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
}