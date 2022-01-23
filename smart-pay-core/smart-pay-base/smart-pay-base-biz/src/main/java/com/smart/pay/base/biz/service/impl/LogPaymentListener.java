package com.smart.pay.base.biz.service.impl;

import com.smart.pay.base.biz.service.PaymentListener;
import com.smart.pay.base.biz.enums.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <li>日志类型,监听器实现</li>
 *
 * @author wangpeng
 * @date 2021/12/24 10:57
 * @see com.smart.pay.base.biz.service.impl
 * @since 1.0
 **/
public class LogPaymentListener implements PaymentListener {
    private static Logger logger= LoggerFactory.getLogger(LogPaymentListener.class);


    @Override
    public void notifyListener(PaymentEvent event) {
        invokeLog(event);
        switch (event.getType()) {
            case WAIT_PAY:
                //发起支付
                invokePayment(event.getMessage());
                break;
            case CLOSE_PAY:
                //关闭订单
                invokeClose(event.getMessage());

                break;
            case REFUND_PAY:
                //发起退款
                invokeRefund(event.getMessage());
                break;
            default:
                return;
        }
    }

    private void invokeLog(PaymentEvent event){
        String sb = "*******************支付日志start**************************\n\t" +
                "****支付类型:{}**************************\n\t" +
                "****支付状态:{}**************************\n\t" +
                "****请求入参:{}**************************\n\t" +
                "****请求返回:{}**************************\n\t" +
                "*******************支付日志end****************************\n\t";
        logger.info(sb,event.getCodeStr(),event.getType().getDec()
                ,event.getRequestStr(),event.getResponseStr());
    }

    private void invokePayment(Object payBase){

    }


    private void invokeClose(Object orderNo){
        //logger.info();

    }



    private void invokeRefund(Object orderNo){

    }

}
