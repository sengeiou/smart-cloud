package com.smart.pay.api.fallback;

import com.smart.pay.api.PaymentApi;
import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import smart.base.ActionResult;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/17 10:11
 * @see com.smart.pay.api.fallback
 * @since 1.0
 **/
@Component
public class PaymentApiFallback implements PaymentApi {
    /**
     * 统一订单支付
     *
     * @param request
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(HttpServletRequest request, PayBase payParam) {
        return null;
    }

    /**
     * 查询支付订单
     *
     * @param request
     * @param queryParam
     * @return
     */
    @Override
    public ActionResult invokeQuery(HttpServletRequest request, PayQueryBase queryParam) {
        return null;
    }

    /**
     * 退款
     *
     * @param request
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeRefund(HttpServletRequest request, PayRefundBase refundParam) {
        return null;
    }

    /**
     * 退款订单查询
     *
     * @param request
     * @param refundParam
     * @return
     */
    @Override
    public ActionResult invokeQueryRefund(HttpServletRequest request, PayRefundBase refundParam) {
        return null;
    }
}
