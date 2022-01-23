package com.smart.pay.base.biz.service;

import com.smart.pay.api.PaymentApi;

import java.io.Serializable;

/**
 * <li>版本控制器</li>
 *
 * @author wangpeng
 * @date 2021/11/24 14:31
 * @see com.smart.pay.base.biz.service
 * @since 1.0
 **/
public interface PayVersion extends Serializable {

    public <T> T  findVersion(String version);
}
