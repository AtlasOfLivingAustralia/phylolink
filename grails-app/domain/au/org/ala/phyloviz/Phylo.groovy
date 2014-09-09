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
//    List env = new ArrayList()
//    List widgets = new ArrayList<Widget>();
//    ArrayList<Widget> widgets;
    static hasMany = [ widgets: Widget ]
    static mapping = {
        widgets cascade:"all-delete-orphan"
    }
//    static belongsTo = Widget
    static constraints = {
        treeid (nullable: false)
        nodeid (nullable: true)
        displayName (nullable: true)
        viz (nullable: false)
        userId (unique: true, nullable: true)
//        env(nullable: true)
        index (nullable: true)
        studyid ( nullable: true)
        widgets (nullable:true)
        regionType(nullable: true, blank: false)
        regionName(nullable: true, blank: false)
        dataResource(nullable: true, blank: true)
    }
    def beforeValidate(){
//        if( treeid.matches(studyid)){
            println( 'match found!' )
            treeid = treeid.replaceAll(studyid + '_','' );
//        }
//        def widget;
//        for( def i in env ){
//            println( i.dump() )
//        }


//        for(def i = 0; i < env.size(); i++){
//            widget = env[i];
//            println( "class name:" )
//            println( widget )
////            env[i] = new Widget( widget ) ;
//        }

    }
//
//    def getWidgetsList() {
//        return LazyList.decorate(
//                widgets,
//                FactoryUtils.instantiateFactory( Widget.class )
//        )
//    }
    def getTreeUrl( format ){
        def url;
        def grailsapplication = this.getDomainClass().getGrailsApplication();
        switch (format){
            case 'newick':
                url = grailsapplication.config.treemachine.replace('STUDYID',studyid).replace('TREEID',treeid) + '.tre';
                break;
        }
        return url;
    }
}