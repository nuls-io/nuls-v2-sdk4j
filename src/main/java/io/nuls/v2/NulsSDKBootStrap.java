package io.nuls.v2;

import io.nuls.v2.util.RestFulUtil;

public class NulsSDKBootStrap {

    /**
     * nuls sdk init
     *
     * @param chainId 运行链的id
     */
    public static void init(int chainId) {
        if (chainId < 1 || chainId > 65535) {
            throw new RuntimeException("[defaultChainId] is invalid");
        }
        SDKContext.main_chain_id = chainId;
    }

    public static void init(String httpUrl) {
        SDKContext.wallet_url = httpUrl;
    }

    public static void init(int chainId, String httpUrl) {
        init(chainId);
        init(httpUrl);
    }
}
