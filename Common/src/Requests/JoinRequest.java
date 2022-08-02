package Requests;

import java.util.ArrayList;
import java.util.List;

import Enums.RequestType;

@SuppressWarnings("serial")
public class JoinRequest extends Request {
	public final ArrayList<String> FileNames;
	
	public JoinRequest(ArrayList<String> fileNames) {
		this.Type = RequestType.JOIN;
		this.FileNames = fileNames;
	}
}
