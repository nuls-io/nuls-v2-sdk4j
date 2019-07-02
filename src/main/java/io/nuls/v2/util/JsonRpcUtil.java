package io.nuls.v2.util;

import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import io.nuls.core.parse.MapUtils;
import io.nuls.v2.model.dto.JsonRpcRequest;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.model.dto.RpcResultError;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.nuls.v2.util.RestFulUtil.connMgr;
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
            Map<String, Object> map = MapUtils.beanToMap(request);
            String resultStr = HttpClientUtil.post(baseUrl, map);
            rpcResult = JSONUtils.json2pojo(resultStr, RpcResult.class);
        } catch (Exception e) {
            Log.error(e);
            rpcResult = RpcResult.failed(new RpcResultError(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null));
        }
        return rpcResult;
    }

}
