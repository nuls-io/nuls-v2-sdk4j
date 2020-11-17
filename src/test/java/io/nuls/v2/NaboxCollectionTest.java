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

import io.nuls.core.basic.Result;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.ImputedGasContractCallForm;
import io.nuls.v2.service.ContractServiceTest;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 测试网测试
 *
 * @author: Loki
 * @date: 2020/11/4
 */
public class NaboxCollectionTest {

    @Before
    public void before() {
        NulsSDKBootStrap.initTest("http://beta.api.nuls.io/");
    }

    /**
     * 普通转账
     * 非NULS资产
     */
    @Test
    public void createTxSimpleTransferOfNonNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "tNULSeBaMrbmG67VrTJeZswv4P2uXXKoFMa6RH";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createTxSimpleTransferOfNonNuls(fromAddress, toAddress, 5, 1, amount);
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(String.format("hash: %s", txHash));
    }

    /**
     * 普通转账
     * NULS
     */
    @Test
    public void createTxSimpleTransferOfNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "tNULSeBaMrbmG67VrTJeZswv4P2uXXKoFMa6RH";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createTxSimpleTransferOfNuls(fromAddress, toAddress, amount);
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(String.format("hash: %s", txHash));
    }

    /**
     * 跨链转账
     * 非NULS
     */
    @Test
    public void createCrossTxSimpleTransferOfNonNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String value = "2.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createCrossTxSimpleTransferOfNonNuls(fromAddress, toAddress, 2, 1, amount);
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(String.format("hash: %s", txHash));
    }

    /**
     * 跨链转账
     * 非NULS
     */
    @Test
    public void createCrossTxSimpleTransferOfNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
        String value = "2.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createCrossTxSimpleTransferOfNuls(fromAddress, toAddress, amount);
        String txHex = (String) result.getData().get("txHex");
        //签名
        String prikey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
        result = NulsSDKTool.sign(txHex, fromAddress, prikey);
        txHex = (String) result.getData().get("txHex");
        String txHash = (String) result.getData().get("hash");
        //广播
        result = NulsSDKTool.broadcast(txHex);
        System.out.println(String.format("hash: %s", txHash));
    }



    /**
     * 合约资产跨链转账
     */
    @Test
    public void nrc20CrossChain() throws Exception {
        ContractServiceTest contractServiceTest = new ContractServiceTest();
        contractServiceTest.nrc20CrossChainSDK("TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9");
    }

    /**
     * 合约资产链内转账
     */

    @Test
    public void tokenTransferTxOffline() throws Exception {
        String fromAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";
        String privateKey = "9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b";

        // 在线接口(不可跳过，一定要调用的接口) - 获取账户余额信息
        Result accountBalanceR = NulsSDKTool.getAccountBalance(fromAddress, SDKContext.main_chain_id, SDKContext.main_asset_id);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(accountBalanceR), accountBalanceR.isSuccess());
        Map balance = (Map) accountBalanceR.getData();
        BigInteger senderFeeBalance = new BigInteger(balance.get("available").toString());
        String nonce = balance.get("nonce").toString();

        String toAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String contractAddress = "tNULSeBaN7D88SKNPUheSKZeHUwmDCUWBH5JdH";

        int tokenDecimals = 8;
        // 转移token数量
        String tokenAmount = "10.56";
        BigInteger amount = new BigDecimal(tokenAmount).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();

        String methodName = "transfer";
        String methodDesc = "";
        Object[] args = new Object[]{toAddress, amount};

        ImputedGasContractCallForm iForm = new ImputedGasContractCallForm();
        iForm.setSender(fromAddress);
        iForm.setContractAddress(contractAddress);
        iForm.setMethodName(methodName);
        iForm.setMethodDesc(methodDesc);
        iForm.setArgs(args);

        Result iResult = NulsSDKTool.imputedContractCallGas(iForm);
        Assert.assertTrue(JSONUtils.obj2PrettyJson(iResult), iResult.isSuccess());
        Map result = (Map) iResult.getData();
        Long gasLimit = Long.valueOf(result.get("gasLimit").toString());

        Result<Map> map = NulsSDKTool.tokenTransferTxOffline(fromAddress, senderFeeBalance, nonce, toAddress, contractAddress, gasLimit, amount, "tokenTransferTxOffline");
        String txHex = map.getData().get("txHex").toString();
        // 签名
        Result res = NulsSDKTool.sign(txHex, fromAddress, privateKey);
        Map signMap = (Map) res.getData();
        // 在线接口 - 广播交易
        Result<Map> broadcaseTxR = NulsSDKTool.broadcast(signMap.get("txHex").toString());
        Assert.assertTrue(JSONUtils.obj2PrettyJson(broadcaseTxR), broadcaseTxR.isSuccess());
        Map data = broadcaseTxR.getData();
        String hash1 = (String) data.get("hash");
        System.out.println(String.format("hash: %s", hash1));
    }


    //transferToContractTxOffline

}
