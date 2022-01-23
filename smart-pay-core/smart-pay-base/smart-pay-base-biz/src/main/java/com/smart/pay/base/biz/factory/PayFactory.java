package com.smart.pay.base.biz.factory;

import com.smart.pay.api.PaymentApi;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.service.PayRefundNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.base.biz.service.PaymentService;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/17 9:21
 * @see com.smart.pay.base.biz.factory
 * @since 1.0
 **/
public class PayFactory {

    /**
     * 获取最新版本的处理类
     * @param code
     * @return
     */
    public static PaymentService createPay(String code) {
        return createPay(code,null);
    }


    public static PaymentService createPay(String code, String version) {
        PayVersion payInstance = PayStrategy.getPayInstance(code);
        if (payInstance !=null){
            return payInstance.findVersion(version);
        }
        return null;
    }

    /**
     * 支付通知类
     * @param code
     * @return
     */
    public static PayNotifyService createNotify(String code) {
        return createNotify(code,null);
    }

    /**
     * 支付通知类
     * @param code
     * @param version
     * @return
     */
    public static PayNotifyService createNotify(String code, String version) {
        return PayStrategy.getNotifyInstance(code).findVersion(version);
    }

    /**
     * 退款通知类
     * @param code
     * @return
     */
    public static PayRefundNotifyService createRefundNotify(String code) {
        return createRefundNotify(code,null);
    }
    /**
     * 退款通知类
     * @param code
     * @param version
     * @return
     */
    public static PayRefundNotifyService createRefundNotify(String code, String version) {
        return PayStrategy.getNotifyInstance(code).findVersion(version);
    }


}
