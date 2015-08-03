package au.org.ala.phyloviz

class Tree {
    def Owner owner
    static belongsTo = [Owner]
    Integer id
    String reference
    Integer year
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
    String notes
    static constraints = {
        tree(nullable: false, widget: 'textarea')
        year(nullable: true)
        reference(nullable: true, widget: 'textarea')
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
        notes(nullable: true, widget: 'textarea')
    }

    static mapping = {
        nexson type: "text"
        tree type: "text"
        reference type: 'text'
        notes type: "text"
    }

//    def beforeValidate(){
//        return false;
//    }
}
