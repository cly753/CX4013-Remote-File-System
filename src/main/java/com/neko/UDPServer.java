package com.neko;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    public static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            //bound to host and port
            socket = new DatagramSocket(SERVER_PORT);

            //a buffer for receive
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                System.out.println("Request: " + new String(request.getData()));

                DatagramPacket reply = new DatagramPacket(
                        request.getData(),
                        request.getLength(),
                        request.getAddress(),
                        request.getPort());
                socket.send(reply); //send packet using socket method
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
