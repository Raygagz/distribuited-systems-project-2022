package Networking;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionInformation {
	public final InetAddress Address;
	public final int Port;
	
	public ConnectionInformation(InetAddress address, int port) {
		this.Address = address;
		this.Port = port;
	}
	public ConnectionInformation(String address, int port) {
		InetAddress tempAddress = null;
		try {
			tempAddress = InetAddress.getByName(address);
		}
		catch (UnknownHostException ex) {
		}
		
		this.Address = tempAddress;
		this.Port = port;
	}
}
