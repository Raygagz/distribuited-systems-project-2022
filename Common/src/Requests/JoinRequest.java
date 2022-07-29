package Requests;

import java.util.List;

import Enums.RequestType;

@SuppressWarnings("serial")
public class JoinRequest extends Request {
	public final List<String> FileNames;
	
	public JoinRequest(List<String> fileNames) {
		this.Type = RequestType.JOIN;
		this.FileNames = fileNames;
	}
}
