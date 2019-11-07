package au.org.ala.phyloviz

import grails.transaction.Transactional
import java.util.Date
import grails.converters.JSON
import grails.gorm.transactions.ReadOnly

import org.apache.commons.lang3.StringUtils

@Transactional
class PhyloService {

    def grailsApplication
    def authService
	def treeService
	def userService
	def doiService
    def emailService
    def utilsService
	
    void deleteVisualisation(Integer id) {
        Phylo.findById(id)?.delete()
    }

    def createVisualization( studyId, treeId, owner ) {
        def viz = new Phylo( [
                studyid: studyId,
                treeid: treeId,
                "viz": ['viz':'PhyloJive'],
                owner: owner
        ])
		
		prepopulateEmptyFields(viz)
		
        viz.save( flush: true )
        if(!viz.hasErrors()){
            viz.setTitle('My viz #'+viz.getId())
			setStatusAndAddWorkflowStatusEntry(viz, Phylo.WorkflowStatus.Draft)
        }
        return viz
    }

	def prepopulateEmptyFields(Phylo phyloInstance) {		
		if (StringUtils.isBlank(phyloInstance.genus) || StringUtils.isBlank(phyloInstance.species)) {
			List<String> genusNames = new ArrayList<>();
			List speciesNames = treeService.getSpeciesNamesFromTree(Integer.parseInt(phyloInstance.studyid))
			for (String species : speciesNames) {
				String genus = Phylo.parseGenusFromSpecies(species)
				if (! isValueInListCaseInsensitive(genus, genusNames)) {
					genusNames.add(genus);
				}
			}
			if (StringUtils.isBlank(phyloInstance.genus)) {
				phyloInstance.genus = Phylo.listToStringWithNewLineDelimiter(genusNames);
			}
			if (StringUtils.isBlank(phyloInstance.species)) {
				phyloInstance.species = Phylo.listToStringWithNewLineDelimiter(speciesNames)
			}
		}

		if (phyloInstance.getStatus() == null) {
			phyloInstance.setWorkflowStatus(Phylo.WorkflowStatus.Draft);
		}
	}

	boolean isValueInListCaseInsensitive(String value, List<String> list) {
		for (String listValue : list) {
			if (value.equalsIgnoreCase(listValue)) {
				return true;
			}
		}
		return false;
	}
			
    def getDemoId(){
        def demo = Phylo.findByTitle('Phylolink Demo');
        if(demo){
            return demo.getId();
        }
    }

	def getOwnerOfDemoViz() {
        utilsService.guestAccount()
	}

	def isDemonstrationViz(Phylo phyloInstance) {
		return phyloInstance.id.equals(getDemoId()) || phyloInstance.owner.userId.equals(getOwnerOfDemoViz());		
	}

	def isPublished(Phylo phyloInstance) {
		return Phylo.WorkflowStatus.Approved.equals(phyloInstance.workflowStatus)
	}
	
    /**
     * @param phyloInstance
     * @return true if the logged in user is the owner/creator of the phylolink visualisation and the workflow status
     *              is Draft or Revision Required (or has no status to handle previous visualisations)
     */
    def isAuthorised(Phylo phyloInstance){
        if (isOwner(phyloInstance)) {
			(phyloInstance.status == null || Phylo.WorkflowStatus.Draft.equals(phyloInstance.workflowStatus) ||
				Phylo.WorkflowStatus.Revision_Required.equals(phyloInstance.workflowStatus)) 
        } else {
            false
        }
    }

	def isLoggedIn() {
		return authService.getUserId() != null;
	}
	
    def isAuthorisedToView(Phylo phyloInstance) {
		if (!phyloInstance) {
			return false;
		}
//		return isPublished(phyloInstance) || isOwner(phyloInstance) || isAuthorisedWorkflowAdmin() || 
//				isDemonstrationViz(phyloInstance);
		// Business Rule as agreed with Corinna and Renee: have to be logged in to view any viz apart from the demo viz
		return isLoggedIn() || isDemonstrationViz(phyloInstance);
    }
	
	def isOwner(Phylo phyloInstance) {
		def Owner owner = phyloInstance.getOwner();
		def userId = authService.getUserId();
		if(owner && owner.getUserId() && userId) {
			userId.toString() == owner.getUserId().toString()
		} else {
			false
		}
	}
	
	def isAuthorisedWorkflowAdmin() {
		userService.userIsPhylolinkWorkflowAdmin();
	}
	
