package au.org.ala.phyloviz

import grails.transaction.Transactional
import grails.converters.JSON

@Transactional
class AlaService {
    def opentreeService
    def utilService
    def webService
    def getLsid( names ) {
        def url = "http://bie.ala.org.au/ws/species/lookup/bulk.json"

        def post = '{"names":["Macropus rufus","Macropus greyi"]}'
        names = ( ['names':names] as JSON ).toString( true );

        return webService.doPost( url , '','', names )
    }
}
