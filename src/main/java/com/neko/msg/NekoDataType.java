package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoDataType {
    TYPE_INT(0),
    TYPE_STR(1);

    private final static Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    public static final NekoDataType[] type;

    public static NekoDataType getType(byte t) {
        log.log(Level.FINE, String.format("0x%02X", t));
        return type[t];
    }

    static {
        NekoDataType[] tempType = NekoDataType.values();
        type = new NekoDataType[tempType.length];
        for (NekoDataType t : tempType) {
            type[t.code] = t;
        }
    }

    private byte code;

    NekoDataType(int code) {
        this.code = (byte)code;
    }

    public byte toByte() {
        log.log(Level.FINE, String.format("0x%02X", code));
        return code;
    }
}
