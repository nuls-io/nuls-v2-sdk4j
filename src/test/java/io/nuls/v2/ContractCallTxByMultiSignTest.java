/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.CoinData;
import io.nuls.base.data.CoinFrom;
import io.nuls.base.data.Transaction;
import io.nuls.base.signture.MultiSignTxSignature;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.base.signture.SignatureUtil;
import io.nuls.base.signture.TransactionSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.constant.TxType;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.parse.JSONUtils;
import io.nuls.core.rpc.info.Constants;
import io.nuls.core.rpc.model.ModuleE;
import io.nuls.core.rpc.model.message.Response;
import io.nuls.core.rpc.netty.processor.ResponseMessageProcessor;
import io.nuls.v2.model.dto.*;
import io.nuls.v2.service.ContractServiceTest;
import io.nuls.v2.txdata.CallContractData;
import io.nuls.v2.util.ContractUtil;
import io.nuls.v2.util.JsonRpcUtil;
import io.nuls.v2.util.ListUtil;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

/**
 * @author: PierreLuo
 * @date: 2019-12-31
 */
public class ContractCallTxByMultiSignTest {

    static int chainId = 2;
    static int assetChainId = 2;
    static int assetId = 1;
    static String rpcAddress;

    private void setDev() {
        chainId = 2;
        assetChainId = 2;
        assetId = 1;
    }

    private void setTest() {
        chainId = 2;
        assetChainId = 2;
        assetId = 1;
        rpcAddress = "https://beta.api.nuls.io/jsonrpc";
    }

    private void setMain() {
        chainId = 1;
        assetChainId = 1;
        assetId = 1;
        rpcAddress = "https://api.nuls.io/jsonrpc";
    }

    @Before
    public void before() {
        //NulsSDKBootStrap.initTest("https://beta.api.nuls.io/");
        NulsSDKBootStrap.initMain("https://api.nuls.io/");
    }

    @Test
    public void createMultiSigAccountTest() throws Exception {
        //create 3 account
        Map<String, Object> params = new HashMap<>();
        List<String> pubKeys = new ArrayList<>();
        //pubKeys.add("03958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e3");// tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG
        //pubKeys.add("0318f683066b45e7a5225779061512e270044cc40a45c924afcf78bb7587758ca0");// tNULSeBaMnrs6JKrCy6TQdzYJZkMZJDng7QAsD
        //pubKeys.add("02c2b4e37fa297879c3ed824d021c0ee4692c6f87fcaf1681d712ccd485784b9bd");// tNULSeBaMrbMRiFAUeeAt6swb4xVBNyi81YL24
        //0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569
        //03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4
        //03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6
        //03b0d0d5780059e25d9b4c2ab4466a15f5ffc4a7bb0e6d2198543e6233ce5543f5
        //02089c6782dac3c9dc3c9d196c31aeda179029c4cc8bac2013b50d6b9314aaebd6
        //03439a5c8fe9928370bf86cd228203ff4088ef54605ece18743a99b8b5b6cc0951

        //0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569
        //03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4
        //03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6
        //029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea9
        //02784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb
        //02d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a538
        pubKeys.add("0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569");
        pubKeys.add("03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4");
        pubKeys.add("03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6");
        pubKeys.add("03151f3bac5ec023d478d392e11141429ff3fbee22263b9f4dfc29cb141be12409");
        pubKeys.add("02784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb");
        pubKeys.add("02d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a538");

        Result multiSignAccount = NulsSDKTool.createMultiSignAccount(pubKeys, 4);
        System.out.println(JSONUtils.obj2PrettyJson(multiSignAccount.getData()));
    }