    def isAuthorisedToApprove(Phylo phyloInstance) {
		isAuthorisedWorkflowAdmin() && 
			(Phylo.WorkflowStatus.Requested_Approval.equals(phyloInstance.workflowStatus) || isAuthorised(phyloInstance))
    }
	
    def isAuthorisedToReject(Phylo phyloInstance) {
		Phylo.WorkflowStatus.Requested_Approval.equals(phyloInstance.workflowStatus) && (isAuthorisedWorkflowAdmin() || isOwner(phyloInstance))
    }

    def isAuthorisedToReinstate(Phylo phyloInstance) {
		isOwner(phyloInstance) && Phylo.WorkflowStatus.No_Longer_Required.equals(phyloInstance.workflowStatus)
    }

	def isAuthorisedToPerformStatusTransition(Phylo phyloInstance, Phylo.WorkflowStatus newStatus) throws IllegalArgumentException {
		if (phyloInstance.workflowStatus.equals(newStatus)) {
			throw new IllegalArgumentException("In isAuthorisedToPerformStatusTransition, newStatus is the same as the current status=" + newStatus)			
		}
		
		switch (newStatus) {
		    case Phylo.WorkflowStatus.Draft:
				return isAuthorisedToReinstate(phyloInstance)
		
		    case Phylo.WorkflowStatus.Requested_Approval:
		    case Phylo.WorkflowStatus.No_Longer_Required:
				return isAuthorised(phyloInstance)
		
		    case Phylo.WorkflowStatus.Approved:
				return isAuthorisedToApprove(phyloInstance)
		
		    case Phylo.WorkflowStatus.Revision_Required:
				return isAuthorisedToReject(phyloInstance)		
		}
		
        throw new IllegalArgumentException("In isAuthorisedToPerformStatusTransition, unhandled Phylo.WorkflowStatus=" + newStatus)
	}	

	public void saveMetadata(Phylo phyloInstance, Object params = null) {
		if (! isAuthorised(phyloInstance)) {
			throw new IllegalArgumentException("In saveMetadata, user is not authorised to save metadata")			
		}
		phyloInstance.setTitle(params.newTitle);
		phyloInstance.setGenus(params.newGenus);
		phyloInstance.setAuthors(params.newAuthors);
		phyloInstance.setPublishedPapers(params.newPublishedPapers);
		phyloInstance.setUnpublishedData(Boolean.valueOf(params.newUnpublishedData));
		phyloInstance.setNotes(params.newNotes);

		def newcharacterDataSetTraitsJSON = JSON.parse(params.newcharacterDataSetTraits)
				
		for (Object characterDataSetTrait : newcharacterDataSetTraitsJSON) {
			CharacterTrait characterTrait = null
			for (CharacterTrait thisCharacterTrait : phyloInstance.characterSource.characterTraits) {
				if (StringUtils.equalsIgnoreCase(thisCharacterTrait.title, characterDataSetTrait['title'])) {
					characterTrait = thisCharacterTrait
					break
				}
			}
			if (characterTrait == null) {
				characterTrait = new CharacterTrait( [
					characters: phyloInstance.characterSource,
				])
				phyloInstance.characterSource.addToCharacterTraits(characterTrait)
			}
			characterTrait.setTitle(characterDataSetTrait['title'])
			characterTrait.setDescription(characterDataSetTrait['description'])
			characterTrait.setTraitType(CharacterTrait.TraitType.forValue(characterDataSetTrait['type']))
		}
		
        phyloInstance.save(flush: true, failOnError: true)	
	}	
	
