package com.smart.pay.biz.wx;

import org.apache.http.client.HttpClient;

/**
 * 常量
 */
public class WXPayConstants {

    public static final String DOMAIN_API = "https://api.mch.weixin.qq.com";
    public static final String DOMAIN_API2 = "api2.mch.weixin.qq.com";//备份域名
    public static final String DOMAIN_APIHK = "apihk.mch.weixin.qq.com";//香港微信
    public static final String DOMAIN_APIUS = "apius.mch.weixin.qq.com";


    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";

    public static final String FIELD_SIGN = "sign";

    public static final String WXPAYSDK_VERSION = "WXPaySDK/3.0.9";
    public static final String USER_AGENT = WXPAYSDK_VERSION +
            " (" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") +
            ") Java/" + System.getProperty("java.version") + " HttpClient/" + HttpClient.class.getPackage().getImplementationVersion();

    /**
     * 统一下单接口
     */
    public static final String UNIFIEDORDER_URL_SUFFIX = "/v3/pay/transactions/";
    /**
     * 查询接口
     */
    public static final String QUERY_URL_SUFFIX        = "/v3/pay/transactions/out-trade-no/";
    public static final String QUERY_URL_SUFFIX_id        = "/v3/pay/transactions/id/";
    /**
     * 退款接口
     */
    public static final String REFUND_URL_SUFFIX       = "/v3/refund/domestic/refunds";
    /**
     * 退款查询接口
     */
    public static final String REFUNDQUERY_URL_SUFFIX  = "/v3/refund/domestic/refunds/";
    public static final String REVERSE_URL_SUFFIX      = "/secapi/pay/reverse";




    /**
     * v2 付款码下单接口
     */
    public static final String V2_MICROPAY_URL_SUFFIX="/pay/micropay";
    /**
     * v2 统一下单接口
     */
    public static final String V2_UNIFIEDORDER_URL_SUFFIX = "/pay/unifiedorder";
    /**
     * 查询订单
     */
    public static final String V2_ORDERQUERY_URL_SUFFIX   = "/pay/orderquery";
    public static final String V2_REVERSE_URL_SUFFIX      = "/secapi/pay/reverse";
    public static final String V2_CLOSEORDER_URL_SUFFIX   = "/pay/closeorder";
    /**
     * 申请退款
     */
    public static final String V2_REFUND_URL_SUFFIX       = "/secapi/pay/refund";
    public static final String V2_REFUNDQUERY_URL_SUFFIX  = "/pay/refundquery";
    public static final String V2_DOWNLOADBILL_URL_SUFFIX = "/pay/downloadbill";
    public static final String V2_REPORT_URL_SUFFIX       = "/payitil/report";
    public static final String V2_SHORTURL_URL_SUFFIX     = "/tools/shorturl";
    public static final String V2_AUTHCODETOOPENID_URL_SUFFIX = "/tools/authcodetoopenid";

}

