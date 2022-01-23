package com.smart.pay.wx.entity.model;

import lombok.Data;

/**
 * <li>验签的http 请求头信息</li>
 *
 * @author wangpeng
 * @date 2021/11/23 17:44
 * @see com.smart.pay.wx.entity.model
 * @since 1.0
 **/
@Data
public class SignHeader {
    /**
     * 时间戳
     */
    private String timeStamp;
    /**
     * 随机串
     */
    private String nonce;
    /**
     * 已签名字符串
     */
    private String signature;
    /**
     * 证书序列号
     */
    private String serial;
}
