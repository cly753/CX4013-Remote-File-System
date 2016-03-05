package com.neko.monitor;

import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoOpcode;
import com.neko.msg.NekoSerializer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoCallbackServer {
    private static final Logger log = Logger.getLogger(NekoCallbackServer.class.getName());

    /**
     * The port to listen for the incoming callback invocation.
     */
    private int port;
    private int bufferSize;
    private NekoCallback callback;
    private DatagramSocket socket;

    public NekoCallbackServer(int port, int bufferSize, NekoCallback callback) {
        this.port = port;
        this.bufferSize = bufferSize;
        this.callback = callback;
    }

    /**
     * Start to listen invocation.
     */
    public void start() {
        start(0);
    }

    /**
     * Start to listen for a time interval in millisecond.
     */
    public void start(final long interval) {
        try {
            socket = new DatagramSocket(port);
            NekoDeserializer deserializer = new NekoDeserializer();

            byte[] buffer = new byte[bufferSize];
            log.log(Level.FINE, "NekoCallbackServer started.");

            // when server only listens for {interval} milliseconds
            if (interval > 0) {
                final Thread serverThread = Thread.currentThread();
                final DatagramSocket finalSocket = socket;

                // Create and start a Thread to behave as a timer.
                // When {interval} milliseconds is passed,
                // close the socket:DatagramSocket
                // so that socket.receive(...) will throw java.net.SocketException
                // instead of keeping waiting.
                Thread timerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(interval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!finalSocket.isClosed()) {
                            finalSocket.close();
                        }
                    }
                });
                timerThread.start();
            }

            // Keep listening request.
            // When {interval} milliseconds is passed,
            // socket:DatagramSocket will be closed by timerThread:Thread,
            // and socket.receive(...) will then throw java.net.SocketException.
            // By this way, we can stop listening.
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                NekoData request = deserializer.deserialize(requestPacket.getData());
                log.log(Level.FINE, "Request: " + request);
                handle(request);
            }
        } catch (IOException e) {
            // If timerThread:Thread does not close the socket,
            // we need to show the error.
            // (However, it is also possible that socket is closed
            // but not by timerThread:Thread...)
            if (!socket.isClosed()) {
                e.printStackTrace();
            }

            log.log(Level.FINE, "socket.isClosed() = " + socket.isClosed());
        } finally {
            stop();
        }

        log.log(Level.FINE, "NekoCallbackServer stopped.");
    }

    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Handle the update.
     */
    public void handle(NekoData request) {
        if (request.getOpcode() != NekoOpcode.RESULT) {
            return ;
        }

        //
        // some validation
        //

        if (null != callback) {
            callback.invoke(request.getPath(), request.getText(), request.getError());
        }
    }

    public NekoCallback getCallback() {
        return callback;
    }

    public void setCallback(NekoCallback callback) {
        this.callback = callback;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
