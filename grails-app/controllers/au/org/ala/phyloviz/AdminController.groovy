package au.org.ala.phyloviz

import au.org.ala.web.AlaSecured

@AlaSecured(value = ["ROLE_ADMIN", "ROLE_PHYLOLINK_ADMIN"], redirectUri = "/403", anyRole = true)
class AdminController {
    TreeService treeService
    NameService nameService

    /**
     * Rematch all tree nodes to Atlas taxonomy. This is helpful when name index changes.
     * This web service does not require any parameters.
     * @return
     */
    def rematchAll(){
        Map result
        try{
            result = treeService.rematchAll()
            flash.message = result.message;
            redirect(controller: 'wizard', action: 'start')
        } catch(Exception e){
            log.error(e.message)
            e.printStackTrace()
            flash.message = e.message
            redirect(controller: 'wizard', action: 'start')
        }
    }
}
