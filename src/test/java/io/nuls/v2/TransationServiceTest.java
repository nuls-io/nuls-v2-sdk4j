package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransationServiceTest {

    static String address = "8CPcA7kaUfbmbNhT6pHGvBhhK1NSKfCrQjdSL";
    static String pubKey = "03ac18d40eb3131f934441f81c631b3898097b606a84893da1559de61fe3d3cfe9";
    static String priKey = "6df381435098e47b685cdc00fa1d7c66fa2ba9cc441179c6dd1a5686153fb0ee";
    static String encryptedPrivateKey = "0c8e925d27660dbd04104455c001efe7a5d4cba8fc484d06506c8ff4baa653be2d69e31c971243e2185782cabbbe265a";
    static String password = "abcd1234";

    static String packingAddress = "8CPcA7kag6XT1a2yoiTijYaJGY7jceebYWFFq";


    @Before
    public void before() {
        NulsSDKBootStrap.initTest( "http://127.0.0.1:18004/");
    }

    @Test
    public void testCreateTransferTx() {
        String fromAddress = "NULSd6HggMezjc4qosG39fJ33rUaCS8eCodmK";
        String toAddress = "NULSd6Hgb4T72E3R1insQ66LFK8LkNfdi9jBE";

        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
        from.setAmount(new BigInteger("1035780572679").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("0000000000000000");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("1035780572679"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result<Map> result = NulsSDKTool.createTransferTxOffline(transferDto);
        String txHex = (String) result.getData().get("txHex");

        //签名
        String prikey = "7dd7d92703858c270abcbc62cceffc9e6045bc9aacd9afdc1e04af38ecbe88f";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        System.out.println(txHex);
        result = NulsSDKTool.validateTx(txHex);
        String txHash = (String) result.getData().get("value");
        System.out.println(txHash);
//        if (result.isSuccess()) {
//            //广播
//            result = NulsSDKTool.broadcast(txHex);
//            System.out.println(result.getData());
//        }
    }

    @Test
    public void testCreateMultiSignTx() {
        String multiSignAddress = "tNULSeBaNTcZo37gNC5mNjJuB39u8zT3TAy8jy";
        String toAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";

        MultiSignTransferDto transferDto = new MultiSignTransferDto();

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
        dto.setAgentAddress("tNULSeBaMuUcKTDkhAgCBdqSrYJgcpn32K9iE5");
        dto.setCommissionRate(10);
        dto.setDeposit(deposit);

        CoinFromDto fromDto = new CoinFromDto();
        fromDto.setAddress("tNULSeBaMuUcKTDkhAgCBdqSrYJgcpn32K9iE5");
        fromDto.setAssetChainId(2);
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
        dto.setAgentHash("49ffb342519fd965126467a0e20c15c553643cc73007d2093962799e276b75bb");
        dto.setAgentAddress("tNULSeBaMuUcKTDkhAgCBdqSrYJgcpn32K9iE5");
        dto.setDeposit(new BigInteger("2000000000000"));
        dto.setPrice(new BigInteger("100000"));
        List<StopDepositDto> list = new ArrayList<>();

        StopDepositDto depositDto1 = new StopDepositDto();
        depositDto1.setDepositHash("33c9ca8284216132f7bf4c8f51ec71fb131b6bfb0a866f4c7a459904d0448adc");
        CoinFromDto fromDto1 = new CoinFromDto();
        fromDto1.setAddress("tNULSeBaMgSVD53hrN7PaDw3vPSXZeB9GLHTTh");
        fromDto1.setAssetChainId(2);
        fromDto1.setAssetId(1);
        fromDto1.setAmount(new BigInteger("20100000000000"));
        depositDto1.setInput(fromDto1);
        list.add(depositDto1);

        dto.setDepositList(list);
        try {
            System.out.println(JSONUtils.obj2json(dto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Result result = NulsSDKTool.createStopConsensusTxOffline(dto);
        System.out.println(result.getData());
    }

    @Test
    public void test() {
        String a = "090008e4bf5d002049ffb342519fd965126467a0e20c15c553643cc73007d2093962799e276b75bbfd16010217020001eb8272032257427b1f12516226a04355a08422450200010000204aa9d1010000000000000000000000000000000000000000000000000000083962799e276b75bbff170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce547120000000000000000000000000000000000000000000000000000087a459904d0448adcff0217020001eb8272032257427b1f12516226a04355a084224502000100609948a9d101000000000000000000000000000000000000000000000000000018f2bf5d00000000170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce547120000000000000000000000000000000000000000000000000000000000000000000000";
        String b = "09004fe4bf5d002049ffb342519fd965126467a0e20c15c553643cc73007d2093962799e276b75bbfd16010217020001eb8272032257427b1f12516226a04355a08422450200010000204aa9d1010000000000000000000000000000000000000000000000000000083962799e276b75bbff170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce547120000000000000000000000000000000000000000000000000000087a459904d0448adcff0217020001eb8272032257427b1f12516226a04355a084224502000100c0dd3aa9d10100000000000000000000000000000000000000000000000000005ff2bf5d00000000170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce547120000000000000000000000000000000000000000000000000000000000000000000000";
        System.out.println(a.equals(b));
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
    public void deserializeTxHex() {
        String txHex = "06005ae5155d0020f0065601e5b94a4c9fa6be808d67bfbc80e74a6afd631232622f795c2196d64f8c011764000115423f8fc2f9f62496cb98d43e3347bd7996327d6400010000d0ed902e00000000000000000000000000000000000000000000000000000008622f795c2196d64fff011764000115423f8fc2f9f62496cb98d43e3347bd7996327d64000100c08dde902e000000000000000000000000000000000000000000000000000000000000000000000000";
        Result result = NulsSDKTool.deserializeTxHex(txHex);
        System.out.println(result.getData());
    }

    @Test
    public void testMultiSignStopConsensusTx() {
        MultiSignStopConsensusDto dto = new MultiSignStopConsensusDto();
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
    public void validateTx() {
        String txHex = "0900c6edbf5d002049ffb342519fd965126467a0e20c15c553643cc73007d2093962799e276b75bbfd16010217020001eb8272032257427b1f12516226a04355a08422450200010000204aa9d1010000000000000000000000000000000000000000000000000000083962799e276b75bbff170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce547120000000000000000000000000000000000000000000000000000087a459904d0448adcff0217020001eb8272032257427b1f12516226a04355a084224502000100609948a9d101000000000000000000000000000000000000000000000000000046e2c35d00000000170200011780d642f03b7123002199491c776eaa9cec097a0200010000285ce54712000000000000000000000000000000000000000000000000000000000000000000006a2102bab8d05cef7f38da4627d4f23ff2d8a67a4ee5dec70d263bf3b772ba2d9fb362473045022100d89c3d5e31b7a4d935de693d00b34b49f244ca82ab3e219c79d42bc8d0ea433102204abd0b7b58bec4e2628b915ee55d01bddf506b8e5283b707ee986ec22601bdbc";
        Result result = NulsSDKTool.validateTx(txHex);
        System.out.println(result);
    }
}
