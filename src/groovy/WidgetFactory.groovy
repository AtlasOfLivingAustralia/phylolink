package au.org.ala.phyloviz;
/**
 * Created by Temi Varghese on 1/08/2014.
 */
class WidgetFactory {
    static def createWidget( data, grailsApplication, webService ){
        if( data == null){
            return ;
        }
        switch ( data.type ){
            case 'contextual':
                println( ' creating contextual')
                return new ContextualWidget( data, grailsApplication, webService )
                break;
            case 'environmental':
                println( ' creating env')
                return new EnvironmentalWidget( data, grailsApplication, webService )
                break;
            case 'pd':
                println( ' creating pd')
                return  new PDWidget( data, grailsApplication, webService )
                break;
        }
    }
}
