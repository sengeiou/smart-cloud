package com.smart.pay.biz.ali.core;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.smart.pay.base.biz.enums.AliPayWayEnum;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.biz.ali.AliPayConfig;
import com.smart.pay.biz.ali.AliPayConstants;
import com.smart.pay.biz.ali.utils.PayResultStatusUtil;
import com.smart.pay.entiity.ali.MicroPayParam;
import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.base.biz.enums.PaymentEvent;
import smart.base.ActionResult;

import java.math.BigDecimal;

import static com.smart.pay.biz.ali.utils.PayResultStatusUtil.tradeError;

/**
 * <li>扫码支付:
 *     用户出示条形码
 *     商户扫码枪扫码收款
 * </li>
 *
 * @author wangpeng
 * @date 2021/12/17 17:27
 * @see com.smart.pay.biz.ali.core
 * @since 1.0
 **/
public class AliPayMicroPayImpl extends AbstractAliPayTemplate {


    private static final long serialVersionUID = -4045190551323327184L;

    public AliPayMicroPayImpl() {
        super(AliPayWayEnum.MICROPAY);
    }

    /**
     * 统一订单支付
     *
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(PayBase payParam) {
        MicroPayParam microPayParam = payParam.convert(MicroPayParam.class);
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payParam.getOrderNo());
        BigDecimal amount=new BigDecimal(payParam.getAmount());
        bizContent.put("total_amount", amount.movePointLeft(2));
        bizContent.put("subject", payParam.getSubject());
        //支付场景。
        //bar_code：当面付条码支付场景；
        //security_code：当面付刷脸支付场景，对应的auth_code为fp开头的刷脸标识串；
        //默认值为bar_code。
        bizContent.put("scene", "bar_code");
        bizContent.put("auth_code", microPayParam.getQrCode());
        // 服务器异步通知页面路径
        request.setNotifyUrl(super.getNotifyUrl());
        // 服务器同步通知页面路径
        request.setReturnUrl("");

        // 封装参数
        request.setBizContent(bizContent.toJSONString());

        ActionResult result=ActionResult.fail("支付失败");

        try {
            // 首先调用支付api
            AlipayTradePayResponse response = (AlipayTradePayResponse) getResponse(client, request);
            PaymentEvent event=new PaymentEvent();
            event.setCodeStr(AliPayWayEnum.MICROPAY.getCode());
            event.setMessage(payParam);
            event.setType(PaymentStatus.WAIT_PAY);
            event.setRequestStr(request.toString());
            event.setResponseStr(response.toString());
            sendListenerMessage(event);
            if (response != null && AliPayConstants.SUCCESS.equals(response.getCode())) {
                // 支付交易明确成功
                logger.info("调用支付交易:明确成功OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
                result = ActionResult.success();

            } else if (response != null && AliPayConstants.PAYING.equals(response.getCode())) {
                // 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
                logger.info("支付交易:支付中OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
                PayQueryBase queryParam=new PayQueryBase();
                queryParam.setOrderNo(payParam.getOrderNo());
                ActionResult actionResult = loopQueryResult(queryParam);
                //查询结果未知调用撤销接口
                return checkQueryAndCancel(payParam.getOrderNo(), null, result, actionResult);

            } else if (tradeError(response)) {
                // 系统错误，则查询一次交易，如果交易没有支付成功，则调用撤销
                logger.info("支付中OrderNo:{},aliResponse:{}",payParam.getOrderNo(),response);
                PayQueryBase queryParam=new PayQueryBase();
                queryParam.setOrderNo(payParam.getOrderNo());
                ActionResult actionResult = invokeQuery(queryParam);
                return checkQueryAndCancel(payParam.getOrderNo(), null, result, actionResult);

            } else {
                // 其他情况表明该订单支付明确失败
                result=ActionResult.fail("支付失败");
            }

        } catch (Exception e) {
            logger.error("下单接口异常:", e);
            result=ActionResult.fail("支付失败");
        }


        return result;
    }



    // 轮询查询订单支付结果
    private ActionResult loopQueryResult(PayQueryBase queryBase) {
        ActionResult actionResult = ActionResult.fail("查询异常");
        for (int i = 0; i < AliPayConfig.getMaxQueryRetry(); i++) {
            //重试间隔
            try {
                Thread.sleep(AliPayConfig.getQueryDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            actionResult = super.invokeQuery(queryBase);
            if (actionResult.isSuccess() ||
                    PayResultCode.FailClosed.getCode().equals(actionResult.getCode())) {
                return actionResult;
            }
        }
        return actionResult;
    }


    // 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
    protected ActionResult checkQueryAndCancel(String outTradeNo, String appAuthToken, ActionResult result,
                                               ActionResult queryResponse) {
        if (queryResponse.isSuccess()) {
            // 如果查询返回支付成功，则返回相应结果
            return ActionResult.success();
        }

        // 如果查询结果不为成功，则调用撤销
        CancelPayUtil cancelPayUtil=new CancelPayUtil(this.client,this.listener);
        AlipayTradeCancelResponse cancelResponse = cancelPayUtil.cancelPayResult(outTradeNo);
        if (PayResultStatusUtil.tradeError(cancelResponse)) {
            // 如果第一次同步撤销返回异常，则标记支付交易为未知状态
            result.setCode(PayResultCode.FailUnknown.getCode());
            result.setMsg(PayResultCode.FailUnknown.getMessage());
        }else {
            //默认撤销成功
            // 标记支付为失败，如果撤销未能成功，产生的单边帐由人工处理
            result=ActionResult.fail("支付失败");
        }
        return result;
    }
}
