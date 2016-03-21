//
// Neko Message Format
//
// Refer to (Google Docs)
//

package com.neko.msg;

import java.util.Map;

public class NekoData {
    @NekoFieldOpcode
    private NekoOpcode opcode;

    @NekoFieldAttribute(attribute = NekoAttribute.REQUEST_ID, type = NekoDataType.STRING)
    private String requestId;
    @NekoFieldAttribute(attribute = NekoAttribute.PATH, type = NekoDataType.STRING)
    private String path;
    @NekoFieldAttribute(attribute = NekoAttribute.OFFSET, type = NekoDataType.INTEGER)
    private Integer offset;
    @NekoFieldAttribute(attribute = NekoAttribute.INTERVAL, type = NekoDataType.INTEGER)
    private Integer interval;
    @NekoFieldAttribute(attribute = NekoAttribute.TEXT, type = NekoDataType.STRING)
    private String text;
    @NekoFieldAttribute(attribute = NekoAttribute.LENGTH, type = NekoDataType.INTEGER)
    private Integer length;
    @NekoFieldAttribute(attribute = NekoAttribute.NUMBER, type = NekoDataType.INTEGER)
    private Integer number;
    @NekoFieldAttribute(attribute = NekoAttribute.ERROR, type = NekoDataType.STRING)
    private String error;

    /**
     * NULL attributes are not set.
     */
    public NekoData() {

    }

    public NekoOpcode getOpcode() {
        return opcode;
    }

    public String getRequestId() {
        return requestId;
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

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
                + '}';
    }
}
