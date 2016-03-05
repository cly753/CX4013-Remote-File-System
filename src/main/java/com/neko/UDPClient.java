package com.neko;

import static com.neko.msg.NekoOpcode.COPY;

import com.neko.msg.NekoData;
import com.neko.msg.NekoDeserializer;
import com.neko.msg.NekoSerializer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

    public static final int SOCKET_SIZE = 2244;
    public static final int SERVER_PORT = 6789;
    public static final int BUFFER_SIZE = 1000;

    public static final String PATH = "/Users/LZQ/Documents/CX4013-Remote-File-System/resource/test.txt";

    /**
     * args give message contents and server hostname
     */
    public static void main(String[] args) {

//        final String message = args[0];
//        final String hostname = args[1];
        final String hostname = "localhost";
        DatagramSocket socket = null;

        try {
            //use a free local port
            socket = new DatagramSocket(SOCKET_SIZE);
            //translate user-specified hostname to Intenet address
            InetAddress host = InetAddress.getByName(hostname);

//            while (true) {
//                System.out.print("Enter request:");
//                String input = System.console().readLine();
//
//                //a buffer for sending
//                byte[] byteMessage = input.getBytes();
//
//                DatagramPacket request =
//                        new DatagramPacket(byteMessage, byteMessage.length, host, port);
//
//                socket.send(request); //send packet using socket method
//
//                byte[] buffer = new byte[BUFFER_SIZE]; //a buffer for receive
//                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
//                socket.receive(reply);
//
//                System.out.println("Reply: " + new String(reply.getData()));
//
//            }

            NekoData request = new NekoData();
            request.setOpcode(COPY);
            request.setPath(PATH);

            System.out.println("Request: " + request.toString());

            NekoSerializer serializer = new NekoSerializer();
            NekoDeserializer deserializer = new NekoDeserializer();

            byte[] requestBytes = serializer.serialize(request).toBytes();
            DatagramPacket requestPacket =
                    new DatagramPacket(requestBytes, requestBytes.length, host, SERVER_PORT);
            socket.send(requestPacket); //send packet using socket method

            byte[] buffer = new byte[BUFFER_SIZE]; //a buffer for receive
            DatagramPacket replyPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(replyPacket);
            NekoData reply = deserializer.deserialize(replyPacket.getData());
            System.out.println("Reply: " + reply.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
