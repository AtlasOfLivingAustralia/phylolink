package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 23/06/2014.
 */
class Widget {
//    def id
    String config
    String displayname
////    WidgetType type
    static constraints = {
//        id(nullable:false)
        config(nullable: false)
        displayname(nullable: false)
//        type(inList: WidgetType.list(), nullable: false)
    }
    static hasMany=[ vizBook :Phylo ]

    static belongsTo = Phylo

//    Widget( String layer){
//
//        config = '{"layer":"'+layer+'"}'
//    }
}
