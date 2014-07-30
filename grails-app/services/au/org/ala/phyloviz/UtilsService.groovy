package au.org.ala.phyloviz

import grails.transaction.Transactional
import net.sf.json.JSONObject
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

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
}

