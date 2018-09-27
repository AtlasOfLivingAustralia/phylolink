package au.org.ala.phylolink

enum TrimOption {
    /**
     * Only show Australian species
     */
    AUSTRALIAN_ONLY,

    /**
     * Only show species where the ALA has occurrence records
     */
    ALA_ONLY,

    /**
     * Only show species that are present in the specified list.
     *
     * If this option is chosen, the dataResourceId of the list will need to be provided
     */
    SPECIES_LIST,

    /**
     * Don't trim (i.e. show everything)
     */
    NONE
}