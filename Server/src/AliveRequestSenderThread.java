import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import Communication.Messages;
import Communication.Sizes;
import Networking.ConnectionInformation;
import Networking.ServerInformation;
import Requests.AliveRequest;
import Services.DispatcherService;

public class AliveRequestSenderThread extends Thread {
	private DatagramSocket Socket;
	
	public AliveRequestSenderThread() throws SocketException {
		this.Socket = new DatagramSocket(ServerInformation.ConnectionInformation.Port + 1, ServerInformation.ConnectionInformation.Address);
		Socket.setSoTimeout(2000);
	}
	
	public void run() {
		try {
			DatagramPacket responsePacket = new DatagramPacket(new byte[Sizes.UDPMaxPacketSize], Sizes.UDPMaxPacketSize);

			String response;
			
			while(true) {
				Thread.sleep(30000);
				ArrayList<PeerInformation> peersInformation = Server.GetAllPeersInformation();
				ArrayList<ConnectionInformation> toRemove = new ArrayList<ConnectionInformation>();
				
				for(PeerInformation peerInformation : peersInformation) {
					toRemove.add(peerInformation.ConnectionInformation);
					ConnectionInformation peerAliveConnectionInformation = new ConnectionInformation(peerInformation.ConnectionInformation.Address, peerInformation.ConnectionInformation.Port+1);
					DispatcherService.UDPSend(Socket, peerAliveConnectionInformation, new AliveRequest());
				}
				
				for(int i=0; i < peersInformation.size(); i++) {
					try {
						Socket.receive(responsePacket);
						
						response = new String(responsePacket.getData(), 0, responsePacket.getLength());
						
						if(response.equals(Messages.Alive)) {
							toRemove.removeIf(c -> c.equals(new ConnectionInformation(responsePacket.getAddress(), responsePacket.getPort()-1)));
						}
					} catch (SocketTimeoutException e) {
						continue;
					}
				}
				
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
