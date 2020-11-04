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
import io.nuls.v2.model.dto.CoinFromDto;
import io.nuls.v2.model.dto.CoinToDto;
import io.nuls.v2.model.dto.TransferDto;
import io.nuls.v2.model.dto.TransferTxFeeDto;
import io.nuls.v2.service.ContractServiceTest;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
    public void createTxWtihSingleAddrTransferOfNonNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "tNULSeBaMrbmG67VrTJeZswv4P2uXXKoFMa6RH";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createTxWtihSingleAddrTransferOfNonNuls(fromAddress, toAddress, 5, 1, amount);
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
    public void createTxWtihSingleAddrTransferOfNuls() throws Exception {
        String fromAddress = "tNULSeBaMfQ6VnRxrCwdU6aPqdiPii9Ks8ofUQ";
        String toAddress = "tNULSeBaMrbmG67VrTJeZswv4P2uXXKoFMa6RH";
        String value = "1.5";
        int tokenDecimals = 8;
        BigInteger amount = new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenDecimals)).toBigInteger();
        Result<Map> result = NulsSDKTool.createTxWtihSingleAddrTransferOfNuls(fromAddress, toAddress, amount);
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

    /** 转账数据 */
    private TransferDto createTransferDto(String fromAddress, String toAddress, int assetChainId, int assetId, BigInteger amount) {
        TransferTxFeeDto feeDto = new TransferTxFeeDto();
        feeDto.setAddressCount(1);
        feeDto.setFromLength(1);
        feeDto.setToLength(1);
        BigInteger fee = NulsSDKTool.calcTransferTxFee(feeDto);

        TransferDto transferDto = new TransferDto();

        List<CoinFromDto> inputs = new ArrayList<>();

        CoinFromDto from = new CoinFromDto();
        from.setAddress(fromAddress);
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
        return transferDto;
    }

    /**
     * 合约资产跨链转账
     */
    @Test
    public void nrc20CrossChain() throws Exception {
        ContractServiceTest contractServiceTest = new ContractServiceTest();
        contractServiceTest.nrc20CrossChainSDK("TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9");
    }


}
