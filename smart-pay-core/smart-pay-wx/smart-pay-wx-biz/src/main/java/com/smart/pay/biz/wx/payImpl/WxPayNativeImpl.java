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
 * <li>Native--用户扫描二维码支付</li>
 *
 * @author wangpeng
 * @date 2021/11/16 11:35
 * @see com.smart.pay.biz.wx 微信v3接口
 * @since 1.0
 **/
public class WxPayNativeImpl extends WxPaymentTemplate {

    private static final long serialVersionUID = -5427739407659677823L;

    public WxPayNativeImpl() {
        super(WxPayWayEnum.NATIVE);
    }

    @Override
    public ActionResult invokePayment(PayBase payParam) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("mchid", WxAdminConfig.MCH_ID)
                .put("appid", payParam.getAppId())
                .put("description", payParam.getSubject())
                .put("out_trade_no", payParam.getOrderNo())
                .put("attach", payParam.getDetail())
                .put("notify_url", getNotifyUrl());
        rootNode.putObject("amount")
                .put("total", payParam.getAmount())
                .put("currency", "CNY");
        try {
            String url = WXPayConstants.DOMAIN_API + WXPayConstants.UNIFIEDORDER_URL_SUFFIX + WxPayWayEnum.NATIVE.getPayCode();
            /*//v3目前没有沙箱环境
            if (!pay.isSandbox()){
            }*/
            logger.info("微信NATIVE下单url{},入参{}",url,rootNode.toString());
            String result = httpClient.httpPost(url, rootNode.toString());
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            String code_url = jsonNode.findPath("code_url").asText(null);
            super.sendListenerMessage(PaymentStatus.WAIT_PAY,payParam,super.code,
                    rootNode.toPrettyString(),jsonNode.toPrettyString());
            if (StringUtil.isNotEmpty(code_url)) {
                Map<String, String> date = new HashMap<>(1);
                date.put("url", code_url);
                return ActionResult.success(date);
            }

            return ActionResult.fail(jsonNode.findPath("message").asText("微信支付异常"));
        } catch (Exception e) {
            logger.error("微信NATIVE下单异常 :", e);
        }

        return ActionResult.fail("下单失败");
    }

}
