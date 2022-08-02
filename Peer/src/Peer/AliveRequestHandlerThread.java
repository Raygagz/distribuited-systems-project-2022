package Peer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Communication.Messages;
import Communication.Sizes;
import Networking.ConnectionInformation;
import Requests.AliveRequest;
import Services.DispatcherService;

public class AliveRequestHandlerThread extends Thread {
	public DatagramSocket UDPAliveSocket;
	
	public AliveRequestHandlerThread(InetAddress address, int port) throws SocketException {
		this.UDPAliveSocket = new DatagramSocket(port+1, address);
	}
	
	public void run() {
		try {
			while (!UDPAliveSocket.isClosed()) {
				DatagramPacket receivedPacket = new DatagramPacket(new byte[Sizes.UDPMaxPacketSize], Sizes.UDPMaxPacketSize);
				UDPAliveSocket.receive(receivedPacket);
				
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
				AliveRequest request = (AliveRequest) inputStream.readObject();
				inputStream.close();
				
				ConnectionInformation serverConnectionInformation = new ConnectionInformation(receivedPacket.getAddress(), receivedPacket.getPort());
				DispatcherService.UDPSend(UDPAliveSocket, serverConnectionInformation, Messages.Alive);
				
				inputStream.close();
			}
		} catch (SocketException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
