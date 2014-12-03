package au.org.ala.phyloviz

import grails.test.mixin.TestFor
import org.apache.commons.logging.LogFactory
import spock.lang.Specification
/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TreeService)
//@Mock([WebService, UtilsService])
class TreeServiceSpec extends Specification {
    private static  final log = LogFactory.getLog( this )
    public  def opentreeControl
    def setup() {
        service.grailsApplication = grailsApplication
        opentreeControl = mockFor(OpentreeService, true)
        opentreeControl.demand.convertNexmlToNexson(3..3){ String tree }
//        service.opentreeService.webService = mockFor(WebService, true).createMock()
    }

    def cleanup() {
    }

    void "test isNewick"() {
        expect:
            service.isNewick('(a,b);') == true
    }
    void "test isNexml"() {
        given:
        def nexml
        when:
        log.debug('just a test')
        nexml = ( new File('artifacts/ot_13.nexml.txt') ).text
        then:
        service.isNexml( nexml ) == true
    }

    void "test isNexus"() {
        given:
        def nexml
        when:
        log.debug('just a test')
        nexml = ( new File('artifacts/Rpl35_1Kite_cdsRC.ph.nex') ).text
        then:
        service.isNexus( nexml ) == true
    }
    void "test guessFormat"(){
        given:
        def tree = this.loadNexml()
        when:
        service.opentreeService = opentreeControl.createMock()
//        service.grailsApplication = grailsApplication
        def format = service.guessFormat( tree.text )
        then:
        assert format == tree['format']
    }
    void "test getTreeMeta"(){
        given:
        log.debug( 'running testMeta')
        def treeMeta = this.loadNexml2();
        when:
        service.opentreeService = opentreeControl.createMock()
        service.opentreeService.webService = mockFor(WebService, true).createMock()
        def meta = service.getTreeMeta( treeMeta.text )
        then:
        assert meta['title'] == treeMeta['title']
//        assert meta['doi'] == treeMeta['doi']
        assert meta['year'] == treeMeta['year']
        assert meta['version'] == treeMeta['version']
        assert meta['citation'] == treeMeta['citation']
        assert meta['format'] == treeMeta['format']
    }

    Map loadNexml(){
        def result = [:], filename = 'ot_13.nexml.txt'
        result['version'] = '1.2.1'
        def file = new File( 'artifacts/' + filename )
        result['text'] = file.text
        result['year'] = '2014'
        result['doi'] = '10.1086/676505'
        result['citation'] = 'Riginos C. 2014. Dispersal capacity predicts both population genetic structure ' +
                'and species richness in reef fishes. The American Naturalist, 184.'
        result['title'] = 'Dispersal capacity predicts both population genetic structure and species richness in reef fishes'
        result['format'] = 'nexml'
        return result
    }
    Map loadNexml2(){
        def result = [:], filename = 'ot_29.nexml.txt'
        result['version'] = '1.2.1'
        def file = new File( 'artifacts/' + filename )
        result['text'] = file.text
        result['year'] = '2011'
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['citation'] = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. ' +
                'Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        result['title'] = 'The evolution and phylogenetic placement of invasive Australian Acacia species'
        result['format'] = 'nexml'
        return result
    }
}
