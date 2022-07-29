import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import Communication.Messages;
import Networking.ConnectionInformation;
import Requests.JoinRequest;
import Requests.LeaveRequest;
import Requests.Request;
import Requests.SearchRequest;
import Requests.UpdateRequest;
import Services.DispatcherService;

public class RequestHandlerThread extends Thread {
	private final DatagramSocket ServerSocket;
	private final ConnectionInformation PeerConnectionInformation;
	private final Request Request;
	
	public RequestHandlerThread(DatagramSocket serverSocket, Request request, ConnectionInformation peerConnectionInformation) {
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
	
	private void SendToPeer(String message){
		try {
			byte[] outputBytes = message.getBytes();
			DatagramPacket outputPacket = new DatagramPacket(outputBytes, outputBytes.length, PeerConnectionInformation.Address, PeerConnectionInformation.Port);
			ServerSocket.send(outputPacket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void SendToPeer(Object object) throws IOException {
		DispatcherService.UDPSend(ServerSocket, PeerConnectionInformation, object);
	}
	
	private void PeerJoin(JoinRequest request) throws IOException {
		PeerInformation peerInformation = new PeerInformation(PeerConnectionInformation, request.FileNames);
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
		List<ConnectionInformation> connectionInformations = Server.SearchFileName(request.FileName);
		
		SendToPeer(connectionInformations);
	}
	
	private void PeerUpdate(UpdateRequest request) throws IOException {
		Server.AddToPeerInformation(PeerConnectionInformation, request.FileName);
		
		SendToPeer(Messages.SuccessfulUpdate);
	}
}
