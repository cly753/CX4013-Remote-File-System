package com.neko.msg;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum NekoOpcode {
    READ(0),
    INSERT(1),
    MONITOR(2),
    COPY(3),
    COUNT(4),
    RESULT(5);

    private static final Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final NekoOpcode[] opcode;

    /**
     * Given a code, return the corresponding {@link NekoOpcode}
     * For example, getOpcode(0) would return {@link #READ} NekoOpcode
     */
    public static NekoOpcode getOpcode(byte op) {
        log.log(Level.FINE, String.format("0x%02X", op));
        return opcode[op];
    }

    static {
        NekoOpcode[] tempOpcode = NekoOpcode.values();
        opcode = new NekoOpcode[tempOpcode.length];
        for (NekoOpcode op : tempOpcode) {
            opcode[op.code] = op;
        }
    }

    private byte code;

    NekoOpcode(int code) {
        this.code = (byte)code;
    }

    public byte toByte() {
        log.log(Level.FINE, String.format("0x%02X", code));
        return code;
    }
}
