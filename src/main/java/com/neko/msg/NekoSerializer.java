package com.neko.msg;

public class NekoSerializer {

    private NekoByteBuffer byteBuffer;

    public NekoSerializer(NekoByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * Serialize data:NekoData to the byteBuffer:NekoBytebuffer.
     * It will serialize all attributes of NekoData.
     *
     * TODO
     * Hardcoded all attributes for now.
     */
    public void serialize(NekoData data) {
        serialize(data.getOpcode());
        trySerialize(NekoAttribute.PATH, data.getPath());
        trySerialize(NekoAttribute.OFFSET, data.getOffset());
        trySerialize(NekoAttribute.INTERVAL, data.getInterval());
        trySerialize(NekoAttribute.ACK, data.getAck());
        trySerialize(NekoAttribute.ERROR, data.getError());
        trySerialize(NekoAttribute.TEXT, data.getText());
        trySerialize(NekoAttribute.LENGTH, data.getLength());
    }

    /**
     * Serialize opcode to the byteBuffer:NekoByteBuffer.
     */
    public void serialize(NekoOpcode opcode) {
        byteBuffer.write(opcode);
    }

    /**
     * Try to serialize an attribute.
     * If the attribute is null, it will do nothing.
     * Otherwise, it will serialize the attribute's name and data
     * to the byteBuffer:NekoByteBuffer.
     */
    public void trySerialize(NekoAttribute attributeName, Integer attribute) {
        if (null != attribute) {
            byteBuffer.write(attributeName);
            byteBuffer.write(attribute);
        }
    }

    /**
     * Try to serialize an attribute.
     * If the attribute is null, it will do nothing.
     * Otherwise, it will serialize the attribute's name and data
     * to the byteBuffer:NekoByteBuffer.
     */
    public void trySerialize(NekoAttribute attributeName, String attribute) {
        if (null != attribute) {
            byteBuffer.write(attributeName);
            byteBuffer.write(attribute);
        }
    }

    /**
     * Get final final size if data:NekoData is converted to bytes.
     * The size includes the size of NekoOpcode and all attributes.
     *
     * TODO
     * Hardcoded all attributes for now.
     */
    public static int sizeInByte(NekoData data) {
        return NekoByteBuffer.sizeInByte(data.getOpcode())
                + sizeInByte(data.getPath())
                + sizeInByte(data.getOffset())
                + sizeInByte(data.getInterval())
                + sizeInByte(data.getAck())
                + sizeInByte(data.getError())
                + sizeInByte(data.getText())
                + sizeInByte(data.getLength());
    }

    /**
     * Get final size of attribute is converted to bytes
     * The size includes 1 byte for attribute name.
     * If the attributes is null standing for the attribute does not exist,
     * it returns 0.
     */
    public static int sizeInByte(Integer attribute) {
        if (null == attribute) {
            return 0;
        } else {
            return 1 + NekoByteBuffer.sizeInByte(attribute);
        }

    }

    /**
     * Get final size of attribute is converted to bytes
     * The size includes 1 byte for attribute name.
     * If the attributes is null standing for the attribute does not exist,
     * it returns 0.
     */
    public static int sizeInByte(String attribute) {
        if (null == attribute) {
            return 0;
        } else {
            return 1 + NekoByteBuffer.sizeInByte(attribute);
        }
    }
}
