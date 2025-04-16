package io.nuls.v2;

import io.nuls.core.model.StringUtils;
import io.nuls.v2.enums.ChainFeeSettingType;
import io.nuls.v2.model.ChainFeeSetting;
import io.nuls.v2.util.TxFeeUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NulsSDKBootStrap {

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
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
    }

    public static void init(int chainId, String addressPrefix, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.addressPrefix = addressPrefix;
    }

    @Deprecated
    public static void init(int chainId, int nulsChainId, String addressPrefix, String httpUrl) {
        initChainId(chainId);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.main_chain_id = nulsChainId;
        SDKContext.addressPrefix = addressPrefix;
    }


    /**
     * NULS-SDK工具连接NULS主网钱包初始化
     *
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void initMain(String httpUrl) {
        initChainId(1);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.addressPrefix = "NULS";
        initChainFeeSetting(1, null);
    }

    public static void initMainWithChainFeeSetting(String httpUrl, List<ChainFeeSetting> settings) {
        initMain(httpUrl);
        initChainFeeSetting(1, settings);
    }

    /**
     * NULS-SDK工具连接NULS测试网钱包初始化
     *
     * @param httpUrl 钱包url访问地址(ip + port)
     */
    public static void initTest(String httpUrl) {
        initChainId(2);
        if (httpUrl != null && !httpUrl.endsWith("/")) {
            httpUrl += "/";
        }
        if (StringUtils.isNotBlank(httpUrl)) {
            SDKContext.wallet_url = httpUrl;
        }
        SDKContext.addressPrefix = "tNULS";
        initChainFeeSetting(2, null);
    }

    public static void initTestWithChainFeeSetting(String httpUrl, List<ChainFeeSetting> settings) {
        initTest(httpUrl);
        initChainFeeSetting(2, settings);
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
        if (chainId == 2) {
            SDKContext.addressPrefix = "tNULS";
        } else if (chainId > 2 && "NULS".equals(SDKContext.addressPrefix)) {
            SDKContext.addressPrefix = "";
        }
        try {
            Class.forName("io.nuls.core.exception.NulsException");
        } catch (ClassNotFoundException e) {
            // skip it
        }
    }

    public static void initChainFeeSetting(int chainId, List<ChainFeeSetting> settings) {
        if (settings != null && !settings.isEmpty()) {
            SDKContext.CHAIN_FEE_SETTING_MAP = settings.stream().collect(Collectors.toMap(ChainFeeSetting::getSymbol, Function.identity()));
            if (chainId == 2) {
                SDKContext.CHAIN_FEE_SETTING_MAP.put(ChainFeeSettingType.NULS.name(), TxFeeUtil.DEFAULT_TESTNET_NULS_FEE_SETTING);
            } else {
                SDKContext.CHAIN_FEE_SETTING_MAP.put(ChainFeeSettingType.NULS.name(), TxFeeUtil.DEFAULT_MAINNET_NULS_FEE_SETTING);
            }
            return;
        }
        if (chainId == 1) {
            settings = TxFeeUtil.DEFAULT_MAINNET_CHAIN_FEE_SETTING;
        } else {
            settings = TxFeeUtil.DEFAULT_TESTNET_CHAIN_FEE_SETTING;
        }
        SDKContext.CHAIN_FEE_SETTING_MAP = settings.stream().collect(Collectors.toMap(ChainFeeSetting::getSymbol, Function.identity()));
        if (chainId == 2) {
            SDKContext.CHAIN_FEE_SETTING_MAP.put(ChainFeeSettingType.NULS.name(), TxFeeUtil.DEFAULT_TESTNET_NULS_FEE_SETTING);
        } else {
            SDKContext.CHAIN_FEE_SETTING_MAP.put(ChainFeeSettingType.NULS.name(), TxFeeUtil.DEFAULT_MAINNET_NULS_FEE_SETTING);
        }
    }


}
