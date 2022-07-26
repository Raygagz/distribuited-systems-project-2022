import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class ServiceThread extends Thread {
	private DatagramSocket socket;
	private DatagramPacket packet;
	
	public ServiceThread(DatagramSocket socket, DatagramPacket packet) {
		this.socket = socket;
		this.packet = packet;
	}
	
	public void run() {
		try {
			InetAddress peerIP = packet.getAddress();
			int peerPort = packet.getPort();
			
			System.out.println("Peer " + peerIP + ":" + Integer.toString(peerPort) + " adicionado com ");
			
			String sentence = new String(packet.getData(), 0, packet.getLength());
			
			while(sentence.compareTo("batata") == 0) {
				continue;
			}
			
			byte[] outputData = new byte[sentence.length()];
			outputData = sentence.getBytes();
			
			DatagramPacket outputPacket = new DatagramPacket(outputData, outputData.length, peerIP, peerPort);
			socket.send(outputPacket);
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
}
