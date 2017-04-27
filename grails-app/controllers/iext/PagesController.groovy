package iext

import com.google.gson.JsonObject
import groovy.json.JsonSlurper
import groovy.sql.Sql
import grails.converters.*
import groovy.json.*

class PagesController {

    def wxmp2
    def dataSource



    def index() { }


    def sync_store() {
        // TODO: delete all data in PagesStores
        def slurper = new JsonSlurper()
        def begin = 0
        def exit = false
        while (exit == false) {
            def param = new JsonObject()
            param.addProperty("begin", begin)
            param.addProperty("limit", 50)
            def str = wxmp2.service.post("https://api.weixin.qq.com/cgi-bin/poi/getpoilist", param.toString())
            def json = slurper.parseText(str)
            def stores = json.business_list
            begin += stores.size()
            exit = (stores.size() == 0)
            stores.each { it ->
                def s = it.base_info
                def ps = new PagesStores()
                ps.sid = s.sid
                ps.businessName = s.business_name
                ps.branchName = s.branch_name
                ps.address = s.address
                ps.telephone = s.telephone
                ps.categories = s.categories.join(", ")
                ps.city = s.city
	            ps.province = s.province
                ps.offsetType = s.offset_type as long
                ps.longitude = s.longitude as double
                ps.latitude = s.latitude as double
                ps.photo1 = (s.photo_list.size() > 0) ? s.photo_list[0].photo_url : ""
                ps.photo2 = (s.photo_list.size() > 1) ? s.photo_list[1].photo_url : ""
                ps.photo3 = (s.photo_list.size() > 2) ? s.photo_list[2].photo_url : ""
                ps.photo4 = (s.photo_list.size() > 3) ? s.photo_list[3].photo_url : ""
                ps.photo5 = (s.photo_list.size() > 4) ? s.photo_list[4].photo_url : ""
                ps.photo6 = (s.photo_list.size() > 5) ? s.photo_list[5].photo_url : ""
                ps.introduction = s.introduction
                ps.recommend = s.recommend
                ps.special = s.special
                ps.openTime = s.open_time
                ps.avgPrice = s.avg_price as double
                ps.poiId = s.poi_id as long
                ps.availableState = s.available_state as long
                ps.district = s.district
                ps.updateStatus = s.update_status as long
                ps.save(failOnError: true)
            }
        }
        render "同步微信门店资料成功！！" 
    }

    def weui() {  render "file too large can't pack to war file"  }


    def store() {
    }

    def getStore() {
        // default location company
        def lat = params?.lat ?: 31.2526760101
        def lng = params?.lng ?: 120.611793518

        def sql = new Sql(dataSource)
        def s = """
            SELECT (6371 * acos(cos(radians(:lat))
             * cos(radians(latitude))
             * cos(radians(longitude) - radians(:lng))
             + sin(radians(:lat))
             * sin(radians(latitude)))) AS distance 
             , *
            FROM pages_stores 
            ORDER BY distance
        """
        def stores = []
        sql.eachRow(s, [lat: lat, lng: lng]) {
            def foo = it.toRowResult()
            foo.dist = (it.distance < 5) ? (it.distance * 1000).toLong().toString() + ' m' : it.distance.round(1).toString() + ' km'
            if (!params?.lat) {
                foo.dist = 'NA'
            }
            stores << foo
        } 

        render stores as JSON
    }

}
