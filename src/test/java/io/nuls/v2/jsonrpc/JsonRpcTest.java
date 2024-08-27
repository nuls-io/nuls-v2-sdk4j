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
package io.nuls.v2.jsonrpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.base.data.CoinData;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.util.JsonRpcUtil;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: PierreLuo
 * @date: 2019-07-01
 */
public class JsonRpcTest {

    @Test
    public void test() throws JsonProcessingException {
        List<Object> params = new LinkedList<>();
        params.add(2);
        params.add("tNULSeBaN9n5FJ3EYXENEuYwC2ZmnRE1agJffz");
        RpcResult result = JsonRpcUtil.request("getContract", params);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }

    @Test
    public void te1() throws Exception {
        String hex = "0117020001f7351034da5cd539c7550dcc7b3f52475831ecc802000100a067f7050000000000000000000000000000000000000000000000000000000008e2a81a75ce26f58600011702000171302895f74292732dbac8377115406ccc17dbd10200010000e1f505000000000000000000000000000000000000000000000000000000000000000000000000170200011098eb2f1393790d4dedcc65312bb0cfb08c9aff0200010080969800000000000000000000000000000000000000000000000000000000000000000000000000";
        CoinData coinData = new CoinData();
        coinData.parse(HexUtil.decode(hex), 0);
        System.out.println(coinData);
    }
}
