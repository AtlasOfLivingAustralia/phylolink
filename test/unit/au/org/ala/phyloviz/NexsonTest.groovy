package au.org.ala.phyloviz

import grails.converters.JSON
/**
 * Created by Temi Varghese on 29/10/2014.
 */
class NexsonTest extends GroovyTestCase {
  void testObjectCreation(){
      // string constructor
      Nexson nex = new Nexson('[1,2,3]')
      assert nex != null

      // json constructor
      def json = [1,2,3] as JSON
      Nexson nex1 = new Nexson(  [1,2,3] as JSON )
      assert nex1 != null

      Nexson nex2 = new Nexson(  JSON.parse( '{"a":"b"}') )
      assert nex2 != null

  }
    void testNexson(){
        def nexson = this.loadNexson()
        assert nexson.text != null

        Nexson nex = new Nexson( nexson.text )
        assert nex.getVersion() == nexson.version
    }
    Map loadNexson(){
        def result = [:], filename = 'ot_29.json.1.2.1.json'
        result['version'] = '1.2.1'
        def file = new File( 'artifacts/' + filename )
        result['text'] = file.text
        return result
    }
}
