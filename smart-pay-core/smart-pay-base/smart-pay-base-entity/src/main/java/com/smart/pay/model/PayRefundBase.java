package com.smart.pay.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * <li>退款入参</li>
 *
 * @author wangpeng
 * @date 2021/11/17 19:43
 * @see com.smart.pay.model
 * @since 1.0
 **/
@Data
public class PayRefundBase {

    /**
     * 生成的订单号
     */
    private String orderNo;

    /**
     * 原支付交易订单号
     */
    private String refundOrderNo;

    /**
     * 原支付交易的订单总金额
     */
    private Long amount;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款金额
     */
    private Long refundAmount;

    /**
     * 扩展参数
     */
    private String expand;

    /**
     * 是否沙箱环境
     */
    private boolean sandbox=false;


    /**
     * 利用扩展参数转换成子类 达到入参动态适配
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends PayRefundBase> T convert(Class<T> tClass){
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
