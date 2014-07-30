
package au.org.ala.phyloviz

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
    def pd ( JadeTree tree , speciesList ){
        def i = 0, j =0,k =0;
        def node;
        def cSize = tree.externalNodeCount
        def rSize = tree.internalNodeCount
        def testChild
        def val
        def matrix = new Double[rSize][cSize + rSize]

        for( i = 0; i < rSize; i ++ ){
            for( j = 0; j < cSize + rSize; j++ ){
                node = tree.getInternalNode( i );
                if( j < cSize ){
                    testChild = tree.getExternalNode( j )
                } else {
                    testChild = tree.getInternalNode( j - cSize );
                }

                if( node.hasChild( testChild )  ){
                    matrix[i][j]  = testChild.getBL();
                    // naming all child
                    testChild.assocObject('position',"${i},${j}");
                } else {
                    matrix[i][j]  = 0;
                }

            }
        }

        def flag = false;

        //do a intersection of speciesList and leaf node names to get the smallest array
        speciesList = speciesList.intersect( this.getLeafNames( tree ))

        //get list of decedent nodes that are in speciesList ie. create a white list
        def whiteList = []
        for ( i =0; i < speciesList.size(); i++ ){
           val  = speciesList[i]
           node = tree.getExternalNode(val)
           while( node ){
               whiteList.push( node )
               node = node.parent
           }
        }
        whiteList = whiteList.unique();

        //create an inverse list
        def whiteListPosition = [:]
        for ( i =0; i < whiteList.size(); i++ ){
            whiteListPosition[ whiteList[i].getObject( 'position' ) ] = whiteList[i];
        }
//        //get nodes from white list and find their positions in matrix
//        def whiteListPositionMat = [:]
//        for( i = 0; i < rSize; i ++ ) {
//            for (j = 0; j < cSize + rSize; j++) {
//                if( j < cSize ){
//                    node = tree.getExternalNode( j )
//                } else {
//                    node = tree.getInternalNode( j - cSize );
//                }
//            }
//        }

        for( i = 0; i < rSize; i ++ ) {
            for( j = 0; j < cSize + rSize; j++ ) {
                if( j < cSize ){
                    node = tree.getExternalNode( j )
                } else {
                    node = tree.getInternalNode( j - cSize );
                }
                // do it only if the value is non zero.
                if ( whiteListPosition[ "${i},${j}" ] == null ){
                    matrix[i][j] = 0
                }
//                if( matrix[i][j]){
//                    flag = true
//                    for( k =0 ; k < whiteList.size(); k++){
//                        testChild = whiteList[k]
//                        if( node == testChild){
//                            flag = false;
//                        }
//                    }
//                    if( flag ){
//                        matrix[i][j] = 0
//                    }
//
//                }
            }
        }

        def sum = 0;
        for( i = 0 ; i < rSize; i++){
            for( j = 0 ; j < rSize+cSize; j++ ){
                sum += matrix[i][j];
            }
        }
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
        return tree.replace('\'', '')
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