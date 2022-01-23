package com.smart.pay.biz.wx.payImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.enums.WxPayWayEnum;
import com.smart.pay.biz.wx.WXPayConstants;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.model.PayBase;
import smart.base.ActionResult;
import smart.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <li>JSAPI--用户用微信浏览器打开商城  调用支付</li>
 *
 * @author wangpeng
 * @date 2021/11/17 11:14
 * @see com.smart.pay.biz.wx.payImpl 微信v3接口
 * @since 1.0
 **/
public class WxPayJsapiImpl extends WxPaymentTemplate {

    private static final long serialVersionUID = -6554270628572702733L;

    public WxPayJsapiImpl() {
        super(WxPayWayEnum.JSAPI);
    }

    /**
     * 统一订单支付
     *
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(PayBase payParam) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid", WxAdminConfig.MCH_ID)
                .put("appid", payParam.getAppId())
                .put("description", payParam.getSubject())
                .put("notify_url", getNotifyUrl())
                .put("out_trade_no", payParam.getOrderNo())
                .put("attach", payParam.getDetail());
        rootNode.putObject("amount")
                .put("total", payParam.getAmount());
        rootNode.putObject("payer")//用户在直连商户appid下的唯一标识。 下单前需获取到用户的Openid
                .put("openid", payParam.getChannelUser());

        //rootNode.toPrettyString();
        try {
            String url = WXPayConstants.DOMAIN_API + WXPayConstants.UNIFIEDORDER_URL_SUFFIX + WxPayWayEnum.JSAPI.getPayCode();
            /*//v3目前没有沙箱环境
            if (!pay.isSandbox()){
            }*/
            logger.info("微信JSAPI下单url{},入参{}",url,rootNode.toString());
            String result = httpClient.httpPost(url, rootNode.toString());
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            String prepay_id = jsonNode.findPath("prepay_id").asText(null);
            super.sendListenerMessage(PaymentStatus.WAIT_PAY,payParam,super.code,
                    rootNode.toPrettyString(),jsonNode.toPrettyString());
            if (StringUtil.isNotEmpty(prepay_id)) {
                Map<String, String> date = new HashMap<>(6);
                date.put("package", "prepay_id=" + prepay_id);
                date.put("appId", payParam.getAppId());
                return ActionResult.success(super.createSign(date));
            }

            return ActionResult.fail(jsonNode.findPath("message").asText("微信支付异常"));
        } catch (Exception e) {
            logger.info("微信JSAPI下单异常:", e);
        }
        return ActionResult.fail("下单失败");
    }


}
