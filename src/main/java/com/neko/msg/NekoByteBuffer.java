package com.neko.msg;

import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoByteBuffer {
    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private byte[] data;
    private int cur;

    public NekoByteBuffer(int size) {
        this.data = new byte[size];
        this.cur = 0;
    }

    private int writeBytes(byte bByte) {
        if (1 + cur > data.length) {
            throw new InputMismatchException();
        }
        data[cur] = bByte;
        cur++;
        return 1;
    }

    private int writeBytes(byte[] bytes) {
        if (bytes.length + cur > data.length) {
            throw new InputMismatchException();
        }
        System.arraycopy(bytes, 0, data, cur, bytes.length);
        cur += bytes.length;
        return bytes.length;
    }

    public int write(int value) {
        int byteNumber = 0;
        byteNumber += write(NekoDataType.TYPE_INT); // type
        byteNumber += writeBytes(getBytes(value)); // data
        return byteNumber;
    }

    public int write(String string) {
        int byteNumber = 0;
        byteNumber += write(NekoDataType.TYPE_STR); // type
        byteNumber += writeBytes(getBytes(string.length())); // length
        byteNumber += writeBytes(getBytes(string)); // data
        return byteNumber;
    }

    public int write(NekoOpcode op) {
        return writeBytes(op.toByte());
    }

    public int write(NekoAttribute attr) {
        return writeBytes(attr.toByte());
    }

    public int write(NekoDataType type) {
        return writeBytes(type.toByte());
    }

    public byte[] toBytes() {
        return data; // return reference
    }

    /**
     * Convert integer value to bytes.
     * It does not add extra attributes such as integer type.
     */
    private static byte[] getBytes(int val) {
        log.log(Level.FINE, String.format("%d", val));
        byte[] ret = new byte[NekoInputStream.INT_LENGTH];
        int mask = 255;
        for (int i = 0; i < NekoInputStream.INT_LENGTH; i++) {
            if (NekoInputStream.BIG_ENDIAN) {
                ret[NekoInputStream.INT_LENGTH - 1 - i] = (byte) (val & mask);
            } else {
                ret[i] = (byte) (val & mask);
            }
            val >>= 8;
        }
        return ret;
    }

    /**
     * Convert string value to bytes.
     * It does not add extra attributes such as string type and string length.
     */
    private static byte[] getBytes(String string) {
        log.log(Level.FINE, String.format("%d %s", string.length(), string));
        return string.getBytes();
    }

    public static int sizeInByte(int val) {
        return 1 + 4; // 1 byte for type + 4 byte for data
    }

    public static int sizeInByte(String string) {
        return 1 + 4 + string.length(); // 1 type + 4 byte for length + data
    }

    public static int sizeInByte(NekoOpcode opcode) {
        return 1;
    }

    public static int sizeInByte(NekoAttribute attribute) {
        return 1;
    }

    public static int sizeInByte(NekoDataType dataType) {
        return 1;
    }
}
