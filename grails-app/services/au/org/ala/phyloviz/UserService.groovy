package au.org.ala.phyloviz

import javax.annotation.PostConstruct

class UserService {
    def grailsApplication, authService, webServiceService
    def auditBaseUrl = ""

    @PostConstruct
    private void init() {
//        auditBaseUrl = grailsApplication.config.ecodata.baseUrl + 'audit'
    }

    def getCurrentUserDisplayName() {
        getUser()?.displayName?:"" //?:"mark.woolston@csiro.au"
    }

    def getCurrentUserId() {
        getUser()?.userId?:""
    }

    au.org.ala.phyloviz.UserDetails getUser() {
        def u = authService.userDetails()
        def user

        if (u?.userId) {
            user = new au.org.ala.phyloviz.UserDetails(u.displayName, u.email, u.userId)
        }

        return user
    }

    def userInRole(role) {
        authService.userInRole(role)
    }

    def userIsSiteAdmin() {
        return authService.userInRole(grailsApplication.config.security.cas.officerRole?:'ROLE_OFFICER') ||
            authService.userInRole(grailsApplication.config.security.cas.adminRole?:'ROLE_PHYLOLINK_ADMIN') ||
            authService.userInRole(grailsApplication.config.security.cas.alaAdminRole?:'ROLE_ADMIN')
    }

    def getRecentEditsForUserId(userId) {
        def url = auditBaseUrl + "/getRecentEditsForUserId/${userId}"
        webServiceService.getJson(url)
    }

    def getProjectsForUserId(userId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/getProjectsForUserId/${userId}"
        webServiceService.getJson(url)
    }

    def getStarredProjectsForUserId(userId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/getStarredProjectsForUserId/${userId}"
        webServiceService.getJson(url)
    }

    def isProjectStarredByUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/isProjectStarredByUser?userId=${userId}&projectId=${projectId}"
        webServiceService.getJson(url)
    }

    def addStarProjectForUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/addStarProjectForUser?userId=${userId}&projectId=${projectId}"
        webServiceService.getJson(url)
    }

    def removeStarProjectForUser(String userId, String projectId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/removeStarProjectForUser?userId=${userId}&projectId=${projectId}"
        webServiceService.getJson(url)
    }

    def addUserAsRoleToProject(String userId, String projectId, String role) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/addUserAsRoleToProject?userId=${userId}&projectId=${projectId}&role=${role}"
        webServiceService.getJson(url)
    }

    def removeUserWithRole(projectId, userId, role) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/removeUserWithRoleFromProject?projectId=${projectId}&userId=${userId}&role=${role}"
        webServiceService.getJson(url)
    }

    def isUserAdminForProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/isUserAdminForProject?projectId=${projectId}&userId=${userId}"
        def results = webServiceService.getJson(url)
        return results?.userIsAdmin
    }

    def isUserCaseManagerForProject(userId, projectId) {
        def url = grailsApplication.config.ecodata.baseUrl + "permissions/isUserCaseManagerForProject?projectId=${projectId}&userId=${userId}"
        def results = webServiceService.getJson(url)
        return results?.userIsCaseManager
    }

    def checkEmailExists(String email) {
        def url = "http://auth.ala.org.au/userdetails/userDetails/getUserDetails?userName=${email}"
        def resp = webServiceService.doPost(url.toString(), [:])
        return resp?.resp?.userId?:""
    }


    /**
     * Register the current user in the system.
     */
    def registerCurrentUser( role ) {
        def userId = authService.getUserId()?.toLong()
        def userDetails = authService.userDetails()
        //def email = authService.getEmail()
        //def displayName = authService.getDisplayName()
        role = role?:'user'
        log.info("Checking user is registered: " + userId + " => " + userDetails)

        if (userId) {
            // check if user is in app DB, if not create it on the fly
            def owner = userId != null ? Owner.findByUserId(userId) : Owner.findByDisplayName('Guest')
            if ( owner == null) {
                log.debug('before save')
                Owner user = new Owner()
                user.userId = userId
                user.created = new Date()
                user.email = userDetails?.email
                user.displayName = userDetails?.displayName
                user.role = role
                user.save(flush: true)
                log.debug('after save')
                return user;
            }
            return owner;
        }
    }
}
