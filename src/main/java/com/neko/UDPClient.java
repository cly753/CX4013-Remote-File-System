package com.neko;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public static final int SOCKET_SIZE = 2244;
    public static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;

    /**
     * args give message contents and server hostname
     */
    public static void main(String[] args) {

        final String message = args[0];
        final String hostname = args[1];

        DatagramSocket socket = null;

        try {
            //use a free local port
            socket = new DatagramSocket(SOCKET_SIZE);

            //a buffer for sending
            byte[] byteMessage = message.getBytes();

            //translate user-specified hostname to Intenet address
            InetAddress host = InetAddress.getByName(hostname);

            DatagramPacket request =
                    new DatagramPacket(byteMessage, byteMessage.length, host, SERVER_PORT);

            socket.send(request); //send packet using socket method

            byte[] buffer = new byte[BUFFER_SIZE]; //a buffer for receive
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);

            System.out.println("Reply: " + new String(reply.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
