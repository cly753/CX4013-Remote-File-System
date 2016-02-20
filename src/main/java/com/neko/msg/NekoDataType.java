package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoDataType {
    TYPE_INT(0),
    TYPE_STR(1);

    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final NekoDataType[] type;

    public static NekoDataType getType(byte typeByte) {
        log.log(Level.FINE, String.format("0x%02X", typeByte));
        return type[typeByte];
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
