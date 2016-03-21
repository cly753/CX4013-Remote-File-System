package com.neko;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.neko.monitor.NekoCallback;
import com.neko.monitor.NekoCallbackClient;
import com.neko.monitor.NekoCallbackClientTracker;
import com.neko.monitor.NekoCallbackServer;
import com.neko.msg.NekoByteBuffer;
import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoOpcode;
import com.neko.msg.NekoSerializer;
import org.junit.Test;

import java.net.UnknownHostException;

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
    public void testNekoCallbackClientTracker() throws UnknownHostException {
        String host = "localhost";
        int port = 8888;
        String path = "http://www.google.com";

        NekoCallbackClient client = new NekoCallbackClient(host, port, -1);
        NekoCallbackClientTracker tracker = new NekoCallbackClientTracker();

        tracker.register(path, client);
        assertTrue(tracker.isRegistered(path, client));
        tracker.deregister(path, client);
        assertFalse(tracker.isRegistered(path, client));

        String text = "Hello Google";
        String error = null;
        tracker.informUpdate(path, text, error);
    }

    @Test
    public void testEOF() {
        NekoData request = new NekoData();
        request.setOpcode(NekoOpcode.READ);
        request.setPath("./UDPServer.java");
        request.setOffset(1);
        request.setLength(10);

        NekoSerializer serializer = new NekoSerializer();
        NekoDeserializer deserializer = new NekoDeserializer();

        byte[] bytes = serializer.serialize(request).toBytes();
        byte[] longerBytes = new byte[bytes.length + 100];
        System.arraycopy(bytes, 0, longerBytes, 0, bytes.length);

        NekoData reply = deserializer.deserialize(longerBytes);

        assertEquals(request.toString(), reply.toString());
    }

    @Test
    public void testNekoCallbackClient() throws UnknownHostException, InterruptedException {
        final String host = "localhost";
        final int port = 8888;
        String[] paths = {
                "/Users/cly/Dropbox/code/Neko/src/main/java/com/neko/UDPServer.java",
                "/Users/cly/Dropbox/code/Neko/src/main/java/com/neko/UDPClient.java"
        };
        String[] texts = {
                "Hello Google",
                "Hello Bing"
        };
        String error = null;

        long[] runFor = {
                1000 * 10
        };

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NekoCallbackServer callbackServer = new NekoCallbackServer(port, 100, new NekoCallback() {
                    @Override
                    public void invoke(String path, String text, String error) {
                        System.out.println(
                                "NekoCallbackServer NekoCallback"
                                        + "\n\tpath: " + path
                                        + "\n\ttext: " + text
                                        + "\n\terror: " + error
                        );
                    }

                    @Override
                    public boolean isValid() {
                        return true;
                    }
                });

                callbackServer.start(1000 * 6);
            }
        });
        serverThread.start();

        NekoCallbackClientTracker tracker = new NekoCallbackClientTracker();
        tracker.register(paths[0], new NekoCallbackClient(host, port, System.currentTimeMillis() + runFor[0]));

        for (int k = 0; k < 3; k++) {
            Thread.sleep(1000 * 4);
            tracker.informUpdate(paths[0], texts[0], error);
            System.out.println("NekoCallbackClientTracker informed");
        }
    }
}
