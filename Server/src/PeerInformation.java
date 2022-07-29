import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

import Networking.ConnectionInformation;

public class PeerInformation {
	public final ConnectionInformation ConnectionInformation;
	public final List<String> FileNames;
	
	public PeerInformation(ConnectionInformation connectionInformation, List<String> fileNames) {
		this.ConnectionInformation = connectionInformation;
		this.FileNames = fileNames;
	}
}
