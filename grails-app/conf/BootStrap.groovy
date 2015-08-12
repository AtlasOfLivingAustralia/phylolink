import au.org.ala.phyloviz.Characters
import au.org.ala.phyloviz.Owner
import au.org.ala.phyloviz.Phylo
import au.org.ala.phyloviz.Tree
import grails.util.Environment

class BootStrap {
    def opentreeService
    def utilsService
    def grailsApplication

    def init = { servletContext ->
        // since bootstrap loads expert trees and expert tree requires curator app on web2py, we cannot
        // proceed further as it will break the test on travis.
        if (Environment.current == Environment.TEST) {
            return;
        }

        if (Boolean.parseBoolean(grailsApplication.config.bootstrap.skip) ) {
            return;
        }

        log.debug('checking for system user name')
        def systemUser = Owner.findByDisplayName("System")
        if (!systemUser) {
            systemUser = new Owner(
                    userId: 1,
                    displayName: "System",
                    email: "phylojive@ala.org.au",
                    created: new Date(),
                    role: "admin"
            ).save(flush: true, failOnError: true)
        }
        log.debug('after getting system username')
        log.debug('in bootstrap init func')
        def trees = [this.acaciaTree(), this.amphibianTree(), this.mammalsTree(),
                     this.marsupialsTree(), this.maluridaeTree(), hornwortsTree()]
        trees.each {
            def tree = it
            tree.owner = systemUser;
            def pt = Tree.findByReference(tree.reference)
            if (!pt) {
                log.debug('adding tree' + tree['title'])
                tree['nexson'] = opentreeService.convertNewickToNexson(tree['tree']).toString();
                pt = new Tree(tree).save(flush: true, failOnError: true);
            } else if (Boolean.parseBoolean(grailsApplication.config.bootstrap.overwrite)) {
                tree['nexson'] = opentreeService.convertNewickToNexson(tree['tree']).toString();
                tree.each { key, value ->
                    pt[key] = value;
                }
                pt.save(flush: true);
            } else {
                log.debug('tree already in database')
            }
        }

        def chars = listCharacters()
        chars.each {
            def c = it
            c.owner = systemUser;
            def pt = Characters.findByDrid(c.drid)
            if (!pt) {
                log.debug('adding character' + c['title'])
                pt = new Characters(c).save(flush: true, failOnError: true);
            } else {
                log.debug('Character already in database');
            }
        }

        //create demo
        utilsService.guestAccount();
        this.createDemo();
    }

    def destroy = {
    }

    def acaciaTree() {
        log.debug('loading acacia tree')
        def result = [:], filename = 'acacia.newick'
        def file = grailsApplication.mainContext.getResource('artifacts/' + filename).file;
        result['tree'] = file.text
        result['year'] = 2011
        result['hide'] = false
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['reference'] = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. ' +
                'Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        result['title'] = 'Acacia – Miller et al 2012'
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Acacia'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:apni.taxon:295861'
        result['treeFormat'] = 'newick'
        result['created'] = new Date()
        return result
    }

    def mammalsTree() {
        log.debug('adding mammals tree to map object')
        def result = [:], filename = 'mammals.newick'
        def file = grailsApplication.mainContext.getResource('artifacts/' + filename).file;
        result['tree'] = file.text
        log.debug('mammals tree' + result['tree'])
        result['treeFormat'] = 'newick'
        log.debug(result['nexson'])
        result['year'] = 2009
        result['hide'] = false
        result['doi'] = ''
        result['title'] = 'Mammals – Fritz et al 2009'
        result['reference'] = 'Fritz, S.A., Bininda-Emonds, O.R.P. & Purvis, A. (2009) Geographical variation in' +
                ' predictors of mammalian extinction risk: Big is bad, but only in the tropics. Ecology' +
                ' Letters, 12, 538-549'
        result['created'] = new Date()
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Mammals'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:afd.taxon:e9e7db31-04df-41fb-bd8d-e0b0f3c332d6'
        return result
    }

    def amphibianTree() {
        log.debug('loading amphibians tree')
        def result = [:], filename = 'amp.newick'
        def file = grailsApplication.mainContext.getResource('artifacts/' + filename).file;
        result['tree'] = file.text
        log.debug('amphibians tree' + result['tree'])
        result['treeFormat'] = 'newick'
        result['year'] = 2011
        result['hide'] = false
        result['doi'] = ''
        result['title'] = 'Amphibians (global) – Pyron & Wiens 2011'
        result['reference'] = 'Pyron R.A., & Wiens J.J. 2011. A large-scale phylogeny of Amphibia including over ' +
                '2800 species, and a revised classification of extant frogs, salamanders, and caecilians. Molecular' +
                ' Phylogenetics and Evolution 61: 543-583.'
        result['created'] = new Date()
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Amphibians'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:afd.taxon:0490a9ba-0d08-473d-a709-6c42e354f118'
        return result
    }

