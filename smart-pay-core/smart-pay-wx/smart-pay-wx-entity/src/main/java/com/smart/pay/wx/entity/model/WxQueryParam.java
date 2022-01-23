package com.smart.pay.wx.entity.model;

import com.smart.pay.model.PayQueryBase;
import lombok.Data;

/**
 * <li>微信订单查询入参</li>
 *
 * @author wangpeng
 * @date 2021/11/17 17:56
 * @see com.smart.pay.wx.entity.model
 * @since 1.0
 **/
@Data
public class WxQueryParam extends PayQueryBase {

    /**
     * 微信支付系统生成的订单号
     * 示例值：1217752501201407033233368018
     */
    private String transactionId;
}
