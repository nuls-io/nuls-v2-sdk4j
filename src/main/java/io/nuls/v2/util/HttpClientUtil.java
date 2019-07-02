package io.nuls.v2.util;

import io.nuls.core.parse.JSONUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClient工具类
 *
 * @author SHANHY
 * @return
 * @create 2015年12月18日
 */
public class HttpClientUtil {

    private static final int timeOut = 10 * 1000;

    private static CloseableHttpClient httpClient = null;

    private final static Object syncLock = new Object();

    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    private static void setPostParams(HttpPost httpPost, Map<String, Object> params) throws Exception {
        //设置请求参数
        String json = JSONUtils.obj2json(params);
        StringEntity entity = new StringEntity(json);
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//发送json需要设置contentType
        httpPost.setEntity(entity);
    }

    private static void setPutParams(HttpPut httpPut, Map<String, Object> params) throws Exception {
        //设置请求参数
        String json = JSONUtils.obj2json(params);
        StringEntity entity = new StringEntity(json);
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//发送json需要设置contentType
        httpPut.setEntity(entity);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url
     * @return
     * @throws IOException
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String post(String url, Map<String, Object> params) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost httppost = new HttpPost(url);
            config(httppost);
            setPostParams(httppost, params);
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * PUT请求URL获取内容
     *
     * @param url
     * @return
     * @throws IOException
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String put(String url, Map<String, Object> params) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            config(httpPut);
            setPutParams(httpPut, params);
            response = getHttpClient(url).execute(httpPut,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();

            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    public static String get(String url, Map<String, Object> params) throws Exception {
        StringBuffer buffer;
        if (null != params && !params.isEmpty()) {
            //遍历map
            buffer = new StringBuffer("");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                buffer.append("&" + entry.getKey() + "=" + entry.getValue());
            }
            url = url + buffer.toString();
        }
        HttpGet httpGet = new HttpGet(url);
        config(httpGet);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpGet,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        // URL列表数组
//        String[] urisToGet = {
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497",
//                "http://blog.csdn.net/catoop/article/details/38849497"};
//
//        long start = System.currentTimeMillis();
//        try {
//            int pagecount = urisToGet.length;
//            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
//            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
//            for (int i = 0; i < pagecount; i++) {
//                HttpGet httpget = new HttpGet(urisToGet[i]);
//                config(httpget);
//                // 启动线程抓取
//                executors
//                        .execute(new GetRunnable(urisToGet[i], countDownLatch));
//            }
//            countDownLatch.await();
//            executors.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("线程" + Thread.currentThread().getName() + ","
//                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
//        }
//
//        long end = System.currentTimeMillis();
//        System.out.println("consume -> " + (end - start));


        try {
            String result = HttpClientUtil.get("http://127.0.0.1:9898/api/accountledger/balance/tNULSeBaMt7c7sybfvP7iAC2p9d1ickHZvH9Sc");
            System.out.println(result);
            Map<String, Object> param = new HashMap<>();
            param.put("count", 2);
            param.put("password", "abcd1234");
            result = HttpClientUtil.post("http://127.0.0.1:9898/api/account", param);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    static class GetRunnable implements Runnable {
//        private CountDownLatch countDownLatch;
//        private String url;
//
//        public GetRunnable(String url, CountDownLatch countDownLatch) {
//            this.url = url;
//            this.countDownLatch = countDownLatch;
//        }
//
//        @Override
//        public void run() {
//            try {
//                System.out.println(HttpClientUtil.get(url));
//            } finally {
//                countDownLatch.countDown();
//            }
//        }
//    }
}