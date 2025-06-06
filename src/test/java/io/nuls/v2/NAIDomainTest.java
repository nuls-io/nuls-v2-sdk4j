package io.nuls.v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.io.IoUtils;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NAIPumpTest;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.txdata.CallContractData;
import io.nuls.v2.util.JsonRpcUtil;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

public class NAIDomainTest {

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
    String betaUrl = "https://beta.api.nuls.io/";
    String devUrl = "http://localhost:18004/";
    String agentHash;
    List<String> agentList = new ArrayList<>();
    String treasury = "tNULSeBaMfrNoRmZMENXbF9qtCiBUbNq4NxLhp";
    String official = "tNULSeBaMuU6sq72mptyghDXDWQXKJ5QUaWhGj";

    private String getMainUrl() {
        this.sender01 = new Sender("NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy",
                "");
        treasury = "NULSd6HgW68AdhE1btLfKKax8a5gr7i6T6Dfe";
        official = "NULSd6Hh6mPcXSdHpqn59HpszPBTyWdLn93kq";
        return mainUrl;
    }

    private String getTestUrl() {
        agentHash = "73583f9795fb8f026f4a25d0a14d2abd72ce33581672ce1759c4c45a453ad992";
        agentList.add(agentHash);
        return betaUrl;
    }

    private String getDevUrl() {
        agentHash = "a3422eac43e226a97ea8129988c99554ebf3088c3b8264ee1f487160f1bce225";
        return devUrl;
    }

    @Before
    public void before() {
        NulsSDKBootStrap.initMain(getMainUrl());
        //NulsSDKBootStrap.initTest(getTestUrl());
        //NulsSDKBootStrap.initTest(getDevUrl());
    }

