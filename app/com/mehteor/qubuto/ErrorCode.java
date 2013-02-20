package com.mehteor.qubuto;

public enum ErrorCode {
	OK(0, "OK"),
	NOT_AUTHENTICATED(1, "You're not authenticated."),
	NOT_ENOUGH_PARAMETERS(2, "You didn't provide enough parameters.");
	
	private String defaultMessage;
	private int errorCode;

	ErrorCode(int errorCode, String defaultMessage) {
		this.defaultMessage = defaultMessage;
		this.errorCode = errorCode;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
