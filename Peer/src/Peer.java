//
//  TCPClient.java
//  Kurose & Ross
//

import java.io.*;
import java.net.*;

import Configurations.ServerInformation;

public class Peer {
	public static void main (String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(ServerInformation.IP);
		
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		String sentence = userInput.readLine();
		
		byte[] outputData = new byte[sentence.length()];
		byte[] responseData = new byte[sentence.length()];
		
		outputData = sentence.getBytes();
		
		DatagramPacket outputPacket = new DatagramPacket(outputData, outputData.length, IPAddress, ServerInformation.Port);
		
		socket.send(outputPacket);
		
		DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
		
		socket.receive(responsePacket);
		
		String responseSentence = new String(responsePacket.getData());
		System.out.println(responseSentence);
		
		socket.close();
		
		/*
		Socket s = new Socket("localhost", 9000);
		
		OutputStream os = s.getOutputStream();
		DataOutputStream serverWriter = new DataOutputStream(os);
		
		InputStreamReader isrServer = new InputStreamReader(s.getInputStream());
		BufferedReader serverReader = new BufferedReader(isrServer);

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence;  
		sentence = inFromUser.readLine();
		
		while (sentence.compareTo("") != 0) {
			serverWriter.writeBytes(sentence + "\n");

			String response = serverReader.readLine();
			System.out.println(response);
			
			sentence = inFromUser.readLine();
		}
		
		serverWriter.writeBytes(sentence + "\n");
		String response = serverReader.readLine();
		System.out.println(response);
		s.close();
		*/
	}
}
