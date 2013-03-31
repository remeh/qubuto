package models;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * Conversation's message.
 * @author Rémy 'remeh' Mathieu
 */
public class Message extends MongoModel {
	/**
	 * Content of the message.
	 */
	private String content;
	
	/**
	 * Author of the message.
	 */
	// reference //
	private String author;
	
	/**
	 * In which conversation is thi message.
	 */
	// reference //
	private String conversation;

	/**
	 * Position in the conversation.
	 */
	private long position;
	
	/**
	 * Creation's date.
	 */
	private Date creationDate;
	
	/**
	 * Last update of this {@link Message}.
	 */
	private Date lastUpdate;
	
	// ---------------------
	
	public Message() {
		this.creationDate = new Date();
		this.lastUpdate = new Date();
	}
	
	// ---------------------

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public User getAuthor() {
		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (author != null) {
			return mu.find(author);
		}
		return null;
	}
	
	public void setAuthor(User author) {
		this.author = author.getId();
	}
	
	public Conversation getConversation() {
		ModelUtils<Conversation> mu = new ModelUtils<Conversation>(Conversation.class);
		if (conversation != null) {
			return mu.find(conversation);
		}
		return null;
	}
	
	public void setConversation(Conversation conversation) {
		this.conversation = conversation.getId();
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
