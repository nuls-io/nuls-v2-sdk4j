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

    @ApiOperation(description = "创建账户")
    @Parameters(value = {
            @Parameter(parameterName = "count", parameterType = "Integer", parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = String.class)
    )
    public static Result<List<String>> createAccount(int count, String password) {
        return accountService.createAccount(count, password);
    }

    @ApiOperation(description = "离线创建账户")
    @Parameters(value = {
            @Parameter(parameterName = "count", parameterType = "Integer", parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class)
    )
    public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, password);
    }

    @ApiOperation(description = "重置密码")
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "oldPassword", parameterType = "String", parameterDes = "原始密码"),
            @Parameter(parameterName = "newPassword", parameterType = "String", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = Boolean.class, description = "是否修改成功")
    }))
    public static Result resetPassword(String address, String oldPassword, String newPassword) {
        return accountService.resetPassword(address, oldPassword, newPassword);
    }

    @ApiOperation(description = "离线重置密码")
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterType = "String", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "oldPassword", parameterType = "String", parameterDes = "原始密码"),
            @Parameter(parameterName = "newPassword", parameterType = "String", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", valueType = String.class, description = "重置密码后的加密私钥")
    }))
    public static Result resetPasswordOffline(String address, String encryptedPriKey, String password, String newPassword) {
        return accountService.resetPasswordOffline(address, encryptedPriKey, password, newPassword);
    }

    @ApiOperation(description = "根据私钥导入账户")
    @Parameters({
            @Parameter(parameterName = "priKey", parameterType = "String", parameterDes = "账户原始私钥"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importPriKey(String priKey, String password) {
        return accountService.importPriKey(priKey, password);
    }

    @ApiOperation(description = "获取账户私钥")
    @Parameters({
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "私钥")
    }))
    public static Result getPriKey(String address, String password) {
        return accountService.getPriKey(address, password);
    }

    @ApiOperation(description = "离线获取账户原始私钥")
    @Parameters({
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterType = "String", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "原始私钥")
    }))
    public static Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        return accountService.getPriKeyOffline(address, encryptedPriKey, password);
    }

    @ApiOperation(description = "导入keystore到钱包")
    @Parameters({
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterType = "String", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importKeystore(String keyStore, String password) {
        return accountService.importKeystore(keyStore, password);
    }

    @ApiOperation(description = "导出keystore到指定文件目录")
    @Parameters({
            @Parameter(parameterName = "address", parameterType = "String", parameterDes = "账户地址"),
            @Parameter(parameterName = "password", parameterType = "String", parameterDes = "密码"),
            @Parameter(parameterName = "filePath", parameterType = "String", parameterDes = "文件目录")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "path", description = "导出的文件路径")
    }))
    public static Result exportKeyStore(String address, String password, String filePath) {
        return accountService.exportKeyStore(address, password, filePath);
    }

    @ApiOperation(description = "查询账户余额")
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

    @ApiOperation(description = "摘要签名")
    @Parameters({
            @Parameter(parameterName = "摘要签名", parameterDes = "摘要签名表单", requestType = @TypeDescriptor(value = SignDto.class))
    })
    public static Result sign(List<SignDto> signDtoList, String txHex) {
        return accountService.sign(signDtoList, txHex);
    }

    /**
     * 明文私钥签名交易(单签)
     *
     * @param txHex
     * @param address
     * @param privateKey
     * @return
     */
    public static Result sign(String txHex, String address, String privateKey) {
        return transactionService.signTx(txHex, address, privateKey);
    }

    /**
     * 密文私钥签名交易(单签)
     *
     * @param txHex
     * @param address
     * @param encryptedPrivateKey
     * @param password
     * @return
     */
    public static Result sign(String txHex, String address, String encryptedPrivateKey, String password) {
        return transactionService.signTx(txHex, address, encryptedPrivateKey, password);
    }

    /**
     * 创建转账交易(离线)
     * create transfer transaction(off-line)
     *
     * @param transferDto 转账请求参数
     * @return
     */
    public static Result createTransferTx(TransferDto transferDto) {
        return transactionService.createTransferTx(transferDto);
    }

    /**
     * 计算转账交易手续费
     * Calculate transfer transaction fee
     *
     * @param dto 请求参数
     * @return result
     */
    public static BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        return transactionService.calcTransferTxFee(dto);
    }

    /**
     * 组装创建共识节点交易
     * Assemble to create consensus node transactions
     *
     * @param consensusDto 创建共识节点请求参数
     * @return result
     */
    public static Result createConsensusTx(ConsensusDto consensusDto) {
        return transactionService.createConsensusTx(consensusDto);
    }

    /**
     * Create a proxy consensus transaction
     * 创建委托共识交易
     *
     * @param dto 委托共识请求参数
     * @return
     */
    public static Result createDepositTx(DepositDto dto) {
        return transactionService.createDepositTx(dto);
    }

    /**
     * 创建取消委托交易
     *
     * @param dto 取消委托交易参数
     * @return result
     */
    public static Result createWithdrawDepositTx(WithDrawDto dto) {
        return transactionService.createWithdrawDepositTx(dto);
    }

    /**
     * 创建注销共识节点交易
     *
     * @param dto 注销节点参数请求
     * @return result
     */
    public static Result createStopConsensusTx(StopConsensusDto dto) {
        return transactionService.createStopConsensusTx(dto);
    }

    /**
     * 发送转账交易
     *
     * @param transferForm 转账交易参数
     * @return result
     */
    public static Result transfer(TransferForm transferForm) {
        return transactionService.transfer(transferForm);
    }

    public static Result getTx(String txHash) {
        return transactionService.getTx(txHash);
    }

    public static Result getBlockHeader(long height) {
        return blockService.getBlockHeader(height);
    }

    public static Result getBlockHeader(String hash) {
        return blockService.getBlockHeader(hash);
    }

    public static Result getBlock(long height) {
        return blockService.getBlock(height);
    }

    public static Result getBlock(String hash) {
        return blockService.getBlock(hash);
    }

    public static Result getBestBlockHeader() {
        return blockService.getBestBlockHeader();
    }

    public static Result getBestBlock() {
        return blockService.getBestBlock();
    }

    public static Result createAgent(CreateAgentForm form) {
        return consensusService.createAgent(form);
    }

    public static Result stopAgent(StopAgentForm form) {
        return consensusService.stopAgent(form);
    }

    public static Result depositToAgent(DepositForm form) {
        return consensusService.depositToAgent(form);
    }

    public static Result withdraw(WithdrawForm form) {
        return consensusService.withdraw(form);
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
    public static Result<Map> createTxOffline(String sender, String alias, String contractCode, Object[] args, String remark) {
        return contractService.createTxOffline(sender, alias, contractCode, args, remark);
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
    public static Result<Map> callTxOffline(String sender, BigInteger value, String contractAddress, String methodName, String methodDesc, Object[] args, String remark) {
        return contractService.callTxOffline(sender, value, contractAddress, methodName, methodDesc, args, remark);
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
    public static Result<Map> deleteTxOffline(String sender, String contractAddress, String remark) {
        return contractService.deleteTxOffline(sender, contractAddress, remark);
    }

    @ApiOperation(description = "离线组装 - token转账交易")
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterType = "String", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "toAddress", parameterType = "String", parameterDes = "转入地址"),
            @Parameter(parameterName = "contractAddress", parameterType = "String", parameterDes = "token合约地址"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterType = "String", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> tokenTransfer(String fromAddress, String toAddress, String contractAddress, BigInteger amount, String remark) {
        return contractService.tokenTransfer(fromAddress, toAddress, contractAddress, amount, remark);
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
    public static Result<Map> tokenToContract(String fromAddress, String toAddress, BigInteger amount, String remark) {
        return contractService.tokenToContract(fromAddress, toAddress, amount, remark);
    }

    @ApiOperation(description = "根据合约代码获取合约构造函数详情")
    @Parameters(description = "参数", value = {
            @Parameter(parameterName = "contractCode", parameterType = "String", parameterDes = "智能合约代码(字节码的Hex编码字符串)")
    })
    @ResponseData(name = "返回值", description = "合约构造函数详情", responseType = @TypeDescriptor(value = ContractConstructorInfoDto.class))
    public static Result<ContractConstructorInfoDto> getConstructor(String contractCode) {
        return contractService.getConstructor(contractCode);
    }
}
