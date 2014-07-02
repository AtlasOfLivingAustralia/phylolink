package au.org.ala.phyloviz;
/**
 * Created by Temi Varghese on 23/06/2014.
 */
public enum WidgetType{
    Environmental('environmental'),
    Contextual('contextual'),
    Map('map'),
    Metrics('metrics'),
    Scatter('scatter plot')
    String id
    WidgetType( String id){
        this.id = id
    }
    static list(){
        [Environmental,Contextual,Map,Metrics,Scatter]
    }
}