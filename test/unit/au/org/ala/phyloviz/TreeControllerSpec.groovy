package au.org.ala.phyloviz
import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification
/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TreeController)
class TreeControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "treeInfo content negotiation"() {
//        def controller = new PhylogeneticTreeController()
        controller.params.format = 'json'
        controller.params.tree = this.getNexmlTree()
        def result = controller.treeInfo()
        assert controller.response.text as JSON
        assert controller.response.contentType == 'application/json'
    }
    def getNexmlTree(){
        return new File('artifacts/ot_13.nexml.txt').text
    }

    void "test guess tree format"(){
        given:
        def nexml
        nexml = this.getNexmlTree()
        controller.treeService = mockFor(TreeService).createMock()
        params.tree = nexml
        when:
        request.method = 'POST'
        controller.guessFormat()
        then:
        response.text == 'nexml'
    }
}
