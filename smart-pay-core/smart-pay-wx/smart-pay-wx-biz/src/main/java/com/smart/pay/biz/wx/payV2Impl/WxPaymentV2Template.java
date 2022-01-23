package com.smart.pay.biz.wx.payV2Impl;

import com.smart.pay.base.biz.enums.PayNotifyEnum;
import com.smart.pay.base.biz.enums.PayResultCode;
import com.smart.pay.base.biz.enums.WxPayWayEnum;
import com.smart.pay.base.biz.factory.PayStrategy;
import com.smart.pay.base.biz.service.AbstractPaymentListener;
import com.smart.pay.base.biz.service.PayVersion;
import com.smart.pay.base.biz.service.PaymentService;
import com.smart.pay.base.biz.utils.HttpClientFactory;
import com.smart.pay.base.biz.utils.HttpClientPayUtil;
import com.smart.pay.biz.wx.WXPayConstants;
import com.smart.pay.biz.wx.WXPayUtil;
import com.smart.pay.biz.wx.WxAdminConfig;
import com.smart.pay.model.PayQueryBase;
import smart.base.ActionResult;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/18 17:40
 * @see com.smart.pay.biz.wx.payV2Impl
 * @since 1.0
 **/
public abstract class WxPaymentV2Template extends AbstractPaymentListener implements PaymentService {

    private static final long serialVersionUID = 8720690238507101611L;
    protected static Logger logger= LoggerFactory.getLogger(WxPaymentV2Template.class);
    //沙箱环境
    protected static String WX_PAY_SANDBOX = "/sandboxnew";

    protected String apiVersion="2.0";
    protected String code;

    protected HttpClientPayUtil httpClient;

    public WxPaymentV2Template(WxPayWayEnum wayEnum) {
        this.code = wayEnum.getPayCode();
        try {
            init();
        } catch (Exception e) {

        }
        PayStrategy.addPayInstance(wayEnum.getCode(), this);
    }


    protected  void init()throws Exception{
        // 证书
        char[] password = WxAdminConfig.MCH_ID.toCharArray();
        InputStream certStream  = Thread.currentThread().getContextClassLoader().getResourceAsStream("cet/apiclient_cert.p12");
        KeyStore keystore = KeyStore.getInstance("PKCS12");//p12格式证书
        //证书密码
        keystore.load(certStream, password);//加载
        SSLContext sslContexts  = SSLContexts.custom().loadKeyMaterial(keystore, password).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContexts,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        Registry<ConnectionSocketFactory> build = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();

        CloseableHttpClient httpClients = HttpClientFactory.createHttpClient(HttpClientBuilder.create(), WXPayConstants.DOMAIN_API, build);
        this.httpClient =new HttpClientPayUtil(httpClients);
    }




    /**
     * 查询支付订单
     *
     * @param queryParam
     * @return
     */
    @Override
    public ActionResult invokeQuery(PayQueryBase queryParam) {
        Map<String, String> param = new HashMap<>();
        param.put("appid", queryParam.getAppId());
        param.put("mch_id", WxAdminConfig.MCH_ID);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("sign_type", "MD5");
        param.put("out_trade_no", queryParam.getOrderNo());
        try {
            logger.info("微信支付查询'{}':入参{}",code,param);
            ActionResult<Map<String, String>> mapActionResult = httpXmlPost(
                    WXPayConstants.V2_ORDERQUERY_URL_SUFFIX, param, queryParam.isSandbox());
            if (mapActionResult.isSuccess()) {
                Map<String, String> data = mapActionResult.getData();
                String trade_state = data.get("trade_state");
                if ("SUCCESS".equals(trade_state)) {
                    // 成功
                    return ActionResult.success("支付成功");
                } else if ("REFUND".equals(trade_state)) {
                    // 转入退款
                    return ActionResult.fail("正在退款");
                } else if ("NOTPAY".equals(trade_state)) {
                    // 未支付
                    return ActionResult.fail("订单未支付");
                } else if ("CLOSED".equals(trade_state)) {
                    // 已关闭
                    return ActionResult.initialization(PayResultCode.FailClosed);
                } else if ("REVOKED".equals(trade_state)) {
                    // 已撤销（刷卡支付）
                    return ActionResult.fail("已撤销");
                } else if ("USERPAYING".equals(trade_state)) {
                    // 用户支付中
                    return ActionResult.fail("支付中");
                } else if ("PAYERROR".equals(trade_state)) {
                    // 支付失败(其他原因，如银行返回失败)
                    return ActionResult.fail("支付失败");
                } else {
                    return ActionResult.fail(data.get("trade_state_desc"));
                }
            }
            return mapActionResult;
        } catch (Exception e) {
            logger.error("微信付款码查询异常 :", e);
        }
        return ActionResult.fail("系统异常!请重试");

    }




    /**
     * 获取回调通知
     * @return
     */
    protected String getNOTIFY_URL(){
        return WXPayConstants.DOMAIN_API+ PayNotifyEnum.wx_pay.getUrl()+apiVersion+"/"+code;
    }
    /**
     * 获取退款回调通知
     * @return
     */
    protected String getNotify_refund_url(){
        return WXPayConstants.DOMAIN_API+ PayNotifyEnum.wx_refund.getUrl()+apiVersion+"/"+code;
    }

    /**
     * v2模板发送post请求
     * @param uri
     * @param params
     * @param isSandbox
     * @return
     * @throws Exception
     */
    protected ActionResult<Map<String, String>> httpXmlPost(String uri,Map<String,String> params,
                                                            boolean isSandbox) throws Exception {
        String url;
        if (isSandbox){
            url= WXPayConstants.DOMAIN_API +WX_PAY_SANDBOX+uri;
        }else {
            url=WXPayConstants.DOMAIN_API +uri;
        }
        String xmlParam = WXPayUtil.generateSignedXml(params,WxAdminConfig.WX_PAY_KEY_V2);
        HashMap<String, String> header = new HashMap<>();
        header.put("ContentType", "text/xml");
        header.put("User-Agent", WXPayConstants.USER_AGENT + " " + WxAdminConfig.MCH_ID);
        String responseStr= httpClient.httpPost(url,
                xmlParam,header);
        return processResponseXml(responseStr);
    }



    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    private ActionResult<Map<String, String>> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            //微服务通信标识字段失败 业务状态未知
            logger.error("微信支付异常,No `return_code` in XML:{}",xmlStr);
            return ActionResult.initialization(PayResultCode.FailUnknown);
        }


        if (!WXPayConstants.SUCCESS.equals(return_code)){
            logger.error("微信支付失败,return_code:{} \n XML:{}",return_code,xmlStr);
            return ActionResult.initialization(PayResultCode.FailUnknown);
            //return ActionResult.fail("wx error:"+respData.get("return_msg"));
        }

        //验证签名
        if (WXPayUtil.isSignatureValid(respData, WxAdminConfig.WX_PAY_KEY_V2)) {
            String result_code = respData.get("result_code");
            if (!WXPayConstants.SUCCESS.equals(result_code)){
                logger.error("微信支付失败,result_code:{} \n XML:{}",result_code,xmlStr);
                return ActionResult.fail("wx error:"+respData.get("err_code_des"));
            }
            //成功
            return ActionResult.success(respData);
        } else {
            //返回结果签名失败有可能被盗链
            return ActionResult.fail("wx返回签名失败!");
        }
    }


    /**
     * 获取当前版本号
     *
     * @return
     */
    @Override
    public String currentVersion() {
        return this.apiVersion;
    }
}
