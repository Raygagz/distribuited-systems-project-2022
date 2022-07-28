package Requests;

import java.io.Serializable;

import Enums.RequestType;

@SuppressWarnings("serial")
public abstract class Request implements Serializable {
	public RequestType Type;
}
