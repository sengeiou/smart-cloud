package com.smart.pay.biz.ali.notify;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.utils.WriterUtil;
import com.smart.pay.biz.ali.AliPayConfig;
import com.smart.pay.model.PayNotifyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <li>支付宝 支付回调</li>
 *
 * @author wangpeng
 * @date 2022/1/4 15:32
 * @see com.smart.pay.biz.ali.notify
 * @since 1.0
 **/
public class AliPaymentNotify extends AbstractPaymentListener implements PayNotifyService {

    private static final long serialVersionUID = -6526200993004264440L;
    private Logger logger = LoggerFactory.getLogger(AliPaymentNotify.class);

    //当前阿里接口版本号,以便以后升级
    private String apiVersion="2.0";

    public AliPaymentNotify() {
        PayStrategy.addNotifyInstance(PayNotifyEnum.ali_pay.getCode(),this);
    }

    /**
     * 验证签名, 验证支付金额, 结果转换成PayNotifyResult
     *
     * @param request
     * @return
     */
    @Override
    public PayNotifyResult invokeWork(HttpServletRequest request) {
        // 编码
        String charset = AliPayConfig.getCharset();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        boolean validation = false;
        PayNotifyResult result = new PayNotifyResult();
        try {
            logger.info("支付宝支付回调通知:   入参:{}",params);
            validation = AlipaySignature.rsaCheckV1(params, AliPayConfig.getPublicKey(), charset,
                    AliPayConfig.getSignType());
            if (validation) {

                String status = params.getOrDefault("trade_status","Error");
                logger.info("签名验证成功! 支付状态{}",status);
                String out_trade_no = params.getOrDefault("out_trade_no","0");
                if ("TRADE_SUCCESS".equals(status)){
                    String total_amount = params.getOrDefault("total_amount","0");
                    String buyer_id = params.getOrDefault("buyer_id","0");
                    BigDecimal amount=new BigDecimal(total_amount);
                    result.setSuccess(true);
                    result.setOrderNo(out_trade_no);
                    result.setAmount(amount.movePointRight(2).longValue());
                    result.setPayUser(buyer_id);
                    sendListenerMessage(PaymentStatus.SUCCESS_PAY,out_trade_no,
                            "通知",null, JSONObject.toJSONString(params));
                }else {
                    sendListenerMessage(PaymentStatus.FAIL_PAY,out_trade_no,
                            "通知",null, JSONObject.toJSONString(params));
                }
            }else {
                logger.info("签名验证失败!");
                result.setSuccess(false);
                result.setPayErrorMsg("签名验证失败");
                result.setPayErrorCode("20001");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常:",e);
            result.setSuccess(false);
            result.setPayErrorMsg("系统异常! 请稍后再试!");
            result.setPayErrorCode("20002");
        }

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
        String charset=AliPayConfig.getCharset();

        if (isSuccess){
            WriterUtil.writer(response,"text/html;charset=" + charset,"success",charset);
        }else {
            WriterUtil.writer(response,"text/html;charset=" + charset,"fail",charset);
        }

    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    @Override
    public String currentVersion() {
        return "2.0";
    }
}
