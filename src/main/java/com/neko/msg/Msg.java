//
// Neko Message Format
//      3 bits of op code -> 1 byte
//
//      Format of one attribute
//          4 bit of name: i.e. path, offset, interval, ack, error, text -> 1 byte
//          1 bit of type: String and Integer -> 1 byte
//          Data:
//              if String : 4 byte of integer as length of the String and the rest of the String
//              if Integer: 4 byte of integer
//

package com.neko.msg;

import java.util.InputMismatchException;

public abstract class Msg {

    protected NekoOpcode opcode;

    public Msg(NekoOpcode opcode) {
        this.opcode = opcode;
    }

    public static Msg parse(byte[] b) {
        NekoInputStream in = new NekoInputStream(b);
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

    public abstract byte[] toByte();
}
