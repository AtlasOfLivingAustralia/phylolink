package au.org.ala.phyloviz
import au.com.bytecode.opencsv.CSVReader
import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.ContentType
import org.codehaus.groovy.grails.web.json.JSONArray

@Transactional
class AlaService {
    def webService
    def grailsApplication
    def userService
    def authService

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
        } else if(!q?.startsWith('q=')){
            p.push('q='+q)
        }

        if(!fq?.endsWith('fq=')){
            p.push(fq);
        }else if(!fq?.startsWith('fq=')){
            p.push('fq='+fq)
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
        if(q?.startsWith('q=')){
            p.push(q)
        }else if(!q?.startsWith('q=')){
            p.push('q='+q)
        }

        if(fq?.startsWith('fq=')){
            p.push(fq);
        }else if(!fq?.startsWith('fq=')){
            p.push('fq='+fq)
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

    /**
     *
     */
    def saveQuery(JSONArray clade){
        def data=[:];
        data.q = filterQuery(clade,null,'species');
        return  getQid(data);
    }

    def getQid(data){
        def url = grailsApplication.config.qidUrl;
        def result = webService.postData(url, data);
        return result;
    }

    /**
     * create a filter query
     */
    def filterQuery(JSONArray list,op,field){
        if(!list){
            return '';
        }
        def fq =[];
        int i;
        op = op?:' OR ';
        log.debug(list.toString());
        for(i = 0; i<list.size(); i++){
            fq.push("${field}:\"${list[i]}\"");
        }

        return '('+fq.join(op)+')';
    }

    /**
     * get a list resource id and return it in charJSON
     * @param drid
     * @return charJSON
     */
    def getListCharJson(drid){
        def url = grailsApplication.config.listUrl;
        def charJson = [:];
        def keyValues;
        url = url.replace('DRID', drid);
        log.debug(url);
        keyValues = webService.get(url);
        keyValues = keyValues?:'[]'
        keyValues = JSON.parse(keyValues);
        keyValues.eachWithIndex{ value, i ->
            log.debug(value);
            if(!charJson[value.name]){
                charJson[value.name] = [:]
            }
            value.kvpValues?.eachWithIndex{ l, j->
                log.debug(l);
                if(l.value?.isInteger()){
                    l.value = Integer.parseInt(l.value);
                } else if(l.value?.isDouble()){
                    l.value = Double.parseDouble(l.value);
                }
                charJson[value.name][l.key]= [l.value]
            }
        }
        return charJson;
    }

    /**
     * save a csv file into list tool
     */
    def createList(CSVReader reader, String name, Integer colIndex, String cookie){
        def data = [:], ch
        def result, next, rcount =0, ccount=0, item, row, items, header
        data['listType'] = 'SPECIES_CHARACTERS'
        data['listName'] = name
        data['listItems'] = []
        data['isPrivate'] = true
        // first line is header
        header = reader.readNext();
//        header.eachWithIndex{it,i->
//            header[i] = it.replace(' ','_')
//        }
        while((next = reader.readNext())!=null){
            ccount = 0;
            row= [:]
            items = []
            next.each{column->
                item = [:]
                if(colIndex!=ccount){
                    item['key'] = header[ccount];
                    item['value'] = next[ccount];
                    items.push(item)
                }
                ccount++
            }
            row['kvpValues'] = items
            row['itemName'] = next[colIndex];
            data['listItems'].push(row);
            rcount ++;
        }
        log.debug(data);
//        return  data
        data = data as JSON
        result = webService.postData('http://lists.ala.org.au/ws/speciesList',data.toString(), ['cookie':cookie], ContentType.JSON);
        if(result.druid){
            ch = addCharacterToDB(name, result.druid)
            result.id = ch.id;
        }
        return result
    }

    def addCharacterToDB(String title, String drid){
        Owner own = Owner.findByUserId( authService.getUserId()?:-1 );
        def charList = [
                'owner': own,
                'title': title,
                'drid': drid
        ]
        def c = new Characters(charList).save(
                flush: true
        )
        return  c;
    }
}
