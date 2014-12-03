package au.org.ala.phyloviz

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import org.apache.commons.logging.LogFactory
/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ElasticService)
@Mock(WebService)
class ElasticServiceSpec extends Specification {
    private static final log = LogFactory.getLog( this )
    def webControl
    def setup() {
//        service.grailsApplication = grailsApplication
        webControl = mockFor(WebService, true)
        webControl.demand.postData(1..1){ String url }
        service.webService = webControl.createMock()
    }

    def cleanup() {

    }

    void "test createIndex"() {
//         assert service.createIndex( null ) != null
        given:
        def nexml
        when:
        nexml = ''
        log.debug('just a test')
        then:
        assert  service.createIndex( 'nexson' ) == null
    }
//    void testCreate(){
//        service.createIndex( null )
//    }
}
