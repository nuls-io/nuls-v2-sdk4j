package io.nuls.v2.util;

import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.RestFulResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 基于HttpClient第三方工具的http工具类
 *
 * @author Administrator
 */
public class RestFulUtil {

    /**
     * 发送get请求不带参数
     *
     * @param url
     * @return
     */
    public static RestFulResult<Map<String, Object>> get(String url) {
        return get(url, null);
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static RestFulResult<Map<String, Object>> get(String url, Map<String, Object> params) {
        try {
            url = SDKContext.wallet_url + url;
            String resultStr = HttpClientUtil.get(url, params);
            RestFulResult<Map<String, Object>> result = toResult(resultStr);
            return result;
        } catch (Exception e) {
            Log.error(e);
            return RestFulResult.failed(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null);
        }
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static RestFulResult<List<Object>> getList(String url, Map<String, Object> params) {
        try {
            url = SDKContext.wallet_url + url;
            String resultStr = HttpClientUtil.get(url, params);
            RestFulResult<List<Object>> result = toResultList(resultStr);
            return result;
        } catch (Exception e) {
            Log.error(e);
            return RestFulResult.failed(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null);
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static RestFulResult post(String url, Map<String, Object> params) {
        try {
            url = SDKContext.wallet_url + url;
            String resultStr = HttpClientUtil.post(url, params);
            RestFulResult result = toResult(resultStr);
            return result;
        } catch (Exception e) {
            Log.error(e);
            return RestFulResult.failed(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null);
        }
    }

    /**
     * 发送put请求
     *
     * @param url
     * @param params
     * @return
     */
    public static RestFulResult<Map<String, Object>> put(String url, Map<String, Object> params) {
        try {
            url = SDKContext.wallet_url + url;
            String resultStr = HttpClientUtil.put(url, params);
            RestFulResult<Map<String, Object>> result = toResult(resultStr);
            return result;
        } catch (Exception e) {
            Log.error(e);
            return RestFulResult.failed(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null);
        }
    }

    private static RestFulResult toResult(String str) throws IOException {
        Map<String, Object> resultMap = JSONUtils.json2map(str);
        RestFulResult result = null;
        Boolean b = (Boolean) resultMap.get("success");
        if (b) {
            result = RestFulResult.success(resultMap.get("data"));
        } else {
            Object dataObj = resultMap.get("data");
            if(dataObj instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
                if (data != null) {
                    result = RestFulResult.failed(data.get("code").toString(), data.get("msg").toString());
                }
            } else {
                result = RestFulResult.failed(CommonCodeConstanst.SYS_UNKOWN_EXCEPTION.getCode(), resultMap.toString());
            }
        }
        return result;
    }

    private static RestFulResult toResultList(String str) throws IOException {
        Map<String, Object> resultMap = JSONUtils.json2map(str);
        RestFulResult<List<Object>> result = null;
        Boolean b = (Boolean) resultMap.get("success");
        if (b) {
            List<Object> data = (List<Object>) resultMap.get("data");
            result = RestFulResult.success(data);
        } else {
            Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
            if (data != null) {
                result = RestFulResult.failed(data.get("code").toString(), data.get("msg").toString());
            }
        }
        return result;
    }

//    public static String sendGet(String url, Map<String, String> map) {
//
//        String resulrStr = null;
//        StringBuffer parms = null;
//        url = baseUrl + url;
//        //创建HttpClient对象
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
//        //设置请求参数
//        if (null != map && !map.isEmpty()) {
//            //遍历map
//            parms = new StringBuffer();
//            for (Entry<String, String> entry : map.entrySet()) {
//                parms.append("&" + entry.getKey() + "=" + entry.getValue());
//            }
//            url = url + parms.toString();
//        }
//        //创建GET请求方法的实例，并填充url
//        HttpGet httpGet = new HttpGet(url);
//        httpGet.setConfig(requestConfig);
//        try {
//            //发送（执行）请求
//            CloseableHttpResponse response = httpClient.execute(httpGet);
//            //获取响应头、内容
//            int statusCode = response.getStatusLine().getStatusCode();
//            //打印状态码
//            System.out.println("执行状态码：" + statusCode);
//            HttpEntity entity = response.getEntity();
//            resulrStr = IOUtils.toString(entity.getContent(), "UTF-8");
//            response.close();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            //关闭连接释放资源
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return resulrStr;
//    }

//    /**
//     * 发送post请求
//     *
//     * @param url
//     * @param map
//     * @return
//     */
//    public static String sendPost(String url, Map<String, Object> map) {
//
//        String resultStr = null;
//        url = baseUrl + url;
//        //创建HttpClient对象
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
//        //创建请求方法实例，填充url
//        HttpPost httpPost = new HttpPost(url);
//        httpPost.setConfig(requestConfig);
//        //设置请求参数（构造参数队列）
//        List<NameValuePair> parmsForm = new ArrayList<NameValuePair>();
//        for (Entry<String, Object> entry : map.entrySet()) {
//            parmsForm.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
//        }
//        UrlEncodedFormEntity entity;
//        try {
//            entity = new UrlEncodedFormEntity(parmsForm, "UTF-8");
//            entity.setContentEncoding("UTF-8");
//            entity.setContentType("application/json");//发送json需要设置contentType
//            //为方法实例设置参数队列实体
//            httpPost.setEntity(entity);
//            //发送（执行）
//            CloseableHttpResponse response = httpClient.execute(httpPost);
//            //获取状态码
//            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println("执行状态码：" + statusCode);
//            //获取响应内容
//            HttpEntity httpEntity = response.getEntity();
//            resultStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
//            response.close();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultStr;
//    }

//    /**
//     * 发送put请求
//     *
//     * @param url
//     * @param map
//     * @return
//     */
//    public static String sendPut(String url, Map<String, String> map) {
//
//        String resultStr = null;
//        url = url + baseUrl;
//        //新建httpClient对象
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
//        //创建请求方法实例并填充url
//        HttpPut httpPut = new HttpPut(url);
//        httpPut.setConfig(requestConfig);
//        //创建参数队列
//        List<NameValuePair> parmsForm = new ArrayList<NameValuePair>();
//        for (Entry<String, String> entry : map.entrySet()) {
//            parmsForm.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        UrlEncodedFormEntity entity;
//        try {
//            entity = new UrlEncodedFormEntity(parmsForm);
//            //将参数加入方法实例
//            httpPut.setEntity(entity);
//            //发送
//            CloseableHttpResponse response = httpClient.execute(httpPut);
//            //获取状态码
//            int statusCode = response.getStatusLine().getStatusCode();
//            System.out.println(statusCode);
//            //获取响应内容
//            HttpEntity httpEntity = response.getEntity();
//            resultStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
//            //关闭响应
//            response.close();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultStr;
//    }
//
//    /**
//     * 发送delete请求
//     *
//     * @param url
//     * @return
//     */
//    public static String sendDelete(String url) {
//
//        String resultStr = null;
//        url = url + baseUrl;
//        //创建httpClient对象
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
//        //创建方法实例
//        HttpDelete httpDelete = new HttpDelete(url);
//        httpDelete.setConfig(requestConfig);
//        //执行
//        try {
//            CloseableHttpResponse response = httpClient.execute(httpDelete);
//            //获取响应内容
//            HttpEntity entity = response.getEntity();
//            resultStr = IOUtils.toString(entity.getContent(), "UTF-8");
//            response.close();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultStr;
//    }

//    public static void main(String[] args) {
//        Map<String, Object> param = new HashMap<>();
//        param.put("count", "2");
//        param.put("password", "abcd1234");
//
//        String url = "account";
//        String result = sendPost(url, param);
//        System.out.println(result);
//    }

    public static void main(String[] args) {
        RestFulResult result = get("api/accountledger/balance/tNULSeBaMt7c7sybfvP7iAC2p9d1ickHZvH9Sc");
        System.out.println(result.getData());
    }
}
