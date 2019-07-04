package io.nuls.v2;

import io.nuls.core.parse.I18nUtils;

public class NulsSDKBootStrap {

    private static final String LANGUAGE = "en";
    private static final String LANGUAGE_PATH =  "languages";
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
        I18nUtils.loadLanguage(NulsSDKBootStrap.class, LANGUAGE_PATH, LANGUAGE);
    }

    public static void init(String httpUrl) {
        SDKContext.wallet_url = httpUrl;
    }

    public static void init(int chainId, String httpUrl) {
        init(chainId);
        init(httpUrl);
    }
}
