/*
package com.smart.pay.base.biz.test;

import com.alibaba.fastjson.JSONObject;
import smart.smartPayCoreApplication;
import smart.base.ActionResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

*/
/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/19 17:30
 * @see com.smart.payImpl
 * @since 1.0
 **//*



@RunWith(SpringRunner.class)
@SpringBootTest(classes = JnpfPayCoreApplication.class)
public class WxPayV2MicroPayTest {



    */
/**
     * 统一订单支付
     *
     * @return
     *//*

    @Test
    public void invokePayment() {
        */
/*com.smart.pay.model.PayBase payBase=new PayBase();
        payBase.setSandbox(true);
        payBase.setAppId("11");
        payBase.setAmount(1L);
        payBase.setDetail("新微信支付,测试数据");
        payBase.setSubject("新微信测试");
        payBase.setOrderNo(UUID.randomUUID().toString().replace("-",""));
        payBase.setClientIp("127.0.0.1");
        payBase.setExpand("{\"qrCode\": \"134845796253426580\"}");
        PaymentApi paymentApi = PayFactory.createPay(WxPayWayEnum.MICROPAY.getCode());
        ActionResult actionResult = paymentApi.invokePayment(payBase);
        System.out.println(JSONObject.toJSONString(actionResult));*//*

    }

    */
/**
     * 查询支付订单
     *
     * @param queryParam
     * @return
     *//*

    public ActionResult invokeQuery(PayQueryBase queryParam) {
        return null;
    }

    */
/**
     * 退款
     *
     * @param refundParam
     * @return
     *//*

    public ActionResult invokeRefund(PayRefundBase refundParam) {
        return null;
    }

    */
/**
     * 退款订单查询
     *
     * @param refundParam
     * @return
     *//*

    public ActionResult invokeQueryRefund(PayRefundBase refundParam) {
        return null;
    }
}
*/
