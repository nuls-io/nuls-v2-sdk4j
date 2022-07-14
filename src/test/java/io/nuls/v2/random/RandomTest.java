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
package io.nuls.v2.random;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.nuls.v2.base.RpcBase;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.util.ContractUtil;
import io.nuls.v2.util.JsonRpcUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: PierreLuo
 * @date: 2020-04-15
 */
public class RandomTest extends RpcBase {

    @BeforeClass
    public static void beforeClass() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    }

    @Before
    public void before() throws InterruptedException {
        super.before();
    }

    /**
     * 设置主网或测试网NULS-API
     */
    @Override
    public String getNulsApiHost() {
        return testNulsApiHost;
    }

    @Test
    public void test() {
        for(int i=0;i<100;i++) {
            RpcResult request = JsonRpcUtil.request("getRandomSeedByCount", List.of(2, 740892 - i * 10, 5, "sha3"));
            Map map = (Map) request.getResult();
            String seed = new BigInteger(map.get("seed").toString()).abs().toString();
            System.out.println("0".repeat(77 - seed.length()) + seed);
        }
    }

}
