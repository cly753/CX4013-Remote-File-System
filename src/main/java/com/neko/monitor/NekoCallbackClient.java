package com.neko.monitor;

import com.neko.msg.NekoData;
import com.neko.msg.NekoOpcode;
import com.neko.msg.NekoSerializer;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Each NekoCallbackClient associates with one client
 * which is listening to the update of path at server
 */
public class NekoCallbackClient implements NekoCallback {
    private static final Logger log = Logger.getLogger(NekoCallbackClient.class.getName());

    private InetAddress host;
    private int port;

    /**
     * System time in milliseconds.
     */
    private long validUntil;

    public NekoCallbackClient(byte[] host, int port, long validUntil) throws UnknownHostException {
        this.host = InetAddress.getByAddress(host);
        this.port = port;
        this.validUntil = validUntil;
    }

    public NekoCallbackClient(String host, int port, long validUntil) throws UnknownHostException {
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.validUntil = validUntil;
    }

    /**
     * Invoke the callback at client side
     * with the update.
     */
    @Override
    public void invoke(String path, String text, String error) {
        NekoData request = new NekoData();
        request.setOpcode(NekoOpcode.RESULT);
        request.setPath(path);
        request.setText(text);
        request.setError(error);

        invokeRemote(request);
    }

    /**
     * Do the actual network operation.
     * Send request containing the update to client.
     */
    private void invokeRemote(NekoData request) {
        //
        // send to client
        // and
        // perform retransmit etc.
        //

        log.log(Level.FINE, "sending request to " + host.getCanonicalHostName() + ":" + port);
        try {
            NekoSerializer serializer = new NekoSerializer();
            byte[] requestBytes = serializer.serialize(request).toBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, host, port);
            socket.send(requestPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return whether the request listening interval has passed.
     */
    @Override
    public boolean isValid() {
        return validUntil > System.currentTimeMillis();
    }

    /**
     * Only compare IP and Port
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        NekoCallbackClient that = (NekoCallbackClient) obj;

        if (port != that.port) {
            return false;
        }

        return Arrays.equals(host.getAddress(), that.host.getAddress());
    }

    @Override
    public java.lang.String toString() {
        return "NekoCallbackClient{"
                + "host=" + host.getCanonicalHostName()
                + ", port=" + port
                + ", validUntil=" + validUntil
                + '}';
    }

    public InetAddress getHost() {
        return host;
    }

    public void setIp(InetAddress host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }
}
