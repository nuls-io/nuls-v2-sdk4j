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

import io.nuls.base.basic.AddressTool;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.base.signture.TransactionSignature;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.HexUtil;
import io.nuls.v2.model.dto.TransactionDto;
import io.nuls.v2.util.NulsSDKTool;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: Charlie
 * @date: 2019/12/24
 */
public class TransactionAnalyzeTest {
    String url = "https://api.nuls.io/";

    @Before
    public void before() {
        NulsSDKBootStrap.initMain(url);
    }

    @Test
    public void getTransaction() throws Exception {
        String hash = "568533e38c472f9e0740f318038b49ebe6b59817ce374c6816bf645744beb6ed";
        Result result = NulsSDKTool.getTransaction(hash);
        TransactionDto txDto = (TransactionDto) result.getData();
        String sign = txDto.getTransactionSignature();
        TransactionSignature txSign = new TransactionSignature();
        txSign.parse(new NulsByteBuffer(HexUtil.decode(sign)));
        for(P2PHKSignature p2PHKSignature :  txSign.getP2PHKSignatures()){
           String address = AddressTool.getStringAddressByBytes( AddressTool.getAddress(p2PHKSignature.getPublicKey(), 1));

           boolean rs = ECKey.verify(HexUtil.decode(hash), p2PHKSignature.getSignData().getSignBytes(), p2PHKSignature.getPublicKey());
            System.out.println(address + " - " + rs );
        }
    }
}
