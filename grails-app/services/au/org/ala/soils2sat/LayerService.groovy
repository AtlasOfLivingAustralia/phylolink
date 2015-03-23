package au.org.ala.soils2sat
import grails.converters.JSON
import grails.transaction.Transactional
import grails.plugin.cache.Cacheable

@Transactional
class LayerService {

    def grailsApplication
    def logService

    @Cacheable("S2S_LayerCache")
    def getLayerInfo(String layerName) {
        def results = "{}"
        def url = new URL("${grailsApplication.config.spatialPortalRoot}/ws/layer/${layerName}")
        results = url.getText()
        return JSON.parse(results)
    }
}
