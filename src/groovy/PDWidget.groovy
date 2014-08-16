package au.org.ala.phyloviz
import grails.converters.JSON
import groovy.json.JsonOutput

/**
 * Created by Temi Varghese on 1/08/2014.
 */
class PDWidget implements WidgetInterface{
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
    }
    def getViewFile(){
        return '';
    }
    def getInputFile(){
        return '';
    }
    def process( data, phylo ){
        println('pd widgets')
//        return  this.testData()
//        this.phyloController.setParams( data )
        def result = []
        def threads =[]
        def removeTree = (data.removeTree?:true) as Boolean

        regions.each{ r->
//            println( r )
            def th
            th = Thread.start {
                def speciesList,pd ;
                speciesList = this.getSpeciesList( r );
//                println( speciesList )
                speciesList = utilsService.convertJsonToArray( speciesList, grailsApplication.config.alaWebServiceMeta['speciesfacet'] )
//                println( 'convert to array ')
//                println( speciesList)
                pd = this.phyloController.getPDCalc( phylo.treeid, phylo.studyid, null, JsonOutput.toJson( speciesList ) );
                pd = pd[0]
//                println( 'printing region code')
//                println( pd )
                pd['region'] = r.split(":")[1];
                if( removeTree ){
                    pd.remove(grailsApplication.config.treeMeta.treeText)
                }
                result.push( pd )
            }
            threads.push( th );

        }
//        println( result )
        for( def th in threads){
            th.join();
        }
        return result;
    }
    def getRegions(){

    }
    def getSpeciesList( region ){
        def url = grailsApplication.config.speciesListUrl;
        url = url.replaceAll( 'REGION', region.encodeAsURL() )
        println( url )
        def species =  webService.get( url );
        species = utilsService.convertCsvToJson( species, null )
        return  species;
    }
    def testData(){
        def test = "\n" +
                "\n" +
                "[{\"region\":\"Nandewar\",\"pd\":[0.0],\"maxPd\":[4229.3054]},{\"region\":\"Darwin Coastal\",\"pd\":[0.0],\"maxPd\":[4229.3054]}]";
        return JSON.parse( test )
    }
}
