/**
 * Created by Temi Varghese on 19/06/15.
 */
package au.org.ala.phyloviz

class ConvertSandbox extends au.org.ala.phyloviz.ConvertDomainObject {
    ConvertSandbox() {
        prop = [
                'id'            : 'id',
                'drid'          : 'drid',
                'scientificName': 'scientificName',
                'serverInstance': 'instanceUrl',
                'title'         : 'title',
                'biocacheServiceUrl': 'biocacheServiceUrl',
                'biocacheHubUrl': 'biocacheHubUrl',
                'dateCreated': 'dateCreated'
        ]
    }

    def convert(obj){
        def result = convert(obj, [:]);
        return addProperties(result);
    }

    def addProperties(obj){
        obj['layerUrl'] = obj['biocacheServiceUrl'] + "/webportal/wms/reflect";
        obj['type'] = 'sandbox';
        return obj;
    }
}
