package com.neko.msg;

import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoByteBuffer {
    private final static Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private byte[] data;
    private int cur;

    public NekoByteBuffer(int size) {
        this.data = new byte[size];
        this.cur = 0;
    }

    public int writeByte(byte b) {
        if (1 + cur > data.length) {
            throw new InputMismatchException();
        }
        data[cur] = b;
        cur++;
        return 1;
    }

    public int writeByte(byte[] b) {
        if (b.length + cur > data.length) {
            throw new InputMismatchException();
        }
        System.arraycopy(b, 0, data, cur, b.length);
        cur += b.length;
        return b.length;
    }

    public int write(int val) {
        int nByte = 0;
        nByte += write(NekoDataType.TYPE_INT); // type
        nByte += writeByte(toByte(val)); // data
        return nByte;
    }

    public int write(String s) {
        int nByte = 0;
        nByte += write(NekoDataType.TYPE_STR); // type
        nByte += writeByte(toByte(s.length())); // length
        nByte += writeByte(toByte(s)); // data
        return nByte;
    }

    public int write(NekoOpcode op) {
        return writeByte(op.toByte());
    }

    public int write(NekoAttr attr) {
        return writeByte(attr.toByte());
    }

    public int write(NekoDataType type) {
        return writeByte(type.toByte());
    }

    public byte[] toByte() {
        return data; // return reference
    }

    private static byte[] toByte(int val) {
        //
        // not include
        // Integer type
        //

        log.log(Level.FINE, String.format("%d", val));
        byte[] ret = new byte[NekoInputStream.INT_LENGTH];
        int mask = 255;
        for (int i = 0; i < NekoInputStream.INT_LENGTH; i++) {
            if (NekoInputStream.BIG_ENDIAN)
                ret[NekoInputStream.INT_LENGTH - 1 - i] = (byte)(val & mask);
            else
                ret[i] = (byte)(val & mask);
            val >>= 8;
        }
        return ret;
    }

    private static byte[] toByte(String s) {
        //
        // not include
        // String type
        // String length
        //

        log.log(Level.FINE, String.format("%d %s", s.length(), s));
        return s.getBytes();
    }

    public static int sizeInByte(int val) {
        return 1 + 4; // 1 byte for type + 4 byte for data
    }

    public static int sizeInByte(String s) {
        return 1 + 4 + s.length(); // 1 type + 4 byte for length + data
    }
}
