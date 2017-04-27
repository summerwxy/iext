package iext

import me.chanjar.weixin.common.api.*
import groovy.sql.Sql
import grails.converters.*
import groovy.json.*
import org.apache.commons.io.IOUtils
import me.chanjar.weixin.common.util.crypto.WxCryptUtil
import me.chanjar.weixin.common.util.*
import me.chanjar.weixin.mp.bean.*
import me.chanjar.weixin.mp.bean.pay.request.*

class EventsController {

    def wxmp
    def dataSource

    def index() {
        // 非法請求
        if (!wxmp.service.checkSignature(params.timestamp, params.nonce, params.signature)) {
            println 'invalid request'
            render 'invalid request' 
            return
        }
        // 設定微信服務器專用
        if (StringUtils.isNotBlank(params.echostr)) {
            println params.echostr
            render params.echostr 
            return
        }
        // 加密方式
        def encryptType = StringUtils.isBlank(params.encrypt_type) ? "raw" : params.encrypt_type

        // TODO: 明文還沒測試 加密的沒問題!!
        if (encryptType == "raw") { // 明文
            def inMessage = WxMpXmlMessage.fromXml(request.getInputStream())
            def outMessage = wxmp.router.route(inMessage)
            render outMessage ? outMessage.toXml() : ''
        } else if (encryptType == "aes") { // AES 加密
            def inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxmp.config, params.timestamp, params.nonce, params.msg_signature)
            def outMessage = wxmp.router.route(inMessage)
            render outMessage ? outMessage?.toEncryptedXml(wxmp.config) : ''
        } else {
           render "unknow encrypt type" 
        }
    }




    def actno = '2017_dragon_boat'

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

        '90160029': 500, // 2016爱维尔沁意
        '90160030': 700, // 2016情洒中秋
        '90160031': 800, // 2016秋戏
        '90160032': 1000, // 2016秋之礼
        '90160033': 1100, // 2016幸福时光
        '90160034': 1100, // 2016福礼
        '90160035': 1400, // 2016爱尚慢生活
        '90160036': 1300, // 2016谢礼
        '90160037': 1600, // 2016甜美生活
        '90160038': 2100, // 2016秋意
        '90160039': 2500, // 2016中国味
        '90160040': 3200, // 2016典藏精选
        '90160041': 3600, // 2016礼赞
        '90160042': 1000, // 2016宝岛印象
        '90160043': 1500, // 2016茶梅酥
        '90160044': 1100, // 2016凤梨酥
        '90160045': 10, // 2016冰雪物语
        '90160046': 1500, // 2016法式风情
        '90160047': 1300, // 2016台北印象
        '90160048': 1900, // 2016蛋黄酥
        '90160049': 1900, // 2016台湾绿豆椪
        '90160050': 1900, // 2016台湾三宝
        '90160051': 10, // 2016雪绵娘
        '90160052': 2800, // 2016舌尖上的台湾


        '16360173': 790, // 万事如意（129型）礼盒
        '16360174': 595, // 吉祥如意（79型）礼盒
        '16360178': 970, // 17圣维尼礼盒
        '16360197': 1030, // 17过年好礼盒
        '16360198': 565, // 17福到礼盒
        '16360199': 320, // 法式牛轧糖A
        '16360200': 320, // 法式牛轧糖B

        '90170001': 0, // 17粽香礼盒
        '90170002': 930, // 17粽意礼盒
        '90170003': 1420, // 17粽爱礼盒
        '90170004': 1950, // 17粽情礼盒
        '90170005': 1400, // 17素食粽礼盒
        '90170006': 2940, // 17粽礼礼盒
    ]

    // TODO: 2017_spring 還是有支付問題, 待解決
    // TODO: only this event allow
    def exc() {
        // ===== weixin oauth2 =====
        if (params.test == 'wxy') {
            session.openid = 'wxy' // for test. 
        }
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
        def isThisActno = true
        def firstTicket = null

        def sql = null
        def s = null
        def row = null

        // 掃碼過來的
        if (params.vid) { 
            sql = new Sql(dataSource)
            s = """select a.GI_P_NO, a.ACTNO, a.GT_NO, b.* from GIFT_TOKEN a left join express_charge_body b on a.vid = b.vid where a.vid = ?"""
            row = sql.firstRow(s, [params.vid])
        }
        
        if (params.vid && !row) { // 不存在的券
            render 'invalid access!'
            return
        } else if (params.vid && row.ACTNO != actno) { // 非本次的活動
            isThisActno = false
        } else if (params.vid && !row.h_id) { // 第一次掃碼, 一定是沒有單頭, 先新增再把號碼查出來
            s = """
                insert express_charge_head(version, address, date_created, express_no, last_updated, name, phone, status, fee, kg, lv1, lv2, lv3, lat, lng) values(0, '', GETDATE(), '', GETDATE(), '', '', '', 0, 0, '', '', '', 0, 0)
                select * from express_charge_head where id = @@IDENTITY
            """
            h = sql.firstRow(s, [])
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
            firstTicket = row.GT_NO
        } else if (params.vid && row.h_id) {
            s = "select * from express_charge_head where id = ?"
            h = sql.firstRow(s, [row.h_id])
            // 找單身資料
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
            if (h?.express_no) {
                def foo = _.parseJson("http://v.juhe.cn/exp/index?key=b7f2944ba8eef30883de8eb21830bb6f&com=sf&no=${h.express_no}")
                if (foo.error_code == 0) {
                    express = foo.result.list
                }
            }
        } else if (params.act == 'pay') { // 填完地址之後 點支付按鈕
            // TODO: 這邊重新做 需要交易處理!!!!!!!
            h = ExpressChargeHead.get(params.hid)
            // 曾經產生過微信支付訂單的數據, 要重新建 head 資料
            if (h.status == 'unpaid') {
                def h2del = h
                h = new ExpressChargeHead()
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
                ExpressChargeBody.findAllByH(h2del).each {
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
            h.saveopenid = session.openid
            h.status = 'unpaid'
            h.save(flush: true)

            // TODO: 檢查有沒有資料 正常才可以出現支付畫面

            def uor = new WxPayUnifiedOrderRequest()
            uor.deviceInfo = 'WEB'
            uor.body = _.dev() ? '測試(' + params.kg + ')' : "爱维尔礼盒(" + params.kg + ")"
            uor.outTradeNo = "IWILL_SF_" + h.id.toString().padLeft(10, "0")
            uor.totalFee = _.dev() ? 1 : params.fee.toInteger() * 100
            uor.spbillCreateIp = request.getRemoteAddr()
            uor.notifyURL = _.dev() ? "http://test.dsiwill.com/events/exc_notify" : "http://api.dsiwill.com/iext/events/exc_notify"
            uor.tradeType = 'JSAPI'
            uor.attach = actno
            uor.openid = session.openid
            def payInfo = wxmp.service.payService.getPayInfo(uor)
            flash.json = payInfo as JSON
            redirect(action: 'exc_pay', params: ['showwxpaytitle': '1'])
            return
        } else if (!params.vid && !params.code && !flash.vid) {
            render 'invalid access!!' // make as AD page!
            return
        } 
        [h: h, ds: ds, lv1s: lv1s, fee: fee as JSON, express: express, isThisActno: isThisActno, firstTicket: firstTicket]    
    }

    def exc_pay() {
        def json = flash.json
        // println json // 看看什么原因没跳出付款界面
        [json: json]
    }

    // TODO: 檢查券有沒有被掃
    // TODO: 檢查 actno
    def exc_check() {
        def msg = ''
        def name = ''
        def tno = params.tno?.trim()
        def vcode = params.vcode?.trim().toUpperCase()
        def vid = ''
        def weight = 0
        def sql = new Sql(dataSource)
        def s = """
            select c.P_NO, c.P_NAME, a.GT_NO, a.VCODE, a.VID, a.ACTNO, b.id
            from GIFT_TOKEN a
            left join express_charge_body b on a.VID = b.VID
            left join part c on a.GI_P_NO = c.P_NO
            where GT_NO = ? and VCODE = ?
        """
        def row = sql.firstRow(s, [tno, vcode])
        if (row && row.ACTNO != actno) {
            msg = '券号或验证码错误！' // 非本次節慶的提貨券, 就顯示錯誤
        }
        if (!row) {
            msg = '券号或验证码错误！!'
        }
        if (row && row.id) {
            msg = '此券号已被使用过！'
        }
        if (!msg) {
            def h = ExpressChargeHead.get(params.hid)
            def d = new ExpressChargeBody()
            d.vid = row.VID?.trim()
            d.h = h
            d.save()
            name = row.P_NAME?.trim()
            vid = row.VID?.trim()
            weight = mooncake_weight[row.P_NO?.trim()]
        }
        def result = [name: name, tno: tno, vcode: vcode, vid: vid, msg: msg, weight: weight]
        render result as JSON
    }

    def exc_del() {
        def foo = ExpressChargeBody.findByVid(params.vid)
        foo.delete()
        def result = [msg: 'okay']
        render result as JSON
    }

    def exc_zone() {
        def result = []
        def sql = new Sql(dataSource)
        def s = "select distinct ${params.clv} as name from zone where ${params.plv} = ? order by ${params.clv}"
        sql.eachRow(s, [params.val]) {
            result << it.toRowResult()
        }
        render result as JSON
    }

    // TODO: 額外紀錄微信號, 然後做通知處理??


    def exc_notify() {
        def xml = IOUtils.toString(request.getInputStream())
        def data = wxmp.service.payService.getJSSDKCallbackData(xml)
        def map = _.obj2map(data)
        def sign = wxmp.service.payService.createSign(map, _.wxmpMchKey)

        // 簽名驗證
        if (sign == data.sign) {
            // update 
            def id = data.out_trade_no[9..18].toInteger()
            def h = ExpressChargeHead.get(id)
            h.openid = data.openid
            h.transactionid = data.transaction_id
            h.status = 'paid'
            h.save(flush: true)
            render "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>"
        } else {
            println 'i am error'
            render "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>"
        }

    }



}
