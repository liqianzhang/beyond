package com.practice.conf;

import com.google.gson.JsonObject;
import me.chanjar.weixin.common.bean.ToJson;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxNetCheckResult;
import me.chanjar.weixin.common.enums.TicketType;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxImgProcService;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.common.service.WxOcrService;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.RequestHttp;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpSemanticQuery;
import me.chanjar.weixin.mp.bean.result.WxMpCurrentAutoReplyInfo;
import me.chanjar.weixin.mp.bean.result.WxMpSemanticQueryResult;
import me.chanjar.weixin.mp.bean.result.WxMpShortKeyResult;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.enums.WxMpApiUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @MethodName: $
 * @Description: TODO
 * @Param: $
 * @Return: $
 * @Author: zhangliqian
 * @Date: $
 */
@Configuration
public class WxConfig {
//
//    @Autowired
//    private WechatAccountConfig accountConfig;
//    @Bean
//    public WxMpService wxMpService() {
//        WxMpService wxMpService = new WxMpServiceImpl();
//        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
//        return wxMpService;
//    }
//
//    @Bean
//    public WxMpConfigStorage wxMpConfigStorage() {
//        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
//        wxMpConfigStorage.setAppId(accountConfig.getMpAppId());
//        wxMpConfigStorage.setSecret(accountConfig.getMpAppSecret());
//        return wxMpConfigStorage;
//    }
//


}


