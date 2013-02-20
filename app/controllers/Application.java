package controllers;


import play.api.templates.Html;
import play.mvc.*;
import scala.util.Random;

public class Application extends SessionController{
    public static Result index() {
    	// if the user is already authenticated
    	// redirect it to its dashboard page
    	if (isAuthenticated()) {
    		redirect(routes.Users.login());
    	}
    	
        return ok(views.html.index.render());
    }
    
    // ---------------------
    
	public static Html renderNotFound() {
		Random random = new Random();
    	int x = 320 + random.nextInt(40);
    	int y = 220 + random.nextInt(40);
    	return views.html.notFound.render(x,y);
	}
}
