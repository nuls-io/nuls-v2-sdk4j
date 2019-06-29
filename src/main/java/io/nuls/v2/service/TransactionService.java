package io.nuls.v2.service;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.*;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.TxType;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.BigIntegerUtils;
import io.nuls.core.model.StringUtils;
import io.nuls.core.rpc.util.NulsDateUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.constant.AccountConstant;
import io.nuls.v2.error.AccountErrorCode;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.txdata.Agent;
import io.nuls.v2.txdata.CancelDeposit;
import io.nuls.v2.txdata.Deposit;
import io.nuls.v2.util.CommonValidator;
import io.nuls.v2.util.TxUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static io.nuls.v2.util.ValidateUtil.validateChainId;

public class TransactionService {

    private TransactionService() {

    }

    private static TransactionService instance = new TransactionService();

    public static TransactionService getInstance() {
        return instance;
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

            Transaction tx = new Transaction(TxType.TRANSFER);
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());
            tx.setRemark(StringUtils.bytes(transferDto.getRemark()));

            CoinData coinData = assemblyCoinData(transferDto, tx.getSize());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return new Result(true).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR);
        }
    }

    /**
     * 组装转账交易的coinData数据
     * Assemble the coinData for the transfer transaction
     *
     * @param transferDto 转账请求参数
     * @param txSize      交易大小
     * @return coinData
     * @throws NulsException
     */
    private CoinData assemblyCoinData(TransferDto transferDto, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();
        for (CoinFromDto from : transferDto.getInputs()) {
            byte[] address = AddressTool.getAddress(from.getAddress());
            byte[] nonce = HexUtil.decode(from.getNonce());
            CoinFrom coinFrom = new CoinFrom(address, from.getChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
            coinFroms.add(coinFrom);
        }

        List<CoinTo> coinTos = new ArrayList<>();
        for (CoinToDto to : transferDto.getOutputs()) {
            byte[] addressByte = AddressTool.getAddress(to.getAddress());
            CoinTo coinTo = new CoinTo(addressByte, to.getChainId(), to.getAssetId(), to.getAmount(), to.getLockTime());
            coinTos.add(coinTo);
        }

        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
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
            return new Result(true).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR);
        }
    }

    private CoinData assemblyCoinData(CoinFromDto from, BigInteger amount, int txSize) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        byte[] nonce = HexUtil.decode(from.getNonce());
        CoinFrom coinFrom = new CoinFrom(address, from.getChainId(), from.getAssetId(), from.getAmount(), nonce, AccountConstant.NORMAL_TX_LOCKED);
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getChainId(), from.getAssetId(), amount, -1);
        coinTos.add(coinTo);

        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;
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
            return new Result(true).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR);
        }
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
                dto.setPrice(SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE);
            }
            CommonValidator.validateWithDrawDto(dto);
            Transaction tx = new Transaction(TxType.CANCEL_DEPOSIT);
            tx.setTime(NulsDateUtils.getCurrentTimeSeconds());

            CancelDeposit cancelDeposit = new CancelDeposit();
            cancelDeposit.setAddress(AddressTool.getAddress(dto.getAddress()));
            cancelDeposit.setJoinTxHash(NulsHash.fromHex(dto.getDepositHash()));
            tx.setTxData(cancelDeposit.serialize());

            CoinData coinData = assemblyCoinData(dto.getInput(), dto.getDepositHash(), tx.size(), dto.getPrice());
            tx.setCoinData(coinData.serialize());
            tx.setHash(NulsHash.calcHash(tx.serializeForHash()));

            Map<String, Object> map = new HashMap<>();
            map.put("hash", tx.getHash().toHex());
            map.put("txHex", HexUtil.encode(tx.serialize()));
            return new Result(true).setData(map);
        } catch (NulsException e) {
            return Result.getFailed(e.getErrorCode());
        } catch (IOException e) {
            return Result.getFailed(AccountErrorCode.DATA_PARSE_ERROR);
        }

    }


    private CoinData assemblyCoinData(CoinFromDto from, String hash, int txSize, BigInteger price) throws NulsException {
        List<CoinFrom> coinFroms = new ArrayList<>();

        byte[] address = AddressTool.getAddress(from.getAddress());
        CoinFrom coinFrom = new CoinFrom(address, from.getChainId(), from.getAssetId(), from.getAmount(), (byte) -1);
        NulsHash nulsHash = NulsHash.fromHex(hash);
        coinFrom.setNonce(TxUtils.getNonce(nulsHash.getBytes()));
        coinFroms.add(coinFrom);

        List<CoinTo> coinTos = new ArrayList<>();
        CoinTo coinTo = new CoinTo(address, from.getChainId(), from.getAssetId(), from.getAmount().subtract(price), 0);
        coinTos.add(coinTo);

        txSize = txSize + getSignatureSize(coinFroms);
        TxUtils.calcTxFee(coinFroms, coinTos, txSize);
        CoinData coinData = new CoinData();
        coinData.setFrom(coinFroms);
        coinData.setTo(coinTos);
        return coinData;

    }
}

