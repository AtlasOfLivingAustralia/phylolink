package au.org.ala.phyloviz

import au.org.ala.web.AuthService
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.regex.Pattern

@Transactional
class CharactersService {
    def speciesListService
    def g
    def webService
    def grailsApplication
    AuthService authService

    def getCharacterListsByOwner() {
        def id = authService.getUserId()
        log.debug(id)
        def owner = Owner.findByUserId(id)
        def lists = Characters.findAllByOwner(owner);
        def slist = Characters.findAllByOwner(Owner.findByUserId(1));
        lists.addAll(slist);
        log.debug(lists)

        getCharUrl(lists);
    }

    def getCharUrl( ArrayList<Characters> lists ) {
        def result=[]
        lists.each{ list->
            result.push([
                'dataResourceId': list.drid,
                'url': getUrl(list.drid),
                'title':list.title,
                'id': list.id,
                'listurl': grailsApplication.config.listsPermUrl.replace('DRID', list.drid)
            ]);
        }
        return result
    }

    /**
     *
     * @param druid
     * @return
     */
    def getUrl(druid){
        if(!g) {
            g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
        }
        return g.createLink(controller: 'ala', action: 'getCharJson') + '?drid=' + druid;
    }

    /**
     * Convert CSV format to charJSON
     *
     * CVS format example:
     *
     * "scientificName","seed mass","Inflorescence colour","plant height","Phyllode length median","Inflorescence arrangement","Stipule length median","Inflorescence shape"," leaflet pairs 2nd leaf","range size","genome size","Phyllode arrangement","Pulvinus length median","first leaf pinnae pairs","section taxonomy"
     * "Acacia diphylla","5.4","unknown","","","unknown","","unknown","1.9","","","unknown","","2.0","unknown"
     * "Acacia courtii","","white to cream||pale yellow","20","115","simple","0.25","cylindrical","","2","","scattered","","","juliflorae"
     *te
     * Note, character values get converted to a JSON array and can thus have multiple values. This is achieved by using
     * an internal separator string (arg 2) with default value of "||".
     *
     * Can be tested with CharServiceTests.groovy. Output was checked to be valid JSON via http://jsonlint.com/
     *
     * @param charCsv
     * @param internalSeparator
     * @return
     */
    def convertCharCsvToJson(String charCsv, String internalSeparator) {
        def lineCount = 0;
        def headers = [] // Names of characters
        def charMap = [:] // Map version of JSON format
        internalSeparator = Pattern.quote(internalSeparator?:"||") // separator string needs escaping as per a regex
//        log.debug "input csv = " + charCsv
        charCsv.eachCsvLine { tokens ->
//            log.debug "tokens = " + tokens
            if (lineCount == 0) {
                // assume first line is header with character names
                headers = tokens[1..<tokens.size()] // ignore first field as it is taxon name
            } else {
                // data lines
                def thisChars = [:]
                def name = tokens[0] // taxon name
                def charValues = tokens[1..<tokens.size()] // characters

                charValues.eachWithIndex() { obj, i ->
                    def rawValue = obj?:null // force empty string to be null
                    def values = rawValue?.split(internalSeparator);
                    def typedValues = []
                    values.each() {
                        // coerce values into their proper types (Float, Boolean or String)
                        if (it.isFloat()) {
                            typedValues.add(it.toFloat())
                        } else if (it ==~ /(?i)^(true|false)$/) {
                            typedValues.add(it.toBoolean())
                        } else {
                            typedValues.add(it)
                        }
                    }
                    thisChars.put(headers[i], typedValues)
                }

                charMap.put(name, thisChars)
            }
//            log.debug(lineCount + ". " + charMap);

            lineCount++;
        }

        //def jsonOutput = new groovy.json.JsonBuilder( charMap ).toString()
        //log.debug("JSON output: " + jsonOutput);

        return charMap
    }

    /**
     *
     * @param drid -
     */
    def getKeys( drid ){
        def url = grailsApplication.config.listKeys.replace('DRID', drid)
        def result = [];
        try{
            result = webService.get( url );
            if( result instanceof String){
                result = JSON.parse(result);
            }
        } catch (Exception e ){
            result = [
                    error: 'An error occurred',
                    message: e.message
            ]
        }
        return  result;
    }

    /**
     * upload data set to list tool
     */
    def upload(){
        def result = [:], id;
        def file = isMultipartRequest() ? request.getFile('file') : null;
        def reader, colIndex, colName, title;
        String cookie = request.getHeader('Cookie');
        log.debug('cooike: '+cookie);
        log.debug(isMultipartRequest());
        log.debug('cookies:'+request.getCookies());
        JSONObject formParams = JSON.parse(request.getParameter("formParms"));
        title = formParams['title']
        colIndex = formParams['column']['id']
//        colIndex = Integer.parseInt(colIndex);
        colName  = formParams['column']['displayname']
        reader = utilsService.getCSVReaderForCSVFileUpload(file, utilsService.detectSeparator(file) as char)
        result = speciesListService.createList(reader, title, colIndex, cookie);
        if(result?.druid){
            def url = getUrl(result.druid);
            id = result.id
            result = [:]
            result['url'] = url
            result['title'] = title
            result['id'] = id;
        } else {
            result['error'] = 'error executing function';
        }

        if(params.callback){
            render(contentType: 'text/javascript', text: "${params.callback}(${result as JSON})")
        } else {
            render(contentType: 'application/json', text: result as JSON)
        }
    }
}
