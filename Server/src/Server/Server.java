package Server;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import Networking.ConnectionInformation;
import Networking.ReceivedObject;
import Networking.ServerInformation;
import Requests.Request;
import Services.DispatcherService;
import info.PeerInformation;

class Server {
	// The Server is responsible for storing the information of the current connected peers
	private static ArrayList<PeerInformation> Peers = new ArrayList<PeerInformation>();

	public static void main (String args[]) throws Exception {
		// Open sockets for receiving UDP requests and verifying the Alive status of connected peers
		DatagramSocket UDPSocket = new DatagramSocket(ServerInformation.ConnectionInformation.Port, ServerInformation.ConnectionInformation.Address);
		StartAliveRequestSender();
		
		while (true) {
			try {
				// When the server receives an object it trying to parse it into an Request
				ReceivedObject receivedObject = DispatcherService.UDPReceiveObject(UDPSocket); 
				Request request = (Request) receivedObject.Object;
	
				ConnectionInformation peerConnectionInformation = new ConnectionInformation(receivedObject.OriginAddress, receivedObject.OriginPort);
				
				// Then the server creates and starts a new Thread to handle the request
				RequestHandlerThread thread = new RequestHandlerThread(UDPSocket, request, peerConnectionInformation);
				thread.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	// Search for PeerInformation in the List
	private static PeerInformation GetPeerInformation(ConnectionInformation connectionInformation) {
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.ConnectionInformation.equals(connectionInformation)) {
				return peerInformation;
			}
		}
		// throw new PeerNotFoundException();
		return null;
	}
	
	// Add peer information if it doesn't exist already
	public static void AddPeerInformation(PeerInformation peerInformation) {
		if(GetPeerInformation(peerInformation.ConnectionInformation) == null) {
			Peers.add(peerInformation);
		}
	}
	
	// Remove peer information if it is in the Peer List
	public static void RemovePeerInformation(ConnectionInformation connectionInformation) {
		Peers.removeIf(p -> (p.ConnectionInformation.equals(connectionInformation)));
	}
	
	// Search for a file name in the PeerInformation List
	// Returns a List of connections information of every Peer who has the searched file 
	public static ArrayList<ConnectionInformation> SearchFileName(String fileName) {
		ArrayList<ConnectionInformation> connectionsInformation = new ArrayList<ConnectionInformation>();
		
		for(PeerInformation peerInformation : Peers) {
			if(peerInformation.FileNames.contains(fileName)) {
				connectionsInformation.add(peerInformation.ConnectionInformation);
			}
		}
		
		return connectionsInformation;
	}
	
	// Gets every file name from a Peer
	public static ArrayList<String> GetPeerFileNames(ConnectionInformation connectionInformation) {
		PeerInformation peerInformation = GetPeerInformation(connectionInformation);
		if(peerInformation != null) {
			return peerInformation.FileNames;
		}
		else {
			return new ArrayList<String>();
		}
	}
	
	// Add a file name to a peer information
	public static void AddToPeerInformation(ConnectionInformation connectionInformation, String fileName) {
		PeerInformation peerInformation = GetPeerInformation(connectionInformation);
		peerInformation.FileNames.add(fileName);
	}
	
	// Returns a List of the connection information of every peer
	public static ArrayList<ConnectionInformation> GetConnectionsInformation() {
		ArrayList<ConnectionInformation> connectionsInformation = new ArrayList<ConnectionInformation>();
		
		for(PeerInformation peerInformation : Peers) {
			connectionsInformation.add(peerInformation.ConnectionInformation);
		}
		
		return connectionsInformation;
	}

	// This Thread keeps track of the Alive status of all connected peers
	private static void StartAliveRequestSender() throws SocketException {
		AliveRequestSenderThread aliveHandler = new AliveRequestSenderThread();
		aliveHandler.start();
	}
}
