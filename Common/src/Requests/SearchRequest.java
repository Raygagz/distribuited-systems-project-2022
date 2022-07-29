package Requests;

import Enums.RequestType;

@SuppressWarnings("serial")
public class SearchRequest extends Request {
	public final String FileName;
	
	public SearchRequest(String fileName) {
		this.Type = RequestType.SEARCH;
		this.FileName = fileName;
	}
}
