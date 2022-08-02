package Server;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import Configurations.Configuration;
import Networking.ConnectionInformation;
import Networking.Messages;
import Networking.ReceivedString;
import Networking.ServerInformation;
import Requests.AliveRequest;
import Services.DispatcherService;

public class AliveRequestSenderThread extends Thread {
	private DatagramSocket Socket;
	
	public AliveRequestSenderThread() throws SocketException {
		this.Socket = new DatagramSocket(ServerInformation.ConnectionInformation.Port + 1, ServerInformation.ConnectionInformation.Address);
		// Set a timeout for the Alive reply
		Socket.setSoTimeout(Configuration.TimeOutMilliseconds);
	}
	
	public void run() {
		try {
			String response;
			
			while(true) {
				// Sleeps for 30 seconds
				Thread.sleep(30000);
				// Get Every Peer connectionInformation
				ArrayList<ConnectionInformation> peersConnectionInformation = Server.GetConnectionsInformation();
				ArrayList<ConnectionInformation> toRemove = new ArrayList<ConnectionInformation>();
				
				// For each set of information, add as a possible removal and send an alive request
				for(ConnectionInformation peerConnectionInformation : peersConnectionInformation) {
					toRemove.add(peerConnectionInformation);
					ConnectionInformation peerAliveConnectionInformation = new ConnectionInformation(peerConnectionInformation.Address, peerConnectionInformation.Port+1);
					DispatcherService.UDPSend(Socket, peerAliveConnectionInformation, new AliveRequest());
				}
				
				// For every response received, remove the peer from the list of peers to be removed
				for(int i=0; i < peersConnectionInformation.size(); i++) {
					try {
						ReceivedString receivedString = DispatcherService.UDPReceiveString(Socket);
						
						response = receivedString.String;
						
						if(response.equals(Messages.Alive)) {
							toRemove.removeIf(c -> c.equals(new ConnectionInformation(receivedString.OriginAddress, receivedString.OriginPort-1)));
						}
					} catch (SocketTimeoutException e) {
						continue;
					}
				}
				
				// Remove those peers information from the server storage 
				for(ConnectionInformation connectionInformation : toRemove) {
					System.out.println(
							"Peer " +
									connectionInformation.Address + ":" + Integer.toString(connectionInformation.Port) +
							" morto. Elminando seus arquivos " +
							String.join(" ", Server.GetPeerFileNames(connectionInformation))
						);
					
					Server.RemovePeerInformation(connectionInformation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
