package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 23/06/2014.
 */
class Widget {
    String config
    String displayname
    String title
    String type
    String region
    String data
    static constraints = {
        region ( nullable: true)
        config(nullable: false)
        displayname(nullable: false)
        data ( nullable: true, widget:'textarea')
        title ( nullable: true)
    }
    static hasMany=[ vizBook :Phylo ]
    static belongsTo = Phylo
}
