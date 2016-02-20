package com.neko.msg;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoInputStream {
    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    public static final int INT_LENGTH = 4;

    /**
     * Big Endian: MSB at lowest memory address
     * TODO(cly753): when will it be false?
     */
    public static final boolean BIG_ENDIAN = true;

    private byte[] data;
    private int cur;

    public NekoInputStream(byte[] data) {
        this.data = data;
        this.cur = 0;
    }

    public byte readOneByte() {
        if (cur + 1 > data.length) {
            throw new InputMismatchException();
        }
        byte ret = data[cur];
        log.log(Level.FINE, String.format("data[%d]=0x%02X", cur, ret));
        cur++;
        return ret;
    }

    public byte[] readByte(int length) {
        if (cur + length > data.length) {
            throw new InputMismatchException();
        }
        byte[] ret = Arrays.copyOfRange(data, cur, cur + length);

        for (int i = 0; i < ret.length; i++) {
            log.log(Level.FINE, String.format("ret[%d]=0x%02X", i, ret[i]));
        }
        cur += length;
        return ret;
    }

    public int readInt() {
        NekoDataType type = NekoDataType.getType(readOneByte());
        if (type != NekoDataType.TYPE_INT) {
            throw new InputMismatchException();
        }
        return convertInt(readByte(INT_LENGTH));
    }

    public String readString() {
        NekoDataType type = NekoDataType.getType(readOneByte());
        if (type != NekoDataType.TYPE_STR) {
            throw new InputMismatchException();
        }
        int len = convertInt(readByte(INT_LENGTH));
        return convertString(readByte(len));
    }

    public static int convertInt(byte[] bytes) {
        if (bytes.length != INT_LENGTH) {
            throw new InputMismatchException();
        }
        int val = 0;
        for (int i = 0; i < INT_LENGTH; i++) {
            val <<= 8;
            if (BIG_ENDIAN) {
                val |= bytes[i];
            } else {
                val |= bytes[INT_LENGTH - 1 - i];
            }
        }
        log.log(Level.FINE, String.format("%d", val));
        return val;
    }

    public static String convertString(byte[] bytes) {
        log.log(Level.FINE, String.format("%d %s", new String(bytes).length(), new String(bytes)));
        return new String(bytes);
    }

}
