package au.org.ala.phylolink

import au.org.ala.web.AuthService

class SecurityFilters {

    static final String ALA_ADMIN_ROLE = "ROLE_ADMIN"
    static final String PHYLOLINK_ADMIN_ROLE = "ROLE_PHYLOLINK_ADMIN"

    AuthService authService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                params.isAdmin = authService.userInRole(ALA_ADMIN_ROLE) || authService.userInRole(PHYLOLINK_ADMIN_ROLE)

                true
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
