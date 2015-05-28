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
import grails.converters.JSON
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.StringBody
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.InitializingBean

import static groovyx.net.http.Method.POST

class WebService implements InitializingBean {

    public void afterPropertiesSet() {
        JSONObject.NULL.metaClass.asBoolean = { -> false }
    }

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
            log.debug error.error
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

    def doJsonPost(String url, String path, String port, String postBody) {
        log.debug "postBody = " + postBody
        def http = new HTTPBuilder(url)
        http.request(groovyx.net.http.Method.POST, groovyx.net.http.ContentType.JSON) {
            uri.path = path
            if (port) {
                uri.port = port as int
            }
            body = postBody
            requestContentType = ContentType.URLENC

            response.success = { resp, json ->
                log.debug "bulk lookup = " + json
                return json
            }

            response.failure = { resp ->
                def error = [error: "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"]
                log.error "Oops: " + error.error
                return error
            }
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
    
    def postStrData(String url, String data) {
        def uri = new URL(url);
        log.debug(uri.host)
        log.debug(uri.path)
        log.debug(uri.port.toString())
        log.debug(data)
        this.doJsonPost("http://${uri.host}", uri.path, uri.port?.toString(), data)
    }

    /**
     * posts data to url. Data is provided in a Map Object.
     * @param url - address to post to
     * @param data - data to post
     * @return data from post response
     */
    def postData( String url, data, head = [:], enc = ContentType.URLENC ){
        def http = new HTTPBuilder( url )
        def resp = ''
//        def response = http.post( body: data,
//                requestContentType: ContentType.URLENC, headers: header ) { resp, result ->
////            headers['Accept'] = 'application/json'
//            log.debug( "POST Success: ${resp.statusLine} ${result}" )
////            return  result
////            res =  result
////            if( res instanceof InputStreamReader ){
////                BufferedReader rd = new BufferedReader(res);
////                String line;
////                def s = ""
////                while ((line = rd.readLine()) != null) {
////                    s += line
////                }
////                res = s;
////            }
//        }
//        return  response?.text();
        log.debug('cookie:'+head['cookie']);
        http.getHeaders()['Cookie']= head['cookie'];
        def response = http.request( POST){
//            headers['Set-Cookie'] = head['cookie'];
            requestContentType = enc;
            body = data;
            log.debug(head['cookie'])
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
    def postData( String url){
        def http = new HTTPBuilder( url )
        http.post( body:"" ) { resp, result ->
//            headers['Accept'] = 'application/json'
            log.debug( "POST Success: ${resp.statusLine} ${result}" )
            return  result
        }
    }

    def postMultipart(url, multi, String cookie) {
        def http = new HTTPBuilder(url)
        def ch, resp ='';

        def response = http.request(POST) { req ->
            if(cookie){
                headers.put('Cookie', cookie)
            }
            requestContentType = 'multipart/form-data';
            MultipartEntity entity = new MultipartEntity();
            entity.addPart('q',new StringBody(multi['q']));
//            entity.content( new InputStreamReader());
            req.entity = entity;
        }

        if( response instanceof  StringReader){
            while((ch = response.read())!= -1){
                resp += (char)ch;
            }
        } else if(response instanceof String ) {
            resp = response;
        } else {
            resp = response?.text();
        }

        return resp;
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
}