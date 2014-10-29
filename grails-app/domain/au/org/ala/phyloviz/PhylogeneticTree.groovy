package au.org.ala.phyloviz

class PhylogeneticTree {
    String reference
    int year
    String format
    String title
    String tag
    boolean hide
    String doi
    String tree
    Long userId
    String displayName
    Date created
    static constraints = {
        tree ( nullable: false, widget:'textarea' )
        year ( nullable:false )
        reference ( nullable: false, widget:'textarea' )
        title( nullable: false, widget:'textarea')
        format ( nullable: false )
        tag ( nullable: true )
        hide ( nullable: false)
        doi ( nullable: true)
        userId ( unique: true)
        displayName ( maxSize: 200)
    }
}
