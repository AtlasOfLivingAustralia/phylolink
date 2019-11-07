package au.org.ala.phyloviz

import java.util.Date
import java.util.List
import org.apache.commons.lang3.StringUtils

/**
 * Created by Temi Varghese on 17/06/2014.
 */
class Phylo {
	
	public enum WorkflowStatus{
		Draft( 'Draft' ),
		Requested_Approval( 'Requested Approval' ),
		Approved( 'Published' ),
		Revision_Required( 'Revision Required' ),
		No_Longer_Required( 'No Longer Required' )
		final String value;
		WorkflowStatus(String value){
			this.value = value
		}
		String toString(){
			value;
		}
		String getKey(){
			name()
		}
		public static WorkflowStatus forValue(String value) {
			if (StringUtils.isNotBlank(value)) {
				for (WorkflowStatus workflowStatus : values()) {
					if (workflowStatus.toString().equalsIgnoreCase(value)) {
						return workflowStatus;
					}
				}
			}
			throw new RuntimeException("Invalid WorkflowStatus value=" + value);
		}
		public static WorkflowStatus forKey(String key) {
			if (StringUtils.isNotBlank(key)) {
				for (WorkflowStatus workflowStatus : values()) {
					if (workflowStatus.getKey().equalsIgnoreCase(key)) {
						return workflowStatus;
					}
				}
			}
			throw new RuntimeException("Invalid WorkflowStatus key=" + key);
		}
	}
	
	Owner owner
    static belongsTo = [Owner]
    Integer id
    String title = "unnamed"
    String treeid
    Integer nodeid
    Visualization viz
    Integer index
    String studyid
    String regionType
    String regionName
    String dataResource
    String habitat
    String characters
    String pjSettings
    String source
    Characters characterSource
	String genus
	String species
	String authors
	String publishedPapers
	Boolean unpublishedData
	String doi
	String doiURL
	String doiUuid
	String notes
	String status = WorkflowStatus.Draft.getKey()

	WorkflowStatus getWorkflowStatus() { 
		status ? WorkflowStatus.forKey(status) : null 
	}
	void setWorkflowStatus(WorkflowStatus workflowStatus) { 
		status = workflowStatus.getKey()
	}

	static transients = ['workflowStatus']

	Date doiCreationDate

    List widgets = new ArrayList()
    List workflowStatusEntries = new ArrayList()
    static hasMany = [ 
		widgets: Widget,
		workflowStatusEntries: WorkflowStatusEntry 
	]
    static mapping = {
        widgets cascade:"all-delete-orphan"
        workflowStatusEntries cascade:"all"
        habitat type:'text'
        characters type:'text'
        source type: 'text'
        pjSettings type: 'text'
        title defaultValue:"'Unnamed'"
		genus type: 'text' 
		species type: 'text' 
		authors type: 'text' 
		publishedPapers type: 'text' 
		unpublishedData defaultValue: false
        notes type: 'text'
    }
    static constraints = {
        treeid (nullable: false)
        nodeid (nullable: true)
        viz (nullable: false)
        index (nullable: true)
        studyid ( nullable: true)
        widgets (nullable:true)
        regionType(nullable: true, blank: false)
        regionName(nullable: true, blank: false)
        dataResource(nullable: true, blank: true)
        owner( nullable: true)
        habitat(nullable: true, blank: true)
        pjSettings(nullable: true, blank: true)
        characters(nullable: true, blank: true)
        source(nullable: true, blank: true)
        characterSource(nullable: true, blank: true)
        title(nullable: true)
		genus(nullable: true, widget: 'textarea') // ideally nullable is false, but temporarily set to true to be compatible with existing data
		species(nullable: true, widget: 'textarea') 
        authors(nullable: true, widget: 'textarea') // ideally nullable is false, but temporarily set to true to be compatible with existing data
        publishedPapers(nullable: true, widget: 'textarea') // ideally nullable is false, but temporarily set to true to be compatible with existing data
        unpublishedData(nullable: true) // ideally nullable is false, but temporarily set to true to be compatible with existing data
        doi(nullable: true)
        doiURL(nullable: true)
        doiUuid(nullable: true)
        notes(nullable: true, widget: 'textarea')
        status(nullable: true, inList: WorkflowStatus.values()*.getKey()) // ideally nullable is false, but temporarily set to true to be compatible with existing data
        doiCreationDate(nullable: true) // ideally nullable is false, but temporarily set to true to be compatible with existing data
        workflowStatusEntries (nullable:true)
    }

	static String parseGenusFromSpecies(String species) {
		int pos = getIndexOfOrReturnTheLengthOfTheString(species, " ");
		int underscorePos = getIndexOfOrReturnTheLengthOfTheString(species, "_");
		
		if (underscorePos < pos) {
			pos = underscorePos;
		}
		
		return species.substring(0, pos);
	}
	
	static int getIndexOfOrReturnTheLengthOfTheString(String string, String character) {
		int result = string.indexOf(character);
		if (result < 0) {
			return string.length();
		}
		return result;
	}
	
	static String listToStringWithNewLineDelimiter(List<String> list) {
		String.join("\r\n", list)
	}

	static String[] getArrayForNewLineAndSemiColonDelimitedValue(String value) {
		(value?:"").replaceAll("\\r\\n|\\r|\\n", ";").split(";")
	}
}