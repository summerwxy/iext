package iext

import me.chanjar.weixin.common.util.*
import me.chanjar.weixin.mp.bean.*

class WechatController {

    def wxmp

    def index() {
        // 非法請求
        if (!wxmp.service.checkSignature(params.timestamp, params.nonce, params.signature)) {
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


}
