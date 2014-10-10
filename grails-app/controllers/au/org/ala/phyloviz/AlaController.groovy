package au.org.ala.phyloviz

import grails.converters.JSON
import au.org.ala.soils2sat.LayerTreeNode;
import au.org.ala.soils2sat.LayerDefinition;

class AlaController {
    def webService
    def utilsService
    def layerService

    def index() {

    }

    def getOccurrenceRecords(speciesName, layer, region){
        layer = layer ?: ''
        region = region ?: ''
        def occurrenceUrl = this.grailsApplication.config.occurrences.replace('SEARCH', speciesName.encodeAsURL()).replace('LAYER',layer.encodeAsURL()).replace('REGION',region.encodeAsURL())
        def occurrencesResult = JSON.parse( webService.get( occurrenceUrl ) );
        return occurrencesResult;
    }

    def extractFacets( occurrencesResult,speciesName ){
        def v, var, summary = []
        def config = grailsApplication.config.intersectionMeta
        occurrencesResult = occurrencesResult?.facetResults[0]
        if ( occurrencesResult?.fieldResult ) {
            for( def i = 0 ; i < occurrencesResult.fieldResult?.size(); i++ ){
                var = [:]
                v = occurrencesResult.fieldResult[ i ];
                v.label = v.label?: 'n/a';
                var[config.name] = speciesName
                var[config.var] = v.label
                var[config.count] = v.count
                summary.push( var )
            }
        }
        return summary
    }

    def getIntersections( species, layer, region ){
        def summary = [], config
        def occurrencesResult
        config = grailsApplication.config.intersectionMeta
        for( speciesName in species ){
            occurrencesResult = this.getOccurrenceRecords( speciesName, layer, region)
            summary.addAll( this.extractFacets( occurrencesResult, speciesName ))
        }
        return  summary;
    }

    def getOccurrenceIntersections(){
        def species = JSON.parse( params.species )
        def download = params.download?:"false"
        def layer = params.layer
        def region = params.region
        download = download.toBoolean();
        def data = this.getIntersections( species, layer, region )

        if( download ){
            response.setHeader('Content-disposition','attachment; filename=data.csv')
            render ( contentType: 'text/plain', text: utilsService.convertJSONtoCSV(data) )
        } else {
            render ( contentType: 'text/plain', text: data as JSON )
        }

    }

    def getEnvLayers(){
        def data = this.getLayers( grailsApplication.config.layersMeta.env )
        render(contentType: 'application/json', text: data as JSON )
    }

    def getClLayers(){
        def data = this.getLayers( grailsApplication.config.layersMeta.cl )
        render(contentType: 'application/json', text: data as JSON )
    }

    def getLayers( type ){
        def result =[]
        def url = grailsApplication.config.layers
        def data = JSON.parse( webService.get( url ) )
        def code='';

        switch ( type ){
            case grailsApplication.config.layersMeta.cl:
                code = 'cl';
                break;
            case grailsApplication.config.layersMeta.env:
                code = 'el';
                break;
        }
        data.eachWithIndex { def entry, int i ->
            if( type == entry.type ){
                entry.id = code + entry.id
                entry.label = entry.displayname
                result.push( entry )
            }
        }
        return result;
    }

    def browseLayersFragment() {
        log.debug( params.type )
        log.debug( grailsApplication.config.layersMeta.env )
        def layerData = getLayers( params.type );
        def results = new ArrayList<LayerDefinition>()
        layerData.each {
            def layer = new LayerDefinition()
            it.each {
                if (it.value && layer.hasProperty(it.key)) {
                    layer[it.key] = it.value
                }
            }
            results.add(layer)
        }
        def root = new LayerTreeNode(label: 'Unclassified')
        results.each {
            if (it.classification1) {
                def topLevelFolder =  root.getOrAddFolder(it.classification1)

                if (it.classification2) {
                    def secondLevel = topLevelFolder.getOrAddFolder(it.classification2)
                    secondLevel.addLayer(it)
                } else {
                    topLevelFolder.addLayer(it)
                }
            } else {
                root.addLayer(it)
            }
        }
        [layerTree: root]
    }

    def layerSelectionDialog(){

    }

    def layerSummaryFragment(){
        def layerName = params.layerName;
        def info = [:]
        LayerDefinition layerDefinition = null
        if (layerName) {
            info = layerService.getLayerInfo(layerName)
            layerDefinition = new LayerDefinition()
            info.each {
                if (it.value && layerDefinition.hasProperty(it.key)) {
                    layerDefinition[it.key] = it.value
                }
            }
        }
        [layerName: layerName, layerDefinition: layerDefinition]
    }
}