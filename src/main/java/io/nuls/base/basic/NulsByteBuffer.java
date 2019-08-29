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
package io.nuls.base.basic;


import io.nuls.base.data.BaseNulsData;
import io.nuls.base.data.NulsHash;
import io.nuls.base.data.Transaction;
import io.nuls.core.basic.VarInt;
import io.nuls.core.constant.ToolsConstant;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.Log;
import io.nuls.core.model.ByteUtils;
import io.nuls.core.parse.SerializeUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;


/**
 * @author Niels
 */
public class NulsByteBuffer {

    private final byte[] payload;

    private int cursor;

    public NulsByteBuffer(byte[] bytes) {
        this(bytes, 0);
    }

    public NulsByteBuffer(byte[] bytes, int cursor) {
        if (null == bytes || bytes.length == 0 || cursor < 0) {
            throw new RuntimeException();
        }
        this.payload = bytes;
        this.cursor = cursor;
    }

    public long readUint32LE() throws NulsException {
        try {
            long u = SerializeUtils.readUint32LE(payload, cursor);
            cursor += 4;
            return u;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }
    public short readUint8() throws NulsException {
        try {
            short val = SerializeUtils.readUint8LE(payload, cursor);
            cursor += 1;
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public int readUint16() throws NulsException {
        try {
            int val = SerializeUtils.readUint16LE(payload, cursor);
            cursor += 2;
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public int readInt32() throws NulsException {
        try {
            int u = SerializeUtils.readInt32LE(payload, cursor);
            cursor += 4;
            return u;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public long readUint32() throws NulsException {
        try {
            long val = SerializeUtils.readUint32LE(payload, cursor);
            cursor += 4;
            return val;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public long readUint48() {
        long value = SerializeUtils.readUint48(payload, cursor);
        cursor += 6;
        if (value == 281474976710655L) {
            return -1L;
        }
        return value;
    }

    public long readInt64() throws NulsException {
        try {
            long u = SerializeUtils.readInt64LE(payload, cursor);
            cursor += 8;
            return u;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public BigInteger readBigInteger() throws NulsException {
        try {
            byte[] bytes = Arrays.copyOfRange(payload, cursor, cursor += 32);
            BigInteger u = SerializeUtils.bigIntegerFromBytes(bytes);
            if (u.compareTo(BigInteger.ZERO) < 0) {
                throw new NulsException(new UnsupportedOperationException());
            }
            return u;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public long readVarInt() throws NulsException {
        return readVarInt(0);
    }

    public long readVarInt(int offset) throws NulsException {
        try {
            VarInt varint = new VarInt(payload, cursor + offset);
            cursor += offset + varint.getOriginalSizeInBytes();
            return varint.value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public byte readByte() throws NulsException {
        try {
            byte b = payload[cursor];
            cursor += 1;
            return b;
        } catch (IndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public byte[] readBytes(int length) throws NulsException {
        try {
            byte[] b = new byte[length];
            System.arraycopy(payload, cursor, b, 0, length);
            cursor += length;
            return b;
        } catch (IndexOutOfBoundsException e) {
            throw new NulsException(e);
        }
    }

    public byte[] readByLengthByte() throws NulsException {
        long length = this.readVarInt();
        if (length == 0) {
            return null;
        }
        return readBytes((int) length);
    }

    public boolean readBoolean() throws NulsException {
        byte b = readByte();
        return 1 == b;
    }


    public void resetCursor() {
        this.cursor = 0;
    }

    public short readShort() throws NulsException {
        byte[] bytes = this.readBytes(2);
        if (null == bytes) {
            return 0;
        }
        return ByteUtils.bytesToShort(bytes);
    }

    public String readString() throws NulsException {
        try {
            byte[] bytes = this.readByLengthByte();
            if (null == bytes) {
                return "";
            }
            return new String(bytes, ToolsConstant.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            throw new NulsException(e);
        }

    }

    public double readDouble() throws NulsException {
        byte[] bytes = this.readBytes(8);
        if (null == bytes) {
            return 0;
        }
        return ByteUtils.bytesToDouble(bytes);
    }

    public boolean isFinished() {
        return this.payload.length == cursor;
    }

//    public byte[] getPayloadByCursor() {
//        byte[] bytes = new byte[payload.length - cursor];
//        System.arraycopy(this.payload, cursor, bytes, 0, bytes.length);
//        return bytes;
//    }

    public byte[] getPayload() {
        return payload;
    }

    public <T extends BaseNulsData> T readNulsData(T nulsData) throws NulsException {
        if (payload == null) {
            return null;
        }
        int length = payload.length - cursor;
        if (length <= 0) {
            return null;
        }
        if (length >= 4) {
            byte[] byte4 = new byte[4];
            System.arraycopy(payload, cursor, byte4, 0, 4);
            if (Arrays.equals(ToolsConstant.PLACE_HOLDER, byte4)) {
                cursor += 4;
                return null;
            }
        }
        nulsData.parse(this);
        return nulsData;
    }

    public Transaction readTransaction() throws NulsException {
        try {
            Transaction transaction = new Transaction();
            transaction.parse(this);
            return transaction;
        } catch (Exception e) {
            Log.error(e);
            throw new NulsException(e);
        }
    }

    public NulsHash readHash() throws NulsException {
        return new NulsHash(this.readBytes(NulsHash.HASH_LENGTH));
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }
}
