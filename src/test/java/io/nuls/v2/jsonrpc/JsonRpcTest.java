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
        params.add(1);
        params.add("NULSd6HgntyX6aBo9ipFSxh9v7Tp2JZmG4rSA");
        RpcResult result = JsonRpcUtil.request("getContract", params);
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }
}
