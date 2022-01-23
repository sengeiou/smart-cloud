package com.smart.pay.model;

import lombok.Data;

/**
 * <li>通知支付结果</li>
 *
 * @author wangpeng
 * @date 2021/11/23 16:31
 * @see com.smart.pay.model
 * @since 1.0
 **/
@Data
public class PayNotifyResult {

    /**
     * 是否支付成功
     */
    private boolean success;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付用户
     */
    private String payUser;

    /**
     * 支付金额
     */
    private long amount;

    /**
     * 支付渠道返回的异常代码
     */
    private String payErrorCode;
    /**
     * 支付渠道返回的异常msg
     */
    private String payErrorMsg;

}
