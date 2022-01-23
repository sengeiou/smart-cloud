package com.smart.pay.base.biz.service;

import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import smart.base.ActionResult;

/**
 * <li>统一支付</li>
 *
 * @author wangpeng
 * @date 2021/11/16 10:08
 * @see com.smart.pay.api
 * @since 1.0
 **/

public interface PaymentService {

    /**
     * 统一订单支付
     * @param payParam
     * @return
     */
    public ActionResult invokePayment(PayBase payParam);

    /**
     * 查询支付订单
     * @param queryParam
     * @return
     */
    public ActionResult invokeQuery(PayQueryBase queryParam);

    /**
     * 退款
     * @param refundParam
     * @return
     */
    public ActionResult invokeRefund(PayRefundBase refundParam);

    /**
     * 退款订单查询
     * @param refundParam
     * @return
     */
    public ActionResult invokeQueryRefund(PayRefundBase refundParam);
   /* public void doRefundQuery();
    public void payNotify();
    public void refundNotify();*/

}