    def marsupialsTree() {
        log.debug('creating marsupials tree metadata')
        def result = [:]
        def file = grailsApplication.mainContext.getResource('artifacts/marsupials.newick').file;
        result['tree'] = file.text
        result['treeFormat'] = 'newick'
        log.debug(result['nexson'])
        result['year'] = 2004
        result['hide'] = false
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1017/S0952836904005539/full'
        result['title'] = 'Marsupials – Cardillo et al 2004'
        result['reference'] = 'Cardillo, M., Bininda-Emonds, R. P., Boakes, E., & Purvis, A. (2004).  ' +
                'A species-level phylogenetic supertree of marsupials.  Journal of Zoology, 264(1), 11–31.'
        result['created'] = new Date()
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Marsupials'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:afd.taxon:602afec4-950f-4961-9ad0-74793be8efbf'
        result['notes'] = 'Source data from treebase/phylows/study/TB2:S1128'
        return result
    }

    def hornwortsTree() {
        log.debug('creating Australian Hornworts tree metadata')
        def result = [:]
        def file = grailsApplication.mainContext.getResource('artifacts/hornworts.newick').file;
        result['tree'] = file.text
        result['treeFormat'] = 'newick'
        log.debug(result['nexson'])
        result['year'] = 2013;
        result['hide'] = false
        result['doi'] = 'http://dx.doi.org/10.1071/SB13030';
        result['title'] = 'Australian Hornworts'
        result['reference'] = 'Cargill D. Christine, Vella Nicole G. F., Sharma Ish, Miller Joseph T. (2013) Cryptic speciation and species diversity among Australian and New Zealand hornwort taxa of Megaceros (Dendrocerotaceae). Australian Systematic Botany 26, 356–377.';
        result['created'] = new Date()
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Hornworts'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:apni.taxon:303372'
        result['notes'] = ''
        return result
    }

    def maluridaeTree() {
        log.debug('creating Maluridae tree metadata')
        def result = [:]
        def file = grailsApplication.mainContext.getResource('artifacts/maluridae.newick').file;
        result['tree'] = file.text
        result['treeFormat'] = 'newick'
        result['year'] = 2012
        result['hide'] = false
        result['doi'] = 'http://doi.org/10.1093/sysbio/syr101'
        result['title'] = 'Maluridae - Lee et al 2012'
        result['reference'] = 'Lee JY, Joseph L, Edwards S. 2012. A species tree for the Australo-Papuan' +
                ' fairy-wrens an allies (Aves: Maluridae). Syst. Biol. 61(2): 253-271.'
        result['created'] = new Date()
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Maluridae'
        result['expertTreeLSID'] = 'urn:lsid:biodiversity.org.au:afd.taxon:e0c1a995-d168-468f-8270-8299043212c5'
        result['notes'] = 'Tree provided by Scott Edwards sedwards@fas.harvard.edu'
        return result
    }

    def listCharacters() {
        return [[
                        drid : 'dr2116',
                        title: 'Acacia Characters'
                ], [
                        drid : 'dr2324',
                        title: 'Mammal traits'
                ]]
    }

    def createDemo() {
        def guest = Owner.findByDisplayName("Guest");
        def tree = Tree.findByTitle('Acacia – Miller et al 2012');
        if (!tree || !guest) {
            return;
        }

        def demo = Phylo.findByTitle('Phylolink Demo');
        if (!demo) {
            demo = new Phylo([
                    title     : 'Phylolink Demo',
                    characters: '{"listLoading":false,"selectedCharacter":null,"count":3,"events":["statechange","moved","edited","newchar","removed"],"edit":true,"newChar":true,"list":{"id":5,"title":"Acacia Characters","listurl":"http://lists.ala.org.au/speciesListItem/list/dr2116","url":"/ala/getCharJson?drid=dr2116"},"_callbacks":{"statechange":[null,null],"newchar":[null]},"lists":[{"id":5,"title":"Acacia Characters","listurl":"http://lists.ala.org.au/speciesListItem/list/dr2116","url":"/ala/getCharJson?drid=dr2116"}],"characters":[{"id":"charChart-2","name":"Inflorescence_arrangement"},{"id":"charChart-1","name":"annual_mean_rad"}]}',
                    habitat   : '{"habitats":[{"id":"habitat-1","yAxis":"Occurrence count","name":"el790","xAxis":"%","displayName":"WorldClim: Temperature - isothermality"},{"id":"habitat-2","yAxis":"Occurrence count","name":"el1010","xAxis":"degrees C","displayName":"2030A1BMk35M: Temperature - coldest month min"}],"count":3,"newChar":true,"selectedHabitat":{"id":"habitat-2","yAxis":"Occurrence count","name":"el1010","xAxis":"degrees C","displayName":"2030A1BMk35M: Temperature - coldest month min"},"_callbacks":{"moved":[null],"removed":[null],"changed":[null,null]}}',
                    owner     : guest,
                    studyid   : tree.getId().toString(),
                    treeid    : '1',
                    viz       : ['viz': 'PhyloJive']
            ]).save(flush: true, failOnError: true);
        }
        return demo
    }
}
