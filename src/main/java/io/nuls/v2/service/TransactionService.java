package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.*;
import io.nuls.base.signture.MultiSignTxSignature;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.TxType;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.StringUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.constant.AccountConstant;
import io.nuls.v2.enums.ChainFeeSettingType;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.ChainFeeSetting;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.txdata.*;
import io.nuls.v2.util.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static io.nuls.v2.SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;
import static io.nuls.v2.constant.AccountConstant.ALIAS_FEE;
import static io.nuls.v2.constant.Constant.NULS_CHAIN_ID;
import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class TransactionService {

    private TransactionService() {

    }

    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
    }

    public Result getTx(String txHash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/tx/" + txHash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            TransactionDto tx = TransactionDto.mapToPojo(map);
            result.setData(tx);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    public Result getTransaction(String txHash) {
        validateChainId();
        RestFulResult restFulResult = RestFulUtil.get("api/tx/" + txHash);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(null);
            Map<String, Object> map = (Map<String, Object>) restFulResult.getData();
            TransactionDto tx = TransactionDto.mapToPojo(map);
            restFulResult = RestFulUtil.get("api/block/header/height/" + tx.getBlockHeight());
            if (restFulResult.isSuccess()) {
                map = (Map<String, Object>) restFulResult.getData();
                tx.setBlockHash((String) map.get("hash"));
            }
            result.setData(tx);
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
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

    public Result crossTransfer(CrossTransferForm form) {
        validateChainId();
        Map<String, Object> params = new HashMap<>();
        params.put("address", form.getAddress());
        params.put("toAddress", form.getToAddress());
        params.put("password", form.getPassword());
        params.put("assetChainId", form.getAssetChainId());
        params.put("assetId", form.getAssetId());
        params.put("amount", form.getAmount());
        params.put("remark", form.getRemark());

        RestFulResult restFulResult = RestFulUtil.post("api/accountledger/crossTransfer", params);
        Result result;
        if (restFulResult.isSuccess()) {
            result = Result.getSuccess(restFulResult.getData());
        } else {
            ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
            result = Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
        }
        return result;
    }

    /**
     * 计算转账交易手续费
     *
     * @param dto 请求参数
     * @return result
     */
    public BigInteger calcTransferTxFee(TransferTxFeeDto dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(SDKContext.NULS_DEFAULT_NORMAL_TX_FEE_PRICE);
        }
        return TxUtils.calcTransferTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), dto.getPrice());
    }


    @Deprecated
    public Map<String, BigInteger> calcCrossTransferTxFee(CrossTransferTxFeeDto dto) {
        boolean isMainNet = false;
        if (SDKContext.main_chain_id == NULS_CHAIN_ID || SDKContext.main_chain_id == 2) {
            isMainNet = true;
        }
        return TxUtils.calcCrossTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), isMainNet);
    }

    /**
     * 计算NULS跨链交易，所需收取的NULS手续费
     *
     * @param dto
     * @return
     */
    public BigInteger calcCrossTransferNulsTxFee(CrossTransferTxFeeDto dto) {
        return TxUtils.calcCrossTxFee(dto.getAddressCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark());
    }

    /**
     * 便捷版 组装在NULS链内，转账非NULS资产的单账户对单账户普通转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费，
     * 如果from地址中没有足够的手续费，该交易不会成功。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress 转出地址（NULS地址）
     * @param toAddress   转入地址（NULS地址）
     * @param assetId
     * @param amount
     * @return
     */
    public Result createTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, 0, null);
    }

    /**
     * 便捷版 组装在NULS链内，转账非NULS资产的单账户对单账户普通转账(不能用于转NULS)。
     * 该方法会主动用fromAddress组装（NULS资产）打包手续费，
     * 如果from地址中没有足够的手续费，该交易不会成功。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress  转出地址（NULS地址）
     * @param toAddress    转入地址（NULS地址）
     * @param assetChainId
     * @param assetId
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        return this.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark, SDKContext.main_chain_id, SDKContext.main_asset_id, null);
    }

    public Result createTxSimpleTransferOfNonNulsByFeeType(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark, ChainFeeSettingType feeType) {
        if (feeType == null || feeType == ChainFeeSettingType.NULS) {
            return this.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark);
        }
        ChainFeeSetting feeSetting = SDKContext.CHAIN_FEE_SETTING_MAP.get(feeType.name());
        Asset feeAsset = feeSetting.getAsset();
        return this.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, time, remark, feeAsset.getAssetChainId(), feeAsset.getAssetId(), new BigInteger(feeSetting.getFeePerKB()));
    }

    public Result createTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark, int feeChainId, int feeAssetId, BigInteger sizePrice) {
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, assetChainId, assetId);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amount) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(assetChainId);
        from.setAssetId(assetId);
        from.setNonce(nonce);
        inputs.add(from);

        Result accountBalanceFeeR = NulsSDKTool.getAccountBalance(fromAddress, feeChainId, feeAssetId);
        if (!accountBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountBalanceFeeR.getErrorCode()).setMsg(accountBalanceFeeR.getMsg());
        }
        Map balanceFee = (Map) accountBalanceFeeR.getData();
        BigInteger senderBalanceFee = new BigInteger(balanceFee.get("available").toString());

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(2);
        feeDto.setToLength(1);
        feeDto.setRemark(remark);
        feeDto.setPrice(sizePrice);
        BigInteger feeNeed = NulsSDKTool.calcTransferTxFee(feeDto);
        if (senderBalanceFee.compareTo(feeNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }
        String nonceFee = balanceFee.get("nonce").toString();
        //手续费资产
        CoinFromDto fromFee = new CoinFromDto();
        fromFee.setAddress(fromAddress);
        fromFee.setAmount(feeNeed);
        fromFee.setAssetChainId(feeChainId);
        fromFee.setAssetId(feeAssetId);
        fromFee.setNonce(nonceFee);
        inputs.add(fromFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(assetChainId);
        to.setAssetId(assetId);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createTransferTx(transferDto, false);
    }


    /**
     * 便捷版 组装在NULS链内，转账NULS资产的单账户对单账户普通转账。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @return
     */
    public Result createTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return createTxSimpleTransferOfNuls(fromAddress, toAddress, amount, 0, null);
    }

    /**
     * 便捷版 组装在NULS链内，转账NULS资产的单账户对单账户普通转账。
     * !! 打包手续费不包含在amount中， 本函数将从fromAddress中额外获取手续费追加到coinfrom中，
     * 请不要将手续费事先加入到amount参数中， amount参数作为实际到账的数量。
     * <p>
     * 如果需要完整信息或结构更复杂的转账（比如多账户），请使用完全版的离线交易组装
     *
     * @param fromAddress
     * @param toAddress
     * @param amount
     * @param time
     * @param remark
     * @return
     */
    public Result createTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(2);
        feeDto.setToLength(1);
        feeDto.setRemark(remark);
        BigInteger feeNeed = NulsSDKTool.calcTransferTxFee(feeDto);
        BigInteger amountTotal = amount.add(feeNeed);
        if (senderBalance.compareTo(amountTotal) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amountTotal);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonce);
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createTransferTx(transferDto);
    }

    public Result createTxSimpleTransferOfNulsByFeeType(String fromAddress, String toAddress, BigInteger amount, long time, String remark, ChainFeeSettingType type) {
        if (type == null || type == ChainFeeSettingType.NULS) {
            return createTxSimpleTransferOfNuls(fromAddress, toAddress, amount, time, remark);
        }
        ChainFeeSetting feeSetting = SDKContext.CHAIN_FEE_SETTING_MAP.get(type.name());
        Asset feeAsset = feeSetting.getAsset();
        Result accountBalanceNuls = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceNuls.isSuccess()) {
            return Result.getFailed(accountBalanceNuls.getErrorCode()).setMsg(accountBalanceNuls.getMsg());
        }
        Result accountBalanceFee = NulsSDKTool.getAccountBalance(fromAddress, feeAsset.getAssetChainId(), feeAsset.getAssetId());
        if (!accountBalanceFee.isSuccess()) {
            return Result.getFailed(accountBalanceFee.getErrorCode()).setMsg(accountBalanceFee.getMsg());
        }
        Map balanceNuls = (Map) accountBalanceNuls.getData();
        BigInteger senderBalanceNuls = new BigInteger(balanceNuls.get("available").toString());
        Map balanceFee = (Map) accountBalanceFee.getData();
        BigInteger senderBalanceFee = new BigInteger(balanceFee.get("available").toString());

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(2);
        feeDto.setToLength(1);
        feeDto.setRemark(remark);
        feeDto.setPrice(new BigInteger(feeSetting.getFeePerKB()));
        BigInteger feeNeed = NulsSDKTool.calcTransferTxFee(feeDto);

        if (senderBalanceNuls.compareTo(amount) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        if (senderBalanceFee.compareTo(feeNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        String nonce = balanceNuls.get("nonce").toString();
        String nonceFee = balanceFee.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();
        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonce);
        inputs.add(from);
        CoinFromDto fromFee = new CoinFromDto();
        fromFee.setAddress(fromAddress);
        fromFee.setAmount(feeNeed);
        fromFee.setAssetChainId(feeAsset.getAssetChainId());
        fromFee.setAssetId(feeAsset.getAssetId());
        fromFee.setNonce(nonceFee);
        inputs.add(fromFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createTransferTx(transferDto, false);

    }


    /**
     * 创建转账交易(离线)
     * create transfer transaction(off-line)
     *
     * @param transferDto 转账请求参数
     * @return
     */
    public Result createTransferTx(TransferDto transferDto) {
        return createTransferTx(transferDto, true);
    }

    public Result createTransferTx(TransferDto transferDto, boolean checkTxSize) {
        validateChainId();
        try {
            CommonValidator.checkTransferDto(transferDto);

            for (CoinFromDto fromDto : transferDto.getInputs()) {
                if (fromDto.getAssetChainId() == 0) {
                    fromDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (fromDto.getAssetId() == 0) {
                    fromDto.setAssetId(SDKContext.main_asset_id);
                }
            }
            for (CoinToDto toDto : transferDto.getOutputs()) {
                if (toDto.getAssetChainId() == 0) {
                    toDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (toDto.getAssetId() == 0) {
                    toDto.setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.TRANSFER);
            if (transferDto.getTime() != 0) {
                tx.setTime(transferDto.getTime());
            } else {
                tx.setTime(getCurrentTimeSeconds());
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto.getInputs(), transferDto.getOutputs(), tx.getSize(), checkTxSize);
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装转账交易的coinData数据
     * Assemble the coinData for the transfer transaction
     *
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs, int txSize, boolean checkTxSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : inputs) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : outputs) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        txSize = txSize + getSignatureSize(coinFroms);
        if (checkTxSize) {
            TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        }
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
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
    public Result createCrossTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        return createCrossTxSimpleTransferOfNonNuls(fromAddress, toAddress, assetChainId, assetId, amount, 0, null);
    }

    /**
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
    public Result createCrossTxSimpleTransferOfNonNuls(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, assetChainId, assetId);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        if (senderBalance.compareTo(amount) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amount);
        from.setAssetChainId(assetChainId);
        from.setAssetId(assetId);
        from.setNonce(nonce);
        inputs.add(from);

        Result accountBalanceFeeR = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceFeeR.isSuccess()) {
            return Result.getFailed(accountBalanceFeeR.getErrorCode()).setMsg(accountBalanceFeeR.getMsg());
        }
        Map balanceFee = (Map) accountBalanceFeeR.getData();
        BigInteger senderBalanceFee = new BigInteger(balanceFee.get("available").toString());

        CrossTransferTxFeeDto crossFeeDto = new CrossTransferTxFeeDto();
        crossFeeDto.setAddressCount(1);
        crossFeeDto.setFromLength(2);
        crossFeeDto.setToLength(1);
        crossFeeDto.setRemark(remark);
        BigInteger feeNeed = calcCrossTransferNulsTxFee(crossFeeDto);
        if (senderBalanceFee.compareTo(feeNeed) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_FEE);
        }
        String nonceFee = balanceFee.get("nonce").toString();
        //手续费资产
        CoinFromDto fromFee = new CoinFromDto();
        fromFee.setAddress(fromAddress);
        fromFee.setAmount(feeNeed);
        fromFee.setAssetChainId(SDKContext.main_chain_id);
        fromFee.setAssetId(SDKContext.main_asset_id);
        fromFee.setNonce(nonceFee);
        inputs.add(fromFee);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(assetChainId);
        to.setAssetId(assetId);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createCrossTransferTx(transferDto);
    }


    /**
     * 便捷版 组装跨链转账NULS资产的单账户对单账户普通跨链转账。
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
    public Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount) {
        return createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount, 0, null);
    }

    /**
     * 便捷版 组装跨链转账NULS资产的单账户对单账户普通跨链转账。
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
    public Result createCrossTxSimpleTransferOfNuls(String fromAddress, String toAddress, BigInteger amount, long time, String remark) {
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        if (!accountBalanceR.isSuccess()) {
            return Result.getFailed(accountBalanceR.getErrorCode()).setMsg(accountBalanceR.getMsg());
        }
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());

        CrossTransferTxFeeDto crossFeeDto = new CrossTransferTxFeeDto();
        crossFeeDto.setAddressCount(1);
        crossFeeDto.setFromLength(2);
        crossFeeDto.setToLength(1);
        crossFeeDto.setRemark(remark);
        BigInteger feeNeed = NulsSDKTool.calcCrossTransferNulsTxFee(crossFeeDto);
        BigInteger amountTotal = amount.add(feeNeed);
        if (senderBalance.compareTo(amountTotal) < 0) {
            return Result.getFailed(AccountErrorCode.INSUFFICIENT_BALANCE);
        }
        String nonce = balance.get("nonce").toString();

        TransferDto transferDto = new TransferDto();
        List<CoinFromDto> inputs = new ArrayList<>();

        //转账资产
        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(amountTotal);
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce(nonce);
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(amount);
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);
        transferDto.setTime(time);
        transferDto.setRemark(remark);
        return createCrossTransferTx(transferDto);
    }


    /**
     * 创建跨链转账交易
     *
     * @param transferDto
     * @return
     */
    public Result createCrossTransferTx(TransferDto transferDto) {
        validateChainId();
        try {
            CommonValidator.checkCrossTransferDto(transferDto);
            Transaction tx = new Transaction(TxType.CROSS_CHAIN);
            if (transferDto.getTime() != 0) {
                tx.setTime(transferDto.getTime());
            } else {
                tx.setTime(getCurrentTimeSeconds());
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = createCrossTxCoinData(transferDto.getInputs(), transferDto.getOutputs());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));

            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }

    }

    public CoinData createCrossTxCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs) {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : inputs) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : outputs) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);

        return coinData;
    }


    public Result createMultiSignTransferTx(MultiSignTransferDto transferDto) {
        validateChainId();
        try {
            CommonValidator.checkMultiSignTransferDto(transferDto);
            for (CoinFromDto fromDto : transferDto.getInputs()) {
                if (fromDto.getAssetChainId() == 0) {
                    fromDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (fromDto.getAssetId() == 0) {
                    fromDto.setAssetId(SDKContext.main_asset_id);
                }
            }
            for (CoinToDto toDto : transferDto.getOutputs()) {
                if (toDto.getAssetChainId() == 0) {
                    toDto.setAssetChainId(SDKContext.main_chain_id);
                }
                if (toDto.getAssetId() == 0) {
                    toDto.setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.TRANSFER);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto, tx.getSize());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) transferDto.getMinSigns());

            List<byte[]> list = new ArrayList<>();
            for (String pubKey : transferDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(MultiSignTransferDto transferDto, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : transferDto.getInputs()) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : transferDto.getOutputs()) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getAssetChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        txSize = txSize + getMultiSignSignatureSize(transferDto.getPubKeys().size());
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 计算转账交易手续费
     *
     * @param dto 请求参数
     * @return result
     */
    public BigInteger calcMultiSignTransferTxFee(MultiSignTransferTxFeeDto dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(SDKContext.NULS_DEFAULT_NORMAL_TX_FEE_PRICE);
        }
        return TxUtils.calcTransferTxFee(dto.getPubKeyCount(), dto.getFromLength(), dto.getToLength(), dto.getRemark(), dto.getPrice());
    }

    /**
     * 通过coinFroms计算签名数据的size
     * 如果coinFroms有重复地址则只计算一次
     * Calculate the size of the signature data by coinFroms
     * if coinFroms has duplicate addresses, it will only be evaluated once
     *
     * @param coinFroms 交易输入
     * @return int size
     */
    private int getSignatureSize(List<CoinFrom> coinFroms) {
        int size = 0;
        Set<String> commonAddress = new HashSet<>();
        for (CoinFrom coinFrom : coinFroms) {
            String address = AddressTool.getStringAddressByBytes(coinFrom.getAddress());
            commonAddress.add(address);
        }
        size += commonAddress.size() * P2PHKSignature.SERIALIZE_LENGTH;
        return size;
    }

    private int getMultiSignSignatureSize(int signNumber) {
        int size = signNumber * P2PHKSignature.SERIALIZE_LENGTH;
        return size;
    }

    public Result createAliasTx(AliasDto aliasDto) {
        validateChainId();
        try {
            CommonValidator.checkAliasDto(aliasDto);

            Transaction tx = new Transaction(TxType.ACCOUNT_ALIAS);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(aliasDto.getRemark()));

            Alias alias = new Alias(AddressTool.getAddress(aliasDto.getAddress()), aliasDto.getAlias());
            tx.setTxData(alias.serialize());

            CoinData coinData = assemblyCoinData(aliasDto);
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(AliasDto dto) {
        byte[] address = AddressTool.getAddress(dto.getAddress());
        byte[] nonce = HexUtil.decode(dto.getNonce());

        List<CoinFrom> coinFroms = new ArrayList<>();
        CoinFrom coinFrom = new CoinFrom();
        coinFrom.setAddress(address);
        coinFrom.setNonce(nonce);
        coinFrom.setAmount(ALIAS_FEE.add(NULS_DEFAULT_OTHER_TX_FEE_PRICE));
        coinFrom.setAssetsChainId(SDKContext.main_chain_id);
        coinFrom.setAssetsId(SDKContext.main_asset_id);
        coinFroms.add(coinFrom);

        String prefix = AccountTool.getPrefix(dto.getAddress());
        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo();
        coinTo.setAddress(AddressTool.getAddress(AccountConstant.DESTORY_PUBKEY, SDKContext.main_chain_id, prefix));
        coinTo.setAmount(ALIAS_FEE);
        coinTo.setAssetsChainId(SDKContext.main_chain_id);
        coinTo.setAssetsId(SDKContext.main_asset_id);
        coinTos.add(coinTo);

        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 组装创建共识节点交易
     * Assemble to create consensus node transactions
     *
     * @param consensusDto 创建共识节点请求参数
     * @return result
     */
    public Result createConsensusTx(ConsensusDto consensusDto) {
        validateChainId();
        try {
            if (StringUtils.isBlank(consensusDto.getRewardAddress())) {
                consensusDto.setRewardAddress(consensusDto.getAgentAddress());
            }
            CommonValidator.validateConsensusDto(consensusDto);

            if (consensusDto.getInput().getAssetChainId() == 0) {
                consensusDto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (consensusDto.getInput().getAssetId() == 0) {
                consensusDto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.REGISTER_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            Agent agent = new Agent();
            agent.setAgentAddress(AddressTool.getAddress(consensusDto.getAgentAddress()));
            agent.setPackingAddress(AddressTool.getAddress(consensusDto.getPackingAddress()));
            agent.setRewardAddress(AddressTool.getAddress(consensusDto.getRewardAddress()));
            agent.setDeposit((consensusDto.getDeposit()));
            agent.setCommissionRate((byte) consensusDto.getCommissionRate());
            tx.setTxData(agent.serialize());

            CoinData coinData = assemblyCoinData(consensusDto.getInput(), agent.getDeposit(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * Create a proxy consensus transaction
     * 创建委托共识交易
     *
     * @param dto 委托共识请求参数
     * @return result
     */
    public Result createDepositTx(DepositDto dto) {
        validateChainId();
        try {
            CommonValidator.validateDepositDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());
            Deposit deposit = new Deposit();
            deposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            deposit.setAgentHash(NulsHash.fromHex(dto.getAgentHash()));
            deposit.setDeposit(dto.getDeposit());
            tx.setTxData(deposit.serialize());

            CoinData coinData = assemblyCoinData(dto.getInput(), dto.getDeposit(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(CoinFromDto from, BigInteger amount, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        byte[] nonce = HexUtil.decode(from.getNonce());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), amount, -1);
        coinTos.add(coinTo);

        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 创建取消委托交易
     *
     * @param dto 取消委托交易参数
     * @return result
     */
    public Result createWithdrawDepositTx(WithDrawDto dto) {
        validateChainId();

        try {
            if (dto.getPrice() == null) {
                dto.setPrice(NULS_DEFAULT_OTHER_TX_FEE_PRICE);
            }
            CommonValidator.validateWithDrawDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.CANCEL_DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());

            CancelDeposit cancelDeposit = new CancelDeposit();
            cancelDeposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            cancelDeposit.setJoinTxHash(NulsHash.fromHex(dto.getDepositHash()));
            tx.setTxData(cancelDeposit.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);

        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装退出共识交易coinData
     *
     * @param dto    请求参数
     * @param txSize 交易大小
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(WithDrawDto dto, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        CoinFromDto from = dto.getInput();
        byte[] address = AddressTool.getAddress(from.getAddress());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(dto.getDepositHash());
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), from.getAmount().subtract(dto.getPrice()), 0);
        coinTos.add(coinTo);

        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    /**
     * 创建注销共识节点交易
     *
     * @param dto 注销节点参数请求
     * @return result
     */
    public Result createStopConsensusTx(StopConsensusDto dto) {
        validateChainId();

        try {
            if (dto.getPrice() == null) {
                dto.setPrice(NULS_DEFAULT_OTHER_TX_FEE_PRICE);
            }
            CommonValidator.validateStopConsensusDto(dto);
            for (StopDepositDto depositDto : dto.getDepositList()) {
                if (depositDto.getInput().getAssetChainId() == 0) {
                    depositDto.getInput().setAssetChainId(SDKContext.main_chain_id);
                }
                if (depositDto.getInput().getAssetId() == 0) {
                    depositDto.getInput().setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.STOP_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            StopAgent stopAgent = new StopAgent();
            NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
            stopAgent.setCreateTxHash(nulsHash);
            tx.setTxData(stopAgent.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 组装注销节点交易coinData
     *
     * @param dto    参数
     * @param txSize 交易大小
     * @return
     * @throws NulsException
     */
    private CoinData assemblyCoinData(StopConsensusDto dto, int txSize) throws NulsException {
        //获取当前链注册共识资产的chainId和assetId
        int chainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;

        List<CoinFrom> coinFromList = new ArrayList<>();
        //组装创建节点交易的coinFrom
        byte[] addressBytes = AddressTool.getAddress(dto.getAgentAddress());
        CoinFrom coinFrom = new CoinFrom(addressBytes, chainId, assetId, dto.getDeposit(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFromList.add(coinFrom);

        Map<String, CoinFromDto> dtoMap = new HashMap<>();
        CoinFromDto fromDto;
        //组装所有委托的coinFrom
        for (StopDepositDto depositDto : dto.getDepositList()) {
            CoinFromDto input = depositDto.getInput();
            byte[] address = AddressTool.getAddress(input.getAddress());
            CoinFrom coinFrom1 = new CoinFrom(address, input.getAssetChainId(), input.getAssetId(), input.getAmount(), (byte) -1);
            NulsHash nulsHash1 = NulsHash.fromHex(depositDto.getDepositHash());
            coinFrom1.setNonce(TxUtils.getNonce(nulsHash1.getBytes()));
            coinFromList.add(coinFrom1);
            //将相同账户的多次委托的金额存放在一起
            String key = input.getAddress() + input.getAssetChainId() + input.getAssetId();
            fromDto = dtoMap.get(key);
            if (fromDto == null) {
                dtoMap.put(key, input);
            } else {
                fromDto.setAmount(fromDto.getAmount().add(input.getAmount()));
            }
        }
        //通过dtoMap组装交易输出
        List<CoinTo> coinToList = new ArrayList<>();
        for (CoinFromDto input : dtoMap.values()) {
            byte[] address = AddressTool.getAddress(input.getAddress());
            CoinTo coinTo = new CoinTo(address, input.getAssetChainId(), input.getAssetId(), input.getAmount(), 0L);
            coinToList.add(coinTo);
        }
        //计算手续费
        BigInteger fee = TxUtils.calcStopConsensusTxFee(coinFromList.size(), coinToList.size() + 1, dto.getPrice());
        //组装退回保证金的coinTo
        CoinTo coinTo = new CoinTo(addressBytes, coinFrom.getAssetsChainId(), coinFrom.getAssetsId(), coinFrom.getAmount().subtract(fee), getCurrentTimeSeconds() + SDKContext.STOP_AGENT_LOCK_TIME);
        coinToList.add(0, coinTo);

        txSize = txSize + P2PHKSignature.SERIALIZE_LENGTH;
        TxUtils.calcTxFee(coinFromList, coinToList, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFromList);
        coinData.setTo(coinToList);
        return coinData;
    }

    /**
     * 密文私钥签名交易(单签)
     *
     * @param address
     * @param txHex
     * @return
     */
    public Result signTx(String txHex, String address, String encryptedPrivateKey, String password) {
        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
        signDto.setEncryptedPrivateKey(encryptedPrivateKey);
        signDto.setPassword(password);
        signDtoList.add(signDto);
        return NulsSDKTool.sign(signDtoList, txHex);
    }

    /**
     * 明文私钥签名交易(单签)
     *
     * @param address
     * @param txHex
     * @return
     */
    public Result signTx(String txHex, String address, String privateKey) {
        List<SignDto> signDtoList = new ArrayList<>();
        SignDto signDto = new SignDto();
        signDto.setAddress(address);
        signDto.setPriKey(privateKey);
        signDtoList.add(signDto);
        return NulsSDKTool.sign(signDtoList, txHex);
    }

    /**
     * 广播交易
     *
     * @param txHex
     * @return
     */
    public Result broadcastTx(String txHex) {
        RpcResult<Map> balanceResult = JsonRpcUtil.request("broadcastTx", ListUtil.of(SDKContext.main_chain_id, txHex));
        RpcResultError rpcResultError = balanceResult.getError();
        if (rpcResultError != null) {
            return Result.getFailed(ErrorCode.init(rpcResultError.getCode())).setMsg(rpcResultError.getMessage());
        }
        Map result = balanceResult.getResult();
        return Result.getSuccess(result);
    }

    /**
     * 验证交易
     *
     * @param txHex
     * @return
     */
    public Result validateTx(String txHex) {
        validateChainId();
        try {
            if (StringUtils.isBlank(txHex)) {
                throw new NulsException(AccountErrorCode.PARAMETER_ERROR, "form is empty");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("txHex", txHex);

            RestFulResult restFulResult = RestFulUtil.post("api/accountledger/transaction/validate", map);
            Result result;
            if (restFulResult.isSuccess()) {
                result = io.nuls.core.basic.Result.getSuccess(restFulResult.getData());
            } else {
                ErrorCode errorCode = ErrorCode.init(restFulResult.getError().getCode());
                result = io.nuls.core.basic.Result.getFailed(errorCode).setMsg(restFulResult.getError().getMessage());
            }
            return result;
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        }
    }

    public Result createMultiSignConsensusTx(MultiSignConsensusDto consensusDto) {
        validateChainId();
        try {
            if (StringUtils.isBlank(consensusDto.getRewardAddress())) {
                consensusDto.setRewardAddress(consensusDto.getAgentAddress());
            }
            CommonValidator.validateMultiSignConsensusDto(consensusDto);
            if (consensusDto.getInput().getAssetChainId() == 0) {
                consensusDto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (consensusDto.getInput().getAssetId() == 0) {
                consensusDto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.REGISTER_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            Agent agent = new Agent();
            agent.setAgentAddress(AddressTool.getAddress(consensusDto.getAgentAddress()));
            agent.setPackingAddress(AddressTool.getAddress(consensusDto.getPackingAddress()));
            agent.setRewardAddress(AddressTool.getAddress(consensusDto.getRewardAddress()));
            agent.setDeposit((consensusDto.getDeposit()));
            agent.setCommissionRate((byte) consensusDto.getCommissionRate());
            tx.setTxData(agent.serialize());

            CoinData coinData = assemblyCoinData(consensusDto.getInput(), agent.getDeposit(), consensusDto.getPubKeys().size(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) consensusDto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : consensusDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    private CoinData assemblyCoinData(CoinFromDto from, BigInteger amount, int pubKeyCount, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        byte[] nonce = HexUtil.decode(from.getNonce());
        CoinFrom coinFrom = new CoinFrom(address, from.getAssetChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getAssetChainId(), from.getAssetId(), amount, -1);
        coinTos.add(coinTo);

        txSize = txSize + getMultiSignSignatureSize(pubKeyCount);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
    }

    public Result createMultiSignDepositTx(MultiSignDepositDto dto) {
        validateChainId();
        try {
            CommonValidator.validateMultiSignDepositDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());
            Deposit deposit = new Deposit();
            deposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            deposit.setAgentHash(NulsHash.fromHex(dto.getAgentHash()));
            deposit.setDeposit(dto.getDeposit());
            tx.setTxData(deposit.serialize());

            CoinData coinData = assemblyCoinData(dto.getInput(), dto.getDeposit(), dto.getPubKeys().size(), tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    /**
     * 创建取消委托交易
     *
     * @param dto 取消委托交易参数
     * @return result
     */
    public Result createMultiSignWithdrawDepositTx(MultiSignWithDrawDto dto) {
        validateChainId();
        try {
            if (dto.getPrice() == null) {
                dto.setPrice(NULS_DEFAULT_OTHER_TX_FEE_PRICE);
            }
            CommonValidator.validateMultiSignWithDrawDto(dto);
            if (dto.getInput().getAssetChainId() == 0) {
                dto.getInput().setAssetChainId(SDKContext.main_chain_id);
            }
            if (dto.getInput().getAssetId() == 0) {
                dto.getInput().setAssetId(SDKContext.main_asset_id);
            }

            Transaction tx = new Transaction(TxType.CANCEL_DEPOSIT);
            tx.setTime(getCurrentTimeSeconds());

            CancelDeposit cancelDeposit = new CancelDeposit();
            cancelDeposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            cancelDeposit.setJoinTxHash(NulsHash.fromHex(dto.getDepositHash()));
            tx.setTxData(cancelDeposit.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);

        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public Result createMultiSignStopConsensusTx(MultiSignStopConsensusDto dto) {
        validateChainId();
        try {
            if (dto.getPrice() == null) {
                dto.setPrice(NULS_DEFAULT_OTHER_TX_FEE_PRICE);
            }
            CommonValidator.validateMultiSignStopConsensusDto(dto);
            for (StopDepositDto depositDto : dto.getDepositList()) {
                if (depositDto.getInput().getAssetChainId() == 0) {
                    depositDto.getInput().setAssetChainId(SDKContext.main_chain_id);
                }
                if (depositDto.getInput().getAssetId() == 0) {
                    depositDto.getInput().setAssetId(SDKContext.main_asset_id);
                }
            }

            Transaction tx = new Transaction(TxType.STOP_AGENT);
            tx.setTime(getCurrentTimeSeconds());

            StopAgent stopAgent = new StopAgent();
            NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
            stopAgent.setCreateTxHash(nulsHash);
            tx.setTxData(stopAgent.serialize());

            CoinData coinData = assemblyCoinData(dto, tx.size());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) dto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : dto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public Result createMultiSignAliasTx(MultiSignAliasDto aliasDto) {
        validateChainId();
        try {
            CommonValidator.validateMultiSignAliasDto(aliasDto);

            Transaction tx = new Transaction(TxType.ACCOUNT_ALIAS);
            tx.setTime(getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(aliasDto.getRemark()));

            Alias alias = new Alias(AddressTool.getAddress(aliasDto.getAddress()), aliasDto.getAlias());
            tx.setTxData(alias.serialize());

            CoinData coinData = assemblyCoinData(aliasDto);
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));
            MultiSignTxSignature signature = new MultiSignTxSignature();
            signature.setM((byte) aliasDto.getMinSigns());
            List<byte[]> list = new ArrayList<>();
            for (String pubKey : aliasDto.getPubKeys()) {
                list.add(HexUtil.decode(pubKey));
            }
            signature.setPubKeyList(list);
            tx.setTransactionSignature(signature.serialize());

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return Result.getSuccess(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode()).setMsg(e.format());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR).setMsg(AccountErrorCode.DATA_PARSE_ERROR.getMsg());
        }
    }

    public long getCurrentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}

