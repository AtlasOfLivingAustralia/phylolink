package au.org.ala.phyloviz

import grails.transaction.Transactional

@Transactional
class PhyloService {

    def createVisualization( studyId, treeId, owner ) {
        def viz = new Phylo( [
                studyid: studyId,
                treeid: treeId,
                "viz": ['viz':'PhyloJive'],
                owner: owner
        ])
        viz.save( flush: true )
        return viz
    }
}
