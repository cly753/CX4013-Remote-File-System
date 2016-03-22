package com.neko.simulation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnstableDatagramSocket extends DatagramSocket {
    private static final Logger log = Logger.getLogger(UnstableDatagramSocket.class.getName());

    public final double DROP_RECEIVE_PROB = 0.5;
    public final double DROP_SEND_PROB = 0.5;

    public final Queue<Boolean> receiveSequence = new LinkedList<>();
    public final Queue<Boolean> sendSequence = new LinkedList<>();

    private final Random ran = new Random();

    public UnstableDatagramSocket(int port) throws SocketException {
        super(port);
    }

    /**
     * receiveSequence: "00110"
     *      means:
     *          for receive calls: [drop, drop, not drop, not drop, drop]
     * sendSequence: "00110"
     *      means:
     *          for send calls: [drop, drop, not drop, not drop, drop]
     */
    public UnstableDatagramSocket(int port, String receiveSequence, String sendSequence) throws SocketException {
        super(port);
        addSequence(this.receiveSequence, receiveSequence);
        addSequence(this.sendSequence, sendSequence);
    }

    private void addSequence(Queue<Boolean> sequence, String sequenceString) {
        if (sequence != null) {
            for (char c : sequenceString.toCharArray()) {
                if (c == '0')
                    sequence.add(false);
                else
                    sequence.add(true);
            }
        }
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
        if (!sendSequence.isEmpty())
            return sendSequence.poll();
        return ran.nextDouble() < DROP_SEND_PROB;
    }

    private boolean dropReceive() {
        if (!receiveSequence.isEmpty())
            return receiveSequence.poll();
        return ran.nextDouble() < DROP_RECEIVE_PROB;
    }
}
