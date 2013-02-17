package com.mehteor.qubuto.session;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import play.mvc.Http;

import models.User;
import models.Session;

import com.mehteor.db.ModelUtils;

/**
 * Deals with sessions.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class SessionManager {
	/**
	 * After how many minutes an non-refreshed session is considered as timeouted.
	 */
	public static final int SESSION_TIMEOUT = 60;

    /**
     * Authenticate an User (creates a Session).
     * @param httpSession the HTTP session.
     * @param user the user for which we want to create a Session.
     * @return the current session of this user.
     */
    public static Session authenticate(Http.Session httpSession, User user) {
        Session existing = SessionManager.findSession(user, httpSession.get("s"));
        
        if (existing != null) {
            existing.setLastUpdate(new Date());
            existing.save();
            return existing;
        } else {
        	Session session = SessionManager.openSession(user);
        	
        	// write in the HTTP session the session ID of this user
        	// and its username
        	httpSession.put("s", session.getId());
        	httpSession.put("u", user.getUsername());
        	
        	return session;
        }
    }

    /**
     * Finds a session in the opened sessions for the provided User.
     * Returns null if there is no open session for this user.
     * @param user the user for which we want to find a Session.
     * @return the found session (the first if there is many, should never happen), null if none Session were found.
     */
    public static Session findSession(User user, String sessionId) {
    	if (user == null || sessionId == null || sessionId.isEmpty()) {
    		return null;
    	}
    	
        ModelUtils<Session> muSession = new ModelUtils<Session>(Session.class);
        List<Session> sessions = muSession.query(String.format("{'user': '%s'}", user.getId()));
        
        if (sessions.size() == 0) {
            return null;
        }
        
        return sessions.get(0);
    }
    
    /**
     * Finds a session in the opened sessions for the provided session ID.
     * Returns null if there is no open session for this user.
     * @param sessionId the session ID to look for.
     * @return the found session (the first if there is many, should never happen), null if none Session were found.
     */
    public static Session findSession(String sessionId) {
        ModelUtils<Session> muSession = new ModelUtils<Session>(Session.class);
        Session session = muSession.find(sessionId);
        
        return session;
    }
    
    /**
     * Cleans the session in the Http Session.
     * @param session the session to clean
     */
    public static void cleanBrowserSession(Http.Session session) {
    	session.remove("s");
    	session.remove("u");
    }
    
    /**
     * Removes a session from the database by the session id.
     * @param sessionId the session to clear
     */
    public static void cleanDatabaseSession(String sessionId) {
        ModelUtils<Session> muSession = new ModelUtils<Session>(Session.class);
        muSession.remove("{'_id': # }", sessionId);
    }
    
    /**
     * Removes a session from the database in the http browser session.
     * @param session the http session
     */
    public static void cleanSession(Http.Session session) {
    	cleanBrowserSession(session);
    	cleanDatabaseSession(session.get("s"));
    }

    /**
     * Opens a session for the provided User.
     * @param user the user for which the session will be created.
     * @return Session the created session.
     */
    public static Session openSession(User user) {
        Session session = new Session();
        session.setUser(user);
        session.setCreationDate(new Date());
        session.setLastUpdate(new Date());
        session.save();
        return session;
    }
    
    /**
     * Removed timeouted sessions in the base.
     * @return how many sessions were removed.
     */
    public static int cleanTimeoutedSessions() {
		ModelUtils<Session> sessions = new ModelUtils<Session>(Session.class);
		
		Calendar userCal = new GregorianCalendar();
		userCal.add(Calendar.MINUTE, -SESSION_TIMEOUT);
		
		return sessions.remove("{ 'lastUpdate': { $lt: # } }", userCal.getTime());
    }
}
