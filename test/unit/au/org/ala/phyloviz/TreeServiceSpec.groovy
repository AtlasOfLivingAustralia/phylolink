package au.org.ala.phyloviz

import grails.test.mixin.TestFor
import org.apache.commons.logging.LogFactory
import spock.lang.Specification
/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TreeService)
class TreeServiceSpec extends Specification {
    private static  final log = LogFactory.getLog( this )
    public  def opentreeControl
    def setup() {
        service.grailsApplication = grailsApplication
        opentreeControl = mockFor(OpentreeService, true)
        opentreeControl.demand.convertNexmlToNexson(3..3){ String tree }
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
        nexml = ( new File('web-app/artifacts/ot_13.nexml.txt') ).text
        then:
        service.isNexml( nexml ) == true
    }

    void "test isNexus"() {
        given:
        def nexml
        when:
        log.debug('just a test')
        nexml = ( new File('web-app/artifacts/Rpl35_1Kite_cdsRC.ph.nex') ).text
        then:
        service.isNexus( nexml ) == true
    }
    void "test guessFormat"(){
        given:
        def tree = this.loadNexml()
        when:
//        service.opentreeService = opentreeControl.createMock()
        def format = service.guessFormat( tree.text )
        then:
        assert format == tree['format']
    }

    Map loadNexml(){
        def result = [:], filename = 'ot_13.nexml.txt'
        result['version'] = '0.0.0'
        def file = new File( 'web-app/artifacts/' + filename )
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
        result['version'] = '0.0.0'
        def file = new File( 'web-app/artifacts/' + filename )
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
