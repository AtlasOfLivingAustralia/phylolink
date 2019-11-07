/**
 * Created by Temi Varghese on 28/10/2014.
 */

package au.org.ala.phyloviz

import grails.converters.JSON
import org.apache.commons.logging.LogFactory
import grails.web.context.ServletContextHolder
import org.grails.web.json.JSONElement
import org.grails.web.util.GrailsApplicationAttributes

/**
 * a class to extract meta data from nexson files. like title, study id, doi, treebase id etc
 */
class Nexson {
    private static final log = LogFactory.getLog(this)
    private String text
    private def json
    private def citation
    private String version
    def utilsService

    private static final supportedVersions = ['1.2.1']
    private static final versionProp = '@nexml2json'

    public Nexson(String nexson) {
        text = nexson
        json = JSON.parse(nexson)
        init()
        log.debug('created nexson object using string')
    }

    public Nexson(JSON json) {
        this.json = json
        init()
        log.debug('created nexson object using JSON object')
    }

    public Nexson(JSONElement json) {
        this.json = json
        init()
        log.debug('created nexson object using JSONElement')
    }

    private init() {
        initVersion()
        initLabels()
        loadServices()
        parseCitation()
    }

    def initVersion() {
        if (json == null || json['data']['nexml'][this.versionProp] == null ||
                !(json.data.nexml[this.versionProp] in supportedVersions)) {
            version = null
        } else {
            version = json.data.nexml[this.versionProp]
        }
    }

    def loadServices() {
        def ctx = ServletContextHolder.servletContext?.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        utilsService = ctx?.utilsService
    }

    def initLabels() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def otus
        switch (this.getVersion()) {
            case '1.2.1':
                def otu;
                def otusById;
                otusById = json.data.nexml.otusById
                def count = 0;
                otusById?.each { k, node ->
                    node?.otuById?.each { otuId, meta ->
//                        log.debug( 'otu value' + meta)
                        if(!meta['^ot:originalLabel']  && meta['@label']){
                            meta['^ot:originalLabel'] = meta['@label']
                            meta['^ot:altLabel'] = null;
                        }
                    }
                }
                break;
        }
    }

    /**
     * get the version of nexson
     * @return null or version number
     */
    public getVersion() {
        return version
    }

    public String getDOI() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def doi
        switch (this.getVersion()) {
            case '1.2.1':
                doi = json.data.nexml['^ot:studyPublication']['@href']
                break;
        }
        return doi
    }

    public String getCitation() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def cite
        switch (this.getVersion()) {
            case '1.2.1':
                cite = json.data.nexml['^ot:studyPublicationReference']
                break;
        }
        return cite;
    }

    public getStudyYear() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def year
        switch (this.getVersion()) {
            case '1.2.1':
                year = json.data.nexml['^ot:studyYear']
                break;
        }
        return year;
    }

    public getTitle() {
        if (citation == null) {
            return
        }
        return citation.title
    }

    /**
     * parse citation information
     * @return an object containing title, year, journal etc.
     */
    public parseCitation() {
        def cite = this.getCitation();
        try {
            citation = utilsService.parseCitation(cite)
        } catch (Exception e) {
            log.debug('An exception occurred at creation of nexson object' + e.getMessage())
        }
    }

    public getMeta(meta) {
        meta = meta ?: [:]
        meta['title'] = this.getTitle();
        meta['year'] = this.getStudyYear();
        meta['citation'] = this.getCitation();
        meta['version'] = this.getVersion()
        meta['doi'] = this.getDOI()
        meta['trees'] = this.getTreeList()
        return meta
    }

    /**
     * get all the otus from nexon in an array format
     * @return
     */
    public getOtus() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def otus
        switch (this.getVersion()) {
            case '1.2.1':
                otus = this.getOtus12()
                break;
        }

        return otus;
    }

    private getOtus12() {
        def otus = []
        def otu;
        def otusById;
        otusById = json.data.nexml.otusById
        def count = 0;
        otusById?.each { k, node ->
            node.otuById.each { otuId, meta ->
                count++;
                otu = [:]
                otu['otuId'] = otuId
                otu['^ot:originalLabel'] = meta['^ot:originalLabel']
                otu['^ot:altLabel'] = meta['^ot:altLabel']
                otu['@ala'] = meta['^ot:taxonLink'] ? meta['^ot:taxonLink']['@ala'] : null
                otu['id'] = count
                otus.push(otu)
            }
        }

        return otus
    }

    Map getOtuNameLookupTable(){
        Map lookup = [:]
        List otus = getOtus()
        otus?.each { otu ->
            lookup[otu['^ot:originalLabel']] = otu
        }

        lookup
    }

    def setAlaId(id, lsid) {
        if (json == null) {
            log.debug('nexml is null')
            return
        }

        def otusById = json.data.nexml.otusById, otuById

        switch (this.getVersion()) {
            case '1.2.1':
                otusById.each { k, otus ->
                    otuById = otus.otuById
                    if (otuById) {
                        if (otuById[id]['^ot:taxonLink'] == null) {
                            otuById[id]['^ot:taxonLink'] = JSON.parse('{"@ala": null}')
                        }
                        otuById[id]['^ot:taxonLink']['@ala'] = lsid;
                    }
                }
                break;
        }
    }

    public getAlaId(id) {
        if (json == null) {
            log.debug('nexml is null')
            return
        }

        def otusById = json.data.nexml.otusById, otu, res
        log.debug(this.getVersion())
        switch (this.getVersion()) {
            case '1.2.1':
                otusById.each { k, otuById ->
                    otu = otuById.otuById
                    if (otu[id]['^ot:taxonLink'] != null) {
                        res = otu[id]['^ot:taxonLink']['@ala']
                    }
                }
                break;
        }
        return res
    }

    public setAltLabel(id, label) {
        if (json == null) {
            log.debug('nexml is null')
            return
        }

        def otusById = json.data.nexml.otusById, otu
        switch (this.getVersion()) {
            case '1.2.1':
                otusById.each { k, otuById ->
                    otu = otuById.otuById
                    otu[id]['^ot:altLabel'] = label;
                }
                break;
        }
    }

    public getAltLabel(id) {
        if (json == null) {
            log.debug('nexml is null')
            return
        }

        def otusById = json.data.nexml.otusById, otu, res
        switch (this.getVersion()) {
            case '1.2.1':
                otusById.each { k, otuById ->
                    otu = otuById.otuById
                    res = otu[id]['^ot:altLabel'];
                }
                break;
        }
        return res
    }

    public getTree() {
        return json.toString()
    }

    public String toString(){
        return json.toString()
    }

    public getTreeList() {
        if (json == null) {
            log.debug('nexml is null')
            return
        }
        def trees = []
        def treesById = json.data.nexml.treesById, tree, res
        log.debug(this.getVersion())
        switch (this.getVersion()) {
            case '1.2.1':
                treesById.each { k, treesId ->
                    tree = treesId.treeById
                    tree.each { id, nodes ->
                        trees.push([
                                name: id,
                                id  : id
                        ])
                    }
                }
                break;
        }
        return trees
    }

}