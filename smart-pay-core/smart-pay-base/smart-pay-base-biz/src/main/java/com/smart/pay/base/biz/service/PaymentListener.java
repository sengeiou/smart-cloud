package com.smart.pay.base.biz.service;

import com.smart.pay.base.biz.enums.PaymentEvent;

/**
 * <li>支付状态监听器</li>
 *
 * @author wangpeng
 * @date 2021/12/24 10:44
 * @see com.smart.pay.base.biz.service
 * @since 1.0
 **/
public interface PaymentListener {

    public void notifyListener(PaymentEvent event);
}
