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
        if(!viz.hasErrors()){
            viz.setTitle('My viz #'+viz.getId())
            viz.save(flush: true);
        }
        return viz
    }

    def getDemoId(){
        def demo = Phylo.findByTitle('Phylolink Demo');
        if(demo){
            return demo.getId();
        }
    }
}
