<div id="manageExpert" style="margin-top:15px; ">
    <div data-bind="visible: ! haveOccurrences()">
        You must add <a id="goToOccurrencesTab" href="javascript:void(0)" aria-controls="profile">Occurrences</a> and select a specific dataset, before you can create an expert visualisation.
        <script>
            $( "#goToOccurrencesTab" ).click(function() {
                $('#recordsTab').trigger("click");
            });
        </script>
    </div>
    <form data-bind="visible: haveOccurrences">
        <p data-bind="visible: ! formDisabled()">If you wish to publish this visualisation, please enter the required metadata below.<br/>
		Please note that for personal use, this visualisation is already saved and the metadata below is not required.<p/>

        <div class="row col-md-12 form-group">
            <label class="control-label" for="title">Visualisation title <span class="mandatory">*</span></label>
            <div class="controls">
                <input name="title" class="form-control" 
                       data-bind="value: title, attr:{disabled: formDisabled()}"
                       type="text"
                       maxlength="255"
                       required
                       autofocus
                       placeholder="The evolution and phylogenetic placement of invasive Australian Acacia species."
                       onfocus="utils.clearPlaceholder(this)"
                />
            </div>                
        </div>

        <div class="row col-md-12 form-group">
            <table class="table-sm readOnlyTable readOnlyInfoInForm">
                <tbody>
                    <tr>
                        <td class="col-md-2">Created by</td>
                        <td>${phyloInstance.owner.displayName}</td>
                    </tr>
                    <tr data-bind="visible: doi">
                        <td class="col-md-2">Visualisation DOI</td>
                        <td><a data-bind="text: doi, attr{href:doiURL}" target="_blank"></a></td>
                    </tr>
                    <tr data-bind="visible: doi">
                        <td class="col-md-2">DOI Citation URL</td>
                        <td><a data-bind="text: doiCitationURL, attr{href:doiCitationURL}" target="_blank"></a></td>
                    </tr>                    
                    <tr data-bind="visible: doi">
                        <td class="col-md-2">Published on</td>
                        <td><span data-bind="text: doiCreationDateFormatted"></span></td>
                    </tr>
                </tbody>
            </table>
        </div>        

        <div class="row">
            <div class="col-md-6" style="width: 48.5%;">
                <div class="form-group">
                    <label class="control-label" for="genus">Genus <span class="mandatory">*</span></label>
                    <textarea name="genus" class="form-control" 
                           data-bind="value: genus, attr:{disabled: formDisabled()}"
                           required
                           placeholder="One per line."
                           onfocus="utils.clearPlaceholder(this)"
                           rows="8"
                    ></textarea>
                </div>
            </div>
            <div class="col-md-6" style="width: 48.5%;">
                <div class="form-group">
                    <label class="control-label" for="species">Species </label>
                    <textarea name="species" class="form-control" 
                           data-bind="value: species"
                           disabled
                           placeholder="One per line."
                           onfocus="utils.clearPlaceholder(this)"
                           rows="8"
                    ></textarea>
                </div>
            </div>
        </div>

        <div class="row col-md-12 form-group">
            <label class="control-label" for="authors">Expert Visualisation Author(s) <span class="mandatory">*</span></label>
            <div class="controls">
                <input name="authors" class="form-control" 
                       data-bind="value: authors, attr:{disabled: formDisabled()}"
                       type="text"
                       required
                       placeholder="Last Name, First Name of each Author; Separated by semicolons e.g. Bloggs, Joe; Smith, Jane;"
                       onfocus="utils.clearPlaceholder(this)"
                />
            </div>                
        </div>

        <div class="row col-md-12 form-group">
            <label class="control-label" for="publishedPapers">Citation of Published Paper(s)</label>
            <div class="controls">
                <textarea name="publishedPapers" class="form-control" 
                       data-bind="value: publishedPapers, attr:{disabled: formDisabled()}"
                       placeholder="e.g. Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x"
                       onfocus="utils.clearPlaceholder(this)"
                       rows="4"
                ></textarea>
            </div>                
        </div>

        <div class="row col-md-12 form-group" style="margin-left:5px;">
            <div class="controls">
                <label class="checkbox">
                    <input name="unpublishedData"  
                           data-bind="checked: unpublishedData, attr:{disabled: formDisabled()}"
                           type="checkbox"
                    />
                    Contains Unpublished data
                </label>
            </div>                
        </div>

        <div class="row col-md-12 form-group">
            <label class="control-label" for="notes">Notes</label>
            <div class="controls">
                <textarea name="notes" class="form-control" 
                       data-bind="value: notes, attr:{disabled: formDisabled()}"
                       placeholder=""
                       onfocus="utils.clearPlaceholder(this)"
                       rows="3"
                ></textarea>
            </div>                
        </div>

        <div data-bind="visible: haveCharacters" class="row col-md-12 form-group">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Character metadata
                </div>
                <div class="panel-body">
                    <table class="table table-sm">
                        <tbody>
                        <tr>
                            <td class="col-md-2 control-label">Dataset:</td>
                            <td data-bind="text: characterDataSetTitle"></td>
                        </tr>
                        </tbody>
                    </table>
            
                    <div style='margin-top: -12px;'>
                        <a class="btn btn-default" target="_blank" data-bind="attr: {href: characterListUrl}">
                            View character list
                        </a>
                    </div>
					
                    <div data-bind='visible: characterDataSetTraits().length > 0' style='margin-top: 12px;'>
						<p data-bind="visible: ! formDisabled()">
							Please provide information on your Character dataset.  Any values currently not specified can be loaded from a CSV 
							file (you can download an
                            <a data-bind="attr{href:sampleCSV}" target="_blank">example CSV file here</a>):
							<label class="btn btn-default btn-file">
								<i class="glyphicon glyphicon-upload"> </i>
								<span>Load values from a CSV file of character metadata</span>
								<input id="characterMetadataFileInput" type="file" value="Upload" accept=".csv" style="display: none;"/>
							</label>
							<div class="row col-md-12 form-group" data-bind="visible:characterMetadataSuccessMessage">
								<div class="alert alert-success" data-bind="text:characterMetadataSuccessMessage"></div>
							</div>
							<div class="row col-md-12 form-group" data-bind="visible:characterMetadataErrorMessage">
								<div class="alert alert-danger" data-bind="text:characterMetadataErrorMessage"></div>
							</div>
						<p/>
						
						<table class="table table-sm">
							<thead>
								<tr>
									<th class="col-md-2">Character name</th>
									<th>Description*</th>
									<th class="col-md-2">Type*</th>
									<%--th>Image</th--%>
								</tr>
							</thead>
							<tbody data-bind='foreach: characterDataSetTraits'>
								<tr>
									<td data-bind="text: title"></td>
									<td><input required data-bind='value: description, uniqueName: true, visible: ! $root.formDisabled()' class="form-control" type="text"/>
										<span data-bind='text: description, visible: $root.formDisabled()'></span>
									</td>
									<td><select required class="form-control" data-bind="options: $root.characterDataSetTraitTypes, value: type, optionsCaption:'Choose..', disable: $root.formDisabled()"></select></td>
									<%--td><select class="form-control" data-bind="options: $root.characterDataSetTraitImages, value: imageFileName, optionsCaption:'Choose..', disable: $root.formDisabled()"></select></td--%>
								</tr>
							</tbody>
						</table>
                    </div>
                </div>
            </div>
        </div>        
		
		<g:if test="${showWorkflowStatusEntries}">
			<div class="row col-md-12 form-group">
				<table class="table-sm readOnlyTable">
					<thead>
						<th>Date</th>
						<th>Status</th>
						<th>Actioned by</th>
						<th>Comment</th>
					</thead>
					<tbody id="workflowStatusEntriesTBody">
						<g:each in="${phyloInstance.workflowStatusEntries}">
							<tr>
								<td>${it.date}</td>
								<td>${it.getWorkflowStatus()}</td>
								<td>${(userIsWorkflowAdmin || userId.toString().equals(it.owner.userId.toString())) ? it.owner.displayName : 'An Approver'}</td>
								<td>${it.comment}</td>
							</tr>
						</g:each>
					</tbody>
				</table>
			</div>
		</g:if>

        <div class="row col-md-12 form-group" data-bind="visible: !formDisabled() || userCanReject() || userCanApprove()">
            <label class="control-label" for="workflowComment">Workflow Comment</label>
            <div class="controls">
                <textarea id="workflowComment" name="workflowComment" class="form-control" 
                       data-bind="value: workflowComment"
                       placeholder="Please note this comment is only saved when a workflow action button is used below, so don't use this as a scratchpad."
                       rows="3"
                ></textarea>
            </div>                
        </div>

        <div class="row col-md-12 form-group">
			All work and data associated with this visualisation is licensed and downloadable under
			<ul>
				<g:each in="${licences}">
					<li>${it}</li>
				</g:each>			
			</ul>
        </div>			
		
		<g:if test="${showWorkflowStatusEntries}">
			<div class="row col-md-12 form-group" style="font-weight: bold;" data-bind="visible: ! doi()">
				Please note that once approved, this visualisation will be published and visible to anyone and can not be changed.
			</div>
		</g:if>
		
        <div id="expertButtons" class="row col-md-12 form-group">
            <button data-bind="click: save, visible: !formDisabled()" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-floppy-save"> </i>
                Save
            </button>
            <button data-bind="click: requestApproval, visible: !formDisabled()" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-share-alt"> </i>
                Request Approval
            </button>
            <button data-bind="click: approve, visible: userCanApprove" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-ok"> </i>
                Approve and Publish
            </button>
            <button data-bind="click: revisionRequired, visible: userCanReject" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-repeat"> </i>
                Revision Required
            </button>
            <button data-bind="click: noLongerRequired, visible: !formDisabled()" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-remove"> </i>
                No Longer Required
            </button>
            <button data-bind="click: reinstate, visible: userCanReinstate" class="btn btn-primary btn-sm">
                <i class="glyphicon glyphicon-refresh"> </i>
                Reinstate
            </button>
        </div>
        <div class="row col-md-12 form-group" data-bind="visible:successMessage">
            <div class="alert alert-success" data-bind="text:successMessage"></div>
        </div>
        <div class="row col-md-12 form-group" data-bind="visible:errorMessage">
            <div class="alert alert-danger" data-bind="text:errorMessage"></div>
        </div>

        <div class="row col-md-12 form-group">
            <g:render template="metadata"></g:render>
        </div>
        
        <div class="row col-md-12 form-group">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Occurrence metadata
                </div>
                <div class="panel-body">
                    <table class="table table-sm">
                        <tbody>
                        <tr>
                            <td class="col-md-2 control-label">Dataset:</td>
                            <td data-bind="text: sourceDisplayTitle"></td>
                        </tr>
                        </tbody>
                    </table>
            
                    <div>
                        <a class="btn btn-default" target="_blank" data-bind="attr: {href: biocacheQueryUrl}">
                            View records
                        </a>
                    </div>
                </div>
            </div>
        </div>        
    </form>

<%-- Uncomment to test the phylolink doi display template: doi-service/grails-app/views/doiResolve/_phylolink.gsp
		g:render template="doi" model="[doi: doi]"/ --%>
</div>
