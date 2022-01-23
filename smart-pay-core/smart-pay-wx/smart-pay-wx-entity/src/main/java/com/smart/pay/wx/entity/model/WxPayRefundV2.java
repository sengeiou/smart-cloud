package com.smart.pay.wx.entity.model;

import com.smart.pay.model.PayRefundBase;
import lombok.Data;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/19 16:23
 * @see com.smart.pay.wx.entity.model
 * @since 1.0
 **/
@Data
public class WxPayRefundV2 extends PayRefundBase {

    private String appId;
}
