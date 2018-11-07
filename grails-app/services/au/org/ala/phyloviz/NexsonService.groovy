package au.org.ala.phyloviz

import grails.transaction.Transactional

@Transactional
class NexsonService {

    def alaService

    def updateOtus(otus, au.org.ala.phyloviz.Nexson nex ) {
        if (otus){
            otus.each { otu ->
                nex.setAlaId( otu.otuId, otu['@ala'] )
                nex.setAltLabel( otu.otuId, otu['^ot:altLabel'] )
            }
        }
        return nex.getTree()
    }

    def autoSuggest( otus ){
        // create a list of names
        def i, names=[], otu, count = 0, index, mapper = [:];
        for( i = 0; i < otus.size(); i++){
            otu = otus[i]

            if( !otu['@ala'] ){
                names.push( otu['^ot:originalLabel'] )
                mapper[i] = count
                count ++
            }
        }

        // query webservice
        def lsids = alaService.getRecords( names )

        // reconcile
        for( i = 0; i < otus.size(); i++){
            index = mapper[i]
            if( index != null && lsids[ index ] ){
                otus[i]['^ot:altLabel'] = lsids[index]['name']
                otus[i]['@ala'] = lsids[index]['guid']
            }
        }

        return otus;
    }
}
