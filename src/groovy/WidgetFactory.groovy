package au.org.ala.phyloviz

import org.apache.commons.logging.LogFactory;
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class WidgetFactory {
    private static final log = LogFactory( this )
    static def createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr ){
        if( data == null){
            return ;
        }
        switch ( data.type ){
            case 'contextual':
                log.debug( ' creating contextual')
                return new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
                break;
            case 'environmental':
                log.debug( ' creating env')
                return new EnvironmentalWidget( data, grailsApplication, webService, utilsService, applicationContext)
                break;
            case 'pd':
                log.debug( ' creating pd')
                return  new PDWidget( data, grailsApplication, webService, utilsService, applicationContext, dr )
                break;
        }
    }
}
