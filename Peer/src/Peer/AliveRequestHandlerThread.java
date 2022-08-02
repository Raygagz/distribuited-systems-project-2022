package Peer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Networking.Configurations;
import Networking.ConnectionInformation;
import Networking.Messages;
import Networking.ReceivedObject;
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
				// Receives an Alive Request and sends an Alive confirmation to the server
				ReceivedObject receivedObject = DispatcherService.UDPReceiveObject(UDPAliveSocket);
				
				AliveRequest request = (AliveRequest) receivedObject.Object;
				
				ConnectionInformation serverConnectionInformation = new ConnectionInformation(receivedObject.OriginAddress, receivedObject.OriginPort);
				DispatcherService.UDPSend(UDPAliveSocket, serverConnectionInformation, Messages.Alive);
			}
		} catch (SocketException e) {
			// In case of an interruption (Peer leaving the network)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
