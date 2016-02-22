package com.neko;

import com.neko.msg.*;
import org.junit.Test;

public class MainTest {

    @Test
    public void testMsgRead() {
        String path = "http://www.google.com";
        int offset = 3;
        int length = 5;

        Msg msg = new MsgRead(path, offset, length);
        byte[] bytes = msg.toBytes();
        Msg msgBack = Msg.parse(bytes);

        assert msg.toString().equals(msgBack.toString());
    }

    @Test
    public void testMsgInsert() {
        String path = "http://www.google.com";
        int offset = 3;
        String text = "Hello Google";

        Msg msg = new MsgInsert(path, offset, text);
        byte[] bytes = msg.toBytes();
        Msg msgBack = Msg.parse(bytes);

        assert msg.toString().equals(msgBack.toString());
    }

    @Test
    public void testNekoDeserializer() {
        NekoOpcode opcode = NekoOpcode.INSERT;
        String path = "http://www.google.com";
        int offset = 3;
        String text = "Hello Google";

        NekoData origin = new NekoData();
        origin.setOpcode(opcode);
        origin.setPath(path);
        origin.setOffset(offset);
        origin.setText(text);

        NekoSerializer serializer = new NekoSerializer();

        NekoByteBuffer byteBuffer = serializer.serialize(origin);
        byte[] bytes = byteBuffer.toBytes();

        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData received = deserializer.deserialize(bytes);

        assert origin.toString().equals(received.toString());
    }
}
