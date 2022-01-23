package com.smart.pay.biz.wx.notify.refunds;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayRefundNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.biz.wx.AesUtils;
import com.smart.pay.base.biz.utils.WriterUtil;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import com.smart.pay.model.PayRefundNotifyResult;
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
 * <li>微信统一退款通知处理v3</li>
 *
 * @author wangpeng
 * @date 2021/11/23 16:17
 * @see com.smart.pay.biz.wx.notify
 * @since 1.0
 **/
public class WxPayRefundNotifyImpl extends AbstractPaymentListener implements PayRefundNotifyService {
    private static Logger logger= LoggerFactory.getLogger(WxPayRefundNotifyImpl.class);

    protected static ObjectMapper objectMapper = new ObjectMapper();

    private String apiVersion="3.0";

    public WxPayRefundNotifyImpl() {
        super();
        super.setBeforeVersion(new WxPayRefundNotifyV2Impl());
        PayStrategy.addNotifyInstance(PayNotifyEnum.wx_refund.getCode(),this);
    }

    /**
     * 读取请求中的入参   xml or json
     *
     * @param request
     * @return
     */
    public String parseParams(HttpServletRequest request) {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(request.getInputStream());
            String encoding= StringUtils.isEmpty(request.getCharacterEncoding())?"UTF-8":request.getCharacterEncoding();
            return new String(bytes, encoding);
        }catch (Exception e){
            logger.error("微信支付回调,解析入参异常:",e);
        }
        return null;
    }

    @Override
    public PayRefundNotifyResult invokeWork(HttpServletRequest request) {
        String param = parseParams(request);
        if (!checkParams(request,param)){
            throw new WxPayException("非法请求，头部签名验证失败");
        }

        PayRefundNotifyResult result=new PayRefundNotifyResult();

        //解析入参
        WxNotifyResult wxNotifyResult = JSONObject.parseObject(param, WxNotifyResult.class);
        WxNotifyResult.Resource resource = wxNotifyResult.getResource();
        String cipherText = resource.getCipherText();
        String associatedData = resource.getAssociatedData();
        String nonce = resource.getNonce();
        String apiV3Key = WxAdminConfig.WX_PAY_KEY;
        try {
            if ("REFUND.SUCCESS".equals(wxNotifyResult.getEventType())){
                String parseStr = AesUtils.decryptToString(associatedData, nonce, cipherText, apiV3Key);
                JsonNode jsonNode = objectMapper.readValue(parseStr, JsonNode.class);
                result.setSuccess(true);
                result.setOrderNo(jsonNode.findPath("out_trade_no").asText());
                long payAmount = jsonNode.findPath("amount").get("refund").asLong();
                String userId = jsonNode.findPath("user_received_account").asText();
                result.setRefundAmount(payAmount);
                result.setRefundUser(userId);
                super.sendListenerMessage(PaymentStatus.SUCCESS_PAY,result.getOrderNo(),
                        "微信通知",null,jsonNode.toPrettyString());
                return result;
            }
        }catch (Exception e){
            logger.error("wx支付退款通知验证异常:",e);
            throw new WxPayException(e);
        }

        result.setSuccess(false);
        result.setPayErrorCode(wxNotifyResult.getEventType());
        result.setPayErrorMsg(wxNotifyResult.getSummary());
        return result;
    }


    /**
     * 返回接口
     *
     * @param response
     * @param isSuccess TRUE is 成功返回
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
