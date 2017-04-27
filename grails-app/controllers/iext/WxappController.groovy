package iext

import groovy.sql.Sql
import grails.converters.*
import groovy.json.*

class WxappController {

    def dataSource


    def index() { }



    def hello() {
        println params
        render "wxywxy!"
    }

    def check_bind() {
        def result = [:]
        def ab = AppBind.findByOpenid(params.openid)
        if (ab) {
            result.status = 'ok'
            result.jobNo = ab.jobNo
        } else {
            result.status = 'fail'
        }
        render result as JSON
    }

    def signin() {
        def result = [:]
        def sql = new Sql(dataSource)
        def s = """
            select Code, Name from txcard..ZlEmployee where Code = :jno and right(rtrim(Sfz), 4) = :pw
        """
        def r = sql.firstRow(s, [params])
        if (r) {
            result.jobNo = r.code?.trim()
            result.name = r.name?.trim()
            
            def ab = AppBind.findByOpenid(params.openid)
            if (!ab) {
                ab = new AppBind()
                ab.openid = params.openid
            }
            ab.jobNo = result.jobNo
            ab.save()
        }
        render result as JSON
    }

    def signout() {
        def result = [:]
        def ab = AppBind.findByOpenid(params.openid)
        if (ab) {
            ab.delete()
        }
        result.status = 'ok'
        render result as JSON
    }
}
