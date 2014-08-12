package au.org.ala.phyloviz

import grails.transaction.Transactional
import net.sf.json.JSONObject
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import au.com.bytecode.opencsv.CSVWriter

@Transactional
class UtilsService {
    def metricsService
    def grailsApplication
    def opentreeService
    def utilsService
    def webService
    LinkGenerator grailsLinkGenerator
    def getViewerUrl( treeId , studyId , meta){
        meta = meta?:[:]
        meta[grailsApplication.config.treeMeta.treeUrl] = "${grailsLinkGenerator.link(controller:'viewer', action:'show',absolute: true)}?studyId=${studyId}&treeId=${treeId}"
        return meta
    }
    def map( obj, map){
        def result
        if( obj instanceof net.sf.json.JSONArray){
            // if an array. problem is string and array have similar function names like size, getAt
//            println('found an array')
            result=[]
            obj.eachWithIndex { def entry, int i ->
                result.push( this.map(entry, map ))
            }
        }else if( obj instanceof JSONObject){
            // check if the variable is a map
//            println('found a map')
            result = [:]
            obj.each{k,v->
                if( map[k] ){
                    result[ map[k] ] = this.map( v, map )
                }
            }
        } else {
//            println('found a string or integer')
            result = obj
        }
        return  result;
    }
    /**
     * an array of hash value are summarized. var contains the variable to summarize and num contains the variable with number
     * @param data
     * @param var
     * @param num
     * @return
     */
    def summarize( data, var, num){
        def summary = [:]
        data?.eachWithIndex{ map, index->
            // this is important as it is getting summary for all the species list received.
            if(  summary[ map[ var] ] ){
                summary[map[ var]] += map[ num ];
            } else {
                summary[map[ var]] = map[ num ];
            }
        }
        return summary
    }
    /**
     * assuming json param will be an array of objects. and all object will have the same number of properties
     * @param json[[a:'1',b:'2'],[a:'3',b:'4']]
     */
    def convertJSONtoCSV( json ){
        if( json.size() == 0 ){
            return
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream()
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter( bytes ))
        def header = []
        // simple header
        json[1]?.each {k,v->
            header.push( k )
        }
        csvWriter.writeNext( header as String[] )
        json.eachWithIndex{ prop, index->
            def row = []
            for( def i =0;i<header.size();i++){
                row.push( prop[header[i]])
            }
            csvWriter.writeNext( row as String[])
        }
        csvWriter.flush()
        def csv = bytes.toString("UTF-8")
        csvWriter.close()
        return csv
    }
    /**
     * convert an object into format that can be displayed as column graphs on google charts
     */

    def toGoogleColumnChart( summary, isNumber ){
        def result = []
        summary = summary.sort{ it.key }
        if( isNumber ) {
            summary.each() { k, v ->
                result.push([ Double.parseDouble( k ), v]);
            }
        } else {
            summary.each() { k, v ->
                result.push([k, v]);
            }
        }
        if( result.size() != 0 ){
            result.add(0, ['Character','Occurrences'])
        } else {
            result.push( ['Character','Occurrences'] );
            result.push( ['',0] );
        }
        return result;
    }
    def googleChartOptions( title, haxis){
        def result = [:]
        result['title'] = title
        result['hAxis'] =['title':haxis, titleTextStyle: ["color": "red"]]
        return result;
    }
}

