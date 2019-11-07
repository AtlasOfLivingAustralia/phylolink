package au.org.ala.phyloviz

import grails.converters.JSON
import groovy.json.JsonSlurper

class ChartController {

    Map graphibleFields = [:]   //biocacheServiceURL -> fields cache

    def index() { }

    def alaService

    /**
     * Move to a service
     * @return
     */
    def graphibleFields(){

        def biocacheServiceUrl = params.biocacheServiceUrl ?: grailsApplication.config.biocacheServiceUrl

        if(!graphibleFields[biocacheServiceUrl]){

            def graphibleFieldsForEndpoint = []

            def indexedFields = "${biocacheServiceUrl}/index/fields"
            def js = new JsonSlurper()
            def json = js.parse(new URL(indexedFields))

            def fields = json.findAll { it.name.startsWith("cl")}.collect { [id:it.name, name:it.description ?: it.name ] }.sort {
                a,b -> a.name <=> b.name
            }

            //get the cardinality of each "cl" fields, and limit
            fields.each { field ->
                def url = "${biocacheServiceUrl}/occurrence/facets?q=*:*&facets=${field.id}&flimit=0"
                def cardJson = js.parse(new URL(url))
                if (cardJson[0].count < 35 && cardJson[0].count > 1){
                    field.cardinality = cardJson[0].count
                    graphibleFieldsForEndpoint << field
                }
            }

            graphibleFields[biocacheServiceUrl] = graphibleFieldsForEndpoint
        }


        def fields = graphibleFields[biocacheServiceUrl].collect()

        if(params.q) {
            //add the dynamic fields....
            def dynamicFields = alaService.getDynamicFacets(biocacheServiceUrl, params.q)
            dynamicFields.each {
                fields.add(0, [id: it.name, name: it.displayName])
            }
        }

        render ( contentType: 'application/json', text: fields as JSON)
    }

    def stackedBar() {

        def biocacheServiceUrl = params.biocacheServiceUrl ?: grailsApplication.config.biocacheServiceUrl
        def query = params.query
        def variable2Name = "ChartName"
        def variable2 = params.variable2
        def variable1 = params.variable1

        def result

        if(params.breakdown == "species") {
            result = getSpeciesCountValues(biocacheServiceUrl, query, variable2Name, variable1, variable2, false)
        } else if(params.breakdown == "speciesGrouped") {
            result = getSpeciesCountValues(biocacheServiceUrl, query, variable2Name, variable1, variable2, true)
        } else {
            result = getDistinctOccurrenceValues(biocacheServiceUrl, query, variable2Name, variable1, variable2)
        }

        render ( contentType: 'application/json', text: result as JSON)
    }

