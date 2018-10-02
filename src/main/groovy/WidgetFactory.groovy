package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class WidgetFactory {

    def createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr ){

        if( data == null){
            return
        }
        String type = data.config;
        if( type && type.startsWith('cl')){
            data.type = 'contextual';
        } else if ( type && type.startsWith('el')){
            data.type = 'environmental';
        } else {
            data.type = 'pd';
        }

        switch ( data.type ){
            case 'contextual':
                return new au.org.ala.phyloviz.ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
                break;
            case 'environmental':
                return new au.org.ala.phyloviz.EnvironmentalWidget( data, grailsApplication, webService, utilsService, applicationContext)
                break;
            case 'pd':
                return new au.org.ala.phyloviz.PDWidget( data, grailsApplication, webService, utilsService, applicationContext, dr )
                break;
        }
    }
}
