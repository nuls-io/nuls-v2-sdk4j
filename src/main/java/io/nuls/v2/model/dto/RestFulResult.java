package io.nuls.v2.model.dto;

public class RestFulResult<T> {

    private boolean success;

    private T data;

    private RpcResultError error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RpcResultError getError() {
        return error;
    }

    public void setError(RpcResultError error) {
        this.error = error;
    }

    public static <T> RestFulResult success(T data) {
        RestFulResult<T> result = new RestFulResult<>();
        result.success = true;
        result.data = data;
        return result;
    }

    public static RestFulResult failed(String code, String msg) {
        return failed(code, msg, null);
    }

    public static RestFulResult failed(String code, String msg, Object errorData) {
        RestFulResult result = new RestFulResult<>();
        RpcResultError error = new RpcResultError(code, msg, errorData);
        result.setSuccess(false);
        result.setError(error);
        return result;
    }
}
