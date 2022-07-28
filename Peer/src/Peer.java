//
//  TCPClient.java
//  Kurose & Ross
//

import java.io.*;
import java.net.*;
import java.util.UUID;

import Communication.Messages;
import Networking.PeerInformation;
import Networking.ServerInformation;
import Requests.JoinRequest;
import Requests.Request;
import Services.FileService;

public class Peer {
	public static void main (String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;
		int selection = 0;
		
		do{
			System.out.println(
				"1. JOIN\n" +
				"2. SEARCH\n" +
				"3. DOWNLOAD\n" +
				"4. LEAVE\n"
			);
			
			userInput = consoleReader.readLine();
			try{
	            selection = Integer.parseInt(userInput);
	        }
	        catch (NumberFormatException ex){
	            continue;
	        }
			
			switch(selection) {
			case 1:
				Join(socket);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			default:
				continue;
			}
			
		} while(!userInput.isEmpty());	
		
		socket.close();
	}
	
	private static void SendRequestToServer(DatagramSocket socket, Request request) throws IOException {
		InetAddress serverAddress = InetAddress.getByName(ServerInformation.IP);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(outputStream);
		oo.writeObject(request);
		oo.close();
		
		DatagramPacket outputPacket = new DatagramPacket(outputStream.toByteArray(), outputStream.toByteArray().length, serverAddress, ServerInformation.Port);
		
		socket.send(outputPacket);
	}
	
	private static String ReceiveMessageFromServer(DatagramSocket socket) throws IOException {
		byte[] buffer = new byte[1024];
		DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(responsePacket);

		String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
		return response;
	}
	
	private static void Join(DatagramSocket socket) throws IOException {
		String[] fileNames = FileService.getFilesFromPath(Configuration.FILES_RELATIVE_PATH);
		JoinRequest request = new JoinRequest(UUID.randomUUID(), fileNames);
		
		SendRequestToServer(socket, request);

		String response = ReceiveMessageFromServer(socket);
		
		if(response.equals(Messages.SuccessfulJoin)) {
			System.out.println(
				"Sou peer " +
				InetAddress.getLocalHost() + ":" + PeerInformation.Port +
				" com arquivos " +
				String.join(" ", fileNames)
			);
		}
		
		System.out.println(response);
	}
}
