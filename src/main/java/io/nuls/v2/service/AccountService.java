package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.MultiSigAccount;
import io.nuls.base.data.Transaction;
import io.nuls.base.signture.MultiSignTxSignature;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.base.signture.SignatureUtil;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.crypto.AESEncrypt;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.CryptoException;
import io.nuls.core.exception.NulsException;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.model.FormatValidUtils;
import io.nuls.core.model.StringUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.Account;
import io.nuls.v2.model.dto.AccountDto;
import io.nuls.v2.model.dto.AccountKeyStoreDto;
import io.nuls.v2.model.dto.RestFulResult;
import io.nuls.v2.model.dto.SignDto;
import io.nuls.v2.util.AccountTool;
import io.nuls.v2.util.CommonValidator;
import io.nuls.v2.util.RestFulUtil;

import java.io.IOException;
import java.util.*;

import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class AccountService {

    private AccountService() {

    }

    private static AccountService instance = new AccountService();

    public static AccountService getInstance() {
        return instance;
    }

    /**
     * Create accounts
     * 批量创建账户
     *
     * @param count    The number of accounts you want to create
     * @param password The password of the account
     * @return result
     */
    public Result<List<String>> createAccount(int count, String password) {
        validateChainId();
        try {
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            if (count < 1) {
                count = 1;
            }
            Map<String, Object> params = new HashMap<>();
            params.put("count", count);
            params.put("password", password);
            RestFulResult restFulResult = RestFulUtil.post("api/account", params);
            Result<List<String>> result;
            if (restFulResult.isSuccess()) {
                Map<String, Object> dataMap = (Map<String, Object>) restFulResult.getData();
                result = Result.getSuccess((dataMap.get("list")));
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    /**
     * Create encrypted off-line accounts
     * 批量创建离线账户
     *
     * @param count    The number of accounts you want to create
     * @param prefix   The address prefix
     * @param password The password of the account
     * @return result
     */
    public Result<List<AccountDto>> createOffLineAccount(int count, String prefix, String password) {
        validateChainId();

        List<AccountDto> list = new ArrayList<>();
        try {
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            if (count < 1) {
                count = 1;
            }
            for (int i = 0; i < count; i++) {
                //create account
                Account account;
                if (StringUtils.isBlank(prefix)) {
                    account = AccountTool.createAccount(SDKContext.main_chain_id);
                } else {
                    account = AccountTool.createAccount(SDKContext.main_chain_id, prefix);
                }
                if (StringUtils.isNotBlank(password)) {
                    account.encrypt(password);
                }
                AccountDto accountDto = new AccountDto();
                accountDto.setAddress(account.getAddress().toString());
                accountDto.setPubKey(HexUtil.encode(account.getPubKey()));
                if (account.isEncrypted()) {
                    accountDto.setPrikey("");
                    accountDto.setEncryptedPrivateKey(HexUtil.encode(account.getEncryptedPriKey()));
                } else {
                    accountDto.setPrikey(HexUtil.encode(account.getPriKey()));
                    accountDto.setEncryptedPrivateKey("");
                }
                list.add(accountDto);
            }
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
        return Result.getSuccess(list);
    }


    public Result getPriKey(String address, String password) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("password", password);

            Result result;
            RestFulResult restFulResult = RestFulUtil.post("api/account/prikey/" + address, params);
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

    /**
     * get unencrypted private-key
     * 获取原始私钥
     *
     * @param address         账户地址
     * @param encryptedPriKey 账户加密后的私钥
     * @param password        密码
     * @return Result
     */
    public Result getPriKeyOffline(String address, String encryptedPriKey, String password) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (StringUtils.isBlank(encryptedPriKey)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPriKey is invalid");
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            byte[] priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(encryptedPriKey), password);
            if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG);
            }
            Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
            if (!address.equals(account.getAddress().getBase58())) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("priKey", HexUtil.encode(account.getPriKey()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (CryptoException e) {
            return Result.getFailed(AccountErrorCode.PASSWORD_IS_WRONG).setMsg(AccountErrorCode.PASSWORD_IS_WRONG.getMsg());
        }
    }

    public Result importKeystore(AccountKeyStoreDto keyStore, String password) {
        validateChainId();
        try {
            if (keyStore == null) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "[keyStore] is invalid");
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("keystore", keyStore);
            params.put("password", password);
            params.put("overwrite", true);
            RestFulResult restFulResult = RestFulUtil.post("api/account/import/keystore/json", params);
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


    public Result exportKeyStore(String address, String password, String filePath) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (StringUtils.isBlank(filePath)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "filePath is invalid");
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("password", password);
            params.put("path", filePath);

            RestFulResult restFulResult = RestFulUtil.post("api/account/export/" + address, params);
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

    public Result resetPassword(String address, String oldPassword, String newPassword) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (!FormatValidUtils.validPassword(oldPassword) || !FormatValidUtils.validPassword(newPassword)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("password", oldPassword);
            params.put("newPassword", newPassword);

            Result result;
            RestFulResult restFulResult = RestFulUtil.put("api/account/password/" + address, params);
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

    public Result setAlias(String address, String alias ,String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("password", password);
        params.put("address", address);
        params.put("alias", alias);

        Result result;
        RestFulResult restFulResult = RestFulUtil.post("api/account/alias/", params);
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    /**
     * Change the off-line account password by encryptedPriKey and passowrd
     *
     * @param address         The address of account
     * @param encryptedPriKey The encrypted Private Key
     * @param oldPassword     The password to use when encrypting the private key
     * @param newPassword     The new password
     * @return Result
     */
    public Result resetPasswordOffline(String address, String encryptedPriKey, String oldPassword, String newPassword) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            if (StringUtils.isBlank(encryptedPriKey)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPriKey is invalid");
            }
            if (!FormatValidUtils.validPassword(oldPassword) || !FormatValidUtils.validPassword(newPassword)) {
                throw new NulsException(AccountErrorCode.PASSWORD_FORMAT_WRONG);
            }
            byte[] priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(encryptedPriKey), oldPassword);
            if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                throw new NulsException(AccountErrorCode.PRIVATE_KEY_WRONG);
            }
            Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
            if (!address.equals(account.getAddress().getBase58())) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            account.encrypt(newPassword);
            Map<String, Object> map = new HashMap<>();
            map.put("newEncryptedPriKey", HexUtil.encode(account.getEncryptedPriKey()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (CryptoException e) {
            return Result.getFailed(AccountErrorCode.PASSWORD_IS_WRONG).setMsg(AccountErrorCode.PASSWORD_IS_WRONG.getMsg());
        }
    }

    /**
     * sign the tx's digest
     *
     * @param signDtoList 签名请求参数
     * @return result
     */
    public Result sign(List<SignDto> signDtoList, String txHex) {
        validateChainId();
        try {
            CommonValidator.validateSignDto(signDtoList);
            if (StringUtils.isBlank(txHex)) {
                throw new NulsRuntimeException(AccountErrorCode.PARAMETER_ERROR, "txHex is invalid");
            }

            List<ECKey> signEcKeys = new ArrayList<>();
            for (SignDto signDto : signDtoList) {
                byte[] priKeyBytes;
                if (StringUtils.isNotBlank(signDto.getPriKey())) {
                    if (!ECKey.isValidPrivteHex(signDto.getPriKey())) {
                        throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getPriKey() + " is invalid");
                    }
                    priKeyBytes = HexUtil.decode(signDto.getPriKey());

                } else {
                    try {
                        priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(signDto.getEncryptedPrivateKey()), signDto.getPassword());
                        if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getEncryptedPrivateKey() + " is invalid");
                        }
                    } catch (CryptoException e) {
                        throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPrivateKey[" + signDto.getEncryptedPrivateKey() + "] password error");
                    }
                }
                Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
                if (!signDto.getAddress().equals(account.getAddress().getBase58())) {
                    throw new NulsRuntimeException(AccountErrorCode.ADDRESS_ERROR, account.getAddress() + " and private key do not match");
                }
                ECKey ecKey = account.getEcKey(signDto.getPassword());
                signEcKeys.add(ecKey);
            }
            Transaction tx = new Transaction();
            tx.parse(new NulsByteBuffer(HexUtil.decode(txHex)));
            SignatureUtil.createTransactionSignture(tx, signEcKeys);

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.SERIALIZE_ERROR).setMsg(AccountErrorCode.SERIALIZE_ERROR.getMsg());
        }
    }

    public Result multiSign(SignDto signDto, String txHex) {
        validateChainId();
        try {
            CommonValidator.validateSignDto(signDto);
            if (StringUtils.isBlank(txHex)) {
                throw new NulsRuntimeException(AccountErrorCode.PARAMETER_ERROR, "txHex is invalid");
            }
            Transaction tx = new Transaction();
            tx.parse(new NulsByteBuffer(HexUtil.decode(txHex)));

            if (tx.getTransactionSignature() == null) {
                throw new NulsRuntimeException(AccountErrorCode.PARAMETER_ERROR, "is not multiSign TransferTx");
            }

            byte[] priKeyBytes;
            if (StringUtils.isNotBlank(signDto.getPriKey())) {
                if (!ECKey.isValidPrivteHex(signDto.getPriKey())) {
                    throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getPriKey() + " is invalid");
                }
                priKeyBytes = HexUtil.decode(signDto.getPriKey());
            } else {
                try {
                    priKeyBytes = AESEncrypt.decrypt(HexUtil.decode(signDto.getEncryptedPrivateKey()), signDto.getPassword());
                    if (!ECKey.isValidPrivteHex(HexUtil.encode(priKeyBytes))) {
                        throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG, signDto.getEncryptedPrivateKey() + " is invalid");
                    }
                } catch (CryptoException e) {
                    throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "encryptedPrivateKey[" + signDto.getEncryptedPrivateKey() + "] password error");
                }
            }
            Account account = AccountTool.createAccount(SDKContext.main_chain_id, HexUtil.encode(priKeyBytes));
            if (!signDto.getAddress().equals(account.getAddress().getBase58())) {
                throw new NulsRuntimeException(AccountErrorCode.ADDRESS_ERROR, account.getAddress() + " and private key do not match");
            }

            MultiSignTxSignature transactionSignature = new MultiSignTxSignature();
            transactionSignature.parse(new NulsByteBuffer(tx.getTransactionSignature()));
            boolean hasPubKey = false;
            for (byte[] bytes : transactionSignature.getPubKeyList()) {
                if(Arrays.equals(bytes, account.getPubKey())) {
                    hasPubKey = true;
                }
            }
            if(!hasPubKey) {
                throw new NulsRuntimeException(AccountErrorCode.ADDRESS_ERROR, account.getAddress() + " not one of the multiSign address");
            }
            List<P2PHKSignature> p2PHKSignatures = transactionSignature.getP2PHKSignatures();
            if(p2PHKSignatures == null) {
                p2PHKSignatures = new ArrayList<>();
            }
            for (P2PHKSignature p2PHKSignature : p2PHKSignatures) {
                if (Arrays.equals(p2PHKSignature.getPublicKey(), account.getPubKey())) {
                    //已经签过名了
                    throw new NulsRuntimeException(AccountErrorCode.ADDRESS_ALREADY_SIGNED);
                }
            }
            ECKey ecKey = account.getEcKey(signDto.getPassword());
            P2PHKSignature p2PHKSignature = SignatureUtil.createSignatureByEckey(tx, ecKey);
            p2PHKSignatures.add(p2PHKSignature);
            transactionSignature.setP2PHKSignatures(p2PHKSignatures);
            tx.setTransactionSignature(transactionSignature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.SERIALIZE_ERROR).setMsg(AccountErrorCode.SERIALIZE_ERROR.getMsg());
        }
    }

    /**
     * 获取账户余额
     *
     * @param address 地址
     * @return result
     */
    public Result getAccountBalance(String address, int chainId, int assetsId) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(chainId, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("assetChainId", chainId);
            params.put("assetId", assetsId);

            Result result;
            RestFulResult restFulResult = RestFulUtil.post("api/accountledger/balance/" + address, params);
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

    /**
     * 导入私钥
     *
     * @param priKey   私钥
     * @param password 导入私钥后，给私钥设置的密码
     * @return result
     */
    public Result importPriKey(String priKey, String password) {
        validateChainId();
        try {
            if (StringUtils.isBlank(priKey)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "priKey[" + priKey + "] is invalid");
            }
            if (!FormatValidUtils.validPassword(password)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "password[" + password + "] is invalid");
            }
            Map<String, Object> params = new HashMap<>();
            params.put("priKey", priKey);
            params.put("password", password);
            params.put("overwrite", true);

            Result result;
            RestFulResult restFulResult = RestFulUtil.post("api/account/import/pri", params);
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

    public Result createMultiSignAccount(List<String> pubKeys, int minSigns) {
        validateChainId();
        try {
            if (pubKeys == null || pubKeys.isEmpty()) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "pubKeys is invalid");
            }
            if (minSigns < 1 || minSigns > pubKeys.size()) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "minSigns is invalid");
            }
            MultiSigAccount multiSigAccount = AccountTool.createMultiSigAccount(SDKContext.main_chain_id, pubKeys, minSigns);
            Map<String, Object> map = new HashMap<>();
            map.put("value", multiSigAccount.getAddress().getBase58());

            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result getAddressByPriKey(String priKey) {
        validateChainId();
        if (!ECKey.isValidPrivteHex(priKey)) {
            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG);
        }
        Account account;
        try {
            account = AccountTool.createAccount(SDKContext.main_chain_id, priKey);
        } catch (NulsException e) {
            throw new NulsRuntimeException(AccountErrorCode.PRIVATE_KEY_WRONG);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("value", account.getAddress().getBase58());

        return Result.getSuccess(map);
    }

}
