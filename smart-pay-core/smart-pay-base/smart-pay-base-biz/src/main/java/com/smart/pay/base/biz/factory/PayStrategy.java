package com.smart.pay.base.biz.factory;


import com.smart.pay.api.PaymentApi;
import com.smart.pay.base.biz.service.PayNotifyService;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.base.biz.utils.SpringContextUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <li>支付策略类</li>
 *
 * @author wangpeng
 * @date 2021/11/16 11:08
 * @see com.smart.pay.api
 * @since 1.0
 **/
public class PayStrategy {

    private static final ConcurrentMap<String, PayVersion> PAY_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, PayVersion> NOTIFY_MAP = new ConcurrentHashMap<>();

    /**
     * <pre>
     *  获取支付实例代码
     * </pre>
     *
     * @param code 实例代码
     * @return com.smart.pay.api.PaymentHandler  or null
     * @author wangpeng
     * @date 2021/11/16
     **/
    public static PayVersion getPayInstance(String code){
        return PAY_MAP.get(code);
    }


    public static void addPayInstance(String wayEnum, PayVersion beanName){
        PAY_MAP.put(wayEnum,beanName);
    }


    public static PayVersion getNotifyInstance(String code){
        return NOTIFY_MAP.get(code);
    }


    public static void addNotifyInstance(String code, PayVersion beanName){
        NOTIFY_MAP.put(code,beanName);
    }
}
