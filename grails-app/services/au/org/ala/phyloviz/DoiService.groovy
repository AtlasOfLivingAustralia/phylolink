package au.org.ala.phyloviz

import grails.converters.JSON
import org.apache.commons.lang3.StringUtils
import org.apache.http.entity.ContentType

class DoiService {

    def webServiceService
    def authService
    def treeService
    def grailsApplication

	static final String DEFAULT_DOI_AUTHOR = "Atlas Of Living Australia"
	static final String DEFAULT_DOI_PROVIDER = "ANDS"
	static final String DEFAULT_DOI_DESCRIPTION = "ALA phylolink visualisation"
	static final String DEFAULT_DOI_CITATIONURL_PREFIX = "https://doi.org/"
	static final String DEFAULT_DOI_DISPLAY_TEMPLATE = "phylolink"
	
    /**
     * Mint / Create a DOI for the visualisation.
     *
     * @param phyloInstance
     * @param visualisationURL the URL to the phylolink visualisation
     * @return
     */
    def mintDoi(Phylo phyloInstance, String visualisationURL) throws Exception {

		String url = "${grailsApplication.config.alaDoiUrl}/api/doi"
		
		String appNameAuthor = grailsApplication.config.app.author?:DEFAULT_DOI_AUTHOR
		String title = phyloInstance.title
		if (grailsApplication.config.doi?.titlePrefix) {
			title = grailsApplication.config.doi?.titlePrefix + " " + title
		}		
		String description = grailsApplication.config.doi?.description?:DEFAULT_DOI_DESCRIPTION;
		String ownerDisplayName = phyloInstance.getOwner().getDisplayName();

		// Provider metadata based on https://documentation.ands.org.au/display/DOC/Minting+DOIs,
		// https://schema.datacite.org/meta/kernel-4.1/doc/DataCite-MetadataKernel_v4.1.pdf and  
		// https://github.com/AtlasOfLivingAustralia/biocache-service/blob/master/src/main/java/au/org/ala/biocache/service/DownloadService.java 

		List<Map<String, Object>> creators = new ArrayList<>();
		Map creator = [ name: ownerDisplayName, type: "Producer" ]
		creators.add(creator);

		for (String author : Phylo.getArrayForNewLineAndSemiColonDelimitedValue(phyloInstance.authors)) {
			author = author.trim();
			if (StringUtils.isNotEmpty(author)) {
				creator = [ name: author, type: "Producer" ]
				creators.add(creator);
			}
		}
		
		// Doi interface defined at https://doi.ala.org.au/webjars/swagger-ui/2.2.8/index.html?url=/api			 
		Map request = [
			authors: appNameAuthor,
			description: description,
			applicationUrl: visualisationURL,
			licence: getLicences(),
			title: title,
			userId: phyloInstance.getOwner().getUserId().toString(),
			active: true,
			provider: grailsApplication.config.doi?.provider?:DEFAULT_DOI_PROVIDER,
			customLandingPageUrl: null,
			displayTemplate: grailsApplication.config.doi?.displayTemplate?:DEFAULT_DOI_DISPLAY_TEMPLATE,
			providerMetadata: [
				title: title,
				authors: [ appNameAuthor ],
				publisher: appNameAuthor,
				creator: creators,
				contributors: [ [ name: ownerDisplayName, type: "Distributor" ] ],
				descriptions: [ [ text: description, type: "Other" ] ],
				resourceText: description,
				resourceType: "Text"
			],
			applicationMetadata: [
				Title: title,
				Authors: appNameAuthor,
				Description: description,
				"Application Url": visualisationURL,				
				Creators: creators,
				Genus: Phylo.getArrayForNewLineAndSemiColonDelimitedValue(phyloInstance.genus),
				Species: Phylo.getArrayForNewLineAndSemiColonDelimitedValue(phyloInstance.species)
			]
		]
		
		def webServiceApiKey = grailsApplication.config.webservice.apiKey
		if (grailsApplication.config.alaDoiUrl_webservice_apiKey) {
			grailsApplication.config.webservice.apiKey = grailsApplication.config.alaDoiUrl_webservice_apiKey
		}

		def result = webServiceService.postData(url, request, ['Accept':'application/json'], ContentType.APPLICATION_JSON, true, false)

		if (grailsApplication.config.alaDoiUrl_webservice_apiKey) {
			grailsApplication.config.webservice.apiKey = webServiceApiKey
		}

        if (result == null || !result['doi']) {
			throw new Exception("Got an empty response from url=" + url + " with request=" + request)
        }
		
		phyloInstance.doi = result['doi']
		phyloInstance.doiURL = result['landingPage']
		phyloInstance.doiUuid = result['uuid']
		phyloInstance.doiCreationDate = new Date()
		
		return phyloInstance
    }

	def getLicences() {
		List<String> licences = new ArrayList<>();		
		while (true) {
			String licence = grailsApplication.config.doi?.licences."${licences.size() + 1}"
			if (licence == null) {
				break;
			}
			licences.add(licence);
		}
		licences
	}
}
