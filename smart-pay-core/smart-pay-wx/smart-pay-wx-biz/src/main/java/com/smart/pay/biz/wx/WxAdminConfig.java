package com.smart.pay.biz.wx;

import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;

import java.security.PrivateKey;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/23 15:52
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
public class WxAdminConfig {

    /**
     * 微信app key 秘钥
     */
    public static String WX_PAY_KEY;

    /**
     * 微信v2 秘钥
     */
    public static String WX_PAY_KEY_V2;

    /**
     * 商户id
     */
    public static String MCH_ID = "1604800139";

    /**
     * 证书内容
     */
    public static PrivateKey PRIVATE_KEY;

    /**
     * 证书序列号
     */
    public static String SERIAL_NUMBER;

    /**
     * 证书更新 间隔时间 单位分钟
     */
    public static int updateTime;

    /**
     * 撤销重试次数
     */
    public static int maxCancelRetry=2;
    /**
     * 撤销间隔时间(毫秒)
     */
    public static int cancelDuration=2000;

    public static AutoUpdateCertificatesVerifier verifier;
}
