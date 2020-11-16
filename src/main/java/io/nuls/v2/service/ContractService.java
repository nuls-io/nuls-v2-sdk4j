package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.Address;
import io.nuls.base.data.Transaction;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.model.FormatValidUtils;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.MapUtils;
import io.nuls.core.rpc.model.*;
import io.nuls.v2.SDKContext;
import io.nuls.v2.constant.Constant;
import io.nuls.v2.error.ContractErrorCode;
import io.nuls.v2.model.annotation.Api;
import io.nuls.v2.model.annotation.ApiType;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.txdata.CallContractData;
import io.nuls.v2.txdata.CreateContractData;
import io.nuls.v2.txdata.DeleteContractData;
import io.nuls.v2.util.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.core.constant.CommonCodeConstanst.NULL_PARAMETER;
import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.error.AccountErrorCode.ADDRESS_ERROR;
import static io.nuls.v2.error.ContractErrorCode.CONTRACT_ALIAS_FORMAT_ERROR;
import static io.nuls.v2.util.ContractUtil.getFailed;
import static io.nuls.v2.util.ContractUtil.getSuccess;
import static io.nuls.v2.util.ValidateUtil.validateChainId;

/**
 * @author: PierreLuo
 * @date: 2019-07-01
 */
@Api(type = ApiType.SDK)
public class ContractService {

    private static ContractService instance = new ContractService();

    public static ContractService getInstance() {
        return instance;
    }

