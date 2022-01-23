package com.smart.pay.entiity.ali;

import com.smart.pay.model.PayBase;

/**
 * <li>付款码支付</li>
 *
 * @author wangpeng
 * @date 2021/12/23 15:44
 * @see com.smart.pay.entiity.ali
 * @since 1.0
 **/
public class MicroPayParam extends PayBase {
    private String qrCode;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
