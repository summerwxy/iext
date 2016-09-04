package iext

class IndexController {

    def index() { 
        render "Hello, World!"
        // redirect(uri: '/static/extjs/index.html')
    }

    def home() {
        redirect(uri: '/static/extjs/index.html')
    }
}
