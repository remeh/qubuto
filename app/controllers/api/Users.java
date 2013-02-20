package controllers.api;

import com.mehteor.qubuto.ErrorCode;

import controllers.SessionController;

import play.mvc.Result;

public class Users extends SessionController {
    public static Result amIAuth() {
    	if (isAuthenticated()) {
    		return ok(renderJson(0, String.format("Yes %s, you're auth.", session().get("u"))));
    	} else {
    		return ok(renderJson(ErrorCode.NOT_AUTHENTICATED.getErrorCode(), "Nope, I don't know who you are."));
    	}
    }
}
