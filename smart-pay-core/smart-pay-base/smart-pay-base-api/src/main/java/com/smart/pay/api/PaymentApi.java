package com.smart.pay.api;

import com.smart.pay.api.fallback.PaymentApiFallback;
import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import smart.base.ActionResult;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <li>统一支付</li>
 *
 * @author wangpeng
 * @date 2021/11/16 10:08
 * @see com.smart.pay.api
 * @since 1.0
 **/

@FeignClient(name = FeignName.PAYMENT_SERVER_NAME , fallback = PaymentApiFallback.class)
public interface PaymentApi {

    /**
     * 统一订单支付
     * @param payParam
     * @return
     */
    @PostMapping("/pay/order/unified")
    public ActionResult invokePayment(HttpServletRequest request,@RequestBody PayBase payParam) throws Exception;

    /**
     * 查询支付订单
     * @param queryParam
     * @return
     */
    @PostMapping("/pay/order/query")
    public ActionResult invokeQuery(HttpServletRequest request,@RequestBody PayQueryBase queryParam);

    /**
     * 退款
     * @param refundParam
     * @return
     */
    @PostMapping("/pay/order/refund")
    public ActionResult invokeRefund(HttpServletRequest request,@RequestBody PayRefundBase refundParam);

    /**
     * 退款订单查询
     * @param refundParam
     * @return
     */
    @PostMapping("/pay/order/refund/query")
    public ActionResult invokeQueryRefund(HttpServletRequest request,@RequestBody PayRefundBase refundParam);
   /* public void doRefundQuery();
    public void payNotify();
    public void refundNotify();*/
}
