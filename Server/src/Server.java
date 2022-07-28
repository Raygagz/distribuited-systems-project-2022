import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Networking.ServerInformation;

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
	}
}
