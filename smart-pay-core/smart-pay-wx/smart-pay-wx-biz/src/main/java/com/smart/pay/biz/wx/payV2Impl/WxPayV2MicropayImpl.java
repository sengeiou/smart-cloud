package com.smart.pay.biz.wx.payV2Impl;

import com.alibaba.fastjson.JSONObject;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.enums.WxPayWayEnum;
import com.smart.pay.biz.wx.WXPayConstants;
import com.smart.pay.biz.wx.WXPayUtil;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import com.smart.pay.wx.entity.model.WxPayQRParamV2;
import com.smart.pay.wx.entity.model.WxPayRefundV2;
import smart.base.ActionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * <li>MICROPAY--用户出示二维码,商家用扫码枪,扫描收款</li>
 *
 * @author wangpeng
 * @date 2021/11/18 17:18
 * @see com.smart.pay.biz.wx.payV2Impl  微信v2接口
 * @since 1.0
 **/
public class WxPayV2MicropayImpl extends WxPaymentV2Template {


    public WxPayV2MicropayImpl() {
        super(WxPayWayEnum.MICROPAY);
    }

    /**
     * 统一订单支付
     *
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(PayBase payParam) {
        Map<String, String> param = new HashMap<>();
        param.put("appid", payParam.getAppId());
        param.put("mch_id", WxAdminConfig.MCH_ID);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("sign_type", "MD5");
        param.put("body", payParam.getSubject());
        param.put("attach", payParam.getDetail());
        param.put("out_trade_no", payParam.getOrderNo());
        // 单位：分
        param.put("total_fee", String.valueOf(payParam.getAmount()));
        param.put("fee_type", "CNY");
        param.put("spbill_create_ip", payParam.getClientIp());
        //巴枪扫描的二维码
        param.put("auth_code", payParam.convert(WxPayQRParamV2.class).getQrCode());
        try {
            logger.info("微信MICROPAY下单,入参{}",payParam);
            ActionResult<Map<String, String>> mapActionResult = httpXmlPost(
                    WXPayConstants.V2_MICROPAY_URL_SUFFIX, param, payParam.isSandbox());

            super.sendListenerMessage(PaymentStatus.WAIT_PAY,payParam,super.code,
                    JSONObject.toJSONString(param), JSONObject.toJSONString(mapActionResult));
            if (mapActionResult.isSuccess()) {
                return ActionResult.success("支付成功", null);
            }

            //微服务异常
            if (PayResultCode.FailUnknown.getCode().equals(mapActionResult.getCode())){
                //调用查询接口
                PayQueryBase queryBase=new PayQueryBase();
                queryBase.setAppId(payParam.getAppId());
                queryBase.setOrderNo(payParam.getOrderNo());
                queryBase.setSandbox(payParam.isSandbox());
                ActionResult result = loopQueryResult(queryBase);
                return checkQueryAndCancel(payParam,mapActionResult,result);
            }

            //明确表示失败
            return mapActionResult;
        } catch (Exception e) {
            logger.error("微信付款码支付异常 :", e);
        }
        return ActionResult.fail("系统异常!请重试");
    }



    /**
     * 退款
     *
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeRefund(PayRefundBase refundParam) {
        WxPayRefundV2 convert = refundParam.convert(WxPayRefundV2.class);
        Map<String, String> param = new HashMap<>();
        param.put("appid", convert.getAppId());
        param.put("mch_id", WxAdminConfig.MCH_ID);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("sign_type", "MD5");
        param.put("out_trade_no", refundParam.getRefundOrderNo());
        param.put("out_refund_no", refundParam.getOrderNo());
        // 单位：分
        param.put("total_fee", String.valueOf(refundParam.getAmount()));
        param.put("refund_fee", String.valueOf(refundParam.getRefundAmount()));
        param.put("refund_fee_type", "CNY");
        param.put("refund_desc", refundParam.getReason());
        //退款通知地址
        param.put("notify_url", getNotify_refund_url());
        try {
            logger.info("微信MICROPAY退款,入参{}",param);
            ActionResult<Map<String, String>> mapActionResult = httpXmlPost(
                    WXPayConstants.V2_REFUND_URL_SUFFIX, param, refundParam.isSandbox());

            super.sendListenerMessage(PaymentStatus.WAIT_REFUND_PAY,refundParam,super.code,
                    JSONObject.toJSONString(param), JSONObject.toJSONString(mapActionResult));

            if (mapActionResult.isSuccess()) {
                return ActionResult.success("退款成功", null);
            }
            return mapActionResult;
        } catch (Exception e) {
            logger.error("微信付款码退款异常 :", e);
        }
        return ActionResult.fail("系统异常!请重试");
    }

    /**
     * 退款订单查询
     *
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeQueryRefund(PayRefundBase refundParam) {
        WxPayRefundV2 convert = refundParam.convert(WxPayRefundV2.class);
        Map<String, String> param = new HashMap<>();
        param.put("appid", convert.getAppId());
        param.put("mch_id", WxAdminConfig.MCH_ID);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("sign_type", "MD5");
        param.put("out_trade_no", refundParam.getRefundOrderNo());
        param.put("out_refund_no", refundParam.getOrderNo());
        //param.put("offset", "0");
        try {
            ActionResult<Map<String, String>> mapActionResult = httpXmlPost(
                    WXPayConstants.V2_REFUNDQUERY_URL_SUFFIX, param, refundParam.isSandbox());
            if (mapActionResult.isSuccess()) {
                Map<String, String> data = mapActionResult.getData();
                int count = Integer.parseInt(data.getOrDefault("refund_count", "1"));
                String refund_status=null;
                for (int i = 0; i < count; i++) {
                    if (data.containsKey("refund_status_"+count)){
                        refund_status = data.get("refund_status_"+count);
                        count=i;
                        break;
                    }
                }

                if ("SUCCESS".equals(refund_status)) {
                    return ActionResult.success(String.format("退款成功,退款到%s",
                            data.get("refund_recv_accout_"+count)));
                } else if ("REFUNDCLOSE".equals(refund_status)) {
                    //退款关闭
                    return ActionResult.fail("退款失败,该退款记录已关闭");
                } else if ("PROCESSING".equals(refund_status)) {
                    //退款处理中
                    return ActionResult.fail("退款处理中");
                } else {
                    //退款异常 "CHANGE".equals(refund_status)
                    return ActionResult.fail("退款异常");
                }

            }
            return mapActionResult;
        } catch (Exception e) {
            logger.error("微信付款码退款异常 :", e);
        }
        return ActionResult.fail("系统异常!请重试");
    }


    // 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
    protected ActionResult checkQueryAndCancel(PayBase payBase, ActionResult result, ActionResult queryResponse) {
        if (queryResponse.isSuccess()) {
            // 如果查询返回支付成功，则返回相应结果
            return ActionResult.success();
        }

        // 如果查询结果不为成功，则调用撤销
        WxCancelPayUtil cancelPayUtil=new WxCancelPayUtil(this.httpClient,super.getListener());
        ActionResult cancelResult = cancelPayUtil.cancelPayResult(payBase);
        if (!cancelResult.isSuccess()) {
            // 如果第一次同步撤销返回异常，则标记支付交易为未知状态
            result.setCode(PayResultCode.FailUnknown.getCode());
            result.setMsg(PayResultCode.FailUnknown.getMessage());
        }else {
            //默认撤销成功
            //标记支付为失败，如果撤销未能成功，产生的单边帐由人工处理
            result=ActionResult.fail("支付失败");
        }
        return result;
    }


    // 轮询查询订单支付结果
    private ActionResult loopQueryResult(PayQueryBase queryBase) {
        ActionResult actionResult = ActionResult.fail("查询异常");
        for (int i = 0; i < WxAdminConfig.maxCancelRetry; i++) {
            //重试间隔
            try {
                Thread.sleep(WxAdminConfig.cancelDuration);
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
}
