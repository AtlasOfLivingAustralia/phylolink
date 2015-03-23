package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 1/08/2014.
 */
//import org.apache.commons.logging.LogFactory;
class WidgetFactory {
//    static final log = LogFactory( this )
    public def createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr ){
//        return;
        if( data == null){
            return ;
        }
        String type = data.config;
//        String type = '';
//        log.debug('in factory')
//        log.debug(data.config)
        if( type && type.startsWith('cl')){
            data.type = 'contextual';
        } else if ( type && type.startsWith('el')){
            data.type = 'environmental';
        } else{
            data.type = 'pd';
        }

        switch ( data.type ){
            case 'contextual':
//                log.debug( ' creating contextual')
                return new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
                break;
            case 'environmental':
//                log.debug( ' creating env')
                return new EnvironmentalWidget( data, grailsApplication, webService, utilsService, applicationContext)
                break;
            case 'pd':
//                log.debug( ' creating pd')
                return  new PDWidget( data, grailsApplication, webService, utilsService, applicationContext, dr )
                break;
        }
    }
//    public def createWidget( data, grailsApplication, webService, utilsService, applicationContext){
//        return new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
//    }
}
