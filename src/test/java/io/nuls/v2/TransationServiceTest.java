package io.nuls.v2;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.CoinData;
import io.nuls.base.data.Transaction;
import io.nuls.base.signture.MultiSignTxSignature;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.enums.ChainFeeSettingType;
import io.nuls.v2.model.ChainFeeSetting;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.nuls.v2.constant.Constant.NULS_ASSET_ID;
import static io.nuls.v2.constant.Constant.NULS_CHAIN_ID;

public class TransationServiceTest {

    static String address = "8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "6df381435098e47b685cdc00fa1d7c66fa2ba9cc441179c6dd1a5686153fb0ee";
    static String encryptedPrivateKey = "0c8e925d27660dbd04104455c001efe7a5d4cba8fc484d06506c8ff4baa653be2d69e31c971243e2185782cabbbe265a";
    static String password = "abcd1234";

    static String packingAddress = "8CPcA7kag6XT1a2yoiTijYaJGY7jceebYWFFq";

    static List<ChainFeeSetting> SETTING;
    static {
        try {
            SETTING = JSONUtils.json2list("[\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-1\",\n" +
                    "        \"symbol\": \"NULS\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"100000\",\n" +
                    "        \"scFeeFoefficient\": \"1\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-2\",\n" +
                    "        \"symbol\": \"BTC\",\n" +
                    "        \"decimals\": 8,\n" +
                    "        \"feePerKB\": \"10000\",\n" +
                    "        \"scFeeFoefficient\": \"0.0001\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"assetId\": \"2-3\",\n" +
                    "        \"symbol\": \"ETH\",\n" +
                    "        \"decimals\": 18,\n" +
                    "        \"feePerKB\": \"100000000000000\",\n" +
                    "        \"scFeeFoefficient\": \"10000000\"\n" +
                    "    }\n" +
                    "]", ChainFeeSetting.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void before() {
        //NulsSDKBootStrap.initTestWithChainFeeSetting("http://localhost:18004/", SETTING);
        NulsSDKBootStrap.initTest("https://beta.api.nuls.io");
        //NulsSDKBootStrap.initMain("https://api.nuls.io");
    }

    @Test
    public void testCoinBase() throws Exception{
        List<String> hashList = new ArrayList<>();
        //hashList.add("312bd90e51c437a6588af53556ae7a19895c47775b9bc42a9da36be69277795d");
        hashList.add("6339d96186eae20cc5b889e03383accd9adfea56e824652c560761f44d4ed8a7");

        Set<String> coinBaseTxHashSet = new LinkedHashSet<>();
        for (String hash : hashList) {
            Result tx = NulsSDKTool.getTx(hash);
            TransactionDto txInfo = (TransactionDto) tx.getData();
            BlockDto block = (BlockDto) NulsSDKTool.getBlock(txInfo.getBlockHeight()).getData();
            TransactionDto coinbaseTx = block.getTxs().get(0);
            if (!coinBaseTxHashSet.add(coinbaseTx.getHash())) {
                continue;
            }
            boolean exist = false;
            List<CoinTosDto> tos = coinbaseTx.getTo();
            for (CoinTosDto to : tos) {
                if (AddressTool.validContractAddress(AddressTool.getAddress(to.getAddress()), 1)) {
                    System.out.println(JSONUtils.obj2json(to));
                    exist = true;
                }
            }
            if (exist) {
                System.out.println(String.format("coinbase tx [%s] exist contract", coinbaseTx.getHash()));
            }
        }
        System.out.println("CoinBase Tx Hash: ");
        coinBaseTxHashSet.forEach(c -> System.out.println(c));
    }

    @Test
    public void desTest() throws Exception {
        String pri = "7cbbf2d3cc7136ca906ad980abe4ec1164b919a9260db075283a04da43354b80";
        String addr = "TNVTdTSPQtQhy2xCY95XnJiDK6XAGw3EfZeKD";
        String hex = "10004f63ad672436633465653766312d376137392d343063312d623836332d363833663061646130616536a8020001aafa57b0d1d4fe629b58ad2a5d8fe412ca6a4bf50200023c16df1ed0a0beec061e67b42c866782967449280000000000000000000000000000000000000000000000000000000000000000e0930400000000001900000000000000087472616e7366657200020126744e554c536542614d75427a4234636a725354626568356b7a32714538716178586b7473335701153130303030303030303030303030303030303030308c0117020001aafa57b0d1d4fe629b58ad2a5d8fe412ca6a4bf502000100008e0c0100000000000000000000000000000000000000000000000000000000080e761c0f826eefb8000117020001e6d9409c5bdf62965998a4ddc51a0b8ee51078be0200010080969800000000000000000000000000000000000000000000000000000000000000000000000000006921037c92dfbdafb4de8f1249c2b2c34c693d1525ae47eac6f95146c8233e107879ad46304402200a851e51fef4bd936b3a66cde5115407d35fd3041c042ff73b39b748efb7f7b402205902ddd4cb776da52a8957712e11b4b921c1eefc76daecf6471b8c6087516182";
        //Result sign = NulsSDKTool.sign(hex, addr, pri);
        Transaction tx = new Transaction();
        tx.parse(HexUtil.decode(hex), 0);
        System.out.println(tx);
        //byte[] coinData = tx.getCoinData();
        //CoinData obj = new CoinData();
        //obj.parse(coinData, 0);
        //System.out.println(sign);
    }

    @Test
    public void testCreateTxByType() throws Exception {
        //0.1 NULS
        //0.00000100 BTC
        //0.00003 ETH
        String teamFeeAddr = "tNULSeBaMg1rfWF5aieibDXK2dzDuar45sgtsP";
        BigInteger teamFeeNULS = new BigDecimal("0.1").movePointRight(8).toBigInteger();
        BigInteger teamFeeBTC = new BigDecimal("0.000001").movePointRight(8).toBigInteger();
        BigInteger teamFeeETH = new BigDecimal("0.00003").movePointRight(18).toBigInteger();

        //SDKContext.CHAIN_FEE_SETTING_MAP.get("ETH").setAssetId("5-2");
        String from = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";
        String to = "tNULSeBaMnrs6JKrCy6TQdzYJZkMZJDng7QAsD";
        BigInteger amount = new BigDecimal("0.123").movePointRight(8).toBigInteger();
        long time = System.currentTimeMillis() / 1000;
        String remark = "feeType test";

        Result<Map> result;
        String txHex;

        /*result = NulsSDKTool.callContractTxOfflineByFeeType(
                "tNULSeBaMvCL5jpJiaiUmo2sBEFAVbypXG692o",
                new BigDecimal("100").movePointRight(8).toBigInteger(),
                "tNULSeBaN1HcXPtSAdowt43DJKey1txjR6bSCu",
                200000,
                "depositForOwn",
                "",
                null, null, null, null,
                ChainFeeSettingType.NULS,
                teamFeeAddr,
                teamFeeNULS);
        System.out.println(JSONUtils.obj2PrettyJson(result));*/

        // String fromAddress, String toAddress, BigInteger amount, long time, String remark, ChainFeeSettingType feeType
        /*result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.NULS, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by NULS fee: " + txHex);
        this.signAndBoradcast(txHex);*/
        result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.ETH, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by ETH fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.BTC, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by BTC fee: " + txHex);
        this.signAndBoradcast(txHex);

        /*result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.NULS, teamFeeAddr, teamFeeNULS);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by NULS fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);*/
        result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.ETH, teamFeeAddr, teamFeeETH);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by ETH fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTxSimpleTransferOfNulsByFeeType(from, to, amount, time, remark, ChainFeeSettingType.BTC, teamFeeAddr, teamFeeBTC);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NULS by BTC fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);

        int assetChainId ;
        int assetId;
        String transferSymbol;

        /*assetChainId = 5;
        assetId = 1;
        transferSymbol = "NVT";
        // String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark, ChainFeeSettingType feeType
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.NULS, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by NULS fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.ETH, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by ETH fee: %s" , transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.BTC, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by BTC fee: %s" , transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.NULS, teamFeeAddr, teamFeeNULS);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by NULS fee & Team Fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.ETH, teamFeeAddr, teamFeeETH);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by ETH fee & Team Fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.BTC, teamFeeAddr, teamFeeBTC);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by BTC fee & Team Fee: %s" , transferSymbol, txHex));

        assetChainId = 2;
        assetId = 202;
        transferSymbol = "ETH";
        // String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount, long time, String remark, ChainFeeSettingType feeType
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.NULS, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by NULS fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.ETH, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by ETH fee: %s" , transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.BTC, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by BTC fee: %s" , transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.NULS, teamFeeAddr, teamFeeNULS);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by NULS fee & Team Fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.ETH, teamFeeAddr, teamFeeETH);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by ETH fee & Team Fee: %s", transferSymbol, txHex));
        result = NulsSDKTool.createTxSimpleTransferOfNonNulsByFeeType(from, to, assetChainId, assetId, amount, time, remark, ChainFeeSettingType.BTC, teamFeeAddr, teamFeeBTC);
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("transfer %s by BTC fee & Team Fee: %s" , transferSymbol, txHex));*/

        String contractAddress = "tNULSeBaMyPq6kY8wbofhqs1qpLhNGMJkbRpPh";//testnet
        //String contractAddress = "tNULSeBaMwB4nKCwEGiwjzzGHseHEeeSAEtChf";//dev
        amount = new BigDecimal("0.123").movePointRight(8).toBigInteger();
        long gasLimit = 500000;
        // String fromAddress, String toAddress, String contractAddress, long gasLimit, BigInteger amount, long time, String remark, ChainFeeSettingType feeType
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.NULS, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by NULS fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.ETH, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by ETH fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.BTC, null, null);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by BTC fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.NULS, teamFeeAddr, teamFeeNULS);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by NULS fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.ETH, teamFeeAddr, teamFeeETH);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by ETH fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);
        result = NulsSDKTool.createTokenTransferTxByFeeType(from, to, contractAddress, gasLimit, amount, time, remark, ChainFeeSettingType.BTC, teamFeeAddr, teamFeeBTC);
        txHex = (String) result.getData().get("txHex");
        System.out.println("transfer NRC20 by BTC fee & Team Fee: " + txHex);
        this.signAndBoradcast(txHex);


    }

    private void signAndBoradcast(String txHex) throws Exception {
        Result<Map> result = NulsSDKTool.sign(txHex, "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG", "9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b");
        txHex = (String) result.getData().get("txHex");
        System.out.println(String.format("signed tx: %s", txHex));
        String txHash = (String) result.getData().get("hash");
        System.out.println(String.format("hash: %s", txHash));
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(String.format("result: %s", JSONUtils.obj2PrettyJson(result)));
        TimeUnit.SECONDS.sleep(8);
    }

    @Test
    public void testBroadTx() {
        String txHex = "02003c812b5f0672656d61726b008c0117050001f7ec6473df12e751d64cf20a8baa7edd50810f810500010000e18a79c2480000000000000000000000000000000000000000000000000000089cd92b91c5e536540001170500017fe9a685e43b3124e00fd9c8e4e59158baea63450200010000009573c24800000000000000000000000000000000000000000000000000000000000000000000692103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e346304402205e335bf49a5e1d963df18b5349ebc4642e1db9dc3c6a876fa318dc375f10e18502206511b6bbffb77a40bc966cb725d7835b4122b4d5ccbd5fddf37e5c4d0a161874";
        //广播
        Result result = NulsSDKTool.broadcast(txHex);
        Map map = (Map) result.getData();
        String hash = (String) map.get("value");
        System.out.println(hash);

        txHex = "020059812b5f0672656d61726b008c0117050001f7ec6473df12e751d64cf20a8baa7edd50810f810500010000e18a79c2480000000000000000000000000000000000000000000000000000089cd92b91c5e53654000117050001bc9cf2a09f0d1dbe7ab0a7dca2ccb87d12da6a990200010000009573c24800000000000000000000000000000000000000000000000000000000000000000000692103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e346304402207bb75ebb571f4ad8ade1e2b726de9dd854ca203a272e149bc9bd3ec5f9c18a0402201ab59a5762c5c30bff9db33e6e6d8b66ba8e5abfc58835a0b0eeb76583fac61c";
        result = NulsSDKTool.broadcast(txHex);
        map = (Map) result.getData();
        if (result.isSuccess()) {
            hash = (String) map.get("value");
            System.out.println(hash);
        } else {
            map.get("msg");
        }

    }

    @Test
    public void testCreateTransferTx() {
        String fromAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";
        String toAddress = "tNULSeBaMsEfHKEXvaFPPpQomXipeCYrru6t81";

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(new BigInteger("20000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("11f5e4619e0a12c2");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("20000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result<Map> result = NulsSDKTool.createTransferTxOffline(transferDto);
        String txHex = (String) result.getData().get("txHex");

        System.out.println(txHex);
        ////签名
        //String prikey = "";
        //result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        //txHex = (String) result.getData().get("txHex");
        //
        //String txHash = (String) result.getData().get("hash");
        ////广播
        //result = NulsSDKTool.broadcast(txHex);
    }


    @Test
    public void testCreateCrossTransferTx() {
        String fromAddress = "TNVTdTSPLmP6SKyn2RigSA8Lr9bMTgjUhnve4";
        String toAddress = "tNULSeBaMoRp6QhNYSF8xjiwBFYnvCoCjkQzvU";
        int assetChainId = 2;
        int assetId = 1;
        BigInteger transferAmount = BigInteger.valueOf(735959726L);

        CrossTransferTxFeeDto feeDto = new CrossTransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        feeDto.setAssetChainId(assetChainId);
        feeDto.setAssetId(assetId);

        Map<String, BigInteger> map = NulsSDKTool.calcCrossTransferTxFee(feeDto);
        BigInteger nulsFee = map.get("NULS");       //跨链交易需要的NULS手续费
        BigInteger localFee = map.get("LOCAL");     //跨链交易需要的本链手续费，如果当前链就是NULS，localFee为0

        List<CoinFromDto> inputs = new ArrayList<>();

        //判断当前链是不是NULS链
        boolean isMainNet = false;
        if (SDKContext.main_chain_id == NULS_CHAIN_ID) {
            isMainNet = true;
        }

        //如果是主网发起的跨链转账
        if (isMainNet) {
            if (assetChainId == NULS_CHAIN_ID && assetId == NULS_ASSET_ID) {
                //如果转账的是NULS资产，则记得添加上跨链手续费
                CoinFromDto from = new CoinFromDto();
                from.setAddress(fromAddress);
                from.setAssetChainId(assetChainId);
                from.setAssetId(assetId);
                from.setAmount(transferAmount.add(nulsFee));
                from.setNonce("bcd7883c67f4fe85");
                inputs.add(from);
            } else if (assetChainId == SDKContext.main_chain_id && assetId == SDKContext.main_asset_id) {
                CoinFromDto from = new CoinFromDto();
                from.setAddress(fromAddress);
                from.setAssetChainId(SDKContext.main_chain_id);
                from.setAssetId(SDKContext.main_asset_id);
                from.setAmount(transferAmount);
                from.setNonce("bcd7883c67f4fe85");
                inputs.add(from);

                //记得单独添加跨链手续费
                CoinFromDto from2 = new CoinFromDto();
                from2.setAddress(fromAddress);
                from2.setAssetChainId(NULS_CHAIN_ID);
                from2.setAssetId(NULS_ASSET_ID);
                from2.setAmount(nulsFee);
                from2.setNonce("bcd7883c67f4fe85");
                inputs.add(from2);
            }
        } else {
            if (assetChainId == NULS_CHAIN_ID && assetId == NULS_ASSET_ID) {
                //如果转账的是NULS资产，则记得添加上跨链手续费
                CoinFromDto from = new CoinFromDto();
                from.setAddress(fromAddress);
                from.setAssetChainId(assetChainId);
                from.setAssetId(assetId);
                from.setAmount(transferAmount.add(nulsFee));
                from.setNonce("5baac6ee777b11f7");
                inputs.add(from);

                //再添加上本链的交易手续费
                CoinFromDto from2 = new CoinFromDto();
                from2.setAddress(fromAddress);
                from2.setAssetChainId(SDKContext.main_chain_id);
                from2.setAssetId(SDKContext.main_asset_id);
                from2.setAmount(localFee);
                from2.setNonce("5baac6ee777b11f7");
                inputs.add(from2);
            } else if (assetChainId == SDKContext.main_chain_id && assetId == SDKContext.main_asset_id) {
                //如果转账的是本链资产
                CoinFromDto from = new CoinFromDto();
                from.setAddress(fromAddress);
                from.setAssetChainId(SDKContext.main_chain_id);
                from.setAssetId(SDKContext.main_asset_id);
                from.setAmount(transferAmount.add(localFee));
                from.setNonce("5baac6ee777b11f7");
                inputs.add(from);

                //再添加跨链手续费
                CoinFromDto from2 = new CoinFromDto();
                from2.setAddress(fromAddress);
                from2.setAssetChainId(NULS_CHAIN_ID);
                from2.setAssetId(NULS_ASSET_ID);
                from2.setAmount(nulsFee);
                from2.setNonce("5baac6ee777b11f7");
                inputs.add(from2);
            } else {
                //如果转的是其他资产
                CoinFromDto from = new CoinFromDto();
                from.setAddress(fromAddress);
                from.setAssetChainId(assetChainId);
                from.setAssetId(assetId);
                from.setAmount(transferAmount);
                from.setNonce("5baac6ee777b11f7");

                //添加本链转账手续费
                CoinFromDto from2 = new CoinFromDto();
                from2.setAddress(fromAddress);
                from2.setAssetChainId(SDKContext.main_chain_id);
                from2.setAssetId(SDKContext.main_asset_id);
                from2.setAmount(localFee);
                from2.setNonce("5baac6ee777b11f7");
                inputs.add(from2);

                //添加跨链转账手续费
                CoinFromDto from3 = new CoinFromDto();
                from3.setAddress(fromAddress);
                from3.setAssetChainId(NULS_CHAIN_ID);
                from3.setAssetId(NULS_ASSET_ID);
                from3.setAmount(nulsFee);
                from3.setNonce("5baac6ee777b11f7");
                inputs.add(from3);
            }
        }

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(transferAmount);
        to.setAssetChainId(assetChainId);
        to.setAssetId(assetId);
        outputs.add(to);


        TransferDto transferDto = new TransferDto();
        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result<Map> result = NulsSDKTool.createCrossTransferTxOffline(transferDto);
        String txHex = (String) result.getData().get("txHex");

        //签名
        String prikey = "33d7eebc0efab7a0c9959d0ac2297923e913ba14028edb4fd9f02e98de2803cb";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");

        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        String hash = (String) result.getData().get("hash");
        System.out.println(hash);
    }


    @Test
    public void desMultiSignTx() throws Exception {
        String hex = "02008fc0856700008c011701000351ea1fbc0fe65d579069201a58a99b6fc752c8ed01000100201d9a000000000000000000000000000000000000000000000000000000000008000000000000000000011701000129562d0f704057c97103756e823b6cfe02c2146d0100010080969800000000000000000000000000000000000000000000000000000000000000000000000000fd0c0204062102089c6782dac3c9dc3c9d196c31aeda179029c4cc8bac2013b50d6b9314aaebd6210325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db95692103439a5c8fe9928370bf86cd228203ff4088ef54605ece18743a99b8b5b6cc09512103a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e42103b0d0d5780059e25d9b4c2ab4466a15f5ffc4a7bb0e6d2198543e6233ce5543f52103d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6210325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db956947304502210094ec9b7df115bfd337810a41bb5c9a1cc80170045f3aa83e76657817e21f05bd022017a63484712c028f6d6a984bf0bb3e22324b3c4616972fc5bcafbbc288d7f93a2103d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6473045022100a89ff570346f1fafbd041a550cd49ecbfecaccfe481bf97b44c24de20acf6b7e02200501713cfebed6d7e722f20a85aa176d93ce1cd24b16cf2b37757e3b331cf9112103a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e44730450221009380de2e2a28044b6f1c0d9dac9c88f7742dec9024cc1d27c593a68db5fef62202204e9b0eb7f99f447075518f1e55b502203b32a16113fc77baf7042fff3125bd7d";
        Transaction tx = new Transaction();
        tx.parse(HexUtil.decode(hex), 0);
        byte[] signature = tx.getTransactionSignature();
        MultiSignTxSignature txSignature = new MultiSignTxSignature();
        txSignature.parse(signature, 0);
        System.out.println(txSignature.getSignersCount());
        List<P2PHKSignature> list = txSignature.getP2PHKSignatures();
        for (P2PHKSignature s : list) {
            System.out.println(HexUtil.encode(s.getPublicKey()));
        }
    }

    @Test
    public void testCreateMultiSignTx() {
        String multiSignAddress = "tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy";
        String toAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";

        MultiSignTransferDto transferDto = new MultiSignTransferDto();

//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");

        transferDto.setPubKeys(pubKeys);
        transferDto.setMinSigns(2);

        MultiSignTransferTxFeeDto feeDto = new MultiSignTransferTxFeeDto();
        feeDto.setPubKeyCount(2);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcMultiSignTransferTxFee(feeDto);

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(multiSignAddress);
        from.setAmount(new BigInteger("100000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("0000000000000000");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("100000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result result = NulsSDKTool.createMultiSignTransferTxOffline(transferDto);
        System.out.println(result.getData());
    }

    @Test
    public void testCreateAgentTx() {
        //创建节点保证金
        BigInteger deposit = new BigInteger("2000000000000");
        BigInteger fee = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;

        ConsensusDto dto = new ConsensusDto();
        dto.setAgentAddress(address);
        dto.setPackingAddress(packingAddress);
        dto.setCommissionRate(10);
        dto.setDeposit(deposit);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("0000000000000000");

        dto.setInput(fromDto);

        Result result = NulsSDKTool.createConsensusTxOffline(dto);
        System.out.println(result.getData());
        //04002ad2155d006600204aa9d101000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d640001cf22472370724906e94f72faa361dcb24bee125864000115423f8fc2f9f62496cb98d43e3347bd7996327d0a8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100406259a9d101000000000000000000000000000000000000000000000000000008000000000000000000011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d1010000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd
    }

    @Test
    public void testCreateDepositTx() {
        //委托共识金额
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger fee = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;

        DepositDto depositDto = new DepositDto();
        depositDto.setAddress(address);
        depositDto.setDeposit(deposit);
        depositDto.setAgentHash("584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd");

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("63b6e201aa9af5f0");

        depositDto.setInput(fromDto);

        Result result = NulsSDKTool.createDepositTxOffline(depositDto);
        System.out.println(result.getData());
        //05003ad9155d005700d0ed902e00000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d640001004012fd902e00000000000000000000000000000000000000000000000000000008827dfaef75714ebd00011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f
    }

    @Test
    public void testWithDrawDepositTx() {
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger price = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;

        WithDrawDto drawDto = new WithDrawDto();
        drawDto.setAddress(address);
        drawDto.setDepositHash("f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f");
        drawDto.setPrice(price);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress(address);
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit);

        drawDto.setInput(fromDto);

        Result result = NulsSDKTool.createWithdrawDepositTxOffline(drawDto);
        System.out.println(result.getData());
        //txHex：06005ae5155d0020f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e00000000000000000000000000000000000000000000000000000008622f795c2196d64fff011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100c08dde902e000000000000000000000000000000000000000000000000000000000000000000000000
        //hash：84fd9e76616f6ff6ac82628bc99d20ebff20da7478df79743724bf007e4c2805
    }

    @Test
    public void testStopConsensusTx() {
        StopConsensusDto dto = new StopConsensusDto();
        dto.setAgentHash("584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd");
        dto.setAgentAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        dto.setDeposit(new BigInteger("2000000000000"));
        dto.setPrice(new BigInteger("100000"));
        List<StopDepositDto> list = new ArrayList<>();

        StopDepositDto depositDto1 = new StopDepositDto();
        depositDto1.setDepositHash("8ada2c25024ee0559b3d78c8e7695184b1c73b42b3a0ba586db83fdd14f6f233");
        CoinFromDto fromDto1 = new CoinFromDto();
        fromDto1.setAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        fromDto1.setAssetChainId(SDKContext.main_chain_id);
        fromDto1.setAssetId(SDKContext.main_asset_id);
        fromDto1.setAmount(new BigInteger("200000000000"));
        depositDto1.setInput(fromDto1);
        list.add(depositDto1);

        StopDepositDto depositDto2 = new StopDepositDto();
        depositDto2.setDepositHash("02d6b74d99c8406e30f9267c8e79f69b318f9a12a162063d63b6e201aa9af5f0");
        CoinFromDto fromDto2 = new CoinFromDto();
        fromDto2.setAddress("8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL");
        fromDto2.setAssetChainId(SDKContext.main_chain_id);
        fromDto2.setAssetId(SDKContext.main_asset_id);
        fromDto2.setAmount(new BigInteger("200000000000"));
        depositDto2.setInput(fromDto2);
        list.add(depositDto2);

        dto.setDepositList(list);

        Result result = NulsSDKTool.createStopConsensusTxOffline(dto);
        System.out.println(result.getData());

        //txHex：090081fd165d0020584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebdfd5c01031764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d101000000000000000000000000000000000000000000000000000008827dfaef75714ebdff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000086db83fdd14f6f233ff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e0000000000000000000000000000000000000000000000000000000863b6e201aa9af5f0ff021764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100609948a9d1010000000000000000000000000000000000000000000000000000990b175d000000001764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000a0db215d000000000000000000000000000000000000000000000000000000000000000000000000
        //txHash：d86502a23ab0e18ab78a43fce3fc302dc70d93467bc52922eb96ecf00d630f58
    }

    @Test
    public void testGetTx() {
        String hash = "8e4e864b2345518163c3dd46c08b2f9a66a496ed65840a5bc12be2335ca524e9";
        Result<TransactionDto> result = NulsSDKTool.getTx(hash);
        TransactionDto tx = result.getData();
        System.out.println(tx.getInBlockIndex());
    }

    @Test
    public void getTransaction() {
        String hash = "8e4e864b2345518163c3dd46c08b2f9a66a496ed65840a5bc12be2335ca524e9";
        Result<TransactionDto> result = NulsSDKTool.getTransaction(hash);
        TransactionDto tx = result.getData();
        System.out.println(tx.getBlockHash());
    }

    @Test
    public void testMultiCreateAgentTx() {
        //创建节点保证金
        BigInteger deposit = new BigInteger("2000000000000");
        BigInteger fee = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;

//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");

        MultiSignConsensusDto dto = new MultiSignConsensusDto();
        dto.setAgentAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        dto.setPackingAddress("tNULSeBaMowgMLTbRUngAuj2BvGy2RmVLt3okv");
        dto.setCommissionRate(10);
        dto.setDeposit(deposit);
        dto.setPubKeys(pubKeys);
        dto.setMinSigns(2);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("fd5de2b08d42bd69");

        dto.setInput(fromDto);

        Result result = NulsSDKTool.createMultiSignConsensusTx(dto);
        System.out.println(result.getData());
        //04002ad2155d006600204aa9d101000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d640001cf22472370724906e94f72faa361dcb24bee125864000115423f8fc2f9f62496cb98d43e3347bd7996327d0a8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100406259a9d101000000000000000000000000000000000000000000000000000008000000000000000000011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d1010000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd
    }

    @Test
    public void testMultiCreateDepositTx() {
        //委托共识金额
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger fee = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;
//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");


        MultiSignDepositDto depositDto = new MultiSignDepositDto();
        depositDto.setPubKeys(pubKeys);
        depositDto.setMinSigns(2);
        depositDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        depositDto.setDeposit(deposit);
        depositDto.setAgentHash("e67ed0f09cea8bd4e2ad3b4b6d83a39841f9f83dd2a9e5737b73b4d5ad203537");

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit.add(fee));
        fromDto.setNonce("7d81947431ba90ad");

        depositDto.setInput(fromDto);

        Result result = NulsSDKTool.createMultiSignDepositTxOffline(depositDto);
        System.out.println(result.getData());
        //05003ad9155d005700d0ed902e00000000000000000000000000000000000000000000000000000064000115423f8fc2f9f62496cb98d43e3347bd7996327d584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebd8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d640001004012fd902e00000000000000000000000000000000000000000000000000000008827dfaef75714ebd00011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000ffffffffffffffff00
        //f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f
    }

    @Test
    public void testMultiSignWithDrawDepositTx() {
        BigInteger deposit = new BigInteger("200000000000");
        BigInteger price = SDKContext.NULS_DEFAULT_OTHER_TX_FEE_PRICE;
//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");

        MultiSignWithDrawDto drawDto = new MultiSignWithDrawDto();
        drawDto.setPubKeys(pubKeys);
        drawDto.setMinSigns(2);
        drawDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        drawDto.setDepositHash("c395d6a03a58b7efc50916f80db17e200999ded125249769e2a45f6068c4bb7a");
        drawDto.setPrice(price);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto.setAssetChainId(SDKContext.main_chain_id);
        fromDto.setAssetId(SDKContext.main_asset_id);
        fromDto.setAmount(deposit);

        drawDto.setInput(fromDto);

        Result result = NulsSDKTool.createMultiSignWithdrawDepositTxOffline(drawDto);
        System.out.println(result.getData());
        //txHex：06005ae5155d0020f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e00000000000000000000000000000000000000000000000000000008622f795c2196d64fff011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100c08dde902e000000000000000000000000000000000000000000000000000000000000000000000000
        //hash：84fd9e76616f6ff6ac82628bc99d20ebff20da7478df79743724bf007e4c2805
    }

    @Test
    public void testMultiSignStopConsensusTx() {
        MultiSignStopConsensusDto dto = new MultiSignStopConsensusDto();
//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db");
        pubKeys.add("03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");

        dto.setPubKeys(pubKeys);
        dto.setMinSigns(2);
        dto.setAgentHash("e67ed0f09cea8bd4e2ad3b4b6d83a39841f9f83dd2a9e5737b73b4d5ad203537");
        dto.setAgentAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        dto.setDeposit(new BigInteger("2000000000000"));
        dto.setPrice(new BigInteger("100000"));
        List<StopDepositDto> list = new ArrayList<>();

        StopDepositDto depositDto1 = new StopDepositDto();
        depositDto1.setDepositHash("d4a9404a823ea533d1c7fba34470970ac499a974f35172bb8a717b0d6c4d4cbe");
        CoinFromDto fromDto1 = new CoinFromDto();
        fromDto1.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto1.setAssetChainId(SDKContext.main_chain_id);
        fromDto1.setAssetId(SDKContext.main_asset_id);
        fromDto1.setAmount(new BigInteger("200000000000"));
        depositDto1.setInput(fromDto1);
        list.add(depositDto1);

        StopDepositDto depositDto2 = new StopDepositDto();
        depositDto2.setDepositHash("7a735d44d9551a06c3d0bf8107c0ff75ba6921a78d932fc37d81947431ba90ad");
        CoinFromDto fromDto2 = new CoinFromDto();
        fromDto2.setAddress("tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy");
        fromDto2.setAssetChainId(SDKContext.main_chain_id);
        fromDto2.setAssetId(SDKContext.main_asset_id);
        fromDto2.setAmount(new BigInteger("200000000000"));
        depositDto2.setInput(fromDto2);
        list.add(depositDto2);

        dto.setDepositList(list);
        Result result = NulsSDKTool.createMultiSignStopConsensusTx(dto);
        System.out.println(result.getData());

        //txHex：090081fd165d0020584ae3c9af9a42c4e68fcde0736fce670a913262346ed10f827dfaef75714ebdfd5c01031764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000204aa9d101000000000000000000000000000000000000000000000000000008827dfaef75714ebdff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e000000000000000000000000000000000000000000000000000000086db83fdd14f6f233ff1764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e0000000000000000000000000000000000000000000000000000000863b6e201aa9af5f0ff021764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100609948a9d1010000000000000000000000000000000000000000000000000000990b175d000000001764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000a0db215d000000000000000000000000000000000000000000000000000000000000000000000000
        //txHash：d86502a23ab0e18ab78a43fce3fc302dc70d93467bc52922eb96ecf00d630f58
    }

    @Test
    public void testTx() {
        String txHex = "0b00c325c15d00e903626e620131055454424e428af8e801010300000003001747ef013e4954857e6fd078df53d73c70d029b5d3082f401747ef0169fdf4bd90d5e9f20bcba8cd77f05a6dae78fbb61747ef01728e8f8c34396600f424c6a371f9c9a136fd84cb4200c80047ef0100036e626e03626e6200205fa01200000000000000000000000000000000000000000000000000000000c817a8040000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000050017020001f7ec6473df12e751d64cf20a8baa7edd50810f81fd15010117020001f7ec6473df12e751d64cf20a8baa7edd50810f8102000100a03e66d94500000000000000000000000000000000000000000000000000000008000000000000000000031702000199092280b81a34b28901654601bbaa764ea0b385020001000040be4025000000000000000000000000000000000000000000000000000000000000000000000017020001f7ec6473df12e751d64cf20a8baa7edd50810f810200010000205fa012000000000000000000000000000000000000000000000000000000ffffffffffffffffff1702000129cfc6376255a78451eeb4b129ed8eacffa2feef02000100005847f80d0000000000000000000000000000000000000000000000000000000000000000000000692103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e3463044022038ea5240e0ac9a3aa8e3f682c8f3f2d4eab8983f150ab9d46377ef257bb26d9c02200a3b0ac2195990b4dd3e48c7fc8eb830835ada750ab79332a56edb12a386b943";

        try {
            Result result = NulsSDKTool.deserializeTxHex(txHex);
            Transaction tx = (Transaction) result.getData();
            tx.getCoinDataInstance();
            NulsSDKTool.validateTx(txHex);
//            CoinData coinData = tx.getCoinDataInstance();
//            for (CoinFrom from : coinData.getFrom()) {
//                String fromAddress = AddressTool.getStringAddressByBytes(from.getAddress());
//                System.out.println(fromAddress);
//                System.out.println(from.getAmount().toString());
//            }
//            for (CoinTo to : coinData.getTo()) {
//                String toAddress = AddressTool.getStringAddressByBytes(to.getAddress());
//                System.out.println(toAddress);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
