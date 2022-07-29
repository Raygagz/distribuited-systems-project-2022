package Networking;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SuppressWarnings("serial")
public class ConnectionInformation implements Serializable {
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
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		this.Address = tempAddress;
		this.Port = port;
	}
}
