package com.smart.pay.biz.wx.payV2Impl;

import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.service.PaymentListener;
import com.smart.pay.base.biz.utils.HttpClientPayUtil;
import com.smart.pay.biz.wx.PayExecutorUtils;
import com.smart.pay.biz.wx.WXPayConstants;
import com.smart.pay.biz.wx.WXPayUtil;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.model.PayBase;
import com.smart.pay.base.biz.enums.PaymentEvent;
import smart.base.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <li>关闭订单工具类</li>
 *
 * @author wangpeng
 * @date 2021/12/23 17:56
 * @see com.smart.pay.biz.ali.core
 * @since 1.0
 **/
public class WxCancelPayUtil {
    private Logger logger= LoggerFactory.getLogger(WxCancelPayUtil.class);
    private HttpClientPayUtil client;

    //监听器
    private PaymentListener paymentListener;

    //沙箱环境
    protected static String WX_PAY_SANDBOX = "/sandboxnew";


    public WxCancelPayUtil(HttpClientPayUtil client, PaymentListener paymentListener) {
        this.client=client;
        this.paymentListener=paymentListener;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected ActionResult<Map<String, String>> invokeCancelPay(final String outTradeNo,String appId,boolean isSandbox) {
        Map<String, String> param = new HashMap<>();
        param.put("appid", appId);
        param.put("mch_id", WxAdminConfig.MCH_ID);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("sign_type", "MD5");
        param.put("out_trade_no", outTradeNo);
        try {
            ActionResult<Map<String, String>> mapActionResult = httpXmlPost(
                    WXPayConstants.V2_ORDERQUERY_URL_SUFFIX, param, isSandbox);
            PaymentEvent event = new PaymentEvent();
            event.setType(PaymentStatus.CLOSE_PAY);
            event.setRequestStr(param.toString());
            event.setResponseStr(mapActionResult.toString());
            event.setCodeStr("---");
            event.setMessage(outTradeNo);
            sendMessage(event);
            return mapActionResult;
        }catch (Exception e){
            logger.error("微信支付撤销异常:",e);
        }
        return null;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected ActionResult cancelPayResult(PayBase payBase) {
        ActionResult<Map<String, String>> mapActionResult = invokeCancelPay(payBase.getOrderNo(),
                payBase.getAppId(), payBase.isSandbox());

        if (mapActionResult !=null && mapActionResult.isSuccess()) {
            // 如果撤销成功，则返回撤销结果
            logger.info("订单号:{},撤销订单成功,订单已关闭;response{}",payBase.getOrderNo(),mapActionResult);
            return mapActionResult;
        }

        // 撤销失败
        if (mapActionResult !=null && !mapActionResult.isSuccess()
                && "Y".equals(mapActionResult.getData().get("recall"))) {
            // 如果需要重试，首先记录日志，然后调用异步撤销
            asyncCancel(payBase);
        }
        return mapActionResult;
    }

    // 异步撤销
    protected void asyncCancel(final PayBase payBase) {
        PayExecutorUtils.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < WxAdminConfig.maxCancelRetry; i++) {
                    //重试间隔
                    try {
                        Thread.sleep(WxAdminConfig.cancelDuration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ActionResult<Map<String, String>> mapActionResult = invokeCancelPay(payBase.getOrderNo(),
                            payBase.getAppId(), payBase.isSandbox());
                    logger.info("异步撤销:订单号:{};response{}",payBase.getOrderNo(),mapActionResult);
                    if (mapActionResult.isSuccess() ||
                            (!mapActionResult.isSuccess()
                                    && "N".equals(mapActionResult.getData().get("recall")))) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        logger.info("异步撤销:订单号:{},终止,执行{}次",payBase.getOrderNo(),i);
                        return ;
                    }
                }
            }
        });
    }






    private void sendMessage(PaymentEvent event){
        if (paymentListener != null) {
            paymentListener.notifyListener(event);
        }
    }

    /**
     * v2模板发送post请求
     * @param uri
     * @param params
     * @param isSandbox
     * @return
     * @throws Exception
     */
    protected ActionResult<Map<String, String>> httpXmlPost(String uri,Map<String,String> params,
                                                            boolean isSandbox) throws Exception {
        String url;
        if (isSandbox){
            url= WXPayConstants.DOMAIN_API +WX_PAY_SANDBOX+uri;
        }else {
            url=WXPayConstants.DOMAIN_API +uri;
        }
        String xmlParam = WXPayUtil.generateSignedXml(params,WxAdminConfig.WX_PAY_KEY_V2);
        HashMap<String, String> header = new HashMap<>();
        header.put("ContentType", "text/xml");
        header.put("User-Agent", WXPayConstants.USER_AGENT + " " + WxAdminConfig.MCH_ID);
        String responseStr= client.httpPost(url,
                xmlParam,header);
        return processResponseXml(responseStr);
    }



    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    private ActionResult<Map<String, String>> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            //微服务通信标识字段失败 业务状态未知
            logger.error("微信支付异常,No `return_code` in XML:{}",xmlStr);
            return ActionResult.initialization(PayResultCode.FailUnknown);
        }


        if (!WXPayConstants.SUCCESS.equals(return_code)){
            logger.error("微信支付失败,return_code:{} \n XML:{}",return_code,xmlStr);
            return ActionResult.initialization(PayResultCode.FailUnknown);
            //return ActionResult.fail("wx error:"+respData.get("return_msg"));
        }

        //验证签名
        if (WXPayUtil.isSignatureValid(respData, WxAdminConfig.WX_PAY_KEY_V2)) {
            String result_code = respData.get("result_code");
            if (!WXPayConstants.SUCCESS.equals(result_code)){
                logger.error("微信支付失败,result_code:{} \n XML:{}",result_code,xmlStr);
                return ActionResult.fail("wx error:"+respData.get("err_code_des"));
            }
            //成功
            return ActionResult.success(respData);
        } else {
            //返回结果签名失败有可能被盗链
            return ActionResult.fail("wx返回签名失败!");
        }
    }

}
