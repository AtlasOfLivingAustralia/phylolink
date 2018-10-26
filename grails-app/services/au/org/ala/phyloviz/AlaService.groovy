package au.org.ala.phyloviz

import au.org.ala.ws.service.WebService
import grails.converters.JSON
import org.apache.http.NameValuePair
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicNameValuePair
import org.grails.web.json.JSONArray
//@Transactional
class AlaService {
    static transactional = true

    def webServiceService
    WebService webService
    def grailsApplication
    def charactersService
    def sandboxService
    NameService nameService

    def allLayersMaxAge = 0
    def allLayersCached = []
    def i18nProperties

    /**
     * //    def utilsService
     * adding utilsService will cause the program to terminate. I think it is because of cyclical dependencies.
     * alaService needs utilsService and utilsService needs alaService
     */

    /**
     * Retrieves a list of LSIDs for the provided list of names. The resulting list will be of the same length as the
     * provided list of names, and each cell will contain the LSID for the corresponding cell in the names list, or null
     * if there is no matching LSID.
     *
     * @param names List of names to search for
     * @return a list of LSIDs corresponding to the items in the provided names list, or null if there was no match.
     */
    List getLsid(List names) {
        names?.collect {
            try {
                nameService.getLSID(it)
            } catch (Exception e) {
                log.error("Unable to match LSID to name ${it}", e)
            }
        }
    }

    List getRecords(List names){
        names?.collect {
            try {
                nameService.getRecord(it)
            } catch (Exception e) {
                log.error("Unable to match LSID to name ${it}", e)
            }
        }
    }

    /**
     * check if a name can be matched to an lsid and returns a list of all matched lsid and a list of all
     * unmatched names.
     * @param names
     * @return
     */
    Map matchNames(List names, au.org.ala.phyloviz.Nexson nex = null) {
        Map matches = ['matched': [], 'unmatched': []]
        String lsid
        Map lookup = nex?.getOtuNameLookupTable()
        names.each { name ->
            try{
                if(lookup && lookup[name] && lookup[name]['@ala']){
                    lsid = lookup[name]['@ala']
                } else {
                    lsid = nameService.getLSID(name)
                }

                if (lsid) {
                    matches['matched'].push(lsid)
                } else {
                    matches['unmatched'].push(name)
                }
            } catch (Exception e){
                matches['unmatched'].push(name)
            }
        }
        matches
    }

    /**
     * get taxon info from guid
     * @param guid
     * @return
     */
    def getTaxonInfo(String guid) throws Exception {
        def url = grailsApplication.config.bieInfo;
        log.debug(guid)
        log.debug(url)
        url = url.replace('QUERY', guid)
        def result = JSON.parse(webServiceService.get(url))
        return result?.taxonConcept;
    }

    /**
     * creates charjson using records in sandbox
     * @param baseUrl
     * @param drid
     * @param key
     * @param fields
     * @return
     */
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
        def keyValues = webServiceService.get(url + "&facets=${key}");
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
            keyValues = webServiceService.get(url + "&fq=${key}:\"${kname}\"&" + fieldFacet);
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

    def i18n() {
        if (i18nProperties == null || i18nProperties.size() == 0) {
            def p = new Properties()
            Map result = webService.get(grailsApplication.config.biocacheServiceUrl + '/facets/i18n',[:],ContentType.DEFAULT_TEXT)
            p.load(new StringReader(result.resp))
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

        if(baseUrl) {
            def url = "${baseUrl}/search/grouped/facets";
            def facets = webServiceService.get(url);
            facets = JSON.parse(facets);

            //merge with group names
            facets.each { g ->
                g.facets.each { f ->
                    results.add([group: g.title, name: f.field, displayName: i18n().getAt('facet.' + f.field) ?: f.field])
                }
            }
        }

        results
    }

    /**
     * get fields that are not indexed
     * @param baseUrl
     * @param drid
     * @return
     */
    def getDynamicFacets(baseUrl, drid) {

        if(!drid){
            return []
        }

        def url = "${baseUrl}/upload/dynamicFacets?q=${drid}";
        def facets = webServiceService.get(url);
        def result = [];
        JSON.parse(facets).eachWithIndex { value, i ->
            log.debug(value);
            if (!value.name.endsWith('_RNG')) {
                result.push(facets[i]);
            }
        }
        return result;
    }

    /**
     * get indexed fields
     * @return
     */
    def getFields() {
        def url = grailsApplication.config.biocacheServiceUrl + "/index/fields"
        def fields = JSON.parse(webServiceService.get(url));
        fields.eachWithIndex { def field, int i ->
            field['displayName'] = field['description'];
            fields[i] = field;
        }
        return fields;
    }

    /**
     * create a hash with field name as key and display name as value
     * @param fields
     * @return
     */
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

    /**
     * get sandbox points for a query.
     * @param baseUrl
     * @param q
     * @param fq
     * @return
     */
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
        def result = webServiceService.get(url);
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
            def layers = JSON.parse(webServiceService.get(grailsApplication.config.layers))
            def fields = JSON.parse(webServiceService.get(grailsApplication.config.fields))

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
            allLayersMaxAge = System.currentTimeMillis() + 60 * 60 * 1000
        }

