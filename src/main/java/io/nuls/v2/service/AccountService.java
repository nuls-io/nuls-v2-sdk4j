package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.Transaction;
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
import io.nuls.v2.model.dto.RestFulResult;
import io.nuls.v2.model.dto.SignDto;
import io.nuls.v2.util.AccountTool;
import io.nuls.v2.util.CommonValidator;
import io.nuls.v2.util.RestFulUtil;
import io.nuls.v2.util.ValidateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                result = new Result(true);
                Map<String, Object> dataMap = (Map<String, Object>) restFulResult.getData();

                result.setData((List<String>) dataMap.get("list"));
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
     * @param password The password of the account
     * @return result
     */
    public Result<List<AccountDto>> createOffLineAccount(int count, String password) {
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
                Account account = AccountTool.createAccount(SDKContext.main_chain_id);
                if (StringUtils.isNotBlank(password)) {
                    account.encrypt(password);
                }
                AccountDto accountDto = new AccountDto();
                accountDto.setAddress(account.getAddress().toString());
                accountDto.setPubKey(HexUtil.encode(account.getPubKey()));
                if (account.isEncrypted()) {
                    accountDto.setPriKey("");
                    accountDto.setEncryptedPrivateKey(HexUtil.encode(account.getEncryptedPriKey()));
                } else {
                    accountDto.setPriKey(HexUtil.encode(account.getPriKey()));
                    accountDto.setEncryptedPrivateKey("");
                }
                list.add(accountDto);
            }
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
        return Result.getSuccess(list);
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
            if (StringUtils.isBlank(address) || !AddressTool.validAddress(SDKContext.main_chain_id, address)) {
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
                result = new Result(true);
                result.setData(restFulResult.getData());
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

    /**
     * 获取账户余额
     *
     * @param address 地址
     * @return result
     */
    public Result getAccountBalance(String address) {
        validateChainId();
        try {
            if (!AddressTool.validAddress(SDKContext.main_chain_id, address)) {
                throw new NulsException(AccountErrorCode.ADDRESS_ERROR);
            }
            Result result;
            RestFulResult restFulResult = RestFulUtil.get("api/accountledger/balance/" + address);
            if (restFulResult.isSuccess()) {
                result = new Result(true);
                result.setData(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result importPrikey(String priKey, String password) {
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
            RestFulResult restFulResult = RestFulUtil.post("/account/import/pri", params);
            if (restFulResult.isSuccess()) {
                result = new Result(true);
                result.setData(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.getErrorCode().getMsg());
        }
    }
}
