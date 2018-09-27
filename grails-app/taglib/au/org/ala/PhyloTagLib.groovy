package au.org.ala
import grails.converters.JSON

class PhyloTagLib {
//    static defaultEncodeAs = 'html'
    //static encodeAsForTags = [tagName: 'raw']
    static namespace = 'phy'

    def webServiceService
    /**
     * a json output of regions. These are mostly state boundaries and IBRA regions.
     */
    def reg = { attrs, body ->
        out << ( this.getRegions() as JSON).toString()
    }
    private  def getRegions(){
        def regions =[], json;
        def regionsUrl = grailsApplication.config.regionsUrl;
        regionsUrl.each{ type, url->
            regions.addAll( this.getRegionsByType( type ) );
        }
        return regions;
    }
    private  def getRegionsByType( String typeS ) {
        def regions = [], json;
        def regionsUrl = grailsApplication.config.regionsUrl[typeS];
        if( regionsUrl ) {
            json = JSON.parse(webServiceService.get( regionsUrl ) );
            for (name in json.names) {
                regions.push(["value": name, "type": typeS, "code":"${typeS}:${name}"]);
            }
        }
        return  regions;
    }
}
