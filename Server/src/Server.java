import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Configurations.ServerInformation;

class Server {

	public Socket s;

	public static void main (String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket(ServerInformation.Port);
		
		while (true) {
			byte[] inputData = new byte[1024];
			
			DatagramPacket receivedPacket = new DatagramPacket(inputData, inputData.length);
			socket.receive(receivedPacket);
			
			ServiceThread thread = new ServiceThread(socket, receivedPacket);
			thread.start();
		}
		
		/*
		ServerSocket serverSocket = new ServerSocket(10098);
		
		while (true) {
			System.out.println("Waiting for connection at port 10098.");
			Socket s = serverSocket.accept();
			System.out.println("Connection established from " + s.getInetAddress());

			ServiceThread thread = new ServiceThread(s);
			thread.start();
		}
		*/
	}
}
