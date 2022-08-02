package Networking;

import java.net.InetAddress;

public class ReceivedString {
	public String String;
	public InetAddress OriginAddress;
	public int OriginPort;
	
	public ReceivedString(String string, InetAddress originAddress, int originPort) {
		this.String = string;
		this.OriginAddress = originAddress;
		this.OriginPort = originPort;
	}
}
