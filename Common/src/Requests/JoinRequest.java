package Requests;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.UUID;

import Enums.RequestType;

@SuppressWarnings("serial")
public class JoinRequest extends Request {
	public UUID PeerID;
	public String[] FileNames;
	
	public JoinRequest (UUID peerID, String[] fileNames) {
		this.Type = RequestType.JOIN;
		this.PeerID = peerID;
		this.FileNames = fileNames;
	}
}
