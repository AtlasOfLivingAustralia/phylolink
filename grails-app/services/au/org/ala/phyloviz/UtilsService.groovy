package au.org.ala.phyloviz

import au.com.bytecode.opencsv.CSVWriter
import grails.transaction.Transactional
import net.sf.json.JSONObject
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import java.util.regex.Pattern

@Transactional
class UtilsService {
    def metricsService
    def grailsApplication
    def opentreeService
    def webService
    def alaService
    def treeService
    def log = LogFactory.getLog(getClass())
    LinkGenerator grailsLinkGenerator

    def getViewerUrl(treeId, studyId, meta) {
        meta = meta ?: [:]
        meta[grailsApplication.config.treeMeta.treeUrl] = "${grailsLinkGenerator.link(controller: 'viewer', action: 'show', absolute: true)}?studyId=${studyId}&treeId=${treeId}"
        return meta
    }

    def map(obj, map) {
        def result
        if (obj instanceof net.sf.json.JSONArray) {
            // if an array. problem is string and array have similar function names like size, getAt
            result = []
            obj.eachWithIndex { def entry, int i ->
                result.push(this.map(entry, map))
            }
        } else if (obj instanceof JSONObject) {
            // check if the variable is a map
            result = [:]
            obj.each { k, v ->
                if (map[k]) {
                    result[map[k]] = this.map(v, map)
                }
            }
        } else {
            result = obj
        }
        return result;
    }

    /**
     * an array of hash value are summarized. var contains the variable to summarize and num contains the variable with number
     * @param data
     * @param var
     * @param num
     * @return
     */
    def summarize(data, var, num) {
        def summary = [:]
        data?.eachWithIndex { map, index ->
            // this is important as it is getting summary for all the species list received.
            if (summary[map[var]]) {
                summary[map[var]] += map[num];
            } else {
                summary[map[var]] = map[num];
            }
        }
        return summary
    }

    /**
     * assuming json param will be an array of objects. and all object will have the same number of properties
     * @param json[[a :'1',b:'2'],[a:'3',b:'4']]
     */
    def convertJSONtoCSV(json) {
        if (json.size() == 0) {
            return
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream()
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(bytes))
        def header = []
        // simple header
        json[1]?.each { k, v ->
            header.push(k)
        }
        csvWriter.writeNext(header as String[])
        json.eachWithIndex { prop, index ->
            def row = []
            for (def i = 0; i < header.size(); i++) {
                row.push(prop[header[i]])
            }
            csvWriter.writeNext(row as String[])
        }
        csvWriter.flush()
        def csv = bytes.toString("UTF-8")
        csvWriter.close()
        return csv
    }

    /**
     * author: Nick dos Remedios
     * Convert CSV format to charJSON
     *
     * CVS format example:
     *
     * "scientificName","seed mass","Inflorescence colour","plant height","Phyllode length median","Inflorescence arrangement","Stipule length median","Inflorescence shape"," leaflet pairs 2nd leaf","range size","genome size","Phyllode arrangement","Pulvinus length median","first leaf pinnae pairs","section taxonomy"
     * "Acacia diphylla","5.4","unknown","","","unknown","","unknown","1.9","","","unknown","","2.0","unknown"
     * "Acacia courtii","","white to cream||pale yellow","20","115","simple","0.25","cylindrical","","2","","scattered","","","juliflorae"
     * te
     * Note, character values get converted to a JSON array and can thus have multiple values. This is achieved by using
     * an internal separator string (arg 2) with default value of "||".
     *
     * Can be tested with CharServiceTests.groovy. Output was checked to be valid JSON via http://jsonlint.com/
     *
     * @param charCsv
     * @param internalSeparator
     * @return
     */
    def convertCsvToJson(String charCsv, String internalSeparator) {
        def lineCount = 0;
        def headers = [] // Names of characters
        def charMap = [] // Map version of JSON format
        internalSeparator = Pattern.quote(internalSeparator ?: "||") // separator string needs escaping as per a regex
        log.debug "input csv = " + charCsv
        charCsv.eachCsvLine { tokens ->
            log.debug "tokens = " + tokens
            if (lineCount == 0) {
                // assume first line is header with character names
                headers = tokens // ignore first field as it is taxon name
            } else {
                // data lines
                def thisChars = [:]
                tokens.eachWithIndex() { obj, i ->
                    thisChars.put(headers[i], obj)
                }

                charMap.push(thisChars)
            }
            log.debug(lineCount + ". " + charMap);

            lineCount++;
        }

        return charMap
    }

    def convertCsvToArray(String charCsv, String internalSeparator, String columnName) {
        def lineCount = 0;
        def columnNumber = 0;
        def headers = [] // Names of characters
        def result = []
        internalSeparator = Pattern.quote(internalSeparator ?: "||") // separator string needs escaping as per a regex
        log.debug "input csv = " + charCsv
        charCsv.eachCsvLine { tokens ->
            log.debug "tokens = " + tokens
            if (lineCount == 0) {
                // assume first line is header with character names
                headers = tokens // ignore first field as it is taxon name
                headers.eachWithIndex { header, i ->
                    if (header == columnName) {
                        columnNumber = i;
                    }
                }
            } else {
                // data lines
                result.push(tokens[columnNumber]);
            }
            lineCount++;
        }

        return result
    }

    /**
     * convert an object into format that can be displayed as column graphs on google charts
     */

    def toGoogleColumnChart(summary, isNumber) {
        def result = []
        summary = summary.sort { it.key }
        if (isNumber) {
            summary.each() { k, v ->
                result.push([Double.parseDouble(k), v]);
            }
        } else {
            summary.each() { k, v ->
                result.push([k, v]);
            }
        }
        if (result.size() != 0) {
            result.add(0, ['Character', 'Occurrences'])
        } else {
            result.push(['Character', 'Occurrences']);
            result.push(['', 0]);
        }
        return result;
    }

    def googleChartOptions(title, haxis) {
        def result = [:]
        result['title'] = title
        result['hAxis'] = ['title': haxis, titleTextStyle: ["color": "red"]]
        return result;
    }

    /**
     * converts a json array of objects to an array of values
     * params
     * json - [{a:1,b:3},{a:2,b:6}]
     * var - 'a'
     * return - [1,2]
     */
    def convertJsonToArray(json, var) {
        def result = []
        json?.eachWithIndex { obj, index ->
            result.push(obj[var]);
        }
        return result;
    }

    /**
     * search for doi using given search terms
     * params
     * q - search terms
     *
     * return
     * [{
     *  doi:
     *  title:
     *  reference:
     *  year:
     * }]
     */
    def searchDoi( terms ){
        String url = grailsApplication.config.doiSearchUrl
        log.debug( url )
        url = url.replaceAll('SEARCH', terms );
        log.debug( url )
        def json = webService.getJson( url );
        return json;
    }

    /**
     * get all leaf from a tree or all trees in a study
     */
    def leafNodes( study , tree ){
        def nexson = opentreeService.getNexson( study )
        def leaves = treeService.getLeaves( nexson, tree )
        return leaves
    }

    def lookupLeafName( study , tree){
        def leaves = this.leafNodes( study, null )

        def names = [];
        for( def i = 0 ; i < leaves.size(); i++){
            names.push( leaves[i].name )
        }

        return alaService.getLsid( names );
    }
}