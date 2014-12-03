package au.org.ala.phyloviz

import grails.transaction.Transactional
import grails.converters.JSON

@Transactional
class AlaService {
    def opentreeService
    def utilService
    def webService
    def grailsApplication
    def getLsid( names ) {
        def url = "http://bie.ala.org.au/ws/species/lookup/bulk.json"

        def post = '{"names":["Macropus rufus","Macropus greyi"]}'
        names = ( ['names':names] as JSON ).toString( true );

        return webService.doPost( url , '','', names )
    }

    def getTaxonInfo( guid ){
        def url = grailsApplication.config.bieInfo;
        log.debug( guid )
        log.debug( url )
        url= url.replace( 'QUERY', guid )
        def result = JSON.parse( webService.get( url ) )
        return result?.taxonConcept ;
    }
}
