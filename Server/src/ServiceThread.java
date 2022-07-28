import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import Communication.Messages;
import Requests.JoinRequest;
import Requests.Request;
import Services.FileService;

public class ServiceThread extends Thread {
	private DatagramSocket socket;
	private DatagramPacket receivedPacket;
	
	public ServiceThread(DatagramSocket socket, DatagramPacket packet) {
		this.socket = socket;
		this.receivedPacket = packet;
	}
	
	public void run() {
		try {
			InetAddress PeerAddress = receivedPacket.getAddress();
			int PeerPort = receivedPacket.getPort();
			
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
			Request request = (Request) inputStream.readObject();
			inputStream.close();
			
			/*
			 * •	Quando receber o JOIN, print “Peer [IP]:[porta] adicionado com arquivos [só nomes dos arquivos].
			 * •	Quando receber o SEARCH, print “Peer [IP]:[porta] solicitou arquivo [só nome do arquivo].
			 * •	Se não receber o ALIVE_OK, print “Peer [IP]:[porta] morto. Eliminando seus arquivos [só nome dos arquivos]”.
			 */
			
			switch(request.Type) {
				case JOIN:
					JoinRequest joinRequest = (JoinRequest) request;
					System.out.println(
						"Peer " +
						PeerAddress + ":" + Integer.toString(PeerPort) +
						" adicionado com " +
						String.join(" ", joinRequest.FileNames)
					);
					
					this.sendToPeer(PeerAddress, PeerPort, Messages.SuccessfulJoin);
					
					break;
				case LEAVE:
					break;
				case SEARCH:
					break;
				case UPDATE:
					break;
				case ALIVE:
					break;
				default:
					break;
			}
			/*
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String input = br.readLine();

			DataOutputStream output = new DataOutputStream(connection.getOutputStream());

			while (input.compareTo("") != 0) {
				output.writeBytes(input.toUpperCase() + "\n");
				input = br.readLine();
			}
			
			output.writeBytes("See ya later!");
			connection.close();
			*/
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void sendToPeer(InetAddress peerAddress, int peerPort, String message) throws IOException {
		byte[] outputBytes = message.getBytes();
		DatagramPacket outputPacket = new DatagramPacket(outputBytes, outputBytes.length, peerAddress, peerPort);
		socket.send(outputPacket);
	}
}
