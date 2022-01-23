package com.smart.pay.biz.ali;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by liuyangkly on 15/6/27.
 */
public class AliPayConfig {
    private static Log log = LogFactory.getLog(AliPayConfig.class);
    private static Configuration configs;

    /**
     * 支付宝openapi域名
     */
    private static String openApiDomain;
    /**
     * 支付宝mcloudmonitor域名
     */
    private static String mcloudApiDomain;
    /**
     * 商户partner id
     */
    private static String pid;
    /**
     * 应用id
     */
    private static String appid;
    /**
     * RSA私钥，用于对商户请求报文加签
     */
    private static String privateKey;
    /**
     * RSA公钥，仅用于验证开发者网关
     */
    private static String publicKey;
    /**
     * 支付宝RSA公钥，用于验签支付宝应答
     */
    private static String alipayPublicKey;
    /**
     * 签名类型
     */
    private static String signType;

    /**
     * 报文格式，推荐：json
     */
    private static String format = "json";

    /**
     * 字符串编码，推荐：utf-8
     */
    private static String charset = "utf-8";

    private static int maxQueryRetry =2;   // 最大查询次数
    private static long queryDuration =1000;  // 查询间隔（毫秒）

    private static int maxCancelRetry=3;  // 最大撤销次数
    private static long cancelDuration=2000; // 撤销间隔（毫秒）

    private static long heartbeatDelay ; // 交易保障线程第一次调度延迟（秒）
    private static long heartbeatDuration ; // 交易保障线程调度间隔（秒）

    private AliPayConfig() {
        // No Constructor
    }

    // 根据文件名读取配置文件，文件后缀名必须为.properties
    public synchronized static void init(String filePath) {
        if (configs != null) {
            return;
        }

        try {
            configs = new PropertiesConfiguration(filePath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        if (configs == null) {
            throw new IllegalStateException("can`t find file by path:" + filePath);
        }

        openApiDomain = configs.getString("open_api_domain");
        mcloudApiDomain = configs.getString("mcloud_api_domain");

        pid = configs.getString("pid");
        appid = configs.getString("appid");

        // RSA
        privateKey = configs.getString("private_key");
        publicKey = configs.getString("public_key");
        alipayPublicKey = configs.getString("alipay_public_key");
        signType = configs.getString("sign_type");

        // 查询参数
        maxQueryRetry = configs.getInt("max_query_retry");
        queryDuration = configs.getLong("query_duration");
        maxCancelRetry = configs.getInt("max_cancel_retry");
        cancelDuration = configs.getLong("cancel_duration");

        // 交易保障调度线程
        heartbeatDelay = configs.getLong("heartbeat_delay");
        heartbeatDuration = configs.getLong("heartbeat_duration");

        log.info("配置文件名: " + filePath);
        log.info(description());
    }

    public static String description() {
        StringBuilder sb = new StringBuilder("Configs{");
        sb.append("支付宝openapi网关: ").append(openApiDomain).append("\n");
        if (StringUtils.isNotEmpty(mcloudApiDomain)) {
            sb.append(", 支付宝mcloudapi网关域名: ").append(mcloudApiDomain).append("\n");
        }

        if (StringUtils.isNotEmpty(pid)) {
            sb.append(", pid: ").append(pid).append("\n");
        }
        sb.append(", appid: ").append(appid).append("\n");

        sb.append(", 商户RSA私钥: ").append(getKeyDescription(privateKey)).append("\n");
        sb.append(", 商户RSA公钥: ").append(getKeyDescription(publicKey)).append("\n");
        sb.append(", 支付宝RSA公钥: ").append(getKeyDescription(alipayPublicKey)).append("\n");
        sb.append(", 签名类型: ").append(signType).append("\n");

        sb.append(", 查询重试次数: ").append(maxQueryRetry).append("\n");
        sb.append(", 查询间隔(毫秒): ").append(queryDuration).append("\n");
        sb.append(", 撤销尝试次数: ").append(maxCancelRetry).append("\n");
        sb.append(", 撤销重试间隔(毫秒): ").append(cancelDuration).append("\n");

        sb.append(", 交易保障调度延迟(秒): ").append(heartbeatDelay).append("\n");
        sb.append(", 交易保障调度间隔(秒): ").append(heartbeatDuration).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private static String getKeyDescription(String key) {
        int showLength = 6;
        if (StringUtils.isNotEmpty(key) &&
                key.length() > showLength) {
            return new StringBuilder(key.substring(0, showLength))
                    .append("******")
                    .append(key.substring(key.length() - showLength))
                    .toString();
        }
        return null;
    }

    public static Configuration getConfigs() {
        return configs;
    }

    public static String getOpenApiDomain() {
        return openApiDomain;
    }

    public static String getMcloudApiDomain() {
        return mcloudApiDomain;
    }

    public static void setMcloudApiDomain(String mcloudApiDomain) {
        AliPayConfig.mcloudApiDomain = mcloudApiDomain;
    }

    public static String getPid() {
        return pid;
    }

    public static String getAppid() {
        return appid;
    }

    public static String getPrivateKey() {
        return privateKey;
    }

    public static String getPublicKey() {
        return publicKey;
    }

    public static String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public static String getSignType() {
        return signType;
    }

    public static int getMaxQueryRetry() {
        return maxQueryRetry;
    }

    public static long getQueryDuration() {
        return queryDuration;
    }

    public static int getMaxCancelRetry() {
        return maxCancelRetry;
    }

    public static long getCancelDuration() {
        return cancelDuration;
    }

    public static void setConfigs(Configuration configs) {
        AliPayConfig.configs = configs;
    }

    public static void setOpenApiDomain(String openApiDomain) {
        AliPayConfig.openApiDomain = openApiDomain;
    }

    public static void setPid(String pid) {
        AliPayConfig.pid = pid;
    }

    public static void setAppid(String appid) {
        AliPayConfig.appid = appid;
    }

    public static void setPrivateKey(String privateKey) {
        AliPayConfig.privateKey = privateKey;
    }

    public static void setPublicKey(String publicKey) {
        AliPayConfig.publicKey = publicKey;
    }

    public static void setAlipayPublicKey(String alipayPublicKey) {
        AliPayConfig.alipayPublicKey = alipayPublicKey;
    }

    public static void setSignType(String signType) {
        AliPayConfig.signType = signType;
    }

    public static void setMaxQueryRetry(int maxQueryRetry) {
        AliPayConfig.maxQueryRetry = maxQueryRetry;
    }

    public static void setQueryDuration(long queryDuration) {
        AliPayConfig.queryDuration = queryDuration;
    }

    public static void setMaxCancelRetry(int maxCancelRetry) {
        AliPayConfig.maxCancelRetry = maxCancelRetry;
    }

    public static void setCancelDuration(long cancelDuration) {
        AliPayConfig.cancelDuration = cancelDuration;
    }

    public static long getHeartbeatDelay() {
        return heartbeatDelay;
    }

    public static void setHeartbeatDelay(long heartbeatDelay) {
        AliPayConfig.heartbeatDelay = heartbeatDelay;
    }

    public static long getHeartbeatDuration() {
        return heartbeatDuration;
    }

    public static void setHeartbeatDuration(long heartbeatDuration) {
        AliPayConfig.heartbeatDuration = heartbeatDuration;
    }

    public static String getFormat() {
        return format;
    }

    public static void setFormat(String format) {
        AliPayConfig.format = format;
    }

    public static String getCharset() {
        return charset;
    }

    public static void setCharset(String charset) {
        AliPayConfig.charset = charset;
    }
}

