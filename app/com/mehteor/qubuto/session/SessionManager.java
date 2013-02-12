package com.mehteor.qubuto.session;

import java.util.Date;
import java.util.List;

import plugins.JongoPlugin;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

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
     * Authenticate an User (creates a Session).
     * @param user the user for which we want to create a Session.
     */
    public static Session authenticate(User user) {
        Session existing = SessionManager.findSession(user);

        if (existing != null) {
            existing.setLastUpdate(new Date());
            existing.save();
            return existing;
        } else {
            return SessionManager.openSession(user);
        }
    }

    /**
     * Finds a session in the opened sessions for the provided User.
     * Returns if there is no open session for this user.
     * @param user the user for which we want to find a Session.
     * @return the found session (the first if there is many, should never happen), null if none Session were found.
     */
    public static Session findSession(User user) {
        ModelUtils<Session> muSession = new ModelUtils<Session>(Session.class);
        List<Session> sessions = muSession.query(String.format("{'user': '%s'}", user.getId()));

        if (sessions.size() == 0) {
            return null;
        }
        return sessions.get(0);
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
}
