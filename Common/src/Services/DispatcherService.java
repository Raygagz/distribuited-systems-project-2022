package Services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Networking.ConnectionInformation;
import Networking.ServerInformation;

public class DispatcherService {
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
				destinationConnectionInformation.Port);
		
		originSocket.send(outputPacket);
	}
}
