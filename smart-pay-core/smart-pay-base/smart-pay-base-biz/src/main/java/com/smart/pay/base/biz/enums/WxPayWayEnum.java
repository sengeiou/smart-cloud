package com.smart.pay.base.biz.enums;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/16 17:27
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public enum WxPayWayEnum {

    /**
     * 原生扫码支付.
     */
    NATIVE("WX_NATIVE","native","原生扫码支付"),
    APP("WX_APP","app","App支付"),
    JSAPI("WX_JSAPI","jsapi","公众号支付/小程序支付"),
    MWEB("WX_MWEB","MWEB","H5支付."),
    MICROPAY("WX_MICROPAY","micropay","出示二维码支付.");


    WxPayWayEnum(String code, String payCode, String dec) {
        this.code=code;
        this.payCode=payCode;
        this.dec=dec;
    }

    /**
     * 系统中支付代码
     */
    private String code;
    /**
     * 支付方服务代码
     */
    private String payCode;
    /**
     * 描述
     */
    private String dec;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }
}
