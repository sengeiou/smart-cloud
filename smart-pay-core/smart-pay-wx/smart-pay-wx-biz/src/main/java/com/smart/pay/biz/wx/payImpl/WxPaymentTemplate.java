package com.smart.pay.biz.wx.payImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.enums.WxPayWayEnum;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.base.biz.service.PaymentService;
import com.smart.pay.base.biz.utils.HttpClientFactory;
import com.smart.pay.base.biz.utils.HttpClientPayUtil;
import com.smart.pay.biz.wx.*;
import com.smart.pay.biz.wx.exceptions.WxPayException;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import com.smart.pay.wx.entity.model.WxQueryParam;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import smart.base.ActionResult;
import smart.util.StringUtil;
import lombok.extern.java.Log;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/16 11:54
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
@Log
public abstract class WxPaymentTemplate extends AbstractPaymentListener implements PaymentService {

    private static final long serialVersionUID = 940899997989196688L;

    protected Logger logger = LoggerFactory.getLogger(WxPaymentTemplate.class);
    //沙箱环境
    protected String WX_PAY_SANDBOX = "/sandboxnew";

    protected String apiVersion="3.0";

    protected String code;

    protected static HttpClientPayUtil httpClient;

    protected static WxKeySigner wxKeySigner;

    protected static ObjectMapper objectMapper = new ObjectMapper();

    public WxPaymentTemplate() {
    }

    public WxPaymentTemplate(WxPayWayEnum wayEnum) {
        this.code = wayEnum.getPayCode();
        init();
        PayStrategy.addPayInstance(wayEnum.getCode(), this);
    }

    protected static void init() {
        try {
            /*KeyFactory kf = KeyFactory.getInstance("RSA");
            WX_PAY_KEY_V3 = kf.generatePrivate(
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode("E8dntisTQMbqrwUxZbXkm3Te73bHoOxq")));*/
            wxKeySigner = new WxKeySigner(WxAdminConfig.SERIAL_NUMBER, WxAdminConfig.PRIVATE_KEY);

            WechatPayHttpClientBuilder httpClientBuilder = WechatPayHttpClientBuilder.create()
                    .withMerchant(WxAdminConfig.MCH_ID, WxAdminConfig.SERIAL_NUMBER, WxAdminConfig.PRIVATE_KEY)
                    .withValidator(new WechatPay2Validator(WxAdminConfig.verifier));

            CloseableHttpClient closeableHttpClient = HttpClientFactory.createHttpClient(httpClientBuilder, WXPayConstants.DOMAIN_API);
            httpClient = new HttpClientPayUtil(closeableHttpClient);
        } catch (Exception e) {

        }
    }


    @Override
    public ActionResult invokeQuery(PayQueryBase queryParam) {
        WxQueryParam params =queryParam.convert(WxQueryParam.class);

        try {
            String url;
            if (params.isSandbox()) {
                url = WXPayConstants.DOMAIN_API + WX_PAY_SANDBOX + WXPayConstants.QUERY_URL_SUFFIX;
            } else {
                if (StringUtil.isNotEmpty(params.getOrderNo())){
                    url = WXPayConstants.DOMAIN_API + WXPayConstants.QUERY_URL_SUFFIX + params.getOrderNo();
                }else {
                    url= WXPayConstants.DOMAIN_API + WXPayConstants.QUERY_URL_SUFFIX_id + params.getOrderNo();
                }
            }

            Map<String, Object> param = new HashMap<>();
            param.put("mchid", WxAdminConfig.MCH_ID);
            String result = httpClient.httpGet(url, param);
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            String trade_state = jsonNode.findPath("trade_state").asText("error");
            if ("SUCCESS".equals(trade_state)) {
                // 成功
                return ActionResult.success("支付成功");
            } else if ("REFUND".equals(trade_state)) {
                // 转入退款
                return ActionResult.fail("正在退款");
            } else if ("NOTPAY".equals(trade_state)) {
                // 未支付
                return ActionResult.fail("订单未支付");
            } else if ("CLOSED".equals(trade_state)) {
                // 已关闭
                return ActionResult.fail("订单已关闭");
            } else if ("REVOKED".equals(trade_state)) {
                // 已撤销（刷卡支付）
                return ActionResult.fail("已撤销");
            } else if ("USERPAYING".equals(trade_state)) {
                // 用户支付中
                return ActionResult.fail("支付中");
            } else if ("PAYERROR".equals(trade_state)) {
                // 支付失败(其他原因，如银行返回失败)
                return ActionResult.fail("支付失败");
            } else {
                return ActionResult.fail(jsonNode.findPath("message")
                        .asText("微信查询异常"));
                //return ActionResult.fail(jsonNode.findPath("trade_state_desc").asText());
            }

        } catch (Exception e) {
            logger.error("微信{}查询订单异常 :", code, e);
        }
        return ActionResult.fail("网络异常,请稍后在试");
    }


