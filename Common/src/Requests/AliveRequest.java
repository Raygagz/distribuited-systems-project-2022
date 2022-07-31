package Requests;

import Enums.RequestType;

@SuppressWarnings("serial")
public class AliveRequest extends Request {
	public AliveRequest() {
		this.Type = RequestType.ALIVE;
	}
}
