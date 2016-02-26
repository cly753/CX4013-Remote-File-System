package com.neko.msg;

import java.util.InputMismatchException;

@Deprecated
public abstract class Msg {

    protected NekoOpcode opcode;

    public Msg(NekoOpcode opcode) {
        this.opcode = opcode;
    }

    public static Msg parse(byte[] bytes) {
        NekoInputStream in = new NekoInputStream(bytes);
        NekoOpcode opcode = NekoOpcode.getOpcode(in.readOneByte());

        Msg msg;
        switch (opcode) {
            case READ:
                msg = MsgRead.parseRest(in);
                break;
            case INSERT:
                msg = MsgInsert.parseRest(in);
                break;
            default:
                throw new InputMismatchException();
        }
        return msg;
    }

    public abstract byte[] toBytes();
}
