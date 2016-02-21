package com.neko.msg;

import java.util.InputMismatchException;

@Deprecated
public class MsgInsert extends Msg {
    private static final int N_ATTR = 3; // number of attributes

    private String path;
    private int offset;
    private String text;

    public MsgInsert(String path, int offset, String text) {
        super(NekoOpcode.INSERT);
        this.path = path;
        this.offset = offset;
        this.text = text;
    }

    public static Msg parseRest(NekoInputStream in) {
        String path = null;
        int offset = -1;
        String text = null;

        for (int i = 0; i < N_ATTR; i++) {
            NekoAttribute attr = NekoAttribute.getAttribute(in.readOneByte());
            switch (attr) {
                case PATH:
                    path = in.readString();
                    break;
                case OFFSET:
                    offset = in.readInt();
                    break;
                case TEXT:
                    text = in.readString();
                    break;
                default:
                    throw new InputMismatchException();
            }
        }

        return new MsgInsert(path, offset, text);
    }

    @Override
    public byte[] toBytes() {
        int totalLen = 1 // type
                + 1 + NekoByteBuffer.sizeInByte(path) // name + path
                + 1 + NekoByteBuffer.sizeInByte(offset) // name + offset
                + 1 + NekoByteBuffer.sizeInByte(text) // name + length
                ;
        NekoByteBuffer out = new NekoByteBuffer(totalLen);

        out.write(super.opcode);
        out.write(NekoAttribute.PATH);
        out.write(path);
        out.write(NekoAttribute.OFFSET);
        out.write(offset);
        out.write(NekoAttribute.TEXT);
        out.write(text);

        return out.toBytes();
    }

    @Override
    public String toString() {
        return "MsgInsert{"
                + "path='" + path + '\''
                + ", offset=" + offset
                + ", text='" + text + '\''
                + '}';
    }
}
