package au.org.ala.phyloviz;
/**
 * Created by Temi Varghese on 23/06/2014.
 */
public enum WidgetType{
    Environmental('Environmental Histogram'),
    Contextual('Contextual Histogram'),
    Map('Map'),
    PD('Phylogenetic Diversity'),
    Scatter('Scatter plot')
    String id
    WidgetType( String id){
        this.id = id
    }
    static list(){
//        [Environmental,Contextual,Map,Metrics,Scatter]
        [Environmental,Contextual,PD]
    }
}