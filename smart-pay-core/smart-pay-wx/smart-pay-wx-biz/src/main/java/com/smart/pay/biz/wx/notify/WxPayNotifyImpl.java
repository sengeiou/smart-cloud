package com.smart.pay.biz.wx.notify;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.biz.wx.AesUtils;
import com.smart.pay.base.biz.utils.WriterUtil;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.biz.wx.exceptions.WxNotifyException;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import com.smart.pay.model.PayNotifyResult;
import com.smart.pay.wx.entity.model.SignHeader;
import com.smart.pay.wx.entity.model.WxNotifyResult;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import smart.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * <li>微信统一通知处理v3</li>
 *
 * @author wangpeng
 * @date 2021/11/23 16:17
 * @see com.smart.pay.biz.wx.notify
 * @since 1.0
 **/
public class WxPayNotifyImpl extends AbstractPaymentListener implements PayNotifyService {
    private static Logger logger= LoggerFactory.getLogger(WxPayNotifyImpl.class);

    protected static ObjectMapper objectMapper = new ObjectMapper();
    private String apiVersion="3.0";
    private PayVersion payVersion;
    private boolean isSuccess = false;
    public WxPayNotifyImpl() {
        PayStrategy.addNotifyInstance(PayNotifyEnum.wx_pay.getCode(),this);
    }

    /**
     * 读取请求中的入参   xml or json
     *
     * @param request
     * @return
     */
    private String parseParams(HttpServletRequest request) {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(request.getInputStream());
            String encoding= StringUtils.isEmpty(request.getCharacterEncoding())?"UTF-8":request.getCharacterEncoding();
            return new String(bytes, encoding);
        }catch (Exception e){
            logger.error("微信支付回调,解析入参异常:",e);
        }
        return null;
    }

    /**
     * 验证签名, 验证支付金额, 结果转换成PayNotifyResult
     *
     * @return
     */
    @Override
    public PayNotifyResult invokeWork(HttpServletRequest request) {
        //获取入参
        String param = parseParams(request);

        //验证
        if (!checkParams(request,param)){
            throw new WxPayException("非法请求，头部签名验证失败");
        }

        PayNotifyResult result=new PayNotifyResult();

        //解析入参
        WxNotifyResult wxNotifyResult = JSONObject.parseObject(param, WxNotifyResult.class);
        WxNotifyResult.Resource resource = wxNotifyResult.getResource();
        String cipherText = resource.getCipherText();
        String associatedData = resource.getAssociatedData();
        String nonce = resource.getNonce();
        String apiV3Key = WxAdminConfig.WX_PAY_KEY;
        try {
            if ("SACTION.SUCCESS".equals(wxNotifyResult.getEventType())){
                String parseStr = AesUtils.decryptToString(associatedData, nonce, cipherText, apiV3Key);
                JsonNode jsonNode = objectMapper.readValue(parseStr, JsonNode.class);
                this.isSuccess=true;
                result.setSuccess(true);
                result.setOrderNo(jsonNode.findPath("out_trade_no").asText());
                long payAmount = jsonNode.findPath("amount").get("payer_total").asLong();
                String userId = jsonNode.findPath("payer").get("openid").asText();
                result.setAmount(payAmount);
                result.setPayUser(userId);
                super.sendListenerMessage(PaymentStatus.SUCCESS_PAY,result.getOrderNo(),
                        "微信支付通知",null,jsonNode.toPrettyString());
                return result;
            }
        }catch (Exception e){
            logger.error("wx支付通知验证异常:",e);
            throw new WxPayException(e);
        }

        result.setSuccess(false);
        result.setPayErrorCode(wxNotifyResult.getEventType());
        result.setPayErrorMsg(wxNotifyResult.getSummary());
        return result;
    }

    /**
     * 返回结果
     *
     * @param response
     * @param isSuccess
     * @return
     */
    @Override
    public void writeResponse(HttpServletResponse response, boolean isSuccess) {
        String result="{\n" +
                "    \"code\": \"FAIL\",\n" +
                "    \"message\": \"失败\"\n" +
                "}";
        if (isSuccess){
            result="{\n" +
                    "    \"code\": \"SUCCESS\",\n" +
                    "    \"message\": \"成功\"\n" +
                    "}";
        }
        WriterUtil.writer(response,"text/json;charset=utf-8",result);
    }

    /**
     *
     * @param request
     * @param param
     * @return
     */
    private boolean checkParams(HttpServletRequest request,String param){
        SignHeader header = new SignHeader();
        header.setTimeStamp(request.getHeader("Wechatpay-Timestamp"));
        header.setNonce(request.getHeader("Wechatpay-Nonce"));
        header.setSerial(request.getHeader("Wechatpay-Serial"));
        header.setSignature(request.getHeader("Wechatpay-Signature"));
        String beforeSign = String.format("%s\n%s\n%s\n",
                header.getTimeStamp(),
                header.getNonce(),
                param);
        return getVerifier().verify(header.getSerial(), beforeSign.getBytes(StandardCharsets.UTF_8),
                header.getSignature());
    }



    private AutoUpdateCertificatesVerifier getVerifier(){
        return WxAdminConfig.verifier;
    }

    @Override
    public PayNotifyService findVersion(String version) {
        if (StringUtil.isEmpty(version) || this.apiVersion.equals(version)){
            return this;
        }

        // 最低版本api
        throw new WxNotifyException(String.format("支付类型{%s}未找到实例",PayNotifyEnum.wx_pay.getCode()));
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    @Override
    public String currentVersion() {
        return this.apiVersion;
    }
}
