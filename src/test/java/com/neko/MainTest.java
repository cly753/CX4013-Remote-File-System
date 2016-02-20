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

        int totalSize = NekoByteBuffer.sizeInByte(opcode)
                + 1
                + NekoByteBuffer.sizeInByte(path)
                + 1
                + NekoByteBuffer.sizeInByte(offset)
                + 1
                + NekoByteBuffer.sizeInByte(text);
        NekoByteBuffer byteBuffer = new NekoByteBuffer(totalSize);
        byteBuffer.write(opcode);
        byteBuffer.write(NekoAttribute.PATH);
        byteBuffer.write(path);
        byteBuffer.write(NekoAttribute.OFFSET);
        byteBuffer.write(offset);
        byteBuffer.write(NekoAttribute.TEXT);
        byteBuffer.write(text);

        byte[] bytes = byteBuffer.toBytes();

        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData received = deserializer.deserialize(bytes);

        assert origin.toString().equals(received.toString());
    }
}
