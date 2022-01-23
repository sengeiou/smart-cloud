package com.smart.pay.biz.ali;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/23 16:14
 * @see com.smart.pay.biz.ali.utils
 * @since 1.0
 **/
public class AliPayConstants {
    private AliPayConstants() {
        // No Constructor.
    }
    public static final String SUCCESS = "10000"; // 成功
    public static final String PAYING  = "10003"; // 用户支付中
    public static final String FAILED  = "40004"; // 失败
    public static final String ERROR   = "20000"; // 系统异常
}