    public Map changeWorkflowStatus(Phylo phyloInstance, Object params, String visualisationURL){
		String responseMessage = "";
		String processDescription = "change status";
		Map response = null;
		try {
			Phylo.WorkflowStatus newStatus = Phylo.WorkflowStatus.forValue(params.newStatus); 
			
			if (! isAuthorisedToPerformStatusTransition(phyloInstance, newStatus)) {
				throw new IllegalStateException("Not authorised to change status from " + phyloInstance.workflowStatus + " to " + newStatus)
	        }
	
			if (newStatus.equals(Phylo.WorkflowStatus.Revision_Required) && StringUtils.isBlank(params.workflowComment)) {
				throw new IllegalArgumentException("A workflow comment must be provided when changing the status to " +
					Phylo.WorkflowStatus.Revision_Required)
			}				
			
			if (isAuthorised(phyloInstance)) {
				saveMetadata(phyloInstance, params);
			}
			
			Date workflowStatusEntryDate = null
			if (! newStatus.equals(Phylo.WorkflowStatus.Approved)) {
				workflowStatusEntryDate = setStatusAndAddWorkflowStatusEntry(phyloInstance, newStatus, params, visualisationURL)
				
				responseMessage = responseMessage + " Successfully changed status.";
			}
					
			if (newStatus.equals(Phylo.WorkflowStatus.Approved) && StringUtils.isBlank(phyloInstance.getDoi())) {
				processDescription = "create DOI";
				phyloInstance = doiService.mintDoi(phyloInstance, visualisationURL)
				responseMessage = responseMessage + " Successfully created the DOI (" + phyloInstance.doi + ").";				

				processDescription = "save DOI metadata";				
				workflowStatusEntryDate = setStatusAndAddWorkflowStatusEntry(phyloInstance, newStatus, params, visualisationURL)

				responseMessage = responseMessage + " Successfully saved DOI metadata.";
			}
		
			response = [
				message: responseMessage,
				doi: phyloInstance.doi,
				doiURL: phyloInstance.doiURL,
				doiCreationDate: phyloInstance.doiCreationDate,
				edit: isAuthorised(phyloInstance),
				userCanApprove: isAuthorisedToApprove(phyloInstance),
				userCanReject: isAuthorisedToReject(phyloInstance),
				userCanReinstate: isAuthorisedToReinstate(phyloInstance),
				workflowStatusEntryDate: workflowStatusEntryDate
			]
			
		} catch (Exception e) {
			log.error("Exception while trying to " + processDescription + " for Phylo with Id=" + phyloInstance?.getId(), e);
			response = [
				message: responseMessage + " Failed to " + processDescription + "."
			]
		}

		response
    }	
	
	public Date setStatusAndAddWorkflowStatusEntry(Phylo phyloInstance, Phylo.WorkflowStatus newStatus, Object params = null, 
			String visualisationURL = null) throws IllegalStateException {

		def currentUser = userService.registerCurrentUser();
	
		if (currentUser == null) {
			throw new IllegalStateException("Must be authenticated to add a workflow comment")
		}

		Date workflowStatusEntryDate = phyloInstance.doiCreationDate ?: new Date()
		
		def workflowStatusEntry = new WorkflowStatusEntry( [
			phylo: phyloInstance,
			owner: currentUser,
			date: workflowStatusEntryDate,
			comment: (params?.workflowComment?:null)
		])
		workflowStatusEntry.setWorkflowStatus(newStatus)

		phyloInstance.addToWorkflowStatusEntries(workflowStatusEntry)
		Phylo.WorkflowStatus oldStatus = null
		if (params) {
			oldStatus = phyloInstance.getWorkflowStatus()
		}
		phyloInstance.setWorkflowStatus(newStatus)

		if (newStatus.equals(Phylo.WorkflowStatus.Approved) && phyloInstance.characterSource != null) {
			phyloInstance.characterSource.setCanDelete(Boolean.FALSE)
		}
		
        phyloInstance.save(flush: true, failOnError: true)	
		
		if (newStatus.equals(Phylo.WorkflowStatus.Approved)) {
			def sandbox = Sandbox.get(phyloInstance.source)
			sandbox.setCanDelete(Boolean.FALSE)

			sandbox.save(flush: true, failOnError: true)	
		}
		
		if (oldStatus) {
			sendStatusChangeEmailToAllRecipients(phyloInstance, currentUser, oldStatus, newStatus, visualisationURL)
		}
		
		workflowStatusEntryDate
	}

