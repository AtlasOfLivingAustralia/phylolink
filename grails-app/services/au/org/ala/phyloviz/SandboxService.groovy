package au.org.ala.phyloviz

import grails.converters.JSON
import org.apache.http.HttpHost
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.entity.mime.content.FileBody

class SandboxService {

    def webServiceService
    def authService
    def grailsApplication

    /**
     * Upload file to sandbox. it is a two part process. uploading file and uploading to sandbox for indexing.
     *
     * @param file
     * @param title
     * @param scName
     * @return
     */
    def upload(File file, String title, String scName, String phyloId) {

        //upload and get drid
        def serverInstance = grailsApplication.config.sandboxUrl;
        def alaId = authService.getUserId()
        def owner = alaId != null ? Owner.findByUserId(alaId) : Owner.findByDisplayName('Guest')
        if (owner == null) {
            return ['error': 'Unrecognised user', message: 'User is not registered on Phylolink.'];
        }

        // read header
        BufferedReader bf = new BufferedReader(new FileReader(file));
        def headers = bf.readLine();
        headers = headers.replaceFirst(scName, 'scientific name');

        FileBody fbody = new FileBody(file);
        String uUrl = "${serverInstance}/dataCheck/uploadFile";
        def result = webServiceService.postMultipart(uUrl, ['myFile': fbody], null)

        if (result.fileId == null) {
            return ['error'  : 'Failed to upload file.',
                    'message': 'Failed uploading file to sandbox. Contact administrator.']
        }

        def sandboxUploadResult = uploadToSandbox(result.fileId, title, headers, "false");

        //update database
        if (!result.error) {
            result.uid = sandboxUploadResult.uid

            def props = [
                    'title'         : title,
                    'scientificName': scName,
                    'drid'          : sandboxUploadResult.uid,
                    'serverInstance': grailsApplication.config.sandboxBiocacheServiceUrl,
                    'biocacheServiceUrl' : grailsApplication.config.sandboxBiocacheServiceUrl,
                    'biocacheHubUrl' : grailsApplication.config.sandboxHubUrl,
                    'owner'         : owner,
                    'status'        : true,
                    'phyloId'       : phyloId
            ]

            result = result + props
            def dr = new Sandbox(props)
            dr.save(flush: true)
            if (dr.hasErrors()) {
                return ['error': "Failed to create an entry into database for dataresource ${result.uid}." + dr.errors]
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
    def getFileId(url) {
        if (url) {
            def id = (url =~ /\/(\d+)\?/)
            if (id.hasGroup()) {
                return ['fileId': id[0][1]];
            }
        }
    }

    /**
     * upload file to sandbox
     */
    def uploadToSandbox(String id, String title, String headers, String firstLineIsData) {
        def sandboxBiocacheServiceUrl = grailsApplication.config.sandboxBiocacheServiceUrl
        def url = "${sandboxBiocacheServiceUrl}/upload/post";
        def result = uploadFile(url, csvFileUrl(id), headers, title, 'COMMA', firstLineIsData, '');
        JSON.parse(result);
    }

    /**
     * create url to download the file
     * @param fileId
     * @return String
     */
    def csvFileUrl(String fileId) {
        return "${grailsApplication.config.sandboxUrl}/dataCheck/serveFile?fileId=${fileId}";
    }

    /**
     * Upload the data to the biocache, passing back the response.
     *
     * @param csvData
     * @param headers
     * @param datasetName
     * @param separator
     * @param firstLineIsData
     * @return response as string
     */
    def uploadFile(String url, String csvUrl, String headers, String datasetName, String separator,
                   String firstLineIsData, String customIndexedFields) {

        NameValuePair[] nameValuePairs = new NameValuePair[7]
        nameValuePairs[0] = new BasicNameValuePair("csvZippedUrl", csvUrl)
        nameValuePairs[1] = new BasicNameValuePair("headers", headers)
        nameValuePairs[2] = new BasicNameValuePair("datasetName", datasetName)
        nameValuePairs[3] = new BasicNameValuePair("separator", separator)
        nameValuePairs[4] = new BasicNameValuePair("firstLineIsData", firstLineIsData)
        nameValuePairs[5] = new BasicNameValuePair("customIndexedFields", customIndexedFields)
        nameValuePairs[6] = new BasicNameValuePair("alaId", authService.getUserId())


        CloseableHttpClient client = HttpClients.createDefault()

        def httpPost = new HttpPost(url)
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs as List))
        CloseableHttpResponse response = client.execute(httpPost)

        StringBuffer buffer = new StringBuffer()
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
        String dataLine = null
        while((dataLine = reader.readLine()) != null){
            buffer.append(dataLine)
        }
        String responseMsg = buffer.toString()


        //reference the UID caches
        def sandboxGet = new HttpGet(grailsApplication.config.sandboxBiocacheServiceUrl + "/cache/refresh")
        def sandboxResponse = client.execute(sandboxGet)

        client.close()

        responseMsg
    }

    /**
     * lists
     */
    def findListByAlaId(alaId) {
        def result = [];
        def url = grailsApplication.config.sandboxCollectoryUrl;
        if (alaId) {
            url = url.replace('ALAID', alaId);
            result = webServiceService.getJson(url);
        }
        result;
    }

    def findByDrtId(drtId) {
        def result = [];
        def url = grailsApplication.config.sandboxCollectoryUrl.replace("?alaId=ALAID", '/' + drtId);
        result = webServiceService.getJson(url);
        result;
    }

    /**
     * check status of uploaded file
     */
    def checkStatus(uid) {
        def url = grailsApplication.config.sandboxBiocacheServiceUrl + "/upload/status/${uid}.json";
        webServiceService.get(url);
    }

    def getDataresourceInfo(String druid, String ownerId, String biocacheServiceUrl, String biocacheHubUrl, String phyloId) {
        def owner = ownerId != null ? Owner.findByUserId(ownerId) : Owner.findByDisplayName("Guest")

        if (!owner) {
            return ['error': 'You are not logged in. Please log in.']
        }

        if (!biocacheServiceUrl) {
            biocacheServiceUrl = grailsApplication.config.sandboxBiocacheServiceUrl;
        }

        if (!biocacheHubUrl) {
            biocacheHubUrl = grailsApplication.config.sandboxBiocacheHubUrl;
        }
        
        def s = Sandbox.findAll {
            drid == druid && owner == owner && serverInstance == biocacheServiceUrl
        }
        
        if (s.size() > 0) {
            return new au.org.ala.phyloviz.ConvertSandbox().convert(s.get(0));
        } else {
            //not found in Sandbox, use default sandbox
            def d = findByDrtId(druid)
            def dr = new Sandbox([
                    'title'         : d.name,
                    'scientificName': 'undefined',
                    'drid'          : druid,
                    'serverInstance': grailsApplication.config.sandboxBiocacheServiceUrl,
                    'biocacheServiceUrl': grailsApplication.config.sandboxBiocacheServiceUrl,
                    'biocacheHubUrl': grailsApplication.config.sandboxHubUrl,
                    'owner'         : owner,
                    'status'        : true,
                    'phyloId'       : phyloId
            ]);

            dr.save(flush: true);
            if (dr.hasErrors()) {
                return ['error': "Failed to create an entry into database for dataresource ${druid}." + dr.errors]
            }
            return new au.org.ala.phyloviz.ConvertSandbox().convert(dr);
        }
    }

    def getAllDataresourceInfo(String userId){
        def owner = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')

        def status = true;

        if (!owner) {
            return ['error': 'You are not logged in. Please log in.']
        }

        def s = Sandbox.findAll {
            owner == owner && status == status
        };
        def result = [], cs =  new au.org.ala.phyloviz.ConvertSandbox();
        s.each{ item ->
            def c = cs.convert(item)
            if (!c.containsKey('biocacheServiceUrl') || !c.biocacheServiceUrl) c.put('biocacheServiceUrl', grailsApplication.config.sandboxBiocacheServiceUrl)
            if (!c.containsKey('biocacheHubUrl') || !c.biocacheHubUrl) c.put('biocacheHubUrl', grailsApplication.config.sandboxHubUrl)
            result.push(c)
        }
        return result;
    }

    /**
     * get dataresource by owner. this is used by anonymous user.
     * @param userId
     * @return
     */
    List getAllDataresourceInfoByOwner(Owner owner){

        def status = true

        def s = Sandbox.findAll {
            owner == owner && status == status
        }

        def result = [], cs =  new au.org.ala.phyloviz.ConvertSandbox()
        s.each { item ->
            def c = cs.convert(item)
            if (!c.containsKey('biocacheServiceUrl') || !c.biocacheServiceUrl) c.put('biocacheServiceUrl', grailsApplication.config.sandboxBiocacheServiceUrl)
            if (!c.containsKey('biocacheHubUrl') || !c.biocacheHubUrl) c.put('biocacheHubUrl', grailsApplication.config.sandboxHubUrl)
            result.push(c)
        }
        result
    }

    /**
     * get sandbox uploads for Guest and phyloId
     */
    def getAllDataresourceInfoByPhyloId(String phyloId){
        def status = true;

        def s = Sandbox.findAll {
            owner == Owner.findByDisplayName("Guest") && status == status && phyloId == phyloId
        };
        def result = [], cs =  new au.org.ala.phyloviz.ConvertSandbox()
        s.each{ item ->
            def c = cs.convert(item)
            if (!c.containsKey('biocacheServiceUrl') || !c.biocacheServiceUrl) c.put('biocacheServiceUrl', grailsApplication.config.sandboxBiocacheServiceUrl)
            if (!c.containsKey('biocacheHubUrl') || !c.biocacheHubUrl) c.put('biocacheHubUrl', grailsApplication.config.sandboxHubUrl)
            result.push(c)
        }
        return result;
    }

    /**
     * get qid url for sandbox instance provided
     *
     */
    def getQidUrl(String biocacheServiceUrl){
        "${biocacheServiceUrl}/webportal/params";
    }
}
