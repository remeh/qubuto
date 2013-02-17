package controllers;

import com.mehteor.qubuto.session.SessionController;

import play.mvc.*;

public class Application extends SessionController{
    public static Result index() {
    	// if the user is already authenticated
    	// redirect it to its dashboard page
    	if (isAuthenticated()) {
    		redirect(routes.Users.login());
    	}
    	
        return ok(views.html.index.render());
    }
}
