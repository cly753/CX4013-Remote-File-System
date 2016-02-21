package com.neko.msg;

import java.util.Map;

public class NekoData {
    private NekoOpcode opcode;

    private String path;
    private Integer offset;
    private Integer interval;
    private Integer ack;
    private Integer error;
    private String text;
    private Integer length;

    private Map<String, Object> additionalAttributes;

    /**
     * Leave attributes to null meaning that they are not set
     *
     * TODO
     * Find a smarter solution ?
     */
    public NekoData() {

    }

    public NekoOpcode getOpcode() {
        return opcode;
    }

    public String getPath() {
        return path;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getInterval() {
        return interval;
    }

    public Integer getAck() {
        return ack;
    }

    public Integer getError() {
        return error;
    }

    public String getText() {
        return text;
    }

    public Integer getLength() {
        return length;
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
    /*
    * Auto-generated.
    * */
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
