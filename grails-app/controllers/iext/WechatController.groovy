package iext

import me.chanjar.weixin.common.util.*
import me.chanjar.weixin.mp.bean.*
import grails.converters.*

class WechatController {

    def wxmp2

    def index() {
        // 非法請求
        if (!wxmp2.service.checkSignature(params.timestamp, params.nonce, params.signature)) {
            render 'invalid request' 
            return
        }
        // 設定微信服務器專用
        if (StringUtils.isNotBlank(params.echostr)) {
            render params.echostr 
            return
        }
        // 加密方式
        def encryptType = StringUtils.isBlank(params.encrypt_type) ? "raw" : params.encrypt_type

        // TODO: 明文還沒測試 加密的沒問題!!
        if (encryptType == "raw") { // 明文
            def inMessage = WxMpXmlMessage.fromXml(request.getInputStream())
            def outMessage = wxmp2.router.route(inMessage)
            render outMessage ? outMessage.toXml() : ''
        } else if (encryptType == "aes") { // AES 加密
            def inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxmp2.config, params.timestamp, params.nonce, params.msg_signature)
            def outMessage = wxmp2.router.route(inMessage)
            render outMessage ? outMessage?.toEncryptedXml(wxmp2.config) : ''
        } else {
           render "unknow encrypt type" 
        }

    }


    def access_token() {
        def result = [:]
        def isIn = params.key in [
            '54a27eb4-c146-45a3-ae9d-976f21b5647d', // 創麥
            '206bff9b-3def-4954-a2f4-113048b56f43', // 群豐
        ]
        if (!isIn) {
            result['errmsg'] = 'invalid key'
            render result as JSON
            return
        }
        // 白癡創麥 只會 force, 把 force 關閉掉
        def force = (params.force == 'true_fuck') ? true : false

        result['access_token'] = wxmp2.service.getAccessToken(force)
        def tmp = wxmp2.config.getExpiresTime()  
        tmp = tmp - System.currentTimeMillis()
        tmp = tmp / 1000l + 200
        int i = tmp
        result['expires_in'] = i
        render result as JSON
    }

}