    public void test() throws NulsException {
        List<String> list = new ArrayList<>();
        list.add("NULSd6HgXvnCdcuFt68WrJQh8zcDn5n6dabkB,8");
        list.add("NULSd6Hged67Lox63FWoQjbmDF72LtdLQzxMU,1");
        list.add("NULSd6Hga8u3LPFzKiUgpnuJZnpga7TiDWDpm,4");
        list.add("NULSd6HgUNFX5Dndc4mu5wLVGJU3Jeb8yuSPJ,3");
        list.add("NULSd6HgWQmksNqLFzhsLbxrTxdHbAvH4S1my,2");
        list.add("NULSd6HgUT23HVNd28ZstnqyUfUWqd8y1fZV2,1");
        list.add("NULSd6HgaPPmbWvRccp7hrab4KsxKKJoCBFXE,1");
        list.add("NULSd6HgbRwvvUhQJQPb7YHZSpwtCusYF33gU,2");
        list.add("NULSd6HgZcKkckbDDD6YyYF5W1MRFuMosQgvT,2");
        list.add("NULSd6HgUKwgLUEJFUzXQDmSnW6tJxVZdBcgL,1");
        list.add("NULSd6HgVCNN7JahWMLfDiY8PFHVtT8smdypN,1");
        list.add("NULSd6HghDY783qwq6ZKUj7aq5e4PCroVLSen,3");
        list.add("NULSd6HgW6jsHZ6WZLFPnmhNFw4uEoKXno5k5,1");
        list.add("NULSd6HgjWBddzqExdREHp4Z3TUXGhYZap6dP,4");
        list.add("NULSd6HgdHwezE9YKM3Xgr5jgm16ebytNJLhW,1");
        list.add("NULSd6HgUsyU7pDoRhXtjG1MnhVgFUjdGdHEP,1");
        list.add("NULSd6HgahX9oEToj5v2FoZT4tVusPgpFyZFw,2");
        list.add("NULSd6HgTwme2QGaSuJM75XpzfHm5ju33ZCuc,3");
        list.add("NULSd6HgXy9P7ziXA78WGPvjt9jYqYJVEQp7E,1");
        list.add("NULSd6HggXi8R715yb6aJGcvy6SLNG9akAa7s,1");
        list.add("NULSd6HgiuBWfeZeNss5r2zBoLv82artDeXZW,1");
        list.add("NULSd6HgjYtVQt6qjhG7macBtPhouorq38Nnq,1");
        list.add("NULSd6HgU3xRibiTtvaRdsyoa5MgzFAwXWGhb,1");
        list.add("NULSd6HgfViyNVPUC6wSdeZArj9YgxFhe2SoS,1");
        list.add("NULSd6Hgfnd9VmTddUCFV68tacDPgJbBRgWtH,1");
        list.add("NULSd6HgdU7sFgpcFk4YFU2oVU7Cdz7nnPf6N,1");
        list.add("NULSd6Hgcn1RwiiP1ihtjYkpT4hNxZU6NNKza,2");
        list.add("NULSd6Hgg4Aqw3acrFA7K9KaFMtEUVKr6iagy,6");
        list.add("NULSd6HgbRHdF6K1aRXnL5yFZP8LrDdXtQF4s,7");
        list.add("NULSd6HgVfveU6Bquy29yFESncpTPd3PKMAE2,5");
        list.add("NULSd6HgcuTEnPjWUj1nBduiB9YS8v7VQfviF,1");
        list.add("NULSd6HgYWqACb8aPKYFqZcXJHp9aHLLBNYLj,1");
        list.add("NULSd6HgWLiCdtZKzGTKsB9BXyNQjUiukUTjg,1");
        list.add("NULSd6HgZ1dVmTE1XcaTfVkMaWFDqYsJzXCHu,1");
        list.add("NULSd6HgUnGJYENXVJSwEyd27zby4ABdLEbpr,2");
        list.add("NULSd6HgWHci1XHnokbQYZEXoa1A1DiNaju5e,2");
        list.add("NULSd6HgY95LVGb337ZmTmw68tT1d7VZJhSQP,3");
        list.add("NULSd6HgUy9c4PGFFXuWjsARXw88Yf53B9t3x,1");
        list.add("NULSd6HgZAgfNmQDs46CcSK7A1c36bHDy3GY5,15");
        list.add("NULSd6Hghs6zVk1oV3L6cjNmYLSxx4dmr4XYx,2");
        list.add("NULSd6HgbfDnU6Dcb5bVxpF9RMEWadQSsXF7K,1");
        list.add("NULSd6HgVXrqyh6d9yY9wHU99ttRUN91UYU4B,2");
        list.add("NULSd6HgYpMkpQwYBSfbxr7514NKsDWzjjPQt,1");
        list.add("NULSd6HgghrMdryvBgphBmP9MFTvTL2dgTW9D,1");
        list.add("NULSd6HgXSm2jLgBfhK4X8em6BH7seZ4ngu4s,1");
        list.add("NULSd6HgYq4VGpYSGrSbNscKdryS53umDkwH8,2");
        list.add("NULSd6HgeCbJ6e5GzEu4H9Hg5yLKzTuaeyWpL,1");
        list.add("NULSd6HgVfPr3Ze1aePJP9xzid5gcH1HnEZAH,1");
        list.add("NULSd6HgbAagRrAvySG2v9RGNFrKB239XUd9y,1");
        list.add("NULSd6HgZx96mu7ih1iu9qRe7j8L9xfEqNryb,6");
        list.add("NULSd6HggQaQrsnK4eHyBdqae9az99sUF6cTu,17");
        list.add("NULSd6HgYVQJSTbVbkrE3vAmrWxpMY3UgXGKv,2");
        list.add("NULSd6HgYo7CwwuC38BU3MVKmQfFTk3RfK4Bw,1");
        list.add("NULSd6Hgd6sUPV4J21yLgNhcnUHco34oUB9m9,2");
        list.add("NULSd6HgWUAK9bgahFEaGNuqD1wo7kt7FWknx,1");
        list.add("NULSd6Hga6BPQDMwEfoy2nge8y3PXxw2uCTe1,10000");
        String[] tos = new String[list.size()];
        int[] quota = new int[list.size()];
        int i = 0;
        for (String str : list) {
            String[] split = str.split(",");
            tos[i] = split[0].trim();
            quota[i++] = Integer.parseInt(split[1].trim());
        }
    }


