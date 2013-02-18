package com.mehteor.qubuto;

public class StringHelper {
	public final static String acceptedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
	
	/**
	 * Validates that the string is only composed of letters, numbers and undescores.
	 * @param toValidate the string to validate
	 * @return true if the string is only composed of letters, numbers and underscores.
	 */
	public static boolean validateString(String toValidate) {
		for (int i = 0; i < toValidate.length(); i++) {
			if (StringHelper.acceptedChars.indexOf(toValidate.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}
}
