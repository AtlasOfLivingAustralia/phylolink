package au.org.ala.phyloviz

import au.org.ala.names.model.LinnaeanRankClassification
import au.org.ala.names.model.NameSearchResult
import au.org.ala.names.search.ALANameSearcher

import javax.annotation.PostConstruct

class NameService {
    def grailsApplication
    ALANameSearcher nameSearcher
    LinnaeanRankClassification rankClassification

    @PostConstruct
    def init() {
        nameSearcher = new ALANameSearcher("${grailsApplication.config.name.index.location}")
        rankClassification = new LinnaeanRankClassification()
    }

    String getLSID(String name) {
        try{
            rankClassification.setScientificName(name)
            nameSearcher.searchForAcceptedLsidDefaultHandling(rankClassification, false)
        } catch (Exception e){
            log.error(e.getMessage(), e)
        }
    }

    Map getRecord(String name){
        String sName = matchName(name)
        String lsid = getLSID(sName)
        [
                lsid: lsid,
                guid: lsid,
                name: sName
        ]
    }

    String matchName(String providedName) {
        try {
            rankClassification.setScientificName(providedName)
            NameSearchResult result = nameSearcher.searchForAcceptedRecordDefaultHandling(rankClassification, true, true)
            result?.getRankClassification()?.getScientificName()
        } catch (Exception e){
            log.error(e.getMessage(), e)
        }
    }
}
