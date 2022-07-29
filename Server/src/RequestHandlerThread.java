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
	private final byte[] Data;
	
	public RequestHandlerThread(DatagramSocket serverSocket, DatagramPacket receivedPacket) {
		this.ServerSocket = serverSocket;
		this.PeerConnectionInformation = new ConnectionInformation(receivedPacket.getAddress(), receivedPacket.getPort());
		this.Data = receivedPacket.getData();
	}
	
	public void run() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(Data));
			Request request = (Request) inputStream.readObject();
			inputStream.close();
			
			switch(request.Type) {
				case JOIN:
					JoinRequest joinRequest = (JoinRequest) request;
					PeerJoin(joinRequest);
					break;
				case LEAVE:
					LeaveRequest leaveRequest = (LeaveRequest) request;
					PeerLeave(leaveRequest);
					break;
				case SEARCH:
					SearchRequest searchRequest = (SearchRequest) request;
					PeerSearch(searchRequest);
					break;
				case UPDATE:
					UpdateRequest updateRequest = (UpdateRequest) request;
					PeerUpdate(updateRequest);
					break;
				default:
					break;
			}
		} catch (Exception e) {
		}
	}
	
	private void SendToPeer(String message){
		try {
			byte[] outputBytes = message.getBytes();
			DatagramPacket outputPacket = new DatagramPacket(outputBytes, outputBytes.length, PeerConnectionInformation.Address, PeerConnectionInformation.Port);
			ServerSocket.send(outputPacket);
		}
		catch (IOException ex) {
			// Timeout
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
