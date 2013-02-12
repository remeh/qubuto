package models;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * A session for an user of Qubuto.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Session extends MongoModel {
	/**
	 * User.
	 */
	// reference //
	private String user;
	
	/**
	 * Opening date of this Session.
	 */
	private Date creationDate;
	
	/**
	 * Last update of this Session.
	 */
	private Date lastUpdate;
	
	// ---------------------
	
	public Session() {
	}
	
	// ---------------------
	
	public User getUser() {
		ModelUtils<User> muUser = new ModelUtils<User>(User.class);
		return muUser.find(user);
	}
	
	public void setUser(User user) {
		this.user = user.getId();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
