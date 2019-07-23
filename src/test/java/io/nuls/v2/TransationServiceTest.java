package io.nuls.v2;

import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.util.NulsSDKTool;
import org.checkerframework.checker.units.qual.A;
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
        NulsSDKBootStrap.initChainId(100);
    }

    @Test
    public void testCreateTransferTx() {
        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(address);
        from.setAmount(new BigInteger("10000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("6db83fdd14f6f233");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress("8CPcA7kaXSHbWb3GHP7bd5hRLFu8RZv57rY9w");
        to.setAmount(new BigInteger("10000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result result = NulsSDKTool.createTransferTxOffline(transferDto);
        //d3de15b1d1746732510610a102c2189da970df534ea9407c48d0b7838eeb5b8e
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
        String hash  = "526d8e25bb7843518b7722a97f01b6d2fb2c46afc60121e869788659d202de92";
        Result result = NulsSDKTool.getTx(hash);
        Map<String,Object> map = (Map<String, Object>) result.getData();

        TransactionDto tx = JSONUtils.map2pojo(map, TransactionDto.class);
    }
}
