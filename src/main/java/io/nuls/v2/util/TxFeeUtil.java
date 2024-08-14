package io.nuls.v2.util;

import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.ChainFeeSetting;

import java.io.IOException;
import java.util.List;

public class TxFeeUtil {

    public final static List<ChainFeeSetting> DEFAULT_TESTNET_CHAIN_FEE_SETTING;
    public final static List<ChainFeeSetting> DEFAULT_MAINNET_CHAIN_FEE_SETTING;
    static {
        String testnet = "[{\"symbol\":\"NULS\",\"scFeeFoefficient\":\"1\",\"feePerKB\":\"100000\",\"assetId\":\"2-1\",\"decimals\":8},{\"symbol\":\"BTC\",\"scFeeFoefficient\":\"0.0001\",\"feePerKB\":\"10000\",\"assetId\":\"2-201\",\"decimals\":8},{\"symbol\":\"ETH\",\"scFeeFoefficient\":\"10000000\",\"feePerKB\":\"100000000000000\",\"assetId\":\"2-202\",\"decimals\":18}]";
        String mainnet = "[{\"assetId\":\"1-1\",\"symbol\":\"NULS\",\"decimals\":8,\"feePerKB\":\"100000\",\"scFeeFoefficient\":\"1\"},{\"assetId\":\"9-787\",\"symbol\":\"BTC\",\"decimals\":8,\"feePerKB\":\"10000\",\"scFeeFoefficient\":\"0.0001\"},{\"assetId\":\"9-2\",\"symbol\":\"ETH\",\"decimals\":18,\"feePerKB\":\"100000000000000\",\"scFeeFoefficient\":\"10000000\"}]";
        try {
            DEFAULT_TESTNET_CHAIN_FEE_SETTING = JSONUtils.json2list(testnet, ChainFeeSetting.class);
            DEFAULT_MAINNET_CHAIN_FEE_SETTING = JSONUtils.json2list(mainnet, ChainFeeSetting.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
