package au.org.ala.phyloviz

import au.org.ala.phylolink.TrimOption
import grails.converters.JSON
import grails.converters.XML
import groovy.json.JsonSlurper
import jade.tree.JadeTree
import jade.tree.TreeReader
import org.apache.commons.io.IOUtils
import grails.web.mapping.LinkGenerator
import org.hibernate.Session

import javax.annotation.PostConstruct
import javax.xml.parsers.DocumentBuilderFactory

class TreeService {
    def opentreeService
    def alaService
    def grailsApplication
    def authService
    def nexsonService
    def metricsService
    def webServiceService
    NameService nameService

    LinkGenerator grailsLinkGenerator
    Integer BATCH_SIZE

    @PostConstruct
    init(){
        BATCH_SIZE = Integer.parseInt((grailsApplication.config.batchSize?:20).toString())
    }


    def treeInfo(nexson) {
        def result = [:], taxon
        def otus, recogNames = 0, isAussie = 0;
        otus = this.treeNamedNodes(nexson)

        result['numberOfNamedNodes'] = otus?.size()
        log.debug(String.valueOf(otus))
        def names = []
        (0..<otus.size()).each { i ->
            names.push(otus[i].name)
        }

        def lsid = alaService.getLsid(names)

        log.debug(String.valueOf(lsid))
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
        log.debug(String.valueOf(trees.size()))

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
        log.debug(String.valueOf(trees.size()))

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
        log.debug(String.valueOf(trees.size()))

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
        def formats = ['nexml', 'nexus', 'newick']
        def result;
        log.debug(String.valueOf(formats))
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
            if (result != null) {
                return result
            }
        }
    }

    def isNexml(String tree) {
        try {
            XML.parse(tree)
            return true
        } catch (Exception e) {
            log.debug('not nexml: ' + e.getMessage())
            return false;
        }
    }

    def isNewick(String tree) {
        def newick, reader

        reader = new TreeReader()
        try {
            newick = reader.readTree(tree);
        } catch (Exception e) {
            log.debug('Exception:' + e.getMessage())
            return false
        }
        return true
    }

    def isNexus(String tree) {
        return tree ==~ /(?s)^\#NEXUS.*/
    }

    /**
     * get meta data from a given tree
     */
    def getTreeMeta(String tree) {
        def meta = [:], nex
        meta['format'] = this.guessFormat(tree)
        log.debug(String.valueOf(meta['format']))
        switch (meta['format']) {
            case 'nexml':
                nex = opentreeService.convertNexmlToNexson(tree)
                nex = new au.org.ala.phyloviz.Nexson(nex)
                nex.getMeta(meta)
                break;
        }
        return meta;
    }

    /**
     * create Tree instance
     * @param
     * treep - tree parameters
     */
    def createTreeInstance(treep) {
        def tree;
        log.debug('in create tree instance function:' + authService.getUserId())
        treep.created = new Date()

        def userId = authService.getUserId()
        def user = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')

        log.debug(user.toString())
        if (treep.tree && treep.treeFormat && user) {
            try {
                log.debug('before convert nexson')
                treep.nexson = opentreeService.convertToNexson(treep.tree, treep.treeFormat);
                if (treep.nexson == null) {
                    return;
                }

                log.debug('after convert nexson')
                treep.nexson = treep.nexson.toString()
                treep.owner = user
                log.debug('before tree instance creation')
                tree = new Tree(treep)
                if (tree.save(flush: true)) {
                    log.debug('tree saved to database.' + tree.getId())
                }
            } catch (Exception e) {
                log.debug('exception while converting tree to nexson' + e.getMessage(), e)
                return
            }

        }
        return tree;
    }

    /**
     * gets the updated otus and saves them to nexson object and then to database
     * @param otus
     * @param study
     * @return
     */
    def saveOtus(otus, study) {
        def nexson = new au.org.ala.phyloviz.Nexson(study.getNexson())
        nexsonService.updateOtus(otus, nexson)
        study.setNexson(nexson.getTree())
        study.save(flush: true)
    }

    def searchTreebase(query) {
        query = query.replace(' ', '%20');
        def json = [], id
        def urlStr = "http://treebase.org/treebase-web/phylows/study/find?format=rss1&recordSchema=study&query=dcterms.title==\"${query}\"";
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        urlStr = uri.toURL().toString();
        log.debug(urlStr)
        def rss = webServiceService.get(urlStr);
        log.debug(String.valueOf(rss));
        if (rss) {
            def df = DocumentBuilderFactory.newInstance();
            def builder = df.newDocumentBuilder()
            try {
                def rsser = builder.parse(IOUtils.toInputStream(rss, 'UTF-8'))
                def nodelist = rsser.getElementsByTagName('item');
                log.debug("getting a lot of names")
                Iterator iter = nodelist.iterator();
                def cIter;
                while (iter.hasNext()) {
                    def link = [:];
                    Object item = iter.next();
                    def children = item.childNodes;
                    cIter = children.iterator();
                    while (cIter.hasNext()) {
                        def child = cIter.next();
                        switch (child.getNodeName()) {
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
                                link.url = child.getTextContent() + '?format=nexml';
                                break;
                            case 'prism:doi':
                                link.doi = child.getTextContent() ?: null;
                                if (link.doi) {
                                    link.doiUrl = this.doiResolution(link.doi)
                                }
                                break;
                        }
                    }
                    json.push(link);
                }
            } catch (Exception e) {
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
    def doiResolution(doi) {
        return grailsApplication.config['doiAddress'] + doi
    }

    /**
     * get nexml from TreeBASE
     */
    def importTB(url) {
        return webServiceService.getXml(url)
    }

    /**
     *
     * @param meta
     * metadata variable to which the result of this function gets added to
     * @return
     */
    def getExpertTreeMeta(meta) {
        meta = meta ?: [:]
        Tree.findAllWhere(['expertTree': true]);
        def trees = grailsApplication.config.expert_trees, i, studyId, treeId, input = [], studyMeta, temp
        for (i = 0; i < trees.size(); i++) {
            if (trees[i][grailsApplication.config.treeMeta.treeText] == null) {
                studyId = trees[i].studyId?.toString()
                treeId = trees[i].treeId?.toString()
                studyMeta = getTreeMeta(treeId, studyId, trees[i])
                input.push(studyMeta.clone())
            } else {
                input.push(trees[i].clone())
            }
        }

        meta[grailsApplication.config.expertTreesMeta.et] = input
        return meta
    }

    /**
     * attaches metadata of tree onto given metadata variable
     * @param treeId
     * @param studyId
     * @param meta
     * @return
     */
    def getTreeMeta(String treeId, String studyId) {
        Integer id = Integer.parseInt(studyId)
        Tree[] trees = Tree.findById(id)
        List result = [];
        Integer i
        Object temp
        ConvertTreeToObject to = new ConvertTreeToObject();
        for (i = 0; i < trees.size(); i++) {
            temp = to.convert(trees[i]);
            temp = opentreeService.addTreeMeta(metricsService.getJadeTreeFromNewick(
                    trees[i][grailsApplication.config.treeMeta.treeText]), temp);
            getViewerUrl(null, trees[i].getId(), temp);
            result.push(temp)
        }
        return result
    }

    /**
     * get a tree for an id.
     * @param id
     * @return
     */
    def getTree(Integer id) {
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
    def getTreeText(String treeId, String studyId, meta) {
        meta = meta ?: [:]
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

    def removeProp(Collection meta, String prop) {
        for (def i = 0; i < meta.size(); i++) {
            meta[i]?.remove(prop)
        }
        return meta;
    }

    def removeProp(HashMap meta, String prop) {
        meta?.remove(prop)
        return meta;
    }

    /**
     * get expert trees from database
     * return: an array of expert trees
     */
    public def getExpertTrees(noTreeText) {
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
            treeObject = opentreeService.addTreeMeta(metricsService.getJadeTreeFromNewick(it[grailsApplication.config.treeMeta.treeText]), treeObject);

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
    def getPDCalc(String treeId, String studyId, String tree, String speciesList) {
        def startTime, deltaTime
        def treeUrl, type, i, pd, sList;
        def studyMeta = [:], result = [], trees = [], input = []
        type = tree ? "tree" : studyId ? "gettree" : "besttrees"
        switch (type) {
            case 'tree':
                studyMeta[grailsApplication.config.treeMeta.treeText] = tree
                studyMeta[grailsApplication.config.studyMetaMap.name] = message(code: 'phylo.userTreeName', default: 'User tree')
                studyMeta = opentreeService.addTreeMeta(metricsService.getJadeTreeFromNewick(tree), studyMeta)
                input.push(studyMeta)
                break;
            case 'gettree':
                studyMeta = this.getTreeMeta(treeId, studyId)
                input = studyMeta;
                break;
            case 'besttrees':
                startTime = System.currentTimeMillis()
                input = this.getExpertTrees(false);
                deltaTime = System.currentTimeMillis() - startTime
                log.debug("time elapse: ${deltaTime}")
                break;
        }

        sList = new JsonSlurper().parseText(speciesList)
        for (i = 0; i < input.size(); i++) {
            studyMeta = [:]
            log.debug(String.valueOf(input[i]));
            input[i][grailsApplication.config.treeMeta.treeText] = metricsService.treeProcessing(input[i][grailsApplication.config.treeMeta.treeText])

            // calculate pd
            pd = metricsService.pd(input[i][grailsApplication.config.treeMeta.treeText], sList)
            input[i]['maxPd'] = metricsService.maxPd(input[i].tree)

            // merge the variables
            pd.each { k, v ->
                input[i][k] = v
            }

            result.push(input[i])
        }

        return result;
    }

    /**
     * can the current user access the tree
     * @param id
     * @return
     */
    def canAccess(id) {
        return true;
    }

    def saveTitle(Phylo phyloInstance, title) {
        String userId = authService.getUserId();
        Owner user;

        if (userId) {
            user = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')
        } else {
            return ['message': 'Cannot find your user details on system. Contact adminstrator.'];
        }

        def result = [:]
        if (phyloInstance.getOwner()?.userId == user.userId) {
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
        result
    }

    /**
     * get current owner
     */
    def getCurrentOwner() {
        def id = authService.getUserId()
        id != null ? Owner.findByUserId(id) : Owner.findByDisplayName('Guest')
    }

    def search(String q) {
        def owner = getCurrentOwner();
        def result = []
        def cv = new ConvertTreeToObject();
        def trees = Tree.withCriteria {
            ilike('tree', "%${q}%") && (eq('expertTree', true) || eq('owner', owner));
        }

        trees.each { tree ->
            result.push(cv.convert(tree));
        }
        return result;
    }

    List getSpeciesNamesFromTree(Integer treeId) {
        JadeTree tree = toJadeTree(treeId)

        metricsService.getLeafNames(tree)
    }

    JadeTree toJadeTree(Integer treeId) {
        Tree tree = Tree.findById(treeId)
        if (tree.treeFormat == "newick") {
            metricsService.getJadeTreeFromNewick(metricsService.treeProcessing(tree.tree))
        } else if (tree.treeFormat == "nexml") {
            metricsService.getJadeTreeFromNeXML(tree.tree)
        }
    }

    /**
     * Removes all character lists that do no contain any of the species in the specified tree
     *
     * @param treeId The ID of the tree to use for filtering
     * @param characterLists Collection of character lists to be filtered
     * @return Subset of the characterLists where at least 1 species in the tree is in each list
     */
    List filterCharacterListsByTree(Integer treeId, Collection characterLists) {
        List speciesNames = getSpeciesNamesFromTree(treeId)

        Map request = [scientificNames: speciesNames, drIds: characterLists.collect { it.dataResourceId }]

        String url = "${grailsApplication.config.listToolBaseURL}/ws/speciesList/filter"

        def filteredDrIds = webServiceService.doJsonPost(url, (request as JSON).toString())?.data

        characterLists.removeAll { !filteredDrIds.contains(it.dataResourceId) }

        characterLists
    }

    /**
     * Deletes the specified tree and any visualisations that were created from it
     *
     * @param treeId ID of the tree to delete
     */
    void deleteTree(Integer treeId) {
        List<Phylo> visualisations = Phylo.findAllByStudyid(treeId)
        visualisations*.delete(flush:true)

        Tree.findById(treeId)?.delete(flush:true)
    }

    /**
     * Trim the specified tree using the provided option.
     *
     * @param treeId Id of the tree to trim. The tree can be either newick or nexml
     * @param option The {@link TrimOption} to use
     * @param trimToInclude True to INCLUDE the identified species, false to EXCLUDE the identified species
     * @param data Additional data required by the trim option (e.g. species list DataResourceId if TrimOption#SPECIES_LIST is selected)
     * @return The trimmed tree in newick format.
     */
    Tree trimTree(Integer treeId, TrimOption option, boolean trimToInclude, data = null) {
        log.debug("Before : ${getSpeciesNamesFromTree(treeId).size()}")

        Tree trimmedTree

        switch (option) {
            case TrimOption.AUSTRALIAN_ONLY:
                List australianSpecies = getAustralianSpecies(treeId)
                trimmedTree = trim(treeId, australianSpecies, trimToInclude)
                break
            case TrimOption.ALA_ONLY:
                List alaRecognisedSpecies = getAlaRecognisedSpecies(treeId)
                trimmedTree = trim(treeId, alaRecognisedSpecies, trimToInclude)
                break
            case TrimOption.SPECIES_LIST:
                List species = getSpeciesFromList(data as String)
                trimmedTree = trim(treeId, species, trimToInclude)
                break
            case TrimOption.NONE:
            default:
                trimmedTree = Tree.findById(treeId)
                break
        }

        trimmedTree
    }

    private List getAustralianSpecies(Integer treeId) {
        List speciesNames = getSpeciesNamesFromTree(treeId)

        matchNames(getBieSpecies(speciesNames), speciesNames)
    }

    private List getAlaRecognisedSpecies(Integer treeId) {
        List speciesNames = getSpeciesNamesFromTree(treeId)

        matchNames(getBiocacheSpecies(speciesNames), speciesNames)
    }

    /**
     * Finds all items from the speciesInTree list where there is a corresponding item in the targetList, regardless of
     * the value.
     *
     * This allows the targetList to contain names in a different format (e.g. names returned from calls to the BIE or
     * Biocache may not be exactly the same as the name used for the search).
     *
     * For example, sending [Amytornis dorotheae] to the BIE will result in a targetList of [Amytornis (Amytornis) dorotheae]
     * due to the name matching rules that the BIE uses.
     *
     * This method assumes:
     * <ol>
     *     <li>That indices of the two lists match: i.e. the value in cell x of the speciesInTree list was used to find/produce the value in cell x of the targetList.</li>
     *     <li>If there was no match for the value from speciesInTree, then the corresponding cell in the target list will be null.</li>
     *     <li>We are only interested in finding items from speciesInTree list that have a match in targetList: we don't care what the value in the target list is.</li>
     * </ol>
     *
     * @param targetList
     * @param speciesInTree
     * @return
     */
    List matchNames(List targetList, List speciesInTree) {
        int index = 0
        targetList.collect {
            if (it?.name) {
                speciesInTree[index++]
            } else {
                index++
                null
            }
        }
    }

    private List getSpeciesFromList(String listDataResourceId) {
        if (!listDataResourceId) {
            throw new IllegalArgumentException("List Data Resource Id is required")
        }
        def data = webServiceService.getJson("${grailsApplication.config.listToolBaseURL}/ws/speciesListItems/${listDataResourceId}")

        data.findResults { it?.name } as HashSet
    }

    private List getBieSpecies(List speciesNames) {
        webServiceService.doJsonPost("${grailsApplication.config.bieRoot}/ws/species/lookup/bulk", "{\"names\": [\"${speciesNames.join("\",\"")}\"],\"vernacular\":true}").data
    }

    private List getBiocacheSpecies(List speciesNames) {
        List lsids = alaService.getLsid(speciesNames)
        String query = alaService.filterQuery(lsids, null, "lsid")

        String url = grailsApplication.config.qidUrl.replace("BIOCACHE_SERVICE", grailsApplication.config.biocacheServiceUrl)
        String qid = alaService.getQid(query, url, null)

        webServiceService.getJson("${grailsApplication.config.biocacheServiceUrl}/mapping/legend?q=qid:${qid}&cm=taxon_name&type=application/json")
    }

    private Tree trim(Integer treeId, List species, boolean trimToInclude) {
        JadeTree tree = toJadeTree(treeId)

        List leavesToTrim = tree.iterateExternalNodes().findAll { trimToInclude ? !species.contains(it.getName()) : species.contains(it.getName()) }

        log.debug("Dropping ${leavesToTrim.collect { it.getName() }}")

        log.debug("Before : ${tree.externalNodeCount}")

        String newTreeText
        if (leavesToTrim.size() != tree.externalNodeCount) {
            leavesToTrim.each {
                if (it.getParent() == null) {
                    println it.getName()
                }
                tree.pruneExternalNode(it)
            }

            newTreeText = "${tree.root.getNewick(true)};"

            log.debug("After : ${tree.externalNodeCount}")
        } else {
            log.warn("Trimmed tree has 0 nodes")
            newTreeText = "();"
        }

        Tree trimmedTree = new Tree(Tree.findById(treeId).properties)
        trimmedTree.id = null
        trimmedTree.created = null
        trimmedTree.version = null
        trimmedTree.treeFormat = "newick"
        trimmedTree.title = "${trimmedTree.title} (Trimmed}"
        trimmedTree.tree = newTreeText

        trimmedTree
    }

    /**
     * match the scientific name and LSID for all trees in database
     * @return Map []
     */
    public Map rematchAll(){
        Integer offset = 0
        Integer total = Tree.count()
        log.debug('Started rematching')

        // batch processing - faster and more memory efficient
        while (offset < total ){
            Tree.withSession { Session session ->
                // sorting on id since sometimes GORM/Hibernate returns a tree multiple times
                List trees = Tree.list(offset: offset, max: BATCH_SIZE, sort: 'id', order: 'asc')
                rematchTrees(trees)

                // commit to DB and free memory
                session.flush()
                session.clear()
            }

            offset = offset + BATCH_SIZE
            log.debug("Offset increased - ${offset}. Total iterations - ${total}.")
        }

        log.debug('Completed rematching')
        return [success: true, message: "The number of re-matched trees - ${total}"]
    }

    /**
     * For the provided list of trees, match scientific name and LSID
     * @param trees - [{@link Tree}, {@link Tree}]
     * @param flush - commit changes to db - Boolean
     * @return
     */
    def rematchTrees(List<Tree> trees, Boolean flush = false){
        Boolean flag = false
        trees?.each { Tree tree ->
            if(tree.nexson){
                au.org.ala.phyloviz.Nexson nex = new au.org.ala.phyloviz.Nexson(tree.nexson)
                // get node names
                List otus = nex.getOtus()
                otus?.each{ Map otu ->
                    if(otu){
                        String name = otu['^ot:altLabel'] ?: otu['^ot:originalLabel']
                        name = cleanNodeName(name)
                        // search name against ala name index
                        Map record = nameService.getRecord(name)
                        if(record.lsid && record.name){
                            // save matched name and LSID
                            nex.setAltLabel(otu.otuId, record.name)
                            nex.setAlaId(otu.otuId, record.lsid)
                        } else {
                            nex.setAltLabel(otu.otuId, name)
                            nex.setAlaId(otu.otuId, null)
                        }
                    }
                }

                // match LSID for expert tree
                if(tree.expertTree && tree.expertTreeTaxonomy) {
                    String expertLSID
                    Map expertMatch
                    expertMatch = nameService.getRecord(tree.expertTreeTaxonomy)
                    if(expertMatch.lsid) {
                       tree.expertTreeLSID = expertMatch.lsid
                    } else {
                        tree.expertTreeLSID = ''
                    }
                }

                // convert the updated JSON to string
                tree.nexson = nex.toString()
                tree.save(flush: flush)
            }
        }
    }

    /**
     * Clean terminal node name. Usually names have underscore and other unnecessary characters
     * @param name
     * @return
     */
    String cleanNodeName(String name){
        return name?.replaceAll("_", " ").replaceAll("'", "");
    }

    /**
     * Function to map a tree nodes to ALA taxonomy. It does automatic mapping on the following conditions.
     * This function can be accessed by admin or by owner of a tree.
     * 1. Checks if tree nodes are mapped to ALA taxonomy, then cancel auto mapping and return only tree nodes.
     * 2. If ALA taxonomy mapping not done, then do auto mapping and return all tree nodes.
     * @param tree {@link Tree}
     * @return
     */
    List getMappedOtus(Tree tree){
        au.org.ala.phyloviz.Nexson nex;
        List otus = [];
        if(tree.nexson){
            nex = new au.org.ala.phyloviz.Nexson( tree.nexson )
            otus = nex.getOtus()
            if(!areOtusMapped(otus)){
                otus = nexsonService.autoSuggest( otus )
            }
        }

        otus
    }

    /**
     * Checks if otus are mapped to a ALA taxonomy. True if mapped and false otherwise.
     * @param otus {@link List<Map>}
     * @return
     */
    public Boolean areOtusMapped(List otus){
        Boolean isMapped = false
        otus?.each { otu ->
            if(otu['@ala']){
                isMapped = true
            }
        }

        isMapped
    }
}