    //@ApiOperation(description = "离线组装 - 发布合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "账户nonce值"),
            @Parameter(parameterName = "alias", parameterDes = "合约别名"),
            @Parameter(parameterName = "contractCode", parameterDes = "智能合约代码(字节码的Hex编码字符串)"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "argsType", requestType = @TypeDescriptor(value = String[].class), parameterDes = "参数类型列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串"),
            @Key(name = "contractAddress", description = "生成的合约地址")
    }))
    public Result<Map> createContractTxOffline(String sender, BigInteger senderBalance, String nonce, String alias, String contractCode, long gasLimit, Object[] args, String[] argsType, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }
        if (!FormatValidUtils.validAlias(alias)) {
            return Result.getFailed(CONTRACT_ALIAS_FORMAT_ERROR).setMsg(String.format("alias [%s] is invalid", alias));
        }
        if (StringUtils.isBlank(contractCode)) {
            return Result.getFailed(CommonCodeConstanst.NULL_PARAMETER).setMsg("contractCode is empty");
        }

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;

        // 随机生成一个合约地址
        Address contract = AccountTool.createContractAddress(chainId);
        byte[] contractAddressBytes = contract.getAddressBytes();
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            if(argsType == null || argsType.length != args.length) {
                return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("size of 'argsType' array not match 'args' array");
            }
            finalArgs = ContractUtil.twoDimensionalArray(args, argsType);
        }
        // 组装交易的txData
        byte[] contractCodeBytes = HexUtil.decode(contractCode);
        byte[] senderBytes = AddressTool.getAddress(sender);
        CreateContractData createContractData = new CreateContractData();
        createContractData.setSender(senderBytes);
        createContractData.setContractAddress(contractAddressBytes);
        createContractData.setGasLimit(gasLimit);
        createContractData.setPrice(CONTRACT_MINIMUM_PRICE);
        createContractData.setCode(contractCodeBytes);
        createContractData.setAlias(alias);
        if (finalArgs != null) {
            createContractData.setArgsCount((short) finalArgs.length);
            createContractData.setArgs(finalArgs);
        }
        // 生成交易
        Transaction tx = ContractUtil.newCreateTx(chainId, assetId, senderBalance, nonce, createContractData, remark);
        try {
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("hash", tx.getHash().toHex());
            resultMap.put("txHex", HexUtil.encode(tx.serialize()));
            resultMap.put("contractAddress", AddressTool.getStringAddressByBytes(contractAddressBytes));
            return getSuccess().setData(resultMap);
        } catch (IOException e) {
            return getFailed().setMsg(e.getMessage());
        }
    }

    //@ApiOperation(description = "离线组装 - 调用合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "账户nonce值"),
            @Parameter(parameterName = "value", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "methodName", parameterDes = "合约方法"),
            @Parameter(parameterName = "methodDesc", parameterDes = "合约方法描述，若合约内方法没有重载，则此参数可以为空", canNull = true),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "argsType", requestType = @TypeDescriptor(value = String[].class), parameterDes = "参数类型列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> callContractTxOffline(String sender, BigInteger senderBalance, String nonce, BigInteger value, String contractAddress, long gasLimit,
                                             String methodName, String methodDesc, Object[] args, String[] argsType, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }

        if (StringUtils.isBlank(methodName)) {
            return Result.getFailed(NULL_PARAMETER).setMsg("methodName is empty");
        }
        if (value == null) {
            value = BigInteger.ZERO;
        }

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            if(argsType == null || argsType.length != args.length) {
                return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("size of 'argsType' array not match 'args' array");
            }
            finalArgs = ContractUtil.twoDimensionalArray(args, argsType);
        }

        // 组装交易的txData
        byte[] contractAddressBytes = AddressTool.getAddress(contractAddress);
        byte[] senderBytes = AddressTool.getAddress(sender);
        CallContractData callContractData = new CallContractData();
        callContractData.setContractAddress(contractAddressBytes);
        callContractData.setSender(senderBytes);
        callContractData.setValue(value);
        callContractData.setPrice(CONTRACT_MINIMUM_PRICE);
        callContractData.setGasLimit(gasLimit);
        callContractData.setMethodName(methodName);
        callContractData.setMethodDesc(methodDesc);
        if (finalArgs != null) {
            callContractData.setArgsCount((short) finalArgs.length);
            callContractData.setArgs(finalArgs);
        }

        // 生成交易
        Transaction tx = ContractUtil.newCallTx(chainId, assetId, senderBalance, nonce, callContractData, remark);
        try {
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("hash", tx.getHash().toHex());
            resultMap.put("txHex", HexUtil.encode(tx.serialize()));
            return getSuccess().setData(resultMap);
        } catch (IOException e) {
            return getFailed().setMsg(e.getMessage());
        }
    }

    //@ApiOperation(description = "离线组装 - 删除合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "账户nonce值"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> deleteContractTxOffline(String sender, BigInteger senderBalance, String nonce, String contractAddress, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }

        // 组装交易的txData
        byte[] senderBytes = AddressTool.getAddress(sender);
        byte[] contractAddressBytes = AddressTool.getAddress(contractAddress);
        DeleteContractData deleteContractData = new DeleteContractData();
        deleteContractData.setContractAddress(contractAddressBytes);
        deleteContractData.setSender(senderBytes);

        int assetId = SDKContext.main_asset_id;

        // 生成交易
        Transaction tx = ContractUtil.newDeleteTx(chainId, assetId, senderBalance, nonce, deleteContractData, remark);
        try {
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("hash", tx.getHash().toHex());
            resultMap.put("txHex", HexUtil.encode(tx.serialize()));
            return getSuccess().setData(resultMap);
        } catch (IOException e) {
            return getFailed().setMsg(e.getMessage());
        }
    }


    //@ApiOperation(description = "离线组装 - token转账交易")
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> tokenTransferTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (amount == null || amount.compareTo(BigInteger.ZERO) <= 0) {
            return Result.getFailed(ContractErrorCode.PARAMETER_ERROR).setMsg(String.format("amount [%s] is invalid", amount));
        }

        if (!AddressTool.validAddress(chainId, fromAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("fromAddress [%s] is invalid", fromAddress));
        }

        if (!AddressTool.validAddress(chainId, toAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("toAddress [%s] is invalid", toAddress));
        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }
        return this.callContractTxOffline(fromAddress, senderBalance, nonce, null, contractAddress, gasLimit, Constant.NRC20_METHOD_TRANSFER, null,
                new Object[]{toAddress, amount.toString()}, new String[]{"String", "BigInteger"}, remark);
    }

    //@ApiOperation(description = "离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易")
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入者账户地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的主链资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> transferToContractTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, long gasLimit, BigInteger amount, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (amount == null || amount.compareTo(BigInteger.ZERO) <= 0) {
            return Result.getFailed(ContractErrorCode.PARAMETER_ERROR).setMsg(String.format("amount [%s] is invalid", amount));
        }

        if (!AddressTool.validAddress(chainId, fromAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("fromAddress [%s] is invalid", fromAddress));
        }

        if (!AddressTool.validAddress(chainId, toAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("toAddress [%s] is invalid", toAddress));
        }

        return this.callContractTxOffline(fromAddress, senderBalance, nonce, amount, toAddress, gasLimit, Constant.BALANCE_TRIGGER_METHOD_NAME, Constant.BALANCE_TRIGGER_METHOD_DESC, null, null, remark);
    }

    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入的合约地址(NERVE网络地址)"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> nrc20CrossChainTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (amount == null || amount.compareTo(BigInteger.ZERO) <= 0) {
            return Result.getFailed(ContractErrorCode.PARAMETER_ERROR).setMsg(String.format("amount [%s] is invalid", amount));
        }

        if (!AddressTool.validAddress(chainId, fromAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("fromAddress [%s] is invalid", fromAddress));
        }

