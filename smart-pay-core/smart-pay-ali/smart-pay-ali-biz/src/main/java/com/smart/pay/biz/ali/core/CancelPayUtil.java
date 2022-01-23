package com.smart.pay.biz.ali.core;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.service.PaymentListener;
import com.smart.pay.biz.ali.AliPayConfig;
import com.smart.pay.biz.ali.utils.DefaultAlipayClient;
import com.smart.pay.biz.ali.utils.PayResultStatusUtil;
import com.smart.pay.model.PayRefundBase;
import com.smart.pay.base.biz.enums.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * <li>关闭订单工具类</li>
 *
 * @author wangpeng
 * @date 2021/12/23 17:56
 * @see com.smart.pay.biz.ali.core
 * @since 1.0
 **/
public class CancelPayUtil {
    private Logger logger= LoggerFactory.getLogger(CancelPayUtil.class);
    private DefaultAlipayClient client;
    protected static ExecutorService executorService = new ThreadPoolExecutor(0, 2000,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    private PaymentListener paymentListener;


    public CancelPayUtil(DefaultAlipayClient client,PaymentListener paymentListener) {
        this.client=client;
        this.paymentListener=paymentListener;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected AlipayTradeCancelResponse invokeCancelPay(final String outTradeNo) {
        AlipayTradeCancelRequest cancelRequest=new AlipayTradeCancelRequest();
        JSONObject param=new JSONObject();
        param.put("out_trade_no",outTradeNo);
        cancelRequest.setBizContent(param.toJSONString());
        AlipayResponse response = getResponse(client, cancelRequest);
        PaymentEvent event=new PaymentEvent();
        event.setType(PaymentStatus.CLOSE_PAY);
        event.setRequestStr(cancelRequest.toString());
        event.setResponseStr(String.valueOf(response));
        event.setCodeStr("---");
        event.setMessage(outTradeNo);
        sendMessage(event);
        return (AlipayTradeCancelResponse) response;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected AlipayTradeCancelResponse cancelPayResult(final String outTradeNo) {
        AlipayTradeCancelResponse response = invokeCancelPay(outTradeNo);
        if (PayResultStatusUtil.cancelSuccess(response)) {
            // 如果撤销成功，则返回撤销结果
            logger.info("订单号:{},撤销订单成功,订单已关闭;response{}",outTradeNo,response);
            return response;
        }

        // 撤销失败
        if (PayResultStatusUtil.needRetry(response)) {
            // 如果需要重试，首先记录日志，然后调用异步撤销
            asyncCancel(outTradeNo);
        }
        return response;
    }

    // 异步撤销
    protected void asyncCancel(final String outTradeNo) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < AliPayConfig.getMaxCancelRetry(); i++) {
                    //重试间隔
                    try {
                        Thread.sleep(AliPayConfig.getCancelDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AlipayTradeCancelResponse response = invokeCancelPay(outTradeNo);
                    logger.info("异步撤销:订单号:{};response{}",outTradeNo,response);
                    if (PayResultStatusUtil.cancelSuccess(response) ||
                            !PayResultStatusUtil.needRetry(response)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        logger.info("异步撤销:订单号:{},终止,执行{}次",outTradeNo,i);
                        return ;
                    }
                }
            }
        });
    }


    protected void asyncRefund(final Function<PayRefundBase, AlipayTradeRefundResponse> function,final PayRefundBase refundBase) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < AliPayConfig.getMaxCancelRetry(); i++) {
                    //重试间隔
                    try {
                        Thread.sleep(AliPayConfig.getCancelDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AlipayTradeRefundResponse response = function.apply(refundBase);
                    logger.info("异步退款:入参:{};response{}",refundBase,response);
                    if (PayResultStatusUtil.success(response)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        logger.info("异步退款:订单号:{},终止,执行{}次",refundBase.getOrderNo(),i);
                        return ;
                    }
                }
            }
        });
    }


    protected AlipayResponse getResponse(AlipayClient client, AlipayRequest request) {
        try {
            AlipayResponse response = client.execute(request);
            if (response != null) {
                logger.info(response.getBody());
            }
            return response;

        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void sendMessage(PaymentEvent event){
        if (paymentListener != null) {
            paymentListener.notifyListener(event);
        }
    }
}
