package au.org.ala.phyloviz


class TreeService {
    def opentreeService
    def alaService
    def treeInfo( nexson ) {
        def result= [:], taxon
        def otus, recogNames = 0, isAussie = 0;
        otus = this.treeNamedNodes( nexson )

        result['numberOfNamedNodes'] = otus?.size()
        log.debug( otus )
        def names = []
        (0..<otus.size()).each{ i ->
            names.push( otus[i].name )
        }

        def lsid = alaService.getLsid( names )
        lsid = lsid.resp;
        log.debug( lsid )
        (0..<lsid.size()).each{ index ->
            if( lsid[index]){
                taxon = lsid[index]

                // check if taxon is in australia
                taxon.isAustralian == 'recorded'? isAussie ++ : 0;
                recogNames ++;
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
    def treeNamedNodes( nexson ){
        if( !nexson ){
            return
        }
        def otus = nexson.data.nexml.otus, node, meta, otu, leaf, result = [],o
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus
        log.debug( trees.size() )

        // now get the name of the otus
        for( def i = 0; i < otus.size(); i++) {
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
    def getLeaves( nexson, allNodes ){
        if( !nexson ){
            return
        }

        def leaves =[], nodes, meta, otus, i
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus.otu
        log.debug( trees.size() )

        for( i=0; i < trees.size(); i++){
            // if tree id is provided then get leaves only for that tree otherwise all leaves
            if((( trees[i]['@id'] == tree ) || (tree == null)) ){
//                log.debug('in trees')
                nodes = trees[i].node;
                for( def j =0; ((j < nodes.size())); j++){
                    for( def k = 0; (nodes[j].meta && (k < nodes[j].meta.size())); k++ ){
                        meta = nodes[j].meta

                        if( (meta['@property'][0] == 'ot:isLeaf') && (meta['$'][0] == true)){
                            leaves.push( ['otu':nodes[j]['@otu'],'id':nodes[j]['@id'] ])
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    def treeLeafNodes( nexson ){
        def allNodes  = this.treeNamedNodes( nexson );
        def leaves = this.getOnlyLeaves( nexson, allNodes );
    }


    /**
     * extract leaves from all trees in a study or a tree
     * @param nexson
     * @param tree
     */
    def getLeavestest( nexson, tree){

        if( !nexson ){
            return new Exception("nexson file not provided");
        }
        def leaves =[], nodes, meta, otus, i, otu, leaf
        def trees = nexson.data.nexml.trees.tree;
        otus = nexson.data.nexml.otus.otu
        log.debug( trees.size() )



        // now get the name of the otus
        for( i = 0; i < leaves.size(); i++){
            leaf = leaves[i]

            for( def j = 0; j < otus.size(); j++){
                otu = otus[j]
                if( otu['@id'] == leaf.otu ){
                    for( def k = 0; (otu.meta && (k < otu.meta.size())); k++){
                        meta = otu.meta[k]
                        if( meta['@property'] == "ot:originalLabel"){
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
    def studyInfo( nexson ){
        Nexson nxon = new Nexson( nexson );
        def meta = [:]
//        meta['title'] = nxon.getTitle();
//        meta['citation'] = nxon.getCitation()
        return meta
    }
}
