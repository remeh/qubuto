package models;


import java.util.Date;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

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
	 * User's subscription date.
	 */
	private Date subscriptionDate;
	
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
	
	public Date getSubscriptionDate() {
		return subscriptionDate;
	}
	
	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}
	
	// ---------------------
	
	/**
	 * Returns an User's jsoned without its secure information.
	 * @return only the public informations about this user.
	 */
	public ObjectNode toJsonPublic() {
		ObjectNode node = Json.newObject();
		node.put("username", username);
//		node.put("email", email);
		return node;
	}
}