    /**
     * create domain only
     */
    @Test
    public void testDomainOnlyCreateTxOffline() throws Exception {
        String domainPath = "/Users/pierreluo/IdeaProjects/NulsDomainContract/NulsDomain/target/NulsDomain-1.0-SNAPSHOT.jar";
        String domain = this._createTxOffline("domain", HexUtil.encode(IoUtils.readBytes(new File(domainPath))), new Object[]{}, new String[]{}, "domain");
        String token721 = "tNULSeBaMzrct862toMudjDY1VnnzrMQvWJLjm";
        String staking = "tNULSeBaNB5sGm88gDU8FBXfECvAhSdHvRUR47";
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, domain, "initialize", "", new Object[]{staking, treasury, official, token721}, new String[]{"Address", "Address", "Address", "Address"}, "init");
    }

    /**
     * create 721 only
     */
    @Test
    public void test721OnlyCreateTxOffline() throws Exception {
        String name = "NABOX";
        String symbol = name.toUpperCase();
        String token721Path = "/Users/pierreluo/IdeaProjects/NulsDomainContract/NulsDomainNRC721/target/NulsDomainNRC721-1.0-SNAPSHOT.jar";
        String token721 = this._createTxOffline("token721", HexUtil.encode(IoUtils.readBytes(new File(token721Path))), new Object[]{name, symbol}, new String[]{"String", "String"}, "token721");
        String domain = "NULSd6HgsFcPKAwaKWaiTTbWDeL27x8JT48HZ";
        TimeUnit.SECONDS.sleep(10);
        this._callTxOffline(BigInteger.ZERO, token721, "initialize", "", new Object[]{official, domain}, new String[]{"Address", "Address"}, "init");
    }

    /**
     * create domain + 721 + staking
     */
    @Test
    public void testDomainCreateTxOffline() throws Exception {
        String treasury = "NULSd6HgW68AdhE1btLfKKax8a5gr7i6T6Dfe";
        //String official = "NULSd6Hh6mPcXSdHpqn59HpszPBTyWdLn93kq";
        String official = "NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy";
        List<String> agentList = new ArrayList<>();
        agentList.add("0a8d1ddf603156f31d47a32361e0eebb5b45f5a70b12676e5715e98555426a70");//niels
        agentList.add("fcf2d9f62ba58e31439402ae8abd16d853ea4946d6b0445654d33b0be0a705fe");//zuozuo2
        agentList.add("e3e98fd2691a70da3ee618d0e237f36311c38842c256584bd26bf667700f978b");//berzeck
        agentList.add("b63cfb380bda6043469d15e80ea8eaef4e52e00518defd777d781f7430a5baf8");//mac
        agentList.add("8e96b85afac544ce81c71da131d81257856220fbfe2092c95951125dbc195a2c");//opportunity
        agentList.add("1e4b79235a0c7d9c788bdd6f865f2009b1ff717b15c56325f1daadbab2283c52");//atrocity
        agentList.add("17e4ac76660ffd4f83d10010347250d4f0d968c1d6416e7778e0f4db1946a323");//sixfeetunder

        String domainPath = "/Users/pierreluo/IdeaProjects/NulsDomainContract/NulsDomain/target/NulsDomain-1.0-SNAPSHOT.jar";
        String token721Path = "/Users/pierreluo/IdeaProjects/NulsDomainContract/NulsDomainNRC721/target/NulsDomainNRC721-1.0-SNAPSHOT.jar";
        String stakingPath = "/Users/pierreluo/IdeaProjects/NulsDomainContract/NulsDomainStaking/target/NulsDomainStaking-1.0-SNAPSHOT.jar";

        String domain = this._createTxOffline("domain", HexUtil.encode(IoUtils.readBytes(new File(domainPath))), new Object[]{}, new String[]{}, "domain");
        TimeUnit.SECONDS.sleep(10);
        String token721 = this._createTxOffline("token721", HexUtil.encode(IoUtils.readBytes(new File(token721Path))), new Object[]{"AI", "AI"}, new String[]{"String", "String"}, "token721");
        TimeUnit.SECONDS.sleep(10);
        String staking = this._createTxOffline("staking", HexUtil.encode(IoUtils.readBytes(new File(stakingPath))), new Object[]{}, new String[]{}, "staking");
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, domain, "initialize", "", new Object[]{staking, treasury, official, token721}, new String[]{"Address", "Address", "Address", "Address"}, "init");
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, token721, "initialize", "", new Object[]{official, domain}, new String[]{"Address", "Address"}, "init");
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, staking, "initialize", "", new Object[]{treasury, official, domain}, new String[]{"Address", "Address", "Address"}, "init");

        for (String hash : agentList) {
            TimeUnit.SECONDS.sleep(15);
            this._callTxOffline(BigInteger.ZERO, staking, "addOtherAgent", "", new Object[]{hash}, new String[]{"String"}, "init");
        }
    }

    /**
     * initialize domain + 721 + staking
     */
    @Test
    public void testDomainInitializeTxOffline() throws Exception {
        String domain = "tNULSeBaNALRMVLfsZKLjznKWoXu7BGL9asGGR";
        String token721 = "tNULSeBaNA2JWAhpnvRrEq5JFBovC4pcyh164f";
        String staking = "tNULSeBaNAW5ZgSTTG83ThdEzmHRdo1FB5bzSc";
        this._callTxOffline(BigInteger.ZERO, domain, "initialize", "", new Object[]{staking, treasury, official, token721}, new String[]{"Address", "Address", "Address", "Address"}, "init");
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, token721, "initialize", "", new Object[]{official, domain}, new String[]{"Address", "Address"}, "init");
        TimeUnit.SECONDS.sleep(15);
        this._callTxOffline(BigInteger.ZERO, staking, "initialize", "", new Object[]{treasury, official, domain}, new String[]{"Address", "Address", "Address"}, "init");
        TimeUnit.SECONDS.sleep(10);
        this._callTxOffline(BigInteger.ZERO, staking, "addOtherAgent", "", new Object[]{agentHash}, new String[]{"String"}, "init");
    }

    private void _callTxOffline(BigInteger value, String contractAddress,
                                String methodName, String methodDesc, Object[] args, String[] argsType, String remark) throws Exception {
        int chainId = SDKContext.main_chain_id;
        Sender _sender = this.sender01;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();
        String signedTxHex = callOfflineHexWithoutValidation(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, argsType, remark);

        //System.out.println(String.format("signed TxHex: %s", signedTxHex));
        // 在线接口 - 广播交易
        Result<Map> broadcastTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcastTxR), broadcastTxR.isSuccess());
    }

    private String _createTxOffline(String alias, String contractCode, Object[] args, String[] argsType, String remark) throws JsonProcessingException {
        return this._createTxOffline(alias, contractCode, args, argsType, remark, null);
    }

    private String _createTxOffline(String alias, String contractCode, Object[] args, String[] argsType, String remark, String contractAddress) throws JsonProcessingException {
        Sender _sender = this.sender01;
        String sender = _sender.getSender();
        String priKey = _sender.getPriKey();

        // 在线接口(可跳过) - 验证发布合约的合法性，可不验证
        if (false) {
            ContractValidateCreateForm vForm = new ContractValidateCreateForm();
            vForm.setSender(sender);
            vForm.setContractCode(contractCode);
            vForm.setArgs(args);
            vForm.setGasLimit(MAX_GASLIMIT);
            vForm.setPrice(CONTRACT_MINIMUM_PRICE);
            Result vResult = NulsSDKTool.validateContractCreate(vForm);
            Assert.assertTrue(vResult.toString(), vResult.isSuccess());
            Map vMap = (Map) vResult.getData();
            boolean success = (boolean) vMap.get("success");
            Assert.assertTrue((String) vMap.get("msg"), success);
        }

        // 在线接口(可跳过) - 估算发布合约需要的GAS，可不估算，离线写一个合理的值
        Long gasLimit = 500000l;
        if (false) {
            ImputedGasContractCreateForm iForm = new ImputedGasContractCreateForm();
            iForm.setSender(sender);
            iForm.setContractCode(contractCode);
            iForm.setArgs(args);
            Result iResult = NulsSDKTool.imputedContractCreateGas(iForm);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(iResult), iResult.isSuccess());
            Map result = (Map) iResult.getData();
            gasLimit = Long.valueOf(result.get("gasLimit").toString());
        }

        // 在线接口(可跳过) - 获取代码的构造函数，生成参数类型的数组，若已知类型，自行编写类型数组，可不调用此接口
        if (argsType == null) {
            Result<ContractConstructorInfoDto> constructorR = NulsSDKTool.getConstructor(contractCode);
            Assert.assertTrue(JSONUtils.obj2PrettyJson(constructorR), constructorR.isSuccess());
            ContractConstructorInfoDto dto = constructorR.getData();
            argsType = dto.getConstructor().argsType2Array();
        }

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(sender, SDKContext.main_chain_id, SDKContext.main_asset_id);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(accountBalanceR), accountBalanceR.isSuccess());
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        // 离线接口 - 组装发布合约的离线交易
        Result<Map> txOfflineR = NulsSDKTool.createContractTxOffline(sender, senderBalance, nonce, alias, contractCode, gasLimit, args, argsType, remark, contractAddress);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(txOfflineR), txOfflineR.isSuccess());
        Map map = txOfflineR.getData();
        String txHex = (String) map.get("txHex");
        String hash = (String) map.get("hash");
        contractAddress = (String) map.get("contractAddress");

        //System.out.println(String.format("hash: %s, contractAddress: %s, txHex no signature: %s", hash, contractAddress, txHex));

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
        System.out.println(String.format("hash: %s, alias: %s, contractAddress: %s", hash, alias, contractAddress));
        return contractAddress;
    }


    private String callOfflineHexWithoutValidation(int chainId, String sender, String priKey, BigInteger value, String contractAddress,
                                                   String methodName, String methodDesc, Object[] args, String[] argsType, String remark) throws JsonProcessingException {

        Long gasLimit = Long.valueOf(500000L);

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
        //System.out.println(String.format("hash: %s, txHex: %s", _hash, signedTxHex));
        System.out.println(String.format("Call %s [%s], hash: %s", contractAddress, methodName, _hash));
        return signedTxHex;
    }
