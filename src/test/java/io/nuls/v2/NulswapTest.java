package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.io.IoUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.ContractConstructorInfoDto;
import io.nuls.v2.model.dto.ContractValidateCreateForm;
import io.nuls.v2.model.dto.ImputedGasContractCreateForm;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

public class NulswapTest {

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
    String treasury = "tNULSeBaMfrNoRmZMENXbF9qtCiBUbNq4NxLhp";
    String _feeToSetter = "tNULSeBaMuU6sq72mptyghDXDWQXKJ5QUaWhGj";
    String WNULS = "tNULSeBaN8aNHMo4yKomvGDbZfL1KAYGwfn8Jk";
    String WASSET_FACTORY = "tNULSeBaN3KSaGWJj7kntJiNs8qVq35fxpHwhq";

    private String getMainUrl() {
        return mainUrl;
    }

    private String getTestUrl() {
        return betaUrl;
    }

    private String getDevUrl() {
        return devUrl;
    }

    void test() {
        NulsSDKBootStrap.initTest(getTestUrl());
        treasury = "tNULSeBaMfrNoRmZMENXbF9qtCiBUbNq4NxLhp";
        _feeToSetter = "tNULSeBaMuU6sq72mptyghDXDWQXKJ5QUaWhGj";
        WNULS = "tNULSeBaN8aNHMo4yKomvGDbZfL1KAYGwfn8Jk";
        WASSET_FACTORY = "tNULSeBaN3KSaGWJj7kntJiNs8qVq35fxpHwhq";
    }

    void main() {
        NulsSDKBootStrap.initMain(getMainUrl());
        treasury = "NULSd6Hgghq8V5Hbkogh7N47b7JPJ7CNgy9BJ";
        _feeToSetter = "NULSd6Hh6mPcXSdHpqn59HpszPBTyWdLn93kq";
        WNULS = "NULSd6HgnjgEdarTNnBRGwhHaXU6MUXyHtLLi";
        WASSET_FACTORY = "NULSd6HgwmtH2s8Usgwgp97V2SQJggcxLEgHJ";
        this.sender01 = new Sender("NULSd6HghCubueCotsdb6GGvTVv9X8dHoTTGa",
                "???");
    }

    @Before
    public void before() {
        //test();
        main();
    }



    /**
     * create router
     */
    @Test
    public void testRouterCreateTxOffline() throws Exception {
        String routerPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapRouter/target/NulswapRouter-1.0-SNAPSHOT.jar";
        String factory = "NULSd6Hgp11fYE42kgoAudZCKh7JmxTTeDBvq";
        String router = this._createTxOffline("router", HexUtil.encode(IoUtils.readBytes(new File(routerPath))), new Object[]{WASSET_FACTORY, factory, WNULS, treasury, _feeToSetter}, new String[]{"Address", "Address", "Address", "Address", "Address"}, "router");
    }

    /**
     * create pairCopy + factory + router
     */
    @Test
    public void testMainAllCreateTxOffline() throws Exception {
        String pairPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapPair/target/NulswapPair-1.0-SNAPSHOT.jar";
        String factoryPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapFactory/target/NulswapFactory-1.0-SNAPSHOT.jar";
        String routerPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapRouter/target/NulswapRouter-1.0-SNAPSHOT.jar";
        String pair = this._createTxOffline("pair", HexUtil.encode(IoUtils.readBytes(new File(pairPath))), new Object[]{}, new String[]{}, "pair");
        TimeUnit.SECONDS.sleep(10);
        String factory = this._createTxOffline("factory", HexUtil.encode(IoUtils.readBytes(new File(factoryPath))), new Object[]{_feeToSetter, pair}, new String[]{"Address", "Address"}, "factory");
        TimeUnit.SECONDS.sleep(10);
        String router = this._createTxOffline("router", HexUtil.encode(IoUtils.readBytes(new File(routerPath))), new Object[]{WASSET_FACTORY, factory, WNULS, treasury, _feeToSetter}, new String[]{"Address", "Address", "Address", "Address", "Address"}, "router");
    }

    /**
     * create factory + router
     */
    @Test
    public void testMain2CreateTxOffline() throws Exception {
        String factoryPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapFactory/target/NulswapFactory-1.0-SNAPSHOT.jar";
        String routerPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapRouter/target/NulswapRouter-1.0-SNAPSHOT.jar";
        String pair = "tNULSeBaMzNuPvZLWjnWcnRhdvu9RYHTRLSGm1";
        String factory = this._createTxOffline("factory", HexUtil.encode(IoUtils.readBytes(new File(factoryPath))), new Object[]{_feeToSetter, pair}, new String[]{"Address", "Address"}, "factory");
        TimeUnit.SECONDS.sleep(10);
        String router = this._createTxOffline("router", HexUtil.encode(IoUtils.readBytes(new File(routerPath))), new Object[]{WASSET_FACTORY, factory, WNULS, treasury, _feeToSetter}, new String[]{"Address", "Address", "Address", "Address", "Address"}, "router");
    }

    /**
     * create pair
     */
    @Test
    public void testPairCreateTxOffline() throws Exception {
        String pairPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/NulswapPair/target/NulswapPair-1.0-SNAPSHOT.jar";
        String pair = this._createTxOffline("pair", HexUtil.encode(IoUtils.readBytes(new File(pairPath))), new Object[]{}, new String[]{}, "pair");
    }

    /**
     * create WAssetFactory
     */
    @Test
    public void testWAssetFactoryCreateTxOffline() throws Exception {
        String pairPath = "/Users/pierreluo/IdeaProjects/NulsNulswap/Nulswap_V3/WAssetFactory/target/WAssetFactory-1.0-SNAPSHOT.jar";
        String pair = this._createTxOffline("wassetfactory", HexUtil.encode(IoUtils.readBytes(new File(pairPath))), new Object[]{_feeToSetter}, new String[]{"Address"}, "wassetfactory");
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