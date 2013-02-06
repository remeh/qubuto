package controllers;

import models.User;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
  
    public static Result index() {
    	
    	User user = new User();
    	user.insert();
    	
        return ok(index.render("Your new application is ready."));
    }
  
}
