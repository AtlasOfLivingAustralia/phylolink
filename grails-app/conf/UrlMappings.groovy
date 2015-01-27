class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/phylo/create/$studyid/$treeid/$index"(controller: 'phylo',action:'create')
        "/phylo/getTree.json"(controller: 'phylo',action:'getTree')
        "/phylo/getTree"(controller: 'viewer',action:'show')
        "/phylo/wizard"(controller: 'phylo',action:'wizard')
        "/ws/listStudies"(controller: 'OTStudy',action:'listStudies')
        "/ws/searchDoi(.$format)?"(controller: "PhylogeneticTree", action: 'searchDoi')
        "/ws/leafNodes"(controller: "OTStudy", action: 'leafNodes')
        "/ws/treeInfo(.$format)?"(controller: "PhylogeneticTree", action: 'treeInfo')
        "/tree/visualize/$id/$treeId?"(controller: 'Tree', action: 'visualize')
        "/tree/mapOtus/$id(.$format)?"(controller: 'Tree', action: 'mapOtus')
        "/tree/taxonInfo/$q"(controller: 'Tree', action: 'taxonInfo')
        "/"(view:"/index")
        "500"(view:'/error')
	}
}