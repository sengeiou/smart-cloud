package com.smart.pay.model;

/**
 * <li>微信支付入参</li>
 *
 * @author wangpeng
 * @date 2021/11/16 9:27
 * @see com.smart.pay.model
 * @since 1.0
 **/
public class WXPay extends PayBase{

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 商品详情
     */
    private String body;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 调用微信支付API的机器IP
     */
    private String spbillCreateIp;

    /**
     * 交易类型JSAPI--JSAPI支付（或小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付，
     *
     * MICROPAY--付款码支付
     */
    private String tradeType;


}
