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
package io.nuls.v2.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.basic.Result;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.SDKContext;
import io.nuls.v2.model.dto.ContractValidateCallForm;
import io.nuls.v2.model.dto.ImputedGasContractCallForm;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.model.dto.RpcResultError;
import io.nuls.v2.util.JsonRpcUtil;
import io.nuls.v2.util.ListUtil;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Map;

import static io.nuls.v2.constant.Constant.CONTRACT_MINIMUM_PRICE;
import static io.nuls.v2.constant.Constant.MAX_GASLIMIT;

/**
 * @author: PierreLuo
 * @date: 2020-02-27
 */
public abstract class RpcBase {

    protected String mainNulsApiHost = "https://api.nuls.io/";
    protected String testNulsApiHost = "http://beta.api.nuls.io/";

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String invokeAddress;
    private String privateKey;
    private String tokenAirdropContract;
    // 0.1个
    protected String airdropTokenAmount = "1000000";
    protected String publicServiceHost;
    protected int chainId;
    protected int assetId;
    protected String tokenContract;
    protected String txRemark;

    public abstract String getNulsApiHost();

    public void before() throws InterruptedException {
        SDKContext.wallet_url = getNulsApiHost();
        RpcResult info = JsonRpcUtil.request("info", ListUtil.of());
        Map result = (Map) info.getResult();
        chainId = (Integer) result.get("chainId");
        assetId = (Integer) result.get("assetId");
        // initial SDK
        NulsSDKBootStrap.init(chainId, getNulsApiHost());
        // 主网配置
        if (chainId == 1) {
            publicServiceHost = "https://public1.nuls.io/";
            invokeAddress = "NULSd6HgZ8xEbCKo9J5MwgJYVy9F3Cpzvh2GY";
            privateKey = "";
            tokenContract = "";
            tokenAirdropContract = "NULSd6HgrdJTSeuiHnXqD6CFiNuE9ULfEj4NP";
        }
        // 测试网配置
        else if (chainId == 2) {
            publicServiceHost = "http://beta.public1.nuls.io/";
            invokeAddress = "tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG";
            privateKey = "9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b";
            tokenContract = "tNULSeBaNAVKdxePZHVfpLoCR3QYov9yjFRgdh";
            tokenAirdropContract = "tNULSeBaMyNU4JYK3K4DRQFxEwNAzu62UphVWT";
        } else {
            throw new RuntimeException(String.format("Unkonw chainId: %s", chainId));
        }
    }

}
