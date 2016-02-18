import java.net.*;
import java.io.*;

public class UDPClient{
	public static void main(String args[]) {
		//args give message contents and server hostname
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(2244); //use a free local port
			byte[] m = args[0].getBytes(); //a buffer for sending
			InetAddress aHost = InetAddress.getByName(args[1]); //translate user-specified hostname to Intenet address
			int serverPort = 6789; //need a port number to constract a packet
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
			aSocket.send(request); //send packet using socket method
			byte[] buffer = new byte[1000]; //a buffer for receive
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			System.out.println("Reply: " + new String(reply.getData()));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (aSocket != null) aSocket.close();
		}
	}
}

/*
$ javac UDPClient.java   

$ java -cp . UDPClient "hello" "localhost"
*/
