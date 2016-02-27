package com.neko.monitor;

import com.neko.msg.NekoData;
import com.neko.msg.NekoOpcode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.logging.Logger;

public class NekoCallbackServer {
    private static final Logger log = Logger.getLogger(NekoCallbackServer.class.getName());

    /**
     * The port to listen for the incoming callback invocation.
     */
    private int port;
    private NekoCallback callback;

    public NekoCallbackServer(int port, NekoCallback callback) {
        this.port = port;
        this.callback = callback;
    }

    /**
     * Start to listen invocation.
     */
    public void start() {

    }

    /**
     * Start to listen for a time interval in second.
     */
    public void start(int interval) {

    }

    public void stop() {
        throw new NotImplementedException();
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
}
