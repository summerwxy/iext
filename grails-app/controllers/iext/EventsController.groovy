package iext

import me.chanjar.weixin.common.api.*
import groovy.sql.Sql
import grails.converters.*
import groovy.json.*

class EventsController {

    def wxmp
    def dataSource

    def index() {
        render "hello"
    }

    def mooncake_weight = [
        '90150022': 900, // 2015爱维尔沁意礼盒
        '90150023': 900, // 2015秋之礼礼盒
        '90150024': 1200, // 2015八月十五礼盒
        '90150025': 1300, // 2015爱维尔乐活礼盒
        '90150026': 1400, // 2015谢礼礼盒
        '90150027': 1600, // 2015甜美生活礼盒
        '90150028': 2000, // 2015爱维尔珍馔礼盒
        '90150029': 2300, // 2015爱维尔金矿礼盒
        '90150030': 3200, // 2015大红礼盒
        '90150032': 900, // 2015宝岛印象礼盒
        '90150033': 1220, // 2015茶梅酥礼盒
        '90150034': 1110, // 2015凤梨酥礼盒
        '90150035': 1320, // 2015台北印象礼盒
        '90150037': 2000, // 2015蛋黄酥礼盒
        '90150038': 1800, // 2015台湾绿豆椪礼盒
        '90150039': 1800, // 2015台湾三宝礼盒
        '90150041': 3300, // 2015舌尖上的台湾礼盒            
    ]

    def exc() {
        // ===== weixin oauth2 =====
        if (params.code) {
            def token = wxmp.service.oauth2getAccessToken(params.code)
            def openid = token.openId
            session.openid = openid
        }
        if (!session.openid) {
            def url = _.wxOauth2Url('events', 'exc')
            url += "?showwxpaytitle=1&vid=" + params.vid
            url = wxmp.service.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_BASE, null)
            redirect(url: url) 
            return
        }
        // =========================

        // 傳出去的變數
        def h = null
        def ds = []
        def lv1s = []
        def fee = [:]
        def express = []            

        if (params.vid) { // 掃碼過來的
            def sql = new Sql(dataSource)
            def s = """select a.GI_P_NO, b.* from GIFT_TOKEN a left join express_charge_body b on a.vid = b.vid where a.vid = ?"""
            def row = sql.firstRow(s, [params.vid])
            // 找單頭數據
            if (!row) {
                render '非法访问！'
                return
            } else if (!row.h_id) {
                s = """
                    insert express_charge_head(version, address, date_created, express_no, last_updated, name, phone, status, fee, kg, lv1, lv2, lv3, lat, lng) values(0, '', GETDATE(), '', GETDATE(), '', '', '', 0, 0, '', '', '', 0, 0)
                    select * from express_charge_head where id = @@IDENTITY
                """
                h = sql.firstRow(s, [])
                s = "insert express_charge_body values(0, GETDATE(), ?, GETDATE(), ?)"
                sql.execute(s, [h.id, params.vid])
                redirect(action: 'exc', params: ['showwxpaytitle': '1', 'vid': params.vid]) // 解決第一次掃描, 沒有單身問題
            } else if (row.h_id) {
                s = "select * from express_charge_head where id = ?"
                h = sql.firstRow(s, [row.h_id])
            } 
            // 找單身數據
            s = """
                select a.GT_NO, a.VCODE, a.VID, c.P_NO, c.P_NAME
                from GIFT_TOKEN a
                left join express_charge_body b on a.VID = b.vid
                left join part c on a.GI_P_NO = c.P_NO
                where b.h_id = ?
            """
            sql.eachRow(s, [row.h_id]) {
                def foo = it.toRowResult()
                foo['weight'] = mooncake_weight[it.P_NO] ?: 0
                ds << foo
            }
            // 省
            s = "select distinct lv1 from zone order by lv1"
            sql.eachRow(s, []) {
                lv1s << it.toRowResult()
            }
            // fee
            s = "select distinct first, additional, lv1 from zone"
            sql.eachRow(s, []) {
                fee[it.lv1] = it.toRowResult()
            }
            // express
            if (h.express_no) {
                def foo = _.parseJson("http://v.juhe.cn/exp/index?key=b7f2944ba8eef30883de8eb21830bb6f&com=sf&no=${h.express_no}")
                if (foo.error_code == 0) {
                    express = foo.result.list
                }
            }

        }

