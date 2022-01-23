package com.smart.pay.base.biz.controller;

import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.factory.PayFactory;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.service.PayRefundNotifyService;
import com.smart.pay.model.PayNotifyResult;
import com.smart.pay.model.PayRefundNotifyResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <li>支付通知接收类</li>
 *
 * @author wangpeng
 * @date 2021/11/23 11:18
 * @see com.smart.pay.base.biz.controller
 * @since 1.0
 **/

@RestController
public class PayNotifyController {


    @PostMapping("/api/pay/notify/{code}")
    public void notify(@PathVariable("code") String type, HttpServletRequest request,
                         HttpServletResponse response){
        PayNotifyService notify = PayFactory.createNotify(type);


        PayNotifyResult result = notify.invokeWork(request);

        if (result.isSuccess()){
            //TODO 查询订单号 比对金额是否一致
            long amount = result.getAmount();

            //返回成功给第三方
            notify.writeResponse(response,true);

        }else {
            //返回失败给第三方
            notify.writeResponse(response,false);
        }
    }


    @PostMapping("/api/refund/notify/{code}/{payCode}")
    public void refundNotify(@PathVariable("code") String code,@PathVariable("payCode") String payCode, HttpServletRequest request,
                         HttpServletResponse response){
        PayRefundNotifyService refundNotify = PayFactory.createRefundNotify(code);


        PayRefundNotifyResult refundResult = refundNotify.invokeWork(request);

        if (refundResult.isSuccess()){
            //TODO 查询订单号 比对金额是否一致
            long amount = refundResult.getRefundAmount();

            //返回成功给第三方
            refundNotify.writeResponse(response,true);

        }else {
            //返回失败给第三方
            refundNotify.writeResponse(response,false);
        }
    }

}
