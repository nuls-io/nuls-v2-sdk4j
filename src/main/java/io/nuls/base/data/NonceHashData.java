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

package io.nuls.base.data;

/**
 * 本地发交易时缓存交易hash并且记录时间,
 * 下次发起交易计算nonce值时,
 * 如果已过期则nonce值从账本中获取
 *
 * @author: Charlie
 * @date: 2019/3/11
 */
public class NonceHashData {

    /**
     * 交易hash
     */
    private NulsHash hash;

    /**
     * hash值缓存时的时间戳
     */
    private long cacheTimestamp;

    public NonceHashData() {
    }

    public NonceHashData(NulsHash hash, long cacheTimestamp) {
        this.hash = hash;
        this.cacheTimestamp = cacheTimestamp;
    }

    public NulsHash getHash() {
        return hash;
    }

    public void setHash(NulsHash hash) {
        this.hash = hash;
    }

    public long getCacheTimestamp() {
        return cacheTimestamp;
    }

    public void setCacheTimestamp(long cacheTimestamp) {
        this.cacheTimestamp = cacheTimestamp;
    }
}
