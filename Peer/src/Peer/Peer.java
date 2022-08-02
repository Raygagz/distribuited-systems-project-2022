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
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import Configurations.Configuration;
import Enums.RequestType;
import Networking.Configurations;
import Networking.ConnectionInformation;
import Networking.Messages;
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
			// Prints menu
			System.out.println(
				"JOIN <address> <port> <pathToFolderWithFiles>\n" +
				"SEARCH <fileName>\n" +
				"DOWNLOAD <address> <port>\n" +
				"LEAVE"
			);
			
			// Splits the user input by spaces and tries to parse the selected option and additional params
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
				} catch (SocketTimeoutException e) {
					// Repeat in case of a Timeout
					Join();
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					// Bad input format
					e.printStackTrace();
					continue;
				}
				break;
				
			case SEARCH:
				if (!Joined) continue;
				try {
					CurrentSearchedFileName = params[0];
					Search();
				} catch (SocketTimeoutException e) {
					// Repeat in case of a Timeout
					Search();
				} catch (ArrayIndexOutOfBoundsException e) {
					// Bad input format
					e.printStackTrace();
					continue;
				}
				break;
				
			case DOWNLOAD:
				if (!Joined || CurrentSearchedFileName == null) continue;
				try {
					ConnectionInformation seederConnectionInformation = new ConnectionInformation(InetAddress.getByName(params[0]), Integer.parseInt(params[1]));
					try {
						Download(seederConnectionInformation);
					} catch (SocketTimeoutException e) {
						// Repeat in case of a Timeout
						Download(seederConnectionInformation);
					}
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					// Bad input format
					e.printStackTrace();
					continue;
				}

				break;
				
			case LEAVE:
				if (!Joined) continue;
				try {
					Leave();
				} catch (SocketTimeoutException e) {
					// Repeat in case of a Timeout
					Leave();
				}
				
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
		return DispatcherService.UDPReceiveString(UDPSocket).String;
	}
	
	private static ArrayList<ConnectionInformation> ReceiveConnectionInformationListFromServer() throws IOException, ClassNotFoundException {
		return (ArrayList<ConnectionInformation>) DispatcherService.UDPReceiveObject(UDPSocket).Object;
	}
	
	private static void Join() throws IOException {
		// Creates a new UDPSocket for the peer
		UDPSocket = new DatagramSocket(Port, Address);
		UDPSocket.setSoTimeout(Configuration.TimeOutMilliseconds);
		
		// Gets the file names from the input folder
		ArrayList<String> fileNames = FileService.GetFilesFromPath(FileFolderPath);
		
		// Creates and sends a Join request to server
		JoinRequest request = new JoinRequest(fileNames);
		SendRequestToServer(request);

		// Receives reply from the server
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
	
	private static void Download(ConnectionInformation seederConnectionInformation) throws IOException {
		Socket seederConnection = new Socket(seederConnectionInformation.Address, seederConnectionInformation.Port);
		
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
			
			byte[] buffer = new byte[Configurations.TCPPacketSize];
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
			
			// If the download was successful creates and send an Update request to the peer
			UpdateRequest request = new UpdateRequest(CurrentSearchedFileName);
			String response = null;
			
			try {
				SendRequestToServer(request);
				response = ReceiveMessageFromServer();
			} catch (SocketTimeoutException e) {
				// Repeat in case of a Timeout
				SendRequestToServer(request);
				response = ReceiveMessageFromServer();
			}
			
			if(response.equals(Messages.SuccessfulUpdate)) {
			}
		} else {
			if (ConnectionInformationPeersWithFile != null && ConnectionInformationPeersWithFile.size() > 0) {
				ConnectionInformation newSeederConnectionInformation = ConnectionInformationPeersWithFile.get(0);
				
				System.out.println(
						"peer " +
								newSeederConnectionInformation.Address.toString() + ":" + Integer.toString(newSeederConnectionInformation.Port) +
						" negou o download, pedindo agora para o peer "
					);
				Download(newSeederConnectionInformation);
			}
			
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


	// This server coordinates TCP Peer to Peer connections
	private static void StartTCPServer() throws IOException {
		PeerServer TCPServerThread = new PeerServer(Address, Port, FileFolderPath);
		TCPServerSocket = TCPServerThread.ServerSocket;
		TCPServerThread.start();
	}
	
	private static void CloseTCPServer() throws IOException {
		TCPServerSocket.close();
	}
	

	// Starts Thread to handle Alive requests from the server
	private static void StartAliveRequestHandler() throws SocketException {
		AliveRequestHandlerThread UDPAliveThread = new AliveRequestHandlerThread(Address, Port);
		UDPAliveSocket = UDPAliveThread.UDPAliveSocket;
		UDPAliveThread.start();
	}
	
	private static void CloseAliveRequestHandler() {
		UDPAliveSocket.close();
	}
}
