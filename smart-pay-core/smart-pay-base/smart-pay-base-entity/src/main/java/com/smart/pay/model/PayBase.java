package com.smart.pay.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * <li>支付共同字段</li>
 *
 * @author wangpeng
 * @date 2021/11/16 9:20
 * @see com.smart.pay.model
 * @since 1.0
 **/
@Data
public class PayBase {

    /**
     * 在微信支付注册的 应用ID
     */
    private String appId;

    /**
     * 商户号
     *//*
    private String mchNo;*/

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额,单位分
     */
    private Long amount;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 商品描述信息
     */
    private String detail;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     */
    private String channelUser;

    /**
     * 是否沙箱环境
     */
    private boolean sandbox=false;

    /**
     * 扩展参数
     */
    private String expand;
    /**
     * 利用扩展参数转换成子类 达到入参动态适配
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends PayBase> T convert(Class<T> tClass){
        T object = JSONObject.parseObject(this.getExpand(), tClass);
        if (object == null) {
            try {
                object=tClass.newInstance();
            } catch (Exception e) {
            }
        }
        BeanUtils.copyProperties(this , object);
        return object;
    }

}
