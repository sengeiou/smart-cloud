package com.smart.pay.base.biz.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <li>http请求工具类</li>
 *
 * @author wangpeng
 * @date 2021/11/17 15:54
 * @see com.smart.pay.base.biz.utils
 * @since 1.0
 **/

public class HttpClientPayUtil {
    private static final Logger logger= LoggerFactory.getLogger(HttpClientPayUtil.class);

    private static final String DEFAULT_ENCODING = "UTF-8";// Charset.defaultCharset().name();
    private static int connectPoolTimeout = 2000;// 设定从连接池获取可用连接的时间
    private static int connectTimeout = 2000;// 建立连接超时时间
    private static int socketTimeout = 5000;// 设置等待数据超时时间5秒 根据业务调整
    private CloseableHttpClient httpClient;
    private RequestConfig requestConfig;

    public HttpClientPayUtil(CloseableHttpClient httpClient){
        this.httpClient =httpClient;
        this.requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(HttpClientPayUtil.connectPoolTimeout)// 设定从连接池获取可用连接的时间
                .setConnectTimeout(HttpClientPayUtil.connectTimeout)// 设定连接服务器超时时间
                .setSocketTimeout(socketTimeout)// 设定获取数据的超时时间
                .build();
    }



    /**
     * post 请求
     * @param url 地址
     * @param params 请求入参
     * @param header 请求头
     * @param socketTimeout 获取超时时间(毫秒)
     * @return
     */
    public String httpPost(final String url, final Map<String, Object> params, final Map<String,String> header, int socketTimeout) {
        final HttpPost httpPost = new HttpPost(url);
        if (!CollectionUtils.isEmpty(header)){
            header.forEach(httpPost::setHeader);
        }
        this.setPostParams(httpPost, params);
        return this.httpMethod(httpPost, socketTimeout);
    }


    /**
     * post 请求
     * @param url 地址
     * @param params 请求入参
     * @param header 请求头
     * @return
     */
    public String httpPost(final String url, final Map params, final Map<String,String> header) {
        final HttpPost httpPost = new HttpPost(url);
        if (!CollectionUtils.isEmpty(header)){
            header.forEach(httpPost::setHeader);
        }
        this.setPostParams(httpPost, params);
        return this.httpMethod(httpPost, socketTimeout);
    }


    /**
     * post 请求
     * @param url 地址
     * @param params 入参
     * @param header 请求头
     * @return
     */
    public String httpPost(final String url, String params ,final Map<String,String> header){
        return this.httpPost(url,params,header,0);
    }

    /**
     * post 请求
     * @param url 地址
     * @param params 入参
     * @return
     */
    public String httpPost(final String url, String params){
        final HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        HttpEntity entity = new StringEntity(params, DEFAULT_ENCODING);
        httpPost.setEntity(entity);
        return this.httpMethod(httpPost, socketTimeout);
    }
    /**
     * post 请求
     * @param url 地址
     * @param params 入参
     * @param header 请求头
     * @param socketTimeout 超时时间
     * @return
     */
    public String httpPost(final String url, String params ,final Map<String,String> header,int socketTimeout){
        final HttpPost httpPost = new HttpPost(url);
        if (!CollectionUtils.isEmpty(header)){
            header.forEach(httpPost::addHeader);
        }
        HttpEntity entity = new StringEntity(params, DEFAULT_ENCODING);
        httpPost.setEntity(entity);
        return this.httpMethod(httpPost, socketTimeout);
    }

    /**
     * get 请求
     * @param url 地址
     * @param params 请求入参
     * @param header 请求头
     * @param socketTimeout 获取超时时间(毫秒)
     * @return
     */
    public String httpGet(final String url,final Map<String, Object> params,final Map<String,String> header,int socketTimeout) {
        final HttpGet httpGet = new HttpGet(url);
        if (!CollectionUtils.isEmpty(header)){
            header.forEach(httpGet::setHeader);
        }
        this.setParams(httpGet, params);
        return this.httpMethod(httpGet, socketTimeout);
    }

    public String httpGet(final String url,final Map<String, Object> params) {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Content-type","application/json; charset=utf-8");
        this.setParams(httpGet, params);
        return this.httpMethod(httpGet, socketTimeout);
    }

    /**
     * get请求
     * @param url 地址
     * @param params 入参
     * @param tClass 类型
     * @return
     */
    public <T> T httpGet(final String url,final Map<String, Object> params, Class<T> tClass){
        String result=httpGet(url,params,null,0);
        return JSONObject.parseObject(result,tClass);
    }



    private void setPostParams(final HttpPost httpost, final Map<String, Object> params) {
        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
        final Set<String> keySet = params.keySet();
        for (final String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HttpClientPayUtil.DEFAULT_ENCODING));
        } catch (Exception e) {
            logger.error("参数设置异常",e);
        }
    }

    private void setParams(final HttpRequestBase httpbase, final Map<String, Object> params) {
        try {
            if (params != null && params.size() > 0) {
                final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
                final Set<String> keySet = params.keySet();
                for (final String key : keySet) {
                    nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
                final String param = EntityUtils
                        .toString(new UrlEncodedFormEntity(nvps, HttpClientPayUtil.DEFAULT_ENCODING));
                httpbase.setURI(new URI(httpbase.getURI().toString() + "?" + param));
            }
        } catch (Exception e) {
            logger.error("参数设置异常",e);
        }
    }

    private String httpMethod(final HttpRequestBase httpBase, int socketTimeout) {
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            socketTimeout=socketTimeout <=0 ?HttpClientPayUtil.socketTimeout:socketTimeout;
            if (socketTimeout <=0){
                httpBase.setConfig(requestConfig);
            }else {
                httpBase.setConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(HttpClientPayUtil.connectPoolTimeout)// 设定从连接池获取可用连接的时间
                        .setConnectTimeout(HttpClientPayUtil.connectTimeout)// 设定连接服务器超时时间
                        .setSocketTimeout(socketTimeout)// 设定获取数据的超时时间
                        .build());
            }
            response = this.httpClient.execute(httpBase, HttpClientContext.create());
            entity = response.getEntity();
            return EntityUtils.toString(entity, HttpClientPayUtil.DEFAULT_ENCODING);
        } catch (Exception e) {
            logger.error("发送http请求异常",e);
        } finally {
            try {
                // 关闭HttpEntity的流，如果手动关闭了InputStream in = entity.getContent();这个流，也可以不调用这个方法
                EntityUtils.consume(entity);
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                logger.error("关闭流异常",e);
            }
        }
        return null;
    }


}
