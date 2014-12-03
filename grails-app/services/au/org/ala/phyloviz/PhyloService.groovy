package au.org.ala.phyloviz

import grails.transaction.Transactional

@Transactional
class PhyloService {

    def createVisualization( studyId, treeId ) {
        def viz = new Phylo( [
                studyid: studyId,
                treeid: treeId,
                "viz": ['viz':'PhyloJive']
        ])
        viz.save( flush: true )
        return viz
    }
}
