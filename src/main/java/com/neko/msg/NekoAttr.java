package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoAttr {
    PATH(0),
    OFFSET(1),
    INTERVAL(2),
    ACK(3),
    ERROR(4),
    TEXT(5),
    LENGTH(6);

    private final static Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    public static final NekoAttr[] attr;

    public static NekoAttr getAttr(byte a) {
        log.log(Level.FINE, String.format("0x%02X", a));
        return attr[a];
    }

    static {
        NekoAttr[] tempAttr = NekoAttr.values();
        attr = new NekoAttr[tempAttr.length];
        for (NekoAttr a : tempAttr) {
            attr[a.code] = a;
        }
    }


    private byte code;

    NekoAttr(int code) {
        this.code = (byte)code;
    }

    public byte toByte() {
        log.log(Level.FINE, String.format("%s 0x%02X", this, code));
        return code;
    }
}
