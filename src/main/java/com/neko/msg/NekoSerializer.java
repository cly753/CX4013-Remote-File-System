package com.neko.msg;

import java.lang.reflect.Field;
import java.util.InputMismatchException;

public class NekoSerializer {

    public NekoSerializer() {

    }

    /**
     * Serialize data:NekoData to the byteBuffer:NekoBytebuffer.
     * It will serialize attributes annotated with NekoFieldOpcode or NekoFieldAttribute.
     */
    public NekoByteBuffer serialize(NekoData data) {
        NekoByteBuffer byteBuffer = new NekoByteBuffer(NekoSerializer.sizeInByte(data));

        try {
            Class dataClass = data.getClass();
            Field[] attributes = dataClass.getDeclaredFields();
            for (Field field : attributes) {
                NekoFieldOpcode annotationOpcode = field.getAnnotation(NekoFieldOpcode.class);
                if (null != annotationOpcode) {
                    field.setAccessible(true);
                    Object fieldValue = field.getType().cast(field.get(data));
                    serialize((NekoOpcode) fieldValue, byteBuffer);
                }

                NekoFieldAttribute annotationAttribute =
                        field.getAnnotation(NekoFieldAttribute.class);
                if (null != annotationAttribute) {
                    field.setAccessible(true);
                    Object fieldValue = field.getType().cast(field.get(data));

                    // Skip if the attribute is null.
                    if (null == fieldValue) {
                        continue;
                    }

                    if (NekoDataType.INTEGER == annotationAttribute.type()) {
                        serialize(annotationAttribute.attribute(),
                                (Integer) fieldValue,
                                byteBuffer);
                    } else if (NekoDataType.STRING == annotationAttribute.type()) {
                        serialize(annotationAttribute.attribute(),
                                (String) fieldValue,
                                byteBuffer);
                    } else {
                        throw new InputMismatchException();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return byteBuffer;
    }

    /**
     * Serialize opcode to the byteBuffer:NekoByteBuffer.
     */
    private void serialize(NekoOpcode opcode, NekoByteBuffer byteBuffer) {
        byteBuffer.write(opcode);
    }

    /**
     * Serialize opcode to the byteBuffer:NekoByteBuffer.
     */
    private void serialize(NekoAttribute attributeName,
                           Integer attribute,
                           NekoByteBuffer byteBuffer) {
        byteBuffer.write(attributeName);
        byteBuffer.write(attribute);
    }

    /**
     * Serialize opcode to the byteBuffer:NekoByteBuffer.
     */
    private void serialize(NekoAttribute attributeName,
                           String attribute,
                           NekoByteBuffer byteBuffer) {
        byteBuffer.write(attributeName);
        byteBuffer.write(attribute);
    }

    /**
     * Get final final size if data:NekoData is converted to bytes. The size includes the size
     * of attributes annotated with NekoFieldOpcode or NekoFieldAttribute.
     */
    private static int sizeInByte(NekoData data) {
        int totalSize = 0;

        try {
            Class dataClass = data.getClass();
            Field[] attributes = dataClass.getDeclaredFields();
            for (Field field : attributes) {
                NekoFieldOpcode annotationOpcode = field.getAnnotation(NekoFieldOpcode.class);
                if (null != annotationOpcode) {
                    field.setAccessible(true);
                    Object fieldValue = field.getType().cast(field.get(data));
                    totalSize += NekoByteBuffer.sizeInByte((NekoOpcode) fieldValue);
                }

                NekoFieldAttribute annotationAttribute =
                        field.getAnnotation(NekoFieldAttribute.class);
                if (null != annotationAttribute) {
                    field.setAccessible(true);
                    Object fieldValue = field.getType().cast(field.get(data));
                    if (null == fieldValue) {
                        continue;
                    }
                    int size;
                    if (NekoDataType.INTEGER == annotationAttribute.type()) {
                        size = sizeInByte((Integer) fieldValue);
                    } else if (NekoDataType.STRING == annotationAttribute.type()) {
                        size = sizeInByte((String) fieldValue);
                    } else {
                        throw new InputMismatchException();
                    }
                    totalSize += size;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return totalSize;
    }

    /**
     * Get final size of attribute is converted to bytes
     * The size includes 1 byte for attribute name.
     * If the attributes is null standing for the attribute does not exist,
     * it returns 0.
     */
    private static int sizeInByte(Integer attribute) {
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
    private static int sizeInByte(String attribute) {
        if (null == attribute) {
            return 0;
        } else {
            return 1 + NekoByteBuffer.sizeInByte(attribute);
        }
    }
}
