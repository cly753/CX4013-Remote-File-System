package com.neko;

import com.neko.monitor.NekoCallbackClient;
import com.neko.monitor.NekoCallbackClientTracker;
import com.neko.msg.*;
import org.junit.Test;

import static org.junit.Assert.*;

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

        assertEquals(origin.toString(), received.toString());
    }

    @Test
    public void testNekoDataResponse() {
        NekoOpcode opcode = NekoOpcode.RESULT;
        String error = "file not found";
        int number = -1;

        NekoData origin = new NekoData();
        origin.setOpcode(opcode);
        origin.setNumber(number);
        origin.setError(error);

        NekoSerializer serializer = new NekoSerializer();

        NekoByteBuffer byteBuffer = serializer.serialize(origin);
        byte[] bytes = byteBuffer.toBytes();

        NekoDeserializer deserializer = new NekoDeserializer();
        NekoData received = deserializer.deserialize(bytes);

        assertEquals(origin.toString(), received.toString());
    }

    @Test
    public void testNekoCallbackClientTracker() {
        byte[] ip = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255};
        int port = 8888;
        String path = "http://www.google.com";
        String text = "Hello Google";
        String error = null;

        NekoCallbackClient client = new NekoCallbackClient(ip, port, -1);
        NekoCallbackClientTracker tracker = new NekoCallbackClientTracker();

        tracker.register(path, client);
        assertTrue(tracker.isRegistered(path, client));
        tracker.deregister(path, client);
        assertFalse(tracker.isRegistered(path, client));
        tracker.informClients(path, text, error);
    }
}
