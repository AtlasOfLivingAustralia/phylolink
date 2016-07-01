package au.org.ala.phyloviz
import grails.converters.JSON

class OTStudyController {
    def opentreeService
    def webServiceService
    def utilsService

    def listStudies() {
        def pageSize, page, result, search;
        page = params.page as Integer;
        pageSize = params.pageSize;
        search = params.q?:null;
        try{
            if( search != null ){
                result = this._searchStudies( search, null);
            } else {
                result = this._listStudies();
            }
            if( (page != null) && (pageSize != null) ) {
                page = page as Integer
                pageSize = pageSize as Integer
                result[grailsApplication.config.jsonkey.stList] = this.getPage( page, pageSize, result[grailsApplication.config.jsonkey.stList]);
            }
            result[grailsApplication.config.jsonkey.stList] = this.mapStudies(result[grailsApplication.config.jsonkey.stList]);
            render (contentType: 'application/json',text: result as JSON)
        } catch ( Exception e) {
            result = [
                    errorType : 'An exception occurred',
                    message: e.message
            ]
            if( grailsApplication.config.debug){
                result.stackTrace = e.stackTrace
            }
            render (contentType: 'application/json',text: result as JSON, status: 500)
        }
    }

    private def _listStudies( meta ){
        meta = meta?:[:]
        def post
        post = opentreeService.getAllStudiesUrl();
        def studies = webServiceService.postData(  post.url, (post.data as JSON).toString() );
        meta[grailsApplication.config.jsonkey.stList] = studies
        return meta
    }

    private  def _searchStudies( String search, meta ){
        meta = meta?:[:]
        def post
        post = opentreeService.getSearchTreeUrl( search );
        def studies = webServiceService.postData(  post.url, (post.data as JSON).toString() );
        meta[grailsApplication.config.jsonkey.stList] = studies[grailsApplication.config.opentree_jsonvars.searchTree]
        return meta
    }

    private  def getPage(Integer page , Integer pageSize, array ){
        def startIndex = (page - 1) * pageSize;
        def endIndex = startIndex + pageSize;
        def result = new Object[ pageSize ]
        for( def i = startIndex; i< endIndex;i++){
            result[i - startIndex] = array.getAt( i )
        }
        return result;
    }

    private def mapStudies( studies ){
        def result =[];
        for( def i =0;i < studies.size();i ++){
            result.push( this.mapStudy( studies[i] ));
        }
        return  result;
    }

    private def mapStudy( study ){
        def mapping = grailsApplication.config.studyListMapping
        return  utilsService.map(study, mapping)
    }

    def leafNodes(){
        def study, tree;
        study = params.study
        tree = params.tree
        def nexson = utilsService.leafNodes( study, tree );
        render( contentType: 'application/json', text: nexson as JSON)
    }

}