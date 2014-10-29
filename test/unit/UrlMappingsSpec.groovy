//package au.org.ala
//package au.org.ala.phyloviz

import au.org.ala.phyloviz.PhylogeneticTreeController
import au.org.ala.phyloviz.UtilsService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.UrlMappingsUnitTestMixin
import spock.lang.Specification

//import au.org.ala.UrlMappings

/**
 * Created by Temi Varghese on 17/10/2014.
 */
@TestFor(UrlMappings)
@TestMixin(UrlMappingsUnitTestMixin)
@Mock([UtilsService, PhylogeneticTreeController])
class UrlMappingsSpec extends  Specification{
    void "testing url mapping"(){
        expect:
        assertForwardUrlMapping("/ws/searchDoi", controller: 'phylogeneticTree', action: "searchDoi")
        assertForwardUrlMapping("/ws/treeInfo.json", controller: 'phylogeneticTree', action: "searchDoi")
    }
}
