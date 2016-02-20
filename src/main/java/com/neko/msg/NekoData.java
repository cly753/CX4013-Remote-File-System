package com.neko.msg;

import java.util.Map;

public class NekoData {
    private NekoOpcode opcode;

    private String path;
    private int offset;
    private int interval;
    private int ack;
    private int error;
    private String text;
    private int length;

    private Map<String, Object> additionalAttributes;

    public NekoData() {
        //
        // TODO
        // initialize all attributes to
        // "invalid"
        // ?
        //
    }

    public void setOpcode(NekoOpcode opcode) {
        this.opcode = opcode;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setAck(int ack) {
        this.ack = ack;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "NekoData{"
                + "opcode=" + opcode
                +", path='" + path + '\''
                +", offset=" + offset
                + ", interval=" + interval
                + ", ack=" + ack
                + ", error=" + error
                + ", text='" + text + '\''
                + ", length=" + length
                + ", additionalAttributes=" + additionalAttributes
                + '}';
    }
}
