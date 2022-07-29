import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Networking.ConnectionInformation;
import Networking.ServerInformation;

class Server {
	private static List<PeerInformation> Peers = new ArrayList<PeerInformation>();

	public static void main (String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket(ServerInformation.ConnectionInformation.Port);
		
		while (true) {
			DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
			socket.receive(receivedPacket);
			
			RequestHandlerThread thread = new RequestHandlerThread(socket, receivedPacket);
			thread.start();
		}
	}
	
	private static PeerInformation GetPeerInformation(ConnectionInformation connectionInformation) {
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.ConnectionInformation.equals(connectionInformation)) {
				return peerInformation;
			}
		}
		return null;
	}
	
	public static void AddPeerInformation(PeerInformation peerInformation) {
		// Avoid duplicates
		if(GetPeerInformation(peerInformation.ConnectionInformation) != null) {
			Peers.add(peerInformation);
		}
	}
	
	public static void RemovePeerInformation(ConnectionInformation connectionInformation) {
		Peers.removeIf(p -> (p.ConnectionInformation.equals(connectionInformation)));
	}
	
	public static List<ConnectionInformation> SearchFileName(String fileName) {
		List<ConnectionInformation> connectionInformations = new ArrayList<ConnectionInformation>();
		
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.FileNames.contains(fileName)) {
				connectionInformations.add(peerInformation.ConnectionInformation);
			}
		}
		
		return connectionInformations;
	}
	
	public static void AddToPeerInformation(ConnectionInformation connectionInformation, String fileName) {
		PeerInformation peerInformation = GetPeerInformation(connectionInformation);
		peerInformation.FileNames.add(fileName);
	}
}
