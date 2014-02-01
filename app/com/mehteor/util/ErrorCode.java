package com.mehteor.util;

public enum ErrorCode {
	OK(0, "OK"),
	NOT_AUTHENTICATED(1, "You're not authenticated."),
	NOT_ENOUGH_PARAMETERS(2, "You didn't provide enough parameters."),
	BAD_PARAMETERS(3, "The parameters provided are wrong or didn't return any correct result."),
    NOT_AUTHORIZED(4, "You're not authorized to perform this action.");

    // ---------------------- 
	
	private String defaultMessage;
	private int errorCode;

    // ---------------------- 

    /**
     * Creates an ErrorCode with the given Id and default text message.
     * @param errorCode         the id of the error code
     * @param defaultMessage    the default message to display.
     */
	private ErrorCode(int errorCode, String defaultMessage) {
		this.defaultMessage = defaultMessage;
		this.errorCode = errorCode;
	}

    // ---------------------- 

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
