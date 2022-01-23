package com.smart.pay.biz.ali.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.smart.pay.base.biz.enums.AliPayWayEnum;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PaymentListener;
import com.smart.pay.base.biz.service.PaymentService;
import com.smart.pay.biz.ali.AliPayConfig;
import com.smart.pay.biz.ali.utils.DefaultAlipayClient;
import com.smart.pay.biz.ali.utils.PayResultStatusUtil;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import com.smart.pay.base.biz.enums.PaymentEvent;
import smart.base.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/17 17:34
 * @see com.smart.pay.biz.ali.core
 * @since 1.0
 **/
abstract class AbstractAliPayTemplate extends AbstractPaymentListener implements PaymentService {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractAliPayTemplate.class);
    private static final long serialVersionUID = -3364744655041043464L;

    //支付类型代码
    private final AliPayWayEnum payEnum;

    //请求客户端
    protected DefaultAlipayClient client;

    //监听器
    protected PaymentListener listener;

    //订单超时时间
    protected String timeoutExpress;
    protected String apiVersion = "2.0";

    public AbstractAliPayTemplate(AliPayWayEnum wayEnum) {
        this.payEnum = wayEnum;
        PayStrategy.addPayInstance(wayEnum.getCode(), this);
    }

    public void init() {
        if (client !=null)
            return;
        client = new DefaultAlipayClient(AliPayConfig.getOpenApiDomain(), AliPayConfig.getAppid(),
                AliPayConfig.getPrivateKey(), AliPayConfig.getFormat(), AliPayConfig.getCharset(),
                AliPayConfig.getAlipayPublicKey(), AliPayConfig.getSignType());
    }


    /**
     * 查询支付订单
     *
     * @param queryParam
     * @return
     */
    @Override
    public ActionResult invokeQuery(PayQueryBase queryParam) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        JSONObject param = new JSONObject();
        param.put("out_trade_no", queryParam.getOrderNo());
        param.put("query_options", queryParam.getExpand());
        request.setBizContent(param.toJSONString());
        logger.info("trade.query bizContent:" + request.getBizContent());

        AlipayTradeQueryResponse queryResponse = (AlipayTradeQueryResponse) getResponse(client, request);

        if (PayResultStatusUtil.tradeError(queryResponse)) {
            // 查询发生异常，交易状态未知
            logger.info("查询发生异常，交易状态:未知,OrderNo:{},aliResponse:{}", queryParam.getOrderNo(), queryResponse);
            ActionResult fail = ActionResult.fail(PayResultCode.FailUnknown.getCode(),
                    PayResultCode.FailUnknown.getMessage());
            return fail;

        } else if (PayResultStatusUtil.querySuccess(queryResponse)) {
            // 查询返回该订单交易支付成功
            logger.info("查询成功，交易状态:支付成功,OrderNo:{},aliResponse:{}", queryParam.getOrderNo(), queryResponse);
            return ActionResult.success();

        } else if ("TRADE_CLOSED".equals(queryResponse.getTradeStatus())) {
            //订单已关闭
            logger.info("查询成功，交易状态:订单已关闭,OrderNo:{},aliResponse:{}", queryParam.getOrderNo(), queryResponse);
            return ActionResult.fail(PayResultCode.FailClosed.getCode(),
                    PayResultCode.FailClosed.getMessage());
        }
        return ActionResult.fail("查询失败,请重试");
    }

    /**
     * 退款
     *
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeRefund(PayRefundBase refundParam) {
        AlipayTradeRefundResponse response = this.refund(refundParam);
        ActionResult result;
        if (PayResultStatusUtil.success(response) && "Y".equals(response.getFundChange())) {
            // 退款交易成功
            logger.info("{}调用退款交易:成功OrderNo:{},aliResponse:{}", this.payEnum.getCode(), refundParam.getOrderNo(), response);
            result = ActionResult.success();

        } else if (PayResultStatusUtil.tradeError(response)) {
            // 退款发生异常，退款状态未知
            logger.info("{}调用退款交易:状态未知OrderNo:{},aliResponse:{}", this.payEnum.getCode(), refundParam.getOrderNo(), response);

            //重试查询一次,如果失败则重新发送退款
            ActionResult queryRefund = invokeQueryRefund(refundParam);
            if (!queryRefund.isSuccess()) {
                new CancelPayUtil(this.client, this.listener).asyncRefund(new Function<PayRefundBase, AlipayTradeRefundResponse>() {
                    @Override
                    public AlipayTradeRefundResponse apply(PayRefundBase object) {
                        return refund(object);
                    }
                }, refundParam);
                //状态未知人工处理
                result = ActionResult.fail(PayResultCode.FailUnknown.getCode(),
                        PayResultCode.FailUnknown.getMessage());
            } else {
                result = ActionResult.success();
            }

        } else {
            // 其他情况表明该订单退款明确失败
            logger.info("{}调用退款交易:失败OrderNo:{},aliResponse:{}", this.payEnum.getCode(), refundParam.getOrderNo(), response);
            result = ActionResult.fail("退款失败");
        }
        return result;
    }

    private AlipayTradeRefundResponse refund(PayRefundBase refundParam) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setNotifyUrl(this.getRefundNotifyUrl());
        //request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", refundParam.getRefundOrderNo());
        bizContent.put("refund_amount", refundParam.getAmount());
        bizContent.put("out_request_no", refundParam.getOrderNo());
        JSONArray query_options=new JSONArray();
        query_options.add("refund_detail_item_list");
        bizContent.put("query_options", query_options);
        request.setBizContent(bizContent.toJSONString());

        AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) getResponse(client, request);
        PaymentEvent event = new PaymentEvent();
        event.setCodeStr(this.payEnum.getDec());
        event.setMessage(refundParam);
        event.setType(PaymentStatus.REFUND_PAY);
        event.setRequestStr(request.toString());
        event.setResponseStr(response.toString());
        sendListenerMessage(event);
        return response;
    }

    /**
     * 退款订单查询
     *
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeQueryRefund(PayRefundBase refundParam) {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        request.setNotifyUrl(this.getNotifyUrl());
        //request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", refundParam.getRefundOrderNo());
        bizContent.put("out_request_no", refundParam.getOrderNo());
        request.setBizContent(bizContent.toJSONString());

        //请求第三方
        AlipayTradeFastpayRefundQueryResponse response = (AlipayTradeFastpayRefundQueryResponse) getResponse(client, request);

        if (PayResultStatusUtil.tradeError(response)) {
            // 查询发生异常，交易状态未知
            logger.info("查询发生异常，交易状态:未知,OrderNo:{},aliResponse:{}", refundParam.getOrderNo(), response);
            ActionResult fail = ActionResult.fail(PayResultCode.FailUnknown.getCode(),
                    PayResultCode.FailUnknown.getMessage());
            return fail;

        } else if (PayResultStatusUtil.refundSuccess(response)) {
            // 查询返回该订单交易支付成功
            logger.info("查询成功，交易状态:退款成功,OrderNo:{},aliResponse:{}", refundParam.getOrderNo(), response);
            return ActionResult.success();

        } else if ("REFUND_CLOSED".equals(response.getRefundStatus())) {
            //订单已关闭
            logger.info("查询成功，交易状态:订单已关闭,OrderNo:{},aliResponse:{}", refundParam.getOrderNo(), response);
            return ActionResult.fail(PayResultCode.FailClosed.getCode(),
                    PayResultCode.FailClosed.getMessage());
        }
        return ActionResult.fail("查询失败,请重试");
    }


    /**
     * 回调地址
     *
     * @return
     */
    protected String getNotifyUrl() {
        return "WXPayConstants.DOMAIN_API" + PayNotifyEnum.ali_pay.getUrl() + this.apiVersion + "/" + this.payEnum.getCode();
    }

    protected String getRefundNotifyUrl() {
        return "WXPayConstants.DOMAIN_API" + PayNotifyEnum.ali_refund.getUrl() + this.apiVersion + "/" + this.payEnum.getCode();
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected AlipayResponse getResponse(AlipayClient client, AlipayRequest request) {
        try {
            AlipayResponse response = client.execute(request);
            if (response != null) {
                logger.info(response.getBody());
            }
            return response;

        } catch (AlipayApiException e) {
            logger.error("支付宝{}支付异常:",this.payEnum.getCode(),e);
            if(e.getCause() instanceof java.security.spec.InvalidKeySpecException){
                logger.error("商户私钥格式不正确，请确认配置文件Alipay-Config.properties中是否配置正确");
            }
            return null;
        }
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
