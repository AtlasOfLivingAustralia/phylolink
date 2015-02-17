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

    def getSandboxCharJson(drid, key, fields){
        def url  = "http://sandbox.ala.org.au/biocache-service/occurrences/search?q=DRID"
        url = url.replace('DRID',drid);
        def facet =[], keys=[], result=[:], fq=[];
        def fieldFacet = '';

        def dynamicFields = getDynamicFacets(drid);
        def fieldsAll = getFields();
        dynamicFields.addAll(fieldsAll);
        dynamicFields = inverse(dynamicFields)


        fields.eachWithIndex{ value, index ->
            facet.push("facets=${value}");
        }
        fieldFacet = facet.join('&');

        // facet on key
        def keyValues = webService.get(url + "&facets=${key}");
        keyValues = JSON.parse(keyValues);
        keyValues = keyValues.facetResults[0].fieldResult;
        keyValues.eachWithIndex{ value, index ->
            keys.push(value.label);
        }

        // for each key facet on list of fields
        keys.eachWithIndex{ kname, index ->
            if(!result[kname]){
                result[kname]=[:];
                // all characters should be present for a name
                fields.eachWithIndex{ value, id ->
                    result[kname][dynamicFields[value]] = []
                }
            }
            keyValues = webService.get(url + "&fq=${key}:\"${kname}\"&" + fieldFacet);
            keyValues = JSON.parse(keyValues);
            keyValues = keyValues.facetResults;
            keyValues.eachWithIndex{ value, i ->
                // flag for converting string to integer
                def isInterger = false;
                if(value.fieldName.endsWith('_i')){
                    isInterger = true;
                }
                def fieldName = dynamicFields[value.fieldName];
                def f = value.fieldResult;
                f.eachWithIndex{ l, j->
                    if(isInterger){
                        l.label = Integer.parseInt(l.label);
                    }
                    result[kname][fieldName].push(l.label);
                }
            }
        }
        return result;
    }

    def getFacetElements ( keyValues ){
        def results = [[fieldName: '',displayName:'None']], temp;
        keyValues = keyValues?.facetResults;
        keyValues?.eachWithIndex{ value, index ->
            temp = ['name':value.fieldName]
            log.debug( value.fieldName )
            temp.displayName = formatDynamicFacetName( temp );
            results.push( temp );
        }
        return results;
    }

    def getDynamicFacets(drid){
        def url = "http://sandbox.ala.org.au/biocache-service/upload/dynamicFacets?q=DRID";
        url = url.replace('DRID',drid);
        def facets = webService.get(url);
        facets = JSON.parse(facets);
        def result = [];
        facets.eachWithIndex{ value, i->
            log.debug(value);
            if(!value.name.endsWith('_RNG')){
                result.push(facets[i]);
            }
        }
        return result;
    }

    def getFields(){
        def url = "http://biocache.ala.org.au/ws/index/fields"
        def fields = JSON.parse(webService.get(url));
        fields.eachWithIndex { def field, int i ->
            field['displayName'] = field['description'];
            fields[i] = field;
        }
        return fields;
    }

    def inverse(fields){
        def result=[:];
        fields.eachWithIndex{ field , i ->
            result[field.name] = field.displayName;
        }
        return result;
    }


    /**
     * Formats the display of dynamic facet names in Sandbox (facet options popup)
     *
     * @attr fieldName REQUIRED the field name
     */
    def formatDynamicFacetName( attrs ){
        String fieldName = attrs.name
        def output
        if (fieldName.endsWith('_s') || fieldName.endsWith('_i') || fieldName.endsWith('_d')) {
            output = fieldName[0..-2].replaceAll("_", " ")
        } else if (fieldName.endsWith('_RNG')) {
            output = fieldName[0..-4].replaceAll("_", " ") + " (range)"
        } else {
            output = fieldName;
        }

        return output
    }

    def getSandboxPoints(q, fq){
        def url = grailsApplication.config['sandboxData'];
//        url = url.replace('DRID',drid);
        def p = [];
        if(!q?.endsWith('q=')){
            p.push(q)
        }
        if(!fq?.endsWith('fq=')){
            p.push(fq);
        }
        url = "${url}?${p.join('&')}";
        def result = webService.get(url);
        result = JSON.parse(result);
        return result;
    }

    def getSandboxFacets(q , fq){
        def result = getSandboxPoints(q, fq)
        result =  getFacetElements(result);
        def dFacets = getDynamicFacets( q );
        return result.plus(dFacets);
    }

    def getAlaPoints(q, fq){
        def url = grailsApplication.config['occurrencesSearch'];
        def p = [];
        if(!q?.endsWith('q=')){
            p.push(q)
        }
        if(!fq?.endsWith('fq=')){
            p.push(fq);
        }
        url = "${url}?${p.join('&')}".replace(' ','+');
        def result = webService.get(url);
        result = JSON.parse(result);
        return result;
    }

    def getAlaFacets(q, fq){
        def result = getAlaPoints(q, fq);
        return getFacetElements(result);
    }

    /**
     * get all layers in spatial portal
     */
    def getAllLayers(){
        def result =[]
        def url = grailsApplication.config.layers
        def data = JSON.parse( webService.get( url ) )
        def code;
        data.eachWithIndex { def entry, int i ->
            code = '';
            switch ( entry.type ){
                case grailsApplication.config.layersMeta.cl:
                    code = 'cl';
                    break;
                case grailsApplication.config.layersMeta.env:
                    code = 'el';
                    break;
            }
            entry.id = code + entry.id;
            entry.label = entry.displayname;
            result.push( entry )
        }
    }
}
