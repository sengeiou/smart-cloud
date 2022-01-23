package com.smart.pay.biz.wx.notify;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.pay.api.PaymentApi;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.biz.wx.AesUtils;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.biz.wx.WxKeySigner;
import com.smart.pay.biz.wx.exceptions.WxNotifyException;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import com.smart.pay.model.PayNotifyResult;
import com.smart.pay.wx.entity.model.SignHeader;
import com.smart.pay.wx.entity.model.WxNotifyResult;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import smart.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * <li>微信统一通知处理 v2</li>
 * 弃用  v2目前只用到付款码接口   付款码类型没有支付通知
 * @author wangpeng
 * @date 2021/11/23 16:17
 * @see com.smart.pay.biz.wx.notify
 * @since 1.0
 **/
@Deprecated
public class WxPayNotifyV2Impl extends AbstractPaymentListener implements PayNotifyService {
    private static Logger logger= LoggerFactory.getLogger(WxPayNotifyV2Impl.class);

    protected static ObjectMapper objectMapper = new ObjectMapper();
    private static String apiVersion="v2";


    public WxPayNotifyV2Impl() {
        //PayStrategy.addNotifyInstance(PayNotifyEnum.wx_v2.getCode(),"WxPayNotifyV2Impl");
    }

    /**
     * 验证签名, 验证支付金额, 结果转换成PayNotifyResult
     *
     * @param request
     * @return
     */
    @Override
    public PayNotifyResult invokeWork(HttpServletRequest request) {
        return null;
    }

    /**
     * 返回接口
     *
     * @param response
     * @param isSuccess TRUE is 成功返回
     */
    @Override
    public void writeResponse(HttpServletResponse response, boolean isSuccess) {

    }


    /**
     * 获取当前版本号
     *
     * @return
     */
    @Override
    public String currentVersion() {
        return apiVersion;
    }
}
