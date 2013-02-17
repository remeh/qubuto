package controllers.api;

import java.util.HashMap;
import java.util.Map;

import com.mehteor.qubuto.session.SessionController;

import play.libs.Json;
import play.mvc.Result;

public class Users extends SessionController {
    public static Result amIAuth() {
    	if (isAuthenticated()) {
    		Map<String, String> result = new HashMap<String, String>();
    		result.put("error", "0");
    		result.put("message", String.format("Yes %s, you're auth.", session().get("u")));
    		return ok(Json.toJson(result));
    	} else {
    		Map<String, String> result = new HashMap<String, String>();
    		result.put("error", "1");
    		result.put("message", String.format("Nope, I don't know who you are.", session().get("u")));
    		return ok(Json.toJson(result));
    	}
    }
}
