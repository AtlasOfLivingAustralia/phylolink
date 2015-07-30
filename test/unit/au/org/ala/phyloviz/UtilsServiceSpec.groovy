package au.org.ala.phyloviz

import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(UtilsService)
class UtilsServiceSpec extends Specification {

    UtilsService service = new UtilsService()

    def "convertJSONtoCSV should return null when given an empty JSON array"() {
        when:
        def result = service.convertJSONtoCSV([] as JSON)

        then:
        result == null
    }

    def "convertJSONtoCSV should convert a JSON array to a byte array representation of the matching CSV format"() {
        when:
        def result = service.convertJSONtoCSV([[a :'1',b:'2'],[a:'3',b:'4']] as JSON)

        then:
        result == "a,b,\n1,2\n3,4".bytes
    }
}
