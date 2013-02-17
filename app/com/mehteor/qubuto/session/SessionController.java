package com.mehteor.qubuto.session;

import models.Session;
import models.User;
import play.mvc.Controller;

public class SessionController extends Controller {
	/**
	 * Returns true if the user is authenticated, false otherwise.
	 * @return true if the user is authenticated, false otherwise.
	 */
	public static boolean isAuthenticated() { 
		return isAuthenticated(null, true);
	}

	/**
	 * Returns true if the user is authenticated, false otherwise.
	 * If the user isn't authenticated and the error parameter is on true,
	 * an error message is written in the flash session.
	 * @param error the message which should be inserted in the flash error.
	 * @param refresh whether the session should be refreshed
	 * @return true if the user is authenticated, false otherwise.
	 */
	public static boolean isAuthenticated(String error, boolean refresh) {
		String sessionId = session().get("s");
		
		if (sessionId == null || sessionId.isEmpty()) {
			SessionController.setFlashError(error);
			return false;
		}
		
		Session session = SessionManager.findSession(sessionId);
		
		if (session == null) {
			SessionController.setFlashError(error);
			// not mandatory, but we remove "s" and "u" data in the browser
			// session. Just-in-case.
			SessionManager.cleanBrowserSession(session());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the user from the session.
	 * @return the user if one has been found with the session, null otherwise (or if there is no session info)
	 */
	public static User getUser() {
		Session session = SessionManager.findSession(session("s"));
		if (session == null) {
			return null;
		}
		
		return session.getUser();
	}
	
	// ---------------------
	
	private static void setFlashError(String error) {
		if (error != null && !error.isEmpty()) {
			flash("error", error);
		}
	}
}
