/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nuls.base.signture;

import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.basic.NulsOutputStreamBuffer;
import io.nuls.base.data.BaseNulsData;
import io.nuls.base.data.NulsHash;
import io.nuls.base.data.NulsSignData;
import io.nuls.core.basic.Result;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.exception.NulsException;
import io.nuls.core.parse.SerializeUtils;

import java.io.IOException;

public class BlockSignature extends BaseNulsData {
    private NulsSignData signData;
    private byte[] publicKey;

    /**
     * serialize important field
     */
    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.write(publicKey.length);
        stream.write(publicKey);
        stream.writeNulsData(signData);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        int length = byteBuffer.readByte();
        this.publicKey = byteBuffer.readBytes(length);
        this.signData = new NulsSignData();
        this.signData.parse(byteBuffer);
    }

    @Override
    public int size() {
        int size = 1 + publicKey.length;
        size += SerializeUtils.sizeOfNulsData(signData);
        return size;
    }

    public Result verifySignature(NulsHash digestData) {
        boolean b = ECKey.verify(digestData.getBytes(), signData.getSignBytes(), publicKey);
        if (b) {
            return new Result(true);
        } else {
            return new Result(false);
        }
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public NulsSignData getSignData() {
        return signData;
    }

    public void setSignData(NulsSignData signData) {
        this.signData = signData;
    }
}
