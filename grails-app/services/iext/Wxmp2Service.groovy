package iext

import grails.transaction.Transactional
import grails.util.Environment
import me.chanjar.weixin.mp.api.*
import me.chanjar.weixin.mp.api.impl.*
import me.chanjar.weixin.common.util.*
import me.chanjar.weixin.mp.bean.*
import me.chanjar.weixin.mp.bean.message.*
import me.chanjar.weixin.common.session.*
import me.chanjar.weixin.common.api.*

@Transactional
class Wxmp2Service {

    // fuwu@dsiwill.com 的
    private __appId = 'wx52ea5a89a99b5be2'
    private __secret = 'b99009a8092759d07350c8bc16d0d745'
    private __token = 'abcde12345'
    private __aesKey = 'soyNMqgiSlkbxnMGH3Tz9SW9pJwpeYNKYZyoTWrzx48'
    private __mchId = '1220083801'
    private __mchKey = 'b84b9bb08bd8f064fab58420c7d304bb'

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
