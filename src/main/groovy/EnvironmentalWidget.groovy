package au.org.ala.phyloviz

import grails.converters.JSON

/**
 * Created by Temi Varghese on 1/08/2014.
 */
class EnvironmentalWidget implements  au.org.ala.phyloviz.WidgetInterface{
    def webService
    def grailsApplication
    def utilsService
    def applicationContext
    def config
    def layer
    def region
    def alaController
    def biocacheServiceUrl

    EnvironmentalWidget(config , grailsApplication, webService, utilsService, applicationContext){
        this.webService = webService;
        this.grailsApplication = grailsApplication;
        this.utilsService = utilsService
        this.applicationContext = applicationContext
        this.config = config;
        this.layer = config.config ?  config.config + "_RNG" :''
        this.region = config.region?:''
        this.alaController = applicationContext.getBean( this.grailsApplication.getArtefactByLogicalPropertyName('Controller','ala').clazz.name );
        this.biocacheServiceUrl = config.biocacheServiceUrl?:this.grailsApplication.config.biocacheServiceUrl
    }

    def getViewFile(){
        return 'environmental';
    }

    def getInputFile(){
        return 'environmentalInput';
    }

    def process( data , phylo){
        def summary;
        if(data.q){
            summary = alaController.getQidIntersections( data.q, this.layer, this.region, this.biocacheServiceUrl );
        } else if (data.speciesList){
            data.speciesList = JSON.parse( data.speciesList?:'[]' );
            summary = alaController.getIntersections( data.speciesList, this.layer, this.region, this.biocacheServiceUrl );
        }

        def output = [:]
        def widgetConf = grailsApplication.config.widgetMeta
        if( summary?.error != null ){
            def summary2 = utilsService.summarize( summary, grailsApplication.config.intersectionMeta.var, grailsApplication.config.intersectionMeta.count )
            output[ widgetConf.data ] = utilsService.toGoogleColumnChart( summary2, false )
            output[widgetConf.chartOptions]= utilsService.googleChartOptions( this.config.type, this.config.displayname)
            return output
        } else {
            return summary
        }
    }
}