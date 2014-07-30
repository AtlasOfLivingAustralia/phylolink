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
        "/ws/listStudies"(controller: 'study',action:'listStudies')
        "/"(view:"/index")
        "500"(view:'/error')
	}
}