    def getSpeciesCountValues(biocacheServiceUrl, biocacheQuery, facetName, facet1, facet2, grouped){

        //get distinct values for facet
        def facets = getFacetList(biocacheServiceUrl, biocacheQuery, facet1)

        if(facets.size() > 20){
            facets = facets.subList(0, 20)
        }

        def distinctFacet2Labels = [] as Set

        def taxonCounts = [:]

        //for each facet value - get a pivot of taxon_concept_lsid vs facet2
        facets.each { facet1Value ->

            println("${biocacheQuery} - ${facet1} - ${facet1Value} - ${facet2}")

            if (grouped){
                //get a distinct set of values for facet2.....
                def pivotUrl = "${biocacheServiceUrl}/occurrence/pivot?q=${biocacheQuery}&fq=${facet1}:%22${java.net.URLEncoder.encode(facet1Value, "UTF-8")}%22&facets=taxon_concept_lsid,${facet2}&apiKey=11fd6287-938b-48f0-bc67-9177fa0e2040&flimit=-1"
                def js = new JsonSlurper()
                def json = js.parse(new URL(pivotUrl))

                //map of facet2 -> taxonCounts
                def facet2TaxonCounts = [:]

                json.pivotResult[0].each { facet2Result ->
                    if(facet2Result.value) { // taxon ID

                        def variantCount = facet2Result.pivotResult.findAll { it.value }.size()
                        if(variantCount > 1 && variantCount <= 3) {
                            def label = variantCount + " types"
                            facet2TaxonCounts[label] = facet2TaxonCounts[label] ? facet2TaxonCounts[label] + 1 : 1
                            distinctFacet2Labels << label

                        } else if (variantCount >= 4){
                            def label = "4 or more types"
                            facet2TaxonCounts[label] = facet2TaxonCounts[label] ? facet2TaxonCounts[label] + 1 : 1
                            distinctFacet2Labels << label
                        } else {
                            if (facet2Result.pivotResult[0].value){
                                def label = facet2Result.pivotResult[0].value
                                facet2TaxonCounts[label] = facet2TaxonCounts[label] ? facet2TaxonCounts[label] + 1 : 1
                                distinctFacet2Labels << label
                            }
                        }
                    }
                }

                taxonCounts[facet1Value] = facet2TaxonCounts

            } else {
                //get a distinct set of values for facet2.....
                def pivotUrl = "${biocacheServiceUrl}/occurrence/pivot?q=${biocacheQuery}&fq=${facet1}:%22${java.net.URLEncoder.encode(facet1Value, "UTF-8")}%22&facets=${facet2},taxon_concept_lsid&apiKey=11fd6287-938b-48f0-bc67-9177fa0e2040&flimit=-1"
                def js = new JsonSlurper()
                def json = js.parse(new URL(pivotUrl))

                //map of facet2 -> taxonCounts
                def facet2TaxonCounts = [:]

                json.pivotResult[0].each { facet2Result ->
                    if(facet2Result.value) {
                        facet2TaxonCounts[facet2Result.value] = facet2Result.pivotResult.size()
                        distinctFacet2Labels << facet2Result.value
                    }
                }

                taxonCounts[facet1Value] = facet2TaxonCounts
            }
        }

        def distinctFacet2LabelsSorted = distinctFacet2Labels.sort()

        //create data structure
        def fullResult = [ [facetName] + distinctFacet2LabelsSorted ]

        //build up result
        taxonCounts.each { facet1Value, facet2TaxonCounts ->
            def array = [facet1Value]
            distinctFacet2LabelsSorted.each { facet2Label ->
                if(facet2Label) {
                    array << facet2TaxonCounts.getOrDefault(facet2Label, 0)
                }
            }
            fullResult << array
        }

        fullResult
    }

    def getFacetList(biocacheServiceUrl, biocacheQuery, facet){

        def facetUrl = "${biocacheServiceUrl}/occurrence/facets?q=${biocacheQuery}&facets=${facet}&flimit=-1"
        def js = new JsonSlurper()
        def json = js.parse(new URL(facetUrl))
        json[0].fieldResult.collect { it.label }.sort(  )
    }

    def getDistinctOccurrenceValues(biocacheServiceUrl, biocacheQuery, facetName, facet1, facet2){
        def url = "${biocacheServiceUrl}/occurrence/pivot?q=${biocacheQuery}&facets=${facet1},${facet2}&apiKey=11fd6287-938b-48f0-bc67-9177fa0e2040"
        def js = new JsonSlurper()
        def json = js.parse(new URL(url))

        def headers = [""] as Set

        //get distinct set of x values....
        json[0].pivotResult.each { pivotResult1 ->
            pivotResult1.pivotResult.each { pivotResult2 ->
                if(pivotResult2.value) {
                    headers << pivotResult2.value
                }
            }
        }

        headers = headers.sort() as List

        def fullResult = [ headers ]

        //for each header, interrogate the pivot
        json[0].pivotResult.each { pivotResult1 ->

            def valueCount = [:]

            def facet1Value = pivotResult1.value

            //convert to map
            pivotResult1.pivotResult.each { pivotResult2 ->
                valueCount[pivotResult2.value] = pivotResult2.count
            }

            def result = [pivotResult1.value]

            headers.each { header ->
                if(header) {
                    //get a species count
                    result << valueCount.getOrDefault(header, 0)
                }
            }

            fullResult << result
        }
        fullResult
    }

    def getSpeciesCount(biocacheServiceUrl, query, variable1, variable1Value, variable2, variable2Value){

        println("${query}, ${variable1}, ${variable1Value}, ${variable2}, ${variable2Value}")

        def url = "${biocacheServiceUrl}/occurrence/facets?q=${query}%20AND%20${variable1}:%22${java.net.URLEncoder.encode(variable1Value, "UTF-8")}%22%20AND%20${variable2}:%22${java.net.URLEncoder.encode(variable2Value, "UTF-8")}%22&facets=taxon_name&flimit=0"

        def js = new JsonSlurper()
        def json = js.parse(new URL(url))
        if(json)
            json[0].count
        else
            0
    }
}
