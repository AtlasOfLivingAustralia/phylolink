<div class='selection-info' role='alert' id='selectionInfo'>
    <table>
        <tr><td data-bind='text: selectedDr'></td></tr>
        <tr><td data-bind='text: selectedClade'></td></tr>
        <tr><td data-bind='visible: selectedCladeNumber() >= ${grailsApplication.config.biocache.maxBooleanClauses}'
                class='alert-error'>
            limited to the first ${grailsApplication.config.biocache.maxBooleanClauses} taxa</td>
        </tr>
    </table>
</div>