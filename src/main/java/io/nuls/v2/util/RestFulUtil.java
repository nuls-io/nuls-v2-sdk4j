package io.nuls.v2.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 基于HttpClient第三方工具的http工具类
 *
 * @author Administrator
 */
public class RestFulUtil {

    public static String baseUrl = "http://127.0.0.1:9898/api/";
    //统一配置
    static final PoolingHttpClientConnectionManager connMgr;
    static final RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送get请求不带参数
     *
     * @param url
     * @return
     */
    public static String sendGet(String url) {

        return sendGet(url, null);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param map
     * @return
     */
    public static String sendGet(String url, Map<String, String> map) {

        String resulrStr = null;
        StringBuffer parms = null;
        url = baseUrl + url;
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //设置请求参数
        if (null != map && !map.isEmpty()) {
            //遍历map
            parms = new StringBuffer();
            for (Entry<String, String> entry : map.entrySet()) {
                parms.append("&" + entry.getKey() + "=" + entry.getValue());
            }
            url = url + parms.toString();
        }
        //创建GET请求方法的实例，并填充url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try {
            //发送（执行）请求
            CloseableHttpResponse response = httpClient.execute(httpGet);
            //获取响应头、内容
            int statusCode = response.getStatusLine().getStatusCode();
            //打印状态码
            System.out.println("执行状态码：" + statusCode);
            HttpEntity entity = response.getEntity();
            resulrStr = IOUtils.toString(entity.getContent(), "UTF-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resulrStr;
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param map
     * @return
     */
    public static String sendPost(String url, Map<String, Object> map) {

        String resultStr = null;
        url = baseUrl + url;
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //创建请求方法实例，填充url
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //设置请求参数（构造参数队列）
        List<NameValuePair> parmsForm = new ArrayList<NameValuePair>();
        for (Entry<String, Object> entry : map.entrySet()) {
            parmsForm.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(parmsForm, "UTF-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");//发送json需要设置contentType
            //为方法实例设置参数队列实体
            httpPost.setEntity(entity);
            //发送（执行）
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取状态码
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("执行状态码：" + statusCode);
            //获取响应内容
            HttpEntity httpEntity = response.getEntity();
            resultStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    /**
     * 发送put请求
     *
     * @param url
     * @param map
     * @return
     */
    public static String sendPut(String url, Map<String, String> map) {

        String resultStr = null;
        url = url + baseUrl;
        //新建httpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //创建请求方法实例并填充url
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(requestConfig);
        //创建参数队列
        List<NameValuePair> parmsForm = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : map.entrySet()) {
            parmsForm.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(parmsForm);
            //将参数加入方法实例
            httpPut.setEntity(entity);
            //发送
            CloseableHttpResponse response = httpClient.execute(httpPut);
            //获取状态码
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            //获取响应内容
            HttpEntity httpEntity = response.getEntity();
            resultStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
            //关闭响应
            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    /**
     * 发送delete请求
     *
     * @param url
     * @return
     */
    public static String sendDelete(String url) {

        String resultStr = null;
        url = url + baseUrl;
        //创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //创建方法实例
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(requestConfig);
        //执行
        try {
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            //获取响应内容
            HttpEntity entity = response.getEntity();
            resultStr = IOUtils.toString(entity.getContent(), "UTF-8");
            response.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }

    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("count", "2");
        param.put("password", "abcd1234");

        String url = "account";
        String result = sendPost(url, param);
        System.out.println(result);
    }

}