    @Test
    public void callContractByMultiAddress() throws Exception {
        setMain();
        String multiAddress = "NULSd6Hh6mPcXSdHpqn59HpszPBTyWdLn93kq";
        String contractAddress = "NULSd6HgsFcPKAwaKWaiTTbWDeL27x8JT48HZ";
        byte[] contractAddressBytes = AddressTool.getAddress(contractAddress);
        byte[] multiAddressBytes = AddressTool.getAddress(multiAddress);
        Transaction tx = new Transaction();
        tx.setType(TxType.CALL_CONTRACT);
        tx.setTime(System.currentTimeMillis() / 1000);
        tx.setRemark("mint quota".getBytes(StandardCharsets.UTF_8));
        CoinData coinData = new CoinData();
        //byte[] nonce = HexUtil.decode(LedgerTestUtil.getUnConfirmedBalanceAndNonce(chain, chainId, assetId, multiAddress).getNonce());
        //if (null == nonce) {
        //    nonce = HexUtil.decode("0000000000000000");
        //}
        byte[] nonce;
        RpcResult request = JsonRpcUtil.request(rpcAddress, "getAccountBalance", ListUtil.of(chainId, assetChainId, assetId, multiAddress));
        Map result = (Map) request.getResult();
        String nonceStr = (String) result.get("nonce");
        if (null == nonceStr) {
            nonce = HexUtil.decode("0000000000000000");
        } else {
            nonce = HexUtil.decode(nonceStr);
        }

        long gasLimit = 500000L;
        long gasPrice = 25;
        coinData.addFrom(new CoinFrom(multiAddressBytes, chainId, assetId, new BigDecimal("0.002").movePointRight(8).toBigInteger().add(BigInteger.valueOf(gasLimit * gasPrice)), nonce, (byte) 0));
        //coinData.addTo(new CoinTo(multiAddressBytes, chainId, assetId, BigInteger.ZERO, (byte) 0));
        tx.setCoinData(coinData.serialize());
        CallContractData callContractData = new CallContractData();
        callContractData.setContractAddress(contractAddressBytes);
        callContractData.setSender(multiAddressBytes);
        callContractData.setValue(BigInteger.ZERO);
        callContractData.setPrice(gasPrice);
        callContractData.setGasLimit(gasLimit);

        callContractData.setMethodName("batchMintHistoryQuota");
        Object[] _args = this.parseNAIDomainHistoryData();
        String[][] args = ContractUtil.twoDimensionalArray(_args, new String[]{"String[]", "int[]"});

        //callContractData.setMethodName("transferOwnership");
        //Object[] _args = new Object[]{"NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy"};
        //String[][] args = ContractUtil.twoDimensionalArray(_args);

        if (args != null) {
            callContractData.setArgsCount((short) args.length);
            callContractData.setArgs(args);
        }
        tx.setTxData(callContractData.serialize());
        //0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569
        //03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4
        //03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6
        //029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea9
        //02784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb
        //02d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a538
        String[] pubkeys = new String[]{
                "0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569",
                "03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4",
                "03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6",
                "029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea9",
                "02784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb",
                "02d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a538"};
        List<String> pubkeyList = Arrays.asList(pubkeys);
        List<byte[]> collect = pubkeyList.stream().map(p -> HexUtil.decode(p)).collect(Collectors.toList());
        MultiSignTxSignature transactionSignature = new MultiSignTxSignature();
        transactionSignature.setM((byte) 4);
        transactionSignature.setPubKeyList(collect);

        List<P2PHKSignature> p2PHKSignatures = new ArrayList<>();
        List<String> priKeyList = new ArrayList<>();
        priKeyList.add("???");
        for (String pri : priKeyList) {
            ECKey eckey = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(pri)));
            P2PHKSignature p2PHKSignature = SignatureUtil.createSignatureByEckey(tx, eckey);
            p2PHKSignatures.add(p2PHKSignature);
            transactionSignature.setP2PHKSignatures(p2PHKSignatures);
        }
        tx.setTransactionSignature(transactionSignature.serialize());
        String txHex = HexUtil.encode(tx.serialize());
        System.out.println(String.format("txHash: %s", tx.getHash().toString()));
        System.out.println(String.format("txHex: %s", txHex));
        //TransactionCall.newTx(chainId, txHex);
        //RpcResult newTx = JsonRpcUtil.request(rpcAddress, "broadcastTx", ListUtil.of(chainId, txHex));
        //System.out.println(JSONUtils.obj2PrettyJson(newTx));
    }

    @Test
    public void testCreateMultiSignTx() {
        String multiSignAddress = "NULSd6Hh6mPcXSdHpqn59HpszPBTyWdLn93kq";
        String toAddress = "NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy";

        MultiSignTransferDto transferDto = new MultiSignTransferDto();

//        List<String> pubKeys = List.of("0377a7e02381a11a1efe3995d1bced0b3e227cb058d7b09f615042123640f5b8db", "03f66892ff89daf758a5585aed62a3f43b0a12cbec8955c3b155474071e156a8a1");
        List<String> pubKeys = new ArrayList<>();
        pubKeys.add("0325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db9569");
        pubKeys.add("03a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e4");
        pubKeys.add("03d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f6");
        pubKeys.add("029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea9");
        pubKeys.add("02784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb");
        pubKeys.add("02d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a538");

        transferDto.setPubKeys(pubKeys);
        transferDto.setMinSigns(4);

        MultiSignTransferTxFeeDto feeDto = new MultiSignTransferTxFeeDto();
        feeDto.setPubKeyCount(4);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcMultiSignTransferTxFee(feeDto);

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(multiSignAddress);
        from.setAmount(new BigInteger("10000000").add(fee));
        from.setAssetChainId(SDKContext.main_chain_id);
        from.setAssetId(SDKContext.main_asset_id);
        from.setNonce("0000000000000000");
        inputs.add(from);

        List<CoinToDto> outputs = new ArrayList<>();
        CoinToDto to = new CoinToDto();
        to.setAddress(toAddress);
        to.setAmount(new BigInteger("10000000"));
        to.setAssetChainId(SDKContext.main_chain_id);
        to.setAssetId(SDKContext.main_asset_id);
        outputs.add(to);

        transferDto.setInputs(inputs);
        transferDto.setOutputs(outputs);

        Result result = NulsSDKTool.createMultiSignTransferTxOffline(transferDto);
        System.out.println(result.getData());
    }

    @Test
    public void appendSignature() throws Exception {
        //setDev();
        String pri = "";
        String txHex = "02006caf906700008c0117010003580a041a96560828b6f0ebca5ea1a901648ac71101000100201d9a0000000000000000000000000000000000000000000000000000000000080000000000000000000117010001603b0b1bbbe7c5410c4242371b96afb40cd2fb890100010080969800000000000000000000000000000000000000000000000000000000000000000000000000fd74020406210325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db95692103a1f65c80936606df6185fe9bd808d7dd5201e1e88f2a475f6b2a70d81f7f52e42103d18c55fe704458198da46c9ad44c3a2a552fcfa034616a78953db6f9d9d3c7f621029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea92102784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb2102d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a5382102d90319f6b6c5ddc1b11258ae6ac81af50724a6c4f692fe856ff8f9b8ec01a53847304502210091ff3e4309c1d7eabcaac58eb618de895d23e00884724b21c418a3c63581ea9902200ed7327b16de8bd64c063bfb037b25f9b88f31d54d66025dbfed6139709da097210325f8f319bd2cc6b1372692111792e2e04cd4511ace2497617321af7a93db95694630440220016bd43fadade34c480792d68064a4f35f5987112aa2e0c8273d4936512cf1e302206f2ec0539911b1c613b2a1ff61726112f5b9a7289d0e581c7b43d512b0b7f96a2102784d89575c16f9407c7218f8ca6c6a80d44023cd37796fc5458cbce1ede88adb473045022100ab7c46470870abde6220f6a1bf1782045d244c4dfe4e4d1bfac0de5a23876144022002878d8b69dca863352246c7cc813e8b3b26d4fc8c54f31792ea25bc8f68b29721029351db507c3370e2b5eed455e6d838042987d092ce31426cefa3acd5b1c22ea9463044022017f128a3f4afdfde634220986ffd1b83b1480f095a6da92e9a23e50cc2f3d62d022030c4b0a657a5246da8cdb0c261f045a1f19f39156361ad6a925538260237db60";
        Transaction tx = new Transaction();
        tx.parse(HexUtil.decode(txHex), 0);
        MultiSignTxSignature transactionSignature = new MultiSignTxSignature();
        transactionSignature.parse(tx.getTransactionSignature(), 0);
        List<P2PHKSignature> p2PHKSignatures = transactionSignature.getP2PHKSignatures();
        for (P2PHKSignature p : p2PHKSignatures) {
            System.out.println(HexUtil.encode(p.getPublicKey()));
            System.out.println(ECKey.verify(tx.getHash().getBytes(), p.getSignData().getSignBytes(), p.getPublicKey()));
        }
        //ECKey eckey = ECKey.fromPrivate(new BigInteger(1, HexUtil.decode(pri)));
        //P2PHKSignature p2PHKSignature = SignatureUtil.createSignatureByEckey(tx, eckey);
        //p2PHKSignatures.add(p2PHKSignature);
        //tx.setTransactionSignature(transactionSignature.serialize());
        //System.out.println(HexUtil.encode(tx.serialize()));
    }

    /**
     * Multiple transaction signatures
     */
    @Test
    public void signMultiSignTxTest() throws Exception {
        String txHex = "0f0080140b5e0b72656d61726b5f74657374fd7c19020003d0a56ca8250a22a74b6807b80d7d4d9bf2004d90020002cc94308474fd917dd514d7a40cd365e298bf3768fd1a19504b03040a0000080000aa7b564e00000000000000000000000003000400696f2ffeca0000504b03040a0000080000aa7b564e00000000000000000000000008000000696f2f6e756c732f504b03040a0000080000aa7b564e00000000000000000000000011000000696f2f6e756c732f636f6e74726163742f504b03040a0000080000aa7b564e00000000000000000000000017000000696f2f6e756c732f636f6e74726163742f746f6b656e2f504b0304140008080800aa7b564e00000000000000000000000028000000696f2f6e756c732f636f6e74726163742f746f6b656e2f53696d706c65546f6b656e2e636c617373b558f97754e5197eeecc90990c972d9090059209a498cc9291a58a8152020d35ca5602b1605bbd99b9492ecc12670922b46ead4babd56aeb5ad1565bd4aa2c020169d59ed353cfe93fd17f84d3d3e7fdee9d3b37612639d5d31ff2ddf77ecbfb3eeff67c77f2afff7cf639804df87b23a23825c323329c96e14c1831fcb411cdf859108f86119497281e93e171997e22cce149197e1ec62ff0d462acc2d3413c13c672fc2a8ce7f0bcccfc3a8817827831cc53bf91e125195e96e5df8af4bb205e09a3430e44f19a0cafcbf086a87d5386df87f096cc9c9597b7457a27843fc8f38f21bc2b7ade0be24f41fc39887341bcaf219033b2a686a6bdc78d692399317213c99152c1ca4d6cd3d0503c951dcb673484d266caca1a99a2066d58c3e252be646446ca535399531a5aeca359a33499dc654d0ce74ae68459e0f1d098417d2993a796d97bca252b93dc674c71b171c49ac819a57281c68766af6edf6be593b972a6984ce573a582912a258be913c9c174ba60168bdb6a9bdb419d412393c99f34d31a4a5f43e337342f0056f5f6d58a6360773e6d4a10ac9cb9bf9c1d330b878db18c8a7a3e6564468d8225efce64a0346931643db7c228e54f98b9e488959de26691a9bbed503957b2b2e6a855b4787c3097636e4a563e47151b6a7b62b87b92a39679924afcbd7d4c6b6b05fdada96cd86ee5acd20e0d3b7a6f75b0decc5c35c37da31a968822cbc80c66f304ae61dd6cfc078d02ebb164166639125dd09143e64365ab60a6a5b2541548e16938dcbb4032e75dad1b8e45f99339b3a0a173fef3acc8e294994bcb565d1d19ac14e8a269235336a5796655c0a9a94a152c192919a9132c47f56eb302b5d048ae386e16f614f2590d47be917bb5bdeb3bc6121c57ea7da53cc3e974f181710d772c60af6ec0828e129242c5030d772e84be2ebea0313555c84f53dd722b972a9846d11c543306c92a6ca4d3667ad48eef7232d79cf565c5f29832e66e0ae53315510ebb49fabf84973d1032ec2d1babe2260f2ed77eb868565f96550e559739b3ab12d7af19cc5109a6bdc583c0d5ba283569a64e90657aeb9f4fd659ab7fa24115ffc68a40df63f3eaf0528b9cf7678b131a06ff27bbb5358547f2e542cadc6349cb2df7306bbf6cd7d18fa48edb65d8884d3a36638b8e217ca0e3436cd1b0a27a6fdc6d1427d9ac3abe8d3b74dc25c35f64f347f858c727381fc4051d177149c7a7b84c42abc3edca76cf61a74586a64da1c82573def5e11ca96477c62816cda2e0213d5cd17115333aaee13a0fccbacf782dd58a888e49983a72c8e83821c367b8a1c382b910ba4a2755d0cd7ad79116adf7e05e1d79301e7fc5df747c8ef31a3a8673c5f2f8b895b2b82fe274703aa254cbe6691d5fe0bc8ea36087270e4f9a11551d916cb9588a8c999109f6312f864869d2c845f2850819dfc8f078e4f67e394e289db32c389413c98fdb36fac5c52f79e32c7cafb216aae57260ecb899a2f6b535fb6bb7f3a261cd7c5153772c0b2e706ce8d001be1c1edacf712a7f527a6bb82e6f86b2e54cc9529f59fdf55ab0ded9d69a78f749eb34149d5b29c22b7f812bcc3f55a6735bbdf7be1d916db7cef4dd3aa5a1bda68123ac4e524ec0cc5a54df5587bd544dd93d3f6172df861a306a1a0d16ec0f025ae83d26e71b53f9ec9451607ee761337e04f9c9868c7b85097943f71eabc51de8e637750c3ec49140039a842af8e3a049d8423d49187cfa8433d493c400d2b492b7f2ef2ecffb003fd699616ca3bc9d3349be697c2e8a5e8676416df90ec70635b9163b38eaf6067c173bf91c745677f1a911422d45beb98ad62fa8682376d750e4ff688ea2be05156dc6f76a280acc45945c50d110f6388afecd1d8bf834a3335476090d571088de40f0e80c4257d05815c3d1d81568d1f815f8a2cd812bf0471397b0b8397015fa552c915397d1780d4b05d0752cf3e3be1b587e54736666b0e21a9a2eba40fbb158fd060c30658bb11a2d6865296cc606a639c6246f645a0799d6434c729a491687b6d8401d8744ea57711049cac5a7a4ef739f5f491b95febb29b705b86d58797eeb4892e50189c51754212692441c8e5dc74a1fbec4aafd89af1064602e24e2ce54f340a02df015429c1449c21f505e75f2f72a887529313611650bc3bd8ec9bf8d798b33e4552f92ae1749ecc53e276dfb29f978ba1b07e8bf9f6797e0204f04f00327a1f6da56ae1de2cc52f86ea22588919b680ee2f0ac248bdf4b7c737c3d825127ef670823c0674f34c6142566d0128d715c1d8d736c95d4c54492ac05aa85daa2c0df8b46825e46b0cd84bc8e80c4b1a8adcf75ac07f7398ef5e087ca31910eaaf408b895fe9ae9e0bde5a4e3491e914cf6c4fe8960e01c02fe6b68932aaba4a6795fdc4ecdbeb8e4c0ef817884d11a453b2174d2f83aea1488115b9f07a21dfb7684713f81f9545417c31fdaa9dd446bad90063517e98ff0632798166d8ae6ae2863199728c6e24e143921f29c3836a9583d80101e64120d92d1980760970bb04b454e53d24105b0665a7fe22279d041d21e750cb7df404705c49ad92096aa9d134ce624bbcff200687701b4ab9ad494340f80075c00136c0151d15d01b0b602404d5c45671d1c59e2c8d1cdbc0747b78ba3dbc5d14d1febe2305c1c978943623c142586ae8aedfdf1c45544ce62859a60e53040e7a057b0b6cc83d5efe9f022a35c22ee32ab6d9a55f4302faf47d8e7a73d8d30e4621f72b10f31cd5b5487df89946a04a9b746e9e21e165ba896533ef914a42c4e3de1907584f545740381685b40bc4bc8108bb705e85d375daaf2acdd0d8fb2d01ea3d5c7b1923abad85955268ab83823187770469846bb61234ec3865821f72826f2c907af83e78c83a7d38327d1b46e06eb2b687a66a1b1cbfe296a7b1a2bf00ccbfe590f924e1749a78ba4d345d2e922697191582e927f7045740cd8b1f0f2b74dd66dce453610508b6dce1dd51688276c49507a59fc39ea7b5efe958836bc40067991fcfc126fa2973d88075cc4032ee20117f1808378156fb5e3e4f32a8bdb335b3923f90f816493248bf3147f42381e9de6aa448ba1bc8a6fed8fc69b36cce0b68a7b8db10423de5d85ee4df72b64ac577975bcc680bf4ee86f78ca72bd0b793d5b6e9ff30563b776031372bf82ec931f340e90a203a4cb01928837f54a862b207ae682b0b3fc16d59d65cdbdcd78bee301e025b7ec1c726b60ac2b00f8b3c701708c6f12cfd5bc075648d3b285dfacde0817dde60cab6def320aef29832df631d7e06a654653415f045fd34e4dd979c8b133e5dc371dd19804597843cabab62ddbc973d4f33eed7e407afcd0435c1daecd0e14944d918aee1543eb2d8ef59263dd74acb7d6f0327e0d7d73ddfc98ee7ce231d9ea9a6c75dc14a9acbe8be6383ced96984f79b1a68ec38959666d8f2f52d12542f894fe5cf6a4758d6b7e8debf11ac7639104887fb6ef2755c53c8c5f2ae53e2c278b3e4b36ece0f35534fe17504b0708ec308779cb09000028180000504b0304140008080800aa7b564e00000000000000000000000022000000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e2e636c617373a552d14e1341143d03b5cb16aa28a8a0284a08697970131ff481a76a24694282a19507dfa6bb431d989da933b325fc9a0f7e801f65bc534ba5b6a5896eb23b3be79e7bcebd33f7c7cf6fdf01bcc64e84e711b623bc60a8367a3d6bfa5c7de80bed19569a5a0bfb5e71e7848bf092186dcbb53b1376c828699e0b86f55afde89cf779a2b8ee262d6fa5ee1e306c9e14dacb5c9c4a273b4a34b4369e7b69b463d83b9226d18572496ab4b73cf589cb2e123ee224a7525c9248d95de51da318963291ca9c2b4a5eacd59b0ccb9ea8aa55f47aea8a61e3ba869cfb2fc93bd96d6a2fbac29244dce154582a8ecf18ded4a61b37b2cc0ae70e668aec8c37f3915b6add0b3bd6d5fedcae4ec4d7425a9191e2921f1e26c3db39554d2faafe99eee85ae4d09a9ce1d33ca17fb489f86034e8ae63ae94b90cc7c9d0fe2fb799475d6999c2a6e2502af2a8b4cd85d0af0295616b42d0876832e030acfe19c2e3ceb9486942f76fcbd8fd6be06f278fcd7e99816101e1894b749788695fa15d99d66580b0952958157727b07b589dc0eee301a9dfc0b0466f144cd70784877844ff01ac2efc0647a1c7d81886d6166f864684cdd9b94ff0748aef388d085b83ef33dca135141a7c4ad46e488a10ff02504b070868fe421cca0100005e040000504b0304140008080800aa7b564e00000000000000000000000030000000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e24417070726f76616c4576656e742e636c6173738d565b6f1b4514fec6bbde75b69bbb49d224b46929e05b6a2ee19a4b73218140d2943835b4406163af926d9cdd74771d90a0129540ea033c801020242e2f252f790089b80824c45390f82ffc03049c995dbb89e3243cf8ccd93367e67cdf7c6746fef39f5f7e03f018ae6988e34213148c7333c1cd248f4da998d63083e735bcc0cd0c66b937abe2450d2770218697f838c7cd7c0c176358e0ee256e5e56b1a822c71075deb64d97e1d49ce564ed72c9cb161cdb778d829ff58a6bd98962d1353d6f9841f5364cbbc853a39b46a96c3274cd5d37368decbae1af6627ad9559db37574c97529511cbb6fc3186cb89a3773d66b6e1f6c93c833ce5140940eb9c659b17cbebcba6bb642c9728d231e7148c52de702dfe1d06657fd5f2189a2736365c87b04f6f9ab6cfa0cfdac47caa64789e49d39983607c67cdb4b34bdc9edbb798389e5d2cdbbeb56ee62dcfa22a970cd758377dd39db06dc7377ccbb169cf546382462d27bb68de285bae59a41d632ba6bf10883190481e2747ccab659f39e694f98969b479ae2aa0e6edf9e065f381a03d54f6104979b930eb64e2706114a2639488f97d6152c9b057b20bcbd7cd823f9cbccac01c2ed28129219241a234e77ca3b0366f6c08eda8c1a9f4aae1ad06824b89e42c152156e5122533fa88f94ece772d7b85215e852f760ea2b4b39673ca6ec19cb1782f6842cdf33c4d471f967474a15b470f3727d1abe332f22a5e21e9fe7f37a87855c7237854c5151d4fe16986ee7a189365ab244e3bbe6fe5bbe2f28deab88ad7b8799d006606c28b364ae43203e2ae898c37740c6384a1adfef4187a1bea1ff6391d5a3e9059f43a43e7be7312413aa6fea308379aded362c4ab514b9054c60627c330983828cd41b5c2631aaecbaff6cf11f9fd89a9c3a771869ecc38a82b2121c215a78735c2451723e94ea346f37de8a7effbe9ab44a34c633c75172c95de412495d981941adc81fca358758a6c17a2646f50a68b267868818f4e94719aa2a9603d06f000203c5e97098f578e088fd796708efc4e89261f2467bf7d080f93e590b234f2e5d1d44f88fc50c3a088e03ba2a61e248435191248868bc7289b575404977b0c34117d8fd6dc143b74055935d44a889a2e444320523d90f71b0249370622d503f980d67c7808909e104806830d80c8f5406e3704725e2c3a0044ae07f211adf9f810205c335e98ae7cb8d75f9423d1782b95fe1e51793bbd8b965405d134fdee202a6da77f8732cf75dba58ea22143bf0ad4afd0ba45375ec4298b9f679020514cda9b205513e45db4f381627205b12db472ef0fa8f21664699b404882462f01053e818a4fe9e43e23989f13e52ff026be14b40602c0355ab7e89fc6e3824e1143148be009d1a0badac2fe46bf8a2715d6adb0b836cea9d32b1752ff393cc6e9805d734a306be26046da4ef7ad069c78580ac36f55e37210271a5a10efbb07bf43dc9eafe9367d43cdf32da6f0dd1e35a643d89d04fa193c4b5038d84128ede3ec5fbae511824b30c947e84718cbefffa445f49c862caed1b6bc51867ec5892b77a1773457484141a5b5a32df025eeb7073e616e6dbb594147059df58d77674fe30d8540231815760c67c3a746c173f42c35fd07504b0708ea7bbc798f040000e6090000504b0304140008080800aa7b564e00000000000000000000000030000000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e245472616e736665724576656e742e636c6173738d56df531b5514fe6e76b31bb60be19740015b5aab8624b06ad156c36f84160da5051a0bd5da25d9c296b08bbb1b5e1c1ffa6ff8aebcf0a033923a3ae3f88433fe2ffe078e7aeedd854212c0879c7bf6dc73eef9cef9cebd933ffff9e53700efe189864e4c3441c12417535c4c73db8c8a590d73b8a3e12e177398e7dabc8a4f345cc244029ff235cfc54202f71258e4ea7d2e1ea85852b1cc203ff3dc6d862b79db359c4ad9378aae1378663130fcd29631552a7996efe7186281cb10df35cb158ba12bffdcdc358d6d33d834a6ed8d7927b0362c8fbc9451dbb183718687a9f30fbc60b7e1f18305823be3960840326f3bd6bdcaf6bae5ad98eb65b2b4e7dda2592e989ecdbf23a31c6cda3e43f38a673afe33cb9bddb59c80419f771ccb9b299bbe6fd176b61e4ce06e598eb1c2e58d53c154e3f5a58a13d8db56c1f66dca72dff4cc6d2bb0bc29c7710333b05d87ce4c372ed03cf63196acaf2ab66795e84475c30ae6040d03a9c18b8850fd23e76b17f498f72b4e47af70e6fc704dd0772124b187729d4163c23ff6ba9c3a9b0c854a30cb54ed6b9153d974368cc5f5e75631c80dae3130971353b72588318988e6e5c02c6e2d983b822f1a674abd69fa9b21c9526a709e92502d95323933fa4804ee72e0d9ce0643e7117c717268a593b565b7e215ad399bf3af090687b99b8e3eace8e842b78e1e2e2ea357c74314547c4674fdff0950f148c73b7857c5aa8e5bb8cdd05d0b63ba62974b96c7d0712af26b7ed5c674ace131179f132bd981c01da3b2b203e26689cd2f74e430cad05adb3786de867c47534ded2a84048bc9a6eca73a248cd4a0fef34a6db47d62a4a8eb8d8681483277762ca7c43094aa27a59ea7a841b91affa3c939c7bf3f3573f636aed1d3d8099a47488871aee9018d71bac54a8cd3aad17e1ffae9fb75fa2ad32ad3da997e0996ce1c2096ce1e404a0f1d40fe51445d21d98538c92c790ea109c36881810e1a82ab644d87f118c01b80d0785e26349e3926349e5bc20dd293120986374fc8b7f036490ec7a09587c6d33f21f6c3717e45186f8a7c7ae810e5634861300a1e276f9e4d1175bc42af09eb2d8ab92d4ee80abd8e112b1162ba060d8148b540720d81641a03916a814c50cce419407a2220596a743d10b916c84c4320c322a80e885c0be40ec5dc3d0308e78b27a68b1e9df517f970ea5ea433df232eef670ed192ae229ea1df77884bfb99dfa12c70de0e699a68c9d2af0af55b24f7a065859dbc783f4307896cd24907e9c8413e441b5fc8265791d843926b7f4095f7204bfb04421265f41250200f150bd4b94582f9804a5ec2975816650d84808fcb7a41ff266e8a724a18215b0cef8be1d4d516f637fa557ca0b06e85756a93bc747adba2d27f8eda381b56d79c1695357130a3ad57fb36c39ab8598acc4f8fec7268a732b4d0def70a7ebbb8398fe826add2f0ac119b8f4fb0311bc1ee20d01fe22382c2c10e41699b64ffd20d8f115c82493a223dc658e1f42705d1531a55f1848ee58332f22b2eadbe84dede5c25064529c9f6d65097b8de16ea8439d9fa4d15ed5574d40eded313833712018d614cc8715c8f9e19051fd393d4f41f504b0708826261e37e040000ca090000504b01020a000a0000080000aa7b564e000000000000000000000000030004000000000000000000000000000000696f2ffeca0000504b01020a000a0000080000aa7b564e000000000000000000000000080000000000000000000000000025000000696f2f6e756c732f504b01020a000a0000080000aa7b564e00000000000000000000000011000000000000000000000000004b000000696f2f6e756c732f636f6e74726163742f504b01020a000a0000080000aa7b564e00000000000000000000000017000000000000000000000000007a000000696f2f6e756c732f636f6e74726163742f746f6b656e2f504b01021400140008080800aa7b564eec308779cb090000281800002800000000000000000000000000af000000696f2f6e756c732f636f6e74726163742f746f6b656e2f53696d706c65546f6b656e2e636c617373504b01021400140008080800aa7b564e68fe421cca0100005e0400002200000000000000000000000000d00a0000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e2e636c617373504b01021400140008080800aa7b564eea7bbc798f040000e60900003000000000000000000000000000ea0c0000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e24417070726f76616c4576656e742e636c617373504b01021400140008080800aa7b564e826261e37e040000ca0900003000000000000000000000000000d7110000696f2f6e756c732f636f6e74726163742f746f6b656e2f546f6b656e245472616e736665724576656e742e636c617373504b0506000000000800080051020000b316000000000b6e726332305f746f6b656ec94f0000000000001900000000000000040103616972010341495201053130303030010132480117020003d0a56ca8250a22a74b6807b80d7d4d9bf2004d900200010001791200000000000000000000000000000000000000000000000000000000000800000000000000000000d102032103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e3210318f683066b45e7a5225779061512e270044cc40a45c924afcf78bb7587758ca02102c2b4e37fa297879c3ed824d021c0ee4692c6f87fcaf1681d712ccd485784b9bd2103958b790c331954ed367d37bac901de5c2f06ac8368b37d7bd6cd5ae143c1d7e3463044022029073139f722e963022204e15f0e4359356c02cb24f606dec675ba51228176f102201bbb30774227338e41bdda146345f175ef9d22f0af9caa73767003cee5afde9c";
        String signAddress = "tNULSeBaMnrs6JKrCy6TQdzYJZkMZJDng7QAsD";

    }

    @Test
    public void airdrop() throws Exception {
        //callContractData.setMethodName("batchMintHistoryQuota");
        Object[] _args = this.parseNAIDomainHistoryData();
        //String[][] args = ContractUtil.twoDimensionalArray(_args, new String[]{"String[]", "int[]"});
        this._callTxOffline(BigInteger.ZERO, "NULSd6HgsFcPKAwaKWaiTTbWDeL27x8JT48HZ", "batchMintHistoryQuota", "", _args, new String[]{"String[]", "int[]"}, "init");
    }

    private void _callTxOffline(BigInteger value, String contractAddress,
                                String methodName, String methodDesc, Object[] args, String[] argsType, String remark) throws Exception {
        int chainId = SDKContext.main_chain_id;
        String sender = "NULSd6HgZnYzztV5LzDmUzyrPp7uqF7RDDGUy";
        String priKey = "";
        String signedTxHex = callOfflineHexWithoutValidation(chainId, sender, priKey, value, contractAddress,
                methodName, methodDesc, args, argsType, remark);

        System.out.println(String.format("signed TxHex: %s", signedTxHex));
        // 在线接口 - 广播交易
        Result<Map> broadcastTxR = NulsSDKTool.broadcast(signedTxHex);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcastTxR), broadcastTxR.isSuccess());
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

    private Object[] parseNAIDomainHistoryData() {
        List<String> list = new ArrayList<>();
        /*list.add("NULSd6HgXvnCdcuFt68WrJQh8zcDn5n6dabkB,8");
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
        list.add("NULSd6Hga6BPQDMwEfoy2nge8y3PXxw2uCTe1,10000");*/
        list.add("NULSd6HgYDZxEdNhBEYPNebhcezcas9MkHsCU,1000000");
        String[] tos = new String[list.size()];
        int[] quota = new int[list.size()];
        int i = 0;
        for (String str : list) {
            String[] split = str.split(",");
            tos[i] = split[0].trim();
            quota[i++] = Integer.parseInt(split[1].trim());
        }
        return new Object[]{tos, quota};
    }

    static class SimpleAccountDTO {
        /**
         * Account address
         */
        private String address;

        /**
         * alias
         */
        private String alias;

        /**
         * Public keyHex.encode(byte[])
         */
        private String pubkeyHex;

        /**
         * Encrypted private keyHex.encode(byte[])
         */
        private String encryptedPrikeyHex;

        public SimpleAccountDTO() {

        }


        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getPubkeyHex() {
            return pubkeyHex;
        }

        public void setPubkeyHex(String pubkeyHex) {
            this.pubkeyHex = pubkeyHex;
        }

        public String getEncryptedPrikeyHex() {
            return encryptedPrikeyHex;
        }

        public void setEncryptedPrikeyHex(String encryptedPrikeyHex) {
            this.encryptedPrikeyHex = encryptedPrikeyHex;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"address\":")
                    .append('\"').append(address).append('\"');
            sb.append(",\"alias\":")
                    .append('\"').append(alias).append('\"');
            sb.append(",\"pubkeyHex\":")
                    .append('\"').append(pubkeyHex).append('\"');
            sb.append(",\"encryptedPrikeyHex\":")
                    .append('\"').append(encryptedPrikeyHex).append('\"');
            sb.append('}');
            return sb.toString();
        }
    }
}
