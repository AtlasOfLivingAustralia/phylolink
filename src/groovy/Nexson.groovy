/**
 * Created by Temi Varghese on 28/10/2014.
 */

package au.org.ala.phyloviz
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement

/**
 * a class to extract meta data from nexson files. like title, study id, doi, treebase id etc
 */

class Nexson {
//    private static final log = LogFactory( this )
    private String text
    private def json
    private def nexml
//    public static final mapping = org.codehaus.groovy.grails.commons.ConfigurationHolder.config['nexmlMetaMapping']
    private static final supportedVersion = ['1.2.1']
    private static final versionProp = '@nexml2json'
    public Nexson( String nexson ){
        text = nexson
        json = JSON.parse( nexson )
    }

    public Nexson( JSON json ){
        this.json = json
    }

    public Nexson( JSONElement json ){
        this.json = json
    }

    /**
     * get the version of nexson
     * @return null or version number
     */
    public getVersion(){
        if(json == null || json['data']['nexml'][this.versionProp] == null ||
                !(json.data.nexml[this.versionProp] in supportedVersion) ){
            return
        }
        return json.data.nexml[ this.versionProp ]
    }

    public String getTitle(){
        if( nexml == null ){
            println('nexml is null')
            return
        }
        println( mapping.doi )
    }

    public String getCitation(){
        if( nexml == null ){
            println('nexml is null')
            return
        }
    }
    public getPathValue( String path ){
        println( path )
        def pathArray = path.split('/'), i;
        def val = json;
        println( json )
        for( i = 0; ( ( i <  pathArray.size() ) ); i++ ){
            val = val[ pathArray[i] ];
            println( val )
        }
        return  val;
    }
}