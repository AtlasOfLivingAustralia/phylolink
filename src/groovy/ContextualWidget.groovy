package au.org.ala.phyloviz

import grails.converters.JSON;
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class ContextualWidget implements WidgetInterface{
    def webService
    def grailsApplication
    def utilsService
    def applicationContext
    def config
    def layer
    def region
    def alaController
    ContextualWidget(config , grailsApplication, webService, utilsService, applicationContext){
        this.webService = webService;
        this.grailsApplication = grailsApplication;
        this.utilsService = utilsService
        this.applicationContext = applicationContext
        this.config = config;
        this.layer = config.config
        this.region = config.region
        this.alaController =applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','ala').clazz.name );
    }
    def getViewFile(){
        return 'environmental';
    }
    def getInputFile(){
        return 'environmentalInput';
    }
    def process( data, phylo ){
        data.speciesList = JSON.parse( data.speciesList );
        def summary = alaController.getIntersections( data.speciesList, this.layer, this.region );
        def output =[:]
        def widgetConf = grailsApplication.config.widgetMeta
        if( summary.error != null){
            summary = utilsService.summarize( summary, grailsApplication.config.intersectionMeta.var, grailsApplication.config.intersectionMeta.count )
            output[ widgetConf.data ] = utilsService.toGoogleColumnChart( summary, false )
            output[widgetConf.chartOptions]= utilsService.googleChartOptions( this.config.type, this.config.displayname)
            return  output
        } else {
            return  summary;
        }
    }
}