package com.smart.pay.base.biz.utils;

import javafx.util.Pair;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <li>TODO</li>
 *
 * @author wangpeng
 * @date 2021/11/17 14:53
 * @see com.smart.pay.biz.wx.payImpl
 * @since 1.0
 **/

public class HttpClientFactory {

    private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);
    private static int maxTotal = 200;// 连接池最大连接数
    private static int maxPerRoute = 4;// 每个主机的并发
    private static int maxRoute = 50;// 目标主机的最大连接数
    private static ScheduledExecutorService monitorExecutor;
    private static Map<String,PoolingHttpClientConnectionManager> cms=new ConcurrentHashMap<>();

    static {
        final long timeout =  30000L;
        HttpClientFactory.monitorExecutor = Executors.newScheduledThreadPool(1);
        HttpClientFactory.monitorExecutor.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                cms.forEach((key, pool) -> {
                    // 关闭异常连接
                    pool.closeExpiredConnections();
                    // 关闭空闲的连接
                    pool.closeIdleConnections(timeout, TimeUnit.MILLISECONDS);
                    final PoolStats poolStats = pool.getTotalStats();
                    if (poolStats.getPending() > 4){
                        logger.debug(key+"httpPool阻塞连接数:"+poolStats.getPending()
                                +"  空闲连接:"+ poolStats.getAvailable());
                    }
                });


            }
        }, timeout, 5000, TimeUnit.MILLISECONDS);
    }

    public static CloseableHttpClient createHttpClient(HttpClientBuilder httpClientBuilder,String hostname){
        return createHttpClient(httpClientBuilder,hostname,null);
    }

    public static CloseableHttpClient createHttpClient(HttpClientBuilder httpClientBuilder, String hostname,
                                                       Registry<ConnectionSocketFactory> registry){

        if (cms.containsKey(hostname)){
            return httpClientBuilder.setConnectionManager(cms.get(hostname))
                    .setRetryHandler(createRetryHandler()).build();
        }
        if (registry ==null ){
            final ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
            final LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
            registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", plainsf).register("https", sslsf).build();
        }
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        manager.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        manager.setDefaultMaxPerRoute(maxPerRoute);
        cms.put(hostname,manager);

        return httpClientBuilder.setConnectionManager(manager)
                .setRetryHandler(createRetryHandler()).build();
    }


    private static HttpRequestRetryHandler createRetryHandler(){
        return (exception, executionCount, context) -> {
            if (executionCount > 0) {
                //禁用重试
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                logger.info("*******》服务器丢掉连接，重试");
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                logger.info("*******》不要重试SSL握手异常");
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                logger.info("*******》 中断");
                return false;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                logger.info("*******》目标服务器不可达");
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                logger.info("*******》连接超时被拒绝");
                return false;
            }
            if (exception instanceof SSLException) {// SSL握手异常
                logger.info("*******》SSL握手异常");
                return false;
            }

            final HttpClientContext clientContext = HttpClientContext.adapt(context);
            final HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };
    }


}
