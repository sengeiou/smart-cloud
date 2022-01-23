package com.smart.pay.biz.ali.utils;


import com.smart.pay.base.biz.utils.HttpClientFactory;
import com.smart.pay.base.biz.utils.HttpClientPayUtil;
import com.smart.pay.biz.ali.AliPayConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/12/20 16:27
 * @see com.smart.pay.biz.ali.utils
 * @since 1.0
 **/
public class AliHttpClient{

    private static SSLContext ctx = null;

    private static SSLSocketFactory socketFactory = null;


    private static  HttpClientPayUtil httpClient ;

    static {
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new AliHttpClient.DefaultTrustManager()},
                    new SecureRandom());
            ctx.getClientSessionContext().setSessionTimeout(15);
            ctx.getClientSessionContext().setSessionCacheSize(1000);
            socketFactory = ctx.getSocketFactory();

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(ctx,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            Registry<ConnectionSocketFactory> build = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();

            CloseableHttpClient httpClients = HttpClientFactory.createHttpClient(HttpClientBuilder.create(), AliPayConfig.getOpenApiDomain(), build);
            httpClient =new HttpClientPayUtil(httpClients);
        } catch (Exception e) {

        }
    }


    protected static HttpClientPayUtil httpClient(){
        return httpClient;
    }
    /*conn.setRequestProperty("Accept", "text/plain,text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "aop-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);*/







    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    }
}
