//
//  TCPClient.java
//  Kurose & Ross
//

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

import Communication.Messages;
import Enums.RequestType;
import Networking.ServerInformation;
import Requests.JoinRequest;
import Requests.Request;
import Server.PeerServer;
import Services.DispatcherService;
import Services.FileService;

public class Peer {
	private static DatagramSocket UDPSocket = null;
	private static InetAddress Address = null;
	private static int Port = 0;
	private static String FileFolderPath = null;
	
	private static Boolean Joined = false;

	public static void main (String args[]) throws Exception {
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		String[] userInput = null;
		RequestType selection = null;
		String[] params = null;
		
		do{
			System.out.println(
				"JOIN <address> <port> <pathToFolderWithFiles>\n" +
				"SEARCH\n" +
				"DOWNLOAD\n" +
				"LEAVE\n"
			);
			
			userInput = consoleReader.readLine().split(" ");
			try {
	            selection = RequestType.valueOf(userInput[0]);
	            params = Arrays.copyOfRange(userInput, 1, userInput.length);
	        }
	        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex){
	            continue;
	        }
			
			switch(selection) {
			case JOIN:
				if (Joined) continue;
				try {
					Address = InetAddress.getByName(params[0]);
					Port = Integer.parseInt(params[1]);
					FileFolderPath = params[2];
					Join();
				}
				catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					continue;
				}
				break;
				
			case SEARCH:
				if (!Joined) continue;
				try {
					String fileName = params[0];
					Search(fileName);
				}
				catch (ArrayIndexOutOfBoundsException ex) {
					continue;
				}
				break;
				
			case DOWNLOAD:
				if (!Joined) continue;
				try {
					InetAddress seederAddress = InetAddress.getByName(params[0]);
					int seederPort = Integer.parseInt(params[1]);
					Download(seederAddress, seederPort);
				}
				catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					continue;
				}
				break;
				
			case LEAVE:
				if (!Joined) continue;
				userInput = null;
				Leave();
				break;
				
			default:
				break;
			}
			
		} while(userInput != null);
		
		if (!UDPSocket.isClosed())
			UDPSocket.close();
	}
	
	private static void SendRequestToServer(Request request) throws IOException {
		DispatcherService.UDPSend(UDPSocket, ServerInformation.ConnectionInformation, request);
	}
	
	private static String ReceiveMessageFromServer() throws IOException {
		byte[] buffer = new byte[1024];
		DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
		UDPSocket.receive(responsePacket);

		String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
		return response;
	}
	
	private static void Join() throws IOException {
		UDPSocket = new DatagramSocket(Port, Address);
		
		List<String> fileNames = FileService.getFilesFromPath(FileFolderPath);
		
		JoinRequest request = new JoinRequest(fileNames);
		
		SendRequestToServer(request);

		String response = ReceiveMessageFromServer();
		
		if(response.equals(Messages.SuccessfulJoin)) {
			System.out.println(
				"Sou peer " +
				Address + ":" + Port +
				" com arquivos " +
				String.join(" ", fileNames)
			);
			Joined = true;
			StartTCPServer();
		}
	}
	
	private static void Search(String fileName) {
		
	}
	
	private static void Download(InetAddress seederAddress, int seederPort) {
		
	}
	
	private static void Leave() {
		Joined = false;
	}
	
	private static void StartTCPServer() throws IOException {
		PeerServer peerServerThread = new PeerServer(Address, Port);
		peerServerThread.start();
	}
}
