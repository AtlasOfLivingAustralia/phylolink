package au.org.ala.phyloviz

import java.util.Date
import java.util.List
import org.apache.commons.lang3.StringUtils

/**
 * Created by Jasen Schremmer on 25/09/2019.
 */
class WorkflowStatusEntry {
		
    Integer id
	Phylo phylo
    static belongsTo = [Phylo]
	Owner owner
	Date date
	String status
    String comment

	Phylo.WorkflowStatus getWorkflowStatus() { 
		status ? Phylo.WorkflowStatus.forKey(status) : null 
	}
	void setWorkflowStatus(Phylo.WorkflowStatus workflowStatus) { 
		status = workflowStatus.getKey()
	}

	static transients = ['workflowStatus']

    static mapping = {
        comment type: 'text'
    }
    static constraints = {
        phylo(nullable: false)
        owner(nullable: false)
		date(nullable: false)
		status(nullable: false, inList: Phylo.WorkflowStatus.values()*.getKey())
        comment(nullable: true, blank: true, widget: 'textarea')
    }
}