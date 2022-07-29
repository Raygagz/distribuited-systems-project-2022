package Requests;

import Enums.RequestType;

@SuppressWarnings("serial")
public class LeaveRequest extends Request {
	public LeaveRequest() {
		this.Type = RequestType.LEAVE;
	}
}
