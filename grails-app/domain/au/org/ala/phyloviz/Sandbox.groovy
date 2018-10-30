package au.org.ala.phyloviz

class Sandbox {
    String title
    String drid
    String scientificName
    String serverInstance
    String biocacheServiceUrl
    String biocacheHubUrl
    String phyloId
    Owner owner
    Boolean status
    Date dateCreated
    static constraints = {
        phyloId nullable: true
    }
    static mapping = {
        status defaultValue: false
    }
}
