import java.util.ArrayList;

import Networking.ConnectionInformation;

public class PeerInformation {
	public final ConnectionInformation ConnectionInformation;
	public final ArrayList<String> FileNames;
	
	public PeerInformation(ConnectionInformation connectionInformation, ArrayList<String> fileNames) {
		this.ConnectionInformation = connectionInformation;
		this.FileNames = fileNames;
	}
}
