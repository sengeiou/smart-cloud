package com.smart.pay.biz.wx;

import com.smart.pay.biz.wx.notify.WxPayNotifyImpl;
import com.smart.pay.biz.wx.notify.refunds.WxPayRefundNotifyImpl;
import com.smart.pay.biz.wx.notify.refunds.WxPayRefundNotifyV2Impl;
import com.smart.pay.biz.wx.payImpl.WxPayJsapiImpl;
import com.smart.pay.biz.wx.payImpl.WxPayNativeImpl;
import com.smart.pay.biz.wx.payV2Impl.WxPayV2MicropayImpl;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.InputStream;

/**
 * <li>配置类</li>
 *
 * @author wangpeng
 * @date 2021/11/22 15:16
 * @see com.smart.pay.biz.wx
 * @since 1.0
 **/
@Configuration
@Order
public class WxPayAutoConfiguration {

    @Bean
    public ApplicationRunner initData2(){
        return (args) -> {
            WxAdminConfig.MCH_ID="1604187129";
            WxAdminConfig.WX_PAY_KEY="PejXZwlTt6oKS42DSF50dKzBmpMK2SF0";
            WxAdminConfig.WX_PAY_KEY_V2="LaiTingKeJi20201116GongXiFaCaiHX";
            WxAdminConfig.SERIAL_NUMBER="5A0FCF9CE4E8D8C35C488F4F73C0BD9F0E45722D";
            String path="cet/apiclient_key.pem";
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            WxAdminConfig.PRIVATE_KEY= PemUtil.loadPrivateKey(resourceAsStream);
            WxAdminConfig.updateTime=120;
            WxKeySigner wxKeySigner = new WxKeySigner(WxAdminConfig.SERIAL_NUMBER, WxAdminConfig.PRIVATE_KEY);
            WxAdminConfig.verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(WxAdminConfig.MCH_ID, wxKeySigner),
                    WxAdminConfig.WX_PAY_KEY.getBytes("utf-8"),
                    WxAdminConfig.updateTime);

            new WxPayV2MicropayImpl();
            new WxPayJsapiImpl();
            new WxPayNativeImpl();
            new WxPayNotifyImpl();
            new WxPayRefundNotifyImpl();
            new WxPayRefundNotifyV2Impl();


        };
    }

}
