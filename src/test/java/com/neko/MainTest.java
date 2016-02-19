package com.neko;

import com.neko.msg.Msg;
import com.neko.msg.MsgInsert;
import com.neko.msg.MsgRead;
import org.junit.Test;

public class MainTest {

    @Test
    public void test1() {

    }

    @Test
    public void testMsgRead() {
        String path = "http://www.google.com";
        int offset = 3;
        int length = 5;

        Msg msg = new MsgRead(path, offset, length);
        byte[] b = msg.toByte();
        Msg msgBack = Msg.parse(b);

        assert msg.toString().equals(msgBack.toString());
    }

    @Test
    public void testMsgInsert() {
        String path = "http://www.google.com";
        int offset = 3;
        String text = "Hello Google";

        Msg msg = new MsgInsert(path, offset, text);
        byte[] b = msg.toByte();
        Msg msgBack = Msg.parse(b);

        assert msg.toString().equals(msgBack.toString());
    }
}
