package au.org.ala.phyloviz

import grails.converters.JSON
import groovy.json.JsonOutput
import org.apache.commons.logging.LogFactory

/**
 * Created by Temi Varghese on 1/08/2014.
 */

class PDWidget implements au.org.ala.phyloviz.WidgetInterface{

    def webService
    def grailsApplication
    def utilsService
    def applicationContext
    def config
    def layer
    def region
    def regions
    def alaController
    def phyloController
    def dr
    def biocacheServiceUrl

    PDWidget(config , grailsApplication, webService, utilsService, applicationContext){
        this.webService = webService;
        this.grailsApplication = grailsApplication;
        this.utilsService = utilsService
        this.applicationContext = applicationContext
        this.config = config;
        this.layer = config.config
        this.region = config.region
        this.regions = JSON.parse( config.data )
        this.alaController =applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','ala').clazz.name );
        this.phyloController =applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','phylo').clazz.name );
        this.biocacheServiceUrl = config.biocacheServiceUrl?:this.grailsApplication.config.biocacheServiceUrl
    }

    PDWidget(config , grailsApplication, webService, utilsService, applicationContext, dr){
        this.webService = webService;
        this.grailsApplication = grailsApplication;
        this.utilsService = utilsService
        this.applicationContext = applicationContext
        this.config = config;
        this.layer = config.config
        this.region = config.region
        this.regions = config.data ? JSON.parse( config.data ) : []
        this.alaController = applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','ala').clazz.name );
        this.phyloController = applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','phylo').clazz.name );
        this.dr = dr
        this.biocacheServiceUrl = config.biocacheServiceUrl?:this.grailsApplication.config.biocacheServiceUrl
    }

    def getViewFile(){
        return '';
    }

    def getInputFile(){
        return '';
    }

    def process( data, phylo ){

//        log.debug('pd widgets')
        def result = []
        def threads =[]
        def removeTree = (data.removeTree?:true) as Boolean

        regions.each { r->
            def th
            th = Thread.start {
                def speciesList,pd ;
                speciesList = this.getSpeciesList( r.code, biocacheServiceUrl );
//                log.debug( r.code )
//                log.debug( 'convert to array ' + r.code)
//                log.debug( speciesList )
                pd = this.phyloController.getPDCalc( phylo.treeid, phylo.studyid, null, JsonOutput.toJson( speciesList ) );
                pd = pd[0]
//                log.debug( 'printing region code')
                pd['region'] = r.code.split(":")[1];
                if( removeTree ){
                    pd.remove(grailsApplication.config.treeMeta.treeText)
                }
                result.push( pd )
            }
            threads.push( th );

        }
        for( def th in threads){
            th.join();
        }
//        log.debug('completed!')
        return result;
    }

    def getRegions(){}

    def getSpeciesList( region, biocacheServiceUrl ){
        def startTime, deltaTime;
        def url = grailsApplication.config.speciesListUrl.replace("BIOCACHE_SERVICE", biocacheServiceUrl);

        if( dr ){
            url = grailsApplication.config.drUrl.replace("BIOCACHE_SERVICE", biocacheServiceUrl)
        }
        url = url.replaceAll( 'REGION', region.encodeAsURL() )
//        log.debug( url )

        startTime = System.currentTimeMillis()
        def species =  webService.get( url );
        deltaTime = System.currentTimeMillis() - startTime
//        log.debug( "download time: ${deltaTime}")
        startTime = System.currentTimeMillis()
        species = utilsService.convertCsvToArray( species, null, grailsApplication.config.alaWebServiceMeta['speciesfacet'])
        deltaTime = System.currentTimeMillis() - startTime
//        log.debug( "convert time: ${deltaTime}")
        return  species;
    }

    def testData(){
        def test = "\n" +
                "\n" +
                "[{\"region\":\"Nandewar\",\"pd\":[0.0],\"maxPd\":[4229.3054]},{\"region\":\"Darwin Coastal\",\"pd\":[0.0],\"maxPd\":[4229.3054]}]";
        return JSON.parse( test )
    }
}