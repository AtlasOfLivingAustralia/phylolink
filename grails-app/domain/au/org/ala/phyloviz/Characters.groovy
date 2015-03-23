package au.org.ala.phyloviz

class Characters {
    def Owner owner
    static belongsTo = [Owner]
    String title
    String drid
    static constraints = {
        owner(nullable: false)
        title(nullable: false)
        drid(nullable: false)
    }
}