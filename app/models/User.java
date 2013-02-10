package models;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * An user of Qubuto.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class User extends MongoModel {
	/**
	 * User's username.
	 */
	private String username;
	
	/**
	 * User's password.
	 */
	private String password;
	
	/**
	 * Salt used for the user password.
	 */
	private String salt;
	
	/**
	 * User's email.
	 */
	private String email;
	
	/**
	 * Session when the user is connected.
	 */
	// reference //
	private String session;
	
	// ---------------------
	
	public User() {
		
	}

	// ---------------------

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Session getSession() {
		ModelUtils<Session> mu = new ModelUtils<Session>(Session.class);
		if (session != null) {
			return mu.find(session);
		}
		return null;
	}
	
	public void setSession(Session session) {
		this.session = session.id;
	}
}
