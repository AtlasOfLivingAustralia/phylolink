package au.org.ala.phyloviz

/**
 * Created by Temi Varghese on 17/06/2014.
 */
class Phylo {
    def Owner owner
    static belongsTo = [Owner]
    Integer id
    String treeid
    Integer nodeid
    Visualization viz
    Integer index
    String studyid
    String regionType
    String regionName
    String dataResource
    List widgets = new ArrayList()
    static hasMany = [ widgets: Widget ]
    static mapping = {
        widgets cascade:"all-delete-orphan"
    }
    static constraints = {
        treeid (nullable: false)
        nodeid (nullable: true)
        viz (nullable: false)
        index (nullable: true)
        studyid ( nullable: true)
        widgets (nullable:true)
        regionType(nullable: true, blank: false)
        regionName(nullable: true, blank: false)
        dataResource(nullable: true, blank: true)
        owner( nullable: true)
    }

//    def beforeValidate(){
//            log.debug( 'match found!' )
//            treeid = treeid.replaceAll(studyid + '_','' );
//    }
}