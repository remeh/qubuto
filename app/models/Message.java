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
	 * Creation's date.
	 */
	private Date creationDate;
	
	// ---------------------
	
	public Message() {
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
}
