package com.smart.pay.model;

import lombok.Data;

/**
 * <li>通知退款结果</li>
 *
 * @author wangpeng
 * @date 2021/11/23 16:31
 * @see com.smart.pay.model
 * @since 1.0
 **/
@Data
public class PayRefundNotifyResult {

    /**
     * 是否退款成功
     */
    private boolean success;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 退款用户
     */
    private String refundUser;

    /**
     * 退款金额
     */
    private long refundAmount;

    /**
     * 支付渠道返回的异常代码
     */
    private String payErrorCode;
    /**
     * 支付渠道返回的异常msg
     */
    private String payErrorMsg;

}
