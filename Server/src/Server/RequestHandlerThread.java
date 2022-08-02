package Server;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import Networking.ConnectionInformation;
import Networking.Messages;
import Requests.JoinRequest;
import Requests.LeaveRequest;
import Requests.Request;
import Requests.SearchRequest;
import Requests.UpdateRequest;
import Services.DispatcherService;
import info.PeerInformation;

public class RequestHandlerThread extends Thread {
	private final DatagramSocket ServerSocket;
	private final ConnectionInformation PeerConnectionInformation;
	private final Request Request;
	
	public RequestHandlerThread(DatagramSocket serverSocket, Request request, ConnectionInformation peerConnectionInformation) throws SocketException {
		this.ServerSocket = serverSocket;
		this.PeerConnectionInformation = peerConnectionInformation;
		this.Request = request;
	}
	
	public void run() {
		try {
			switch(Request.Type) {
				case JOIN:
					JoinRequest joinRequest = (JoinRequest) Request;
					PeerJoin(joinRequest);
					break;
				case LEAVE:
					LeaveRequest leaveRequest = (LeaveRequest) Request;
					PeerLeave(leaveRequest);
					break;
				case SEARCH:
					SearchRequest searchRequest = (SearchRequest) Request;
					PeerSearch(searchRequest);
					break;
				case UPDATE:
					UpdateRequest updateRequest = (UpdateRequest) Request;
					PeerUpdate(updateRequest);
					break;
				default:
					break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void SendToPeer(String message) throws IOException {
		DispatcherService.UDPSend(ServerSocket, PeerConnectionInformation, message);
	}
	private void SendToPeer(Object object) throws IOException {
		DispatcherService.UDPSend(ServerSocket, PeerConnectionInformation, object);
	}
	
	private void PeerJoin(JoinRequest request) throws IOException {
		ArrayList<String> peerFileNames = (request.FileNames != null ? request.FileNames : new ArrayList<String>());
		PeerInformation peerInformation = new PeerInformation(PeerConnectionInformation, peerFileNames);
		Server.AddPeerInformation(peerInformation);

		System.out.println(
				"Peer " +
						PeerConnectionInformation.Address + ":" + Integer.toString(PeerConnectionInformation.Port) +
				" adicionado com " +
				String.join(" ", request.FileNames)
			);
		
		SendToPeer(Messages.SuccessfulJoin);
	}
	
	private void PeerLeave(LeaveRequest request) throws IOException {
		Server.RemovePeerInformation(PeerConnectionInformation);
		
		SendToPeer(Messages.SuccessfulLeave);
	}
	
	private void PeerSearch(SearchRequest request) throws IOException {
		ArrayList<ConnectionInformation> connectionsInformation = Server.SearchFileName(request.FileName);
		
		System.out.println(
			"Peer " +
			PeerConnectionInformation.Address.toString() + ":" + Integer.toString(PeerConnectionInformation.Port) +
			" solicitou arquivo " +
			request.FileName
		);
		
		SendToPeer(connectionsInformation);
	}
	
	private void PeerUpdate(UpdateRequest request) throws IOException {
		Server.AddToPeerInformation(PeerConnectionInformation, request.FileName);
		
		SendToPeer(Messages.SuccessfulUpdate);
	}
}
