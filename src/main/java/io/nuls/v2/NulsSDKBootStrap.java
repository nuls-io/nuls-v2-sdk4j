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

    public static void init(int chainId, String addressPrefix, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.wallet_url = httpUrl;
        SDKContext.addressPrefix = addressPrefix;
    }

    @Deprecated
    public static void init(int chainId, int nulsChainId, String addressPrefix, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.main_chain_id = nulsChainId;
        SDKContext.wallet_url = httpUrl;
        SDKContext.addressPrefix = addressPrefix;
    }

    /**
     * 初始化为 nuls 与 nerve 对接跨链关系的工具包
     * 初始化时必须按照以下方式来使用(默认已配置成主网对主网)
     *  nuls测试网对nerve测试网
     *  nuls主网对nerve主网
     *
     * @param chainId
     * @param nerveChainId
     * @param addressPrefix
     * @param addressPrefixNerve
     * @param httpUrl
     */
    public static void init(int chainId, int nerveChainId, String addressPrefix,String addressPrefixNerve, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        SDKContext.nerve_chain_id = nerveChainId;
        SDKContext.wallet_url = httpUrl;
        SDKContext.addressPrefix = addressPrefix;
        SDKContext.addressPrefix_nerve = addressPrefixNerve;
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
        SDKContext.nerve_chain_id = 5;
        SDKContext.addressPrefix = "tNULS";
        SDKContext.addressPrefix_nerve = "TNVT";
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
        I18nUtils.loadLanguage(NulsSDKBootStrap.class, LANGUAGE_PATH, LANGUAGE);
    }


}
