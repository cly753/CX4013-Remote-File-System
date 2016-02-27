//
// Neko Message Format
//
// Refer to (Google Docs)
//

package com.neko.msg;

import java.util.Map;

public class NekoData {
    private NekoOpcode opcode;

    private String path;
    private Integer offset;
    private Integer interval;
    private String text;
    private Integer length;
    private Integer number;
    private String error;

    private Map<String, Object> additionalAttributes;

    /**
     * NULL attributes are not set.
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

    public String getText() {
        return text;
    }

    public Integer getLength() {
        return length;
    }

    public Integer getNumber() {
        return number;
    }

    public String getError() {
        return error;
    }

    public void setOpcode(NekoOpcode opcode) {
        this.opcode = opcode;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "NekoData{"
                + "opcode=" + opcode
                + ", path='" + path + '\''
                + ", offset=" + offset
                + ", interval=" + interval
                + ", text='" + text + '\''
                + ", length=" + length
                + ", number=" + number
                + ", error='" + error + '\''
                + ", additionalAttributes=" + additionalAttributes
                + '}';
    }
}
