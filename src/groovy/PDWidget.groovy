package au.org.ala.phyloviz;
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
//        this.phyloController.setParams( data )

        return this.phyloController.getPDCalc( phylo.treeid, phylo.studyid, null, data.speciesList );
    }
}
