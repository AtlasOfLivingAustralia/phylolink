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
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.InitializingBean

class WebService implements InitializingBean {

    public void afterPropertiesSet() {
        JSONObject.NULL.metaClass.asBoolean = { -> false }
    }

    def get(String url) {
        log.debug "GET on " + url
        def conn = new URL(url).openConnection()
        try {
            conn.setConnectTimeout(10000)
            conn.setReadTimeout(50000)
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

    def getJson(String url) {
        log.debug "getJson URL = " + url
        def conn = new URL(url).openConnection()

        try {
            conn.setConnectTimeout(60000)
            conn.setReadTimeout(50000)
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
    
    def postData(String url, String data) {
        def uri = new URL(url);
        log.debug(uri.host)
        log.debug(uri.path)
        log.debug(uri.port.toString())
        log.debug(data)
        this.doJsonPost("http://${uri.host}", uri.path, uri.port.toString(), data)
    }

    /**
     * posts data to url. Data is provided in a Map Object.
     * @param url - address to post to
     * @param data - data to post
     * @return data from post response
     */
    def postData( String url, data ){
        def http = new HTTPBuilder( url )
        http.post( body: data,
                requestContentType: ContentType.URLENC ) { resp, result ->
            log.debug( "POST Success: ${resp.statusLine}" )
            return  result
        }
    }
}