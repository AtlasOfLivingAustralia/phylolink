package au.org.ala.phyloviz



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class VisualizationController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Visualization.list(params), model:[visualizationInstanceCount: Visualization.count()]
    }

    def show(Visualization visualizationInstance) {
        respond visualizationInstance
    }

    def create() {
        respond new Visualization(params)
    }

    @Transactional
    def save(Visualization visualizationInstance) {
        if (visualizationInstance == null) {
            notFound()
            return
        }

        if (visualizationInstance.hasErrors()) {
            respond visualizationInstance.errors, view:'create'
            return
        }

        visualizationInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'visualizationInstance.label', default: 'Visualization'), visualizationInstance.id])
                redirect visualizationInstance
            }
            '*' { respond visualizationInstance, [status: CREATED] }
        }
    }

    def edit(Visualization visualizationInstance) {
        respond visualizationInstance
    }

    @Transactional
    def update(Visualization visualizationInstance) {
        if (visualizationInstance == null) {
            notFound()
            return
        }

        if (visualizationInstance.hasErrors()) {
            respond visualizationInstance.errors, view:'edit'
            return
        }

        visualizationInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Visualization.label', default: 'Visualization'), visualizationInstance.id])
                redirect visualizationInstance
            }
            '*'{ respond visualizationInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Visualization visualizationInstance) {

        if (visualizationInstance == null) {
            notFound()
            return
        }

        visualizationInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Visualization.label', default: 'Visualization'), visualizationInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'visualizationInstance.label', default: 'Visualization'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
