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
        "/ws/listStudies"(controller: 'OTStudy',action:'listStudies')
        "/ws/searchDoi(.$format)?"(controller: "PhylogeneticTree", action: 'searchDoi')
        "/ws/leafNodes"(controller: "OTStudy", action: 'leafNodes')
        "/ws/treeInfo(.$format)?"(controller: "PhylogeneticTree", action: 'treeInfo')
        "/"(view:"/index")
        "500"(view:'/error')
	}
}