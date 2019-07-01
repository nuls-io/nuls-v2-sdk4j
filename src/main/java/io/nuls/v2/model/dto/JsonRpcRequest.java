package io.nuls.v2.model.dto;

import java.util.List;


public class JsonRpcRequest {

    private String id;

    private String jsonrpc;

    private String method;

    private List<Object> params;

    public JsonRpcRequest() {
        this.jsonrpc = "2.0";
        this.id = "1";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
