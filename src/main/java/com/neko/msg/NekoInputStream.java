package com.neko.msg;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoInputStream {
    private static final Logger log = Logger.getLogger(NekoInputStream.class.getName());

    private byte[] data;
    private int cur;

    public NekoInputStream(byte[] data) {
        this.data = data;
        this.cur = 0;
    }

    public byte readOneByte() {
        if (cur + 1 > data.length) {
            throw new InputMismatchException("No enough bytes to read");
        }
        byte ret = data[cur];
        log.log(Level.FINEST, String.format("data[%d]=0x%02X", cur, ret));
        cur++;
        return ret;
    }

    public byte[] readBytes(int length) {
        if (cur + length > data.length) {
            throw new InputMismatchException("No enough bytes to read");
        }
        byte[] ret = Arrays.copyOfRange(data, cur, cur + length);

        for (int i = 0; i < ret.length; i++) {
            log.log(Level.FINEST, String.format("ret[%d]=0x%02X", i, ret[i]));
        }
        cur += length;
        return ret;
    }

    public int readInt() {
        NekoDataType type = readDataType();
        if (type != NekoDataType.INTEGER) {
            throw new InputMismatchException(
                    "Try to read Integer while NekoDataType is not INTEGER");
        }
        return convertInt(readBytes(NekoIOConstants.INT_LENGTH));
    }

    public String readString() {
        NekoDataType type = readDataType();
        if (type != NekoDataType.STRING) {
            throw new InputMismatchException("Try to read String while NekoDataType is not STRING");
        }
        int len = convertInt(readBytes(NekoIOConstants.INT_LENGTH));
        return convertString(readBytes(len));
    }

    public NekoOpcode readOpcode() {
        return NekoOpcode.getOpcode(readOneByte());
    }

    public NekoAttribute readAttribute() {
        return NekoAttribute.getAttribute(readOneByte());
    }

    public NekoDataType readDataType() {
        return NekoDataType.getType(readOneByte());
    }

    public boolean hasNext() {
        if (cur >= data.length) {
            throw new InputMismatchException("Byte pointer goes out of bound.");
        }
        return data[cur] != NekoIOConstants.EOF;
    }

    public static int convertInt(byte[] bytes) {
        if (bytes.length != NekoIOConstants.INT_LENGTH) {
            throw new InputMismatchException(
                    "Number of bytes provided does not match NekoIOConstants.INT_LENGTH");
        }
        int val = 0;
        for (int i = 0; i < NekoIOConstants.INT_LENGTH; i++) {
            val <<= 8;
            if (NekoIOConstants.BIG_ENDIAN) {
                val |= Byte.toUnsignedInt(bytes[i]);
            } else {
                val |= Byte.toUnsignedInt(bytes[NekoIOConstants.INT_LENGTH - 1 - i]);
            }
        }
        log.log(Level.FINEST, String.format("%d", val));
        return val;
    }

    public static String convertString(byte[] bytes) {
        log.log(Level.FINEST, String.format("%d %s", new String(bytes).length(), new String(bytes)));
        return new String(bytes);
    }
}
