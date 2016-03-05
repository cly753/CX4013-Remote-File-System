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
            throw new InputMismatchException("Internal byte array is full");
        }
        data[cur] = bByte;
        cur++;
        return 1;
    }

    private int writeBytes(byte[] bytes) {
        if (bytes.length + cur > data.length) {
            throw new InputMismatchException("Internal byte array is full");
        }
        System.arraycopy(bytes, 0, data, cur, bytes.length);
        cur += bytes.length;
        return bytes.length;
    }

    public int write(int value) {
        int byteNumber = 0;
        byteNumber += write(NekoDataType.INTEGER); // type
        byteNumber += writeBytes(getBytes(value)); // data
        return byteNumber;
    }

    public int write(String string) {
        int byteNumber = 0;
        byteNumber += write(NekoDataType.STRING); // type
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

    public int writeEOF() {
        return writeBytes(NekoIOConstants.EOF);
    }

    public byte[] toBytes() {
        return data; // return reference
    }

    /**
     * Convert integer value to bytes.
     * It does not add extra attributes such as integer type.
     */
    private static byte[] getBytes(int val) {
        log.log(Level.FINEST, String.format("%d", val));
        byte[] ret = new byte[NekoIOConstants.INT_LENGTH];
        for (int i = 0; i < NekoIOConstants.INT_LENGTH; i++) {
            if (NekoIOConstants.BIG_ENDIAN) {
                ret[NekoIOConstants.INT_LENGTH - 1 - i] = (byte) val;
            } else {
                ret[i] = (byte) val;
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
        log.log(Level.FINEST, String.format("%d %s", string.length(), string));
        return string.getBytes();
    }

    public static int sizeInByte(int val) {
        // 1 byte for type + 4 byte for data
        return sizeInByte(NekoDataType.INTEGER) + NekoIOConstants.INT_LENGTH;
    }

    public static int sizeInByte(String string) {
        // 1 type + 4 byte for length + data
        return sizeInByte(NekoDataType.STRING) + NekoIOConstants.INT_LENGTH + string.length();
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

    public static int sizeEOF() {
        return 1;
    }
}
