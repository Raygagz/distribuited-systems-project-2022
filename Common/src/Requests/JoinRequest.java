package Requests;

import java.util.UUID;

import DTO.FileInformation;
import Enums.RequestType;

public class JoinRequest extends Request {
	public UUID peerID;
	public FileInformation[] files;
	
	public JoinRequest (UUID peerID, FileInformation[] files) {
		this.requestType = RequestType.JOIN;
		this.peerID = peerID;
		this.files = files;
	}
}
