package iext


class AuthInterceptor {

    AuthInterceptor() {
        matchAll().excludes(controller:"test")
    }

    boolean before() { 
        println 'check auth'

        return true
    }

    boolean after() { 
        true 
    }

    void afterView() {
        // no-op
    }
}
