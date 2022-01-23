package com.smart.pay.base.biz.service;

import com.smart.pay.base.biz.enums.PaymentStatus;
import com.smart.pay.base.biz.enums.PaymentEvent;
import smart.util.StringUtil;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2022/1/5 10:30
 * @see com.smart.pay.base.biz.service
 * @since 1.0
 **/
public abstract class AbstractPaymentListener implements PayVersion{
    private static final long serialVersionUID = -2027694638333900002L;

    private PaymentListener paymentListener;

    private PayVersion beforeVersion;

    public void registerListener(PaymentListener paymentListener) {
        this.paymentListener = paymentListener;
    }

    protected PaymentListener getListener(){
        return paymentListener;
    }

    /**
     * 触发监听
     * @param event 消息
     */
    protected void sendListenerMessage(PaymentEvent event){
        if (paymentListener ==null)
            return;

        paymentListener.notifyListener(event);
    }


    /**
     * 发送通知消息给监听器
     * @param status 支付状态
     * @param param 入参
     * @param code 支付代码
     * @param request 第三方请求入参
     * @param response 第三方返回
     */
    protected void sendListenerMessage(PaymentStatus status,Object param, String code
            ,String request,String response){
        if (paymentListener ==null)
            return;
        PaymentEvent event=new PaymentEvent();
        event.setType(status);
        event.setMessage(param);
        event.setCodeStr(code);
        event.setRequestStr(request);
        event.setResponseStr(response);
        paymentListener.notifyListener(event);
    }


    protected void setBeforeVersion(PayVersion version){
        this.beforeVersion=version;
    }

    @Override
    public <T> T  findVersion(String version) {
        if (StringUtil.isEmpty(version) || this.beforeVersion== null){
            return (T) this;
        }

        if (this.currentVersion().equals(version))
            return (T) this;

        return beforeVersion.findVersion(version);
    }

    /**
     * 获取当前版本号
     * @return
     */
    public abstract String currentVersion();
}
