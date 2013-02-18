package com.mehteor.qubuto.todolist;

import com.mehteor.qubuto.StringHelper;

public class TodolistHelper {
	/**
	 * Returns a valid URL string by replacing every other characters than letters and numbers with '-'.
	 * @param todolistName the string to reformat
	 * @return the formatted string.
	 */
	public static String generateNameId(String todolistName) {
		String shortenName = todolistName;
		if (shortenName.length() > 40) {
			shortenName = shortenName.substring(0,40);
		}
		
		StringBuilder cleanName = new StringBuilder(41);
		for (int i = 0; i < shortenName.length(); i++) {
			if (StringHelper.acceptedChars.indexOf(shortenName.charAt(i)) == -1) {
				cleanName.append("-");
			} else {
				cleanName.append(shortenName.charAt(i));
			}
		}
		
		String result = cleanName.toString();
		
		return result.toLowerCase();
	}
}
