package com.smart.pay.biz.wx.notify.refunds;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayRefundNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.base.biz.utils.WriterUtil;
import com.smart.pay.biz.wx.*;
import com.smart.pay.biz.wx.exceptions.WxNotifyException;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import com.smart.pay.model.PayRefundNotifyResult;
import smart.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * <li>微信统一退款通知处理v3</li>
 *
 * @author wangpeng
 * @date 2021/11/23 16:17
 * @see com.smart.pay.biz.wx.notify
 * @since 1.0
 **/
public class WxPayRefundNotifyV2Impl extends AbstractPaymentListener implements PayRefundNotifyService {
    private static Logger logger= LoggerFactory.getLogger(WxPayRefundNotifyV2Impl.class);

    protected static ObjectMapper objectMapper = new ObjectMapper();

    private String apiVersion="2.0";

    public WxPayRefundNotifyV2Impl() {
        super();
        super.setBeforeVersion(null);
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

    /**
     * 验证签名, 验证支付金额, 结果转换成PayNotifyResult
     *
     * @return
     */
    @Override
    public PayRefundNotifyResult invokeWork(HttpServletRequest request) {
        PayRefundNotifyResult result=new PayRefundNotifyResult();
        try {
            //解析入参
            String param = parseParams(request);
            Map<String, String> dataMap = parseParams(param);
            String status=dataMap.get("refund_status");
            if (WXPayConstants.SUCCESS.equals(status)){
                result.setOrderNo(dataMap.get("out_trade_no"));
                result.setSuccess(true);
                result.setRefundAmount(new Long(dataMap.get("refund_fee")));
                result.setRefundUser(dataMap.get("refund_recv_accout"));
                sendListenerMessage(PaymentStatus.REFUND_PAY,result.getOrderNo(),
                        "微信退款通知",null, JSONObject.toJSONString(result));
                return result;
            }else if ("CHANGE".equals(status)){
                result.setSuccess(false);
                result.setPayErrorCode(status);
                result.setPayErrorMsg("退款异常");
                return result;
            }else if ("REFUNDCLOSE".equals(status)){
                result.setSuccess(false);
                result.setPayErrorCode(status);
                result.setPayErrorMsg("退款关闭");
                return result;
            }

        }catch (Exception e){
            logger.error("wx支付退款通知验证异常:",e);
            throw new WxPayException(e);
        }

        result.setSuccess(false);
        result.setPayErrorCode("403");
        result.setPayErrorMsg("退款失败");
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
        String result="<xml>\n" +
                "  <return_code><![CDATA[FAIL]]></return_code>\n" +
                "  <return_msg><![CDATA[处理失败]]></return_msg>\n" +
                "</xml>";
        if (isSuccess){
            result="<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }
        WriterUtil.writer(response,"text/xml;charset=utf-8",result);
    }

    /**
     *
     * @param param
     * @return
     */
    private Map<String,String> parseParams(String param)throws Exception{
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(param);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            logger.error("微信支付异常,No `return_code` in XML:{}",param);
            throw new WxNotifyException("微信支付异常,No `return_code`");
        }


        if (!WXPayConstants.SUCCESS.equals(return_code)){
            logger.error("微信支付失败,return_code:{} \n XML:{}",return_code,param);
            throw new WxNotifyException(String.format("微信支付失败,return_code:{%s}",return_code));
        }


        String req_info = respData.get("req_info");

        //解密
        String info=AesUtils.decryptToString(req_info,WxAdminConfig.WX_PAY_KEY);

        return WXPayUtil.xmlToMap(info);
    }




    @Override
    public PayRefundNotifyService findVersion(String version) {
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
