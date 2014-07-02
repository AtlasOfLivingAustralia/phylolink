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

import au.org.ala.cas.util.AuthenticationCookieUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.web.context.request.RequestContextHolder

class AuthService {

    static transactional = false

    def getEmail() {
        def email = RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.email

        if (!email) {
            email  = AuthenticationCookieUtils.getUserName(RequestContextHolder.currentRequestAttributes().getRequest())
        }

        email
    }

    def getUserId() {
        def userId = RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.userid

        if (!userId) {
            userId = AuthenticationCookieUtils.getUserName(RequestContextHolder.currentRequestAttributes().getRequest())
        }

        userId
    }



    def getDisplayName() {
        if(RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.firstname){
          ((RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.firstname) +
                 " " + (RequestContextHolder.currentRequestAttributes()?.getUserPrincipal()?.attributes?.lastname))
        } else {
          null
        }
    }

    protected boolean userInRole(role) {
        log.debug("userInRole: " + RequestContextHolder.currentRequestAttributes()?.isUserInRole(role))
        return ConfigurationHolder.config.security.cas.bypass ||
                RequestContextHolder.currentRequestAttributes()?.isUserInRole(role) // || isAdmin()
    }
}