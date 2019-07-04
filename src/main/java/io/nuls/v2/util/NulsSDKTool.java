package io.nuls.v2.util;

import io.nuls.core.basic.Result;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.service.*;

import java.math.BigInteger;
import java.util.List;

public class NulsSDKTool {

    private static AccountService accountService = AccountService.getInstance();

    private static TransactionService transactionService = TransactionService.getInstance();

    private static AccountTxService accountTxService = AccountTxService.getInstance();

    private static BlockService blockService = BlockService.getInstance();

    private static ConsensusService consensusService = ConsensusService.getInstance();

    /**
     * Create accounts
     * 批量创建账户
     *
     * @param count    The number of accounts you want to create
     * @param password The password of the account
     * @return result
     */
    public static Result<List<String>> createAccount(int count, String password) {
        return accountService.createAccount(count, password);
    }

    /**
     * Create encrypted off-line accounts
     * 批量创建离线账户
     *
     * @param count    The number of accounts you want to create
     * @param password The password of the account
     * @return result
     */
    public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, password);
    }

    public static Result resetPassword(String address, String oldPassword, String newPassword) {
        return accountService.resetPassword(address, oldPassword, newPassword);
    }

    /**
     * Change the off-line account password by encryptedPriKey and passowrd
     *
     * @param address         The address of account
     * @param encryptedPriKey The encrypted Private Key
     * @param password        The password to use when encrypting the private key
     * @param newPassword     The new password
     * @return Result
     */
    public static Result resetPasswordOffline(String address, String encryptedPriKey, String password, String newPassword) {
        return accountService.resetPasswordOffline(address, encryptedPriKey, password, newPassword);
    }

    /**
     * 导入私钥
     *
     * @param priKey   私钥
     * @param password 导入私钥后，给私钥设置的密码
     * @return result
     */
    public static Result importPriKey(String priKey, String password) {
        return accountService.importPriKey(priKey, password);
    }

    /**
     * get unencrypted private-key
     * 获取钱包账户原始私钥
     *
     * @param address  账户地址
     * @param password 密码
     * @return Result
     */
    public static Result getPriKey(String address, String password) {
        return accountService.getPriKey(address, password);
    }


    /**
     * get off-line address unencrypted private-key
     * 获取离线账户原始私钥
     *
     * @param address         账户地址
     * @param encryptedPriKey 账户加密后的私钥
     * @param password        密码
     * @return Result
     */
    public static Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        return accountService.getPriKeyOffline(address, encryptedPriKey, password);
    }

    /**
     * 导入keystore到钱包
     *
     * @param keyStore keyStore json字符串
     * @param password 密码
     * @return result
     */
    public static Result importKeystore(String keyStore, String password) {
        return accountService.importKeystore(keyStore, password);
    }

    /**
     * 导出keystore
     *
     * @param address  地址
     * @param password 密码
     * @param filePath 导出文件路径
     * @return result
     */
    public static Result exportKeyStore(String address, String password, String filePath) {
        return accountService.exportKeyStore(address, password, filePath);
    }

    /**
     * 获取账户余额
     *
     * @param address 地址
     * @return result
     */
    public static Result getAccountBalance(String address) {
        return accountService.getAccountBalance(address);
    }

    /**
     * sign the tx's digest
     *
     * @param signDtoList 签名请求参数
     * @return result
     */
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
        return accountTxService.transfer(transferForm);
    }


    public static Result getTx(String txHash) {
        return accountTxService.getTx(txHash);
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


}