	private sendStatusChangeEmailToAllRecipients(Phylo phyloInstance, Owner currentUser, Phylo.WorkflowStatus oldStatus, 
			Phylo.WorkflowStatus newStatus, String visualisationURL) {
		String sender = null;
		Map model = null;
		try {
			def noreply = grailsApplication.config.getProperty('support.noreply', String, 'no-reply@ala.org.au')
			sender = "phylolink <$noreply>"
			model = [phyloInstance: phyloInstance, oldStatus: oldStatus, newStatus: newStatus, visualisationURL: visualisationURL]

			if (! isOwner(phyloInstance)) { // Workflow admin who is not the owner has approved or revision required, so let owner know
				Boolean actionRequired = ! (newStatus.equals(Phylo.WorkflowStatus.Approved) || newStatus.equals(Phylo.WorkflowStatus.Requested_Approval))
				sendStatusChangeEmail(phyloInstance.owner.email, sender, actionRequired, "Your Phylolink visualisation now has status " + newStatus, 
						model, '/phylo/emails/status-change-to-creator')
			}

			Boolean actionRequired = null
			if (newStatus.equals(Phylo.WorkflowStatus.Requested_Approval)) {
				actionRequired = Boolean.TRUE
			} else if (oldStatus.equals(Phylo.WorkflowStatus.Requested_Approval) && 
					(newStatus.equals(Phylo.WorkflowStatus.Approved) || newStatus.equals(Phylo.WorkflowStatus.Revision_Required))) {
				actionRequired = Boolean.FALSE
			}
			if (actionRequired != null) {
				List<String> recipients = userService.getPhylolinkWorkflowAdminEmailRecipients()
				sendStatusChangeEmail(recipients, sender, actionRequired, "Phylolink visualisation with ID " + phyloInstance.id + " now has status " + newStatus, 
					model, '/phylo/emails/status-change-to-approver')
			}

		} catch (Exception ignored) {
			log.error("IGNORING exception while trying to sendStatusChangeEmail for status change " + oldStatus + " to " + 
					newStatus + " for Phylo with Id=" + phyloInstance?.getId(), ignored);			
		}
	}

	private sendStatusChangeEmail(Object recipient, String sender, Boolean actionRequired, String subjectText, Map model, String htmlView) {
		try {
			if (actionRequired) {
				subjectText = "[ACTION REQ] " + subjectText
				model['actionRequiredText'] = "log on, then go to the Metadata tab to review and action"
			} else {
				subjectText = "[FYI] " + subjectText
				model['actionRequiredText'] = "view"
			}

			emailService.sendEmailView(recipient, sender, subjectText, model, htmlView)		
		} catch (Exception ignored) {
			log.error("IGNORING exception while trying to sendStatusChangeEmail to recipient=" + recipient + " for status change " + 
					model['oldStatus'] + " to " + model['newStatus'] + " for Phylo with Id=" + model['phyloInstance']?.getId() + 
					" and htmlView=" + htmlView, ignored);
		}
	}

	/**
	* filter search will return all visualisations with status "Approved and Published" where for each of the words (limited to the first 5) in the search filter, 
	* the word must be present (case insensitive) in at least one of the following visualisation fields: title, genus, species, authors, publishedPapers, created by, doi
	**/
    @ReadOnly
    def listPublishedViz(String filter, int pageSize, int startFrom, String sortBy = "doiCreationDate", String sortOrder = "desc") {
		Map hqlParams = [ max: pageSize, offset: startFrom ]
		
		String hql = "from Phylo as p " +
                     "where p.status = '" + Phylo.WorkflowStatus.Approved.getKey() + "'"

		if (StringUtils.isNotBlank(filter)) {
			String[] words = filter.trim().replaceAll("%", "").toLowerCase().split(" ")
			int i = 0
			for (String word : words) {
				if (StringUtils.isNotBlank(word)) {
					i++;
					hqlParams.put("word" + i, "%" + word.toLowerCase() + "%");
					hql = hql + " and (lower(p.title) like :word" + i + " or lower(p.genus) like :word" + i + " or lower(p.species) like :word" + i +
							    " or lower(p.authors) like :word" + i + " or lower(p.publishedPapers) like :word" + i +
							    " or lower(p.owner.displayName) like :word" + i + " or lower(p.doi) like :word" + i +
							    ")"
					// Only do a maximum of 5 words
					if (i == 5) {
						break;
					}
				}
			}
		}
		
		def rowCount = Phylo.executeQuery("select count(p) " + hql, hqlParams).get(0)
		
		hql = hql + " order by p." + sortBy + " " + sortOrder

		log.debug("In listPublishedViz hql='${hql}', hqlParams=${hqlParams}")
		
		return [list : Phylo.findAll(hql, hqlParams), totalCount : rowCount]
	}

    @ReadOnly
    def listVizThatRequireMyAttention(String sortBy = "id", String sortOrder = "asc") {

		def userId = authService.getUserId();
		
		if (userId == null) {
			return new ArrayList();
		}
			
		String hql = "from Phylo as p " +
                     "where (p.owner.userId = :userId and p.status = '" + Phylo.WorkflowStatus.Revision_Required.getKey() + "')"
		if (isAuthorisedWorkflowAdmin()) {
			hql = hql + " or p.status = '" + Phylo.WorkflowStatus.Requested_Approval.getKey() + "'"
		}
		hql = hql + " order by p." + sortBy + " " + sortOrder		
		return Phylo.findAll(hql, [userId: Long.valueOf(userId)])
    }
}
