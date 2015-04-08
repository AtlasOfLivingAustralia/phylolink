
package au.org.ala.phyloviz

import jade.tree.JadeNode
import jade.tree.JadeTree
import jade.tree.TreeReader

class MetricsService {
    def grailsApplication
    def webService

    def getJadeTree( String treeText ){
        def tReader = new TreeReader();
        def tree = tReader.readTree(treeText);
        return tree;
    }

    def pd( String treeText, speciesList ) {
        def tree = this.getJadeTree( treeText );
        return this.pd(tree, speciesList)
    }

    def pdByTraversal( JadeNode node ){
        Double sum = 0;
        if( node && node.getObject('pd') ){
            node.children?.each {v->
                sum += this.pdByTraversal( v )
            }
            sum += node.getBL();
        }
        return sum;
    }

    def pd ( JadeTree tree , speciesList ){
        def node,i,j
        def val
        def start,end
        speciesList = speciesList.clone()

        //do a intersection of speciesList and leaf node names to get the smallest array
        def intersect = []
        def leaves = this.getLeafNames( tree )
        def leavesSmall = [:]
        def lcase
        for( i =0; i< leaves.size();i++){
            lcase = leaves[i].trim().toLowerCase()
            leavesSmall[ lcase ] = leaves[i]
            leaves[i] = lcase;
        }
        leavesSmall
        for(i=0;i<speciesList.size();i++){
           speciesList[i] = speciesList[i].trim().toLowerCase()
        }
        speciesList = speciesList.intersect(  leaves )

        start = System.currentTimeMillis();
        //get list of decedent nodes that are in speciesList ie. create a white list
        for ( i =0; i < speciesList.size(); i++ ){
           val  = speciesList[i]
           node = tree.getExternalNode( leavesSmall[val] )
           while( node ){
               node.assocObject('pd',true)
               node = node.parent
           }
        }
        def sum = this.pdByTraversal( tree.getRoot() );
        end = System.currentTimeMillis();
        log.debug( "pd elapsed time" + (end -start))
        return ['pd':sum.trunc(4),'taxaRecognised':speciesList];
    }

    def getTree( String format, String treeId, String studyId){
        def treeUrl = this.getTreeUrl(format, treeId , studyId )
        return  webService.get( treeUrl )
    }

    /**
     * do string manipulation on tree example remove all quotes etc.
     * @param tree
     * @return String - processed tree
     */
    def treeProcessing(String tree){
        return tree.replace('\'', '').replace('_',' ');
    }

    def getLeafNames( JadeTree tree ){
        def i = 0, node, result =[]
        for( i = 0; i< tree.externalNodeCount; i++ ){
            node = tree.getExternalNode( i );
            result.push( node.getName() );
        }
        return result
    }

    def maxPd( String tree ){
        def treeObj = this.getJadeTree( tree )
        def leaves = this.getLeafNames( treeObj );
        def maxPd = [:]
        maxPd = this.pd( tree, leaves)
        return maxPd.pd
    }
}