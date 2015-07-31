package au.org.ala.phyloviz

import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(UtilsService)
class UtilsServiceSpec extends Specification {

    UtilsService service = new UtilsService()
    UtilsService service = new UtilsService()

    def "statisticSummary should return a map with sampleSize == 0 if given a null OR empty list"() {
        when:
        Map result = service.statisticSummary(null)

        then:
        result == [sampleSize: 0]

        when:
        result = service.statisticSummary([])

        then:
        result == [sampleSize: 0]
    }

    def "convertJSONtoCSV should return null when given an empty JSON array"() {
        when:
        def result = service.convertJSONtoCSV([] as JSON)

        then:
        result == null
    def "statisticSummary should throw an illegal argument exception if the list is not a list of tuples"() {
        when:
        service.statisticSummary([1,2,3,4])

        then:
        thrown IllegalArgumentException


        when:
        service.statisticSummary([[1,2,3,4], [1,2,3]])

        then:
        thrown IllegalArgumentException

        when:
        service.statisticSummary([[1,2], [1,2,3,4], [1,2,3]])

        then:
        thrown IllegalArgumentException

        when:
        service.statisticSummary([[1,2],[3,4]])

        then:
        notThrown IllegalArgumentException
    }

    def "convertJSONtoCSV should convert a JSON array to a byte array representation of the matching CSV format"() {
        when:
        def result = service.convertJSONtoCSV([[a :'1',b:'2'],[a:'3',b:'4']] as JSON)

        then:
        result == "a,b,\n1,2\n3,4".bytes
    def "statisticSummary should calculate the most and least frequent items as the item occurring most/least often in the list when faceted = false"() {
        when:
        Map result = service.statisticSummary([["a", 1], ["b", 2], ["c", -1], ["a", 55], ["c", 1231], ["a", 4], ["e", 99]])

        then:
        result.mostFrequent == [count: 3, items: ["a"]]
        result.leastFrequent == [count: 1, items: ["b", "e"]]
    }

    def "statisticSummary should calculate the most and least frequent items as the item with the largest total value in the list when faceted = true"() {
        when:
        Map result = service.statisticSummary([["a", 1], ["b", 2], ["c", 1], ["a", 55], ["c", 1231], ["a", 4], ["e", 99]], true)

        then:
        result.mostFrequent == [count: 1232, items: ["c"]]
        result.leastFrequent == [count: 2, items: ["b"]]
    }

    def "statisticSummary should consider faceted data as numeric if the FIRST cell of the tuple is a number"() {
        when:
        Map result = service.statisticSummary([[1, 1]], true)

        then:
        result.numeric

        when:
        result = service.statisticSummary([["aa", 1]], true)

        then:
        !result.numeric
    }

    def "statisticSummary should consider non-faceted data as numeric if the SECOND cell of the tuple is a number"() {
        when:
        Map result = service.statisticSummary([[1, 1]])

        then:
        result.numeric

        when:
        result = service.statisticSummary([[1, "aa"]])

        then:
        !result.numeric
    }

    def "statisticSummary should return the total sample size as the sum of all facet counts when faceted = true"() {
        when:
        Map result = service.statisticSummary([["a", 1], ["b", 2], ["c", 3], ["a", 4], ["c", 5], ["a", 6], ["e", 7]], true)

        then:
        result.sampleSize == 28
    }

    def "statisticSummary should return the total sample size as the count of tuples when faceted = false"() {
        when:
        Map result = service.statisticSummary([["a", 1], ["b", 2], ["c", 3], ["a", 4], ["c", 5], ["a", 6], ["e", 7]])

        then:
        result.sampleSize == 7
    }

    def "statisticSummary should return sampleSize, mostFrequent, leastFrequent when the data is not numeric"() {
        when:
        Map result = service.statisticSummary([["a", 1], ["b", 2], ["c", 3], ["a", 4], ["c", 5], ["a", 6], ["e", 7]], true)

        then:
        result.size() == 4
        result.containsKey("numeric")
        result.containsKey("sampleSize")
        result.containsKey("mostFrequent")
        result.containsKey("leastFrequent")
    }

    def "statisticSummary should return sampleSize, mostFrequent, leastFrequent, min, max, mean, median, standardDeviation when the data is numeric"() {
        when:
        Map result = service.statisticSummary([[1, 1], [2, 2], [3, 3], [4, 4]], true)

        then:
        result.size() == 9
        result.containsKey("numeric")
        result.containsKey("sampleSize")
        result.containsKey("mostFrequent")
        result.containsKey("leastFrequent")
        result.containsKey("min")
        result.containsKey("max")
        result.containsKey("mean")
        result.containsKey("median")
        result.containsKey("standardDeviation")
    }

    def "statisticSummary should expand faceted numeric data sets when calculating the statistic summary"() {
        when:
        Map result = service.statisticSummary([[2, 2], [3, 3], [4, 4]], true)

        then:
        result.mean == 3.222 // 29 / 5, where 29 is the expanded data: 2, 2, 3, 3, 3, 4, 4, 4, 4

        when:
        result = service.statisticSummary([[2, 2], [3, 3], [4, 4]])

        then:
        result.mean == 3 // 9 / 3
    }
}
