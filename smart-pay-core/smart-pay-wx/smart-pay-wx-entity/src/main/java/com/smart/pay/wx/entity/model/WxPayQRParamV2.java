package com.smart.pay.wx.entity.model;

import com.smart.pay.model.PayBase;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/22 9:35
 * @see com.smart.pay.wx.entity.model
 * @since 1.0
 **/

public class WxPayQRParamV2 extends PayBase {

    private String qrCode;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
