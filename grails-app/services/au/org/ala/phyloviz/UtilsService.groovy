package au.org.ala.phyloviz

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import grails.converters.JSON
import net.sf.json.JSONObject
import org.apache.commons.io.FileUtils
import org.apache.commons.logging.LogFactory
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.web.multipart.commons.CommonsMultipartFile

import java.util.regex.Pattern

class UtilsService {
    def grailsApplication
    def opentreeService
    def webService
    def alaService
    def treeService
    def authService

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
    def convertJSONtoCSV(List json) {
        if (json.size() == 0) {
            return
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream()
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(bytes), ',' as char, '"' as char)

        json.each {
            csvWriter.writeNext(it as String[])
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
     * [{*  doi:
     *  title:
     *  reference:
     *  year:
     *}]
     */
    def searchDoi(terms) {
        String url = grailsApplication.config.doiSearchUrl
        log.debug(url)
        url = url.replaceAll('SEARCH', terms);
        log.debug(url)
        def json = webService.getJson(url);
        return json;
    }

    /**
     * get all leaf from a tree or all trees in a study
     */
    def leafNodes(study, tree) {
        def nexson = opentreeService.getNexson(study)
        def leaves = treeService.getLeaves(nexson, tree)
        return leaves
    }

    def lookupLeafName(study, tree) {
        def leaves = this.leafNodes(study, null)

        def names = [];
        for (def i = 0; i < leaves.size(); i++) {
            names.push(leaves[i].name)
        }

        return alaService.getLsid(names);
    }

    /**
     * give a citation and parse it into respective elements
     */
    def parseCitation(String cite) {
        if (cite == null) {
            return
        }
        log.debug(cite)
        def url = grailsApplication.config['citationParser'];
        def data = [
                'citation': cite
        ]
        def xml = webService.postData(url, data, ['Accept': 'application/json'])
        log.debug(xml.toString())
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

    def autocomplete(q) {
        def url = grailsApplication.config.autocompleteUrl
        log.debug(url)
        url = url.replace('QUERY', q)
        def result = webService.get(url)
        result = JSON.parse(result)
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
    def guestAccount() {
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

    /**
     * gets a file class instance for a file sent via post.
     * Params - CommonsMultipartFile - file instance returned by request object.
     */
    def getFileFromCommonsMultipartFile(file) {
        def is = file.getInputStream(), path = null;
        if( is instanceof FileInputStream){
            def tmp = File.createTempFile('upload','tmp')
            FileUtils.copyInputStreamToFile(is, tmp)
            path = tmp.path;
        } else if( is instanceof ByteArrayInputStream){
            path = file.getFileItem()?.tempFile?.path;
        }

        return new File(path);
    }

    /**
     * Takes a list of tuples and produces a statistical summary of the data.
     * <p/>
     * The first cell of the tuple is assumed to be the unit description, and the second the unit value.
     * <p/>
     * If the unit value is numeric, then the summary will include the following:
     * <ol>
     *     <li>sampleSize - the total number of values in the data set
     *     <li>mostFrequent - the most frequent units in the data set
     *     <li>leastFrequent - the least frequent units in the data set
     *     <li>min - the minimum value in the data set
     *     <li>max - the maximum value in the data set
     *     <li>mean - the mean of all values in the data set
     *     <li>median - the median of all values in the data set
     *     <li>standardDeviation - the standard deviation of all values in the data set
     * </ol>
     * All statistic measures are rounded to 3 decimal places
     * <p/>
     * If the unit value is not numeric, then the summary will include the following:
     * <ol>
     *     <li>sampleSize - the total number of values in the data set
     *     <li>mostFrequent - the most frequent units in the data set
     *     <li>leastFrequent - the least frequent units in the data set
     * </ol>
     * <p/>
     * NOTE: The first non-empty unit value will be used to determine whether the data is to be considered numeric or not.
     *
     * @param data List of tuples on which to perform a statistical analysis
     * @param faceted True to indicate that the data has been grouped into facets such that each unit value is the count
     *                of occurrences of the unit - the total sample size will be the sum of all units, not the count.
     *                Defaults to false. This is only applicable to numeric data sets.
     * @firstTupleIsHeading True to indicate if the first tuple in the data set represents column headings. Defaults to true.
     * @return Map of statistical measures as described above
     */
    Map statisticSummary(List data, boolean faceted = false, boolean firstTupleIsHeading = true) {
        Map summary = [:]

        if (data) {
            if (firstTupleIsHeading) {
                data = data.tail()
            }

            // if the data is faceted, then the values are in the first cell and the count is in the second
            // if the data is not faceted, then the categories are in the first cell and the values rae in the second
            int dataIndex = faceted ? 0 : 1

            if (data.find { !(it instanceof List) || it.size() != 2 } != null) {
                throw new IllegalArgumentException("Data must be a list of tuples")
            }
            summary.sampleSize = faceted ? data.sum { it[1] } : data.size()

            List firstNonEmpty = data.find { it[dataIndex] != null }

            summary.numeric = firstNonEmpty[dataIndex] instanceof Number

            if (summary.numeric) {
                DescriptiveStatistics stats = new DescriptiveStatistics()
                data.each { tuple ->
                    // Faceted data groups the data by value, with the first cell being the value and the second the count
                    // To calculate the stats, we need to expand this data, adding a value for each instance in the faceted data
                    (1..(faceted ? tuple[1] : 1)).each {
                        stats.addValue(tuple[dataIndex])
                    }
                }

                summary.min = stats.getMin().round(3)
                summary.max = stats.getMax().round(3)
                summary.mean = stats.getMean().round(3)
                summary.median = stats.getPercentile(50).round(3)
                summary.standardDeviation = stats.getStandardDeviation().round(3)
            }

            Map mappedCounts = [:].withDefault { 0 }
            data.each {
                mappedCounts[it[0]] = mappedCounts[it[0]] + (faceted ? it[1] : 1)
            }

            mappedCounts = mappedCounts.sort { it.value }
            int lowestCount = mappedCounts.values().first() as int
            int highestCount = mappedCounts.values().last() as int
            summary.mostFrequent = [count: highestCount, items: []]
            mappedCounts.reverseEach { k, v ->
                if (v == highestCount) {
                    summary.mostFrequent.items << k
                }
            }
            summary.leastFrequent = [count: lowestCount, items: mappedCounts.takeWhile { it.value == lowestCount }*.key]
        } else {
            summary.sampleSize = 0
        }

        summary
    }

}