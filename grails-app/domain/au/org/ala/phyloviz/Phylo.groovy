package au.org.ala.phyloviz

/**
 * Created by Temi Varghese on 17/06/2014.
 */
class Phylo {
    Integer id
    Long userId
    String treeid
    Integer nodeid
    String displayName
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
        displayName (nullable: true)
        viz (nullable: false)
        userId (unique: true, nullable: true)
        index (nullable: true)
        studyid ( nullable: true)
        widgets (nullable:true)
        regionType(nullable: true, blank: false)
        regionName(nullable: true, blank: false)
        dataResource(nullable: true, blank: true)
    }

    def beforeValidate(){
            log.debug( 'match found!' )
            treeid = treeid.replaceAll(studyid + '_','' );
    }
}