package Requests;

import Enums.RequestType;

@SuppressWarnings("serial")
public class UpdateRequest extends Request {
	public final String FileName;
	
	public UpdateRequest(String fileName) {
		this.Type = RequestType.UPDATE;
		this.FileName = fileName;
	}
}
