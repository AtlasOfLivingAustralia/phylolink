package au.org.ala.phyloviz

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import grails.converters.JSON
import grails.transaction.Transactional
import net.sf.json.JSONObject
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.util.regex.Pattern

@Transactional
class UtilsService {
    def grailsApplication
    def opentreeService
    def webService
    def alaService
    def treeService

    def log = LogFactory.getLog(getClass())
    LinkGenerator grailsLinkGenerator

    def getViewerUrl(treeId, studyId, meta) {
        meta = meta ?: [:]
        meta[grailsApplication.config.treeMeta.treeUrl] =
                "${grailsLinkGenerator.link(controller: 'viewer', action: 'show', absolute: true)}?studyId=${studyId}&treeId=${treeId}"
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
            result.add(0, ['Character', 'Occurrence count'])
        } else {
            result.push(['Character', 'Occurrence count']);
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

    /**
     * give a citation and parse it into respective elements
     */
    def parseCitation( String cite ){
        if( cite == null){
            return
        }
        log.debug( cite )
        def url = grailsApplication.config['citationParser'];
        def data = [
                'citation':cite
        ]
        def xml = webService.postData(url, data, ['Accept':'application/json'])
        log.debug( xml.toString() )
        def result = [:]
        result['title'] = xml[0]?.title;
        result['year'] = xml[0]?.year
        result['fullCitation'] = cite
        result['authors'] = xml[0]?.authors
        result['doi'] = xml[0]?.doi
        result['journal'] = xml[0]?.journal
        result['pages'] = xml[0]?.pages
        result['volume'] = xml[0]?.volume
        return result
    }

    def autocomplete( q ){
        def url = grailsApplication.config.autocompleteUrl
        log.debug( url )
        url = url.replace( 'QUERY', q )
        def result = webService.get( url )
        result = JSON.parse( result )
        log.debug(result)
        return result?.searchResults?.results
    }

    /**
     * checks separator for csv file
     * @param raw
     * @return
     */
    def getSeparator(String raw) {
        String firstLine = raw.indexOf("\n") > 0 ? raw.substring(0, raw.indexOf("\n")) : raw

        int tabs = firstLine.count("\t")
        int commas = firstLine.count(",")

        tabs > commas ? '\t' : ','
    }

    /**
     * finds the separator used by file
     * @param file
     * @return
     */
    String detectSeparator(CommonsMultipartFile file) {
        file.getInputStream().withReader { r -> getSeparator(r.readLine()) }
    }

    /**
     * creates reader for multipart file
     * @param file
     * @param separator
     * @return
     */
    def getCSVReaderForCSVFileUpload(CommonsMultipartFile file, char separator) {
        new CSVReader(new InputStreamReader(file.getInputStream()), separator)
    }

    /**
     * create guest account or retrieve the guest account
     * @return
     */
    def guestAccount(){
        def guest = Owner.findByDisplayName("Guest")
        if (!guest) {
            guest = new Owner(
                    userId: 2,
                    displayName: "Guest",
                    email: "phylolink@ala.org.au",
                    created: new Date(),
                    role: "user"
            ).save(flush: true, failOnError: true)
        }
        return guest
    }
}