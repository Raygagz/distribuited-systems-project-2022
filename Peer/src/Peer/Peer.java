package Peer;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import Communication.Messages;
import Communication.Sizes;
import Enums.RequestType;
import Networking.ConnectionInformation;
import Networking.ServerInformation;
import Requests.JoinRequest;
import Requests.LeaveRequest;
import Requests.Request;
import Requests.SearchRequest;
import Requests.UpdateRequest;
import Server.PeerServer;
import Services.DispatcherService;
import Services.FileService;

public class Peer {
	private static DatagramSocket UDPSocket = null;
	private static DatagramSocket UDPAliveSocket = null;
	private static ServerSocket TCPServerSocket = null;
	private static InetAddress Address = null;
	private static int Port = 0;
	
	private static String CurrentSearchedFileName = null;
	private static ArrayList<ConnectionInformation> ConnectionInformationPeersWithFile = null;
	
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
				"SEARCH <fileName>\n" +
				"DOWNLOAD <address> <port>\n" +
				"LEAVE"
			);
			
			userInput = consoleReader.readLine().split(" ");
			try {
	            selection = RequestType.valueOf(userInput[0]);
	            params = Arrays.copyOfRange(userInput, 1, userInput.length);
	        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e){
	        	e.printStackTrace();
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
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
				break;
				
			case SEARCH:
				if (!Joined) continue;
				try {
					CurrentSearchedFileName = params[0];
					Search();
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
				break;
				
			case DOWNLOAD:
				if (!Joined || CurrentSearchedFileName == null) continue;
				try {
					InetAddress seederAddress = InetAddress.getByName(params[0]);
					int seederPort = Integer.parseInt(params[1]);
					Download(seederAddress, seederPort);
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
				break;
				
			case LEAVE:
				if (!Joined) continue;
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
		byte[] buffer = new byte[Sizes.UDPMaxPacketSize];
		DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
		UDPSocket.receive(responsePacket);

		String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
		return response;
	}
	
	private static ArrayList<ConnectionInformation> ReceiveConnectionInformationListFromServer() throws IOException, ClassNotFoundException {
		DatagramPacket receivedPacket = new DatagramPacket(new byte[Sizes.UDPMaxPacketSize], Sizes.UDPMaxPacketSize);
		UDPSocket.receive(receivedPacket);
		
		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
		ArrayList<ConnectionInformation> connectionInformations = (ArrayList<ConnectionInformation>) inputStream.readObject();
		inputStream.close();
		
		return connectionInformations;
	}
	
	private static void Join() throws IOException {
		UDPSocket = new DatagramSocket(Port, Address);
		
		ArrayList<String> fileNames = FileService.GetFilesFromPath(FileFolderPath);
		
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
			StartTCPServer();
			StartAliveRequestHandler();
			Joined = true;
		}
	}
	
	private static void Search() throws IOException, ClassNotFoundException {
		SearchRequest request = new SearchRequest(CurrentSearchedFileName);
		
		SendRequestToServer(request);
		
		ConnectionInformationPeersWithFile = ReceiveConnectionInformationListFromServer();
		
		System.out.println("peers com arquivo solicitado:");
		for(ConnectionInformation connectionInformation : ConnectionInformationPeersWithFile) {
			System.out.println(connectionInformation.Address.toString() + ":" + Integer.toString(connectionInformation.Port));
		}
	}
	
	private static void Download(InetAddress seederAddress, int seederPort) throws IOException {
		Socket seederConnection = new Socket(seederAddress, seederPort);
		
		OutputStream os = seederConnection.getOutputStream();
		DataOutputStream serverWriter = new DataOutputStream(os);

		// Send fileName as a line
		serverWriter.writeBytes(CurrentSearchedFileName + "\n");
		
		// Check response if download was accepted
		BufferedReader br = new BufferedReader(new InputStreamReader(seederConnection.getInputStream()));
		String donwloadAllowedOrDenied = br.readLine();
		
		if(donwloadAllowedOrDenied.equals(Messages.DownloadAllowed)) {
			// Start File receive procedure
			// https://heptadecane.medium.com/file-transfer-via-java-sockets-e8d4f30703a5
			int bytes = 0;
			FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(FileFolderPath, CurrentSearchedFileName).toString());
			DataInputStream dataInputStream = new DataInputStream(seederConnection.getInputStream());
		
			// Receive File size
			long size = dataInputStream.readLong();
			
			byte[] buffer = new byte[Sizes.TCPPacketSize];
			while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
	            fileOutputStream.write(buffer,0,bytes);
	            size -= bytes;      // read upto file size
	        }
	        fileOutputStream.close();
	        
	        System.out.println(
	        	"Arquivo " +
	        	CurrentSearchedFileName +
	        	" baixado com sucesso na pasta " +
	        	FileFolderPath
	        );
			
			seederConnection.close();
			
			UpdateRequest request = new UpdateRequest(CurrentSearchedFileName);
			SendRequestToServer(request);
			
			String response = ReceiveMessageFromServer();
			
			if(response.equals(Messages.SuccessfulUpdate)) {
			}
		} else {
			System.out.println(
					"peer " +
					seederAddress.toString() + ":" + Integer.toString(seederPort) +
					" negou o download, pedindo agora para o peer "
				);
		}
	}
	
	private static void Leave() throws IOException {
		LeaveRequest request = new LeaveRequest();
		
		SendRequestToServer(request);
		
		String response = ReceiveMessageFromServer();
		if(response.equals(Messages.SuccessfulLeave)) {
			CloseTCPServer();
			CloseAliveRequestHandler();
			
			Joined = false;
		}
	}
	
	private static void StartTCPServer() throws IOException {
		PeerServer TCPServerThread = new PeerServer(Address, Port, FileFolderPath);
		TCPServerSocket = TCPServerThread.ServerSocket;
		TCPServerThread.start();
	}
	
	private static void CloseTCPServer() throws IOException {
		TCPServerSocket.close();
	}
	
	private static void StartAliveRequestHandler() throws SocketException {
		AliveRequestHandlerThread UDPAliveThread = new AliveRequestHandlerThread(Address, Port);
		UDPAliveSocket = UDPAliveThread.UDPAliveSocket;
		UDPAliveThread.start();
	}
	
	private static void CloseAliveRequestHandler() {
		UDPAliveSocket.close();
	}
}
