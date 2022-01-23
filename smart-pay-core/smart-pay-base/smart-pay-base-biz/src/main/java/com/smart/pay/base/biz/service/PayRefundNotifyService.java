package com.smart.pay.base.biz.service;

import com.smart.pay.model.PayNotifyResult;
import com.smart.pay.model.PayRefundNotifyResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <li>统一退款通知</li>
 *
 * @author wangpeng
 * @date 2021/11/23 17:08
 * @see com.smart.pay.base.biz.service
 * @since 1.0
 **/
public interface PayRefundNotifyService {


    /**
     * 验证签名, 验证退款金额, 结果转换成PayNotifyResult
     * @return
     */
    public PayRefundNotifyResult invokeWork(HttpServletRequest request);


    /**
     * 返回接口
     * @param response
     * @param isSuccess TRUE is 成功返回
     */
    public void writeResponse (HttpServletResponse response,boolean isSuccess);
}
