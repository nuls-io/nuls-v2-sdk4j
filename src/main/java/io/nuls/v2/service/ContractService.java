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
import io.nuls.core.rpc.model.*;
import io.nuls.v2.SDKContext;
import io.nuls.v2.constant.Constant;
import io.nuls.v2.error.ContractErrorCode;
import io.nuls.v2.model.annotation.Api;
import io.nuls.v2.model.annotation.ApiOperation;
import io.nuls.v2.model.annotation.ApiType;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.tx.CallContractTransaction;
import io.nuls.v2.tx.CreateContractTransaction;
import io.nuls.v2.tx.DeleteContractTransaction;
import io.nuls.v2.txdata.CallContractData;
import io.nuls.v2.txdata.CreateContractData;
import io.nuls.v2.txdata.DeleteContractData;
import io.nuls.v2.util.AccountTool;
import io.nuls.v2.util.ContractUtil;
import io.nuls.v2.util.JsonRpcUtil;
import io.nuls.v2.util.RestFulUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.core.constant.CommonCodeConstanst.NULL_PARAMETER;
import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;
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

    @ApiOperation(description = "离线组装 - 发布合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterType = "String", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "alias", parameterType = "String", parameterDes = "合约别名"),
            @Parameter(parameterName = "contractCode", parameterType = "String", parameterDes = "智能合约代码(字节码的Hex编码字符串)"),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串"),
            @Key(name = "contractAddress", description = "生成的合约地址")
    }))
    public Result<Map> createTxOffline(String sender, String alias, String contractCode, Object[] args, String remark) {
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
        // 验证发布合约的合法性
        RpcResult validateResult = JsonRpcUtil.request("validateContractCreate", List.of(chainId, sender, MAX_GASLIMIT, CONTRACT_MINIMUM_PRICE, contractCode, args));
        Map map = (Map) validateResult.getResult();
        boolean success = (boolean) map.get("success");
        if (!success) {
            return Result.getFailed(ErrorCode.init((String) map.get("code"))).setMsg((String) map.get("msg"));
        }

        // 预估发布合约需要的GAS
        RpcResult<Map> rpcResult = JsonRpcUtil.request("imputedContractCreateGas", List.of(chainId, sender, contractCode, args));
        RpcResultError rpcResultError = rpcResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        Map result = rpcResult.getResult();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        int assetChainId = SDKContext.nuls_chain_id;
        int assetId = SDKContext.nuls_asset_id;

        // 随机生成一个合约地址
        Address contract = AccountTool.createContractAddress(chainId);
        byte[] contractAddressBytes = contract.getAddressBytes();
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            RpcResult<Map> constructorResult = JsonRpcUtil.request("getContractConstructor", List.of(chainId, contractCode));
            rpcResultError = constructorResult.getError();
            if (rpcResultError != null) {
                return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
            }
            result = constructorResult.getResult();
            Map constructor = (Map) result.get("constructor");
            List<Map> argsList = (List<Map>) constructor.get("args");
            int size = argsList.size();
            String[] argTypes = new String[size];
            int i = 0;
            for (Map arg : argsList) {
                argTypes[i++] = arg.get("type").toString();
            }
            finalArgs = ContractUtil.twoDimensionalArray(args, argTypes);
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
            createContractData.setArgsCount((byte) finalArgs.length);
            createContractData.setArgs(finalArgs);
        }
        // 获取交易创建者的nonce值
        RpcResult<Map> balanceResult = JsonRpcUtil.request("getAccountBalance", List.of(chainId, assetChainId, assetId, sender));
        rpcResultError = balanceResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        result = balanceResult.getResult();
        BigInteger senderBalance = new BigInteger(result.get("balance").toString());
        String nonce = result.get("nonce").toString();
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


    @ApiOperation(description = "离线组装 - 调用合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterType = "String", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "value", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO"),
            @Parameter(parameterName = "contractAddress", parameterType = "String", parameterDes = "合约地址"),
            @Parameter(parameterName = "methodName", parameterType = "String", parameterDes = "合约方法"),
            @Parameter(parameterName = "methodDesc", parameterType = "String", parameterDes = "合约方法描述，若合约内方法没有重载，则此参数可以为空", canNull = true),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> callTxOffline(String sender, BigInteger value, String contractAddress,
                                     String methodName, String methodDesc, Object[] args, String remark) {
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

        // 验证调用合约的合法性
        RpcResult validateResult = JsonRpcUtil.request("validateContractCall", List.of(chainId, sender, value,
                MAX_GASLIMIT, CONTRACT_MINIMUM_PRICE, contractAddress, methodName, methodDesc, args));
        Map map = (Map) validateResult.getResult();
        boolean success = (boolean) map.get("success");
        if (!success) {
            return Result.getFailed(ErrorCode.init((String) map.get("code"))).setMsg((String) map.get("msg"));
        }

        // 估算调用合约需要的GAS
        RpcResult<Map> rpcResult = JsonRpcUtil.request("imputedContractCallGas", List.of(chainId, sender, value, contractAddress, methodName, methodDesc, args));
        RpcResultError rpcResultError = rpcResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        Map result = rpcResult.getResult();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        int assetChainId = SDKContext.nuls_chain_id;
        int assetId = SDKContext.nuls_asset_id;
        // 生成参数的二维数组
        String[][] finalArgs = null;
        if (args != null && args.length > 0) {
            RpcResult<List> constructorResult = JsonRpcUtil.request("getContractMethodArgsTypes", List.of(chainId, contractAddress, methodName));
            rpcResultError = constructorResult.getError();
            if (rpcResultError != null) {
                return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
            }
            List<String> list = constructorResult.getResult();
            int size = list.size();
            String[] argTypes = new String[size];
            argTypes = list.toArray(argTypes);
            finalArgs = ContractUtil.twoDimensionalArray(args, argTypes);
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
            callContractData.setArgsCount((byte) finalArgs.length);
            callContractData.setArgs(finalArgs);
        }

        // 获取交易创建者的nonce值
        RpcResult<Map> balanceResult = JsonRpcUtil.request("getAccountBalance", List.of(chainId, assetChainId, assetId, sender));
        rpcResultError = balanceResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        result = balanceResult.getResult();
        BigInteger senderBalance = new BigInteger(result.get("balance").toString());
        String nonce = result.get("nonce").toString();

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


    @ApiOperation(description = "离线组装 - 删除合约的交易")
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterType = "String", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterType = "String", parameterDes = "合约地址"),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> deleteTxOffline(String sender, String contractAddress, String remark) {
        int chainId = SDKContext.main_chain_id;
        if (!AddressTool.validAddress(chainId, sender)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("sender [%s] is invalid", sender));
        }
        if (!AddressTool.validAddress(chainId, contractAddress)) {
            return Result.getFailed(ADDRESS_ERROR).setMsg(String.format("contractAddress [%s] is invalid", contractAddress));
        }
        // 验证删除合约的合法性
        RpcResult validateResult = JsonRpcUtil.request("validateContractDelete", List.of(chainId, sender, contractAddress));
        Map map = (Map) validateResult.getResult();
        boolean success = (boolean) map.get("success");
        if (!success) {
            return Result.getFailed(ErrorCode.init((String) map.get("code"))).setMsg((String) map.get("msg"));
        }

        // 组装交易的txData
        byte[] senderBytes = AddressTool.getAddress(sender);
        byte[] contractAddressBytes = AddressTool.getAddress(contractAddress);
        DeleteContractData deleteContractData = new DeleteContractData();
        deleteContractData.setContractAddress(contractAddressBytes);
        deleteContractData.setSender(senderBytes);

        // 获取交易创建者的nonce值
        int assetChainId = SDKContext.nuls_chain_id;
        int assetId = SDKContext.nuls_asset_id;
        RpcResult<Map> balanceResult = JsonRpcUtil.request("getAccountBalance", List.of(chainId, assetChainId, assetId, sender));
        RpcResultError rpcResultError = balanceResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        Map result = balanceResult.getResult();
        BigInteger senderBalance = new BigInteger(result.get("balance").toString());
        String nonce = result.get("nonce").toString();

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


    @ApiOperation(description = "根据合约代码获取合约构造函数详情")
    @Parameters(description = "参数", value = {
            @Parameter(parameterName = "contractCode", parameterType = "String", parameterDes = "智能合约代码(字节码的Hex编码字符串)")
    })
    @ResponseData(name = "返回值", description = "合约构造函数详情", responseType = @TypeDescriptor(value = ContractConstructorInfoDto.class))
    public Result<ContractConstructorInfoDto> getConstructor(String contractCode) {
        if (StringUtils.isBlank(contractCode)) {
            return Result.getFailed(CommonCodeConstanst.NULL_PARAMETER).setMsg("contractCode is empty");
        }
        int chainId = SDKContext.main_chain_id;
        RpcResult<Map> rpcResult = JsonRpcUtil.request("getContractConstructor", List.of(chainId, contractCode));
        RpcResultError rpcResultError = rpcResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        ContractConstructorInfoDto dto = new ContractConstructorInfoDto(rpcResult.getResult());
        return getSuccess().setData(dto);
    }

    @ApiOperation(description = "离线组装 - token转账交易")
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterType = "String", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "toAddress", parameterType = "String", parameterDes = "转入者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterType = "String", parameterDes = "token合约地址"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> tokenTransfer(String fromAddress, String toAddress, String contractAddress, BigInteger amount, String remark) {
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
        return this.callTxOffline(fromAddress, null, contractAddress, Constant.NRC20_METHOD_TRANSFER, null, new Object[]{toAddress, amount.toString()}, remark);
    }

    @ApiOperation(description = "离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易")
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterType = "String", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "toAddress", parameterType = "String", parameterDes = "转入的合约地址"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的主链资产金额"),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public Result<Map> tokenToContract(String fromAddress, String toAddress, BigInteger amount, String remark) {
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

        return this.callTxOffline(fromAddress, amount, toAddress, Constant.BALANCE_TRIGGER_METHOD_NAME, Constant.BALANCE_TRIGGER_METHOD_DESC, null, remark);
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
        params.put("chainId", SDKContext.main_chain_id);
        params.put("alias", form.getAlias());
        params.put("args", form.getArgs());
        params.put("contractCode", form.getContractCode());

        RestFulResult restFulResult = RestFulUtil.post("api/contract/create", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
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
        params.put("chainId", SDKContext.main_chain_id);
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
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result deleteContract(ContractDeleteForm form) {
        if (form == null) {
            return Result.getFailed(CommonCodeConstanst.PARAMETER_ERROR).setMsg("form data is empty");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("chainId", SDKContext.main_chain_id);
        params.put("sender", form.getSender());
        params.put("contractAddress", form.getContractAddress());
        params.put("password", form.getPassword());
        params.put("remark", form.getRemark());

        RestFulResult restFulResult = RestFulUtil.post("api/contract/delete", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
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
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
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
            result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

}
