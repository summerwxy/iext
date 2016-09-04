package iext

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }


        "/"(controller: 'index', action: 'index')
        "/home"(controller: 'index', action: 'home')
        "/default_index"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