    /**
     * 退款
     *
     * @param refundParam
     * @return
     */
    private JsonNode refund(PayRefundBase refundParam){
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("out_trade_no", refundParam.getRefundOrderNo())
                .put("out_refund_no", refundParam.getOrderNo())
                .put("reason", refundParam.getReason())
                .put("notify_url", getRefundNotifyUrl());
        rootNode.putObject("amount")
                .put("refund", refundParam.getRefundAmount())
                .put("total", refundParam.getAmount())
                .put("currency", "CNY");
        try {
            String url = WXPayConstants.DOMAIN_API + WXPayConstants.REFUND_URL_SUFFIX;
            /*//v3目前没有沙箱环境
            if (!pay.isSandbox()){
            }*/
            logger.info("微信{}退款,入参{}", code, rootNode.toPrettyString());
            String result = httpClient.httpPost(url, rootNode.toPrettyString());
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            sendListenerMessage(PaymentStatus.WAIT_REFUND_PAY, refundParam, code,
                    rootNode.toPrettyString(),jsonNode.toPrettyString());
            return jsonNode;
        }catch (Exception e){
            logger.info("微信{}退款异常:", code , e);
        }
        return null;
    }



    @Override
    public ActionResult invokeRefund(PayRefundBase refundParam) {
        JsonNode jsonNode = refund(refundParam);
        String refundStatus="errorOO";
        if (jsonNode !=null)
            refundStatus = jsonNode.findPath("status").asText("errorOO");

        if ("SUCCESS".equals(refundStatus)) {
                //退款成功
                /*JsonNode amount = jsonNode.findPath("amount");
                if (amount.isObject()) {
                    System.out.println(amount.toString());
                }*/
            return ActionResult.success();
        } else if ("CLOSED".equals(refundStatus)) {
            //退款关闭
            return ActionResult.initialization(PayResultCode.FailClosed);
        } else if ("PROCESSING".equals(refundStatus)) {
            //退款处理中
            return ActionResult.initialization(PayResultCode.FailUnknown);
        } else {
            //退款异常
            //网络异常,微信实际退款成功,防止用户2次退款

            ActionResult queryRefund = invokeQueryRefund(refundParam);
            if (queryRefund.isSuccess()){
                return queryRefund;
            }

            //异步重试
            asyncRefund(this::refund, refundParam);

            return ActionResult.initialization(PayResultCode.FailUnknown);
        }
    }


    @Override
    public ActionResult invokeQueryRefund(PayRefundBase refundParam) {
        Map<String, Object> param = new HashMap<>(1);
        //param.put("mchid", WxAdminConfig.MCH_ID);
        try {
            String url = WXPayConstants.DOMAIN_API + WXPayConstants.REFUNDQUERY_URL_SUFFIX + refundParam.getOrderNo();
            /*//v3目前没有沙箱环境
            if (!pay.isSandbox()){
            }*/
            String result = httpClient.httpGet(url, param);
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            String refundNo = jsonNode.findPath("out_refund_no").asText();
            String status = jsonNode.findPath("status").asText("errorOO");

                if ("SUCCESS".equals(status)) {
                    //退款成功
                    String accounts = jsonNode.findPath("user_received_account").asText();
                    JsonNode amount = jsonNode.findPath("amount");
                    if (amount.isObject()) {
                        //退款金额
                        long refund = amount.get("refund").asLong();
                        System.out.println(amount.toString());
                    }
                    return ActionResult.success();
                } else if ("CLOSED".equals(status)) {
                    //退款关闭
                    return ActionResult.success("退款成功,订单已关闭");
                } else if ("PROCESSING".equals(status)) {
                    //退款处理中
                    return ActionResult.fail("微信退款中");
                } else {
                    //退款异常
                    return ActionResult.fail(jsonNode.findPath("message")
                            .asText("微信退款异常"));
                }


        } catch (Exception e) {
            logger.error("微信{}退款异常:", code, e);
        }
        return ActionResult.fail("退款失败");
    }

    /**
     * 创建签名
     *
     * @param params
     * @return
     * @throws Exception
     */
    protected Map<String, String> createSign(Map<String, String> params) {
        String nonceStr = WXPayUtil.generateNonceStr();
        long timestamp = WXPayUtil.getCurrentTimestamp();
        params.put("timeStamp", String.valueOf(timestamp));
        params.put("nonceStr", String.valueOf(timestamp));
        if (!params.containsKey("package") || !params.containsKey("appId"))
            throw new WxPayException("prepayId或者appId不能为空");

        String message = params.get("appId").trim() + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + params.get("package").trim() + "\n";

        String signStr = wxKeySigner.signStr(message.getBytes(StandardCharsets.UTF_8));
        params.put("signType", "RSA");
        params.put("paySign", signStr);
        return params;
    }


    /**
     * 异步执行退款
     * @param function 退款方法
     * @param refundBase 退款入参
     */
    protected void asyncRefund(final Function<PayRefundBase, JsonNode> function, final PayRefundBase refundBase) {
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

                    JsonNode apply = function.apply(refundBase);
                    logger.info("异步退款:入参:{};response{}",refundBase,apply);
                    String refundStatus="errorOO";
                    if (apply !=null)
                        refundStatus = apply.findPath("status").asText("errorOO");
                    if ("SUCCESS".equals(refundStatus) || "CLOSED".equals(refundStatus)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        logger.info("异步退款:订单号:{},终止,执行{}次",refundBase.getOrderNo(),i);
                        return ;
                    }
                }
            }
        });
    }

    protected String getNotifyUrl() {
        return WXPayConstants.DOMAIN_API + PayNotifyEnum.wx_pay.getUrl()+apiVersion+ "/"+ code;
    }

    protected String getRefundNotifyUrl() {
        return WXPayConstants.DOMAIN_API + PayNotifyEnum.wx_refund.getUrl()+apiVersion+"/" + code;
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
