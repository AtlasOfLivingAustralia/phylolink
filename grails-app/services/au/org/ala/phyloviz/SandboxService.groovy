package au.org.ala.phyloviz
import grails.converters.JSON
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.http.entity.mime.content.FileBody

class SandboxService {
    def webService;
    def authService;
    def grailsApplication;

    /**
     * upload file to sandbox. it is a two part process. uploading file and uploading to sandbox for indexing.
     *
     * @param file
     * @param title
     * @param scName
     * @return
     */
    def upload(File file, String title, String scName){
        //upload and get drid
        def result;
        def headers = "scientific name,lineage ID,Location,Latitude,Longitude,phenotype"
        def preview, dr;
        def serverInstance = grailsApplication.config.sandboxUrl;
        def alaId = authService.getUserId();
        def owner = Owner.findByUserId( alaId );
        if(owner== null){
            return ['error':'Unrecognised user', message: 'User is not registered on Phylolink.'];
        }

        // read header
        BufferedReader bf = new BufferedReader(new FileReader(file));
        headers = bf.readLine();
        headers = headers.replaceFirst(scName, 'scientific name');


        FileBody fbody = new FileBody(file);
        String uUrl = "${serverInstance}/datacheck/upload/uploadFile";
        result = webService.postMultipart(uUrl, ['myFile':fbody], null);
        preview = getFileId( result.location );
        if(preview.fileId == null){
            return ['error':'Failed to upload file.',
                    'message':'Failed uploading file to sandbox. Contact administrator.']
        }

        result =  uploadToSandbox( preview.fileId, title, headers, "false" );
        //update database
        if(!result.error){
            dr = new Sandbox([
                'title': title,
                'scientificName': scName,
                'drid': result.uid,
                'serverInstance': serverInstance,
                'owner': owner
            ]);

            dr.save(flush: true);
            if(dr.hasErrors()){
                return ['error':"Failed to create an entry into database for dataresource ${result.uid}." + dr.errors]
            } else {
                result.message = 'Saved data into sandbox and database';
            }
        }

        return result
    }

    /**
     * get file id from request
     * @param location url eg: 'Location: http://sandbox1.ala.org.au/datacheck/upload/preview/1433916778370?fn=Hbinoei.csv'
     * @return ['fileId':'123']
     */
    def getFileId(url){
        if(url){
            def id = (url =~ /\/(\d+)\?/)
            if( id.hasGroup()){
                return ['fileId':id[0][1]];
            }
        }
    }

    /**
     * upload file to sandbox
     */
    def uploadToSandbox(String id, String title, String headers, String firstLineIsData){
        def sandboxUrl = grailsApplication.config.sandboxUrl
        String url = "${sandboxUrl}/ws/upload/post";

        def result;
        result = uploadFile( url, csvFileUrl(id), id, headers, title, 'COMMA', firstLineIsData, '');
        JSON.parse(result);
    }

    /**
     * create url to download the file
     * @param fileId
     * @return String
     */
    def csvFileUrl(String fileId){
        def biocache = grailsApplication.config.sandboxUrl;
        return "${biocache}/datacheck/upload/serveFile?fileId=${fileId}";
    }

    /**
     * Upload the data to the biocache, passing back the response
     * @param csvData
     * @param headers
     * @param datasetName
     * @param separator
     * @param firstLineIsData
     * @return response as string
     */
    def uploadFile(String url, String csvUrl, String fileId, String headers, String datasetName, String separator,
                   String firstLineIsData, String customIndexedFields){

        NameValuePair[] nameValuePairs = new NameValuePair[7]
        nameValuePairs[0] = new NameValuePair("csvZippedUrl", csvUrl)
        nameValuePairs[1] = new NameValuePair("headers", headers)
        nameValuePairs[2] = new NameValuePair("datasetName", datasetName)
        nameValuePairs[3] = new NameValuePair("separator", separator)
        nameValuePairs[4] = new NameValuePair("firstLineIsData", firstLineIsData)
        nameValuePairs[5] = new NameValuePair("customIndexedFields", customIndexedFields)
        nameValuePairs[6] = new NameValuePair("alaId", authService.getUserId())

        def post = new PostMethod(url)
        post.setRequestBody(nameValuePairs)

        def http = new HttpClient()
        http.executeMethod(post)

        //TODO check the response
        log.debug(post.getResponseBodyAsString())

        //reference the UID caches
        def get = new GetMethod(grailsApplication.config.sandboxUrl + "/ws/cache/refresh")
        http.executeMethod(get)

        post.getResponseBodyAsString()
    }
}
