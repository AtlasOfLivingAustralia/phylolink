package au.org.ala.phyloviz

import java.util.Date

/**
 * Created by Temi Varghese on 17/06/2014.
 */
class Visualization {

    enum VizType{
        PhyloJive( 'PhyloJive' ),
        Argus( 'Argus' ),
        OneZoom( 'OneZoom' )
        final String value;
        VizType(String value){
            this.value = value
        }
        String toString(){
            value;
        }
        String getKey(){
            name()
        }
        static list(){
            [ PhyloJive ]
        }
    }
    static constraints = {
      viz( inList : VizType.list() , nullable : false)
    }
    VizType viz
    static hasMany = [ phylo : Phylo ]
    static belongsTo =  Phylo
}
