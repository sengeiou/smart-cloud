package com.smart.pay.biz.ali.utils;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.smart.pay.biz.ali.AliPayConstants;
import smart.util.Constants;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/23 17:02
 * @see com.smart.pay.biz.ali.utils
 * @since 1.0
 **/
public class PayResultStatusUtil {

    // 查询返回“支付成功”
    public static boolean querySuccess(AlipayTradeQueryResponse response) {
        return AliPayConstants.SUCCESS.equals(response.getCode()) &&
                ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                        "TRADE_FINISHED".equals(response.getTradeStatus())
                );
    }

    public static boolean refundSuccess(AlipayTradeFastpayRefundQueryResponse response) {
        return AliPayConstants.SUCCESS.equals(response.getCode()) &&
                ("REFUND_SUCCESS".equals(response.getRefundStatus()));
    }

    // 撤销返回“撤销成功”
    public static boolean cancelSuccess(AlipayTradeCancelResponse response) {
        return response != null &&
                AliPayConstants.SUCCESS.equals(response.getCode());
    }

    public static boolean success(AlipayResponse response) {
        return response != null &&
                AliPayConstants.SUCCESS.equals(response.getCode());
    }

    // 交易异常，或发生系统错误
    public static boolean tradeError(AlipayResponse response) {
        return response == null ||
                AliPayConstants.ERROR.equals(response.getCode());
    }


    // 撤销需要重试
    public static boolean needRetry(AlipayTradeCancelResponse response) {
        return response == null ||
                "Y".equals(response.getRetryFlag());
    }
}
