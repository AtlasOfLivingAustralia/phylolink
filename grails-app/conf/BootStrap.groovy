import au.org.ala.phyloviz.Owner
import au.org.ala.phyloviz.Tree
import au.org.ala.phyloviz.Owner

class BootStrap {
    def opentreeService
    def skip = true
//    def elasticService

    def init = { servletContext ->
        if(skip){
            return
        }
        log.debug('checking for system user name')
        def systemUser = Owner.findByDisplayName("System")
        if (!systemUser) {
            systemUser = new Owner(
                    userId: 1,
                    displayName: "System",
                    email: "phylojive@ala.org.au",
                    created: new Date(),
                    role:"admin"
            ).save(flush: true, failOnError: true)
        }
        log.debug('after getting system username')
        log.debug( 'in bootstrap init func')
        def trees = [ this.acaciaTree(), this.amphibianTree(), this.mammalsTree()]
        trees.each{
            def tree = it
            tree.owner = systemUser;
            def pt = Tree.findByReference(tree.reference)
            if (!pt) {
                log.debug( 'adding tree' +  tree['title'] )
                pt = new Tree(tree).save(flush: true, failOnError: true);
            } else {
                log.debug( 'tree already in database' )
            }
        }

    }

    def destroy = {
    }

    def acaciaTree() {
        log.debug('loading acacia tree')
        def result = [:], filename = 'acacia.nexml'
        def file = new File('artifacts/' + filename)
        result['tree'] = file.text
        result['nexson'] = opentreeService.convertNexmlToNexson(result.tree)
        result['nexson'] = result['nexson'].toString();
        result['year'] = '2011'
        result['hide'] = false
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['reference'] = 'Miller, J. T., Murphy, D. J., Brown, G. K., Richardson, D. M. and González-Orozco,' +
                ' C. E. (2011), The evolution and phylogenetic placement of invasive Australian Acacia species. ' +
                'Diversity and Distributions, 17: 848–860. doi: 10.1111/j.1472-4642.2011.00780.x'
        result['title'] = 'Acacia – Miller et al 2012'
        result['expertTree'] = true
        result['expertTreeTaxonomy'] = 'Acacia'
        result['expertTreeLSID']= 'urn:lsid:biodiversity.org.au:apni.taxon:295861'
        result['treeFormat'] = 'nexml'
        result['created'] = new Date()
        return result
    }

    def mammalsTree(){
        log.debug('adding mammals tree to map object')
        def result = [:], filename = 'mammals.nexml'
        def file = new File('artifacts/' + filename)
        result['tree'] = file.text
        log.debug('mammals tree' + result['tree'])
        result['treeFormat'] = 'nexml'
        result['nexson'] = opentreeService.convertNexmlToNexson(result.tree)
        result['nexson'] = result['nexson'].toString();
        log.debug(result['nexson'])
        result['year'] = '2009'
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

    def amphibianTree(){
        log.debug('loading amphibians tree')
        def result = [:], filename = 'amp.nexml'
        def file = new File('artifacts/' + filename)
        result['tree'] = file.text
        log.debug('amphibians tree' + result['tree'])
        result['treeFormat'] = 'nexml'
        result['nexson'] = opentreeService.convertNexmlToNexson(result.tree)
        result['nexson'] = result['nexson'].toString();
        result['year'] = '2011'
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

    def loadNexmlFile2() {
        def result = [:], filename = 'ot_13.nexml.txt'
        def file = new File('artifacts/' + filename)
        result['tree'] = file.text
        result['nexson'] = new File('artifacts/ot_13.json.1.2.1.json').text
        result['year'] = '2014'
        result['hide'] = false
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['reference'] = 'Riginos C. 2014. Dispersal capacity predicts both population genetic structure' +
                ' and species richness in reef fishes. The American Naturalist, 184.'
        result['title'] = 'Dispersal capacity predicts both population genetic structure and species richness in reef fishes'
        result['expertTree'] = false
        result['treeFormat'] = 'nexml'
        result['created'] = new Date()
        return result
    }

    def loadNexmlFile3(){
        def result = [:], filename = 'ot_14.nexml.json'
        def file = new File( 'artifacts/' + filename )
        result['tree'] = file.text
        result['nexson'] = new File( 'artifacts/ot_14.json.1.2.1.json' ).text
        result['year'] = '2014'
        result['hide'] = false
        result['doi'] = 'http://onlinelibrary.wiley.com/doi/10.1111/j.1472-4642.2011.00780.x/full'
        result['reference'] = 'Riginos C. 2014. Dispersal capacity predicts both population genetic structure' +
                ' and species richness in reef fishes. The American Naturalist, 184.'
        result['title'] = 'Dispersal capacity predicts both population genetic structure and species richness in reef fishes'
        result['treeFormat'] = 'nexml'
        result['expertTree'] = false
        result['created'] = new Date()
        return result
    }
}
