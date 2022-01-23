package com.smart.pay.base.biz.enums;

import smart.base.ActionResultCode;
import smart.base.ApplicationCode;

/***
 * <pre>
 *  支付错误代码
 * </pre>
 *
 * @throws
 * @author wangpeng
 * @date 2021/12/23
 **/
public enum PayResultCode implements ApplicationCode {


    FailUnknown(30001,"交易状态未知,请人工查询"),
    FailClosed(30001,"订单已关闭"),

    SessionError(602, "Token验证失败");

    private int code;
    private String message;

    PayResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
