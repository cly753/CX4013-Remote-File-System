package com.neko.msg;

import java.util.InputMismatchException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MsgRead extends Msg {
    private final static Logger log = Logger.getLogger(NekoByteBuffer.class.getName());

    private static final int N_ATTR = 3; // number of attributes
    private String path;
    private int offset;
    private int length;

    public MsgRead(String path, int offset, int length) {
        super(NekoOpcode.READ);
        this.path = path;
        this.offset = offset;
        this.length = length;
    }

    public static MsgRead parseRest(NekoInputStream in) {
        String path = null;
        int offset = -1;
        int length = -1;

        for (int i = 0; i < N_ATTR; i++) {
            NekoAttr attr = NekoAttr.getAttr(in.readOneByte());
            log.log(Level.FINE, String.format(i + ": " + attr));
            switch (attr) {
                case PATH:
                    path = in.readString();
                    break;
                case OFFSET:
                    offset = in.readInt();
                    break;
                case LENGTH:
                    length = in.readInt();
                    break;
                default:
                    throw new InputMismatchException();
            }
        }
        return new MsgRead(path, offset, length);
    }

    @Override
    public byte[] toByte() {
        int totalLen = 1 // type
                + 1 + NekoByteBuffer.sizeInByte(path) // name + path
                + 1 + NekoByteBuffer.sizeInByte(offset) // name + offset
                + 1 + NekoByteBuffer.sizeInByte(length) // name + text
                ;
        NekoByteBuffer out = new NekoByteBuffer(totalLen);

        out.write(super.opcode);
        out.write(NekoAttr.PATH);
        out.write(path);
        out.write(NekoAttr.OFFSET);
        out.write(offset);
        out.write(NekoAttr.LENGTH);
        out.write(length);

        return out.toBytes();
    }

    @Override
    public String toString() {
        return "MsgRead{" +
                "path='" + path + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                '}';
    }
}
