import au.org.ala.phyloviz.Characters
import au.org.ala.phyloviz.Owner
import au.org.ala.phyloviz.Phylo
import au.org.ala.phyloviz.Tree
import grails.util.Environment

class BootStrap {
    def opentreeService
    def utilsService
    def grailsApplication

    def init = { servletContext ->

        //update alaDataresourceInfo with biocache urls
        grailsApplication.config.alaDataresourceInfo.biocacheServiceUrl = grailsApplication.config.biocacheServiceUrl
        grailsApplication.config.alaDataresourceInfo.biocacheHubUrl = grailsApplication.config.biocacheHubUrl
        grailsApplication.config.alaDataresourceInfo.layerUrl = grailsApplication.config.biocacheServiceUrl + grailsApplication.config.alaDataresourceInfo.layerUrl
        grailsApplication.config.alaDataresourceInfo.title = "All occurrences"
        grailsApplication.config.alaDataresourceInfo.type = "ala"
        grailsApplication.config.alaDataresourceInfo.id = -1
    }

    def destroy = {}
}
