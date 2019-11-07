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
        "/phylo/wizard"(controller: 'phylo',action:'wizard') // this action isn't defined in the phyloController??
        "/ws/chart/stackedBar"(controller: 'chart',action:'stackedBar')
        "/ws/listStudies"(controller: 'OTStudy',action:'listStudies')
        "/ws/searchDoi(.$format)?"(controller: "tree", action: 'searchDoi')
        "/ws/leafNodes"(controller: "OTStudy", action: 'leafNodes')
        "/ws/treeInfo(.$format)?"(controller: "tree", action: 'treeInfo')
        "/tree/visualize/$id/$treeId?"(controller: 'tree', action: 'visualize')
        "/tree/mapOtus/$id(.$format)?"(controller: 'tree', action: 'mapOtus')
        "/tree/taxonInfo/$q"(controller: 'tree', action: 'taxonInfo')
        "/"(controller: 'phylo', action: 'startPage')
        "500"(view:'/error')
        "400"(view:'/error')
        "403"(view:'/notAuthorised')
        "401"(view:'/notAuthorised')
        "404"(view:'/notFound')
        "/error" (view:'error')
	}
}