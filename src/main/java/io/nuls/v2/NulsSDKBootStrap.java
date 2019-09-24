package io.nuls.v2;

import io.nuls.core.parse.I18nUtils;

public class NulsSDKBootStrap {

    private static final String LANGUAGE = "en";
    private static final String LANGUAGE_PATH = "languages";


    /**
     * NULS-SDK工具初始化
     * 设置对接的链的ID和钱包NULS-SDK-Provider模块的url访问地址
     *
     * @param chainId 链ID
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void init(int chainId, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     * NULS-SDK工具连接NULS主网钱包初始化
     * 设置主网钱包NULS-SDK-Provider模块的url访问地址
     *
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void initMain(String httpUrl) {
        initChainId(1);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     * NULS-SDK工具连接NULS测试网钱包初始化
     * 设置测试网钱包NULS-SDK-Provider模块的url访问地址
     *
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void initTest(String httpUrl) {
        initChainId(2);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
    }

    /**
     * nuls sdk init
     *
     * @param chainId 运行链的id
     */
    private static void initChainId(int chainId) {
        if (chainId < 1 || chainId > 65535) {
            throw new RuntimeException("[defaultChainId] is invalid");
        }
        SDKContext.main_chain_id = chainId;
        SDKContext.nuls_chain_id = chainId;
        I18nUtils.loadLanguage(NulsSDKBootStrap.class, LANGUAGE_PATH, LANGUAGE);
    }

}
