package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoAttribute {
    PATH(0),
    OFFSET(1),
    INTERVAL(2),
    TEXT(3),
    LENGTH(4),

    NUMBER(5),
    ERROR(6);

    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final NekoAttribute[] attributes;

    public static NekoAttribute getAttribute(byte bByte) {
        log.log(Level.FINE, String.format("0x%02X", bByte));
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
        log.log(Level.FINE, String.format("%s 0x%02X", this, code));
        return code;
    }
}
