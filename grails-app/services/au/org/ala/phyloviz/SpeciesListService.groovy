package au.org.ala.phyloviz

import au.com.bytecode.opencsv.CSVReader
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.ContentType

@Transactional
class SpeciesListService {
    def webServiceService, grailsApplication, authService

    /**
     * save a csv file into list tool
     */
    def createList(CSVReader reader, String name, Integer colIndex, String cookie) {

        def data = [:], ch
        def result, next, rcount = 0, ccount = 0, item, row, items, header
        data['listType'] = 'SPECIES_CHARACTERS'
        data['listName'] = name
        data['listItems'] = []
        data['isPrivate'] = true
        // first line is header
        header = reader.readNext();
        while ((next = reader.readNext()) != null) {
            ccount = 0;
            row = [:]
            items = []
            next.each { column ->
                item = [:]
                if (colIndex != ccount) {
                    item['key'] = header[ccount];
                    item['value'] = (next[ccount] ?: 'undefined').toString();
                    items.push(item)
                }
                ccount++
            }
            row['kvpValues'] = items
            row['itemName'] = next[colIndex] ?: '';
            data['listItems'].push(row);
            rcount++;
        }

        result = webServiceService.postData(grailsApplication.config.listPost, data, ['cookie': cookie], org.apache.http.entity.ContentType.APPLICATION_JSON);

        if (result && result.druid) {
            ch = addCharacterToDB(name, result.druid)
            result.id = ch.id;
            result.listurl = grailsApplication.config.listsPermUrl.replaceAll("DRID", result.druid)
            result
        }
        return result
    }

    /**
     * make an entry to character table when character matrix is uploaded to list tool.
     * @param title
     * @param drid
     * @return
     */
    def addCharacterToDB(String title, String drid) {
        Owner own = Owner.findByUserId(authService.getUserId() ?: -1);
        def charList = [
                'owner': own,
                'title': title,
                'drid' : drid,
				'canDelete': true
        ]
        def c = new Characters(charList).save(
                flush: true
        )
        return c;
    }

}
