package com.neko.simulation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnstableDatagramSocket extends DatagramSocket {
    private static final Logger log = Logger.getLogger(UnstableDatagramSocket.class.getName());

    public final double RECEIVE_FAIL_PROB = 0.5;
    public final double SEND_FAIL_PROB = 0.5;

    private final Random ran = new Random();

    public UnstableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        if (dropSend()) {
            log.log(Level.ALL, "Drop send.");
            return;
        }
        else {
            log.log(Level.ALL, "Send.");
            super.send(p);
        }
    }

    @Override
    public void receive(DatagramPacket p) throws IOException {
        if (dropReceive()) {
            log.log(Level.ALL, "Drop receive.");
            return;
        }
        else {
            log.log(Level.ALL, "Receive.");
            super.receive(p);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    private boolean dropSend() {
        return ran.nextDouble() > SEND_FAIL_PROB;
    }

    private boolean dropReceive() {
        return ran.nextDouble() > RECEIVE_FAIL_PROB;
    }
}
