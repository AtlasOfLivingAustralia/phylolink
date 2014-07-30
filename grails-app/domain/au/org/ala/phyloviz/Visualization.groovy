package au.org.ala.phyloviz

/**
 * Created by Temi Varghese on 17/06/2014.
 */
class Visualization {
    public enum VizType{
        PhyloJive('PhyloJive'),
        Argus('Argus'),
        OneZoom('OneZoom')
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
//            this.values()
//            [PhyloJive, Argus, OneZoom ]
            [PhyloJive ]
        }
    }
    static constraints = {
      viz(inList: VizType.list(),nullable: false)
    }
//    Integer viztype_id
    VizType viz
//    static transients = [ 'deleted' ]
    static hasMany = [ phylo:Phylo ]
    static belongsTo =  Phylo
}