/*

    NULSd6HgcCBi9f3aGddzRwYcdd56fUVWS4dh9,{"mainDomain":"zz888888.ai","uri":null,"pub":"023907eccd72eb2e21808b9c8f58f643bd53f59fbe9e4548a9ee99534889409a13","activeDomains":["zz888888.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgikFqoshpWFGcFqeKUZGqRpBmjz5XC,{"mainDomain":"Niels.ai","uri":null,"pub":"0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569","activeDomains":["Niels.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HghVxPuCQ8cm6xX8r814UXJWRi4887k,{"mainDomain":"887k.ai","uri":null,"pub":"023788f6b82b1fb910f58140bba5cc6d064055855d58cab64a01102ba78e248745","activeDomains":["887k.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgV3H5FecBPdXiY7r9d9cQa79KhaezY,{"mainDomain":"pierre.ai","uri":null,"pub":"039ec7b0a2268210969f30e3dfefb2aceb6eef5213e75d33e998ddacce196f8931","activeDomains":["pierre.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgcRrm8MG1PyvZojsK8HSLHZTKRdHVV,{"mainDomain":"GKim.ai","uri":null,"pub":"0261df759671b58380d7035983c307c3c7ce2d7159fa8a123f6cfcdf30f45d7e33","activeDomains":["GKim.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgXahwfXGb3srzaWvVkj4SN9tgaEzKa,{"mainDomain":"peter.ai","uri":null,"pub":"039850150bb6869f6967afb7fdfd85add84273e6d883ee94143e003d608a80a090","activeDomains":["peter.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgbHth9KYCGnwz4mXS8ikc85QZEhyvh,{"mainDomain":"screw.ai","uri":null,"pub":"0219ff668ccd4d6c43e196559da3ed947cdff0369288a4d09bb1c50c99614f423f","activeDomains":["screw.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgiyrHCBd7t5mxqxY4cde9TZd2AesXy,{"mainDomain":"BZDGuanZiDMW.ai","uri":null,"pub":"02cb2706ba864c42852d29d44a8c97a7047f81b3f485a7a16b33b75d38a6c8d66b","activeDomains":["WRDXiaoPing18.ai","BZDGuanZiDMW.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgcPBsZ31FqhTApv3XspDbiBSx47NAY,{"mainDomain":"DOGCat.ai","uri":null,"pub":"036a35dcd3489355eb440566477819cdc0e0f50249a0f0335ba11a906f8a20a8d6","activeDomains":["DOGCat.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgW9PW5dcTJ4q5wuFQcBJ31JaiC9tpk,{"mainDomain":"Loki.ai","uri":null,"pub":"030320dc80d26e8e9ef82ff26d1b5172a8c232ed9024385d9a0740ae9c58434a06","activeDomains":["Loki.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgjUtXPe9RNWLc3fYPXCrmsrSBgDevv,{"mainDomain":"Fly_DiDiDi.ai","uri":null,"pub":"03a55bd21620a2787b5df904c44e503f20143f58bf975b35d40ecf497320c642dd","activeDomains":["Fly_DiDiDi.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgV742A3royY8HvZXSpo5wFo6xTi4EJ,{"mainDomain":"AaronXy.ai","uri":null,"pub":"021c65211650cd7ff3dc75f4966a42a570d21f02171e82d77dc8a1b2006c7e2871","activeDomains":["AaronXy.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgVfveU6Bquy29yFESncpTPd3PKMAE2,{"mainDomain":"reaper.ai","uri":null,"pub":"03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4","activeDomains":["aaaa.ai","xxxx.ai","dddd.ai","bbbb.ai","oooo.ai","cccc.ai","candy.ai"],"inactiveDomains":["solana.ai","hello.ai","reaper.ai","bitcoin.ai","stake.ai"],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgUozwcD3Ej7FukcLx7aFk1iGS3abzZ,{"mainDomain":"Fenny.ai","uri":null,"pub":"03ce178e2daab80277f5ebc6c31e064df280b3e08e28d7d33d08ab4f9fc0ad24fc","activeDomains":["Fenny.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6Hga6BPQDMwEfoy2nge8y3PXxw2uCTe1,{"mainDomain":"nuls.ai","uri":null,"pub":"02b5682417cbd1234a05f9c2feaad3245eb7690b8351ce89acf9736d9e5a128305","activeDomains":["nuls.ai"],"inactiveDomains":["nerve.ai","nabox.ai"],"received":"0","pending":"0","rewardDebt":"0","historyQuota":9998}
    NULSd6HgUjESdre3UUrNwXXFGTxiKaW7JjTPz,{"mainDomain":"Nulstar.ai","uri":null,"pub":"03c4f3982213a23e1177ff8a242f51d7ca57a40bdb4e4869a37efab5e2b112e2bf","activeDomains":["nulstar.ai","berzeck.ai","Nulstar.ai","Berzeck.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}
    NULSd6HgfWYYyg5eAthzTzvgau8wpW4GyNzSL,{"mainDomain":"NULS.ai","uri":"bafkreie5dhed7ly6jiqjnvtvsbskf2urhrn7soyuxhgo5z2ziw7j7ttdoq","pub":"038bb48c9c620c674672e0498172ae594a5e46099babf4ac077c4af536c022c8d3","activeDomains":["NULS.ai"],"inactiveDomains":[],"received":"0","pending":"0","rewardDebt":"0","historyQuota":0}                                                                                                                           NULSd6HgUjESdre3UUrNwXXFGTxiKaW7JjTPz,400
                                                                                                                                                NULSd6HgfWYYyg5eAthzTzvgau8wpW4GyNzSL,120
*/

    @Test
    public void refund() throws Exception {
        List<String[]> list = this.refundData();
        int total = 0;
        for (String[] s : list) {
            total += Integer.parseInt(s[1]);
        }
        System.out.println(total);
    }

    private List<String[]> refundData() throws Exception {
        List<String[]> result = new ArrayList<>();
        List<String> list = new ArrayList<>();
        list.add("NULSd6HgcCBi9f3aGddzRwYcdd56fUVWS4dh9,100");
        list.add("NULSd6HgikFqoshpWFGcFqeKUZGqRpBmjz5XC,110");
        list.add("NULSd6HghVxPuCQ8cm6xX8r814UXJWRi4887k,120");
        list.add("NULSd6HgV3H5FecBPdXiY7r9d9cQa79KhaezY,100");
        list.add("NULSd6HgcRrm8MG1PyvZojsK8HSLHZTKRdHVV,120");
        list.add("NULSd6HgXahwfXGb3srzaWvVkj4SN9tgaEzKa,110");
        list.add("NULSd6HgbHth9KYCGnwz4mXS8ikc85QZEhyvh,110");
        list.add("NULSd6HgiyrHCBd7t5mxqxY4cde9TZd2AesXy,200");
        list.add("NULSd6HgcPBsZ31FqhTApv3XspDbiBSx47NAY,100");
        list.add("NULSd6HgW9PW5dcTJ4q5wuFQcBJ31JaiC9tpk,120");
        list.add("NULSd6HgjUtXPe9RNWLc3fYPXCrmsrSBgDevv,100");
        list.add("NULSd6HgV742A3royY8HvZXSpo5wFo6xTi4EJ,100");
        list.add("NULSd6HgVfveU6Bquy29yFESncpTPd3PKMAE2,830");
        list.add("NULSd6HgUozwcD3Ej7FukcLx7aFk1iGS3abzZ,110");
        list.add("NULSd6Hga6BPQDMwEfoy2nge8y3PXxw2uCTe1,120");
        list.add("NULSd6HgUjESdre3UUrNwXXFGTxiKaW7JjTPz,400");
        list.add("NULSd6HgfWYYyg5eAthzTzvgau8wpW4GyNzSL,120");
        for (String s : list) {
            String[] split = s.split(",");
            result.add(split);
        }
        return result;
    }

    @Test
    public void transferAccounts() throws Exception {
        String fromAddress = "NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy";

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(17);
        BigInteger fee = NulsSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        List<CoinToDto> outputs = new ArrayList<>();

        BigInteger total = BigInteger.ZERO;
        List<String[]> addrList = this.refundData();
        for (String[] addr : addrList) {
            CoinToDto to = new CoinToDto();
            to.setAddress(addr[0]);
            to.setAmount(new BigDecimal(addr[1]).movePointRight(8).toBigInteger());
            to.setAssetChainId(SDKContext.main_chain_id);
            to.setAssetId(SDKContext.main_asset_id);
            outputs.add(to);
            total = total.add(to.getAmount());
        }

        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(total.add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("e0e07b9553ac3599");
        inputs.add(from);


        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result<Map> result = NulsSDKTool.createTransferTxOffline(transferDto);
        String txHex = (String) result.getData().get("txHex");
        System.out.println("txHex: " + txHex);

        //签名
        String prikey = "???";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");

        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }
    Sender senderMain = new Sender("NULSd6HgZ8xEbCKo9J5MwgJYVy9F3Cpzvh2GY", "???");
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
}