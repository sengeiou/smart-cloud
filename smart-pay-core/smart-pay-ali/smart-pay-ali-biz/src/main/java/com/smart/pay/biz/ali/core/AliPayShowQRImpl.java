package com.smart.pay.biz.ali.core;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.smart.pay.base.biz.enums.AliPayWayEnum;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.biz.ali.utils.PayResultStatusUtil;
import com.smart.pay.model.PayBase;
import com.smart.pay.base.biz.enums.PaymentEvent;
import smart.base.ActionResult;

import java.math.BigDecimal;

/**
 * <li>
 *     生成二维码支付:
 *     商户出示二维码,用户扫码支付
 * </li>
 *
 * @author wangpeng
 * @date 2021/12/30 14:28
 * @see com.smart.pay.biz.ali.core
 * @since 1.0
 **/
public class AliPayShowQRImpl extends AbstractAliPayTemplate{


    private static final long serialVersionUID = -5630266271668008262L;

    public AliPayShowQRImpl() {
        super(AliPayWayEnum.QRPAY);
    }

    /**
     * 统一订单支付
     *
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(PayBase payParam) {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(super.getNotifyUrl());
        //request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payParam.getOrderNo());
        BigDecimal amount=new BigDecimal(payParam.getAmount());
        bizContent.put("total_amount", amount.movePointLeft(2));
        bizContent.put("subject", "测试商品");
        bizContent.put("body", "详情");
        request.setBizContent(bizContent.toJSONString());
        logger.info("trade.precreate bizContent:" + request.getBizContent());

        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) getResponse(client, request);
        PaymentEvent event=new PaymentEvent();
        event.setCodeStr(AliPayWayEnum.QRPAY.getDec());
        event.setMessage(payParam);
        event.setType(PaymentStatus.WAIT_PAY);
        event.setRequestStr(request.toString());
        event.setResponseStr(response.toString());
        sendListenerMessage(event);
        ActionResult result;
        if (PayResultStatusUtil.success(response)) {
            // 预下单交易成功
            logger.info("调用支付交易:明确成功OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
            result=ActionResult.success(response.getQrCode());

        } else if (PayResultStatusUtil.tradeError(response)) {
            // 预下单发生异常，状态未知
            logger.info("调用支付交易:请求异常,状态未知,OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
            result= ActionResult.fail(PayResultCode.FailUnknown.getCode(),
                    PayResultCode.FailUnknown.getMessage());

        } else {
            // 其他情况表明该预下单明确失败
            logger.info("调用支付交易:明确失败OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
            result=ActionResult.fail("下单失败");
        }
        return result;
    }
}
