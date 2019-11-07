package au.org.ala.phyloviz

import grails.converters.JSON

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED
import static org.apache.http.HttpStatus.SC_NOT_FOUND

abstract class BaseController {
    public static final String CONTEXT_TYPE_JSON = "application/json"

    def badRequest = {String message = null ->
        sendError(SC_BAD_REQUEST, message ?: "")
    }

    def notAuthorised = {String message = null ->
		sendError(SC_UNAUTHORIZED, message ?: "You do not have permission to perform the requested action.")
    }

    def renderJSONNotAuthorised = {String message = null ->
        renderJSONErrorMsg(SC_UNAUTHORIZED, message ?: "You do not have permission to perform the requested action.")
    }

    protected void notFound(String message = null) {
        sendError(SC_NOT_FOUND, message ?: "No matching record was found.")
    }

	protected void error(String message = null) {
		sendError(SC_INTERNAL_SERVER_ERROR, message ?: "An unexpected error has occurred.")
	}

	protected void renderJSONError(String message = null) {
		renderJSONErrorMsg(SC_INTERNAL_SERVER_ERROR, message ?: "An unexpected error has occurred.")
	}

    def success = { resp ->
        renderJSONResponse(SC_OK, resp)
    }

	protected void renderJSONResponse(int status, Object resp) {
		response.status = status
		response.setContentType(CONTEXT_TYPE_JSON)
		render resp as JSON		
	}
	
	private renderJSONErrorMsg(int status, String msg) {
		Map resp = [
			error: msg
		]
        renderJSONResponse(status, resp)
	}
	
    def saveFailed = {
        sendError(SC_INTERNAL_SERVER_ERROR)
    }

    def sendError = {int status, String msg = null ->
log.error("About to sendError", new RuntimeException("About to sendError"))        
        response.status = status
        response.sendError(status, msg)
    }
}
