package au.org.ala.phyloviz
import grails.converters.JSON
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.Node

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import static org.elasticsearch.node.NodeBuilder.nodeBuilder

class ElasticService {
    def grailsApplication
    def webService

    Client client
    Node node;
    def NEXSON_INDEX
    def NEXSON_TYPE
    def DEFAULT_INDEX
    def DEFAULT_TYPE

    /**
     * start elasticsearch database and create client
     * @return
     */
    @PostConstruct
    def initialize(){
        NEXSON_INDEX = grailsApplication.config.eIndex
        NEXSON_TYPE = grailsApplication.config.eType
        DEFAULT_INDEX = NEXSON_INDEX
        DEFAULT_TYPE = NEXSON_TYPE

        log.debug( 'setup elasticsearch')
        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.put("path.home", grailsApplication.config.app.elasticsearch.location);
        node = nodeBuilder().local(true).settings(settings).node();
        client = node.client();
        // TODO: is this needed?
        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
    }

    /**
     * initialize an index
     * @return
     */
    def initDatabase(){
        // delete index
        deleteIndex(  getNEXSON_INDEX() )

        // create and add mapping
        def schema = this.getSchema();
        setSchema( getNEXSON_INDEX(), null, schema )
    }

    def createIndex( index ){
        log.debug( 'before index creation:' + index)
        client.admin().indices().create( Requests.createIndexRequest( index )).actionGet()
        log.debug( 'after index creation:' + index)
    }

    def getSchema (){
        def file = new File( grailsApplication.config.elasticSchema )
        return file.text;
    }

    /**
     * set a schema for an index
     * @param index
     * @param type
     * @param schema
     * @return
     */
    def setSchema( index, type, String schema){
        log.debug('before setting schema' + schema)
//        client.admin().indices().prepareCreate(index).addMapping( getNEXSON_TYPE(), schema ).execute().actionGet()
        client.admin().indices().prepareCreate(index).setSource( schema ).execute().actionGet()
        log.debug('after setting schema')
    }


    /**
     * Index a single document (toMap representation not domain class)
     * Does a check to see if doc has been marked as deleted.
     *
     * @param doc
     * @return IndexResponse
     */
    def indexDoc( index, id, doc ) {
        def docJson = JSON.parse( doc )
        def docId = getEntityId(docJson)
        index = index?:DEFAULT_INDEX
        log.debug('before indexing document:'+index+' document:'+doc)
        try {
            docJson = setDocId(docJson, id)
            IndexRequestBuilder builder = client.prepareIndex(index, DEFAULT_TYPE,Integer.toString(id) )
            builder.setSource( docJson.toString() ).execute().actionGet()
            log.debug( 'success indexing document')
        } catch (Exception e) {
            log.error "Error indexing document: ${docJson.toString()}\nError: ${e}", e
        }
        log.debug('after indexing document')
    }

    def setDocId( doc, id ){
        if( doc == null){
            return
        }
        doc.studyId = id;
        return  doc
    }
    def getEntityId( doc ){
        return doc.studyId
    }

    def createDoc(index, type, doc) {
        def url = urlType( index, type)
        webService.postData( url, doc )
    }
    
    def updateDoc(index, type, doc){
        
    }

    def deleteDoc(index, type, doc){
        
    }

    def searchDoc(index, type, params){
        def facets = grailsApplication.config.facets;
        facets = JSON.parse( facets )
        if( params.fq ){
            facets = getFilters( params, facets )
        }
        facets = facets.toString()
        def url = this.urlType( index, type )
        url = "${url}/_search"
        return webService.postData( url, facets.toString() )
    }

    def getFilters( params, query ){
        if( params.fq == null ){
            return
        }
        def terms = []
        def fq = params.fq
        def filter = '''
        {
            "filter": {
                "bool": {
                    "should": [

                    ]}
            }
        }
        '''
        filter = JSON.parse( filter );
//        fq.eachWithIndex { f, i ->
            def term = ["term":[:]]
            log.debug( 'f value is:')
            log.debug( params.fq )
            term['term'][ fq.split(':')[0] ] =  fq.split(':')[ 1 ]
//            term = term as
        filter = ([ "bool":["should":[ term ]] ] as JSON).toString()
            query['filter'] =  JSON.parse( filter )
//        }
//        query['filter'] = filter['filter']
        log.debug( ' query value')
        log.debug( query )
        return  query
    }

    def transformSearch( out ){

        def result = [:],
        source
        result['hits'] = [:]
        result['hits']['total'] = out?.hits.total
        def hits = []
        out?.hits.hits.eachWithIndex{ val, i ->
            source = val['_source']
            source = new Nexson( source.toString())
            log.debug( source.getMeta( ) )
            hits.push( source.getMeta( ) )
        }

        result['hits']['hits'] = hits;
        result['facets'] = [:]
        log.debug('in transformSearch')
//        log.debug( hits )
        out?.aggregations.each{ facet, val ->
            result['facets'][ facet ] = [:]
            def terms = []

            for( def i = 0; i< val.buckets.size();i++ ){
                def f = val.buckets[i]
                def term = [:]
                term['term'] = f['key']
                term['count'] = f['doc_count']
                terms.push( term )
            }
            result['facets'][ facet ]['terms'] = terms
        }

        return result
    }

    /**
     * Delete the (default) ES index
     *
     * @return
     */
    public deleteIndex(index) {
        def indexes = (index) ? [ index ] : [ NEXSON_INDEX ]

        indexes.each {
            log.info "trying to delete $it"
            try {
                def response = node.client().admin().indices().prepareDelete(it).execute().get()
                if (response.acknowledged) {
                    log.info "The index is removed"
                } else {
                    log.error "The index could not be removed"
                }
            } catch (Exception e) {
                log.error "The index you want to delete is missing : ${e.message}"
            }
        }

        return "index cleared"
    }

    def deleteType(index, type){

    }

    def urlType( ei = null, et  = null ){
        ei = this.urlIndex( ei )
        if( et == null ){
            et = grailsApplication.config.eType
        }
        return "${ei}/${et}"
    }

    def urlIndex( ei ){
        def bUrl = this.urlEndPoint();
        if( ei == null ){
            ei =  grailsApplication.config.eIndex
        }
        return "${bUrl}/${ei}"
    }

    def urlEndPoint(){
        return grailsApplication.config.elasticBaseUrl
    }

    /**
     * close elasticsearch node
     * @return
     */
    @PreDestroy
    def destroy(){
        node.close();
    }
}
