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
import io.nuls.core.rpc.util.NulsDateUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.constant.AccountConstant;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.txdata.*;
import io.nuls.v2.util.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static io.nuls.v2.SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;
import static io.nuls.v2.constant.AccountConstant.ALIAS_FEE;
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
            result = Result.getSuccess(restFulResult.getData());
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

    /**
     * 创建转账交易(离线)
     * create transfer transaction(off-line)
     *
     * @param transferDto 转账请求参数
     * @return
     */
    public Result createTransferTx(TransferDto transferDto) {
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
                tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
            }
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto.getInputs(), transferDto.getOutputs(), tx.getSize());
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
    private CoinData assemblyCoinData(List<CoinFromDto> inputs, List<CoinToDto> outputs, int txSize) throws NulsException {
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
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
        CoinFromDto fromDto = dto.getDepositList().get(0).getInput();
        int chainId = fromDto.getAssetChainId();
        int assetId = fromDto.getAssetId();

        List<CoinFrom> coinFromList = new ArrayList<>();
        //组装创建节点交易的coinFrom
        byte[] addressBytes = AddressTool.getAddress(dto.getAgentAddress());
        CoinFrom coinFrom = new CoinFrom(addressBytes, chainId, assetId, dto.getDeposit(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(dto.getAgentHash());
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFromList.add(coinFrom);

        Map<String, CoinFromDto> dtoMap = new HashMap<>();
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
        CoinTo coinTo = new CoinTo(addressBytes, coinFrom.getAssetsChainId(), coinFrom.getAssetsId(), coinFrom.getAmount().subtract(fee), NulsDateUtils.getCurrentTimeSeconds() + SDKContext.STOP_AGENT_LOCK_TIME);
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

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
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
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
}

