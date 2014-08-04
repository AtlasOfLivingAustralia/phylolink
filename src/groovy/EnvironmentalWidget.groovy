package au.org.ala.phyloviz;
import grails.converters.JSON

/**
 * Created by Temi Varghese on 1/08/2014.
 */
class EnvironmentalWidget implements  WidgetInterface{
    def webService
    def grailsApplication
    def config
    def layer
    def region
    EnvironmentalWidget(config , grailsApplication, webService){
        this.webService = webService;
        this.grailsApplication = grailsApplication;
        this.config = config;
        this.layer = config.config
        this.region = config.region
    }
    def getViewFile(){
        return 'environmental';
    }
    def getInputFile(){
        return 'environmentalInput';
    }
    def process( data ){
        data.speciesList = JSON.parse( data.speciesList );
        def summary = this.getIntersections( data.speciesList )
        return  this.toGoogleColumnChart( summary )
    }
    def getIntersections( species ){
        def summary = [:]
        for( speciesName in species ){
            speciesName = speciesName.replaceAll(' ', '%20')
            def occurrenceUrl = this.grailsApplication.config.occurrences.replace('SEARCH', speciesName).replace('LAYER',this.layer).replace('REGION',this.region)
//            "http://biocache.ala.org.au/ws/occurrences/search?q=${speciesName.replaceAll(' ', '%20')}&facets=${layer}&fq=${region}"
            println( occurrenceUrl );
            def occurrencesResult = JSON.parse( this.webService.get( occurrenceUrl ) );
            occurrencesResult = occurrencesResult?.facetResults[0]
            if( occurrencesResult?.fieldResult ){
                for( def i = 0 ; i < occurrencesResult.fieldResult?.size(); i++ ){
                    def v = occurrencesResult.fieldResult[ i ];
                    v.label = v.label? v.label : 'n/a';
                    // this is important as it is getting summary for all the species list received.
                    if(  summary[v.label] ){
                        summary[v.label] += v.count;
                    } else {
                        summary[v.label] = v.count;
                    }


                }

            }
        }
        return  summary;
    }
    def toGoogleColumnChart( summary ){
        def result = []
        summary = summary.sort{ it.key }
//        if( layer.contains('el') ) {
//            println( 'parsing to double')
            summary.each() { k, v ->
                result.push([ Double.parseDouble( k ), v]);
            }
//        } else {
//            summary.each() { k, v ->
//                result.push([k, v]);
//            }
//        }
        if( result.size() != 0 ){
            result.add(0, ['Character','Occurrences'])
        } else {
            result.push( ['Character','Occurrences'] );
            result.push( ['',0] );
        }
        return ['data':result];
    }
}
