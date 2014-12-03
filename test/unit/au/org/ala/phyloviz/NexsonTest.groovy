package au.org.ala.phyloviz
/**
 * Created by Temi Varghese on 29/10/2014.
 */
class NexsonTest extends GroovyTestCase {
    void testNexson(){
        def nexson = this.loadNexson()
        assert nexson.text != null

        Nexson nex = new Nexson( nexson.text )
        assert nex.getVersion() == nexson.version
        assert nex.getCitation() == nexson.citation
        assert nex.getStudyYear() == nexson.year
        assert nex.getDOI() == nexson.doi
    }
    void testGetters(){
        def nexson = this.loadNexson()
        assert nexson.text != null

        Nexson nex = new Nexson( nexson.text )
        nex.setAlaId('otu6', 'urn:lsid:biodiversity.org.au:apni.taxon:302465')
        println('test:: ' + nex.getAlaId('otu6') )
        assert nex.getAlaId('otu6') == 'urn:lsid:biodiversity.org.au:apni.taxon:302465'

        nex.setAltLabel('otu6', 'Paraserianthes')
        assert nex.getAltLabel('otu6') == 'Paraserianthes'
    }
    Map loadNexson(){
        def result = [:], filename = 'ot_29.json.1.2.1.json'
        result['version'] = '1.2.1'
        def file = new File( 'artifacts/' + filename )
        result['text'] = file.text
        result['year'] = '2011'
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['citation'] = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and Gonzlez-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species.' +
                ' Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        return result
    }

}