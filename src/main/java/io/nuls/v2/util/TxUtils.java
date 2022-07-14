package io.nuls.v2.util;

import io.nuls.base.basic.TransactionFeeCalculator;
import io.nuls.base.data.CoinFrom;
import io.nuls.base.data.CoinTo;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.core.exception.NulsException;
import io.nuls.core.model.BigIntegerUtils;
import io.nuls.core.model.StringUtils;
import io.nuls.v2.SDKContext;
import io.nuls.v2.error.AccountErrorCode;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.v2.constant.Constant.NULS_ASSET_ID;
import static io.nuls.v2.constant.Constant.NULS_CHAIN_ID;

public class TxUtils {

    public static boolean isMainAsset(int chainId, int assetId) {
        return chainId == SDKContext.main_chain_id && assetId == SDKContext.main_asset_id;
    }

    public static boolean isNulsAsset(int chainId, int assetId) {
        return chainId == NULS_CHAIN_ID && assetId == NULS_ASSET_ID;
    }

    public static void calcTxFee(List<CoinFrom> coinFroms, List<CoinTo> coinTos, int txSize) throws NulsException {
        BigInteger totalFrom = BigInteger.ZERO;
        for (CoinFrom coinFrom : coinFroms) {
            txSize += coinFrom.size();
            if (TxUtils.isMainAsset(coinFrom.getAssetsChainId(), coinFrom.getAssetsId())) {
                totalFrom = totalFrom.add(coinFrom.getAmount());
            }
        }
        BigInteger totalTo = BigInteger.ZERO;
        for (CoinTo coinTo : coinTos) {
            txSize += coinTo.size();
            if (TxUtils.isMainAsset(coinTo.getAssetsChainId(), coinTo.getAssetsId())) {
                totalTo = totalTo.add(coinTo.getAmount());
            }
        }
        //本交易预计收取的手续费
        BigInteger targetFee = TransactionFeeCalculator.getNormalTxFee(txSize);
        //实际收取的手续费, 可能自己已经组装完成
        BigInteger actualFee = totalFrom.subtract(totalTo);
        if (BigIntegerUtils.isLessThan(actualFee, BigInteger.ZERO)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_FEE);
        } else if (BigIntegerUtils.isLessThan(actualFee, targetFee)) {
            throw new NulsException(AccountErrorCode.INSUFFICIENT_FEE);
        }
    }


    public static BigInteger calcTransferTxFee(int addressCount, int fromLength, int toLength, String remark, BigInteger price) {
        int size = 10;
        size += addressCount * P2PHKSignature.SERIALIZE_LENGTH;
        size += 70 * fromLength;
        size += 68 * toLength;
        if (StringUtils.isNotBlank(remark)) {
            size += StringUtils.bytes(remark).length;
        }
        size = size / 1024 + 1;
        return price.multiply(new BigInteger(size + ""));
    }

    public static Map<String, BigInteger> calcCrossTxFee(int addressCount, int fromLength, int toLength, String remark, boolean isMainNet) {
        int size = 10;

        size += addressCount * P2PHKSignature.SERIALIZE_LENGTH;
        size += 70 * fromLength;
        size += 68 * toLength;
        if (StringUtils.isNotBlank(remark)) {
            size += StringUtils.bytes(remark).length;
        }

        size = size / 1024 + 1;
        BigInteger fee = TransactionFeeCalculator.getCrossTxFee(size);

        Map<String, BigInteger> map = new HashMap<>();


        if (!isMainNet) {
            BigInteger localFee = TransactionFeeCalculator.getNormalTxFee(size);
            map.put("LOCAL", localFee.multiply(BigInteger.valueOf(10)));
            map.put("NULS", fee.multiply(BigInteger.valueOf(10)));
        } else {
            map.put("LOCAL", BigInteger.ZERO);
            map.put("NULS", fee);
        }
        return map;
    }

    public static BigInteger calcCrossTxFee(int addressCount, int fromLength, int toLength, String remark) {
        int size = 10;
        size += addressCount * P2PHKSignature.SERIALIZE_LENGTH;
        size += 70 * fromLength;
        size += 68 * toLength;
        if (StringUtils.isNotBlank(remark)) {
            size += StringUtils.bytes(remark).length;
        }
        size = size / 1024 + 1;
        BigInteger fee = TransactionFeeCalculator.getCrossTxFee(size);
        return fee;
    }

    public static BigInteger calcStopConsensusTxFee(int fromLength, int toLength, BigInteger price) {
        int size = 152;
        size += 70 * fromLength;
        size += 68 * toLength;
        size = size / 1024 + 1;
        return price.multiply(new BigInteger(size + ""));
    }

    /**
     * 根据交易HASH获取NONCE（交易HASH后8位）
     * Obtain NONCE according to HASH (the last 8 digits of HASH)
     */
    public static byte[] getNonce(byte[] txHash) {
        byte[] targetArr = new byte[8];
        System.arraycopy(txHash, txHash.length - 8, targetArr, 0, 8);
        return targetArr;
    }

    public static String cleanHexPrefix(String input) {
        return containsHexPrefix(input) ? input.substring(2) : input;
    }

    public static boolean containsHexPrefix(String input) {
        return !StringUtils.isBlank(input) && input.length() > 1 && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }
}
