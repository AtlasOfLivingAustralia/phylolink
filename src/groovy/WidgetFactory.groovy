package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class WidgetFactory {
    public def createWidget( data, grailsApplication, webService, utilsService, applicationContext, dr ){
        if( data == null){
            return ;
        }
        String type = data.config;
        if( type && type.startsWith('cl')){
            data.type = 'contextual';
        } else if ( type && type.startsWith('el')){
            data.type = 'environmental';
        } else{
            data.type = 'pd';
        }

        switch ( data.type ){
            case 'contextual':
                return new ContextualWidget( data, grailsApplication, webService, utilsService, applicationContext )
                break;
            case 'environmental':
                return new EnvironmentalWidget( data, grailsApplication, webService, utilsService, applicationContext)
                break;
            case 'pd':
                return  new PDWidget( data, grailsApplication, webService, utilsService, applicationContext, dr )
                break;
        }
    }
}
