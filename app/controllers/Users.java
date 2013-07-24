package controllers;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.List;

import play.data.Form;
import play.libs.Crypto;
import play.mvc.*;

import models.User;

import com.mehteor.db.ModelUtils;
import com.mehteor.util.StringHelper;
import com.mehteor.util.session.SessionManager;

public class Users extends SessionController {
    final static Form<User> userForm = Form.form(User.class);

	// ---------------------

    /**
     * Checks whether the username is already used in the base.
     * @param username the username to look for
     * @return true if the username is already used.
     */
    private static boolean checkExistingUsername(String username) {
        ModelUtils<User> muUser = new ModelUtils<User>(User.class);
        List<User> users = muUser.query(String.format("{'username':'%s'}",username.toLowerCase()));
        return users.size() > 0;
    }

	// ---------------------
    
	public static Result login() {
		if (isAuthenticated()) {
			return redirect(routes.Projects.list(session("u")));
		}
		
		return ok(views.html.users.login.render(userForm));
	}
	
	public static Result logout() {
		if (session("s") != null) {
			flash("success", "You're correctly log out.");
		}
		SessionManager.cleanSession(session());
		return redirect(routes.Users.login());
	}
	
	public static Result enter() {
		/*
         * Bind the form
         */
        Form<User> form = userForm.bindFromRequest(); 
        
        /*
         *  Required fields
         */
        if(form.field("password").valueOr("").isEmpty() || form.field("username").valueOr("").isEmpty()) {
        	// don't return the password in the request
        	form.data().remove("password");
        	return badRequest(views.html.users.login.render(form));
        }

        /*
         * Looks for this user in the database
         */
        ModelUtils<User> muUsers = new ModelUtils<User>(User.class);
        
        String username = form.field("username").value();
		String password = form.field("password").value();

        username = username.toLowerCase();
        
        List<User> users = muUsers.query("{'username':#}", username);
        
        // Should never return > 1 
        User user = null;
        if (users.size() == 1) {
        	user = users.get(0);
        	String salt = user.getSalt();
        	String cryptedPassword = Crypto.sign(salt + password);
        	if (!cryptedPassword.equals(user.getPassword())) {
        		user = null;
        	}
        }
        
        if (user != null) {
        	// Open the session for this user.
        	SessionManager.authenticate(session(), user);
        } else {
        	form.reject("password", "Bad username or password.");
        	return badRequest(views.html.users.login.render(form));
        }
        
        return redirect(routes.Projects.list(user.getUsername()));
	}
	
	public static Result register() {
		return ok(views.html.users.register.render(userForm));
	}
	
	public static Result update() {
        /*
         * Bind the form
         */
        Form<User> form = userForm.bindFromRequest(); 
        
        /*
         *  Required fields
         */
        
        if (form.field("username").valueOr("").isEmpty()) {
        	form.reject("username", "Required");
        } else if (StringHelper.validateString(form.field("username").value()) == false) {
    		form.reject("username", "Must be composed of letters, numbers or underscores.");
    	}
        
        // TODO validate that it's an email
        if (form.field("email").valueOr("").isEmpty()) {
        	form.reject("email", "Required");
        }
        
        if(form.field("password").valueOr("").isEmpty()) {
        	form.reject("password", "Required");
        }
        
        /*
         * Check password validation
         */
        
        if (!form.field("password").valueOr("").isEmpty()) {
            if (!form.field("password").valueOr("").equals(form.field("password_validation").value())) {
            	form.reject("password_validation", "Password don't match");
            }
        }

        /*
         * Checks if existing account.
         */

        if (checkExistingUsername(form.field("username").value())) {
            form.reject("username", "Username already used.");
        }
        
        if (form.hasErrors()) {
        	// don't return the password in the request
        	form.data().remove("password");
        	form.data().remove("password_validation");
        	
            return badRequest(views.html.users.register.render(form));
        }
        

        /*
         * Read the future User in the request.
         */

        User user = form.get();

        // username to lowerstring
        user.setUsername(user.getUsername().toLowerCase());

        /*
         * Generating salt
         */

        Random r = new Random();
        String salt = String.format("%s%d%d%d%d%d%d%s", new GregorianCalendar(), new Date().getTime(), r.nextInt(),
        		r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt(), new Date());
        salt = Crypto.sign(salt);
        
        /*
         * Set users credential information.
         */

        user.setSalt(salt);
        user.setPassword(Crypto.sign(salt + user.getPassword()));
        
        user.save();	

        /*
         * Open the session for this user.
         */
        SessionManager.authenticate(session(), user);
        
        return redirect(routes.Projects.list(user.getUsername()));
	}
}
