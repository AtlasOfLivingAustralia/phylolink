package au.org.ala.phyloviz
import au.com.bytecode.opencsv.CSVReader
import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.ContentType
import org.apache.commons.httpclient.NameValuePair
import org.codehaus.groovy.grails.web.json.JSONArray

@Transactional
class AlaService {
    def webService
    def grailsApplication
    def charactersService
    def sandboxService
    def authService
    
    def allLayersMaxAge = 0
    def allLayersCached = []
    
    /**
     * //    def utilsService
     * adding utilsService will cause the program to termiate. I think it is because of cyclical dependencies.
     * alaService needs utilsService and utilsService needs alaService
     */

    def getLsid(names) {
        def url = "http://bie.ala.org.au/ws/species/lookup/bulk.json"
        def post = '{"names":["Macropus rufus","Macropus greyi"]}'
        names = (['names': names] as JSON).toString(true);
        return webService.doPost(url, '', '', names)
    }

    def getTaxonInfo(guid) {
        def url = grailsApplication.config.bieInfo;
        log.debug(guid)
        log.debug(url)
        url = url.replace('QUERY', guid)
        def result = JSON.parse(webService.get(url))
        return result?.taxonConcept;
    }

    def getSandboxCharJson(baseUrl, drid, key, fields) {
        def url = "${baseUrl}/ws/occurrences/search?q=${drid}"
        def facet = [], keys = [], result = [:], fq = [];
        def fieldFacet = '';

        def dynamicFields = getDynamicFacets(baseUrl, drid);
        def fieldsAll = getFields();
        dynamicFields.addAll(fieldsAll);
        dynamicFields = inverse(dynamicFields)


        fields.eachWithIndex { value, index ->
            facet.push("facets=${value}");
        }
        fieldFacet = facet.join('&');

        // facet on key
        def keyValues = webService.get(url + "&facets=${key}");
        keyValues = JSON.parse(keyValues);
        keyValues = keyValues.facetResults[0].fieldResult;
        keyValues.eachWithIndex { value, index ->
            keys.push(value.label);
        }

        // for each key facet on list of fields
        keys.eachWithIndex { kname, index ->
            if (!result[kname]) {
                result[kname] = [:];
                // all characters should be present for a name
                fields.eachWithIndex { value, id ->
                    result[kname][dynamicFields[value]] = []
                }
            }
            keyValues = webService.get(url + "&fq=${key}:\"${kname}\"&" + fieldFacet);
            keyValues = JSON.parse(keyValues);
            keyValues = keyValues.facetResults;
            keyValues.eachWithIndex { value, i ->
                // flag for converting string to integer
                def isInterger = false;
                if (value.fieldName.endsWith('_i')) {
                    isInterger = true;
                }
                def fieldName = dynamicFields[value.fieldName];
                def f = value.fieldResult;
                f.eachWithIndex { l, j ->
                    if (isInterger) {
                        l.label = Integer.parseInt(l.label);
                    }
                    result[kname][fieldName].push(l.label);
                }
            }
        }
        return result;
    }
    
    def i18nProperties
    def i18n() {
        if (i18nProperties == null || i18nProperties.size() == 0) {
            def p = new Properties()
            p.load(new StringReader(webService.get(grailsApplication.config.biocacheServiceUrl + '/facets/i18n')))
            i18nProperties = p
        }
        i18nProperties
    }

    def getFacetElements(keyValues) {
        def results = [], temp;

        Map fields = getFields().collectEntries { [(it.name): it] }

        keyValues = keyValues?.facetResults;
        keyValues?.eachWithIndex { value, index ->
            temp = ['name': value.fieldName]
            log.debug(value.fieldName)
            temp.displayName = fields[value.fieldName]?.description ?: formatDynamicFacetName(temp);
            results.push(temp);
        }
        return results;
    }
    
    def getFacets(baseUrl) {
        def results = []
        
        def url = "${baseUrl}/search/grouped/facets";
        def facets = webService.get(url);
        facets = JSON.parse(facets);
        
        //merge with group names
        facets.each { g ->
            g.facets.each { f ->
                results.add([group: g.title, name: f.field, displayName: i18n().getAt('facet.' + f.field) ?: f.field])
            }
        }
        
        results
    }

