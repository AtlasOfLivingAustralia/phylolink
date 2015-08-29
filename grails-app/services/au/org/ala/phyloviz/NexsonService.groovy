package au.org.ala.phyloviz

import grails.transaction.Transactional

@Transactional
class NexsonService {
    def alaService

    def updateOtus( otus, Nexson nex ) {
        if( otus.size() > 0 ){
            def i ;
            def otu
            for( i = 0; i < otus.size(); i++ ){
                otu = otus[i]
                log.debug( otu )
                nex.setAlaId( otu.otuId, otu['@ala'] )
                nex.setAltLabel( otu.otuId, otu['^ot:altLabel'] )
            }
        }
        return nex.getTree()
    }

    def autoSuggest( otus ){
        // create a list of names
        def i, names=[], otu, count = 0, index, mapper = [:];
        log.debug( 'otus received' +otus );

        for( i = 0; i < otus.size(); i++){
            otu = otus[i]

            if( !otu['@ala'] ){
//                log.debug(otu['@ala'] )
                names.push( otu['^ot:originalLabel'] )
                mapper[i] = count
                count ++
            }
        }
        log.debug( names )

        // query webservice
        def lsids = alaService.getLsid( names )
        log.debug('lsids found')
        log.debug( lsids )

        // reconcile
        for( i = 0; i < otus.size(); i++){
            otu = otus[i]
            index = mapper[i]
            log.debug( index + ' ' + i)

            if( index != null && lsids[ index ] ){
                log.debug( lsids[ index ] )
                otus[i]['^ot:altLabel'] = lsids[index]['name']
                otus[i]['@ala'] = lsids[index]['guid']
            }
        }

        return otus;
    }
}
