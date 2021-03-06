package models;

import java.util.Date;

import models.QubutoModel;

import com.mehteor.db.ModelUtils;

/**
 * Conversation's message.
 * @author Rémy 'remeh' Mathieu
 */
public class Message extends QubutoModel {
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
        Object cache = cache("author");
        if (cache != null) {
            return (User)cache;
        }

		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (author != null) {
			return (User) cache("author", mu.find(author));
		}
		return null;
	}

    @Override
    public User getCreator() {
        return getAuthor();
    }
	
	public void setAuthor(User author) {
        invalidate("author");
		this.author = author.getId();
	}
	
	public Conversation getConversation() {
        Object cache = cache("conversation");
        if (cache != null) {
            return (Conversation)cache;
        }

		ModelUtils<Conversation> mu = new ModelUtils<Conversation>(Conversation.class);
		if (conversation != null) {
			return (Conversation) cache("conversation", mu.find(conversation));
		}
		return null;
	}
	
	public void setConversation(Conversation conversation) {
        invalidate("conversation");
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
