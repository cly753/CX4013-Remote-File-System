package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoDataType {
    INTEGER(0),
    STRING(1);

    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final NekoDataType[] type;

    public static NekoDataType getType(byte bByte) {
        log.log(Level.FINE, String.format("0x%02X", bByte));
        return type[bByte];
    }

    static {
        NekoDataType[] tempTypes = NekoDataType.values();
        type = new NekoDataType[tempTypes.length];
        for (NekoDataType tempType : tempTypes) {
            type[tempType.code] = tempType;
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
