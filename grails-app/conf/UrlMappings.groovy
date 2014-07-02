class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/phylo/create/$studyid/$treeid/$index"(controller: 'phylo',action:'create')
        "/"(view:"/index")
        "500"(view:'/error')
	}
}