//        if (!AddressTool.validAddress(SDKContext.nerve_chain_id, toAddress)) {
//            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("toAddress [%s] is invalid", toAddress));
//        }

        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }
        // 跨链手续费
        BigInteger value = BigInteger.valueOf(1000_0000L);
        return this.callContractTxOffline(fromAddress, senderBalance, nonce, value, contractAddress, gasLimit, Constant.NRC20_EVENT_TRANSFER_CROSS_CHAIN, null,
                new Object[]{toAddress, amount.toString()}, new String[]{"String", "BigInteger"}, remark);
    }

    public Result createContract(ContractCreateForm form) {
        validateChainId();
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getGasLimit() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("gasLimit [%s] is invalid", form.getGasLimit()));
        }
        if (form.getPrice() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("price [%s] is invalid", form.getPrice()));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sender", form.getSender());
        params.put("gasLimit", form.getGasLimit());
        params.put("price", form.getPrice());
        params.put("password", form.getPassword());
        params.put("remark", form.getRemark());
        params.put("alias", form.getAlias());
        params.put("args", form.getArgs());
        params.put("contractCode", form.getContractCode());

        RestFulResult restFulResult = RestFulUtil.post("api/contract/create", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result callContract(ContractCallForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getGasLimit() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("gasLimit [%s] is invalid", form.getGasLimit()));
        }
        if (form.getPrice() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("price [%s] is invalid", form.getPrice()));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sender", form.getSender());
        params.put("gasLimit", form.getGasLimit());
        params.put("price", form.getPrice());
        params.put("password", form.getPassword());
        params.put("remark", form.getRemark());
        params.put("contractAddress", form.getContractAddress());
        params.put("value", form.getValue());
        params.put("methodName", form.getMethodName());
        params.put("methodDesc", form.getMethodDesc());
        params.put("args", form.getArgs());

        RestFulResult restFulResult = RestFulUtil.post("api/contract/call", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result deleteContract(ContractDeleteForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("sender", form.getSender());
        params.put("contractAddress", form.getContractAddress());
        params.put("password", form.getPassword());
        params.put("remark", form.getRemark());

        RestFulResult restFulResult = RestFulUtil.post("api/contract/delete", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result tokentransfer(ContractTokenTransferForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getAmount() == null || form.getAmount().compareTo(BigInteger.ZERO) < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("amount is invalid");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/tokentransfer", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result transferTocontract(ContractTransferForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getAmount() == null || form.getAmount().compareTo(BigInteger.ZERO) < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("amount is invalid");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/transfer2contract", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getTokenBalance(String contractAddress, String address) {
        if (address == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("[address] is invalid");
        }
        if (contractAddress == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("[contractAddress] is invalid");
        }
        RestFulResult restFulResult = RestFulUtil.get("api/contract/balance/token/" + contractAddress + "/" + address);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getContractResult(String hash) {
        if (hash == null || !ValidateUtil.validHash(hash)) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("[hash] is invalid");
        }
        RestFulResult restFulResult = RestFulUtil.get("api/contract/result/" + hash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }


    public Result getContractInfo(String contractAddress) {
        if (contractAddress == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("[contractAddress] is invalid");
        }
        RestFulResult restFulResult = RestFulUtil.get("api/contract/info/" + contractAddress);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result<ContractConstructorInfoDto> getConstructor(String contractCode) {
        if (StringUtils.isBlank(contractCode)) {
            return Result.getFailed(CommonCodeConstanst.NULL_PARAMETER).setMsg("contractCode is empty");
        }
        int chainId = SDKContext.main_chain_id;
        RpcResult<Map> rpcResult = JsonRpcUtil.request("getContractConstructor", ListUtil.of(chainId, contractCode));
        RpcResultError rpcResultError = rpcResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        ContractConstructorInfoDto dto = new ContractConstructorInfoDto(rpcResult.getResult());
        return getSuccess().setData(dto);
    }

    public Result getContractMethod(ContractMethodForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (!AddressTool.validAddress(SDKContext.main_chain_id, form.getContractAddress())) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("contractAddress [%s] is invalid", form.getContractAddress()));
        }
        if (StringUtils.isBlank(form.getMethodName())) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("methodName is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/method", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getContractMethodArgsTypes(ContractMethodForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (!AddressTool.validAddress(SDKContext.main_chain_id, form.getContractAddress())) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("contractAddress [%s] is invalid", form.getContractAddress()));
        }
        if (StringUtils.isBlank(form.getMethodName())) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("methodName is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/method/argstypes", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result validateContractCreate(ContractValidateCreateForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getGasLimit() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("gasLimit [%s] is invalid", form.getGasLimit()));
        }
        if (form.getPrice() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("price [%s] is invalid", form.getPrice()));
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/validate/create", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result validateContractCall(ContractValidateCallForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        if (form.getGasLimit() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("gasLimit [%s] is invalid", form.getGasLimit()));
        }
        if (form.getPrice() < 0) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg(String.format("price [%s] is invalid", form.getPrice()));
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/validate/call", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result validateContractDelete(ContractValidateDeleteForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/validate/delete", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result imputedContractCreateGas(ImputedGasContractCreateForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/imputedgas/create", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result imputedContractCallGas(ImputedGasContractCallForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/imputedgas/call", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result invokeView(ContractViewCallForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = MapUtils.beanToMap(form);

        RestFulResult restFulResult = RestFulUtil.post("api/contract/view", params);
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
