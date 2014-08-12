package au.org.ala.phyloviz;
/**
 * Created by Temi Varghese on 1/08/2014.
 */
public interface WidgetInterface {
//    def grailsApplication
//    def webService
//    def config
    def process( data, phylo )
    def getViewFile()
    def getInputFile()
}
