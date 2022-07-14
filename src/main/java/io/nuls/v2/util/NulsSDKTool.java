package io.nuls.v2.util;

import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.Transaction;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.parse.I18nUtils;
import io.nuls.core.rpc.model.*;
import io.nuls.v2.SDKContext;
import io.nuls.v2.enums.EncodeType;
import io.nuls.v2.model.annotation.ApiOperation;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.service.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static io.nuls.v2.util.ContractUtil.getSuccess;

public class NulsSDKTool {

    private static AccountService accountService = AccountService.getInstance();

    private static TransactionService transactionService = TransactionService.getInstance();

    private static BlockService blockService = BlockService.getInstance();

    private static ConsensusService consensusService = ConsensusService.getInstance();

    private static ContractService contractService = ContractService.getInstance();

    private static final String LANGUAGE = "en";
    private static final String LANGUAGE_PATH = "languages";

    static {
        I18nUtils.loadLanguage(NulsSDKTool.class, LANGUAGE_PATH, LANGUAGE);
    }

    @ApiOperation(description = "获取本链相关信息,其中共识资产为本链创建共识节点交易和创建委托共识交易时，需要用到的资产", order = 001)
    @ResponseData(name = "返回值", description = "返回本链信息", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "chainId", description = "本链的ID"),
            @Key(name = "assetId", description = "本链默认主资产的ID"),
            @Key(name = "inflationAmount", description = "本链默认主资产的初始数量"),
            @Key(name = "agentChainId", description = "本链共识资产的链ID"),
            @Key(name = "agentAssetId", description = "本链共识资产的ID")
    }))
    public static Result<Map> getInfo() {
        return blockService.getInfo();
    }

    @ApiOperation(description = "批量创建账户", order = 101, detailDesc = "创建的账户存在于本地钱包内")
    @Parameters(value = {
            @Parameter(parameterName = "count", requestType = @TypeDescriptor(value = int.class), parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回账户地址集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = String.class)
    )
    public static Result<List<String>> createAccount(int count, String password) {
        return accountService.createAccount(count, password);
    }

    @ApiOperation(description = "修改账户密码", order = 102)
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

    @ApiOperation(description = "导出账户私钥", order = 103, detailDesc = "只能导出本地钱包已存在账户的私钥")
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

    @ApiOperation(description = "根据私钥导入账户", order = 104, detailDesc = "导入私钥时，需要输入密码给明文私钥加密")
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

    @ApiOperation(description = "根据keystore导入账户", order = 105)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "pubKey", parameterDes = "公钥"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户地址")
    }))
    public static Result importKeystore(AccountKeyStoreDto keyStore, String password) {
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

    @ApiOperation(description = "查询账户余额", order = 107, detailDesc = "根据资产链ID和资产ID，查询本链账户对应资产的余额与nonce值")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址"),
            @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "资产的链ID"),
            @Parameter(parameterName = "assetsId", requestType = @TypeDescriptor(value = int.class), parameterDes = "资产ID")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = AccountBalanceDto.class))
    public static Result getAccountBalance(String address, int chainId, int assetsId) {
        return accountService.getAccountBalance(address, chainId, assetsId);
    }

    @ApiOperation(description = "设置账户别名", order = 108, detailDesc = "别名格式为1-20位小写字母和数字的组合，设置别名会销毁1个NULS")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址"),
            @Parameter(parameterName = "alias", requestType = @TypeDescriptor(value = String.class), parameterDes = "别名"),
            @Parameter(parameterName = "password", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "设置别名交易的hash")
    }))
    public static Result setAlias(String address, String alias, String password) {
        return accountService.setAlias(address, alias, password);
    }

    @ApiOperation(description = "验证地址格式是否正确", order = 109, detailDesc = "验证本链地址格式是否正确")
    @Parameters({
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class))
    public static Result validateAddress(String address) {
        return accountService.validateAddress(SDKContext.main_chain_id, address);
    }

    @ApiOperation(description = "验证地址格式是否正确", order = 110, detailDesc = "根据chainId验证地址格式是否正确")
    @Parameters({
            @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "链ID"),
            @Parameter(parameterName = "address", requestType = @TypeDescriptor(value = String.class), parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class))
    public static Result validateAddress(int chainId, String address) {
        return accountService.validateAddress(chainId, address);
    }



    @ApiOperation(description = "离线 - 批量创建账户", order = 150, detailDesc = "创建的账户不会保存到钱包中,接口直接返回账户的keystore信息")
    @Parameters(value = {
            @Parameter(parameterName = "count", requestType = @TypeDescriptor(value = int.class), parameterDes = "创建数量"),
            @Parameter(parameterName = "password", parameterDes = "密码")
    })
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class)
    )
    public static Result<List<AccountDto>> createOffLineAccount(int count, String password) {
        return accountService.createOffLineAccount(count, password);
    }

    @ApiOperation(description = "离线 - 批量创建地址带固定前缀的账户", order = 151, detailDesc = "创建的账户不会保存到钱包中,接口直接返回账户的keystore信息")
    @Parameters(
            value = {
                    @Parameter(parameterName = "chainId", requestType = @TypeDescriptor(int.class), parameterDes = "地址对应的链Id"),
                    @Parameter(parameterName = "count", requestType = @TypeDescriptor(int.class), parameterDes = "创建数量"),
                    @Parameter(parameterName = "prefix", requestType = @TypeDescriptor(String.class), canNull = true, parameterDes = "地址前缀"),
                    @Parameter(parameterName = "password", parameterDes = "密码")
            }
    )
    @ResponseData(name = "返回值", description = "返回一个账户keystore集合",
            responseType = @TypeDescriptor(value = List.class, collectionElement = AccountDto.class))
    public static Result<List<AccountDto>> createOffLineAccount(int chainId, int count, String prefix, String password) {
        return accountService.createOffLineAccount(chainId, count, prefix, password);
    }

    @ApiOperation(description = "离线修改账户密码", order = 152)
    @Parameters(value = {
            @Parameter(parameterName = "address", parameterDes = "账户地址"),
            @Parameter(parameterName = "encryptedPriKey", parameterDes = "加密后的私钥"),
            @Parameter(parameterName = "oldPassword", parameterDes = "原密码"),
            @Parameter(parameterName = "newPassword", parameterDes = "新密码")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "重置密码后的加密私钥")
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

    @ApiOperation(description = "多账户摘要签名", order = 154, detailDesc = "用于签名离线组装的多账户转账交易，调用接口时，参数可以传地址和私钥，或者传地址和加密私钥和加密密码")
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

    public static Result sign(int chainId, String prefix, List<SignDto> signDtoList, String txHex) {
        return accountService.sign(chainId, prefix, signDtoList, txHex);
    }

    @ApiOperation(description = "多签账户摘要签名", order = 155, detailDesc = "用于签名离线组装的多签账户转账交易，每次调用接口时，只能传入一个账户的私钥进行签名，签名成功后返回的交易字符串再交给第二个账户签名，依次类推")
    @Parameters({
            @Parameter(parameterName = "signDto", parameterDes = "摘要签名表单", requestType = @TypeDescriptor(value = SignDto.class)),
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "签名后的交易16进制字符串")
    }))
    public static Result multiSign(SignDto signDto, String txHex) {
        return accountService.multiSign(signDto, txHex);
    }

    @ApiOperation(description = "明文私钥摘要签名", order = 156)
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

    @ApiOperation(description = "密文私钥摘要签名", order = 157)
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

    public static Result sign(String txHex, int chainId, String prefix, String address, String encryptedPrivateKey, String password) {
        return accountService.sign(txHex, chainId, prefix, address, encryptedPrivateKey, password);
    }

    public Result multiSign(int chainId, String prefix, String address, String encryptedPrivateKey, String password, String txHex) {
        return accountService.multiSign(chainId, prefix, address, encryptedPrivateKey, password, txHex);
    }

    @ApiOperation(description = "创建多签账户", order = 158, detailDesc = "根据多个账户的公钥创建多签账户，minSigns为多签账户创建交易时需要的最小签名数")
    @Parameters(value = {
            @Parameter(parameterName = "pubKeys", requestType = @TypeDescriptor(value = List.class, collectionElement = String.class), parameterDes = "账户公钥集合"),
            @Parameter(parameterName = "minSigns", requestType = @TypeDescriptor(value = int.class), parameterDes = "最小签名数")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户的地址")
    }))
    public static Result createMultiSignAccount(List<String> pubKeys, int minSigns) {
        return accountService.createMultiSignAccount(pubKeys, minSigns);
    }

    @ApiOperation(description = "根据私钥获取地址", order = 159, detailDesc = "根据传入的私钥，生成对应的地址，私钥不会存储在钱包里")
    @Parameters(value = {
            @Parameter(parameterName = "priKey", parameterDes = "原始私钥")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "账户的地址")
    }))
    public static Result getAddressByPriKey(String priKey) {
        return accountService.getAddressByPriKey(priKey);
    }

    @ApiOperation(description = "转换NULS1.0地址为NULS2.0地址", order = 160, detailDesc = "转换NULS1.0地址为NULS2.0地址")
    @Parameters({
            @Parameter(parameterName = "v1Address", requestType = @TypeDescriptor(value = String.class), parameterDes = "NULS1.0账户地址")
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "NULS2.0地址")
    }))
    public static Result changeV1addressToV2address(String v1Address) {
        return accountService.changeV1addressToV2address(v1Address);
    }
    @ApiOperation(description = "消息解密", order = 161)
    @Parameters(value = {
            @Parameter(parameterName = "privateKey", parameterDes = "私钥"),
            @Parameter(parameterName = "encryptMsg", parameterDes = "加密消息"),
            @Parameter(parameterName = "encodeType", parameterDes = "解密消息的编码方式: HEX, UTF8"),
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "解密消息")
    }))
    public static Result decryptData(String privateKey, String encryptMsg, EncodeType encodeType) {
        return accountService.decryptData(privateKey, encryptMsg, encodeType);
    }

    @ApiOperation(description = "根据区块高度查询区块头", order = 201)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(long height) {
        return blockService.getBlockHeader(height);
    }

    @ApiOperation(description = "根据区块hash查询区块头", order = 202)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlockHeader(String hash) {
        return blockService.getBlockHeader(hash);
    }

    @ApiOperation(description = "根据区块高度查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 203)
    @Parameters({
            @Parameter(parameterName = "height", requestType = @TypeDescriptor(value = Long.class), parameterDes = "区块高度")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(long height) {
        return blockService.getBlock(height);
    }

    @ApiOperation(description = "根据区块hash查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用", order = 204)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "区块hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBlock(String hash) {
        return blockService.getBlock(hash);
    }

    @ApiOperation(description = "查询最新区块头信息", order = 205)
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockHeaderDto.class))
    public static Result getBestBlockHeader() {
        return blockService.getBestBlockHeader();
    }

    @ApiOperation(description = "查询最新区块", order = 206)
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = BlockDto.class))
    public static Result getBestBlock() {
        return blockService.getBestBlock();
    }

    @ApiOperation(description = "根据hash查询交易详情", order = 301)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "交易hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = TransactionDto.class))
    public static Result getTx(String txHash) {
        return transactionService.getTx(txHash);
    }

    public static Result getTransaction(String txHash) {
        return transactionService.getTransaction(txHash);
    }

    @ApiOperation(description = "验证交易", order = 302, detailDesc = "验证离线组装的交易,验证成功返回交易hash值,失败返回错误提示信息")
    @Parameters({
            @Parameter(parameterName = "txHex", parameterDes = "交易序列化16进制字符串")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result validateTx(String txHex) {
        return transactionService.validateTx(txHex);
    }

    @ApiOperation(description = "广播交易", order = 303, detailDesc = "广播离线组装的交易,成功返回true,失败返回错误提示信息")
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

    @ApiOperation(description = "单笔转账", order = 304, detailDesc = "发起单账户单资产的转账交易")
    @Parameters({
            @Parameter(parameterName = "transferForm", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result transfer(TransferForm transferForm) {
        return transactionService.transfer(transferForm);
    }

    @ApiOperation(description = "单笔跨链转账", order = 305, detailDesc = "发起单账户单资产的跨链转账交易")
    @Parameters({
            @Parameter(parameterName = "transferForm", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = CrossTransferForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "value", description = "交易hash")
    }))
    public static Result crossTransfer(CrossTransferForm transferForm) {
        return transactionService.crossTransfer(transferForm);
    }

    @ApiOperation(description = "离线组装转账交易", order = 350, detailDesc = "根据inputs和outputs离线组装转账交易，用于单账户或多账户的转账交易。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
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

    /**
     * 便捷版 组装在NULS链内，转账非NULS资产的单账户对单账户普通转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费。
     *
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NULS地址）
     * @param toAddress 转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId 转账资产id
     * @param amount 到账数量
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内非NULS资产转账交易", order = 350, detailDesc = "组装在NULS链内，转账非NULS资产的单账户对单账户普通转账。" +
            "该方法会主动用fromAddress组装(NULS资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return transactionService.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount);
    }

    /**
     * 便捷版 组装在NULS链内，转账非NULS资产的单账户对单账户普通转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费。
     *
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NULS地址）
     * @param toAddress 转入地址（NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId 转账资产id
     * @param amount 到账数量
     * @param time 交易时间
     * @param remark 备注
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内非NULS资产转账交易", order = 350, detailDesc = "组装在NULS链内，转账非NULS资产的单账户对单账户普通转账。" +
            "该方法会主动用fromAddress组装(NULS资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        return transactionService.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
    }

    /**
     * 便捷版 组装在NULS链内，转账NULS资产的单账户对单账户普通转账(只能用于转NULS)。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     *
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NULS地址）
     * @param toAddress 转入地址（NULS地址）
     * @param amount 到账数量（不含手续费）
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内NULS资产转账交易", order = 350, detailDesc = "组装在NULS链内，转账NULS资产的单账户对单账户普通转账。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return transactionService.createTxSimpleTransferOfNuls(fromAddress, toAddress, amount);
    }

    /**
     * 便捷版 组装在NULS链内，转账NULS资产的单账户对单账户普通转账(只能用于转NULS)。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     *
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NULS地址）
     * @param toAddress 转入地址（NULS地址）
     * @param amount 到账数量（不含手续费）
     * @param time 交易时间
     * @param remark 备注
     * @return 交易hex
     */
    @ApiOperation(description = "离线组装链内NULS资产转账交易", order = 350, detailDesc = "组装在NULS链内，转账NULS资产的单账户对单账户普通转账。" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        return transactionService.createTxSimpleTransferOfNuls(fromAddress, toAddress, amount, time, remark);
    }



    /**
     * 跨链交易
     * 便捷版 组装跨链转账非NULS资产的单账户对单账户普通跨链转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NULS地址）
     * @param toAddress    转入地址（非NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @param time         交易时间
     * @param remark       备注
     * @return
     */
    @ApiOperation(description = "离线组装跨链非NULS资产转账交易", order = 350, detailDesc = "组装跨链转账非NULS资产的单账户对单账户普通跨链转账(不能用于转NULS)。" +
            "该方法会主动用fromAddress组装(NULS资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        return transactionService.createCrossTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
    }

    /**
     * 跨链交易
     * 便捷版 组装跨链转账非NULS资产的单账户对单账户普通跨链转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NULS地址）
     * @param toAddress    转入地址（非NULS地址）
     * @param assetChainId 转账资产链id
     * @param assetId      转账资产id
     * @param amount       转账token数量
     * @return
     */
    @ApiOperation(description = "离线组装跨链非NULS资产转账交易", order = 350, detailDesc = "组装跨链转账非NULS资产的单账户对单账户普通跨链转账(不能用于转NULS)。" +
            "该方法会主动用fromAddress组装(NULS资产)打包手续费")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "assetChainId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产链id"),
            @Parameter(parameterName = "assetId", requestType = @TypeDescriptor(value = int.class), parameterDes = "转账资产id"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return transactionService.createCrossTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount);
    }

    /**
     * 跨链交易
     * 便捷版 组装跨链转账NULS资产的单账户对单账户普通跨链转账（只能用于转NULS）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress （非NULS地址）
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    @ApiOperation(description = "离线组装跨链NULS资产转账交易", order = 350, detailDesc = "组装跨链转账NULS资产的单账户对单账户普通跨链转账（只能用于转NULS）" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", requestType = @TypeDescriptor(value = String.class), parameterDes = "备注")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        return transactionService.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount, time, remark);
    }


    /**
     * 跨链交易
     * 便捷版 组装跨链转账NULS资产的单账户对单账户普通跨链转账（只能用于转NULS）。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress （非NULS地址）
     * @param amount
     * @return
     */
    @ApiOperation(description = "离线组装跨链NULS资产转账交易", order = 350, detailDesc = "组装跨链转账NULS资产的单账户对单账户普通跨链转账（只能用于转NULS）" +
            "打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。")
    @Parameters({
            @Parameter(parameterName = "fromAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转出地址(NULS地址)"),
            @Parameter(parameterName = "toAddress", requestType = @TypeDescriptor(value = String.class), parameterDes = "转入地址(NULS地址)"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "到账数量")
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return transactionService.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount);
    }



    @ApiOperation(description = "离线组装跨链转账交易", order = 350, detailDesc = "根据inputs和outputs离线组装转账交易，用于单账户或多账户的转账交易。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = TransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createCrossTransferTxOffline(TransferDto transferDto) {
        return transactionService.createCrossTransferTx(transferDto);
    }

    @ApiOperation(description = "计算离线创建转账交易所需手续费", order = 351)
    @Parameters({
            @Parameter(parameterName = "TransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = TransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        return transactionService.calcTransferTxFee(dto);
    }

    @ApiOperation(description = "计算离线创建跨链转账交易所需手续费(不建议使用)", order = 351)
    @Parameters({
            @Parameter(parameterName = "CrossTransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = CrossTransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = Map.class))
    @Deprecated
    public static Map<String, BigInteger> calcCrossTransferTxFee(CrossTransferTxFeeDto dto) {
        return transactionService.calcCrossTransferTxFee(dto);
    }

    @ApiOperation(description = "计算离线创建跨链转账交易所需NULS手续费", order = 351)
    @Parameters({
            @Parameter(parameterName = "CrossTransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = CrossTransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = Map.class))
    public static BigInteger calcCrossTransferNulsTxFee(CrossTransferTxFeeDto dto) {
        return transactionService.calcCrossTransferNulsTxFee(dto);
    }

    @ApiOperation(description = "离线组装多签账户转账交易", order = 352, detailDesc = "根据inputs和outputs离线组装转账交易，用于单个多签账户转账。" +
            "交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和")
    @Parameters({
            @Parameter(parameterName = "transferDto", parameterDes = "转账交易表单", requestType = @TypeDescriptor(value = MultiSignTransferDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignTransferTxOffline(MultiSignTransferDto transferDto) {
        return transactionService.createMultiSignTransferTx(transferDto);
    }

    @ApiOperation(description = "计算离线创建多签账户转账交易所需手续费", order = 353)
    @Parameters({
            @Parameter(parameterName = "MultiSignTransferTxFeeDto", parameterDes = "转账交易手续费", requestType = @TypeDescriptor(value = MultiSignTransferTxFeeDto.class))
    })
    @ResponseData(name = "返回值", description = "手续费金额", responseType = @TypeDescriptor(value = BigInteger.class))
    public static BigInteger calcMultiSignTransferTxFee(MultiSignTransferTxFeeDto dto) {
        return transactionService.calcMultiSignTransferTxFee(dto);
    }

    @ApiOperation(description = "离线创建设置别名交易", order = 354)
    @Parameters({
            @Parameter(parameterName = "AliasDto", parameterDes = "创建别名交易表单", requestType = @TypeDescriptor(value = AliasDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createAliasTxOffline(AliasDto dto) {
        return transactionService.createAliasTx(dto);
    }

    @ApiOperation(description = "离线创建多签账户设置别名交易", order = 355)
    @Parameters({
            @Parameter(parameterName = "MultiSignAliasDto", parameterDes = "多签账户创建别名交易表单", requestType = @TypeDescriptor(value = MultiSignAliasDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignAliasTxOffline(MultiSignAliasDto dto) {
        return transactionService.createMultiSignAliasTx(dto);
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

    @ApiOperation(description = "token转账", order = 404)
    @Parameters({
            @Parameter(parameterName = "token转账", parameterDes = "token转账表单", requestType = @TypeDescriptor(value = ContractTokenTransferForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHash", description = "交易hash")
    }))
    public static Result tokentransfer(ContractTokenTransferForm form) {
        return contractService.tokentransfer(form);
    }

    @ApiOperation(description = "从账户地址向合约地址转账(主链资产)的合约交易", order = 405)
    @Parameters({
            @Parameter(parameterName = "向合约地址转账", parameterDes = "向合约地址转账表单", requestType = @TypeDescriptor(value = ContractTransferForm.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "txHash", description = "交易hash")
    }))
    public static Result transferTocontract(ContractTransferForm form) {
        return contractService.transferTocontract(form);
    }

    @ApiOperation(description = "获取账户地址的指定token余额", order = 406)
    @Parameters({
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "address", parameterDes = "账户地址")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = ContractTokenInfoDto.class))
    public static Result getTokenBalance(String contractAddress, String address) {
        return contractService.getTokenBalance(contractAddress, address);
    }

    @ApiOperation(description = "获取智能合约详细信息", order = 407)
    @Parameters({
            @Parameter(parameterName = "address", parameterDes = "合约地址")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = ContractInfoDto.class))
    public static Result getContractInfo(String contractAddress) {
        return contractService.getContractInfo(contractAddress);
    }

    @ApiOperation(description = "获取智能合约执行结果", order = 408)
    @Parameters({
            @Parameter(parameterName = "hash", parameterDes = "交易hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = ContractResultDto.class))
    public static Result getContractResult(String hash) {
        return contractService.getContractResult(hash);
    }

    @ApiOperation(description = "根据合约代码获取合约构造函数详情", order = 409)
    @Parameters(description = "参数", value = {
            @Parameter(parameterName = "contractCode", parameterDes = "智能合约代码(字节码的Hex编码字符串)")
    })
    @ResponseData(name = "返回值", description = "合约构造函数详情", responseType = @TypeDescriptor(value = ContractConstructorInfoDto.class))
    public static Result<ContractConstructorInfoDto> getConstructor(String contractCode) {
        return contractService.getConstructor(contractCode);
    }

    @ApiOperation(description = "获取已发布合约指定函数的信息", order = 410)
    @Parameters({
            @Parameter(parameterName = "获取已发布合约指定函数的信息", parameterDes = "获取已发布合约指定函数的信息表单", requestType = @TypeDescriptor(value = ContractMethodForm.class))
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个Map对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = ProgramMethod.class))
    public static Result getContractMethod(ContractMethodForm form) {
        return contractService.getContractMethod(form);
    }

    @ApiOperation(description = "获取已发布合约指定函数的参数类型列表", order = 411)
    @Parameters({
            @Parameter(parameterName = "获取已发布合约指定函数的参数类型列表", parameterDes = "获取已发布合约指定函数的参数类型表单", requestType = @TypeDescriptor(value = ContractMethodForm.class))
    })
    @ResponseData(name = "返回值", responseType = @TypeDescriptor(value = List.class, collectionElement = String.class))
    public static Result getContractMethodArgsTypes(ContractMethodForm form) {
        return contractService.getContractMethodArgsTypes(form);
    }

    @ApiOperation(description = "验证发布合约", order = 412)
    @Parameters(value = {
            @Parameter(parameterName = "验证发布合约", parameterDes = "验证发布合约表单", requestType = @TypeDescriptor(value = ContractValidateCreateForm.class))
    })
    @ResponseData(name = "返回值", description = "返回消耗的gas值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "success", valueType = boolean.class, description = "验证成功与否"),
            @Key(name = "code", description = "验证失败的错误码"),
            @Key(name = "msg", description = "验证失败的错误信息")
    }))
    public static Result validateContractCreate(ContractValidateCreateForm form) {
        return contractService.validateContractCreate(form);
    }

    @ApiOperation(description = "验证调用合约", order = 413)
    @Parameters(value = {
            @Parameter(parameterName = "验证调用合约", parameterDes = "验证调用合约表单", requestType = @TypeDescriptor(value = ContractValidateCallForm.class))
    })
    @ResponseData(name = "返回值", description = "返回消耗的gas值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "success", valueType = boolean.class, description = "验证成功与否"),
            @Key(name = "code", description = "验证失败的错误码"),
            @Key(name = "msg", description = "验证失败的错误信息")
    }))
    public static Result validateContractCall(ContractValidateCallForm form) {
        return contractService.validateContractCall(form);
    }

    @ApiOperation(description = "验证删除合约", order = 414)
    @Parameters(value = {
            @Parameter(parameterName = "验证删除合约", parameterDes = "验证删除合约表单", requestType = @TypeDescriptor(value = ContractValidateDeleteForm.class))
    })
    @ResponseData(name = "返回值", description = "返回消耗的gas值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "success", valueType = boolean.class, description = "验证成功与否"),
            @Key(name = "code", description = "验证失败的错误码"),
            @Key(name = "msg", description = "验证失败的错误信息")
    }))
    public static Result validateContractDelete(ContractValidateDeleteForm form) {
        return contractService.validateContractDelete(form);
    }

    @ApiOperation(description = "估算发布合约交易的GAS", order = 415)
    @Parameters(value = {
            @Parameter(parameterName = "估算发布合约交易的GAS", parameterDes = "估算发布合约交易的GAS表单", requestType = @TypeDescriptor(value = ImputedGasContractCreateForm.class))
    })
    @ResponseData(name = "返回值", description = "返回消耗的gas值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "gasLimit", valueType = Long.class, description = "消耗的gas值，执行失败返回数值1")
    }))
    public static Result imputedContractCreateGas(ImputedGasContractCreateForm form) {
        return contractService.imputedContractCreateGas(form);
    }

    @ApiOperation(description = "估算调用合约交易的GAS", order = 416)
    @Parameters(value = {
            @Parameter(parameterName = "估算调用合约交易的GAS", parameterDes = "估算调用合约交易的GAS表单", requestType = @TypeDescriptor(value = ImputedGasContractCallForm.class))
    })
    @ResponseData(name = "返回值", description = "返回消耗的gas值", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "gasLimit", valueType = Long.class, description = "消耗的gas值，执行失败返回数值1")
    }))
    public static Result imputedContractCallGas(ImputedGasContractCallForm form) {
        return contractService.imputedContractCallGas(form);
    }

    @ApiOperation(description = "调用合约不上链方法", order = 417)
    @Parameters(value = {
            @Parameter(parameterName = "调用合约不上链方法", parameterDes = "调用合约不上链方法表单", requestType = @TypeDescriptor(value = ContractViewCallForm.class))
    })
    @ResponseData(name = "返回值", description = "返回Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "result", description = "视图方法的调用结果")
    }))
    public static Result invokeView(ContractViewCallForm form) {
        return contractService.invokeView(form);
    }

    @ApiOperation(description = "离线组装 - 发布合约的交易", order = 451)
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
    public static Result<Map> createContractTxOffline(String sender, BigInteger senderBalance, String nonce, String alias, String contractCode, long gasLimit, Object[] args, String[] argsType, String remark) {
        return contractService.createContractTxOffline(sender, senderBalance, nonce, alias, contractCode, gasLimit, args, argsType, remark);
    }

    @ApiOperation(description = "离线组装 - 调用合约的交易", order = 452)
    @Parameters(value = {
            @Parameter(parameterName = "sender", parameterDes = "交易创建者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "账户nonce值"),
            @Parameter(parameterName = "value", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO"),
            @Parameter(parameterName = "multyAssetValues", requestType = @TypeDescriptor(value = String[][].class), parameterDes = "调用者向合约地址转入的其他资产金额，没有此业务时填空，规则: [[<value>,<assetChainId>,<assetId>]]"),
            @Parameter(parameterName = "contractAddress", parameterDes = "合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "methodName", parameterDes = "合约方法"),
            @Parameter(parameterName = "methodDesc", parameterDes = "合约方法描述，若合约内方法没有重载，则此参数可以为空", canNull = true),
            @Parameter(parameterName = "args", requestType = @TypeDescriptor(value = Object[].class), parameterDes = "参数列表", canNull = true),
            @Parameter(parameterName = "argsType", requestType = @TypeDescriptor(value = String[].class), parameterDes = "参数类型列表", canNull = true),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true),
            @Parameter(parameterName = "multyAssetValues", requestType = @TypeDescriptor(value = String[][].class), parameterDes = "调用者向合约地址转入的其他资产金额，没有此业务时填空，规则: [[<value>,<assetChainId>,<assetId>]]"),
            @Parameter(parameterName = "nulsValueToOthers", requestType = @TypeDescriptor(value = String[][].class), parameterDes = "调用者向其他账户地址转入的NULS资产金额，没有此业务时填空，规则: [[<value>,<address>]]")
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> callContractTxOffline(String sender, BigInteger senderBalance, String nonce, BigInteger value, String contractAddress, long gasLimit,
                                                    String methodName, String methodDesc, Object[] args, String[] argsType, String remark,
                                                    List<ProgramMultyAssetValue> multyAssetValues, List<AccountAmountDto> nulsValueToOthers) {
        return contractService.callContractTxOffline(sender, senderBalance, nonce, value, contractAddress, gasLimit, methodName, methodDesc,
                args, argsType, System.currentTimeMillis() / 1000, remark, multyAssetValues, nulsValueToOthers);
    }
    public static Result<Map> callContractTxOffline(String sender, BigInteger senderBalance, String nonce, BigInteger value, String contractAddress, long gasLimit,
                                                    String methodName, String methodDesc, Object[] args, String[] argsType, String remark,
                                                    List<ProgramMultyAssetValue> multyAssetValues) {
        return contractService.callContractTxOffline(sender, senderBalance, nonce, value, contractAddress, gasLimit, methodName, methodDesc,
                args, argsType, System.currentTimeMillis() / 1000, remark, multyAssetValues, null);
    }

    public static Result<Map> callContractTxOffline(String sender, BigInteger senderBalance, String nonce, BigInteger value, String contractAddress, long gasLimit,
                                                    String methodName, String methodDesc, Object[] args, String[] argsType, String remark) {
        return contractService.callContractTxOffline(sender, senderBalance, nonce, value, contractAddress, gasLimit, methodName, methodDesc,
                args, argsType, System.currentTimeMillis() / 1000, remark, null, null);
    }

    @ApiOperation(description = "离线组装 - 删除合约的交易", order = 453)
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
    public static Result<Map> deleteContractTxOffline(String sender, BigInteger senderBalance, String nonce, String contractAddress, String remark) {
        return contractService.deleteContractTxOffline(sender, senderBalance, nonce, contractAddress, remark);
    }

    @ApiOperation(description = "离线组装 - token转账交易", order = 454)
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
    public static Result<Map> tokenTransferTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, String remark) {
        return contractService.tokenTransferTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, remark);
    }

    @ApiOperation(description = "离线组装 - token转账交易", order = 454)
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入者账户地址"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "交易时间"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> tokenTransferTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, long time, String remark) {
        return contractService.tokenTransferTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, time, remark);
    }

    @ApiOperation(description = "离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易", order = 455)
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入的合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的主链资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> transferToContractTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, long gasLimit, BigInteger amount, String remark) {
        return contractService.transferToContractTxOffline(fromAddress, senderBalance, nonce, toAddress, gasLimit, amount, remark);
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
            @Parameter(parameterName = "委托参与共识", parameterDes = "委托参与共识表单", requestType = @TypeDescriptor(value = DepositForm.class))
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

    @ApiOperation(description = "查询节点的委托共识列表", order = 505)
    @Parameters({
            @Parameter(parameterName = "agentHash", parameterDes = "创建共识节点的交易hash")
    })
    @ResponseData(name = "返回值", description = "注意: 返回值是一个List<Map>集合对象，内部key-value结构是[responseType]描述对象的结构", responseType = @TypeDescriptor(value = List.class, collectionElement = DepositInfoDto.class))
    public static Result getDepositList(String agentHash) {
        return consensusService.getDepositList(agentHash);
    }

    @ApiOperation(description = "离线组装创建共识节点交易", order = 551, detailDesc = "参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)")
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

    @ApiOperation(description = "离线组装委托共识交易", order = 552, detailDesc = "参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)")
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

    @ApiOperation(description = "离线组装退出委托共识交易", order = 553, detailDesc = "接口的input数据，则是委托共识交易的output数据，nonce值可为空")
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

    @ApiOperation(description = "离线组装注销共识节点交易", order = 554, detailDesc = "组装交易的StopDepositDto信息，可通过查询节点的委托共识列表获取，input的nonce值可为空")
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

    @ApiOperation(description = "离线组装多签账户创建共识节点交易", order = 555, detailDesc = "参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)")
    @Parameters({
            @Parameter(parameterName = "consensusDto", parameterDes = "多签账户创建节点交易表单", requestType = @TypeDescriptor(value = MultiSignConsensusDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignConsensusTx(MultiSignConsensusDto consensusDto) {
        return transactionService.createMultiSignConsensusTx(consensusDto);
    }

    @ApiOperation(description = "离线组装多签账户委托共识交易", order = 556, detailDesc = "参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)")
    @Parameters({
            @Parameter(parameterName = "depositDto", parameterDes = "多签账户委托共识交易表单", requestType = @TypeDescriptor(value = MultiSignDepositDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignDepositTxOffline(MultiSignDepositDto depositDto) {
        return transactionService.createMultiSignDepositTx(depositDto);
    }

    @ApiOperation(description = "离线组装多签账户退出委托共识交易", order = 557, detailDesc = "接口的input数据，则是委托共识交易的output数据，nonce值可为空")
    @Parameters({
            @Parameter(parameterName = "withDrawDto", parameterDes = "多签账户退出委托交易表单", requestType = @TypeDescriptor(value = MultiSignWithDrawDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignWithdrawDepositTxOffline(MultiSignWithDrawDto withDrawDto) {
        return transactionService.createMultiSignWithdrawDepositTx(withDrawDto);
    }

    @ApiOperation(description = "离线组装多签账户注销共识节点交易", order = 558, detailDesc = "组装交易的StopDepositDto信息，可通过查询节点的委托共识列表获取，input的nonce值可为空")
    @Parameters({
            @Parameter(parameterName = "stopConsensusDto", parameterDes = "多签账户注销共识节点交易表单", requestType = @TypeDescriptor(value = StopConsensusDto.class))
    })
    @ResponseData(name = "返回值", description = "返回一个Map对象", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化16进制字符串")
    }))
    public static Result createMultiSignStopConsensusTx(MultiSignStopConsensusDto stopConsensusDto) {
        return transactionService.createMultiSignStopConsensusTx(stopConsensusDto);
    }

    /**
     * 根据交易的hex ,反序列化成交易实体类
     *
     * @param txHex
     * @return
     */
    public static Result deserializeTxHex(String txHex) {
        Transaction tx = new Transaction();
        try {
            tx.parse(new NulsByteBuffer(HexUtil.decode(txHex)));
            return Result.getSuccess(tx);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    @ApiOperation(description = "离线组装 - token转账交易", order = 559)
    @Parameters(value = {
            @Parameter(parameterName = "fromAddress", parameterDes = "转出者账户地址"),
            @Parameter(parameterName = "senderBalance", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出者账户余额"),
            @Parameter(parameterName = "nonce", parameterDes = "转出者账户nonce值"),
            @Parameter(parameterName = "toAddress", parameterDes = "转入者账户地址(NERVE)"),
            @Parameter(parameterName = "contractAddress", parameterDes = "token合约地址"),
            @Parameter(parameterName = "gasLimit", requestType = @TypeDescriptor(value = long.class), parameterDes = "设置合约执行消耗的gas上限"),
            @Parameter(parameterName = "amount", requestType = @TypeDescriptor(value = BigInteger.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "time", requestType = @TypeDescriptor(value = long.class), parameterDes = "转出的token资产金额"),
            @Parameter(parameterName = "remark", parameterDes = "交易备注", canNull = true)
    })
    @ResponseData(name = "返回值", description = "返回一个Map", responseType = @TypeDescriptor(value = Map.class, mapKeys = {
            @Key(name = "hash", description = "交易hash"),
            @Key(name = "txHex", description = "交易序列化字符串")
    }))
    public static Result<Map> nrc20CrossChainTxOffline(String fromAddress, BigInteger senderBalance, String nonce, String toAddress, String contractAddress, long gasLimit, BigInteger amount, long time, String remark) {
        return contractService.nrc20CrossChainTxOffline(fromAddress, senderBalance, nonce, toAddress, contractAddress, gasLimit, amount, time, remark);
    }


    public static BigDecimal getBalance(int assetChainId, int assetId)  {
        Result rs = getSymbolInfo(assetChainId, assetId);
        Map map = (Map)rs.getData();
        String usdPrice = map.get("usdPrice").toString();
        return new BigDecimal(usdPrice);
    }
    public static Result getSymbolInfo(int assetChainId, int assetId)  {
        if (assetChainId == 0 || assetId == 0) {
            return Result.getFailed(CommonCodeConstanst.NULL_PARAMETER).setMsg("assetChainId or assetId is empty");
        }
        RpcResult<Map> rpcResult = JsonRpcUtil.request("https://scan.nerve.network/api/","getSymbolInfo", ListUtil.of(assetChainId, assetId));
        RpcResultError rpcResultError = rpcResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        // key : usdPrice
        return getSuccess().setData(rpcResult.getResult());
    }
}