        println h
        [h: h, ds: ds, lv1s: lv1s, fee: fee as JSON, express: express]    
    }







    // TODO: 檢查有沒有被掃過券了
    // 付费快递版本 
    def mooncake2() {
        /*
        def h = null
        def ds = []
        def lv1s = []
        def fee = [:]
        def express = []

        if (params.vid) { // 扫码来的
            def sql = _.sql
            def s = """
                select a.vid, b.h_id 
                from GIFT_TOKEN a
                left join mooncake2expressd b on a.vid = b.vid
                where a.vid = ? 
            """
            def row = sql.firstRow(s, [params.vid])
            if (!row) {
                render '无效访问！'
                return
            } else if (!row.h_id) {
                s = """
                    insert mooncake2expressh(version, address, date_created, express_no, last_updated, name, phone, status, fee, kg, lv1, lv2, lv3, lat, lng) values(0, '', GETDATE(), '', GETDATE(), '', '', '', 0, 0, '', '', '', 0, 0)
                    select * from mooncake2expressh where id = @@IDENTITY
                """
                h = sql.firstRow(s, [])
                s = "insert mooncake2expressd values(0, GETDATE(), ?, GETDATE(), ?)"
                sql.execute(s, [h.id, params.vid])
                // 解决第一次扫描没单身问题
                redirect(action: 'mooncake2', params: ['showwxpaytitle': '1', 'vid': params.vid])
            } else if (row.h_id) {
                s = "select * from mooncake2expressh where id = ?"
                h = sql.firstRow(s, [row.h_id])
            }
            s = """
                select a.GT_NO, a.VCODE, a.VID, c.P_NO, c.P_NAME
                from GIFT_TOKEN a
                left join mooncake2expressd b on a.VID = b.vid
                left join part c on a.GI_P_NO = c.P_NO
                where b.h_id = ?
            """
            sql.eachRow(s, [row.h_id]) {
                def foo = it.toRowResult()
                foo['weight'] = mooncake_weight[it.P_NO] ?: 0
                ds << foo
            }
            // 省
            s = "select distinct lv1 from zone order by lv1"
            sql.eachRow(s, []) {
                lv1s << it.toRowResult()
            }
            // fee
            s = "select distinct first, additional, lv1 from zone"
            sql.eachRow(s, []) {
                fee[it.lv1] = it.toRowResult()
            }
            // express
            if (h.express_no) {
                def foo = _.parseJson("http://v.juhe.cn/exp/index?key=b7f2944ba8eef30883de8eb21830bb6f&com=sf&no=${h.express_no}")
                if (foo.error_code == 0) {
                    express = foo.result.list
                }
            }
        } else if (params.act == 'pay') {
            h = Mooncake2ExpressH.get(params.hid)
            if (h.status == 'unpaid') {
                def h2del = h
                h = new Mooncake2ExpressH()
                h.name = ''
                h.phone = ''
                h.address = ''
                h.lv1 = ''
                h.lv2 = ''
                h.lv3 = ''
                h.kg = 0
                h.fee = 0
                h.expressNo = ''
                h.status = ''
                h.save()
                Mooncake2ExpressD.findAllByH(h2del).each {
                    it.h = h
                    it.save()
                }
                h2del.status = 'delete'
                h2del.save()
            }
            h.name = params.name
            h.phone = params.phone
            h.lv1 = params.lv1
            h.lv2 = params.lv2
            h.lv3 = params.lv3
            h.address = params.address
            h.kg = params.kg.toFloat()
            h.fee = params.fee.toFloat()
            h.status = 'unpaid'
            h.save(flush: true)

            def appid = _.wxMpAppId
            def mchid = _.wxMpMchId
            def key = _.wxMpMchKey

            Unifiedorder unifiedorder = new Unifiedorder();
            unifiedorder.setAppid(appid); 
            unifiedorder.setMch_id(mchid); 
            unifiedorder.setNonce_str(UUID.randomUUID().toString().toString().replace("-", ""));
            unifiedorder.setOpenid(session.openid);
            unifiedorder.setBody("爱维尔中秋礼盒(" + params.kg + ")");
            unifiedorder.setOut_trade_no("IWILL_SF_" + h.id.toString().padLeft(10, "0"));
            def tf = params.fee.toInteger() * 100
            if (_.dev()) { // 开发时除 100
                tf = params.fee.toInteger()
            }
            unifiedorder.setTotal_fee(tf.toString()); //单位分
            unifiedorder.setSpbill_create_ip(request.getRemoteAddr());//IP
            if (_.dev()) {
                unifiedorder.setNotify_url("http://test.dsiwill.com/imis/market/mooncake2_notify"); 
            } else {
                unifiedorder.setNotify_url("http://api.dsiwill.com/imis/market/mooncake2_notify"); 
            }
            unifiedorder.setTrade_type("JSAPI");//JSAPI，NATIVE，APP，WAP
            // 统一下单，生成预支付订单
            UnifiedorderResult unifiedorderResult = PayMchAPI.payUnifiedorder(unifiedorder,key);

            flash.json = PayUtil.generateMchPayJsRequestJson(unifiedorderResult.getPrepay_id(), appid, key);
            redirect(action: 'mooncake2_pay', params: ['showwxpaytitle': '1'])
            return
        } else if (!params.vid && !params.code && !flash.vid) {
            render '无效访问！！' // AD page ?
            return
        } 

        [h: h, ds: ds, lv1s: lv1s, fee: fee as JSON, express: express]    
            */
    }

    def mooncake2_zone() {
        /*
        def result = []
        def sql = _.sql    
        def s = "select distinct ${params.clv} as name from zone where ${params.plv} = ? order by ${params.clv}"
        sql.eachRow(s, [params.val]) {
            result << it.toRowResult()
        }
        render (contentType: 'text/json') {result}
        */
    }

    def mooncake2_pay() {
        /*
        def json = flash.json
        println json // 看看什么原因没跳出付款界面
        [json: json]
        */
    }

    def mooncake2_del() {
        /*
        def foo = Mooncake2ExpressD.findByVid(params.vid)
        foo.delete()
        render (contentType: 'text/json') {[msg: 'okay']}
        */
    }

    // private static ExpireSet<String> expireSet = new ExpireSet<String>(60);
    def mooncake2_notify() {
        /*
        def key = _.wxMpMchKey
        //def key = "b84b9bb08bd8f064fab58420c7d304bb"

        // 获取请求数据
        MchPayNotify payNotify = XMLConverUtil.convertToObject(MchPayNotify.class, request.getInputStream());

        // 已处理 去重
        if(expireSet.contains(payNotify.getTransaction_id())){
            return;
        }
        // 签名验证
        if(SignatureUtil.validateAppSignature(payNotify,key)){
            // update
            def id = payNotify.out_trade_no[9..18].toInteger()
            def h = Mooncake2ExpressH.get(id)
            h.status = 'paid'
            h.save(flush: true)
            
            expireSet.add(payNotify.getTransaction_id());
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("SUCCESS");
            baseResult.setReturn_msg("OK");
            render XMLConverUtil.convertToXML(baseResult)
        }else{
            MchBaseResult baseResult = new MchBaseResult();
            baseResult.setReturn_code("FAIL");
            baseResult.setReturn_msg("ERROR");
            render XMLConverUtil.convertToXML(baseResult)
        }            
        */
    }

    // TODO: 檢查券有沒有被掃
    def mooncake2_check() {
        /*
        def msg = ''
        def name = ''
        def tno = params.tno?.trim()
        def vcode = params.vcode?.trim().toUpperCase()
        def vid = ''
        def weight = 0
        def sql = _.sql
        def s = """
            select c.P_NO, c.P_NAME, a.GT_NO, a.VCODE, a.VID, b.id
            from GIFT_TOKEN a
            left join mooncake2expressd b on a.VID = b.VID
            left join part c on a.GI_P_NO = c.P_NO
            where GT_NO = ? and VCODE = ?
        """
        def row = sql.firstRow(s, [tno, vcode])
        if (!row) {
            msg = '券号或验证码错误！'
        }
        if (row && row.id) {
            msg = '此券号已被使用过！'
        }
        if (!msg) {
            def h = Mooncake2ExpressH.get(params.hid)
            def d = new Mooncake2ExpressD()
            d.vid = row.VID?.trim()
            d.h = h
            d.save()
            name = row.P_NAME?.trim()
            vid = row.VID?.trim()
            weight = mooncake_weight[row.P_NO?.trim()]
        }
        render (contentType: 'text/json') {[name: name, tno: tno, vcode: vcode, vid: vid, msg: msg, weight: weight]}
        */
    }




}
