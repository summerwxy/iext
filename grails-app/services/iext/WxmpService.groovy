package iext

import grails.transaction.Transactional
import grails.util.Environment
import me.chanjar.weixin.mp.api.*
import me.chanjar.weixin.common.util.*
import me.chanjar.weixin.mp.bean.*
import me.chanjar.weixin.common.session.*
import me.chanjar.weixin.common.api.*

@Transactional
class WxmpService {

    private __appId = _.wxmpAppId
    private __secret = _.wxmpAppSecret
    private __token = _.wxmpToken
    private __aesKey = _.wxmpAesKey
    private __mchId = _.wxmpMchId
    private __mchKey = _.wxmpMchKey

    private __config
    def getConfig() {
        if (!__config) {
            __config = new WxMpInMemoryConfigStorage()
            __config.setAppId(__appId)
            __config.setSecret(__secret)
            __config.setToken(__token)
            __config.setAesKey(__aesKey)
            __config.setPartnerId(__mchId)
            __config.setPartnerKey(__mchKey)
        }
        return __config
    }
    private __service
    def getService() {
        if (!__service) {
            __service = new WxMpServiceImpl()
            __service.setWxMpConfigStorage(config)
        }
        return __service
    }
    

    private __router
    def getRouter() {
        if (!__router || Environment.current != Environment.PRODUCTION) { // 开发时, 每次都要新增
            __router = new WxMpMessageRouter(service)
            // 使用方法: http://tinyurl.com/qhloo95
            // WxConsts: http://tinyurl.com/lp6hyau

            // ========== msgType='event', event='subscribe' ==========
            //__bindSubscribe()
            // ========== msgType='event', event='LOCATION' ==========
            //__bindLocation()
            // ========= 点赞 event = CLICK_LIKE =========
            //__bindClickLike()
            // ========= 连到任何网页 ==========
            //__bindViewAll()
            // ========= test ==========
            __bindTest()
            // ========= catch all ==========
            //__bindCatchAll()
        }
        return __router
    }


    def __bindTest() {
        def handler = new WxMpMessageHandler() {
            @Override 
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager wxSessionManager) {
                def m = WxMpXmlOutMessage.TEXT().content("测试加密消息").fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName()).build()
                return m
            }
        }
        __router.rule()
            .async(false)
            .content("wxy test")
            .handler(handler)
        .end()
    }


}
