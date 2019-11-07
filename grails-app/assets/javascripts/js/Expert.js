/**
 * The _expert.gsp file contains the view associated with this script. This code is used by the Expert tab in the UI.
 */
var Expert = function (opt) {
    // emitter mixin. adding functions that support events.
    new Emitter(this);
    var $ = jQuery;
    var options = opt;
    var pj = options.pj;
    var titleViewModel = options.pj.titleViewModel;
    var tabDivId = 'manageExpert';
    
    var ExpertViewModel = function () {
        new Emitter(this);
        var self = this;
        var spinner = new Spinner({
            top: '50%',
            left: '50%',
            className: 'loader'
        });

        self.title = ko.observable(titleViewModel.title());
        self.genus = ko.observable(options.genus || "");
        self.species = ko.observable(options.species || "");
        self.authors = ko.observable(options.authors || "");
        self.publishedPapers = ko.observable(options.publishedPapers || "");
        self.unpublishedData = ko.observable(options.unpublishedData || false);
        self.doi = ko.observable(options.doi || "");
        self.doiURL = ko.observable(options.doiURL || "");
        self.notes = ko.observable(options.notes || "");
        self.status = ko.observable(options.status || "");
        self.workflowComment = ko.observable("");
        self.doiCreationDate = ko.observable(options.doiCreationDate);
        self.userCanApprove = ko.observable(options.userCanApprove);
        self.userCanReject = ko.observable(options.userCanReject);
        self.userCanReinstate = ko.observable(options.userCanReinstate);
        self.edit = ko.observable(options.edit);
        
        self.formDisabled = function() {
            return ! self.edit();
        };

        self.successMessage = ko.observable("");
        self.errorMessage = ko.observable("");
        
        self.sourceId = ko.observable(options.records.config.initResourceId);
        self.sourceDisplayTitle = ko.observable("");
        self.biocacheQueryUrl = ko.observable("");
        
        self.haveOccurrences = ko.computed(function() {
        	var sourceId = this.sourceId();
            return sourceId != null && sourceId != "" && sourceId != options.records.config.allOccurrencesSourceId;
        }, this);

    	self.characterDataSetId = ko.observable(options.character.config.initCharacterResourceId);
    	self.characterDataSetTitle = ko.observable("");
    	self.characterListUrl = ko.observable("");		
    	self.characterDataSetTraits = ko.observableArray([]);  // see convertCharacterDataSetTraits() below which converts options.characterDataSetTraits		
		self.characterDataSetTraitTypes = ko.observableArray(options.characterDataSetTraitTypes);
		self.characterDataSetTraitImages = ko.observableArray([]);
        self.sampleCSV = options.sampleCSV;

        self.characterMetadataSuccessMessage = ko.observable("");
        self.characterMetadataErrorMessage = ko.observable("");
		
		self.haveCharacters = ko.computed(function() {
        	var characterDataSetId = this.characterDataSetId();
            return characterDataSetId != null && characterDataSetId != "" && characterDataSetId.toString() != "0";
        }, this);
		
        self.doiCreationDateFormatted = ko.computed(function() {
        	return dateToString(this.doiCreationDate());
        }, this);
        
        self.doiCitationURL = ko.computed(function() {
        	var doi = this.doi();
        	if (doi == null || doi == "") {
        		return null;
        	}
            return (options.doiCitationUrlPrefix + doi);
        }, this);
		
        self.save = function() {
        	self.successMessage(null);
        	self.errorMessage(null);

        	// Perform HTML5 validation on the form
			// Allow partial save so don't perform validation, instead changeStatus will do the validation
//        	if (! document.getElementById(tabDivId).getElementsByTagName("FORM")[0].reportValidity()) {
        		// Validation failed so return
//        		return;
//        	}
        	
        	titleViewModel.title(self.title());

            self.startSaving();

            var syncData = {
            	id: options.phyloId, 
                newTitle: self.title(),
                newGenus: self.genus(),
                newAuthors: self.authors(),
            	newPublishedPapers: self.publishedPapers(),
            	newUnpublishedData: self.unpublishedData(),
                newNotes: self.notes(),
				newcharacterDataSetTraits: ko.toJSON(self.characterDataSetTraits)
            };

            $.ajax({
                url: options.syncUrl,
                dataType: 'JSON',
                type: "PUT",                
                data: syncData,
                success: function (data) {
                	self.successMessage(data.message);
                	self.errorMessage(null);
                    self.stopSaving();
                },
                error: function (xhr, status, error){
                	self.successMessage(null);
                	self.errorMessage('Failed to save.  Error message = ' + xhr.responseJSON.error);
                    self.stopSaving();
                }
            });
        };
        
        self.changeStatus = function(newStatus) {
        	self.successMessage(null);
        	self.errorMessage(null);

       		document.getElementById("workflowComment").required = (newStatus == options.revisionRequiredStatus);
        	
        	// Perform HTML5 validation on the form
        	if (! document.getElementById(tabDivId).getElementsByTagName("FORM")[0].reportValidity()) {
        		// Validation failed so return
        		return;
        	}
        	
        	titleViewModel.title(self.title());

            self.startSaving();

            var syncData = {
            	id: options.phyloId, 
                newTitle: self.title(),
                newGenus: self.genus(),
                newAuthors: self.authors(),
            	newPublishedPapers: self.publishedPapers(),
            	newUnpublishedData: self.unpublishedData(),
                newNotes: self.notes(),
				newcharacterDataSetTraits: ko.toJSON(self.characterDataSetTraits),
                newStatus: newStatus,
                workflowComment: self.workflowComment()
            };

            $.ajax({
                url: options.changeStatusUrl,
                dataType: 'JSON',
                type: "PUT",                
                data: syncData,
                success: function (data) {
                	self.successMessage(data.message);
                	self.errorMessage(null);
                	self.doi(data.doi);
                	self.doiURL(data.doiURL);
                	self.doiCreationDate(data.doiCreationDate);
                	self.edit(data.edit);
					options.records.dataresourceViewModel.edit(data.edit);
					options.character.characterViewModel.edit(data.edit);
					titleViewModel.edit(data.edit);
                	self.userCanApprove(data.userCanApprove);
                	self.userCanReject(data.userCanReject);
                	self.userCanReinstate(data.userCanReinstate);
					self.status(newStatus);

					$('#workflowStatusEntriesTBody').append('<tr><td>' + dateToString(data.workflowStatusEntryDate) + 
						'</td><td>' + newStatus +
						'</td><td>' + options.userName +
						'</td><td>' + self.workflowComment() + '</td></tr>');
                	
					self.workflowComment('');

                	self.stopSaving();
                },
                error: function (xhr, status, error){
                	self.successMessage(null);
                	self.errorMessage('Failed to change status.  Error message = ' + xhr.responseJSON.error);
                    self.stopSaving();
                }
            });
        };
        
        self.startSaving = function () {
            if (!spinner || !spinner.el) {
                spinner = new Spinner({
                    top: '50%',
                    left: '50%',
                    className: 'loader'
                });
            }
            spinner.spin();
            $('#expertButtons').append(spinner.el);
        };

        self.stopSaving = function () {
            spinner.stop();
        };

        self.requestApproval = function () {
        	self.changeStatus(options.requestedApprovalStatus);
        };

        self.approve = function () {
        	self.changeStatus(options.approvedStatus);
        };

        self.revisionRequired = function () {
        	self.changeStatus(options.revisionRequiredStatus);
        };

        self.noLongerRequired = function () {
        	self.changeStatus(options.noLongerRequiredStatus);
        };

        self.reinstate = function () {
        	self.changeStatus(options.draftStatus);
        };
    };

    var expertViewModel = new ExpertViewModel();
	convertCharacterDataSetTraits();
    ko.applyBindings(expertViewModel, document.getElementById(tabDivId));

    //Subscribe to when the title is updated. See https://knockoutjs.com/documentation/observables.html
    titleViewModel.title.subscribe(function(newValue) {
    	expertViewModel.title(newValue);
    });

    //Subscribe to when the occurrence dataset is updated
    options.records.dataresourceViewModel.selectedValue.subscribe(function(newValue) {
    	expertViewModel.sourceId(newValue === undefined ? null : newValue.id());
    	expertViewModel.sourceDisplayTitle(newValue === undefined ? null : newValue.displayTitle());
    	expertViewModel.biocacheQueryUrl(newValue === undefined ? null : newValue.biocacheQueryUrl());
    });        

    //Subscribe to when the character dataset is updated
	options.character.on('setcharacterlist', function() {
		var newValue = options.character.characterViewModel.list();
    	expertViewModel.characterDataSetId(newValue === undefined || newValue == null ? null : newValue.id);
    	expertViewModel.characterDataSetTitle(newValue === undefined || newValue == null ? null : newValue.title);
    	expertViewModel.characterListUrl(newValue === undefined || newValue == null ? null : newValue.listurl);
		
		var existingCharacterDataSetTraits = expertViewModel.characterDataSetTraits();
		expertViewModel.characterDataSetTraits([]);
		traitTitles = options.character.characterViewModel.activeCharacterList();
		for(var i = 0; i < traitTitles.length; i++) {
			var characterDataSetTrait = findMatchingCharacterDataSetTrait(traitTitles[i], existingCharacterDataSetTraits);
			if (characterDataSetTrait == null) {				
				characterDataSetTrait = new CharacterDataSetTrait(traitTitles[i], "", null, null);
			}
			expertViewModel.characterDataSetTraits.push(characterDataSetTrait);
		}		
	});

    //Subscribe to when a new character is added to the tree
	options.character.characterViewModel.on('newchar', function() {
		var character = options.character.characterViewModel.selectedCharacter();
		var title = character.name();
		
		var existingCharacterDataSetTraits = expertViewModel.characterDataSetTraits();
		var characterDataSetTrait = findMatchingCharacterDataSetTrait(traitTitles[i], existingCharacterDataSetTraits);
		if (characterDataSetTrait != null) {
			character.description(characterDataSetTrait.description());
			character.type(characterDataSetTrait.type());
		}
	});
	
	function convertCharacterDataSetTraits() {
		var existingCharacterDataSetTraits = expertViewModel.characterDataSetTraits();
		expertViewModel.characterDataSetTraits([]);
		for(var i = 0; i < options.characterDataSetTraits.length; i++) {
			var initCharacterDataSetTrait = options.characterDataSetTraits[i];
			var characterDataSetTrait = findMatchingCharacterDataSetTrait(initCharacterDataSetTrait.title, existingCharacterDataSetTraits);
			if (characterDataSetTrait == null) {				
				characterDataSetTrait = new CharacterDataSetTrait(initCharacterDataSetTrait.title, initCharacterDataSetTrait.description, 
						initCharacterDataSetTrait.type, initCharacterDataSetTrait.image);
			}
			expertViewModel.characterDataSetTraits.push(characterDataSetTrait);			
		}
	}
	
	function CharacterDataSetTrait(title, description, type, imageFileName) {  
		this.title = ko.observable(title);
		this.description = ko.observable(description);
		this.type = ko.observable(type);
		this.imageFileName = ko.observable(imageFileName);
	}
	
	function findMatchingCharacterDataSetTrait(traitTitle, characterDataSetTraits) {
		for(var i = 0; i < characterDataSetTraits.length; i++) {
			if (traitTitle.toLocaleLowerCase() === characterDataSetTraits[i].title().toLocaleLowerCase()) {
				return characterDataSetTraits[i];
			}
		}
		return null;
	}

	$("#characterMetadataFileInput").on('change', function(event) {
		expertViewModel.characterMetadataSuccessMessage(null);
		expertViewModel.characterMetadataErrorMessage(null);

		var file = event.target.files[0];
		if(!file){
			return;
		}
		Papa.parse(file, {
			header: true,
			complete: function(results) {
				var data = results.data;
					 
				var existingCharacterDataSetTraits = expertViewModel.characterDataSetTraits();
				for(i=0;i<data.length;i++) {
					var line = data[i];
					var title = (line.title ? line.title : line.character);
					if (title) {
						var characterDataSetTrait = findMatchingCharacterDataSetTrait(title.trim(), existingCharacterDataSetTraits);
						if (characterDataSetTrait != null) {
							if (! characterDataSetTrait.description()) {
								var description = (line.description ? line.description : line.character_description);
								if (description) {
									characterDataSetTrait.description(description.trim());
								}
							}
							if (! characterDataSetTrait.type()) {								
								var type = (line.type ? line.type : line.character_type);
								if (type) {
									characterDataSetTrait.type(toCamelCase(type.trim()));
								}
							}
							if (! characterDataSetTrait.imageFileName()) {								
								var imageFileName = (line.image ? line.image : line.character_image);
								if (imageFileName) {
									characterDataSetTrait.imageFileName(imageFileName.trim());
								}
							}
						}
					}
				}
				expertViewModel.characterMetadataSuccessMessage("Successfully loaded values that were previously not specified from the csv file.");
			}
		});
	});
};

function toCamelCase(str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
};

$("#workflowStatusEntriesTBody tr td:first-child").each(function(){
	$(this).html(dateToString($(this).html()));
});

