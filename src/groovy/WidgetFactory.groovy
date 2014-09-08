package au.org.ala.phyloviz;
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class WidgetFactory {
    static def createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr ){
        if( data == null){
            return ;
        }
        switch ( data.type ){
            case 'contextual':
                println( ' creating contextual')
                return new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
                break;
            case 'environmental':
                println( ' creating env')
                return new EnvironmentalWidget( data, grailsApplication, webService, utilsService, applicationContext)
                break;
            case 'pd':
                println( ' creating pd')
                return  new PDWidget( data, grailsApplication, webService, utilsService, applicationContext, dr )
                break;
        }
    }
}
