package Peer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Communication.Messages;
import Networking.ConnectionInformation;
import Networking.ServerInformation;
import Requests.AliveRequest;
import Requests.Request;
import Services.DispatcherService;

public class AliveRequestHandlerThread extends Thread {
	private DatagramSocket UDPSocket;
	
	public AliveRequestHandlerThread(InetAddress address, int port) throws SocketException {
		this.UDPSocket = new DatagramSocket(port+1, address);
	}
	
	public void run() {
		try {
			while (true) {
				DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
				UDPSocket.receive(receivedPacket);
				
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
				AliveRequest request = (AliveRequest) inputStream.readObject();
				inputStream.close();
				
				ConnectionInformation serverConnectionInformation = new ConnectionInformation(receivedPacket.getAddress(), receivedPacket.getPort());
				DispatcherService.UDPSend(UDPSocket, serverConnectionInformation, Messages.Alive);
				
				inputStream.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
