package com.smart.pay.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/17 18:03
 * @see com.smart.pay.model
 * @since 1.0
 **/
@Data
public class PayQueryBase {

    private String appId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 扩展参数
     */
    private String expand;

    private boolean sandbox;

    /**
     * 利用扩展参数转换成子类 达到入参动态适配
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends PayQueryBase> T convert(Class<T> tClass){
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
