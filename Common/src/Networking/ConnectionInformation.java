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
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    ConnectionInformation that = (ConnectionInformation) o;
		return (
			this.Address.toString().compareTo(that.Address.toString()) == 0 &&
			this.Port == that.Port
		);	
	}
}
