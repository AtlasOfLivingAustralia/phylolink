/******* Change this stuff for your project *******/
appName = 'Phylo Link'
serverName='http://localhost:8080'
contextPath='/PhyloLink'
//security.cas.uriFilterPattern = ''
/******* End of change this stuff for your project *******/

/*** Phylo Link config *******/
debug = true

//opentree configs
treemachine_address = 'http://115.146.93.110:8000'
oti_address = 'http://115.146.93.110:7478'
ot_address = 'http://115.146.93.110:8000'
find_all_studies= "${oti_address}/db/data/ext/QueryServices/graphdb/findAllStudies"
ot_api = "${ot_address}/api/v1"
tree_api = "${ot_api}/study/STUDYID/tree/TREEID"
newick_tree = "${tree_api}.tre"
studyMeta = "${ot_api}/study/STUDYID.json?output_nexml2json=1.2.1"

find_all_studies_postdata = [ "includeTreeMetadata":true,"verbose":true ]
//opentree configs end


//variable config
jsonkey = [
        stList:"studies"
]



treeMeta = [
        numLeaves:'numberOfLeaves',
        numIntNodes:'numberOfInternalNodes',
        hasBL:'hasBranchLength',
        treeText:'tree',
        treeUrl:'treeViewUrl'
]
studyMetaMap = [
        name :'studyName',
        year :'year',
        authors:'authors'
]
expertTreesMeta=[
        et:'expertTrees'
]

nexmlMetaMapping = [
        "${studyMetaMap.name}":'data/nexml/^ot:studyPublicationReference',
        'focalClade': 'data/nexml/^ot:focalCladeOTTTaxonName',
        'doi': 'data/nexml/^ot:studyPublication/@href',
        'year':'data/nexml/^ot:studyYear'
]


studyListMapping=[
        'ot:studyPublicationReference': studyMetaMap.name,
        'is_deprecated': 'deprecated',
        'ot:focalCladeOTTTaxonName': 'focalCladeName',
        'ot:studyYear': studyMetaMap.year,
        'matched_trees':'trees',
        'is_deprecated': 'deprecated',
        'oti_tree_id': "treeId",
        'ot:tag': 'tag',
        'ot:curatorName': 'curator',
        'ot:studyPublication': 'doi',
        'ot:focalClade': 'focalCladeId',
        'ot:studyId': 'studyId',
        'ot:dataDeposit': 'source'
]
//variable config end

/**** Phylo Link config end ****/

/**
 * Expert Tree config
 */

expert_trees = [
//        [
//            "group":"Fungi",
//            "studyId":"439",
//            "treeId":"tree556"
//        ],
//                [
//            "group":"Amphibia",
//            "studyId":"423",
//            "treeId":"tree2857"
//        ],
                [
        "group":"Birds",
        "studyId":"2015",
        "treeId":"tree4152"
        ]
//                ,[
//            "group":"Primates",
//            "studyId":"2816",
//            "treeId":"tree6557"
//        ],[
//            "group":"Dinosaur",
//            "studyId":"2740",
//            "treeId":"tree6336"
//        ]
]

/** Tree config **/

/******* ALA standard config ************/
headerAndFooter.baseURL = "http://www2.ala.org.au/commonui"
security.cas.casServerName = "https://auth.ala.org.au"
security.cas.uriFilterPattern = "/tree/.*,/user/.*,/treeViewer/showPrivate/.*"
security.cas.authenticateOnlyIfLoggedInPattern = "/,/treeViewer/.*"
security.cas.uriExclusionFilterPattern = "/images.*,/css.*,/js.*"
security.cas.loginUrl = "${security.cas.casServerName}/cas/login"
security.cas.logoutUrl = "${security.cas.casServerName}/cas/logout"
security.cas.casServerUrlPrefix = "${security.cas.casServerName}/cas"
security.cas.bypass = false
ala.baseURL = "http://www.ala.org.au"
bie.baseURL = "http://bie.ala.org.au"
bie.searchPath = "/search"
auth.admin_role = "ROLE_ADMIN"
grails.project.groupId = au.org.ala // change this to alter the default package name and Maven publishing destination

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        serverName = 'http://dev.ala.org.au:8080'
        security.cas.appServerName = serverName
        security.cas.contextPath = "/${appName}"
        grails.logging.jul.usebridge = true
        grails.serverURL = "${serverName}/${appName}" //'http://nickdos.ala.org.au:8080/' + appName
    }
    test {
        serverName = 'http://115.146.93.110:8080'
        contextPath = ''
        security.cas.appServerName = serverName
        grails.logging.jul.usebridge = true
        security.cas.contextPath = ""
        grails.serverURL = "${serverName}/${appName}"
//        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
    production {
        serverName = 'http://115.146.93.193:8080'
        contextPath = ''
        security.cas.appServerName = serverName
        grails.logging.jul.usebridge = false
        security.cas.contextPath = ""
        grails.serverURL = "${serverName}/${appName}"
//        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
