package com.neko.msg;

public class NekoIOConstants {
    private NekoIOConstants() {}

    public static final int INT_LENGTH = 4;

    /**
     * Big Endian: MSB at lowest memory address
     */
    public static final boolean BIG_ENDIAN = true;

    public static final byte EOF = (byte) 255;
}
