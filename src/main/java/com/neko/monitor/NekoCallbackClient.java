package com.neko.monitor;

import com.neko.msg.NekoData;
import com.neko.msg.NekoOpcode;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Each NekoCallbackClient associates with one client
 * which is listening to the update of path at server
 */
public class NekoCallbackClient implements NekoCallback {
    private static final Logger log = Logger.getLogger(NekoCallbackServer.class.getName());

    private byte[] ip;
    private int port;

    /**
     * System time in milliseconds.
     */
    private long validUntil;

    public NekoCallbackClient(byte[] ip, int port, long validUntil) {
        this.ip = ip;
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
    }

    /**
     * Return whether the request listening interval has passed.
     */
    @Override
    public boolean isValid() {
        return System.currentTimeMillis() > validUntil;
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
        return Arrays.equals(ip, that.ip);
    }

    public byte[] getIp() {
        return ip;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
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
