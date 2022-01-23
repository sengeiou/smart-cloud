package smart.util.wxutil;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Slf4j
public class HttpUtil {

    private HttpUtil() {
        throw new IllegalAccessError("工具类不能实例化");
    }

    private static PoolingHttpClientConnectionManager connectionManager = null;

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(3000)
            .build();

    static {
        SSLContext sslcontext = SSLContexts.createSystemDefault();
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(1000);
        // 每个路由最大的请求数量
        connectionManager.setDefaultMaxPerRoute(200);
    }

    public static CloseableHttpClient getHttpClient() {
        return getHttpClientBuilder().build();
    }

    public static CloseableHttpClient getHttpClient(SSLContext sslContext) {
        return getHttpClientBuilder(sslContext).build();
    }

    public static HttpClientBuilder getHttpClientBuilder() {
        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig);
    }

    public static HttpClientBuilder getHttpClientBuilder(SSLContext sslContext) {
        if (sslContext != null) {
            return getHttpClientBuilder().setSSLContext(sslContext);
        } else {
            return getHttpClientBuilder();
        }

    }

    /**
     * post 请求
     *
     * @param httpUrl    请求地址
     * @param sslContext ssl证书信息
     * @return
     */
    public static String sendHttpPost(String httpUrl, SSLContext sslContext) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost(httpPost, sslContext);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpPost(String httpUrl) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost(httpPost, null);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param params  参数(格式:key1=value1&key2=value2)
     */
    public static String sendHttpPost(String httpUrl, String params) {
        return sendHttpPost(httpUrl, params, null);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param params     参数(格式:key1=value1&key2=value2)
     * @param sslContext ssl证书信息
     */
    public static String sendHttpPost(String httpUrl, String params, SSLContext sslContext) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            // 设置参数
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sendHttpPost(httpPost, sslContext);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param maps    参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps) {
        return sendHttpPost(httpUrl, maps, null);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param maps       参数
     * @param sslContext ssl证书信息
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps, SSLContext sslContext) {
        HttpPost httpPost = wrapHttpPost(httpUrl, maps);
        return sendHttpPost(httpPost, null);
    }

    /**
     * 封装获取HttpPost方法
     *
     * @param httpUrl
     * @param maps
     * @return
     */
    public static HttpPost wrapHttpPost(String httpUrl, Map<String, String> maps) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> m : maps.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(m.getKey(), m.getValue()));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return httpPost;
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl 地址
     * @param file    附件,名称和File对应
     */
    public static String sendHttpPost(String httpUrl, File file) {
        Map<String, File> immutableMap = new HashMap<>(16);
        immutableMap.put("media", file);
        return sendHttpPost(httpUrl, immutableMap, null, null);
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl 地址
     * @param file    附件,名称和File对应
     * @param maps    参数
     */
    public static String sendHttpPost(String httpUrl, File file, Map<String, String> maps) {
        Map<String, File> immutableMap = new HashMap<>(16);
        immutableMap.put("media", file);
        return sendHttpPost(httpUrl, immutableMap, maps, null);
    }

    /**
     * 发送 post请求（带文件）,默认 files 名称数组.
     *
     * @param httpUrl   地址
     * @param fileLists 附件
     * @param maps      参数
     */
    public static String sendHttpPost(String httpUrl, List<File> fileLists, Map<String, String> maps) {
        return sendHttpPost(httpUrl, fileLists, maps, null);
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl 地址
     * @param fileMap 附件,名称和File对应
     * @param maps    参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, File> fileMap, Map<String, String> maps) {
        return sendHttpPost(httpUrl, fileMap, maps, null);
    }

    /**
     * 发送 post请求（带文件）,默认 files 名称数组.
     *
     * @param httpUrl    地址
     * @param fileLists  附件
     * @param maps       参数
     * @param sslContext ssl证书信息
     */
    public static String sendHttpPost(String httpUrl, List<File> fileLists, Map<String, String> maps,
                                      SSLContext sslContext) {
        Map<String, File> fileMap = new HashMap<>(16);
        if (fileLists == null || fileLists.isEmpty()) {
            for (File file : fileLists) {
                fileMap.put("media", file);
            }
        }
        return sendHttpPost(httpUrl, fileMap, maps, sslContext);
    }

    /**
     * 发送 post请求（带文件）
     *
     * @param httpUrl    地址
     * @param fileMap    附件,名称和File对应
     * @param maps       参数
     * @param sslContext ssl证书信息
     */
    public static String sendHttpPost(String httpUrl, Map<String, File> fileMap, Map<String, String> maps,
                                      SSLContext sslContext) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        if (null != maps) {
            for (Map.Entry<String, String> m : maps.entrySet()) {
                meBuilder.addPart(m.getKey(), new StringBody(m.getValue(), ContentType.TEXT_PLAIN));
            }
        }
        if (null != fileMap) {
            for (Map.Entry<String, File> m : fileMap.entrySet()) {
                FileBody fileBody = new FileBody(m.getValue());
                meBuilder.addPart(m.getKey(), fileBody);
            }
        }

        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpPost(httpPost, sslContext);
    }

    /**
     * 发送Post请求
     *
     * @param httpPost
     * @return
     */
    public static String sendHttpPost(HttpPost httpPost) {
        return sendHttpPost(httpPost, null);
    }

    /**
     * 发送Post请求
     *
     * @param httpPost
     * @param sslConext ssl证书信息
     * @return
     */
    public static String sendHttpPost(HttpPost httpPost, SSLContext sslConext) {
        CloseableHttpClient httpClient = getHttpClient(sslConext);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                // 关闭连接,释放资源
                if (entity != null) {
                    // 会自动释放连接
                    EntityUtils.consumeQuietly(entity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseContent;
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl
     */
    public static String sendHttpGet(String httpUrl) {
        return sendHttpGet(httpUrl, null);
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl
     * @param sslConext ssl证书信息
     */
    public static String sendHttpGet(String httpUrl, SSLContext sslConext) {
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet, sslConext);
    }

    /**
     * 发送Get请求
     *
     * @param httpGet
     * @return
     */
    public static String sendHttpGet(HttpGet httpGet) {
        return sendHttpGet(httpGet, null);
    }

    /**
     * 发送Get请求
     *
     * @param httpGet
     * @param sslConext ssl证书信息
     * @return
     */
    public static String sendHttpGet(HttpGet httpGet, SSLContext sslConext) {
        CloseableHttpClient httpClient = getHttpClient(sslConext);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                // 关闭连接,释放资源
                if (entity != null) {
                    // 会自动释放连接
                    EntityUtils.consumeQuietly(entity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return responseContent;
    }

    /**
     * 发送 get请求
     *
     * @param httpUrl 请求路径
     * @param headers 请求头参数
     * @return
     */
    public static String sendHttpHeaderGet(String httpUrl, Map<String, String> headers) {
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            httpGet.setHeader(key, value);
        }
        return sendHttpGet(httpGet, null);
    }

    /**
     * Get 下载文件
     *
     * @param httpUrl
     * @param file
     * @return
     */
    public static File sendHttpGetFile(String httpUrl, File file) {
        if (file == null) {
            return null;
        }
        HttpGet httpGet = new HttpGet(httpUrl);
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            inputStream = entity.getContent();
            fileOutputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                fileOutputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                // 关闭连接,释放资源
                if (entity != null) {
                    // 会自动释放连接
                    EntityUtils.consumeQuietly(entity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return file;
    }

    /**
     * Post 下载文件
     *
     * @param httpUrl
     * @param maps
     * @param file
     * @return
     */
    public static File sendHttpPostFile(String httpUrl, Map<String, String> maps, File file) {
        if (file == null) {
            return null;
        }
        HttpPost httpPost = wrapHttpPost(httpUrl, maps);
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            entity = response.getEntity();
            inputStream = entity.getContent();
            fileOutputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                fileOutputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                // 关闭连接,释放资源
                if (entity != null) {
                    // 会自动释放连接
                    EntityUtils.consumeQuietly(entity);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return file;
    }


    /**
     * 判断是否微信返回错误
     *
     * @param jsonObject
     * @return
     */
    public static boolean isWxError(JSONObject jsonObject) {
        if (null == jsonObject || jsonObject.getIntValue("errcode") != 0) {
            return true;
        }
        return false;
    }

    /**
     * http请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                jsonObject = JSONObject.parseObject("{\"code\":400,\"message\":\"租户不存在,请先注册\",\"data\":null}");
            }
            log.error(String.valueOf(e.getStackTrace()));
        }
        return jsonObject;
    }

    /**
     * 工作流事件接口请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static JSONObject httpRequestAll(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        boolean isHttp = requestUrl.toLowerCase().contains("https");
        if (isHttp) {
            jsonObject = HttpUtil.httpsRequest(requestUrl, requestMethod, outputStr);
        } else {
            jsonObject = HttpUtil.httpRequest(requestUrl, requestMethod, outputStr);
        }
        return jsonObject;
    }

    /**
     * https请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();
            jsonObject = JSONObject.parseObject(buffer.toString());
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                jsonObject = JSONObject.parseObject("{\"code\":400,\"message\":\"租户不存在,请先注册\",\"data\":null}");
            }
            log.error(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * http请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static boolean httpCronRequest(String requestUrl, String requestMethod, String outputStr) {
        boolean falg = false;
        try {
            URL url = new URL(requestUrl);
            final HttpURLConnection[] conn = {null};
            Callable<String> task = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    //执行耗时代码
                    try {
                        conn[0] = (HttpURLConnection) url.openConnection();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    conn[0].setDoOutput(true);
                    conn[0].setDoInput(true);
                    conn[0].setUseCaches(false);
                    conn[0].setRequestMethod(requestMethod);
                    if (null != outputStr) {
                        OutputStream outputStream = conn[0].getOutputStream();
                        outputStream.write(outputStr.getBytes("UTF-8"));
                        outputStream.close();
                    }
                    InputStream inputStream = conn[0].getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    conn[0].disconnect();
                    return "url连接ok";
                }
            };
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(task);
            try {
                //设置超时时间
                String rst = future.get(3, TimeUnit.SECONDS);
                if("url连接ok".equals(rst)){
                    falg = true;
                }
            } catch (TimeoutException e) {
                log.error("连接url超时");
            } catch (Exception e) {
                log.error("获取异常," + e.getMessage());
            } finally {
                executorService.shutdown();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return falg;
    }



    /**
     * https请求
     *
     * @param requestUrl    url
     * @param requestMethod GET/POST
     * @param outputStr     参数
     * @return
     */
    public static boolean httpsCronRequest(String requestUrl, String requestMethod, String outputStr) {
        boolean falg = false;
        try {
            URL url = new URL(requestUrl);
            final HttpsURLConnection[] conn = {null};
            Callable<String> task = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    //执行耗时代码
                    try {
                        conn[0] = (HttpsURLConnection) url.openConnection();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                    conn[0].setDoOutput(true);
                    conn[0].setDoInput(true);
                    conn[0].setUseCaches(false);
                    conn[0].setRequestMethod(requestMethod);
                    if (null != outputStr) {
                        OutputStream outputStream = conn[0].getOutputStream();
                        outputStream.write(outputStr.getBytes("UTF-8"));
                        outputStream.close();
                    }
                    InputStream inputStream = conn[0].getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    bufferedReader.close();
                    inputStreamReader.close();
                    inputStream.close();
                    conn[0].disconnect();
                    return "url连接ok";
                }
            };
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<String> future = executorService.submit(task);
            try {
                //设置超时时间
                String rst = future.get(3, TimeUnit.SECONDS);
                if ("url连接ok".equals(rst)) {
                    falg = true;
                }
            } catch (TimeoutException e) {
                log.error("连接url超时");
            } catch (Exception e) {
                log.error("获取异常," + e.getMessage());
            } finally {
                executorService.shutdown();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return falg;
    }

}
