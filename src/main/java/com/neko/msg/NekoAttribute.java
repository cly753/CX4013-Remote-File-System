package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoAttribute {
    REQUEST_ID(0),
    PATH(1),
    OFFSET(2),
    INTERVAL(3),
    TEXT(4),
    LENGTH(5),

    NUMBER(6),
    ERROR(7);

    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final NekoAttribute[] attributes;

    public static NekoAttribute getAttribute(byte bByte) {
        log.log(Level.FINEST, String.format("0x%02X", bByte));
        return attributes[bByte];
    }

    static {
        NekoAttribute[] tempAttributes = NekoAttribute.values();
        attributes = new NekoAttribute[tempAttributes.length];
        for (NekoAttribute tempAttribute : tempAttributes) {
            attributes[tempAttribute.code] = tempAttribute;
        }
    }

    private byte code;

    NekoAttribute(int code) {
        this.code = (byte)code;
    }

    public byte toByte() {
        log.log(Level.FINEST, String.format("%s 0x%02X", this, code));
        return code;
    }
}
