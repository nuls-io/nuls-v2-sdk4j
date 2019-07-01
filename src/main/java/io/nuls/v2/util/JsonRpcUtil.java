package io.nuls.v2.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.JsonRpcRequest;
import io.nuls.v2.model.dto.RpcErrorCode;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.model.dto.RpcResultError;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static io.nuls.v2.util.RestFulUtil.requestConfig;

/**
 * JSON-RPC 请求工具
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class JsonRpcUtil {

    public static String baseUrl = "http://127.0.0.1:9898/jsonrpc";

    private static final String ID = "id";
    private static final String JSONRPC = "jsonrpc";
    private static final String METHOD = "method";
    private static final String PARAMS = "params";

    public static RpcResult request(String method, List<Object> params) {
        RpcResult rpcResult;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            JsonRpcRequest request = new JsonRpcRequest();
            request.setMethod(method);
            request.setParams(params);
            //创建HttpClient对象
            httpClient = HttpClients.createDefault();
            //创建请求方法实例，填充url
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setConfig(requestConfig);

            //设置请求参数
            String json = JSONUtils.obj2json(request);
            StringEntity entity = new StringEntity(json);
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");//发送json需要设置contentType
            httpPost.setEntity(entity);
            //发送（执行）
            response = httpClient.execute(httpPost);
            //获取响应内容
            HttpEntity httpEntity = response.getEntity();
            String resultStr = IOUtils.toString(httpEntity.getContent(), "UTF-8");
            rpcResult = JSONUtils.json2pojo(resultStr, RpcResult.class);
            //int firstLetter = Integer.parseInt(String.valueOf(statusCode).substring(0, 1));
        } catch (Exception e) {
            Log.error(e);
            rpcResult = RpcResult.failed(new RpcResultError(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null));
        } finally {
            close(response);
            close(httpClient);
        }
        return rpcResult;
    }

    private static void close(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
