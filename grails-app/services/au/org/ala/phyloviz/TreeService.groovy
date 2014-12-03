package au.org.ala.phyloviz

import grails.converters.XML
import jade.tree.TreeReader
import org.apache.commons.io.IOUtils

import javax.xml.parsers.DocumentBuilderFactory

class TreeService {
    def opentreeService
    def alaService
    def grailsApplication
    def authService
    def nexsonService
    def elasticService
    def userService
    def webService
//    def utilsService

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
        def formats = grailsApplication.config['treeFormats']
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
//        treep.userId = authService.getUserId()
//        treep.displayName = authService.getDisplayName()
        log.debug('in create tree instance function:' + authService.getUserId())
        treep.created = new Date()
//        userService.registerCurrentUser()
        def user = Owner.findByUserId( authService.getUserId()?:-1 )

//        log.debug( treep )
        log.debug( user.toString() )
        if( treep.tree && treep.treeFormat && user ){
            try {
                log.debug('before convert nexson')
                treep.nexson = opentreeService.convertToNexson( treep.tree, treep.treeFormat );
                log.debug('after convert nexson')
                treep.nexson = treep.nexson.toString()
                treep.owner = user
                log.debug('before tree instance creation')
                tree = new Tree( treep )
                if( tree.save( flush: true, failOnError: true) ){
                    log.debug('tree saved to database.' + tree.getId())
//                    elasticService.indexDoc(elasticService.getNEXSON_INDEX(), tree.getId(), tree.getNexson());
                }
            } catch (Exception e) {
                log.debug( 'exception while converting tree to nexson' + e.getMessage() )
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
}
