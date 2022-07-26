import java.util.UUID;

public class PeerInformation {
	public UUID id;
	public String ip;
	public String port;
	public FileInformation[] files;
	
	public PeerInformation() {}
	public PeerInformation(
		String ip,
		String port
	) {
		this.ip = ip;
		this.port = port;
	}
}
