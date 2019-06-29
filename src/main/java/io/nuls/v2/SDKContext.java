package io.nuls.v2;

import java.math.BigInteger;

public class SDKContext {

    public static String DEFAULT_ENCODING = "UTF-8";
    /**
     * 本链id
     */
    public static int main_chain_id = 1;
    /**
     * 本链主资产id
     */
    public static int main_asset_id = 1;
    /**
     * NULS主网链id
     */
    public static int nuls_chain_id = 1;
    /**
     * NULS主资产id
     */
    public static int nuls_asset_id = 1;
    /**
     * NULS主网默认转账交易手续费单价
     */
    public static BigInteger NULS_DEFAULT_NORMAL_TX_FEE_PRICE = new BigInteger("100000");
    /**
     * NULS主网默认业务交易手续费单价
     */
    public static BigInteger NULS_DEFAULT_OTHER_TX_FEE_PRICE = new BigInteger("1000000");
    /**
     * 注销共识节点，保证金锁定时间
     */
    public static int stop_agent_lock_time = 3600;

}
