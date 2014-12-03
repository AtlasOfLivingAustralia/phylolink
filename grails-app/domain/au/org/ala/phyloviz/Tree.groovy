package au.org.ala.phyloviz

class Tree {
    def Owner owner
    static belongsTo = [Owner]
    Integer id
    String reference
    int year
    String treeFormat
    String title
    String tag
    boolean hide
    String doi
    String tree
    Date created
    String nexson
    boolean expertTree
    String expertTreeTaxonomy
    String expertTreeLSID
    String expertTreeID
    static constraints = {
        tree(nullable: false, widget: 'textarea')
        year(nullable: false)
        reference(nullable: false, widget: 'textarea')
        title(nullable: false, widget: 'textarea')
        treeFormat(nullable: false)
        tag(nullable: true)
        hide(nullable: false)
        doi(nullable: true)
        nexson(nullable: true)
        expertTree(nullable: false)
        expertTreeTaxonomy(nullable: true)
        expertTreeLSID(nullable: true)
        expertTreeID(nullable: true)
    }

    static mapping = {
        nexson type: "text"
        tree type: "text"
        reference type: 'text'
    }

//    def beforeValidate(){
//        return false;
//    }
}
