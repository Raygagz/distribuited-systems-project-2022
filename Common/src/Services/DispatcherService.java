package Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Networking.ConnectionInformation;
import Networking.ServerInformation;
import Requests.Request;

public class DispatcherService {
	public static void UDPSend(DatagramSocket originSocket, ConnectionInformation destinationConnectionInformation, String message) throws IOException {
		try {
			byte[] outputBytes = message.getBytes();
			DatagramPacket outputPacket = new DatagramPacket(outputBytes, outputBytes.length, destinationConnectionInformation.Address, destinationConnectionInformation.Port);
			originSocket.send(outputPacket);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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
}
