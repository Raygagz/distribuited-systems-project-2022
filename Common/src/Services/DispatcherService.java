package Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Networking.Configurations;
import Networking.ConnectionInformation;
import Networking.ReceivedObject;
import Networking.ReceivedString;

public class DispatcherService {
	// UDP Send methods (Convert to byte array and send through the socket)
	public static void UDPSend(DatagramSocket originSocket, ConnectionInformation destinationConnectionInformation, String message) throws IOException {
		byte[] outputBytes = message.getBytes();
		DatagramPacket outputPacket = new DatagramPacket(outputBytes,
				outputBytes.length,
				destinationConnectionInformation.Address,
				destinationConnectionInformation.Port);
		
		originSocket.send(outputPacket);
	}
	
	public static void UDPSend(DatagramSocket originSocket, ConnectionInformation destinationConnectionInformation, Object object) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		oos.writeObject(object);
		oos.close();
		
		byte[] outputBytes = outputStream.toByteArray();
		
		DatagramPacket outputPacket = new DatagramPacket(
			outputBytes,
			outputBytes.length,
			destinationConnectionInformation.Address,
			destinationConnectionInformation.Port
		);
		
		originSocket.send(outputPacket);
	}
	
	
	// UDP Receive methods: Receives the packet, parses it
	// and creates an object with the sender information
	public static ReceivedString UDPReceiveString(DatagramSocket originSocket) throws IOException {
		byte[] buffer = new byte[Configurations.UDPMaxPacketSize];
		DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
		originSocket.receive(receivedPacket);

		String string = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
		
		return new ReceivedString(string, receivedPacket.getAddress(), receivedPacket.getPort());
	}
	
	public static ReceivedObject UDPReceiveObject(DatagramSocket originSocket) throws IOException, ClassNotFoundException {
		byte[] buffer = new byte[Configurations.UDPMaxPacketSize];
		DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
		originSocket.receive(receivedPacket);
		
		ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
		Object object = inputStream.readObject();
		inputStream.close();

		return new ReceivedObject(object, receivedPacket.getAddress(), receivedPacket.getPort());
	}
}
