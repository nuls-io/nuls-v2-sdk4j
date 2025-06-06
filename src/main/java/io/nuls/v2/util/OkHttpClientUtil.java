package io.nuls.v2.util;

import io.nuls.core.parse.JSONUtils;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtil {
    private static final int READ_TIMEOUT = 100;
    private static final int CONNECT_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 60;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final byte[] LOCKER = new byte[0];
    private static OkHttpClientUtil mInstance;
    private OkHttpClient okHttpClient;
    //private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1087));
    private static Proxy proxy = null;
    private OkHttpClientUtil() {

        okhttp3.OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient.Builder();
        // 读取超时
        clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        // 连接超时
        clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        if (proxy != null) {
            okHttpClient = clientBuilder.proxy(proxy).build();
        } else {
            okHttpClient = clientBuilder.build();
        }
    }
    /**
     * 单例模式获取 NetUtils
     *
     * @return {@link OkHttpClientUtil}
     */
    public static OkHttpClientUtil getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientUtil();
                }
            }
        }
        return mInstance;
    }
    /**
     * GET，同步方式，获取网络数据
     *
     * @param url 请求地址
     * @return {@link Response}
     */
    public Response getResponseData(String url) {
        // 构造 Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call，得到 Response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getData(String url) throws Exception {
        return getData(url, null);
    }
    /**
     * GET，同步方式，获取网络数据
     *
     * @param url 请求地址
     * @return {@link Response}
     */
    public String getData(String url, Map<String, Object> params) throws Exception {
        StringBuffer buffer;
        if (null != params && !params.isEmpty()) {
            //遍历map
            buffer = new StringBuffer("");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                buffer.append("&" + entry.getKey() + "=" + entry.getValue());
            }
            url = url + buffer.toString();
        }
        // 构造 Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call，得到 Response
        Response response = null;
        response = call.execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
    /**
     * POST 请求，同步方式，提交数据
     *
     * @param url        请求地址
     * @param bodyParams 请求参数
     * @return {@link Response}
     */
    public Response postData(String url, Map<String, Object> bodyParams) throws Exception {
        // 构造 RequestBody
        RequestBody body = RequestBody.create(JSONUtils.obj2json(bodyParams), JSON);
        // 构造 Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call，得到 Response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    /**
     * GET 请求，异步方式，获取网络数据
     *
     * @param url       请求地址
     * @param netCallback 回调函数
     */
    public void getDataAsync(String url, final NetCallback netCallback) {
        // 构造 Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCallback.failed(call, e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCallback.success(call, response);
            }
        });
    }
    /**
     * POST 请求，异步方式，提交数据
     *
     * @param url        请求地址
     * @param bodyParams 请求参数
     * @param netCallback  回调函数
     */
    public void postDataAsync(String url, Map<String, Object> bodyParams, final NetCallback netCallback) throws Exception {
        // 构造 RequestBody
        RequestBody body = RequestBody.create(JSONUtils.obj2json(bodyParams), JSON);
        // 构造 Request
        buildRequest(url, netCallback, body);
    }
    /**
     * 同步 POST 请求，使用 JSON 格式作为参数
     *
     * @param url  请求地址
     * @param json JSON 格式参数
     * @return 响应结果
     * @throws IOException 异常
     */
    public String postJson(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 同步 POST 请求，使用 JSON 格式作为参数
     *
     * @param url  请求地址
     * @param bodyParams 格式参数
     * @return 响应结果
     * @throws IOException 异常
     */
    public String postJson(String url, Map<String, Object> bodyParams)  throws Exception {
        // 构造 RequestBody
        RequestBody body = RequestBody.create(JSONUtils.obj2json(bodyParams), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("content-type", JSON.toString())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 异步 POST 请求，使用 JSON 格式作为参数
     *
     * @param url       请求地址
     * @param json      JSON 格式参数
     * @param netCallback 回调函数
     * @throws IOException 异常
     */
    public void postJsonAsync(String url, String json, final NetCallback netCallback) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        // 构造 Request
        buildRequest(url, netCallback, body);
    }
    /**
     * 构造 POST 请求参数
     *
     * @param bodyParams 请求参数
     * @return {@link RequestBody}
     */
//    private RequestBody setRequestBody(Map<String, Object> bodyParams) throws Exception {
//        RequestBody body = null;
//        okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
//        if (bodyParams != null) {
//            Iterator<String> iterator = bodyParams.keySet().iterator();
//            String key = "";
//            while (iterator.hasNext()) {
//                key = iterator.next().toString();
//                formEncodingBuilder.add(key, JSONUtils.obj2json(bodyParams.get(key)));
//            }
//        }
//        body = formEncodingBuilder.build();
//        return body;
//    }
    /**
     * 构造 Request 发起异步请求
     *
     * @param url       请求地址
     * @param netCallback 回调函数
     * @param body      {@link RequestBody}
     */
    private void buildRequest(String url, NetCallback netCallback, RequestBody body) {
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        // 将 Request 封装为 Call
        Call call = okHttpClient.newCall(request);
        // 执行 Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCallback.failed(call, e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCallback.success(call, response);
            }
        });
    }
    /**
     * 自定义网络回调接口
     */
    public interface NetCallback {
        /**
         * 请求成功的回调处理
         *
         * @param call     {@link Call}
         * @param response {@link Response}
         * @throws IOException 异常
         */
        void success(Call call, Response response) throws IOException;
        /**
         * 请求失败的回调处理
         *
         * @param call {@link Call}
         * @param e    异常
         */
        void failed(Call call, IOException e);
    }
}