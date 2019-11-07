/*
 * Copyright (C) 2012 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.phyloviz

import au.org.ala.ws.service.WebService
import grails.converters.JSON
import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.apache.http.NameValuePair
import org.apache.commons.io.IOUtils
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.HttpClients
import org.grails.web.converters.exceptions.ConverterException
import org.springframework.beans.factory.InitializingBean
import static groovyx.net.http.Method.POST

class WebServiceService implements InitializingBean {

    WebService webService

    void afterPropertiesSet() {}

    def get(String url, String cookie) {
        log.debug "GET on " + url
        def conn = new URL(url).openConnection()
        if(cookie != null){
            conn.setRequestProperty('Cookie',cookie);
        }
        try {
            conn.setConnectTimeout(180000)
            conn.setReadTimeout(180000)
            return conn.content.text
        } catch (SocketTimeoutException e) {
            def error = [error: "Timed out calling web service. URL= \${url}."]
            log.debug error.error
            return error as JSON
        } catch (Exception e) {
            def error = [error: "Failed calling web service. ${e.getClass()} ${e.getMessage()} URL= ${url}."]
            log.error error.error
            return error as JSON
        }
    }

    def get(String url){
        return get(url, null);
    }

    def getXml( url ){
        def weather = new RESTClient( url )
        def resp = weather.get( contentType: groovyx.net.http.ContentType.TEXT,
                headers : [Accept : 'application/xml'] )

        return resp.data.text
    }

    def getJson(String url) {
        log.debug "getJson URL = " + url
        def conn = new URL(url).openConnection()

        try {
            conn.setConnectTimeout(180000)
            conn.setReadTimeout(180000)
            def json = conn.content.text
            return JSON.parse(json)
        } catch (ConverterException e) {
            def error = "{'error': 'Failed to parse json. ${e.getClass()} ${e.getMessage()} URL= ${url}.', 'exception': '${e}' }"
            log.error error
            return JSON.parse(error)
        } catch (SocketTimeoutException e) {
            def error = "{'error': 'Timed out getting json. URL= ${url}.', 'exception': '${e}'}"
            log.error error
            return JSON.parse(error)
        } catch (Exception e) {
            def error = "{'error': 'Failed to get json from web service. ${e.getClass()} ${e.getMessage()} URL= ${url}.', 'exception': '${e}'}"
            log.error error
            return JSON.parse(error)
        }
    }

    def doJsonPost(String url, body, Map query = null, Map headers = null, ContentType requestContentType = ContentType.JSON, ContentType responseContentType = ContentType.JSON) {
        RESTClient client = new RESTClient(url)
        def response = client.post(headers: headers,
                query: query,
                requestContentType: requestContentType,
                contentType: responseContentType,
                body: body)

        if (responseContentType == ContentType.TEXT) {
            IOUtils.toString(response.data)
        } else {
            response
        }
    }

    def doPost(String url, String path, String port, String postBody) {
        def conn = new URL(url).openConnection()
        try {
            conn.setDoOutput(true)
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())
            wr.write(postBody)
            wr.flush()
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            def resp = ""
            while ((line = rd.readLine()) != null) {
                resp += line
            }
            rd.close()
            wr.close()
            return [error: null, resp: JSON.parse(resp)]
        } catch (SocketTimeoutException e) {
            def error = [error: "Timed out calling web service. URL= \${url}."]
            log.debug error.error
            return error as JSON
        } catch (Exception e) {
            def error = [error: "Failed calling web service. ${e.getClass()} ${e.getMessage()} ${e} URL= ${url}."]
            log.debug error.error
            return error as JSON
        }
    }
    
    /**
     * Posts data to url. Data is provided in a Map Object.
     *
     * @param url - address to post to
     * @param data - data to post
     * @return data from post response
     */
    def postData( String url, body, head = [:], enc = org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED,
			boolean includeApiKey = true, boolean includeUser = true){
        def response = webService.post(url, body, [:], enc, includeApiKey, includeUser, head)
        response.resp
    }

    def postMultipart(url, file) {
        def response = webService.postMultipart(url, [:], [:], [file], org.apache.http.entity.ContentType.APPLICATION_JSON, true, true, ['Accept':'application/json'])
        response.resp
    }

    /**
     * posts data to url. Data is provided in a Map Object.
     * @param url - address to post to
     * @param data - data to post
     * @return data from post response
     */
    def postData2( String url, data, head = [:], enc = ContentType.URLENC ){
        def http = new HTTPBuilder( url )
        def resp = ''
        log.debug('cookie:'+head['cookie']);
        http.getHeaders()['Cookie']= head['cookie'];
        def response = http.request( POST){
            requestContentType = enc;
            body = data;
            log.debug(String.valueOf(head['cookie']))
            response.'500' = { HttpResponseDecorator r ->
                log.error("POST to ${url} with data ${data} failed with HTTP 500.");
                return [error:'Internal server problem'];
            }
        }
        def ch;
        if( response instanceof  StringReader){
            while((ch = response.read())!= -1){
                resp += (char)ch;
            }
        } else if(response instanceof String ) {
            resp = response;
        } else if(response instanceof  java.util.HashMap){
            resp = response
        }else {
            resp = response?.text();
        }
        return resp
    }

    /**
     * posts data to url. Data is provided in a Map Object.
     * @param url - address to post to
     * @param data - data to post
     * @return data from post response
     */
    def delete( String url){
        def cl = new RESTClient( url )
        try {
            def resp = cl.delete( path:'phylolink')
            return resp.status == 200
        } catch ( Exception e){
            log.debug( 'cannot delete ' + e.getMessage() )
        }

    }

    /**
     * pass a named value pair and url, to get the post response as string.
     * @param url
     * @param nameValuePairs
     * @return
     */
    def postNameValue(String url, NameValuePair[] nameValuePairs ){

        def client = HttpClients.createDefault()
        def httpPost = new HttpPost(url)

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs as List));

        CloseableHttpResponse response = client.execute(httpPost);

        StringBuffer buffer = new StringBuffer()
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
        String dataLine = null
        while((dataLine = reader.readLine()) != null){
            buffer.append(dataLine)
        }
        String responseMsg = buffer.toString()
        client.close()

        responseMsg
    }
}