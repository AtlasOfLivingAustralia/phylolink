package au.org.ala.phyloviz

import grails.transaction.Transactional

import java.util.regex.Pattern

@Transactional
class CharactersService {
    def alaService
    def g
    def grailsApplication

    def getCharUrl( ArrayList<Characters> lists ) {
        def result=[]
        lists.each{ list->
            result.push([
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

}
