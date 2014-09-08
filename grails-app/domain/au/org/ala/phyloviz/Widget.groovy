package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 23/06/2014.
 */
class Widget {
//    def id
    String config
    String displayname
    String title
    String type
    String region
    String data
////    WidgetType type
    static constraints = {
//        id(nullable:false)
        region ( nullable: true)
        config(nullable: false)
        displayname(nullable: false)
        data ( nullable: true, widget:'textarea')
        title ( nullable: true)
//        type(inList: WidgetType.list(), nullable: false)
    }
    static hasMany=[ vizBook :Phylo ]

    static belongsTo = Phylo

//    Widget( String layer){
//
//        config = '{"layer":"'+layer+'"}'
//    }
}
