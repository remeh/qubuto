package controllers;

import java.util.List;

import models.QubutoModel;
import models.Session;
import models.User;
import models.UserRight;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;
import com.mehteor.qubuto.right.RightType;
import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.util.session.SessionManager;

import play.mvc.Result;
import play.mvc.Results;

public class SessionController extends BaseController {
    public static ModelUtils<UserRight> mu = new ModelUtils<UserRight>(UserRight.class);

    // ---------------------- 

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
			BaseController.setFlashError(error);
			return false;
		}
		
		Session session = SessionManager.findAndUpdateSession(sessionId);
		
		if (session == null) {
			BaseController.setFlashError(error);
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

    /**
     * Tests if the currently logger user has the given right.
     * @param category      the RightCategory to test for
     * @param objectId      the object to test for
     * @param type          the RightType to test for.
     * @return true whether the currently logged user has the wanted rights.
     */
    public static boolean hasRight(RightCategory category, QubutoModel object, RightType type) {
        if (object.getCreator().getUsername().equals(getUser().getUsername())) {
            return true;
        }
        if (object instanceof MongoModel) {
            return hasRight(category, ((MongoModel)object).getId(), type);
        }
        return false;
    }

    /**
     * Tests if the currently logger user has the given right.
     * @param category      the RightCategory to test for
     * @param objectId      the object to test for
     * @param type          the RightType to test for.
     * @return true whether the currently logged user has the wanted rights.
     */
    public static boolean hasRight(RightCategory category, String objectId, RightType type) {
        //System.out.println("%s %s %s %s", category.toString(), objectId, getUser().getId(), type.toString());
        long rights = mu.count("{'category': #, 'objectId': #, 'user': #, 'type': #}", category.toString(), objectId, getUser().getId(), type.toString());
        return rights > 0;
    }

    public static Result forbid(RightCategory category, RightType type) {
        return Results.unauthorized(BaseController.renderNotAuthorizedJson(), "utf8");
    }
}
