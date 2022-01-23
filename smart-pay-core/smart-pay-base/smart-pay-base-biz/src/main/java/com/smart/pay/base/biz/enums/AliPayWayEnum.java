package com.smart.pay.base.biz.enums;

/**
 * <li>阿里支付类型</li>
 *
 * @author wangpeng
 * @date 2021/12/18 13:42
 * @see com.smart.pay.base.biz.enums
 * @since 1.0
 **/
public enum  AliPayWayEnum {

    MICROPAY("Ali_MICROPAY","micropay","扫码枪扫码收款."),
    QRPAY("Ali_QRPAY","qrPay","生成二维码支付.");


    AliPayWayEnum(String code, String payCode, String dec) {
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


    public static AliPayWayEnum getForCode(String code){
        AliPayWayEnum[] values = AliPayWayEnum.values();
        for (AliPayWayEnum value : values) {
            if (value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }


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
