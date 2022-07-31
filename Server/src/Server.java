import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import Networking.ConnectionInformation;
import Networking.ServerInformation;
import Requests.Request;
import Services.DispatcherService;

class Server {
	private static List<PeerInformation> Peers = new ArrayList<PeerInformation>();

	public static void main (String args[]) throws Exception {
		DatagramSocket UDPSocket = new DatagramSocket(ServerInformation.ConnectionInformation.Port, ServerInformation.ConnectionInformation.Address);
		StartAliveRequestSender();
		
		while (true) {
			try {
				DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
				UDPSocket.receive(receivedPacket);
				
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
				Request request = (Request) inputStream.readObject();
				inputStream.close();
	
				ConnectionInformation peerConnectionInformation = new ConnectionInformation(receivedPacket.getAddress(), receivedPacket.getPort());
				
				RequestHandlerThread thread = new RequestHandlerThread(UDPSocket, request, peerConnectionInformation);
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private static PeerInformation GetPeerInformation(ConnectionInformation connectionInformation) {
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.ConnectionInformation.equals(connectionInformation)) {
				return peerInformation;
			}
		}
		// throw new PeerNotFoundException();
		return null;
	}
	
	public static void AddPeerInformation(PeerInformation peerInformation) {
		if(GetPeerInformation(peerInformation.ConnectionInformation) == null) {
			Peers.add(peerInformation);
		}
	}
	
	public static void RemovePeerInformation(ConnectionInformation connectionInformation) {
		Peers.removeIf(p -> (p.ConnectionInformation.equals(connectionInformation)));
	}
	
	public static List<ConnectionInformation> SearchFileName(String fileName) {
		List<ConnectionInformation> connectionsInformation = new ArrayList<ConnectionInformation>();
		
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.FileNames.contains(fileName)) {
				connectionsInformation.add(peerInformation.ConnectionInformation);
			}
		}
		
		return connectionsInformation;
	}
	
	public static List<String> GetPeerFileNames(ConnectionInformation connectionInformation) {
		PeerInformation peerInformation = GetPeerInformation(connectionInformation);
		if(peerInformation != null) {
			return peerInformation.FileNames;
		}
		else {
			return new ArrayList<String>();
		}
	}
	
	public static void AddToPeerInformation(ConnectionInformation connectionInformation, String fileName) {
		PeerInformation peerInformation = GetPeerInformation(connectionInformation);
		peerInformation.FileNames.add(fileName);
	}
	
	public static List<PeerInformation> GetAllPeersInformation() {
		return Peers;
	}

	private static void StartAliveRequestSender() throws SocketException {
		AliveRequestSenderThread aliveHandler = new AliveRequestSenderThread();
		aliveHandler.start();
	}
}
