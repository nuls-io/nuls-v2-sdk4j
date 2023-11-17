package io.nuls.v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.util.ContractUtil;
import io.nuls.v2.util.NulsSDKTool;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

public class PocmHtgCrossTest {

    private static class Sender {
        private String sender;
        private String priKey;

        public Sender(String sender, String priKey) {
            this.sender = sender;
            this.priKey = priKey;
        }

        public String getSender() {
            return sender;
        }

        public String getPriKey() {
            return priKey;
        }
    }

    String mainUrl = "https://api.nuls.io/";
    String betaUrl = "http://beta.api.nuls.io/";
    String devUrl = "http://localhost:18004/";

    private String getMainUrl() {
        return mainUrl;
    }

    private String getTestUrl() {
        return betaUrl;
    }

    String token = "5-160";
    String oldPocm = "";
    String newPocm = "tNULSeBaN2AKdCFEiLEoSsiPrYxQZnDLJ2CWrD";

    @Before
    public void before() {
        //NulsSDKBootStrap.initMain(getMainUrl());
        NulsSDKBootStrap.initTest(getTestUrl());
    }

    @Test
    public void createPocmCross() throws Exception {
        Sender _sender = this.sender01;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        String alias = "htgcrosspocm";
        InputStream in = new FileInputStream("/Users/pierreluo/IdeaProjects/pocmContract-new/target/pocmContract-new-1.0-SNAPSHOT.jar");
        String contractCode = HexUtil.encode(IOUtils.toByteArray(in));
        String[] tokenInfo = token.split("-");
        Object[] args = new Object[]{
                null,
                tokenInfo[0],
                tokenInfo[1],
                "2000000000000000000",
                "200000000000000000000000",
                "1",
                "500000000",
                "500000000000000",
                "1",
                "0",
                "83de7fee-16ec-4cd1-9762-3ca80b4e91d3",
                "1",
                "2000"
        };
        String remark = "";
        Long gasLimit = Long.valueOf(2000000);

        // 在线接口(可跳过) - 获取代码的构造函数，生成参数类型的数组，若已知类型，自行编写类型数组，可不调用此接口
        Result<ContractConstructorInfoDto> constructorR = NulsSDKTool.getConstructor(contractCode);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(constructorR), constructorR.isSuccess());
        ContractConstructorInfoDto dto = constructorR.getData();
        String[] argsType = dto.getConstructor().argsType2Array();

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(sender, 2, 1);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(accountBalanceR), accountBalanceR.isSuccess());
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        // 离线接口 - 组装发布合约的离线交易
        Result<Map> txOfflineR = NulsSDKTool.createContractTxOffline(sender, senderBalance, nonce, alias, contractCode, gasLimit, args, argsType, remark);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(txOfflineR), txOfflineR.isSuccess());
        Map map = txOfflineR.getData();
        String txHex = (String) map.get("txHex");
        String hash = (String) map.get("hash");
        String contractAddress = (String) map.get("contractAddress");

        System.out.println(String.format("hash: %s, contractAddress: %s, txHex no signature: %s", hash, contractAddress, txHex));

        // 离线接口 - 签名交易
        Result<Map> signTxR = NulsSDKTool.sign(txHex, sender, priKey);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(signTxR), signTxR.isSuccess());
        Map resultData = signTxR.getData();
        String _hash = (String) resultData.get("hash");
        Assert.assertEquals("hash不一致", hash, _hash);
        String signedTxHex = (String) resultData.get("txHex");

        // 在线接口 - 广播交易
        Result<Map> broadcastTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcastTxR), broadcastTxR.isSuccess());
        Map data = broadcastTxR.getData();
        String hash1 = (String) data.get("hash");
        Assert.assertEquals("hash不一致", hash, hash1);
        System.out.println(String.format("hash: %s, contractAddress: %s", hash, contractAddress));
    }

    @Test
    public void transferProjectCandyAsset() throws JsonProcessingException {
        int chainId = SDKContext.main_chain_id;
        Sender _sender = this.sender37;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        BigInteger value = BigInteger.ZERO;
        String contractAddress = oldPocm;
        String methodName = "transferProjectCandyAsset";
        String methodDesc = "";
        ContractViewCallForm form = new ContractViewCallForm();
        form.setContractAddress(contractAddress);
        form.setMethodName("calcUnAllocationTokenAmount");
        Result result = NulsSDKTool.invokeView(form);
        Map map = (Map) result.getData();
        Object[] args = new Object[]{sender38.sender, (String) map.get("result")};
        String remark = "";

        String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, remark);

        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
        Map data = broadcaseTxR.getData();
        String hash1 = (String) data.get("hash");
        System.out.println(String.format("hash: %s", hash1));
    }

    @Test
    public void transferCandyToPocm() throws JsonProcessingException {
        int chainId = SDKContext.main_chain_id;
        Sender _sender = this.sender38;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        BigInteger value = BigInteger.ZERO;
        String contractAddress = newPocm;
        String methodName = "_payableMultyAsset";
        String methodDesc = "";
        List<ProgramMultyAssetValue> multyAssetValueList = new ArrayList<>();
        String[] tokenInfo = token.split("-");
        int crossChainId = Integer.valueOf(tokenInfo[0]);
        int crossAssetId = Integer.valueOf(tokenInfo[1]);
        Result crossBalance = NulsSDKTool.getAccountBalance(sender, crossChainId, crossAssetId);
        Map map = (Map) crossBalance.getData();
        String crossNonce = map.get("nonce").toString();
        multyAssetValueList.add(new ProgramMultyAssetValue(new BigInteger("200000000000000000000000"), crossNonce, crossChainId, crossAssetId));
        Object[] args = new Object[]{};
        String remark = "";

        String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, remark, multyAssetValueList);

        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
        Map data = broadcaseTxR.getData();
        String hash1 = (String) data.get("hash");
        System.out.println(String.format("hash: %s", hash1));
    }

    @Test
    public void addAgent() throws JsonProcessingException {
        int chainId = SDKContext.main_chain_id;
        Sender _sender = this.sender01;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        BigInteger value = BigInteger.ZERO;
        String contractAddress = newPocm;
        String methodName = "addOtherAgent";
        String methodDesc = "";
        Object[] args = new Object[]{"014e8efcc48501b7bdeb785c7e6328be350fd85412eefb0852c51ffd3803e7b7"};
        String remark = "";

        String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, remark);

        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
        Map data = broadcaseTxR.getData();
        String hash1 = (String) data.get("hash");
        System.out.println(String.format("hash: %s", hash1));
    }

    @Test
    public void deposit() throws Exception {
        int chainId = SDKContext.main_chain_id;
        String contractAddress = newPocm;
        String methodName = "depositForOwn";
        String methodDesc = "";
        Object[] args = new Object[0];
        String remark = "";
        int[] users = new int[]{1,3,38,6,19};
        String[] amounts = new String[]{"25000", "25000", "25000", "15000", "10000"};

        for (int i = 0; i < users.length; i++) {
            Sender _sender = sender("getSender", users[i]);
            String sender = _sender.getSender();
            String priKey = _sender.getPriKey();
            BigInteger value = new BigDecimal(amounts[i]).movePointRight(8).toBigInteger();
            String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                    methodName, methodDesc, args, remark);

            Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
            Map data = broadcaseTxR.getData();
            String hash1 = (String) data.get("hash");
            System.out.println(String.format("hash: %s", hash1));
        }
    }

    @Test
    public void receive() throws Exception {
        int chainId = SDKContext.main_chain_id;
        String contractAddress = newPocm;
        String methodName = "receiveAwards";
        String methodDesc = "";
        Object[] args = new Object[]{"0"};
        String remark = "";
        int[] users = new int[]{1,3,38,6,19};

        for (int i = 0; i < users.length; i++) {
            Sender _sender = sender("getSender", users[i]);
            String sender = _sender.getSender();
            String priKey = _sender.getPriKey();
            BigInteger value = BigInteger.ZERO;
            String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                    methodName, methodDesc, args, remark);

            Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
            Map data = broadcaseTxR.getData();
            String hash1 = (String) data.get("hash");
            System.out.println(String.format("hash: %s", hash1));
        }
    }

    @Test
    public void transferConsensusRewardByOwner() throws JsonProcessingException {
        int chainId = SDKContext.main_chain_id;
        Sender _sender = this.sender01;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        BigInteger value = BigInteger.ZERO;
        String contractAddress = newPocm;
        String methodName = "transferConsensusRewardByOwner";
        String methodDesc = "";
        Object[] args = new Object[]{};
        String remark = "";

        String signedTxHex = callOfflineHex(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, remark);

        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
        Map data = broadcaseTxR.getData();
        String hash1 = (String) data.get("hash");
        System.out.println(String.format("hash: %s", hash1));
    }

    @Test
    public void withdraw() throws Exception {
        if (1 == 2) {
            throw new Exception("暂停执行提现");
        }
        int chainId = SDKContext.main_chain_id;
        String contractAddress = newPocm;
        String methodName = "withdraw";
        String methodDesc = "";
        String remark = "";
        int[] users = new int[]{1, 3,38,6,19};
        String[] amounts = new String[]{"25000", "25000", "25000", "15000", "10000"};

        for (int i = 0; i < users.length; i++) {
            Sender _sender = sender("getSender", users[i]);
            String sender = _sender.getSender();
            String priKey = _sender.getPriKey();
            BigInteger value = new BigDecimal(amounts[i]).movePointRight(8).toBigInteger();
            Object[] args = new Object[]{value.toString()};
            String signedTxHex = callOfflineHex(chainId, sender, priKey, BigInteger.ZERO, contractAddress,
                    methodName, methodDesc, args, remark);

            Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signedTxHex);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
            Map data = broadcaseTxR.getData();
            String hash1 = (String) data.get("hash");
            System.out.println(String.format("hash: %s", hash1));
        }
    }


    protected Sender sender(String methodBaseName, int i) throws Exception {
        String _i;
        if (i < 10) {
            _i = "0" + i;
        } else {
            _i = "" + i;
        }
        return (Sender) this.getClass().getMethod(methodBaseName + _i).invoke(this);
    }

    private String callOfflineHex(int chainId, String sender, String priKey, BigInteger value, String contractAddress,
                                  String methodName, String methodDesc, Object[] args, String remark) throws JsonProcessingException {
        return callOfflineHex(chainId, sender, priKey, value, contractAddress, methodName, methodDesc, args, remark, null);
    }

    private String callOfflineHex(int chainId, String sender, String priKey, BigInteger value, String contractAddress,
                                  String methodName, String methodDesc, Object[] args, String remark, List<ProgramMultyAssetValue> multyAssetValueList) throws JsonProcessingException {
        String[][] multyAssetValues = null;
        if (multyAssetValueList != null) {
            multyAssetValues = ContractUtil.multyAssetStringArray(multyAssetValueList);
        }
        // 在线接口(可跳过) - 验证调用合约的合法性，可不验证
        ContractValidateCallForm validateCallForm = new ContractValidateCallForm();
        validateCallForm.setSender(sender);
        validateCallForm.setValue(value.longValue());
        validateCallForm.setGasLimit(MAX_GASLIMIT);
        validateCallForm.setPrice(CONTRACT_MINIMUM_PRICE);
        validateCallForm.setContractAddress(contractAddress);
        validateCallForm.setMethodName(methodName);
        validateCallForm.setMethodDesc(methodDesc);
        validateCallForm.setArgs(args);
        validateCallForm.setMultyAssetValues(multyAssetValues);
        Result vResult = NulsSDKTool.validateContractCall(validateCallForm);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(vResult), vResult.isSuccess());
        Map map = (Map) vResult.getData();
        boolean success = (boolean) map.get("success");
        Assert.assertTrue((String) map.get("msg"), success);

        // 在线接口(可跳过) - 估算调用合约需要的GAS，可不估算，离线写一个合理的值
        ImputedGasContractCallForm iForm = new ImputedGasContractCallForm();
        iForm.setSender(sender);
        iForm.setValue(value);
        iForm.setContractAddress(contractAddress);
        iForm.setMethodName(methodName);
        iForm.setMethodDesc(methodDesc);
        iForm.setArgs(args);
        iForm.setMultyAssetValues(multyAssetValues);
        Result iResult = NulsSDKTool.imputedContractCallGas(iForm);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(iResult), iResult.isSuccess());
        Map result = (Map) iResult.getData();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;
        // 在线接口(可跳过) - 生成参数类型的数组，若已知类型，自行编写类型数组，可不调用此接口
        String[] argsType = null;
        if (args != null && args.length > 0) {
            ContractMethodForm cFrom = new ContractMethodForm();
            cFrom.setContractAddress(contractAddress);
            cFrom.setMethodName(methodName);
            cFrom.setMethodDesc(methodDesc);
            Result cResult = NulsSDKTool.getContractMethodArgsTypes(cFrom);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(cResult), cResult.isSuccess());
            List<String> list = (List<String>) cResult.getData();
            int size = list.size();
            argsType = new String[size];
            argsType = list.toArray(argsType);
        }

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(sender, 2, 1);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(accountBalanceR), accountBalanceR.isSuccess());
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        // 离线接口 - 组装调用合约的离线交易
        Result<Map> txOfflineR = NulsSDKTool.callContractTxOffline(sender, senderBalance, nonce, value, contractAddress, gasLimit, methodName, methodDesc, args, argsType, remark, multyAssetValueList);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(txOfflineR), txOfflineR.isSuccess());
        Map txMap = txOfflineR.getData();
        String txHex = (String) txMap.get("txHex");
        String hash = (String) txMap.get("hash");

        // 离线接口 - 签名交易
        Result<Map> signTxR = NulsSDKTool.sign(txHex, sender, priKey);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(signTxR), signTxR.isSuccess());
        Map resultData = signTxR.getData();
        String _hash = (String) resultData.get("hash");
        Assert.assertEquals("hash不一致", hash, _hash);
        String signedTxHex = (String) resultData.get("txHex");
        System.out.println(String.format("hash: %s, txHex: %s", _hash, signedTxHex));
        return signedTxHex;
    }

    private String callOfflineHexWithoutValidation(int chainId, String sender, String priKey, BigInteger value, String contractAddress,
                                                   String methodName, String methodDesc, Object[] args, String[] argsType, String remark) throws JsonProcessingException {

        Long gasLimit = Long.valueOf(50000L);

        int assetChainId = SDKContext.main_chain_id;
        int assetId = SDKContext.main_asset_id;

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(sender, SDKContext.main_chain_id, SDKContext.main_asset_id);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(accountBalanceR), accountBalanceR.isSuccess());
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        // 离线接口 - 组装调用合约的离线交易
        Result<Map> txOfflineR = NulsSDKTool.callContractTxOffline(sender, senderBalance, nonce, value, contractAddress, gasLimit, methodName, methodDesc, args, argsType, remark);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(txOfflineR), txOfflineR.isSuccess());
        Map txMap = txOfflineR.getData();
        String txHex = (String) txMap.get("txHex");
        String hash = (String) txMap.get("hash");

        // 离线接口 - 签名交易
        Result<Map> signTxR = NulsSDKTool.sign(txHex, sender, priKey);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(signTxR), signTxR.isSuccess());
        Map resultData = signTxR.getData();
        String _hash = (String) resultData.get("hash");
        Assert.assertEquals("hash不一致", hash, _hash);
        String signedTxHex = (String) resultData.get("txHex");
        System.out.println(String.format("hash: %s, txHex: %s", _hash, signedTxHex));
        return signedTxHex;
    }

    Sender senderMain = new Sender("NULSd6HgZ8xEbCKo9J5MwgJYVy9F3Cpzvh2GY", "598f2ab7adc660b26021c771644d3eb540e7849134a5ef96a9bdec3223c169f0");
    Sender sender01 = new Sender("tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG", "9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b");
    Sender sender02 = new Sender("tNULSeBaMnrs6JKrCy6TQdzYJZkMZJDng7QAsD", "477059f40708313626cccd26f276646e4466032cabceccbf571a7c46f954eb75");
    Sender sender03 = new Sender("tNULSeBaMrbMRiFAUeeAt6swb4xVBNyi81YL24", "8212e7ba23c8b52790c45b0514490356cd819db15d364cbe08659b5888339e78");
    Sender sender04 = new Sender("tNULSeBaMu38g1vnJsSZUCwTDU9GsE5TVNUtpD", "4100e2f88c3dba08e5000ed3e8da1ae4f1e0041b856c09d35a26fb399550f530");
    Sender sender05 = new Sender("tNULSeBaMp9wC9PcWEcfesY7YmWrPfeQzkN1xL", "bec819ef7d5beeb1593790254583e077e00f481982bce1a43ea2830a2dc4fdf7");
    Sender sender06 = new Sender("tNULSeBaMshNPEnuqiDhMdSA4iNs6LMgjY6tcL", "ddddb7cb859a467fbe05d5034735de9e62ad06db6557b64d7c139b6db856b200");
    Sender sender07 = new Sender("tNULSeBaMtkzQ1tH8JWBGZDCmRHCmySevE4frM", "979c0ceeba6062e46b8eaa0f8435951ce27859581a39d4d2e7c0eef1baac15d3");
    Sender sender08 = new Sender("tNULSeBaMhKaLzhQh1AhhecUqh15ZKw98peg29", "edacaeb4ae6836ead7dd61d8ab79444b631274a303f91608472c8f99d646bbdf");
    Sender sender09 = new Sender("tNULSeBaMv8q3pWzS7bHpQWW8yypNGo8auRoPf", "ab69dab113f27ecac4024536c8a72b35f1ad4c8c934e486f7c4edbb14d8b7f9e");
    Sender sender10 = new Sender("tNULSeBaMmbiCH5soCFasXnG4TwqknyTzYBM3S", "14e86ce16c0a21fe3960e18a86c4ed943d4cf434cb4dc0c761cf811b20486f43");
    Sender sender11 = new Sender("tNULSeBaMsUBLVxwoaswjWvghJyoUJfbfB6dja", "a17e3161cc2b2a5d8ac2777f4113f6147270cda9ec9ba2ca979c06839f264e39");
    Sender sender12 = new Sender("tNULSeBaMi5yGkDbDgKGGX8TGxYdDttZ4KhpMv", "2a1eff43919f52370682c527a5932ca43ea0d65ebd3b4686b5823c5087b33355");
    Sender sender13 = new Sender("tNULSeBaMqjttJV62GZ1iXVFDBudet3ey2aYSB", "425c9c6e9cf1c6dbb51fe22baf2487b273b0b3fe0f596f6e7b406cbb97775fd0");
    Sender sender14 = new Sender("tNULSeBaMgTcqskhNrE1ZSt3kZpdAv6B83npXE", "3ba402d5138ff7439fd0187f6359b1b1e1ec0529544dc05bf0072445b5196e2d");
    Sender sender15 = new Sender("tNULSeBaMjcximfy1JEGzjxodNMjrjydWuiffr", "1a4bb53eddab9d355c56c840097de6611497b53fc348f4abaa664ea5a5f8829c");
    Sender sender16 = new Sender("tNULSeBaMfMk3RGzotV3Dw788NFTP52ep7SMnJ", "2cca1c7f69f929680a00d45298dca7b705d87d34ae1dbbcb4125b5663552db36");
    Sender sender17 = new Sender("tNULSeBaMj8XfWDjyKHZ1ybC3ShR8qKGyVKRcb", "1c73a09db8b19b14921f8790ef015ac1ee6137cdb99f967a3f257b21f68bac1d");
    Sender sender18 = new Sender("tNULSeBaMqwycXLTWtjexSHHfa4jDTrVq9FMWE", "2d969f5dd4b68089fcb581f9d029fd3bb472c4858b88bcfec96f0575c1310eb5");
    Sender sender19 = new Sender("tNULSeBaMoixxbUovqmzPyJ2AwYFAX2evKbuy9", "d60fc83130dbe5537d4f1e1e35c533f1a396b8b7d641d717b2d1eb1245d0d796");
    Sender sender20 = new Sender("tNULSeBaMvaRhahBAYkZKQFhiSqcC67UiRzoSA", "d78bbdd20e0166d468d93c6a5bde7950c84427b7e1da307217f7e68583b137b5");
    Sender sender21 = new Sender("tNULSeBaMuk5jx12ZXhaf5HLgcAr3WCwUhRGfT", "63da888abcdfbb20931e88ec1f926e0624f57792e4c41dcde889bf6bbe01f49a");
    Sender sender22 = new Sender("tNULSeBaMqjT3y9bGz4gBeJ7FJujmxBDTGdNp1", "c34d3ec20f8134b53f3df9b61bbac0c50d6e368db3dbf0a0069b0206db409643");
    Sender sender23 = new Sender("tNULSeBaMobzkpUc1zYcT67wheRPLg7cmas5A6", "f13d4e1ff9f8e8311072b6cf8cff74f754f38675905bd22a51dd17461ab8946c");
    Sender sender24 = new Sender("tNULSeBaMjXxVzqB4T7zFoykRwfSZSD5ptAn4A", "bc6032137bf45ccc7a230cdef655a263a86a69b1d98f3b9567688872afa5af15");
    Sender sender25 = new Sender("tNULSeBaMpaiBiMHWfAeTzdXhnfJXPfwXwKikc", "0bf13b6653412e905b06001e4b57d95c113da9fe279db83076a88159b2828d23");
    Sender sender26 = new Sender("tNULSeBaMrL5netZkTo9FZb86xGSk47kq6TRBR", "a08afb9b85f54622503b06f26b8883a78a90892d2909071e4e1a3306e283992a");
    Sender sender27 = new Sender("tNULSeBaMk52mfhacRWkmB98PrwCVXuEzCdQuk", "a9d2e66e4bb78a71a99a0509c05689f394f2ccb77ca88a14670b5fda117d2de7");
    Sender sender28 = new Sender("tNULSeBaMiKWTid5Gj3FoqBFP7WomUzgumVeKc", "a99470957cc20287b66ff4a9deb70770a698ea1968e7ac8262532c9a644d01c1");
    Sender sender29 = new Sender("tNULSeBaMvGmZSrFyQHptSL9yBCNSDfhWoxEHF", "6f5a847f5bce1e7bae540daeb024fc05cab6b21c2eea2cdf2c8837e2844af4e2");
    Sender sender30 = new Sender("tNULSeBaMkzsRE6qc9RVoeY6gHq8k1xSMcdrc7", "f8ae46ce88cf33091eab6068fa39756d4aa2181c49192b3e1531ecb52dad1b1d");
    Sender sender31 = new Sender("tNULSeBaMfXDQeT4MJZim1RusCJRPx5j9bMKQN", "9da23738f9efe1e8271c7a43d45df6087a57782e276c7b4a8e19f7ced04b73c8");
    Sender sender32 = new Sender("tNULSeBaMj7QaB8mYBBvkhaT3jCrXEMCEcRfb1", "6fec251b4f3b2b48f98c2426587515fee2339a3f9f1e0011d816cd1f54d37be7");
    Sender sender33 = new Sender("tNULSeBaMtgmrSYu98QwP1Mv8G5FwaMDkWSkuy", "9f32c23400bcb08ae52c6195ba9167969ee36ac3219bb320e9c7bc3d49efb4ee");
    Sender sender34 = new Sender("tNULSeBaMh4VafNqp5TJSmV5ogdZviq1nbXBSu", "4ecbcf0768ea880c6001ad46838e5ece4fa5641424f4e0cce5ec412c11d5ae8f");
    Sender sender35 = new Sender("tNULSeBaMfCD8hK8inyEKDBZpuuBUjLdiKgwnG", "5633c9e3923773a5665c4e8cf5f8e80abb7085f9b30694656dfc1c9f3b7092d2");
    Sender sender36 = new Sender("tNULSeBaMvQr8dVnk3f3DPvwCYX3ctTRtrTurD", "b936b61041b6fc84943b46dc0bc8ed79c009fdeb607fce17113800b64a726f0c");
    Sender sender37 = new Sender("tNULSeBaMuU6sq72mptyghDXDWQXKJ5QUaWhGj", "8c6715620151478cdd4ee8c95b688f2c2112a21a266f060973fa776be3f0ebd7");
    Sender sender38 = new Sender("tNULSeBaMrQaVh1V7LLvbKa5QSN54bS4sdbXaF", "4594348E3482B751AA235B8E580EFEF69DB465B3A291C5662CEDA6459ED12E39");

    public Sender getSender01() {
        return sender01;
    }

    public Sender getSender02() {
        return sender02;
    }

    public Sender getSender03() {
        return sender03;
    }

    public Sender getSender04() {
        return sender04;
    }

    public Sender getSender05() {
        return sender05;
    }

    public Sender getSender06() {
        return sender06;
    }

    public Sender getSender07() {
        return sender07;
    }

    public Sender getSender08() {
        return sender08;
    }

    public Sender getSender09() {
        return sender09;
    }

    public Sender getSender10() {
        return sender10;
    }

    public Sender getSender11() {
        return sender11;
    }

    public Sender getSender12() {
        return sender12;
    }

    public Sender getSender13() {
        return sender13;
    }

    public Sender getSender14() {
        return sender14;
    }

    public Sender getSender15() {
        return sender15;
    }

    public Sender getSender16() {
        return sender16;
    }

    public Sender getSender17() {
        return sender17;
    }

    public Sender getSender18() {
        return sender18;
    }

    public Sender getSender19() {
        return sender19;
    }

    public Sender getSender20() {
        return sender20;
    }

    public Sender getSender21() {
        return sender21;
    }

    public Sender getSender22() {
        return sender22;
    }

    public Sender getSender23() {
        return sender23;
    }

    public Sender getSender24() {
        return sender24;
    }

    public Sender getSender25() {
        return sender25;
    }

    public Sender getSender26() {
        return sender26;
    }

    public Sender getSender27() {
        return sender27;
    }

    public Sender getSender28() {
        return sender28;
    }

    public Sender getSender29() {
        return sender29;
    }

    public Sender getSender30() {
        return sender30;
    }

    public Sender getSender31() {
        return sender31;
    }

    public Sender getSender32() {
        return sender32;
    }

    public Sender getSender33() {
        return sender33;
    }

    public Sender getSender34() {
        return sender34;
    }

    public Sender getSender35() {
        return sender35;
    }

    public Sender getSender36() {
        return sender36;
    }

    public Sender getSender37() {
        return sender37;
    }

    public Sender getSender38() {
        return sender38;
    }
}