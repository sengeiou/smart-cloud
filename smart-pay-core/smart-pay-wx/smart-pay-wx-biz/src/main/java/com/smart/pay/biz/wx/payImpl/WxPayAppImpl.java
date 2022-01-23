package com.smart.pay.biz.wx.payImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smart.pay.base.biz.enums.WxPayWayEnum;
import com.smart.pay.biz.wx.WXPayConstants;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.model.PayBase;
import smart.base.ActionResult;
import smart.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <li>暂时未使用 未测试</li>
 *
 * @author wangpeng
 * @date 2021/11/17 10:34
 * @see com.smart.pay.biz.wx.payImpl
 * @since 1.0
 **/
public class WxPayAppImpl extends WxPaymentTemplate {

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
                .put("appid",payParam.getAppId())
                .put("description",payParam.getSubject())
                .put("notify_url",getNotifyUrl())
                .put("out_trade_no",payParam.getOrderNo())
                .put("attach",payParam.getDetail());
        rootNode.putObject("amount")
                .put("total", payParam.getAmount());

        try {
            String url= WXPayConstants.DOMAIN_API +WXPayConstants.UNIFIEDORDER_URL_SUFFIX+ WxPayWayEnum.APP.getPayCode();
            /*//v3目前没有沙箱环境
            if (!pay.isSandbox()){
            }*/
            String result=httpClient.httpPost(url,rootNode.toPrettyString());
            JsonNode jsonNode = objectMapper.readValue(result, JsonNode.class);
            String prepay_id=jsonNode.findPath("prepay_id").asText();
            if (StringUtil.isNotEmpty(prepay_id)){
                Map<String,String> date=new HashMap<>(1);
                date.put("prepayId",prepay_id);
                return ActionResult.success(date);
            }

            return ActionResult.fail(jsonNode.findPath("message").asText("微信支付异常"));
        }catch (Exception e){
            logger.info("微信app下单异常:",e);
        }
        return ActionResult.fail("下单失败");
    }
}
