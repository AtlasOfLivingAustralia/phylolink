package au.org.ala.phyloviz

class Sandbox {
    String title
    String drid
    String scientificName
    String serverInstance
    Owner owner
    Boolean status
    static constraints = {

    }
    static mapping = {
        status defaultValue: false
    }
}
