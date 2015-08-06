package au.org.ala.phyloviz

import grails.converters.XML
import groovy.json.JsonSlurper
import jade.tree.TreeReader
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import javax.xml.parsers.DocumentBuilderFactory

class TreeService {
    def opentreeService
    def alaService
    def grailsApplication
    def authService
    def nexsonService
    def metricsService
    def webService

    LinkGenerator grailsLinkGenerator

    def treeInfo(nexson) {
        def result = [:], taxon
        def otus, recogNames = 0, isAussie = 0;
        otus = this.treeNamedNodes(nexson)

        result['numberOfNamedNodes'] = otus?.size()
        log.debug(otus)
        def names = []
        (0..<otus.size()).each { i ->
            names.push(otus[i].name)
        }

        def lsid = alaService.getLsid(names)
        if( lsid?.error ){
            result = lsid
        } else {
            lsid = lsid.resp;
            log.debug(lsid)
            (0..<lsid.size()).each { index ->
                if (lsid[index]) {
                    taxon = lsid[index]

                    // check if taxon is in australia
                    taxon.isAustralian == 'recorded' ? isAussie++ : 0;
                    recogNames++;
                }
            }
            result['recognisedNames'] = recogNames;
            result['australianCount'] = isAussie;
        }

        return result
    }

    /**
     * get a list of all nodes that are named
     * @param nexson
     */
    def treeNamedNodes(nexson) {
        if (!nexson) {
            return
        }
        def otus = nexson.data.nexml.otus, node, meta, otu, leaf, result = [], o
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus
        log.debug(trees.size())

        // now get the name of the otus
        for (def i = 0; i < otus.size(); i++) {
            o = otus[i].otu;
            for (def j = 0; j < o.size(); j++) {
                node = [:]
                otu = o[j]
                node.otuid = otu['@id']
                node.name = otu['@label']
                result.push(node)
            }
        }
        return result
    }