    def getDynamicFacets(baseUrl, drid) {
        def url = "${baseUrl}/upload/dynamicFacets?q=${drid}";
        def facets = webService.get(url);
        facets = JSON.parse(facets);
        def result = [];
        facets.eachWithIndex { value, i ->
            log.debug(value);
            if (!value.name.endsWith('_RNG')) {
                result.push(facets[i]);
            }
        }
        return result;
    }

    def getFields() {
        def url = grailsApplication.config.biocacheServiceUrl + "/index/fields"
        def fields = JSON.parse(webService.get(url));
        fields.eachWithIndex { def field, int i ->
            field['displayName'] = field['description'];
            fields[i] = field;
        }
        return fields;
    }

    def inverse(fields) {
        def result = [:];
        fields.eachWithIndex { field, i ->
            result[field.name] = field.displayName;
        }
        return result;
    }

    /**
     * Formats the display of dynamic facet names in Sandbox (facet options popup)
     *
     * @attr fieldName REQUIRED the field name
     */
    def formatDynamicFacetName(attrs) {
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

    def getSandboxPoints(baseUrl, q, fq) {
        def url = "${baseUrl}/occurrences/search";
        def p = [];
        if (!q?.endsWith('q=')) {
            p.push(q)
        } else if (!q?.startsWith('q=')) {
            p.push('q=' + q)
        }

        if (!fq?.endsWith('fq=')) {
            p.push(fq);
        } else if (!fq?.startsWith('fq=')) {
            p.push('fq=' + fq)
        }

        url = "${url}?${p.join('&')}";
        def result = webService.get(url);
        result = JSON.parse(result);
        return result;
    }

    def getSandboxFacets(baseUrl, q, fq) {
        def result = getFacets(baseUrl);
        def dFacets = getDynamicFacets(baseUrl, q);
        return result.plus(dFacets);
    }

    /**
     * get all layers in spatial portal
     */
    def getAllLayers() {
        if (allLayersCached.size() == 0 || System.currentTimeMillis() > allLayersMaxAge) {
            def result = []
            def layers = JSON.parse(webService.get(grailsApplication.config.layers))
            def fields = JSON.parse(webService.get(grailsApplication.config.fields))

            fields.eachWithIndex { def entry, int i ->
                if (entry.indb && entry.defaultlayer && entry.enabled) {
                    //find layer
                    layers.each {
                        if (entry.spid.equals(String.valueOf(it.id)) && it.enabled) {
                            it.id = entry.id
                            it.label = it.displayname
                            it.url = grailsApplication.config.layerMetadata + it.name
                            result.push(it)
                        }
                    }
                }
            }
            if (result.size() > 0) allLayersCached = result
            
            //refresh in an hour
            allLayersMaxAge = System.currentTimeMillis() + 60*60*1000
        }
        
        allLayersCached
    }

    /**
     *
     */
    def saveQuery(JSONArray clade, String dataLocationType, String biocacheServiceUrl, String drid, String matchingCol) {
        def data, url = grailsApplication.config.qidUrl.replace("BIOCACHE_SERVICE", biocacheServiceUrl),
            fq;
        matchingCol = matchingCol ?: 'taxon_name';
        data = filterQuery(clade, null, matchingCol);
        if (drid != null && !drid.isEmpty()) {
            fq = data;
            data = "data_resource_uid:${drid}"
        }

        return getQid(data, url, fq);
    }

    /**
     * makes a post request and returns a qid as string.
     * @param data
     * @return
     */
    def getQid(String q, String url, String fq) {
        NameValuePair[] nameValuePairs = new NameValuePair[2];
        
        //truncate q OR terms to avoid SOLR error
        //TODO: rewrite all biocache/ws usage to collate multiple qid queries to avoid loss of OR terms
        def qsplit = q.split(' OR ')
        if (qsplit.length >= grailsApplication.config.biocache.maxBooleanClauses) {
            qsplit = (qsplit as List).subList(0, grailsApplication.config.biocache.maxBooleanClauses)
            q = (qsplit as List).join(' OR ')
            if (q.startsWith('(')) q += ')'
        }
        nameValuePairs[0] = new NameValuePair("q", q);
        nameValuePairs[1] = new NameValuePair("fq", fq);
        return webService.postNameValue(url, nameValuePairs);
    }

    /**
     * create a filter query
     */
    def filterQuery(JSONArray list, op, field) {
        if (!list) {
            return '';
        }
        def fq = [];
        int i;
        op = op ?: ' OR ';
        log.debug(list.toString());
        for (i = 0; i < list.size(); i++) {
            fq.push("${field}:\"${list[i]}\"");
        }

        return '(' + fq.join(op) + ')';
    }

    /**
     * get a list resource id and return it in charJSON
     * @param drid
     * @return charJSON
     */
    def getListCharJson(drid, cookie) {
        def url = grailsApplication.config.listCSV;
        def charJson;
        url = url.replace('DRID', drid);
        def csv = webService.get(url, cookie);
        charJson = charactersService.convertCharCsvToJson(csv, '||');
        return charJson;
    }

    /**
     * get a list resource id and return it in charJSON
     * @param drid
     * @return charJSON
     */
    def getCharJsonForKeys(drid, cookie, keys) {
        def url = grailsApplication.config.listCsvForKeys;
        def charJson;
        url = url.replace('DRID', drid).replace('KEYS', keys).replaceAll(' ', '+');
        def csv = webService.get(url, cookie);
        charJson = charactersService.convertCharCsvToJson(csv, '||');
        return charJson;
    }

    /**
     * save a csv file into list tool
     */
    def createList(CSVReader reader, String name, Integer colIndex, String cookie) {
        def data = [:], ch
        def result, next, rcount = 0, ccount = 0, item, row, items, header
        data['listType'] = 'SPECIES_CHARACTERS'
        data['listName'] = name
        data['listItems'] = []
        data['isPrivate'] = true
        // first line is header
        header = reader.readNext();
        while ((next = reader.readNext()) != null) {
            ccount = 0;
            row = [:]
            items = []
            next.each { column ->
                item = [:]
                if (colIndex != ccount) {
                    item['key'] = header[ccount];
                    item['value'] = (next[ccount] ?: 'undefined').toString();
                    items.push(item)
                }
                ccount++
            }
            row['kvpValues'] = items
            row['itemName'] = next[colIndex] ?: '';
            data['listItems'].push(row);
            rcount++;
        }
        log.debug(data);
        data = data as JSON
        result = webService.postData(grailsApplication.config.listPost, data.toString(), ['cookie': cookie], ContentType.JSON);
        if (result.druid) {
            ch = addCharacterToDB(name, result.druid)
            result.id = ch.id;
        }
        return result
    }

    def addCharacterToDB(String title, String drid) {
        Owner own = Owner.findByUserId(authService.getUserId() ?: -1);
        def charList = [
                'owner': own,
                'title': title,
                'drid' : drid
        ]
        def c = new Characters(charList).save(
                flush: true
        )
        return c;
    }

    /**
     * function that calls respective function to upload data
     */
    def uploadData(String type, String title, String scName, File file, String cookie, String phyloId) {
        def result;

        switch (type) {
            case 'character':
                charactersService.upload(title, scName, file);
                break;
            case 'occurrence':
                if (file == null || !file.exists()) {
                    return ['error': 'File not found.', 'message': 'Did you click a file to upload?']
                }

                if (title == null || title.isEmpty()) {
                    return ['error': 'No title provided', 'message': 'Please give a title to upload']
                }

                if (scName == null || scName.isEmpty()) {
                    return ['error': 'No species name or otu number provided', 'message': 'Please select from the list provided.']
                }

                result = sandboxService.upload(file, title, scName, phyloId);
                break;
        }

        return result;
    }

    /**
     * get list of all data resources including atlas data
     * @param userId
     * @return
     */
    def getRecordsList(userId, phyloId){
        def result = [grailsApplication.config.alaDataresourceInfo];
        if (userId != null) {
            result.addAll(sandboxService.getAllDataresourceInfo(userId));
        } else {
            //add phyloId instance uploads when no user is logged in
            result.addAll(sandboxService.getAllDataresourceInfoByPhyloId(phyloId))
        }
        result
    }

    /**
     * two sources to get legends - ala and sandbox. This method queries appropriate sources.
     * @param source
     * @param q
     * @param fq
     * @param type
     * @param cm
     * @return
     */
    List getLegends(String source, String q, String fq, String type, String cm, String biocacheHubUrl){
        List result = [];
        String url;
        List params = [];
        type = type?:'application/json';
        url = grailsApplication.config.legendAla.replace('BIOCACHE_HUB', biocacheHubUrl);

        params.push("cm=${cm}");
        params.push("type=${type}");
        params.push("q=${q}");
        params.push("fq=${fq}");
        url = "${url}?${params.join('&')}";
        result = webService.getJson(url) as List;
    }
}
