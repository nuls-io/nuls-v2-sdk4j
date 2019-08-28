package io.nuls.v2.util;

import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.model.dto.RpcResultError;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.v2.SDKContext.wallet_url;

/**
 * JSON-RPC 请求工具
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class JsonRpcUtil {

    private static final String ID = "id";
    private static final String JSONRPC = "jsonrpc";
    private static final String METHOD = "method";
    private static final String PARAMS = "params";
    private static final String DEFAULT_ID = "1";
    private static final String JSONRPC_VERSION = "2.0";

    public static RpcResult request(String method, List<Object> params) {
        RpcResult rpcResult;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            Map<String, Object> map = new HashMap<>(8);
            map.put(ID, DEFAULT_ID);
            map.put(JSONRPC, JSONRPC_VERSION);
            map.put(METHOD, method);
            map.put(PARAMS, params);
            String resultStr = HttpClientUtil.post(wallet_url + JSONRPC, map);
            rpcResult = JSONUtils.json2pojo(resultStr, RpcResult.class);
        } catch (Exception e) {
            Log.error(e);
            rpcResult = RpcResult.failed(new RpcResultError(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null));
        }
        return rpcResult;
    }

    public static RpcResult request(String requestURL, String method, List<Object> params) {
        RpcResult rpcResult;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            Map<String, Object> map = new HashMap<>(8);
            map.put(ID, DEFAULT_ID);
            map.put(JSONRPC, JSONRPC_VERSION);
            map.put(METHOD, method);
            map.put(PARAMS, params);
            String resultStr = HttpClientUtil.post(requestURL, map);
            rpcResult = JSONUtils.json2pojo(resultStr, RpcResult.class);
        } catch (Exception e) {
            Log.error(e);
            rpcResult = RpcResult.failed(new RpcResultError(CommonCodeConstanst.DATA_ERROR.getCode(), e.getMessage(), null));
        }
        return rpcResult;
    }

}
