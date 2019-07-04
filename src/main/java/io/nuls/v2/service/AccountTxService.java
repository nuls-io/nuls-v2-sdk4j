package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.exception.NulsException;
import io.nuls.v2.SDKContext;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.dto.RestFulResult;
import io.nuls.v2.model.dto.TransferForm;
import io.nuls.v2.util.RestFulUtil;

import java.util.HashMap;
import java.util.Map;

import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class AccountTxService {

    private AccountTxService accountTxService;

    private static AccountTxService instance = new AccountTxService();

    public static AccountTxService getInstance() {
        return instance;
    }

    public Result transfer(TransferForm transferForm) {
        validateChainId();

        Map<String, Object> params = new HashMap<>();
        params.put("address", transferForm.getAddress());
        params.put("toAddress", transferForm.getToAddress());
        params.put("password", transferForm.getPassword());
        params.put("amount", transferForm.getAmount());
        params.put("remark", transferForm.getRemark());
        RestFulResult restFulResult = RestFulUtil.post("api/accountledger/transfer", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }


    public Result getBalance(String address) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            RestFulResult restFulResult = RestFulUtil.get("api/accountledger/balance/" + address);
            Result result;
            if (restFulResult.isSuccess()) {
                result = Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }


    public Result getTx(String txHash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/accountledger/tx/" + txHash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }
}
