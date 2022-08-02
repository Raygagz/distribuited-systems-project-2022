package Networking;

import java.net.InetAddress;

public class ReceivedObject {
	public Object Object;
	public InetAddress OriginAddress;
	public int OriginPort;
	
	public ReceivedObject(Object object, InetAddress originAddress, int originPort) {
		this.Object = object;
		this.OriginAddress = originAddress;
		this.OriginPort = originPort;
	}
}
