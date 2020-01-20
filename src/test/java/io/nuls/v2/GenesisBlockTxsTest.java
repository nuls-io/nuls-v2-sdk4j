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
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.v2.model.dto.RpcResult;
import io.nuls.v2.util.JsonRpcUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试网映射余额和别名, 生成创世块文件交易列表
 *
 * @author: Charlie
 * @date: 2020/1/20
 */
public class GenesisBlockTxsTest {
    String url = "https://beta.wallet.nuls.io/api";

    @Before
    public void before() {
        NulsSDKBootStrap.initMain(url);
    }

    @Test
    public void getBetaAccountData() throws JsonProcessingException {
        int page = 1;
        List<AccBalance> accBalanceList = new ArrayList<>();
        List<AccAlias> accAliasList = new ArrayList<>();
        while (true) {
            RpcResult rpcResult = JsonRpcUtil.request(url, "getCoinRanking", List.of(2, page, 1000));
            Map<String, Object> mapResult = (Map<String, Object>) rpcResult.getResult();
            List<Map<String, Object>> list = (List) mapResult.get("list");
            if (list.isEmpty()) {
                break;
            }
            for (Map<String, Object> map : list) {
                String address = (String) map.get("address");
                BigInteger amount = new BigInteger(map.get("totalBalance").toString());
//                long lockTime = (long) map.get("lockTime");
                String alias = (String) map.get("alias");
                if(amount.compareTo(new BigInteger("100000000")) > 0) {
                    accBalanceList.add(new AccBalance(address, amount, 0));
                }
                if(StringUtils.isNotBlank(alias)) {
                    accAliasList.add(new AccAlias(address, alias));
                }
            }
            page++;
        }

        Map<String, List> rsMap = new HashMap<>();
        rsMap.put("alias", accAliasList);
        rsMap.put("txs", accBalanceList);
        System.out.println("accountSize:" + accBalanceList.size() + "; aliasSize:" + accAliasList.size());
        System.out.println(JSONUtils.obj2PrettyJson(rsMap));
    }

    class AccBalance {
        String address;
        BigInteger amount;
        long lockTime;

        public AccBalance(String address, BigInteger amount, long lockTime) {
            this.address = address;
            this.amount = amount;
            this.lockTime = lockTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public void setAmount(BigInteger amount) {
            this.amount = amount;
        }

        public long getLockTime() {
            return lockTime;
        }

        public void setLockTime(long lockTime) {
            this.lockTime = lockTime;
        }
    }

    class AccAlias {
        String address;
        String alias;

        public AccAlias(String address, String alias) {
            this.address = address;
            this.alias = alias;
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
    }
}
