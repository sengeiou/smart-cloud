package com.smart.pay.base.biz.enums;

/**
 * <li></li>
 *
 * @author wangpeng
 * @date 2021/12/29 17:20
 * @see com.smart.pay.base.biz.enums
 * @since 1.0
 **/
public enum PaymentStatus {

    WAIT_PAY(10,"支付中"),
    SUCCESS_PAY(11,"支付成功"),
    UNKNOWN_PAY(12,"支付状态未知"),
    FAIL_PAY(13,"支付失败"),
    CLOSE_PAY(20,"订单已关闭"),
    WAIT_REFUND_PAY(30,"退款中"),
    REFUND_PAY(31,"已退款"),
    ;

    //状态
    private Integer status;
    //描述
    private String dec;

    PaymentStatus(Integer status, String dec) {
        this.status=status;
        this.dec=dec;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
}
