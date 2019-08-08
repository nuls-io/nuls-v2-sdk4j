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
package io.nuls.base.data;


import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.crypto.Sha256Hash;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.model.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author facjas
 */
public class NulsHash {

    public static final int HASH_LENGTH = 32;

    private byte[] bytes;

    private String hex;

    public NulsHash() {
    }

    public NulsHash(byte[] bytes) {
        this.bytes = bytes;
        this.hex = null;
        if (bytes.length != HASH_LENGTH) {
            throw new RuntimeException("the length is not eq 32 byte");
        }
    }

    public void parse(NulsByteBuffer buffer) throws NulsException {
        this.bytes = buffer.readBytes(HASH_LENGTH);
        this.hex = null;
    }

    public String toHex() {
        if (null == hex) {
            hex = HexUtil.encode(bytes);
        }
        return hex;
    }

    public static NulsHash fromHex(String hex) {
        byte[] bytes = HexUtil.decode(hex);
        return new NulsHash(bytes);
    }

    public static boolean validHash(String hex) {
        try {
            fromHex(hex);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verify() {
        return this.getBytes() != null && this.getBytes().length == HASH_LENGTH;
    }

    public static NulsHash calcHash(BaseNulsData data) {
        try {
            return calcHash(data.serialize());
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public byte[] getBytes() {
        return bytes;
    }


    public static NulsHash calcHash(byte[] data) {
        NulsHash digestData = new NulsHash();
        digestData.bytes = Sha256Hash.hashTwice(data);
        return digestData;
    }

    public static NulsHash calcMerkleHash(List<NulsHash> hashList) {
        List<NulsHash> ddList = new ArrayList<>(hashList);
        int levelOffset = 0;
        for (int levelSize = ddList.size(); levelSize > 1; levelSize = (levelSize + 1) / 2) {
            for (int left = 0; left < levelSize; left += 2) {
                int right = Math.min(left + 1, levelSize - 1);
                byte[] leftBytes = ByteUtils.reverseBytes(ddList.get(levelOffset + left).getBytes());
                byte[] rightBytes = ByteUtils.reverseBytes(ddList.get(levelOffset + right).getBytes());
                byte[] whole = new byte[leftBytes.length + rightBytes.length];
                System.arraycopy(leftBytes, 0, whole, 0, leftBytes.length);
                System.arraycopy(rightBytes, 0, whole, leftBytes.length, rightBytes.length);
                NulsHash digest = NulsHash.calcHash(whole);
                ddList.add(digest);
            }
            levelOffset += levelSize;
        }
        byte[] bytes = ddList.get(ddList.size() - 1).getBytes();
        Sha256Hash merkleHash = Sha256Hash.wrap(bytes);
        NulsHash digestData = new NulsHash();
        digestData.bytes = merkleHash.getBytes();
        return digestData;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof NulsHash)) {
            return false;
        }

        final NulsHash rhs = (NulsHash) obj;
        return Arrays.equals(this.bytes, rhs.getBytes());
    }

    @Override
    public String toString() {
        return toHex();
    }
}
