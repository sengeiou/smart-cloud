package com.smart.pay.base.biz.enums;

/**
 * <li>支付事件</li>
 *
 * @author wangpeng
 * @date 2021/12/24 11:37
 * @see com.smart.pay.model
 * @since 1.0
 **/
public class PaymentEvent {
    /**
     * 事件类型
     */
    private PaymentStatus type;
    /**
     * 本系统入参
     */
    private Object message;

    /**
     * 第三方入参
     */
    private String requestStr;
    /**
     * 第三方应答
     */
    private String responseStr;

    /**
     * 支付代码
     */
    private String codeStr;

    public PaymentStatus getType() {
        return type;
    }

    public void setType(PaymentStatus type) {
        this.type = type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getRequestStr() {
        return requestStr;
    }

    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }

    public String getCodeStr() {
        return codeStr;
    }

    public void setCodeStr(String codeStr) {
        this.codeStr = codeStr;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }
}
