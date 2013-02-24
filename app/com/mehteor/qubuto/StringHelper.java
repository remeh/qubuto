package com.mehteor.qubuto;

import play.Logger;

public class StringHelper {
	private static String websiteUri = null;
	
	public final static String acceptedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
	
	// ---------------------
	
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
	
	/**
	 * Returns a valid URL string by replacing every other characters than letters and numbers
	 * with the provided char, or simply removing them
	 * @param name the string to reformat
	 * @param replaceWith the character to replace with. Sets this parameter to null to simple remove
	 * @return the formatted string.
	 */
	public static String cleanString(String name, String replaceWith) {
		if (name == null) {
			Logger.error("A null name has been provided to generateNameId.");
			return null;
		}
		
		String shortenName = name;
		if (shortenName.length() > 40) {
			shortenName = shortenName.substring(0,40);
		}
		
		StringBuilder cleanName = new StringBuilder(41);
		for (int i = 0; i < shortenName.length(); i++) {
			if (StringHelper.acceptedChars.indexOf(shortenName.charAt(i)) == -1) {
				if (replaceWith != null) {
					cleanName.append(replaceWith);
				}
			} else {
				cleanName.append(shortenName.charAt(i));
			}
		}
		
		String result = cleanName.toString();
		
		return result.toLowerCase();
	}

	public static String removeRoutes(String uri) {
		if (websiteUri != null) {
			return websiteUri;
		}
		
		// TODO
		websiteUri = "http://localhost:9000/";
		return websiteUri;
	}
}
