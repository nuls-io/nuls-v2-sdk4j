package io.nuls.v2.util;

import io.nuls.core.parse.JSONUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {


    /**
     * post 方式提交json数据
     *
     * @throws Exception
     */
    public static String postJson(String url, Object param) throws Exception {
        //创建默认的httpClient实例.
        CloseableHttpClient httpclient = null;
        //接收响应结果
        CloseableHttpResponse response = null;

        String resultStr = null;
        try {
            //创建httppost
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            //参数

            String json = JSONUtils.obj2json(param);
            StringEntity se = new StringEntity(json);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/json");//发送json需要设置contentType
            httpPost.setEntity(se);
            response = httpclient.execute(httpPost);
            //解析返结果
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                resultStr = EntityUtils.toString(entity, "UTF-8");
            }
            return resultStr;
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
            response.close();
        }
    }

    /**
     * post方式提交表单
     *
     * @throws Exception
     */
    public static String postForm(String url, Map<String, Object> param) throws Exception {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = null;
        //接收响应结果
        CloseableHttpResponse response = null;

        String resultStr = null;
        try {
            httpclient = HttpClients.createDefault();
            // 创建httppost
            HttpPost httppost = new HttpPost(url);
            // 创建参数队列
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String mapKey = entry.getKey();
                Object mapValue = entry.getValue();
                formparams.add(new BasicNameValuePair(mapKey, mapValue.toString()));
            }

            //参数转码
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                resultStr = EntityUtils.toString(entity, "UTF-8");
            }
            return resultStr;
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
            response.close();
        }
    }

    /**
     * 发送 get请求
     *
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            httpclient = HttpClients.createDefault();
            // 创建httpget.
            HttpGet httpget = new HttpGet(url);
            // 执行get请求.
            response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            // 打印响应状态
            if (entity != null) {
                // 打印响应内容
                responseContent = EntityUtils.toString(entity);
            }
            return responseContent;
        } catch (Exception e) {
            throw e;
        } finally {
            httpclient.close();
            response.close();
        }
    }
}
