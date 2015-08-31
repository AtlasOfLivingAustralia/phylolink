package au.org.ala.phyloviz

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(AlaService)
@Mock(NameService)
class AlaServiceSpec extends Specification {

    AlaService service = new AlaService()
    NameService mockNameService


    def setup() {
        mockNameService = Mock(NameService)
        service.nameService = mockNameService;
    }

    def "getLsid should return a list with the same order as the input list"() {
        given:
        mockNameService.getLSID("a") >> "3"
        mockNameService.getLSID("b") >> "2"
        mockNameService.getLSID("c") >> "1"

        when:
        List lsids = service.getLsid(["a", "b", "c"])

        then:
        lsids == ["3", "2", "1"]
    }

    def "getLsid should return a list will all nulls when no item in the input list has a matching LSID"() {
        given:
        mockNameService.getLSID(_) >> null

        when:
        List lsids = service.getLsid(["a", "b", "c"])

        then:
        lsids == [null, null, null]
    }

    def "getLsid should return a null item in the list when the corresponding input value has not matching LSID"() {
        given:
        mockNameService.getLSID("a") >> "3"
        mockNameService.getLSID("b") >> null
        mockNameService.getLSID("c") >> "1"

        when:
        List lsids = service.getLsid(["a", "b", "c"])

        then:
        lsids == ["3", null, "1"]
    }

    def "getLsid should return a null item in the list when the corresponding input value results in an exception"() {
        given:
        mockNameService.getLSID("a") >> "3"
        mockNameService.getLSID("b") >> { throw new Exception("test") }
        mockNameService.getLSID("c") >> "1"

        when:
        List lsids = service.getLsid(["a", "b", "c"])

        then:
        lsids == ["3", null, "1"]
    }

}
