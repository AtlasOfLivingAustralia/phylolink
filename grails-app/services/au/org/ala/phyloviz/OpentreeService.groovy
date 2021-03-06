package au.org.ala.phyloviz

import grails.converters.JSON
import grails.transaction.Transactional
import jade.tree.JadeTree

@Transactional
class OpentreeService {
    def grailsApplication
    def webServiceService
    def metricsService

    def getStudyMetadata( id, addMeta ) {
        def i
        addMeta = addMeta?:[:]
        au.org.ala.phyloviz.ConvertTreeToObject cv = new au.org.ala.phyloviz.ConvertTreeToObject();
        Tree t = Tree.findById(id);
        def result = cv.convert(t);
        // do some processing now
        result = this.getAuthor( result );
        return result
    }

    def mapStudyFields( meta , result){
        def i
        def mapping = grailsApplication.config.nexmlMetaMapping
        result = result?:[:]

        mapping.each {metaProp, path->
            def pathArray = path.split('/');
            def val = meta;
            for( i = 0; i<pathArray.size(); i++ ){
                if( val != null ){
                    val = val[ pathArray[i] ];
                } else {
                    break;
                }

            }
            result[ metaProp ]= val
        }

        return result;
    }

    /**
     * extract authors from full publication text
     * @param data
     * @return
     */
    def getAuthor ( data ){
        def studyMeta = grailsApplication.config.studyMetaMap
        def study = data[ studyMeta.name ]
        def year = data[studyMeta.year]
        data[studyMeta.authors] = study?.split ("\\s+[\\(]*${year}[\\)]*\\.")[0]
        return data;
    }

    /**
     * add tree meta data to meta parameter
     * @param tree
     * @param meta
     * @return
     */
    def addTreeMeta( JadeTree tree, meta ){
        meta = meta?:[:]
        meta[grailsApplication.config.treeMeta.numLeaves] = tree.externalNodeCount;
        meta[grailsApplication.config.treeMeta.numIntNodes] = tree.internalNodeCount;

        return  meta
    }

    /**
     * get the url of a tree resource
     * @param format
     * @param treeid
     * @param studyid
     * @return a url string
     */
    def getTreeUrl( String format, treeid, studyid ){
        def url;
        def urlFormat
        switch (format){
            case 'newick':
                urlFormat = grailsApplication.config['newick_tree']
                url = urlFormat.replace('STUDYID',studyid.toString()).replace('TREEID',treeid.toString());
                break;
        }
        return url;
    }

    def getTreeUrlNewick( String treeId , String studyId ){
        return this.getTreeUrl('newick', treeId, studyId)
    }

    /**
     * create url that will fetch all studies
     */
    def getAllStudiesUrl(){
        def result = [:]
        result.url = grailsApplication.config.find_all_studies
        result.data = grailsApplication.config.find_all_studies_postdata
        return result
    }

    /**
     * create url that will search for trees with query term
     */
    def getSearchTreeUrl( query ){
        def result = [:]
        result.url = grailsApplication.config.treesearch_url
        result.data = grailsApplication.config.search_postdata.clone()
        result['data']['value'] = query;
        return result
    }
    /**
     * gets nexson for a study
     * @param studyId
     * @return
     */
    def getNexson( String studyId, String format = '0.0.0'){
        def url = grailsApplication.config['studyUrl']
        url = url.replaceAll( 'STUDYID', studyId.toString() )
        url = url.replaceAll( 'FORMAT', format )
        log.debug( url )
        return  webServiceService.getJson( url )
    }

    def convertNexmlToNexson( nexml ){
        return this.convertToNexson( nexml, 'nexml' )
    }

    def convertNewickToNexson( newick ){
        return this.convertToNexson( newick, 'newick' )
    }

    def convertToNexson(tree, format){
        def url = grailsApplication.config['to_nexson'];
        log.debug( url )
        def data = [:]

        // error saving tree with blank space in scientific name. BH-73
        switch (format){
            case 'newick':
                tree = tree?.replace(' ','_');
                break;
        }

        data['content'] = tree
        data['output'] = 'nexson'
        data['inputFormat'] = format
        data['nexml2json'] = grailsApplication.config[ 'nexml2json' ]
        def nexson = webServiceService.postData2( url, data, ['Accepts':'application/json'])
        log.debug('nexson returned is : ' + nexson)
        JSON.parse(nexson)
    }
}