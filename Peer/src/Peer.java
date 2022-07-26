//
//  TCPClient.java
//  Kurose & Ross
//

import java.io.*;
import java.net.*;

public class Peer {
	public static void main (String args[]) throws Exception {
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
	}
	
	public static void sendInformation () {
		
	}
}
