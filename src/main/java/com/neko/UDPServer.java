package com.neko;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer{
	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(6789);
			//bound to host and port
			byte[] buffer = new byte[1000]; //a buffer for receive
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				System.out.println("Request: " + new String(request.getData()));
				DatagramPacket reply = new DatagramPacket(
					request.getData(),
					request.getLength(),
					request.getAddress(),
					request.getPort());
				aSocket.send(reply); //send packet using socket method
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (aSocket != null) aSocket.close();
		}
	}
}
