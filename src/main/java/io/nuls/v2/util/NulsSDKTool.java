package io.nuls.v2.util;

import io.nuls.core.basic.Result;
import io.nuls.core.rpc.model.*;
import io.nuls.v2.model.annotation.ApiOperation;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.service.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class NulsSDKTool {

    private static AccountService accountService = AccountService.getInstance();

    private static TransactionService transactionService = TransactionService.getInstance();

    private static BlockService blockService = BlockService.getInstance();

    private static ConsensusService consensusService = ConsensusService.getInstance();

    private static ContractService contractService = ContractService.getInstance();

    @ApiOperation(description = "创建账户", order = 101)
    @Parameters(value = {
            @Parameter(parameterName = "count", parameterType = "Integer", parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = String.class)
    )
    public static Result<List<String>> createAccount(int count, String password) {
        return accountService.createAccount(count, password);
    }

    @ApiOperation(description = "重置密码", order = 102)
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "oldPassword", parameterDes = "原密码"),
            @Parameter(parameterName = "newPassword", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = Boolean.class, description = "是否修改成功")
    }))
    public static Result resetPassword(String address, String oldPassword, String newPassword) {
        return accountService.resetPassword(address, oldPassword, newPassword);
    }

    @ApiOperation(description = "根据私钥导入账户", order = 103)
    @Parameters({
            @Parameter(parameterName = "priKey", parameterDes = "账户明文私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importPriKey(String priKey, String password) {
        return accountService.importPriKey(priKey, password);
    }

    @ApiOperation(description = "获取账户私钥", order = 104)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "私钥")
    }))
    public static Result getPriKey(String address, String password) {
        return accountService.getPriKey(address, password);
    }

    @ApiOperation(description = "导入keystore到钱包", order = 105)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importKeystore(String keyStore, String password) {
        return accountService.importKeystore(keyStore, password);
    }

    @ApiOperation(description = "导出keystore到指定文件目录", order = 106)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterDes = "密码"),
            @Parameter(parameterName = "filePath", parameterDes = "文件目录")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "path", description = "导出的文件路径")
    }))
    public static Result exportKeyStore(String address, String password, String filePath) {
        return accountService.exportKeyStore(address, password, filePath);
    }

    @ApiOperation(description = "查询账户余额", order = 107)
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "total", description = "总余额"),
            @Key(name = "freeze", description = "锁定金额"),
            @Key(name = "available", description = "可用余额")
    }))
    public static Result getAccountBalance(String address) {
        return accountService.getAccountBalance(address);
    }

    @ApiOperation(description = "多账户摘要签名", order = 108)
    @Parameters({
            @Parameter(parameterName = "signDtoList", parameterDes = "摘要签名表单", requestType = @TypeDescriptor(value = SignDto.class)),
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(List<SignDto> signDtoList, String txHex) {
        return accountService.sign(signDtoList, txHex);
    }

    @ApiOperation(description = "明文私钥摘要签名", order = 109)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串"),
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "privateKey", parameterDes = "账户明文私钥")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(String txHex, String address, String privateKey) {
        return transactionService.signTx(txHex, address, privateKey);
    }

    @ApiOperation(description = "密文私钥摘要签名", order = 110)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串"),
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPrivateKey", parameterDes = "账户密文私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result sign(String txHex, String address, String encryptedPrivateKey, String password) {
        return transactionService.signTx(txHex, address, encryptedPrivateKey, password);
    }

    @ApiOperation(description = "离线创建账户", order = 151)
    @Parameters(value = {
            @Parameter(parameterName = "count", parameterType = "Integer", parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class)
    )
    public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, password);
    }

    @ApiOperation(description = "离线重置密码", order = 152)
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "oldPassword", parameterDes = "原密码"),
            @Parameter(parameterName = "newPassword", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = String.class, description = "重置密码后的加密私钥")
    }))
    public static Result resetPasswordOffline(String address, String encryptedPriKey, String password, String newPassword) {
        return accountService.resetPasswordOffline(address, encryptedPriKey, password, newPassword);
    }

    @ApiOperation(description = "离线获取账户明文私钥", order = 153)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "明文私钥")
    }))
    public static Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        return accountService.getPriKeyOffline(address, encryptedPriKey, password);
    }

    @ApiOperation(description = "根据区块高度查询区块头", order = 201)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(long height) {
        return blockService.getBlockHeader(height);
    }

    @ApiOperation(description = "根据区块hash查询区块头", order = 202)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(String hash) {
        return blockService.getBlockHeader(hash);
    }

    @ApiOperation(description = "根据区块高度查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 203)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(long height) {
        return blockService.getBlock(height);
    }

    @ApiOperation(description = "根据区块hash查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 204)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(String hash) {
        return blockService.getBlock(hash);
    }

    @ApiOperation(description = "查询最新区块头信息", order = 205)
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBestBlockHeader() {
        return blockService.getBestBlockHeader();
    }

    @ApiOperation(description = "查询最新区块", order = 206)
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = BlockDto.class))
    public static Result getBestBlock() {
        return blockService.getBestBlock();
    }

    @ApiOperation(description = "转账", order = 301)
    @Parameters({
            @Parameter(parameterName = "transferForm", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result transfer(TransferForm transferForm) {
        return transactionService.transfer(transferForm);
    }

    @ApiOperation(description = "计算创建转账交易所需手续费", order = 302)
    @Parameters({
            @Parameter(parameterName = "TransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = TransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        return transactionService.calcTransferTxFee(dto);
    }

    @ApiOperation(description = "根据hash获取交易，只查已确认交易", order = 303)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "交易hash")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = TransactionDto.class))
    public static Result getTx(String txHash) {
        return transactionService.getTx(txHash);
    }

    @ApiOperation(description = "验证交易是否正确", order = 304)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result validateTx(String txHex) {
        return transactionService.validateTx(txHex);
    }

    @ApiOperation(description = "广播交易", order = 305)
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = boolean.class, description = "是否成功"),
            @Key(name = "hash", description = "交易hash")
    }))
    public static Result broadcast(String txHex) {
        return transactionService.broadcastTx(txHex);
    }

    @ApiOperation(description = "离线组装转账交易", order = 351)
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTransferTxOffline(TransferDto transferDto) {
        return transactionService.createTransferTx(transferDto);
    }

    @ApiOperation(description = "发布合约", order = 401)
    @Parameters({
            @Parameter(parameterName = "发布合约", parameterDes = "发布合约表单", requestType = @TypeDescriptor(value = ContractCreateForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象，包含两个属性", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHash", description = "发布合约的交易hash"),
            @Key(name = "contractAddress", description = "生成的合约地址")
    }))
    public static Result createContract(ContractCreateForm form) {
        return contractService.createContract(form);
    }

    @ApiOperation(description = "调用合约", order = 402)
    @Parameters({
            @Parameter(parameterName = "调用合约", parameterDes = "调用合约表单", requestType = @TypeDescriptor(value = ContractCallForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHash", description = "调用合约的交易hash")
    }))
    public static Result callContract(ContractCallForm form) {
        return contractService.callContract(form);
    }

    @ApiOperation(description = "删除合约", order = 403)
    @Parameters({
            @Parameter(parameterName = "删除合约", parameterDes = "删除合约表单", requestType = @TypeDescriptor(value = ContractDeleteForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHash", description = "删除合约的交易hash")
    }))
    public static Result deleteContract(ContractDeleteForm form) {
        return contractService.deleteContract(form);
    }

    @ApiOperation(description = "获取账户地址的指定token余额", order = 404)
    @Parameters({
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "address", parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = ContractTokenInfoDto.class))
    public static Result getTokenBalance(String contractAddress, String address) {
        return contractService.getTokenBalance(contractAddress, address);
    }

    @ApiOperation(description = "获取智能合约详细信息", order = 405)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "合约地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = ContractInfoDto.class))
    public static Result getContractInfo(String contractAddress) {
        return contractService.getContractInfo(contractAddress);
    }


    @ApiOperation(description = "根据合约代码获取合约构造函数详情", order = 406)
    @Parameters(description = "参数", value = {
            @Parameter(parameterName = "contractCode", parameterDes = "智能合约代码(字节码的Hex编码字符串)")
    })
    @ResponseData(name = "返回值", description = "合约构造函数详情", responseType = @TypeDescriptor(value = ContractConstructorInfoDto.class))
    public static Result<ContractConstructorInfoDto> getConstructor(String contractCode) {
        return contractService.getConstructor(contractCode);
    }

    @ApiOperation(description = "离线组装 - 发布合约的交易", order = 451)
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "alias", parameterDes = "合约别名"),
            @Parameter(parameterName = "contractCode", parameterDes = "智能合约代码(字节码的Hex编码字符串)"),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串"),
            @Key(name = "contractAddress", description = "生成的合约地址")
    }))
    public static Result<Map> createContractTxOffline(String sender, String alias, String contractCode, Object[] args, String remark) {
        return contractService.createContractTxOffline(sender, alias, contractCode, args, remark);
    }

    @ApiOperation(description = "离线组装 - 调用合约的交易", order = 452)
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "value", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "methodName", parameterDes = "合约方法"),
            @Parameter(parameterName = "methodDesc", parameterDes = "合约方法描述，若合约内方法没有重载，则此参数可以为空", canNull = true),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> callContractTxOffline(String sender, BigInteger value, String contractAddress, String methodName, String methodDesc, Object[] args, String remark) {
        return contractService.callContractTxOffline(sender, value, contractAddress, methodName, methodDesc, args, remark);
    }

    @ApiOperation(description = "离线组装 - 删除合约的交易", order = 453)
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> deleteContractTxOffline(String sender, String contractAddress, String remark) {
        return contractService.deleteContractTxOffline(sender, contractAddress, remark);
    }

    @ApiOperation(description = "离线组装 - token转账交易", order = 454)
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入地址"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> tokenTransferTxOffline(String fromAddress, String toAddress, String contractAddress, BigInteger amount, String remark) {
        return contractService.tokenTransferTxOffline(fromAddress, toAddress, contractAddress, amount, remark);
    }

    @ApiOperation(description = "离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易", order = 455)
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入的合约地址"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的主链资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> transferToContractTxOffline(String fromAddress, String toAddress, BigInteger amount, String remark) {
        return contractService.transferToContractTxOffline(fromAddress, toAddress, amount, remark);
    }

    @ApiOperation(description = " 创建共识节点", order = 501)
    @Parameters({
            @Parameter(parameterName = "创建共识(代理)节点", parameterDes = "创建共识(代理)节点表单", requestType = @TypeDescriptor(value = CreateAgentForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result createAgent(CreateAgentForm form) {
        return consensusService.createAgent(form);
    }

    @ApiOperation(description = "注销共识节点", order = 502)
    @Parameters({
            @Parameter(parameterName = "注销共识节点", parameterDes = "注销共识节点表单", requestType = @TypeDescriptor(value = StopAgentForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result stopAgent(StopAgentForm form) {
        return consensusService.stopAgent(form);
    }

    @ApiOperation(description = "deposit nuls to a bank! 申请参与共识", order = 503)
    @Parameters({
            @Parameter(parameterName = "申请参与共识", parameterDes = "申请参与共识表单", requestType = @TypeDescriptor(value = DepositForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result depositToAgent(DepositForm form) {
        return consensusService.depositToAgent(form);
    }

    @ApiOperation(description = "退出共识", order = 504)
    @Parameters({
            @Parameter(parameterName = "退出共识", parameterDes = "退出共识表单", requestType = @TypeDescriptor(value = WithdrawForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result withdraw(WithdrawForm form) {
        return consensusService.withdraw(form);
    }

    @ApiOperation(description = "离线组装创建共识节点交易", order = 551)
    @Parameters({
            @Parameter(parameterName = "consensusDto", parameterDes = "创建节点交易表单", requestType = @TypeDescriptor(value = ConsensusDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createConsensusTxOffline(ConsensusDto consensusDto) {
        return transactionService.createConsensusTx(consensusDto);
    }

    @ApiOperation(description = "离线组装委托共识交易", order = 552)
    @Parameters({
            @Parameter(parameterName = "depositDto", parameterDes = "委托共识交易表单", requestType = @TypeDescriptor(value = DepositDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createDepositTxOffline(DepositDto depositDto) {
        return transactionService.createDepositTx(depositDto);
    }

    @ApiOperation(description = "离线组装退出委托共识交易", order = 553)
    @Parameters({
            @Parameter(parameterName = "withDrawDto", parameterDes = "退出委托交易表单", requestType = @TypeDescriptor(value = WithDrawDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createWithdrawDepositTxOffline(WithDrawDto withDrawDto) {
        return transactionService.createWithdrawDepositTx(withDrawDto);
    }

    @ApiOperation(description = "离线组装注销共识节点交易", order = 554)
    @Parameters({
            @Parameter(parameterName = "stopConsensusDto", parameterDes = "注销共识节点交易表单", requestType = @TypeDescriptor(value = StopConsensusDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createStopConsensusTxOffline(StopConsensusDto stopConsensusDto) {
        return transactionService.createStopConsensusTx(stopConsensusDto);
    }
}
