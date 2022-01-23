package com.smart.pay.base.biz.controller;

import com.smart.pay.api.PaymentApi;
import com.smart.pay.base.biz.factory.PayFactory;
import com.smart.pay.base.biz.service.PaymentService;
import com.smart.pay.model.PayBase;
import com.smart.pay.model.PayQueryBase;
import com.smart.pay.model.PayRefundBase;
import smart.base.ActionResult;
import smart.util.StringUtil;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * <li>统一支付页面</li>
 *
 * @author wangpeng
 * @date 2021/12/2 17:22
 * @see com.smart.pay.base.biz.controller
 * @since 1.0
 **/
@RestController
public class PaymentController implements PaymentApi {

    /**
     * 统一订单支付
     *
     * @param request
     * @param payParam
     * @return
     */
    @Override
    public ActionResult invokePayment(HttpServletRequest request, PayBase payParam) throws Exception {
        String code = request.getHeader("code");
        String version=request.getHeader("version");

        if (StringUtil.isEmpty(code))
            return ActionResult.fail("支付代码不能为空");

        version=StringUtil.isEmpty(version)? null:version;

        //生成订单id
        payParam.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        PaymentService paymentService = PayFactory.createPay(code, version);
        return paymentService.invokePayment(payParam);
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
        String code = request.getHeader("code");
        String version=request.getHeader("version");

        if (StringUtil.isEmpty(code))
            return ActionResult.fail("支付代码不能为空");

        version=StringUtil.isEmpty(version)? null:version;

        PaymentService paymentService = PayFactory.createPay(code, version);
        return paymentService.invokeQuery(queryParam);
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
        String code = request.getHeader("code");
        String version=request.getHeader("version");

        if (StringUtil.isEmpty(code))
            return ActionResult.fail("支付代码不能为空");

        version=StringUtil.isEmpty(version)? null:version;
        PaymentService paymentService = PayFactory.createPay(code, version);

        //生成订单id
        refundParam.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        //TODO 查询订单总金额
        return paymentService.invokeRefund(refundParam);
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
        String code = request.getHeader("code");
        String version=request.getHeader("version");

        if (StringUtil.isEmpty(code))
            return ActionResult.fail("支付代码不能为空");

        version=StringUtil.isEmpty(version)? null:version;

        PaymentService paymentService = PayFactory.createPay(code, version);
        return paymentService.invokeQueryRefund(refundParam);
    }
}
