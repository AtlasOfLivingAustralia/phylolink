/******* Change this stuff for your project *******/
def appName = 'phylolink'
//serverName='http://localhost:8080'
//contextPath='/phylolink'
def ENV_NAME = "${appName.toUpperCase()}_CONFIG"
default_config = "/data/${appName}/config/${appName}-config.properties"
//runWithNoExternalConfig = true

if(!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}

if(System.getenv(ENV_NAME) && new File(System.getenv(ENV_NAME)).exists()) {
    println "[${appName}] Including configuration file specified in environment: " + System.getenv(ENV_NAME);
    grails.config.locations.add "file:" + System.getenv(ENV_NAME)
} else if(System.getProperty(ENV_NAME) && new File(System.getProperty(ENV_NAME)).exists()) {
    println "[${appName}] Including configuration file specified on command line: " + System.getProperty(ENV_NAME);
    grails.config.locations.add "file:" + System.getProperty(ENV_NAME)
} else if(new File(default_config).exists()) {
    println "[${appName}] Including default configuration file: " + default_config;
    grails.config.locations.add "file:" + default_config
} else {
    println "[${appName}] No external configuration file defined."
}

println "[${appName}] (*) grails.config.locations = ${grails.config.locations}"

/******************************************************************************\
 *  RELOADABLE CONFIG
 \******************************************************************************/
reloadable.cfgs = ["file:/data/${appName}/config/${appName}-config.properties"]

/******* End of change this stuff for your project *******/

/*** Phylo Link config *******/
sandboxUrl = "http://sandbox1.ala.org.au"
debug = true
skin.fluidLayout = 1;

//address that resolves doi value
doiAddress = "http://dx.doi.org/"

//variables used for facetting
alaWebServiceMeta = [
        "speciesfacet":'taxon_name'
]

//external webservice
doiSearchUrl = "http://search.crossref.org/dois?q=SEARCH&header=true"
citationParser = "http://freecite.library.brown.edu/citations/create"

//ala webservices
occurrences = "http://biocache.ala.org.au/ws/occurrences/search?q=SEARCH&facets=LAYER&fq=REGION&flimit=1000000"
layers = "http://spatial.ala.org.au/ws/layers"
spatialPortalRoot="http://spatial.ala.org.au"
regionsUrl = [
        "state": "http://regions.ala.org.au/regions/regionList?type=states",
        "ibra": "http://regions.ala.org.au/regions/regionList?type=ibras"
];
speciesListUrl = "http://biocache.ala.org.au/ws/occurrences/facets/download?facets=${alaWebServiceMeta['speciesfacet']}&flimit=1000000&fq=REGION&fq=rank:species"
drUrl = "http://sandbox.ala.org.au/biocache-service/occurrences/search?q=data_resource_uid:DATA_RESOURCE&facets=${alaWebServiceMeta['speciesfacet']}&fq=REGION"
sandboxData = "http://sandbox.ala.org.au/biocache-service/occurrences/search";
occurrencesSearch = "http://biocache.ala.org.au/ws/occurrences/search"
autocompleteUrl = "http://bie.ala.org.au/ws/search.json?q=QUERY&fq=idxtype:TAXON"
bieInfo = 'http://bie.ala.org.au/ws/species/info/QUERY.json'
qidUrl = 'http://biocache.ala.org.au/ws/webportal/params'
//listUrl = "http://lists.ala.org.au/ws/speciesListItems/DRID?includeKVP=true"
listUrl = "http://lists.ala.org.au/ws/speciesListItems/DRID?includeKVP=true"
//listPost = 'http://lists.ala.org.au/ws/speciesList'
listPost = 'http://lists.ala.org.au/ws/speciesList'
listCSV = 'http://lists.ala.org.au/speciesListItem/downloadList/DRID?id=DRID&action=list&controller=speciesListItem&max=10&sort=itemOrder&fetch=%7BkvpValues%3Dselect%7D&file=test'
listCsvForKeys = 'http://lists.ala.org.au/ws/speciesListItems/byKeys?druid=DRID&keys=KEYS&format=csv'
listKeys = 'http://lists.ala.org.au/ws/speciesListItems/keys?druid=DRID'
listsPermUrl = 'http://lists.ala.org.au/speciesListItem/list/DRID'

//opentree configs
find_all_studies= "${oti_address}/db/data/ext/QueryServices/graphdb/findAllStudies"
ot_api = "${ot_address}/api/v1"
tree_api = "${ot_api}/study/STUDYID/tree/TREEID"
newick_tree = "${tree_api}.tre"
studyMeta = "${ot_api}/study/STUDYID.json?output_nexml2json=1.2.1"
studyUrl = "${ot_api}/study/STUDYID.json?output_nexml2json=FORMAT"
treesearch_url = "${oti_address}/db/data/ext/QueryServices/graphdb/singlePropertySearchForTrees"
curator = "${ot_address}/curator"
to_nexson = "${curator}/default/to_nexson"

find_all_studies_postdata = [ "includeTreeMetadata":true,"verbose":true ]
search_postdata = ["property":"ot:originalLabel","value":'',"verbose":true]
//opentree configs end


// ala web service meta
layersMeta=[
        env:"Environmental",
        cl:'Contextual'
]
// ala web service meta end


//variable config

// nexml2json 0.0 is best since other versions are giving errors.
nexml2json = "1.2.1"

// supported tree formats
treeFormats = [ 'nexml', 'nexus', 'newick' ]

jsonkey = [
        stList:"studies"
]

opentree_jsonvars=[
        searchTree :'matched_studies'
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

intersectionMeta =[
        name:'name',
        var:'variable',
        count:'count'
]

widgetMeta =[
        data:'data',
        chartOptions:'options'
]


//variable config end

/**** Phylo Link config end ****/

/**
 * Expert Tree config
 */

expert_trees = [
                [
            "group":"Amphibia",
            "studyId":"423",
            "treeId":"tree2857"
        ],
                [
        "group":"Birds",
        "studyId":"2015",
        "treeId":"tree4152"
        ]
        ,[
           "group":"Acacia",
           "studyId":"ot_29",
          "treeId":"tree5"
]
]

/** Tree config **/

/**
 * elastic search configs
 */
if( !app.elasticsearch.location ){
    app.elasticsearch.location = "/data/phylolink/elasticsearch/"
}
elasticBaseUrl = 'http://localhost:9200'
eIndex = 'phylolink'
eType = 'nexson'
elasticSchema = 'artifacts/schema.json'
facets ='''
        {
    "aggs" : {
        "Publisher" : {
            "terms" : {
                "field" : "^dc:publisher"
            }
        },
        "Expert Trees":{
            "terms":{
                "field" : "expertTree"
            }
        }
    }
}
'''


grails.project.groupId = au.org.ala // change this to alter the default package name and Maven publishing destination

//localAuthService properties
auth.userDetailsUrl='http://auth.ala.org.au/userdetails/userDetails/'
auth.userNamesForIdPath='getUserList'
auth.userNamesForNumericIdPath='getUserListWithIds'

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
    }
    test {
    }
    production {
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    root{
        debug 'stdout'
    }
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n'), threshold: org.apache.log4j.Level.WARN
    }
    debug 'grails.app',
          'au.org.ala.phyloviz.Nexson'
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