    /**
     *
     * @param nexson
     * @param allNodes
     */
    def getLeaves(nexson, allNodes) {
        if (!nexson) {
            return
        }

        def leaves = [], nodes, meta, otus, i
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus.otu
        log.debug(trees.size())

        for (i = 0; i < trees.size(); i++) {
            // if tree id is provided then get leaves only for that tree otherwise all leaves
            if (((trees[i]['@id'] == tree) || (tree == null))) {
//                log.debug('in trees')
                nodes = trees[i].node;
                for (def j = 0; ((j < nodes.size())); j++) {
                    for (def k = 0; (nodes[j].meta && (k < nodes[j].meta.size())); k++) {
                        meta = nodes[j].meta

                        if ((meta['@property'][0] == 'ot:isLeaf') && (meta['$'][0] == true)) {
                            leaves.push(['otu': nodes[j]['@otu'], 'id': nodes[j]['@id']])
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    def treeLeafNodes(nexson) {
        def allNodes = this.treeNamedNodes(nexson);
        def leaves = this.getOnlyLeaves(nexson, allNodes);
    }

    /**
     * extract leaves from all trees in a study or a tree
     * @param nexson
     * @param tree
     */
    def getLeavestest(nexson, tree) {

        if (!nexson) {
            return new Exception("nexson file not provided");
        }
        def leaves = [], nodes, meta, otus, i, otu, leaf
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus.otu
        log.debug(trees.size())

        // now get the name of the otus
        for (i = 0; i < leaves.size(); i++) {
            leaf = leaves[i]

            for (def j = 0; j < otus.size(); j++) {
                otu = otus[j]
                if (otu['@id'] == leaf.otu) {
                    for (def k = 0; (otu.meta && (k < otu.meta.size())); k++) {
                        meta = otu.meta[k]
                        if (meta['@property'] == "ot:originalLabel") {
                            leaf.name = meta['$']
                        }
                    }
                }
            }
        }
        return leaves
    }

    /**
     * get tree meta
     */
    def studyInfo(nexson) {
        Nexson nxon = new Nexson(nexson);
        def meta = [:]
//        meta['title'] = nxon.getTitle();
//        meta['citation'] = nxon.getCitation()
        return meta
    }

    /**
     * guess tree file format
     *
     */
    def guessFormat(String tree) {
        def formats = [ 'nexml', 'nexus', 'newick' ]
        def result;
        log.debug( formats )
        for (def i = 0; i < formats.size(); i++) {
            switch (formats[i]) {
                case 'nexml':
                    log.debug('nexml')
                    result = isNexml(tree) ? 'nexml' : null;
                    break
                case 'newick':
                    log.debug('newick')
                    result = isNewick(tree) ? 'newick' : null;
                    break;
                case 'nexus':
                    log.debug('nexus')
                    result = isNexus(tree) ? 'nexus' : null;
                    break;
            }
            if( result != null ){
                return result
            }
        }
    }

    def isNexml( String tree) {
        try {
            XML.parse(tree)
            return true
        } catch (Exception e) {
            log.debug('not nexml: ' + e.getMessage())
            return false;
        }
    }

    def isNewick( String tree) {
        def newick, reader

        reader = new TreeReader()
        try{
            newick = reader.readTree(tree);
        }catch ( Exception e){
            log.debug( 'Exception:' + e.getMessage() )
            return false
        }
        return true
    }

    def isNexus( String tree ){
        return tree ==~ /(?s)^\#NEXUS.*/
    }

    /**
     * get meta data from a given tree
     */
    def getTreeMeta( String tree ){
        def meta = [:], nex
        meta['format'] = this.guessFormat( tree )
        log.debug( meta['format'] )
        switch ( meta['format'] ){
            case 'nexml':
                nex = opentreeService.convertNexmlToNexson( tree )
                nex = new Nexson( nex )
                nex.getMeta( meta )
                break;
        }
        return meta;
    }

    /**
     * create Tree instance
     * @param 
     * treep - tree parameters
     */
    def createTreeInstance( treep ){
        def tree;
        log.debug('in create tree instance function:' + authService.getUserId())
        treep.created = new Date()
//        userService.registerCurrentUser()
        def user = Owner.findByUserId( authService.getUserId()?:-1 )

        log.debug( user.toString() )
        if( treep.tree && treep.treeFormat && user ){
            try {
                log.debug('before convert nexson')
                treep.nexson = opentreeService.convertToNexson(treep.tree, treep.treeFormat);
//                return
                if(treep.nexson == null){
                    return;
                }

                log.debug('after convert nexson')
                treep.nexson = treep.nexson.toString()
                treep.owner = user
                log.debug('before tree instance creation')
                tree = new Tree( treep )
                if( tree.save( flush: true ) ){
                    log.debug('tree saved to database.' + tree.getId())
//                    elasticService.indexDoc(elasticService.getNEXSON_INDEX(), tree.getId(), tree.getNexson());
                }
            } catch (Exception e) {
                log.debug( 'exception while converting tree to nexson' + e.getMessage() )
                return
            }

        }
        return  tree;
    }

    /**
     * gets the updated otus and saves them to nexson object and then to database
     * @param otus
     * @param study
     * @return
     */
    def saveOtus( otus, study ){
        def nexson = new Nexson( study.getNexson() )

        nexsonService.updateOtus( otus , nexson )
        study.setNexson( nexson.getTree() )
        if( study.save( flush: true ) ){
//            elasticService.indexDoc(elasticService.getNEXSON_INDEX(), study.getId(), study.getNexson());
        }
    }

    def searchTreebase( query ){
        query = query.replace(' ','%20');
        def json = [], id
        def urlStr = "http://treebase.org/treebase-web/phylows/study/find?format=rss1&recordSchema=study&query=dcterms.title==\"${query}\"";
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        urlStr = uri.toURL().toString();
        log.debug( urlStr )
        def rss = webService.get( urlStr );
        log.debug( rss );
        if( rss ){
            def df = DocumentBuilderFactory.newInstance();
            def builder =  df.newDocumentBuilder()
            try{
                def rsser = builder.parse( IOUtils.toInputStream( rss,'UTF-8' ) )
                def nodelist = rsser.getElementsByTagName('item');
                log.debug("getting a lot of names")
                Iterator iter = nodelist.iterator();
                def cIter;
                while(iter.hasNext()){
                    def link = [:];
                    Object item = iter.next();
                    def children = item.childNodes;
                    cIter = children.iterator();
                    while( cIter.hasNext() ){
                        def child = cIter.next();
                        switch ( child.getNodeName() ){
                            case 'description':
                                id = child.getTextContent()
                                link.description = id
                                break;
                            case 'title':
                                link.title = child.getTextContent()
                                break;
                            case 'dcterms:bibliographicCitation':
                                link.reference = child.getTextContent()
                                break;
                            case 'prism:publicationDate':
                                link.year = child.getTextContent()
                                break;
                            case 'prism:publicationName':
                                link.publication = child.getTextContent();
                                break;
                            case 'link':
                                link.url = child.getTextContent()+'?format=nexml';
                                break;
                            case 'prism:doi':
                                link.doi = child.getTextContent()?:null;
                                if( link.doi ){
                                    link.doiUrl = this.doiResolution(link.doi)
                                }
                                break;
                        }
                    }
                    json.push( link );
                }
            } catch (Exception e ){
                return "NEXML is not well formed"
            }
        }
        return json;
    }

    /**
     * convert doi number to resolvable address
     * @param doi
     * @return
     */
    def doiResolution( doi){
        return grailsApplication.config['doiAddress'] + doi
    }


    /**
     * get nexml from TreeBASE
     */
    def importTB( url ){
        return webService.getXml( url )
    }


    /**
     *
     * @param meta
     * metadata variable to which the result of this function gets added to
     * @return
     */
     def getExpertTreeMeta( meta ) {
        meta = meta?:[:]
        Tree.findAllWhere(['expertTree':true]);
        def trees = grailsApplication.config.expert_trees, i, studyId , treeId, input = [], studyMeta, temp
        for( i = 0;i < trees.size(); i++ ){
            if( trees[i][grailsApplication.config.treeMeta.treeText] == null ){
                studyId = trees[i].studyId?.toString()
                treeId = trees[i].treeId?.toString()
                studyMeta = getTreeMeta( treeId, studyId, trees[i] )
                input.push( studyMeta.clone() )
            } else {
                input.push( trees[i].clone() )
            }
        }

        meta[ grailsApplication.config.expertTreesMeta.et ] = input
        return  meta
    }

    /**
     * attaches metadata of tree onto given metadata variable
     * @param treeId
     * @param studyId
     * @param meta
     * @return
     */
     def getTreeMeta(String treeId, String studyId){
         Integer id = Integer.parseInt(studyId)
         Tree [] trees = Tree.findById(id)
         List result = [];
         Integer i
         Object temp
         ConvertTreeToObject to = new ConvertTreeToObject();
         for( i = 0;i < trees.size(); i++ ){
             temp = to.convert(trees[i]);
             temp = opentreeService.addTreeMeta(metricsService.getJadeTree(
                     trees[i][grailsApplication.config.treeMeta.treeText] ), temp);
             getViewerUrl(null,trees[i].getId(), temp);
             result.push( temp )
         }
         return  result
    }

    /**
     * get a tree for an id.
     * @param id
     * @return
     */
    def getTree( Integer id){
        Tree tree = Tree.findById(id);
        ConvertTreeToObject to = new ConvertTreeToObject();
        tree = to.convert(tree);
        return tree
    }

    /**
     * this func creates a url and fetches its newick string
     * @param treeId
     * @param studyId
     * @param meta
     * @return
     */
    def getTreeText(String treeId, String studyId, meta){
        meta = meta?:[:]
        log.debug(studyId);
        studyId = Integer.parseInt(studyId);
        String tree = Tree.findById(studyId).tree;
        meta[grailsApplication.config.treeMeta.treeText] = tree;
        return meta
    }

    def getViewerUrl(treeId, studyId, meta) {
        meta = meta ?: [:]
        meta[grailsApplication.config.treeMeta.treeUrl] =
                "${grailsLinkGenerator.link(controller: 'viewer', action: 'show', absolute: true)}?studyId=${studyId}&treeId=${treeId}"
        return meta
    }

    def removeProp( Collection meta, String prop){
        for ( def i = 0 ; i < meta.size(); i++){
            meta[i]?.remove( prop )
        }
        return meta;
    }

    def removeProp( HashMap meta , String prop ){
        meta?.remove( prop )
        return meta;
    }

    /**
     * get expert trees from database
     * return: an array of expert trees
     */
    public def getExpertTrees(noTreeText){
        convertTreesToObjects(Tree.findAllByExpertTree(true), !noTreeText)
    }

    List<Tree> getPublicTrees() {
        Tree.findAllByHide(false)?.sort { it.title?.toLowerCase() }
    }

    Tree toggleExpertTree(treeId, expertTreeTaxonomy = null, expertTreeLsid = null, expertTreeId = null) {
        Tree tree = Tree.findById(treeId)

        tree.expertTree = !tree.expertTree
        tree.expertTreeTaxonomy = expertTreeTaxonomy
        tree.expertTreeID = expertTreeId
        tree.expertTreeLSID = expertTreeLsid

        tree.save(flush: true)

        tree
    }

    private convertTreesToObjects(List<Tree> trees, boolean includeTreeText = true) {
        ConvertTreeToObject cv = new ConvertTreeToObject();

        List treeObjects = trees.collect {
            Map treeObject = cv.convert(it)
            treeObject = opentreeService.addTreeMeta(metricsService.getJadeTree(it[grailsApplication.config.treeMeta.treeText]), treeObject);

            getViewerUrl(null, it.getId(), treeObject);

            if (!includeTreeText) {
                treeObject.remove(grailsApplication.config.treeMeta.treeText)
            }

            treeObject
        }

        treeObjects
    }

    /**
     * this function calculated pd for a supplied tree.
     * @param treeId
     * @param studyId
     * @param tree
     * @param speciesList
     * @return
     */
    def getPDCalc( String treeId, String studyId, String tree, String speciesList){
        def startTime, deltaTime
        def treeUrl, type, i,pd, sList;
        def studyMeta = [:], result =[], trees = [], input =[]
        type = tree?"tree":studyId?"gettree":"besttrees"
        switch (type){
            case 'tree':
                studyMeta [grailsApplication.config.treeMeta.treeText]=tree
                studyMeta [ grailsApplication.config.studyMetaMap.name ]= message(code: 'phylo.userTreeName', default: 'User tree' )
                studyMeta = opentreeService.addTreeMeta(metricsService.getJadeTree( tree ), studyMeta )
                input.push( studyMeta )
                break;
            case 'gettree':
                studyMeta = this.getTreeMeta(treeId, studyId )
                input= studyMeta;
                break;
            case 'besttrees':
                startTime = System.currentTimeMillis()
                input = this.getExpertTrees(false);
                deltaTime = System.currentTimeMillis() - startTime
                log.debug( "time elapse: ${deltaTime}")
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

    /**
     * can the current user access the tree
     * @param id
     * @return
     */
    def canAccess(id){
        return true;
    }

    def saveTitle(Phylo phyloInstance, title){
        String userId = authService.getUserId();
        Owner user;

        if( userId ){
            user = Owner.findByUserId(userId)
        } else {
            return [ 'message' : 'Cannot find your user details on system. Contact adminstrator.' ];
        }

        def result = [:]
        log.debug(user)
        log.debug(phyloInstance.getOwner()?.userId)
        if(phyloInstance.getOwner()?.userId == user.userId){
            phyloInstance.setTitle(title);
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
        return result;
    }

    /**
     * get current owner
     */
    def getCurrentOwner(){
        def id = authService.getUserId()
        Owner.findByUserId(id)
    }

    def search(String q){
        def owner = getCurrentOwner();
        def result = []
        def cv = new ConvertTreeToObject();
        def trees = Tree.withCriteria {
            ilike('tree',"%${q}%") && ( eq('expertTree', true) || eq('owner', owner));
        }

        trees.each{tree->
            result.push(cv.convert(tree));
        }
        return result;
    }

    def deleteTree(Integer treeId) {
        Tree.findById(treeId)?.delete()
    }
}
