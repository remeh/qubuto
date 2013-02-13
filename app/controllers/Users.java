package controllers;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.List;

import play.data.Form;
import play.libs.Crypto;
import play.mvc.*;

import models.User;
import models.Session;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.session.SessionManager;

public class Users extends Controller {
    final static Form<User> userForm = Form.form(User.class);

	// ---------------------

    /**
     * Checks whether the username is already used in the base.
     * @param username the username to look for
     * @return true if the username is already used.
     */
    private static boolean checkExistingUsername(String username) {
        ModelUtils<User> muUser = new ModelUtils<User>(User.class);
        List<User> users = muUser.query(String.format("{'username':'%s'}",username));
        return users.size() > 0;
    }

	// ---------------------

	public static Result register() {
        return ok(views.html.users.register.render(userForm));
	}
	
	public static Result update() {
        // bind the form
        Form<User> form = userForm.bindFromRequest(); 
        
        /*
         *  Required fields
         */
        
        if (form.field("username").valueOr("").isEmpty()) {
        	form.reject("username", "Required");
        }
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
            //form.reject("username", "Username already used.");
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
        SessionManager.authenticate(user);
        
        return ok(views.html.index.render());
	}
}
