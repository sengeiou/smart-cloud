package com.smart.pay.base.biz.enums;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/16 19:04
 * @see com.smart.pay.base.biz.enums
 * @since 1.0
 **/
public enum PayNotifyEnum {

    wx_pay("wx_pay","/api/pay/notify/wx_pay/","微信通知回调"),
    ali_pay("ali_pay","/api/pay/notify/ali_pay/","支付宝支付通知回调"),
    wx_refund("wx_refund","/api/refund/notify/wx_refund/","微信退款通知回调"),
    ali_refund("ali_refund","/api/refund/notify/ali_refund/","支付宝退款通知回调");

    private String code;
    private String url;
    private String dec;

    PayNotifyEnum(String code,String url, String dec) {
        this.code = code;
        this.url = url;
        this.dec = dec;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