        allLayersCached
    }

    /**
     * post query to a biochache service and get a query id. All subsequent request are done using this query id.
     * When a node on tree is clicked on phylojive, this function is called. Since some trees have thousands of
     * taxa and some queries can easily exceed the get request limit.
     */
    def saveQuery(List clade, String dataLocationType, String biocacheServiceUrl, String drid, String matchingCol, String treeId,boolean characterQuery = false) {
        def q, url = grailsApplication.config.qidUrl.replace("BIOCACHE_SERVICE", biocacheServiceUrl),
            fqs = [], fq , matchNamesResult;
        Tree tree
        au.org.ala.phyloviz.Nexson nex

        if(treeId){
            Integer treeIdInt = Integer.parseInt(treeId)
            tree = Tree.findById(treeId)
            if(tree){
                nex = new au.org.ala.phyloviz.Nexson(tree.nexson)
            }
        }

        // trim species selected to maxLimit to prevent solr error.
        clade = trimSpeciesListToMax(clade);
        matchNamesResult = matchNames(clade, nex)
        if (matchNamesResult['matched'].size()) {
            fqs.push(filterQuery(matchNamesResult['matched'], null, 'lsid', true).replace('(','').replace(')',''))
        }

        if (matchNamesResult['unmatched'].size()) {
            fqs.push(filterQuery(matchNamesResult['unmatched'], null, 'raw_name', true).replace('(','').replace(')',''))
        }

        if(fqs){
            if(fqs.size() > 1) {
                fq = '(' + fqs.join(' OR ') + ')'
            } else {
                fq = fqs[0]
            }
        } else {
            fq = ""
        }

        if (drid != null && !drid.isEmpty()) {
            q = "data_resource_uid:${drid}"
        }

        // if q is empty then transfer fq params to q
        if (!q) {
            q = fq;
            fq = null;
        }

        Map result = [:]
        result.qid = getQid(q, url, fq);

        if (characterQuery) {
            result.count = getOccurrenceCount(result.qid, biocacheServiceUrl)
        }

        return result
    }

    /**
     * get total number of occurrences
     * @param qid
     * @param biocacheServiceUrl
     * @return
     */
    int getOccurrenceCount(String qid, String biocacheServiceUrl) {
        String url = grailsApplication.config.occurrencesSearch.replace("BIOCACHE_SERVICE", biocacheServiceUrl)

        webServiceService.getJson("${url}?q=qid:${qid}&pageSize=0&facet=off").totalRecords
    }

    /**
     * trimming the number of scientific names sent from server.
     * @param list
     * @return
     */
    def trimSpeciesListToMax(List list) {
        //truncate q OR terms to avoid SOLR error
        //TODO: rewrite all biocache/ws usage to collate multiple qid queries to avoid loss of OR terms
        if (list.size() >= Integer.parseInt(grailsApplication.config.biocache.maxBooleanClauses + '')) {
            list = list.subList(0, grailsApplication.config.biocache.maxBooleanClauses)
        }

        list as JSONArray;
    }

    /**
     * makes a post request and returns a qid as string.
     * @param data
     * @return
     */
    def getQid(String q, String url, String fq) {
        NameValuePair[] nameValuePairs = new NameValuePair[2];

        // moved code to limiting the number of filter query to saveQuery function.
        // it is better to put it there than do it here.

        nameValuePairs[0] = new BasicNameValuePair("q", q);
        nameValuePairs[1] = new BasicNameValuePair("fq", fq);
        return webServiceService.postNameValue(url, nameValuePairs);
    }

    /**
     * create a filter query
     */
    def filterQuery(list, op, field, Boolean useQuote = true) {
        if (!list) {
            return '';
        }
        def fq = [];
        int i;
        op = op ?: ' OR ';
        log.debug(list.toString());
        for (i = 0; i < list.size(); i++) {
            fq.push("${field}:${useQuote?'\"':''}${list[i]}${useQuote ? '\"' : ''}");
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
        def csv = webServiceService.get(url, cookie);
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
        def csv = webServiceService.get(url, cookie);
        charJson = charactersService.convertCharCsvToJson(csv, '||');
        return charJson;
    }

    /**
     * function that calls respective function to upload data
     */
    def uploadData(String type, String title, String scName, File file, String phyloId) {
        def result;

        switch (type) {
            case 'character':
                if (file == null || !file.exists()) {
                    return ['error': 'File not found.', 'message': 'Did you click a file to upload?']
                }

                if (title == null || title.isEmpty()) {
                    return ['error': 'No title provided', 'message': 'Please give a title to upload']
                }

                if (scName == null || scName.isEmpty()) {
                    return ['error': 'No species name or otu number provided', 'message': 'Please select from the list provided.']
                }
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
    def getRecordsList(userId, Integer phyloId) {
        def result = [grailsApplication.config.alaDataresourceInfo];
        if (userId != null) {
            result.addAll(sandboxService.getAllDataresourceInfo(userId));
        } else {
            //add phyloId instance uploads when no user is logged in
            result.addAll(sandboxService.getAllDataresourceInfoByPhyloId(phyloId.toString()))
        }
        // check the owner of visualisation. load all owners occurrence data since anyone could have been selected for visualisation.
        Owner own = Phylo.findById(phyloId).owner
        result.addAll(sandboxService.getAllDataresourceInfoByOwner(own));
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
    List getLegends(String source, String q, String fq, String type, String cm, String biocacheHubUrl) {
        List result = [];
        String url;
        List params = [];
        type = type ?: 'application/json';
        url = grailsApplication.config.legendAla.replace('BIOCACHE_HUB', biocacheHubUrl);

        cm != null ? params.push("cm=${cm}") : null;
        type != null ? params.push("type=${type}") : null;
        q != null ? params.push("q=${q}") : null;
        fq != null ? params.push("fq=${fq}") : null;
        url = "${url}?${params.join('&')}";
        result = webServiceService.getJson(url) as List;
    }
}
