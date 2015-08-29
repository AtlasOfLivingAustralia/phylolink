package au.org.ala.phyloviz

import au.org.ala.names.model.LinnaeanRankClassification
import au.org.ala.names.model.NameSearchResult
import au.org.ala.names.search.ALANameSearcher

import javax.annotation.PostConstruct

class NameService {
    def grailsApplication
    ALANameSearcher nameSearcher

    @PostConstruct
    def init() {
        nameSearcher = new ALANameSearcher("${grailsApplication.config.name.index.location}")
    }

    String getLSID(String name) {
        nameSearcher.searchForLSID(name)
    }

    String matchedName(String providedName) {
        LinnaeanRankClassification rankClassification = new LinnaeanRankClassification()
        rankClassification.setScientificName(providedName)
        NameSearchResult result = nameSearcher.searchForAcceptedRecordDefaultHandling(rankClassification, true, true)

        result?.getRankClassification()?.getScientificName()
    }
}
