package au.org.ala.phyloviz

import grails.web.JSONBuilder
import groovy.json.JsonBuilder

/**
 * Created by Temi Varghese on 19/06/2014.
 */

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.converters.JSON

@Transactional(readOnly = true)
class PhyloController {
    def webService;
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phylo.list(params), model: [phyloInstanceCount: Phylo.count()]
    }

    def show(Phylo phyloInstance) {
        respond phyloInstance
    }

    def create() {
        respond new Phylo(params)
    }
//    def create( $studyId, $treeId, $index) {
//        respond new Phylo(params)
//    }
    @Transactional
    def save(Phylo phyloInstance) {
        if (phyloInstance == null) {
            notFound()
            return
        }

        if (phyloInstance.hasErrors()) {
            respond phyloInstance.errors, view: 'create'
            return
        }

        phyloInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'phyloInstance.label', default: 'Phylo'), phyloInstance.id])
                redirect phyloInstance
            }
            '*' { respond phyloInstance, [status: CREATED] }
        }
    }

    def edit(Phylo phyloInstance) {
        respond phyloInstance
    }

    @Transactional
    def update(Phylo phyloInstance) {
        if (phyloInstance == null) {
            notFound()
            return
        }

        if (phyloInstance.hasErrors()) {
            respond phyloInstance.errors, view: 'edit'
            return
        }

        phyloInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Phylo.label', default: 'Phylo'), phyloInstance.id])
                redirect phyloInstance
            }
            '*' { respond phyloInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Phylo phyloInstance) {

        if (phyloInstance == null) {
            notFound()
            return
        }

        phyloInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Phylo.label', default: 'Phylo'), phyloInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'phyloInstance.label', default: 'Phylo'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    def getWidgetData(Phylo phyloInstance){
//        def config = JSON.parse( phyloInstance.env.config );
        def layers = ['cl678','cl617','cl966','cl613','cl613']
        def species = JSON.parse( params.speciesList );
        def summary = [:]
//        def layer = phyloInstance.env?.getAt(0).config?:'landuse';
//        def layer = layers[Integer.parseInt(params.wid)];
        println( 'wid' )
        println( params.wid );
        def widget = phyloInstance.widgets?.getAt( Integer.parseInt(params.wid) );
        def layer = widget.config
        def name = widget.displayname
        for( speciesName in species ){
            def occurenceUrl = "http://biocache.ala.org.au/ws/occurrences/search?q=${speciesName.replaceAll(' ', '%20')}&facets=${layer}&pageSize=10";
            println( occurenceUrl );
            def occurencesResult = JSON.parse( webService.get( occurenceUrl ) );
            println("query")
            println( occurencesResult.query )

            println( occurencesResult.dump());
            if( occurencesResult['facetResults'][0]?.fieldResult ){
//                println( occurencesResult['facetResults'][0]?.fieldResult.size() )
                for( def i = 0 ; i < occurencesResult['facetResults'][0]?.fieldResult.size(); i++ ){
                    def v = occurencesResult['facetResults'][0]?.fieldResult[ i ];
                    if ( summary[v.label] ){
//                    println( v.label );
                        summary[v.label] += v.count
                    } else {
                        summary[v.label] = v.count;
                    }

                }
//                println(i);
            }
//            println( occurencesResult.occurrences )
//            for( occurrence in occurencesResult.occurrences){
//                def lat = occurrence.decimalLatitude
//                def lng = occurrence.decimalLongitude
//
//                def urlStr = "http://spatial.ala.org.au/ws/intersect/${layer}/${lat}/${lng}";
//                println( urlStr )
//                def iValue = webService.getJson( urlStr );
//                def value = iValue?.get(0)?.get('value')
//                if ( value ){
//                    if( summary[value] == null ){
//                        summary[value] = 0;
//                    }
//                    summary[value] += 1;
//                }
//            }
        }
//        summary.sort{it.value};
        def result = []
        if( layer.contains('ev') ){
            println( "Matching found" );
            summary = summary.sort{ Float.parseFloat( it.key ) }
        } else {
            summary = summary.sort{ it.key }
        }
        summary.each(){ k,v->
            result.push([ k,v ]);
        }

        if( result.size() != 0 ){
            result.add(0, ['Character','Occurrences'])
        } else {
            result.push( ['Character','Occurrences'] );
            result.push( ['',0] );
        }
//        def data = result;
//
//        def data = summary.keySet().toArray();
//        def data = summary.values().toArray();
//        def data = summary.entrySet().toArray();;
        // get guid
        // get occurrences
        // get env charachters
        render( contentType: 'application/json', text:'{ "data" :'+ new JsonBuilder( result ).toString() +
                ',"options":{' +
                "          \"title\": \"${name}\"," +
                '          "hAxis": {"title": "Type", "titleTextStyle": {"color": "red"}}' +
                '        }' +
                '}');
//        render( contentType: 'application/json', text:'{ "data" :[' +
//                '          ["Year", "Sales", "Expenses"],' +
//                '          ["2004",  1000,      400],' +
//                '          ["2005",  1170,      460],' +
//                '          ["2006",  660,       1120],' +
//                '          ["2007",  1030,      540]' +
//                '        ], '+
//                '"options":{' +
//                '          "title": "Company Performance",' +
//                '          "hAxis": {"title": "Year", "titleTextStyle": {"color": "red"}}' +
//                '        }' +
//                '}');
//        render { contentType:'application/json' text:'{data:[],options:{}}'}
    }
}
