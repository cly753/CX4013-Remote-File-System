package com.neko;

import com.neko.cli.Neko;
import com.neko.msg.*;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainTest {

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

    @Test
    public void testNekoDataResponse() {
        NekoOpcode opcode = NekoOpcode.RESULT;
        int response = 404;
        String error = "not found";
        int number = -1;

        NekoData origin = new NekoData();
        origin.setOpcode(opcode);
        origin.setResponse(response);
        origin.setNumber(number);
        origin.setError(error);

        NekoSerializer serializer = new NekoSerializer();

        NekoByteBuffer byteBuffer = serializer.serialize(origin);
        byte[] bytes = byteBuffer.toBytes();
        for (int i = 0; i < bytes.length; i++)
            System.out.printf("%2d: 0x%02X\n", i, bytes[i]);

        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData received = deserializer.deserialize(bytes);

        System.out.println(origin);
        System.out.println(received);
        assert origin.toString().equals(received.toString());
    }
}
