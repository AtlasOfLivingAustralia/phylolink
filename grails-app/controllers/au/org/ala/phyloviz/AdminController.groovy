package au.org.ala.phyloviz

import au.org.ala.web.AlaSecured
import grails.converters.JSON
import org.grails.io.support.GrailsResourceUtils

@AlaSecured(value = ["ROLE_ADMIN", "ROLE_PHYLOLINK_ADMIN"], redirectUri = "/403", anyRole = true)
class AdminController {

    def treeService
    def alaService
    def bootstrapService

    def index(){}

    def testSandboxLink(){
        String type = 'occurrence';
        String title = 'Sandbox smoke test';
        String scientificName = 'scientificName';
        String phyloId = params?.phyloId

        def file = GrailsResourceUtils.getFile(new URI( 'file://' + System.getProperty("user.dir") + '/grails-app/assets/testfiles/occurrence_test.csv'))

        def result = alaService.uploadData(type, title, scientificName, file, phyloId);

        if( result.error ){
            render(status: 405, contentType: 'application/json', text: result as JSON);
        } else{
            render(contentType: 'application/json', text: result as JSON);
        }
    }

    def testQidGeneration(){

        String q = "*:*"
//        String fq = "lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:7ea76f62-9ccc-4fac-b8d9-55c8b06c227e\""
        String fq = "(lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:7ea76f62-9ccc-4fac-b8d9-55c8b06c227e\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:c51ff867-bd6e-473a-86ba-2678efdf3d86\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:f61dae45-fef9-4483-86ca-d02615221d27\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:8efd5c21-8e99-4f1e-bf41-6258eb1fe5ea\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:b473632c-c6c6-492c-b666-27d9861974f3\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:8d32b5e5-4398-4859-8499-e53e07035b5c\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:2980cbe5-418f-4f2c-afd2-b1ab07ad0571\" OR lsid:\"urn:lsid:biodiversity.org.au:afd.taxon:1ec9ce12-f25c-4150-a0cb-5f3cbde327ff\" OR raw_name:\"Malurus alboscapulatus\")"

        //def qid = alaService.getQid(q, "http://localhost:8999/biocache-service/webportal/params", fq)
        def qid = alaService.getQid(q, "http://living-atlas.org/biocache-service/webportal/params", fq)
        render(contentType: 'application/json', text: [result:qid] as JSON);
    }

    /**
     * Rematch all tree nodes to Atlas taxonomy. This is helpful when name index changes.
     * This web service does not require any parameters.
     *
     * @return
     */
    def rematchAll(){
        Map result
        try{
            result = treeService.rematchAll()
            flash.message = result.message;
            redirect(controller: 'wizard', action: 'start')
        } catch(Exception e){
            log.error(e.message, e)
            flash.message = e.message
            redirect(controller: 'wizard', action: 'start')
        }
    }


    def bootstrap(){
        try {
            bootstrapService.loadTrees()
            render(contentType: 'application/json', text: [success:true] as JSON);
        } catch (Exception e){
            log.error(e.getMessage(), e)
            render(contentType: 'application/json', text: [success:false] as JSON);
        }
    }
}
