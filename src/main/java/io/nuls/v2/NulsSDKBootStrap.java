package io.nuls.v2;

public class NulsSDKBootStrap {

    /**
     *  nuls sdk init
     * @param chainId 运行链的id
     */
    public static void init(int chainId) {
        if (chainId < 1 || chainId > 65535) {
            throw new RuntimeException("[defaultChainId] is invalid");
        }
        SDKContext.main_chain_id = chainId;
    }
}
