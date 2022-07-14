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
package io.nuls.v2.consensus;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.nuls.core.io.IoUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.base.RpcBase;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.util.JsonRpcUtil;
import io.nuls.v2.util.ListUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: PierreLuo
 * @date: 2020-03-31
 */
public class ConsensusRpcTest extends RpcBase {
    static List<String> agents = new ArrayList<>();
    static {
        agents.add("84c4bcecf53c807e938b2b672e6b799796d6e3b017b4fcc06c37f7a1123fa5c1");
        agents.add("360d72af0f8df855042b57ba3ce260cc5a81b73adc367b3dc51009f8841b8201");
        agents.add("2e5cb2b2fc3acce78f1d6d71ae1b18f4b58bf153bc3cc537b7fdd3667fd369be");

        // 18485.1
        // 481520.6
    }

    @BeforeClass
    public static void beforeClass() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    }

    /**
     * 设置主网或测试网NULS-API
     */
    @Override
    public String getNulsApiHost() {
        return mainNulsApiHost;
    }

    @Before
    public void before() throws InterruptedException {
        super.before();
    }

    @Test
    public void testyq() throws Exception {
        String path = "/Users/pierreluo/IdeaProjects/imagetool/src/test/resources/yiqing4.txt";
        File file = new File(path);
        String string = IoUtils.readBytesToString(file);
        System.out.println();

        RpcResult result = JsonRpcUtil.request("transfer",
                List.of(2, 1,
                        "tNULSeBaMuddAtExjKRE2GTXWLgcc4VMVi6GzQ",
                        "tNULSeBaMuddAtExjKRE2GTXWLgcc4VMVi6GzQ",
                        "qwer1234",
                        1000000,
                        string));
        System.out.println(JSONUtils.obj2PrettyJson(result));
    }

    @Test
    public void test() {
        for(String agent : agents) {
            RpcResult result = JsonRpcUtil.request(publicServiceHost, "getConsensusNode", ListUtil.of(chainId, agent));
            Map map = (Map) result.getResult();
            //String agentAddress = map.get("agentAddress").toString();
            String totalDeposit = new BigDecimal(map.get("totalDeposit").toString()).movePointLeft(8).toPlainString();
            System.out.println(String.format("agentHash: %s, totalDeposit: %s", agent, totalDeposit));
        }
    }

}
