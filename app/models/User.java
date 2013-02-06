package models;

import java.util.Date;

/**
 * An user of Qubuto.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class User extends MongoModel {
	/**
	 * User's username.
	 */
	public String username;
	
	/**
	 * User's password.
	 */
	public String password;
	
	/**
	 * Salt used for the user password.
	 */
	public String salt;
	
	/**
	 * User's email.
	 */
	public String email;
	
	/**
	 * Session ID when the user is connected.
	 */
	public String sessionId;
	
	/**
	 * Last time this session was updated.
	 */
	public Date lastUpdate;


	// ---------------------
	
	public User() {
		
	}
	
	@Override
	public String getCollectionName() {
		return "users";
	}

	// ---------------------
}
